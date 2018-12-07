package it.almawave.gateway.asr;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.jboss.logging.Logger;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.almawave.gateway.configuration.PropertiesBean;
import it.almawave.gateway.crm.CRMClient;
import it.almawave.gateway.db.DbManager;
import it.almawave.gateway.db.bean.CRMRequestBean;
import it.almawave.gateway.db.excption.DbException;
import it.pervoice.audiomabox.commontypes._1.OutputType;
import it.pervoice.audiomabox.services.common._1.EnumStatusType;
import it.pervoice.audiomabox.services.download._1.DownloadRequest;
import it.pervoice.audiomabox.services.download._1.DownloadResponse;
import it.pervoice.audiomabox.services.status._1.JobFileType;
import it.pervoice.audiomabox.services.status._1.StatusRequest;
import it.pervoice.audiomabox.services.status._1.StatusResponse;
import it.pervoice.ws.audiomabox.service.download._1.DownloadFault;
import it.pervoice.ws.audiomabox.service.download._1.DownloadWS;
import it.pervoice.ws.audiomabox.service.status._1.StatusFault;
import it.pervoice.ws.audiomabox.service.status._1.StatusWS;



/**
 * Session Bean implementation class StatusTimerService
 */
@Singleton
@LocalBean
public class StatusTimerService {
	@EJB
	PropertiesBean propertiesBean;
	
	private static final Logger LOGGER = Logger.getLogger(StatusTimerService.class);

	//AUDIOMA_ID
	private String identificativo = null;
	//EXT_ID
	private String idDifformita;

	
	@EJB
	CRMClient crm;
	
	@EJB
	DbManager dbM;
    
    @Resource
    private TimerService timerService;
    
    public StatusTimerService() {

    }

    //@PostConstruct
    public void init(String id, String idDifformita) {

        this.identificativo = id;
        this.idDifformita = idDifformita;
        
        TimerConfig timerConfig = new TimerConfig();
    	timerConfig.setInfo("StatusTimerService_"+this.getIdentificativo());
    	LOGGER.info("Timer initial duration and internal duration: "+propertiesBean.getInitialDuration()+" "+ propertiesBean.getInternalDuration());
    	timerService.createIntervalTimer(propertiesBean.getInitialDuration(), propertiesBean.getInternalDuration(), timerConfig); //ogni 5 sec 

    }
    
	@Timeout
	public void execute(Timer timer) {
		
		try {
			
			LOGGER.info("Timer Service : " + timer.getInfo());
			LOGGER.info("Execution Time : " + new Date());
			LOGGER.info("____________________________________________");
			
			if (identificativo == null) {
				timer.cancel();
				return;
			}
			//chiamare il servizio statusService
			StatusWS serviceS = new ServiceStatus().getService();
			
			StatusRequest statusRequest = new StatusRequest();
			statusRequest.setJobId(Long.parseLong(identificativo));
			
			StatusResponse statusResponse = serviceS.status(statusRequest);
			
			if (statusResponse.getJob().isEmpty()) {
				LOGGER.error("_______ nessun job NON trovato ________");
				//registrare errore nel db nessun job trovato per identificativo inserito
				dbM.modificaStato(this.idDifformita, 150);
				timer.cancel();
				return;
			} 
			
			JobFileType job = statusResponse.getJob().get(0);
			LOGGER.info("_______  job stato : " + job.getStatus().value());
			
			//job in errore 
			if(job.getStatus().value().equals(EnumStatusType.FAILED.value())) {
				//registrare errore nel db nessun job trovato per identificativo inserito
				dbM.modificaStato(this.idDifformita, 150);
				LOGGER.info("_______ job terminato con errore : " + job.getErrCode());
				timer.cancel();
				return;
			}
			
			//job se Completata 
			if (job.getStatus().value().equals(EnumStatusType.COMPLETED.value()) ){
				
				//chiamare il servizio statusDownload
				DownloadWS serviceD = new ServiceDownload().getService();
				DownloadRequest downloadRequest = new DownloadRequest();
				downloadRequest.setJobId(Long.parseLong(identificativo));
				downloadRequest.setFormat(OutputType.PVT);
				
				DownloadResponse downloadResponse = serviceD.download(downloadRequest);
				
				//TODO: formattare il testo dalle response
				String testo = " INDEBITA DISPOSIZIONE A VIA IMPEDITA SEGNALE DI PARTENZA al km 16+300 Roma Anagnina";
				
				//salvare il testo nella tabella request per EXT_ID
				dbM.inserisciTesto(idDifformita, testo);
				
				//chiamo il servizio cmr
				crm.initClient(propertiesBean.getCrmHost(), propertiesBean.getCrmPort(), propertiesBean.getCrmUser(), propertiesBean.getCrmPassword());
				
				CRMRequestBean bean = new CRMRequestBean();
				List<String> classificationLogicList = new ArrayList<String>();
				//TODO: comporre il classificationLogicList
				classificationLogicList.add("Visita Al Binario a Piedi");
				bean.setClassificationLogicList(classificationLogicList);
				bean.setTextMessage(testo);
				ObjectMapper objectMapper = new ObjectMapper();
		        String jsonString = objectMapper.writeValueAsString(bean);

		        CloseableHttpResponse crmResponse = crm.doPostJson(jsonString, propertiesBean.getCrmClassificationEndPoint());
		        
		        //TODO:salvare le triplette nel db
		        
		        timer.cancel();
				
			}
				
	
		} catch (MalformedURLException|StatusFault e) {
			//registrare errore nel db
			dbM.modificaStato(this.idDifformita, 160);
			LOGGER.error("_______ ERRORE invocazione servizio Status________");
			e.printStackTrace();
			timer.cancel();
		} catch (DownloadFault e) {
			//registrare errore nel db
			dbM.modificaStato(this.idDifformita, 170);
			LOGGER.error("_______ ERRORE invocazione servizio Download________");
			e.printStackTrace();
			timer.cancel();
		} catch (JsonProcessingException e) {
			dbM.modificaStato(this.idDifformita, 180);
			LOGGER.error("_______ ERRORE conversione json________");
			e.printStackTrace();
			timer.cancel();
		} catch (DbException e) {
			dbM.modificaStato(this.idDifformita, 190);
			LOGGER.error("_______ ERRORE salvataggio del testo________");
			e.printStackTrace();
			timer.cancel();
		} 
		

	}

        
	public String getIdentificativo() {
		return identificativo;
	}

	public void setIdentificativo(String identificativo) {
		this.identificativo = identificativo;
	}

	public String getIdDifformita() {
		return idDifformita;
	}

	public void setIdDifformita(String idDifformita) {
		this.idDifformita = idDifformita;
	}
 


}

package it.almawave.gateway;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jboss.logging.Logger;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.almawave.gateway.asr.ServiceDownload;
import it.almawave.gateway.asr.ServiceStatus;
import it.almawave.gateway.bean.GatewayResponse;
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
//@Singleton
@Stateless
@LocalBean
public class GatewayManager {
	private static final Logger LOGGER = Logger.getLogger(GatewayManager.class);
	@EJB
	PropertiesBean propertiesBean;
	@EJB
	CRMClient crm;
	@EJB
	DbManager dbM;
	@Resource
	private TimerService timerService;

	//AUDIOMA_ID
	private String identificativo = null;
	//EXT_ID
	private String idDifformita;

	//attivare per test
	//private int count=0;


	public GatewayManager() {

	}

	public void init(String id, String idDifformita) throws  IOException {
		this.identificativo = id;
		this.idDifformita = idDifformita;

		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo("StatusTimerService_"+this.getIdentificativo());
		LOGGER.info("Timer initial duration and internal duration: "+propertiesBean.getInitialDuration()+" "+ propertiesBean.getInternalDuration());
		timerService.createIntervalTimer(propertiesBean.getInitialDuration(), propertiesBean.getInternalDuration(), timerConfig); //ogni 5 sec 
	}

	@Timeout
	public void startProcess(Timer timer){

		try {

			LOGGER.info("Timer Service : " + timer.getInfo());
			LOGGER.info("Execution Time : " + new Date());
			LOGGER.info("____________________________________________");

			if (identificativo == null) {
				timer.cancel();
				return;
			}
            
			/*  NOTA 1
			 *  tutto questo mettiamolo in metodo
			 * in modo di avere una sola riga che restituisce 
			 * il valoro dello status
			 * Questo per dare maggiore pulizia al codice
			 */
			//chiamare il servizio statusService
			StatusWS serviceS = new ServiceStatus().getService();

			StatusRequest statusRequest = new StatusRequest();
			statusRequest.setJobId(Long.parseLong(identificativo));

			StatusResponse statusResponse = serviceS.status(statusRequest);
            /*
             * FINE NOTA 1
             */
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

			/*
			 * La richiesta è stata completata da ASR
			 * Posso procedere con il resto del processo
			 */
			if (job.getStatus().value().equals(EnumStatusType.COMPLETED.value()) ){
				timer.cancel();
                /*
                 * NOTA 2 
                 * Stesse considerazioni di nota 1
                 * puliamo il codice mettiamo tutto fuori questo metodo
                 * una chimata che restituisce direttamente
                 * la RESPONSE gia pulita queste operazioni facciamole fuori
                 * 
                 */
				//chiamare il servizio statusDownload
				DownloadWS serviceD = new ServiceDownload().getService();
				DownloadRequest downloadRequest = new DownloadRequest();
				downloadRequest.setJobId(Long.parseLong(identificativo));
				downloadRequest.setFormat(OutputType.PVT);

				DownloadResponse downloadResponse = serviceD.download(downloadRequest);

				/*
				 * FINE NOTA 2
				 */
				//TODO: formattare il testo dalle response
				String testo = " INDEBITA DISPOSIZIONE A VIA IMPEDITA SEGNALE DI PARTENZA al km 16+300 Roma Anagnina";

				//salvare il testo nella tabella request per EXT_ID
				dbM.inserisciTesto(idDifformita, testo);

				/*
				 * Invoco il servizio di Classificazione e ottengo
				 * in risposta un GatewayResponse 
				 * GatewayResponse è la risposta del CRM con i dati già pulit
				 */
				GatewayResponse crmResponse=startClassification();

				//TODO:salvare le triplette nel db

			}

			/*
			 * Da utilizzare come test
			 * 
			 */
			//count++
			//			if(count>6) {
			//				timer.cancel();	
			//
			//				crm.initClient(propertiesBean.getCrmHost(), propertiesBean.getCrmPort(), propertiesBean.getCrmUser(), propertiesBean.getCrmPassword());
			//				String crmResponse=crm.startClassification();
			//				System.out.println(om.writeValueAsString(crmResponse));
			//
			//				return;
			//			}



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
		} catch (HttpResponseException e) {
			/*!!!!!!!!!da fare errore nella chiamata CRM*/
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			/*!!!!!!!!!da fare errore nella chiamata CRM*/
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	
	private GatewayResponse startClassification() throws HttpResponseException, IOException {
		crm.initClient(propertiesBean.getCrmHost(), propertiesBean.getCrmPort(), propertiesBean.getCrmUser(), propertiesBean.getCrmPassword());
		GatewayResponse crmResponse=crm.startClassification();
		return crmResponse;
	}
	/*
	 * necessaria per riazzerare i timer quando stoppo l'applicazione
	 */
	@PreDestroy 
	public void stopMyTimers() {
		if(timerService!=null){
			for(Timer timer : timerService.getTimers()) {
				if(timer!=null) timer.cancel();
			}
		}
	}    

}

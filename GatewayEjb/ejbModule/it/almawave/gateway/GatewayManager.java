package it.almawave.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;

import javax.activation.DataHandler;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.HttpResponseException;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.almawave.gateway.asr.ServiceDownload;
import it.almawave.gateway.asr.ServiceStatus;
import it.almawave.gateway.asr.UtilsAsr;
import it.almawave.gateway.bean.GatewayResponse;
import it.almawave.gateway.configuration.Parametri;
import it.almawave.gateway.configuration.PropertiesBean;
import it.almawave.gateway.crm.CRMClient;
import it.almawave.gateway.db.DbManager;
import it.almawave.gateway.db.excption.DbException;
import it.pervoice.audiomabox.commontypes._1.OutputType;
import it.pervoice.audiomabox.services.common._1.EnumStatusType;
import it.pervoice.audiomabox.services.common._1.FaultType;
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
	private int count=0;


	public GatewayManager() {

	}

	public void init(String id, String idDifformita) throws  IOException, DbException {

		this.identificativo = id;
		this.idDifformita = idDifformita;

		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo("StatusTimerService_"+this.getIdDifformita());
		Long start = Long.valueOf(propertiesBean.getValore(Parametri.initialDuration));
		Long iter = Long.valueOf(propertiesBean.getValore(Parametri.internalDuration));
		LOGGER.info("Timer initial duration and internal duration: "+start+" "+ iter);
		timerService.createIntervalTimer(start, iter, timerConfig); //ogni 5 sec
		
		dbM.modificaStato(this.idDifformita, 102);//in lavorazione
	}

	@Timeout
	public void startProcess(Timer timer) throws DbException{

		try {

			LOGGER.info("Timer Service : " + timer.getInfo());
			LOGGER.info("Execution Time : " + new Date());
			LOGGER.info("____________________________________________");

			if (identificativo == null) {
				timer.cancel();
				return;
			}
            
			//chiamare il servizio statusService
			String stato = startServiceStatus();
			
			LOGGER.info("____________________________________________" + stato);

			/*
			 * La richiesta è stata completata da ASR
			 * Posso procedere con il resto del processo
			 */
			if (stato.equals(EnumStatusType.COMPLETED.value()) ){
				
				timer.cancel();

				//chiamare il servizio Download
				String testo = startServiceDownload();

				//TODO: solo per test
				testo = "abrasioni su piano rotolamento corda alt dal chilometro 206+470 206+570";
				
				//chiamo il servizio del cmr
				GatewayResponse crmResponse = startClassification(testo);
				
				//mofico lo stato in concluso
				dbM.modificaStato(this.idDifformita, 110);//completata
				
				LOGGER.info("_______ processo concluso ________");

			}

			/*
			 * Da utilizzare come test
			 * 
			 */
//			count++;
//						if(count>6) {
//							timer.cancel();	
//			
////							crm.initClient(propertiesBean.getCrmHost(), propertiesBean.getCrmPort(), propertiesBean.getCrmUser(), propertiesBean.getCrmPassword());
////							String crmResponse=crm.startClassification();
////							System.out.println(om.writeValueAsString(crmResponse));
//			
//							throw new Exception("test");
//						}


		} catch (JsonProcessingException e) {
			dbM.modificaStato(this.idDifformita, 140);
			LOGGER.error("_______ ERRORE conversione json ________");
			e.printStackTrace();
			try { timer.cancel(); }catch (Exception ex) {}
		} catch (DbException e) {
			dbM.modificaStato(this.idDifformita, e.getCodice());
			LOGGER.error("_______ ERRORE salvataggio nel db ________");
			e.printStackTrace();
			try { timer.cancel(); }catch (Exception ex) {}
		} catch (StatusFault e) {
			//registrare errore nel db
			dbM.modificaStato(this.idDifformita, 120);
			LOGGER.error("_______ ERRORE invocazione servizio Status ________");
			e.printStackTrace();
			try { timer.cancel(); }catch (Exception ex) {}
		} catch (DownloadFault e) {
			//registrare errore nel db
			dbM.modificaStato(this.idDifformita, 121);
			LOGGER.error("_______ ERRORE invocazione servizio Download ________");
			e.printStackTrace();
			try { timer.cancel(); }catch (Exception ex) {}
		} catch (IOException|ParserConfigurationException|SAXException e) {
			//registrare errore nel db
			dbM.modificaStato(this.idDifformita, 141);
			LOGGER.error("_______ ERRORE conversione oggetto ________");
			e.printStackTrace();
			try { timer.cancel(); }catch (Exception ex) {}
		} catch (Exception e) {
			dbM.modificaStato(this.idDifformita, 999);
			LOGGER.error("_______ ERRORE generico ________" + e.getMessage());
			e.printStackTrace();
			try { timer.cancel(); }catch (Exception ex) {}
		}

	}


	/**
	 * Invoco il servizio di Classificazione e ottengo in risposta un GatewayResponse 
	 * @param testo
	 * @return GatewayResponse è la risposta del CRM con i dati già puliti
	 * @throws HttpResponseException
	 * @throws IOException
	 * @throws DbException 
	 */
	private GatewayResponse startClassification(String testo) throws HttpResponseException, IOException, DbException {
		crm.initClient(propertiesBean.getValore(Parametri.crmHost), Integer.parseInt(propertiesBean.getValore(Parametri.crmPort)), propertiesBean.getValore(Parametri.crmUser), propertiesBean.getValore(Parametri.crmPassword));
		
		GatewayResponse crmResponse = crm.startClassification(testo);
		
		dbM.inserisciResponse(this.idDifformita, crmResponse);

		return crmResponse;
	}
	
	/**
	 * eseguie la chiamata al servizio di asr per recuperare lo Status della richiesta 
	 * @return String lo stato della richiesta in lavorazione dal asr
	 * @throws MalformedURLException
	 * @throws StatusFault
	 */
	private String startServiceStatus() throws MalformedURLException, StatusFault {
		
		String stato = EnumStatusType.COMPLETED.value();
		
		Boolean isMokcServicesAsr = Boolean.parseBoolean( propertiesBean.getValore(Parametri.isMokcServicesAsr)) ;
		
		ServiceStatus statusService = new ServiceStatus(propertiesBean.getValore(Parametri.asrStatusUrl), propertiesBean.getValore(Parametri.asrUser), propertiesBean.getValore(Parametri.asrPassword), isMokcServicesAsr);
		
		StatusWS serviceS = statusService.getService();

		StatusResponse statusResponse = serviceS.status(statusService.initStatusRequest(identificativo));

		stato = statusService.elaboraResonse(statusResponse, identificativo);

		return stato;
		
	}
	
	/**
	 * 
	 * @return String testo da passare al servizio del crm
	 * @throws DownloadFault
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws DbException
	 */
	private String startServiceDownload() throws DownloadFault, IOException, ParserConfigurationException, SAXException, DbException {
		
		String testo ="";

		
		Boolean isMokcServicesAsr = Boolean.parseBoolean( propertiesBean.getValore(Parametri.isMokcServicesAsr)) ;
		
		ServiceDownload downloadService = new ServiceDownload(propertiesBean.getValore(Parametri.asrDownloadUrl), propertiesBean.getValore(Parametri.asrUser), propertiesBean.getValore(Parametri.asrPassword), isMokcServicesAsr);
		
		DownloadWS serviceD = downloadService.getService();

		DownloadResponse downloadResponse = serviceD.download(downloadService.initDownloadRequest(identificativo));
		
		String[] testi = downloadService.elaboraResonse(downloadResponse, identificativo);
		
		//salvare il pvt ed il testo nel db
		dbM.inserisciFilePVTandTesto(idDifformita, testi[0], testi[1]);
		
		testo = testi[1];
		
		return testo;
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

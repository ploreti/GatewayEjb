package it.almawave.gateway;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jboss.logging.Logger;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.almawave.gateway.asr.ServiceDownload;
import it.almawave.gateway.asr.ServiceStatus;
import it.almawave.gateway.asr.UtilsAsr;
import it.almawave.gateway.bean.GatewayResponse;
import it.almawave.gateway.configuration.PropertiesBean;
import it.almawave.gateway.crm.CRMClient;
import it.almawave.gateway.db.DbManager;
import it.almawave.gateway.db.bean.CRMRequestBean;
import it.almawave.gateway.db.excption.DbException;
import it.pervoice.audiomabox.commontypes._1.OutputType;
import it.pervoice.audiomabox.services.common._1.ClientInfoType;
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
            
			//chiamare il servizio statusService
			String stato = startServiceStatus();

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

				//salvare il testo nella tabella request per EXT_ID
				dbM.inserisciTesto(idDifformita, testo);
				
				/*
				 * Invoco il servizio di Classificazione e ottengo
				 * in risposta un GatewayResponse 
				 * GatewayResponse è la risposta del CRM con i dati già pulit
				 */
				GatewayResponse crmResponse = startClassification(testo);

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


		} catch (JsonProcessingException e) {
			dbM.modificaStato(this.idDifformita, 180);
			LOGGER.error("_______ ERRORE conversione json________");
			e.printStackTrace();
			if (timer != null)
				timer.cancel();
		} catch (DbException e) {
			dbM.modificaStato(this.idDifformita, 190);
			LOGGER.error("_______ ERRORE salvataggio del testo________");
			e.printStackTrace();
			if (timer != null)
				timer.cancel();
		} catch (StatusFault e) {
			//registrare errore nel db
			dbM.modificaStato(this.idDifformita, 160);
			LOGGER.error("_______ ERRORE invocazione servizio Status ________");
			e.printStackTrace();
			if (timer != null)
				timer.cancel();
		} catch (IOException|ParserConfigurationException|SAXException|DownloadFault e) {
			//registrare errore nel db
			dbM.modificaStato(this.idDifformita, 170);
			LOGGER.error("_______ ERRORE invocazione servizio Download________");
			e.printStackTrace();
			if (timer != null)
				timer.cancel();
		}

	}


	private GatewayResponse startClassification(String testo) throws HttpResponseException, IOException {
		crm.initClient(propertiesBean.getCrmHost(), propertiesBean.getCrmPort(), propertiesBean.getCrmUser(), propertiesBean.getCrmPassword());
		GatewayResponse crmResponse=crm.startClassification(testo);
		return crmResponse;
	}
	
	private String startServiceStatus() throws MalformedURLException, StatusFault {
		
		String stato = "";
		
		StatusWS serviceS = new ServiceStatus().getService();

		StatusRequest statusRequest = new StatusRequest();
		statusRequest.setClientInfo(UtilsAsr.popolaclientInfo());
		statusRequest.setJobId(Long.parseLong(identificativo));
		StatusResponse statusResponse = serviceS.status(statusRequest);

		if (statusResponse.getJob().isEmpty()) {
			LOGGER.error("_______ job NON trovato ________");
			//registrare errore nel db nessun job trovato per identificativo inserito
			dbM.modificaStato(this.idDifformita, 150);
			throw new StatusFault("nessun job trovato per identificativo inserito " + identificativo, null) ;
		} 

		JobFileType job = statusResponse.getJob().get(0);
		LOGGER.info("_______  job stato : " + job.getStatus().value());

		//job in errore 
		if(job.getStatus().value().equals(EnumStatusType.FAILED.value())) {
			//registrare errore nel db nessun job trovato per identificativo inserito
			dbM.modificaStato(this.idDifformita, 150);
			LOGGER.info("_______ job terminato con errore : " + job.getErrCode());
			FaultType fault = new FaultType();
			fault.setErrorCode(String.valueOf( job.getErrCode() ));
			fault.setErrorMessage(job.getStatusReason());
			throw new StatusFault("job terminato con errore", fault) ;
		}
		
		stato = job.getStatus().value();
		
		return stato;
		
	}
	
	
	private String startServiceDownload() throws DownloadFault, IOException, ParserConfigurationException, SAXException {
		
		String testo ="";
		
		DownloadWS serviceD = new ServiceDownload().getService();
		DownloadRequest downloadRequest = new DownloadRequest();
		
		downloadRequest.setClientInfo(UtilsAsr.popolaclientInfo());
		downloadRequest.setJobId(Long.parseLong(identificativo));
		downloadRequest.setFormat(OutputType.PVT);

		DownloadResponse downloadResponse = serviceD.download(downloadRequest);
		
		//formattare il testo dalle response
		DataHandler dataHandler = downloadResponse.getTranscription().getData();
		
		final InputStream in = dataHandler.getInputStream();
		byte[] byteArray = org.apache.commons.io.IOUtils.toByteArray(in);
		
		String filePvt = new String(byteArray, "UTF-8");
		
		LOGGER.info("_______ filePvt : " + filePvt);
		
		Document fileXml = UtilsAsr.convertStringToDocument(filePvt);
		fileXml.getDocumentElement().normalize();
		
		testo = UtilsAsr.concatena(fileXml);
		
		LOGGER.info("_______ testo : " + testo);

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

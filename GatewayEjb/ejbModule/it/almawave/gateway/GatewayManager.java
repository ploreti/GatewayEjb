package it.almawave.gateway;

import java.io.IOException;
import java.net.MalformedURLException;

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
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.almawave.gateway.asr.ServiceDownload;
import it.almawave.gateway.asr.ServiceStatus;
import it.almawave.gateway.bean.GatewayResponse;
import it.almawave.gateway.configuration.Parametri;
import it.almawave.gateway.configuration.PropertiesBean;
import it.almawave.gateway.crm.CRMClient;
import it.almawave.gateway.db.DbManager;
import it.almawave.gateway.db.excption.DbException;
import it.pervoice.audiomabox.services.common._1.EnumStatusType;
import it.pervoice.audiomabox.services.download._1.DownloadResponse;
import it.pervoice.audiomabox.services.status._1.StatusResponse;
import it.pervoice.ws.audiomabox.service.download._1.DownloadFault;
import it.pervoice.ws.audiomabox.service.download._1.DownloadWS;
import it.pervoice.ws.audiomabox.service.status._1.StatusFault;
import it.pervoice.ws.audiomabox.service.status._1.StatusWS;



/**
 * Session Bean implementation class StatusTimerService
 */
@Stateless
@LocalBean
public class GatewayManager {
	private static final Logger LOGGER = Logger.getLogger(GatewayManager.class);

	private String timerName;
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

	public GatewayManager() {

	}

	public void init(String id, String idDifformita) throws  IOException, DbException {

		this.identificativo = id;
		this.idDifformita = idDifformita;

		TimerConfig timerConfig = new TimerConfig();
		timerName="StatusTimerService_"+this.getIdDifformita();
		timerConfig.setInfo(timerName);
		Long start = Long.valueOf(propertiesBean.getValore(Parametri.initialDuration));
		Long iter = Long.valueOf(propertiesBean.getValore(Parametri.internalDuration));
		LOGGER.info("Timer initial duration and internal duration: "+start+" "+ iter);

		timerService.createIntervalTimer(start, iter, timerConfig); //ogni 5 sec

		dbM.modificaStato(this.idDifformita, 102);//in lavorazione
	}

	@Timeout
	public void startProcess(Timer timer) throws DbException{

		try {
			if (identificativo == null) {
				//timer.cancel();
				removeTimer();
				return;
			}

			//chiamare il servizio statusService
			String stato = startServiceStatus();

			LOGGER.info(composeHeaderMsg(timer.getInfo() +" "+stato));

			/*
			 * La richiesta è stata completata da ASR
			 * Posso procedere con il resto del processo
			 */
			if (stato.equals(EnumStatusType.COMPLETED.value()) ){

				//timer.cancel();
				removeTimer();
				//chiamare il servizio Download
				String testo = startServiceDownload();
				LOGGER.info(testo);
				//TODO: solo per test
				testo = "abrasioni su piano rotolamento corda alt dal chilometro 206+470 206+570";

				//chiamo il servizio del cmr
				GatewayResponse crmResponse = startClassification(testo);

				//mofico lo stato in concluso
				dbM.modificaStato(this.idDifformita, 110);//completata

				LOGGER.info(composeHeaderMsg(this.idDifformita +" PROCESSO CONCLUSO"));

			}

			

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
		String url = propertiesBean.getValore(Parametri.asrStatusUrl);
		if (isMokcServicesAsr) url = propertiesBean.getValore(Parametri.simAsrStatusUrl);
		String user = propertiesBean.getValore(Parametri.asrUser);
		String pw = propertiesBean.getValore(Parametri.asrPassword);

		ServiceStatus statusService = new ServiceStatus(url, user, pw, isMokcServicesAsr);

		StatusWS serviceS = statusService.getService();

		StatusResponse statusResponse = serviceS.status(statusService.initStatusRequest(identificativo, propertiesBean));

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
		String url = propertiesBean.getValore(Parametri.asrDownloadUrl);
		if (isMokcServicesAsr) url = propertiesBean.getValore(Parametri.simAsrDownloadUrl);
		String user = propertiesBean.getValore(Parametri.asrUser);
		String pw = propertiesBean.getValore(Parametri.asrPassword);

		ServiceDownload downloadService = new ServiceDownload(url, user, pw, isMokcServicesAsr);

		DownloadWS serviceD = downloadService.getService();

		DownloadResponse downloadResponse = serviceD.download(downloadService.initDownloadRequest(identificativo, propertiesBean));

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

	private void removeTimer() {
		TimerService timerService = this.timerService;
		for (Object obj : timerService.getTimers()) {
			javax.ejb.Timer timer = (javax.ejb.Timer) obj;
			String scheduled = (String) timer.getInfo();
			//LOGGER.info(composeHeaderMsg("Timer Found : " + scheduled));
			if (scheduled.equals(timerName)) {
				LOGGER.info(composeHeaderMsg("Removing old timer : " + scheduled));
				timer.cancel();
			}
		}
	}

	private boolean checkTimer(String name) {
		boolean result=false;
		TimerService timerService = this.timerService;
		for (Object obj : timerService.getTimers()) {
			javax.ejb.Timer timer = (javax.ejb.Timer) obj;
			String scheduled = (String) timer.getInfo();
			//LOGGER.info(composeHeaderMsg("Timer Found : " + scheduled));
			if (scheduled.equals(name)) {
				result= true;
				break;
			}
		}
		return result;
	}

	private String composeHeaderMsg(String msg) {
		return("____________________"+msg.toUpperCase()+"____________________");
	}
}

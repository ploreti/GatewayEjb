package it.almawave.gateway;

import java.net.MalformedURLException;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.logging.Logger;

import it.almawave.gateway.asr.ServiceDownload;
import it.almawave.gateway.asr.ServiceStatus;
import it.almawave.gateway.asr.UtilsAsr;
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
 * Session Bean implementation class Job
 */
@Singleton
@LocalBean
public class GatewayExcutor {
	
	private static final Logger LOGGER = Logger.getLogger(GatewayExcutor.class);
	
	@EJB
	PropertiesBean propertiesBean;
	@EJB
	CRMClient crm;
	@EJB
	DbManager dbM;
	@EJB
	GatewayScheduler sj;

    /**
     * Default constructor. 
     */
    public GatewayExcutor() {
        
    }
    
	public void execute(String timerInfo) throws DbException {

		String idDifformita = null;
		try {

			StringTokenizer tokens = new StringTokenizer(timerInfo, "_");
			idDifformita = UtilsAsr.getIdDifformita(tokens);
			String idVoice = UtilsAsr.getId(tokens);
			Long intervallo = Long.valueOf(propertiesBean.getValore(Parametri.internalDuration));

			if (idDifformita == null || idDifformita.trim().equals(""))
				return;
			
			int statoDB = dbM.leggiIdStato(idDifformita);
			if (statoDB < 102)
				dbM.modificaStato(idDifformita, 102);// in lavorazione

			// invoco il servizio per conosce lo stato del Voice
			String stato = startServiceStatus(idVoice);
			LOGGER.info(UtilsAsr.composeHeaderMsg(timerInfo + " stato :" + stato));

			if (stato.equals(EnumStatusType.COMPLETED.value())) {
				// invoco il servizio per recuperare li testo generato da Voice
				String testo = startServiceDownload(idVoice, idDifformita);
				LOGGER.info(UtilsAsr.composeHeaderMsg("testo :" + testo));
				// TODO: solo per test da togliere
				testo = "urgente canalizzazione scoperta";//"abrasioni su piano rotolamento corda alt dal chilometro 206+470 206+570";
				// invoco il servizio per estrarre le triplette del testo
				startClassification(testo, idDifformita);
				dbM.modificaStato(idDifformita, 110);// completata
				LOGGER.info(UtilsAsr.composeHeaderMsg(timerInfo + " PROCESSO CONCLUSO"));

			} else {
				LOGGER.info(UtilsAsr.composeHeaderMsg("reload " + timerInfo ));
				// lanciare nuovamente lo scheduladore del job
				sj.init(idVoice, idDifformita, intervallo);
			}
		} catch (DbException e) {
			dbM.modificaStato(idDifformita, e.getCodice());
			LOGGER.error("_______ ERRORE salvataggio nel db ________");
		} catch (StatusFault e) {
			dbM.modificaStato(idDifformita, 120);
			LOGGER.error("_______ ERRORE invocazione servizio Status ________");
		} catch (DownloadFault e) {
			dbM.modificaStato(idDifformita, 121);
			LOGGER.error("_______ ERRORE invocazione servizio Download ________");
		} catch (ParserConfigurationException e) {
			dbM.modificaStato(idDifformita, 141);
			LOGGER.error("_______ ERRORE conversione oggetto ________");
		} catch (MalformedURLException e) {
			dbM.modificaStato(idDifformita, 122);
			LOGGER.error("_______ ERRORE url invocazione servizio ________");
		} catch (Exception e) {
			dbM.modificaStato(idDifformita, 999);
			LOGGER.error("_______ ERRORE generico ________" + e.getMessage());
			e.printStackTrace();
		}
	}
    
    
    
	/*
	 * eseguie la chiamata al servizio di asr per recuperare lo Status della richiesta 
	 * @return String lo stato della richiesta in lavorazione dal asr
	 */
	private String startServiceStatus(String identificativo) throws MalformedURLException, StatusFault {

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

	/*
	 * eseguie la chiamata al servizio di asr per recuperare il testo della richiesta e salva nel db il testo e il file pvt
	 * @return String testo estratto dal file audio passato a Voice
	 */
	private String startServiceDownload(String identificativo, String idDifformita) throws MalformedURLException, DownloadFault, DbException, ParserConfigurationException  {

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
	 * eseguie la chiamata al servizio Iride Text Analytics e salva nel db le triplette
	 */
	private void startClassification(String testo, String idDifformita) throws DbException, ParserConfigurationException {
		crm.initClient(propertiesBean.getValore(Parametri.crmHost), Integer.parseInt(propertiesBean.getValore(Parametri.crmPort)), propertiesBean.getValore(Parametri.crmUser), propertiesBean.getValore(Parametri.crmPassword));
		GatewayResponse crmResponse = new GatewayResponse();
		try {
			crmResponse = crm.startClassification(testo);
		}catch (Exception e){
			throw new ParserConfigurationException();
		}
		//salva le triplette nel db
		dbM.inserisciResponse(idDifformita, crmResponse);
	}

}

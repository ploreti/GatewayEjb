package it.almawave.gateway;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.almawave.gateway.asr.ServiceUpload;
import it.almawave.gateway.bean.GatewayResponse;
import it.almawave.gateway.bean.Tuple;
import it.almawave.gateway.configuration.Parametri;
import it.almawave.gateway.configuration.PropertiesBean;
import it.almawave.gateway.crm.CRMClient;
import it.almawave.gateway.crm.bean.StartClassficationVOOut;
import it.almawave.gateway.db.DbManager;
import it.almawave.gateway.db.bean.CRMRequestBean;
import it.almawave.gateway.db.bean.DoRequestBean;
import it.almawave.gateway.db.excption.DbException;
import it.pervoice.audiomabox.services.upload._1.UploadResponse;
import it.pervoice.ws.audiomabox.service.upload._1.UploadFault;
import it.pervoice.ws.audiomabox.service.upload._1.UploadWS;

/**
 * Session Bean implementation class GatewayInternalDb
 */
@Stateless
@LocalBean
public class GatewayServices implements GatewayServicesRemote, GatewayServicesLocal {

	@EJB
	DbManager dbM;

	@EJB
	GatewayManager gm;

	@EJB
	PropertiesBean propertiesBean;

	@EJB
	CRMClient crm;

	private static final Logger LOGGER = Logger.getLogger(GatewayServices.class);

	//	@Resource
	//	private SessionContext sessionContext;
	/**
	 * Default constructor. 
	 */
	public GatewayServices() {

	}

	/**
	 * Il servizio avvia il processo di trascrizione/classificazione. 
	 * @param request
	 * @return Identificativo univoco della richiesta.
	 */
	//@TransactionAttribute(value=TransactionAttributeType.REQUIRES_NEW)
	public String doRequest(DoRequestBean request) {

		try {

			String id = "1000";
			
			Boolean isMokcServicesAsr = Boolean.parseBoolean( propertiesBean.getValore(Parametri.isMokcServicesAsr)) ;
			String url = propertiesBean.getValore(Parametri.asrUploadUrl);
			if (isMokcServicesAsr) url = propertiesBean.getValore(Parametri.simAsrUploadUrl);
			String user = propertiesBean.getValore(Parametri.asrUser);
			String pw = propertiesBean.getValore(Parametri.asrPassword);
			
			ServiceUpload uploadService = new ServiceUpload(url, user ,pw , isMokcServicesAsr);

			UploadWS service = uploadService.getService(); 

			UploadResponse uploadResponse = service.upload(uploadService.initStatusRequest(request, propertiesBean));
			
			id = uploadService.elaboraResonse(uploadResponse);

			//memorizzare nel db la requestee lo status
			dbM.inserisciRequest(request, id);
			
			//laciare il timer per il recupero dellostatus
			gm.init(id, request.getIdDifformita());

			return request.getIdDifformita();

		}catch (FileNotFoundException e) {
			return "File audio non trovato";
		} 
		catch (MalformedURLException|UploadFault e) {
			e.printStackTrace();
			return "Errore nella chiata al servizio Upload di PerVoice";
		}
		catch (Exception e) {
			return "Nessuna richiesta è stata inserita";
		}finally {

		}

	}

	/**
	 * Il servizio verifica lo stato della richiesta di trascrizione/classificazione. 
	 * @param id
	 * @return Stato della richiesta
	 */
	public String getStatus(String id) {

		try {

			return dbM.leggiStato(id);

		}catch (Exception e) {
			return "Errore nel recupero dello stato della richiesta";
		}finally {

		}

	}
	
	/**
	 * Il servizio ottenuta la conferma che la richiesta è stata completata, richiede il testo trascritto e classificato.
	 * @param id
	 * @return il testo trascritto, le tuple di classificazione (ordinate per livello di attendibilità), le entità e i concetti identificati.
	 */
	public String getResponse(String id) {

		try {

			return dbM.leggiResponse(id);

		}catch (Exception e) {
			return "Errore nel recupero della response della richiesta";
		}finally {

		}

	}



	public String tester() {

		String messaggio = "modificato";

		try {
			
			Properties p = System.getProperties();
			String _p= p.getProperty("jboss.server.name");
			
			System.out.println(_p);
			
		}catch (Exception e) {
			e.printStackTrace();
			return "Errore : " + e.getMessage();
		}finally {

		}

		return messaggio;

	}



}
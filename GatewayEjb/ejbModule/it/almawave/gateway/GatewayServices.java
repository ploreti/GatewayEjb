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

//	@EJB
//	GatewayManager gm;
	@EJB
	GatewayScheduler sj;

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
			Boolean isMokcServicesAsr = Boolean.parseBoolean( propertiesBean.getValore(Parametri.isMokcServicesAsr)) ;
			String url = propertiesBean.getValore(Parametri.asrUploadUrl);
			if (isMokcServicesAsr) url = propertiesBean.getValore(Parametri.simAsrUploadUrl);
			String user = propertiesBean.getValore(Parametri.asrUser);
			String pw = propertiesBean.getValore(Parametri.asrPassword);
			
			ServiceUpload uploadService = new ServiceUpload(url, user ,pw , isMokcServicesAsr);

			UploadWS service = uploadService.getService(); 

			UploadResponse uploadResponse = service.upload(uploadService.initStatusRequest(request, propertiesBean));
			
			String id = uploadService.elaboraResonse(uploadResponse);

			//memorizzare nel db la requestee lo status
			dbM.inserisciRequest(request, id);
			
			//laciare il timer per il recupero dellostatus
			//gm.init(id, request.getIdDifformita());
			Long start = Long.valueOf(propertiesBean.getValore(Parametri.initialDuration));
			sj.init(id, request.getIdDifformita(), start);

			return request.getIdDifformita();

		}catch (FileNotFoundException e) {
			return "File audio non trovato";
		} 
		catch (MalformedURLException|UploadFault e) {
			e.printStackTrace();
			return "Errore nella chiata al servizio Upload di PerVoice";
		}
		catch (Exception e) {
			return "Nessuna richiesta � stata inserita";
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
	 * Il servizio ottenuta la conferma che la richiesta � stata completata, richiede il testo trascritto e classificato.
	 * @param id
	 * @return il testo trascritto, le tuple di classificazione (ordinate per livello di attendibilit�), le entit� e i concetti identificati.
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

	/*
	 * Only for test
	 */
	public String startClassification() throws HttpResponseException, IOException, DbException {
		GatewayResponse gr=new GatewayResponse();
		String testo = "urgente canalizzazione scoperta";
		crm.initClient(propertiesBean.getValore(Parametri.crmHost), Integer.parseInt(propertiesBean.getValore(Parametri.crmPort)), propertiesBean.getValore(Parametri.crmUser), propertiesBean.getValore(Parametri.crmPassword));

		CRMRequestBean bean = new CRMRequestBean();
		List<String> classificationLogicList = new ArrayList<String>();
		//TODO: comporre il classificationLogicList
		classificationLogicList.add("Visita Al Binario a Piedi");
		bean.setClassificationLogicList(classificationLogicList);
		bean.setTextMessage(testo);
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(bean);

		CloseableHttpResponse crmResponse = crm.doPostJson(jsonString, propertiesBean.getValore(Parametri.crmClassificationEndPoint));
		String responseString=new BasicResponseHandler().handleResponse(crmResponse);
		ObjectMapper om=new ObjectMapper();
		StartClassficationVOOut startClssificationObject=om.readValue(responseString, StartClassficationVOOut.class);

		System.out.println("-----------------"+responseString);
		
		Map<String,Object> addProp=startClssificationObject.getAdditionalProperties();
		Iterator<String> keyIterator=addProp.keySet().iterator();
		while(keyIterator.hasNext()) {
			System.out.println("-----------------"+keyIterator.next());
		}
		

		ArrayList<LinkedHashMap<String,Object>> tupleScores=(ArrayList<LinkedHashMap<String,Object>>)addProp.get("tupleScores");
		
        
        List<Tuple> tupleList=new ArrayList<Tuple>();
        
		tupleScores.forEach(item->{
			Tuple tuple=new Tuple();
			tuple.setValue((String)item.get("label"));
			tuple.setRank((Integer)item.get("rankPosition"));
			System.out.println(item.get("label"));
			System.out.println(item.get("rankPosition"));
			tupleList.add(tuple);
			}
		);
		
		gr.setIsUrgent(false);
		ArrayList<LinkedHashMap<String,Object>> extractedConcepts=(ArrayList<LinkedHashMap<String,Object>>)addProp.get("extractedConcepts");
		
		extractedConcepts.forEach(item->{
			
			String resourceURI = (String)item.get("resourceURI");
			int index = resourceURI.indexOf("#urgente");
			if (index > 0)gr.setIsUrgent(true);

			System.out.println(item.get("resourceURI"));

			}
		);
		
		String plainText=(String)addProp.get("plainText");
		System.out.println(plainText);
		gr.setTuples(tupleList);
		gr.setPlainText(plainText);
		
		dbM.inserisciResponse("aaaa111", gr);
		
		String o = om.writeValueAsString(gr);
		
		LOGGER.info("-------------------------------\n " + o);
		return o;
	}

}
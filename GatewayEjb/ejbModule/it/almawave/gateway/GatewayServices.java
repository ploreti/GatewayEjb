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
			Boolean isMokcServicesAsr = Boolean.parseBoolean( propertiesBean.getValore(Parametri.isMokcServicesAsr)) ;
			String url = propertiesBean.getValore(Parametri.asrUploadUrl);
			if (isMokcServicesAsr) url = propertiesBean.getValore(Parametri.simAsrUploadUrl);
			String user = propertiesBean.getValore(Parametri.asrUser);
			String pw = propertiesBean.getValore(Parametri.asrPassword);
			
			ServiceUpload uploadService = new ServiceUpload(url, user ,pw , isMokcServicesAsr);

			UploadWS service = uploadService.getService(); 

			UploadResponse uploadResponse = service.upload(uploadService.initStatusRequest(request));
			
			String id = uploadService.elaboraResonse(uploadResponse);

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
			

			
			//dbM.inserisciFilePVTandTesto("aaaa111", "</Trans>", "INDEBITA DISPOSIZIONE A VIA IMPEDITA SEGNALE DI PARTENZA al km 16+300 Roma Anagnina");
			//dbM.modificaStato("aaaa111", 140);
			
			

//			File file = new File("/opt/visiteinlinea/simulator/Registrazione.m4a"); 
//			byte[] data = FileUtils.readFileToByteArray(file);
//			ByteArrayDataSource rawData = new ByteArrayDataSource(data,"application/octet-stream");
//
//			LOGGER.info("----------------- file letto + " + file.getName());
			
//			ServiceDownload serviceDownload = new ServiceDownload(propertiesBean.getAsrDownloadUrl(), propertiesBean.getAsrUser(), propertiesBean.getAsrPassword());
//			
//			DownloadWS service = serviceDownload.getService();
//			
//			DownloadRequest downloadRequest = new DownloadRequest();
//			
//			downloadRequest.setClientInfo(UtilsAsr.popolaclientInfo());
//			downloadRequest.setJobId(1500L);
//			downloadRequest.setFormat(OutputType.PVT);
//
//			DownloadResponse downloadResponse = service.download(downloadRequest);

//			UploadWS service = new ServiceUpload(propertiesBean.getAsrUploadUrl(), propertiesBean.getAsrUser(), propertiesBean.getAsrPassword()).getService(); 
//
//			UploadRequest uploadRequest = new UploadRequest();
//			//file
//			RemoteFile remoteFile = new RemoteFile();
//			FileType fileType = new FileType(); 
//			fileType.setName(file.getName());
//			DataHandler dataHandler =  new DataHandler(rawData);
//			fileType.setData(dataHandler);
//			remoteFile.setFile(fileType);
//			uploadRequest.setRemoteFile(remoteFile);
//
//			UploadResponse uploadResponse = service.upload(uploadRequest);
//
//			messaggio += uploadResponse.toString();

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
		String testo = "abrasioni su piano rotolamento corda alt dal chilometro 206+470 206+570";
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
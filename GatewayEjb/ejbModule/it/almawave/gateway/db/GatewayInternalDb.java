package it.almawave.gateway.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;

import it.almawave.gateway.asr.ServiceUpload;
import it.almawave.gateway.asr.StatusTimerService;
import it.almawave.gateway.db.bean.DoRequestBean;
import it.almawave.gateway.internal.Request;
import it.almawave.gateway.internal.RequestStatus;
import it.pervoice.audiomabox.commontypes._1.FileType;
import it.pervoice.audiomabox.services.upload._1.UploadRequest;
import it.pervoice.audiomabox.services.upload._1.UploadRequest.RemoteFile;
import it.pervoice.audiomabox.services.upload._1.UploadResponse;
import it.pervoice.ws.audiomabox.service.upload._1.UploadFault;
import it.pervoice.ws.audiomabox.service.upload._1.UploadWS;

/**
 * Session Bean implementation class GatewayInternalDb
 */
@Stateless
@LocalBean
public class GatewayInternalDb implements GatewayInternalDbRemote, GatewayInternalDbLocal {

	@PersistenceContext(unitName = "GatewayJpa")
	EntityManager em;

	@EJB
	StatusTimerService st;

	@EJB
	Test test;
	//	@Resource
	//	private SessionContext sessionContext;
	/**
	 * Default constructor. 
	 */
	public GatewayInternalDb() {

	}

	/**
	 * Il servizio avvia il processo di trascrizione/classificazione. 
	 * @param request
	 * @return Identificativo univoco della richiesta.
	 */
	@TransactionAttribute(value=TransactionAttributeType.REQUIRES_NEW)
	public String doRequest(DoRequestBean request) {

		try {

			//recuperare il file
			File file = new File(request.getPercorsoFileAudio()); 
			byte[] data = FileUtils.readFileToByteArray(file);
			ByteArrayDataSource rawData = new ByteArrayDataSource(data,"application/octet-stream");

			//chamare il servizio uploadService
			UploadWS service = new ServiceUpload().getService(); 

			UploadRequest uploadRequest = new UploadRequest();
			//file
			RemoteFile remoteFile = new RemoteFile();
			FileType fileType = new FileType(); 
			fileType.setName(file.getName());
			DataHandler dataHandler =  new DataHandler(rawData);
			fileType.setData(dataHandler);
			remoteFile.setFile(fileType);
			uploadRequest.setRemoteFile(remoteFile);

			//TODO: finire di completare la riquest

			//recuperare id dalla respons
			UploadResponse uploadResponse = service.upload(uploadRequest);
			String id = String.valueOf(uploadResponse.getJobElement().get(0).getJobId());

			//memorizzare nel db la requeste e lo status
			Request _request = new Request();
			_request.setEXT_ID(request.getIdDifformita());
			_request.setNODE_ID(1);
			_request.setFILE_URI(request.getPercorsoFileAudio());
			_request.setTIPO_VISITA(request.getTipoVisita());
			_request.setDTP(request.getDtp());
			_request.setSPECIALIZZAZIONE(request.getSpecializzazione());
			_request.setAUDIOMA_ID(Long.valueOf(id));

			RequestStatus _requestStatus = new RequestStatus();

			_requestStatus.setEXT_ID(request.getIdDifformita());
			_requestStatus.setINSERT_DATE(new Date());
			_requestStatus.setSTATUS(1);
			_requestStatus.setSYSTEM_ID(1);

			em.persist(_request);
			em.persist(_requestStatus);

			//laciare il timer per il recupero dello status
			st = new StatusTimerService(id, request.getIdDifformita());

			return request.getIdDifformita();

		}catch (FileNotFoundException e) {
			return "File audio non trovato";
		} catch (UploadFault e) {
			e.printStackTrace();
			return "Errore nella chiata al servizio Upload di PerVoice";
		}catch (Exception e) {
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

			Query query = em.createNamedQuery("RequestStatus.findStatusByExtId");
			query.setParameter("extID", id);
			List results = query.getResultList();

			if (!results.isEmpty())
				return "Nessuna richiesta è stata trovata";

			return ((Integer)results.get(0)).toString();


		}catch (Exception e) {
			return "Errore nel recupero dello sto della richiesta";
		}finally {

		}

	}

	public void tester() {
		test.callEJB();
	}

}
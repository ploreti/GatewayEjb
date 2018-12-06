package it.almawave.gateway.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
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

	@EJB
	DbManager dbM;

	@EJB
	StatusTimerService st;

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

			//TODO: finire di completare la request

			//recuperare id dalla respons
			UploadResponse uploadResponse = service.upload(uploadRequest);
			String id = String.valueOf(uploadResponse.getJobElement().get(0).getJobId());

			//memorizzare nel db la requeste e lo status
			dbM.inserisciRequest(request, id);

			//laciare il timer per il recupero dello status
			st.init(id, request.getIdDifformita());

			return request.getIdDifformita();

		}catch (FileNotFoundException e) {
			return "File audio non trovato";
		} catch (MalformedURLException|UploadFault e) {
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

			return dbM.leggiStato(id);

		}catch (Exception e) {
			return "Errore nel recupero dello sto della richiesta";
		}finally {

		}

	}



}
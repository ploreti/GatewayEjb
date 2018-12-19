package it.almawave.gateway.asr;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;

import it.almawave.gateway.configuration.Parametri;
import it.almawave.gateway.configuration.PropertiesBean;
import it.almawave.gateway.db.bean.DoRequestBean;
import it.pervoice.audiomabox.commontypes._1.FileType;
import it.pervoice.audiomabox.commontypes._1.UploadTypeEnum;
import it.pervoice.audiomabox.services.upload._1.OutputFormatType;
import it.pervoice.audiomabox.services.upload._1.UploadRequest;
import it.pervoice.audiomabox.services.upload._1.UploadRequest.RemoteFile;
import it.pervoice.audiomabox.services.upload._1.UploadResponse;
import it.pervoice.ws.audiomabox.service.upload._1.UploadWS;
import it.pervoice.ws.audiomabox.service.upload._1.UploadWSService;

public class ServiceUpload {
	
	//https://www.youtube.com/watch?v=VdVbhaVvMtc  -------- mock service soap
	//https://www.soapui.org/soap-mocking/service-mocking-overview.html
	
	private static final Logger LOGGER = Logger.getLogger(ServiceUpload.class);
	private UploadWS uploadWS = null;

	public ServiceUpload(String serviceUploadUrl, String username, String password, Boolean isMokcServicesAsr) throws MalformedURLException {
		
		LOGGER.info("[Costrutture Service Upload INVOKED]");

		URL baseUrl =  it.pervoice.ws.audiomabox.service.upload._1.UploadWSService.class.getResource(".");
		URL url = new URL(baseUrl, serviceUploadUrl);
		


		LOGGER.info("url " + url.toString());

		UploadWSService service = null;
		if (isMokcServicesAsr)
			service = new UploadWSService(url, new QName("http://ws.mock.asr.visiteinlinea.it/", "ServiceUpdate"));
		else
		    service = new UploadWSService(url, new QName("http://ws.pervoice.it/audiomabox/service/Upload/1.0/", "UploadWSService"));

		uploadWS = service.getPort(UploadWS.class);
		
		
		BindingProvider bp = (BindingProvider)uploadWS;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUploadUrl);
		// Add username and password for Basic Authentication
		bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
		bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		
		Binding binding = bp.getBinding();

		// Add the logging handler
		List<Handler> handlerList = binding.getHandlerChain();
		if (handlerList == null)
			handlerList = new ArrayList();
		LoggingHandler loggingHandler = new LoggingHandler();
		handlerList.add(loggingHandler);
		binding.setHandlerChain(handlerList);

		LOGGER.info("[Costrutture Service Upload ENDED]");

	}

	public UploadWS getService() {
		return uploadWS;
	}
	
	public ByteArrayDataSource recuperaFile(String url) throws FileNotFoundException {
		
		try {
			//recuperare il file
			File file = new File(url); 
			byte[] data = FileUtils.readFileToByteArray(file);
			ByteArrayDataSource rawData = new ByteArrayDataSource(data,"application/octet-stream");
			
			return  rawData;
		} catch (Exception e) {
			throw new FileNotFoundException();
		}
		
	}
	
	public UploadRequest initStatusRequest(DoRequestBean request, PropertiesBean propertiesBean) throws FileNotFoundException  {
		
		ByteArrayDataSource rawData = this.recuperaFile(request.getPercorsoFileAudio());
		
		UploadRequest uploadRequest = new UploadRequest();
		uploadRequest.setClientInfo(UtilsAsr.popolaclientInfo(propertiesBean));
		
		//file
		RemoteFile remoteFile = new RemoteFile();
		FileType fileType = new FileType(); 
		fileType.setName(rawData.getName());
		DataHandler dataHandler =  new DataHandler(rawData);
		fileType.setData(dataHandler);
		remoteFile.setFile(fileType);
		uploadRequest.setRemoteFile(remoteFile);
		
		uploadRequest.setUploadType(UploadTypeEnum.REMOTE_FILE);
		uploadRequest.getOutputFormats().add(OutputFormatType.PVT);
		uploadRequest.setDomainId(propertiesBean.getValore(Parametri.uploadDomainId)); //ita_ITA_RFI0_VINL-ita_ITA_STND_W_DHD
		uploadRequest.setSlots(Integer.getInteger(propertiesBean.getValore(Parametri.uploadSlot))); //8

		//uploadRequest.setCustomerProvidedId("1");
		//uploadRequest.setManualRevision(true);
		//uploadRequest.setPunctuationEnabled(true);
		
//		PriorityType priorityType = new PriorityType();
//		priorityType.setCode("HIGH");
//		uploadRequest.setPriority(priorityType);
		
		//uploadRequest.setStep(StepTypeEnum.ONE_STEP);
		
		return uploadRequest;
		
	}
	
	public String elaboraResonse(UploadResponse uploadResponse) {
		
		String id = "";
		
		id = String.valueOf(uploadResponse.getJobElement().get(0).getJobId());
		
		return id;
		
	}

	
	

}

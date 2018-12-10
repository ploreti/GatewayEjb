package it.almawave.gateway.asr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.jboss.logging.Logger;

import it.pervoice.ws.audiomabox.service.upload._1.UploadWS;
import it.pervoice.ws.audiomabox.service.upload._1.UploadWSService;

public class ServiceUpload {
	
	//https://www.youtube.com/watch?v=VdVbhaVvMtc  -------- mock service soap
	//https://www.soapui.org/soap-mocking/service-mocking-overview.html
	
	private static final Logger LOGGER = Logger.getLogger(ServiceUpload.class);
	private UploadWS uploadWS = null;
	//TODO: configurare con le properties
	private String serviceUploadUrl = "http://10.121.193.80:80/audioma-ws/uploadService/";

	public ServiceUpload() throws MalformedURLException {
		
		try {
		
		LOGGER.info("[Costrutture Service Upload INVOKED]");

		URL baseUrl =  it.pervoice.ws.audiomabox.service.upload._1.UploadWSService.class.getResource(".");
		URL url = new URL(baseUrl, serviceUploadUrl);
		
		LOGGER.info("----------------- baseUrl  " + baseUrl.toString());
		LOGGER.info("----------------- url  " + url.toString());
		
		UploadWSService service = new UploadWSService();
		//service = new UploadWSService(url, new QName("http://ws.pervoice.it/audiomabox/service/Upload/1.0/", "UploadWSService"));

		LOGGER.info("----------------- service istanziato + " + service.getWSDLDocumentLocation().getFile());
		uploadWS = service.getUploadWSSoap11();
		
		// Add username and password for Basic Authentication
		Map<String, Object> reqContext = ((BindingProvider) uploadWS).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, "abox");
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, "ab0x");
		
		BindingProvider bp = (BindingProvider)uploadWS;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUploadUrl);
		Binding binding = bp.getBinding();

		// Add the logging handler
		List<Handler> handlerList = binding.getHandlerChain();
		if (handlerList == null)
			handlerList = new ArrayList();
		LoggingHandler loggingHandler = new LoggingHandler();
		handlerList.add(loggingHandler);
		binding.setHandlerChain(handlerList);

		LOGGER.info("[Costrutture Service Upload ENDED]");
		
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	public UploadWS getService() {
		return uploadWS;
	}

	
	

}

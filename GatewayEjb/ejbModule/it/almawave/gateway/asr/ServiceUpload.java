package it.almawave.gateway.asr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
	private String serviceUploadUrl = "";

	public ServiceUpload() throws MalformedURLException {
		
		LOGGER.info("[Costrutture Service Upload INVOKED]");

		URL baseUrl =  it.pervoice.ws.audiomabox.service.upload._1.UploadWSService.class.getResource(".");
		URL url = new URL(baseUrl, serviceUploadUrl);
		
		UploadWSService service = null;
		service = new UploadWSService(url, new QName("http://ws.pervoice.it/audiomabox/service/Upload/1.0/", "UploadWSService"));

		UploadWS port = service.getPort(UploadWS.class);
		BindingProvider bp = (BindingProvider)port;
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

	}

	public UploadWS getService() {
		return uploadWS;
	}

	
	

}

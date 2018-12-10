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

import it.pervoice.ws.audiomabox.service.download._1.DownloadWS;
import it.pervoice.ws.audiomabox.service.download._1.DownloadWSService;

public class ServiceDownload {
	
	
	private static final Logger LOGGER = Logger.getLogger(ServiceDownload.class);
	private DownloadWS downloadWS = null;


	public ServiceDownload(String serviceDownloadUrl, String username, String password) throws MalformedURLException {
		
		LOGGER.info("[Costrutture Service Download INVOKED]");

		URL baseUrl =  it.pervoice.ws.audiomabox.service.download._1.DownloadWSService.class.getResource(".");
		URL url = new URL(baseUrl, serviceDownloadUrl);
		
		LOGGER.info("----------------- url  " + url.toString());
		
		DownloadWSService service = new DownloadWSService();
		//service = new DownloadWSService(url, new QName("http://ws.pervoice.it/audiomabox/service/Download/1.0/", "DownloadWSService"));
		
		LOGGER.info("----------------- service istanziato");

		downloadWS = service.getDownloadWSSoap11();
		
		// Add username and password for Basic Authentication
		Map<String, Object> reqContext = ((BindingProvider) downloadWS).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, username);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		BindingProvider bp = (BindingProvider)downloadWS;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceDownloadUrl);
		Binding binding = bp.getBinding();

		// Add the logging handler
		List<Handler> handlerList = binding.getHandlerChain();
		if (handlerList == null)
			handlerList = new ArrayList();
		LoggingHandler loggingHandler = new LoggingHandler();
		handlerList.add(loggingHandler);
		binding.setHandlerChain(handlerList);

		LOGGER.info("[Costrutture Service Download ENDED]");

	}

	public DownloadWS getService() {
		return downloadWS;
	}

	
	

}

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

import it.pervoice.ws.audiomabox.service.status._1.StatusWS;
import it.pervoice.ws.audiomabox.service.status._1.StatusWSService;

public class ServiceStatus {
	
	
	private static final Logger LOGGER = Logger.getLogger(ServiceStatus.class);
	private StatusWS statusWS = null;

	public ServiceStatus(String serviceStatusUrl, String username, String password) throws MalformedURLException {
		
		LOGGER.info("[Costrutture Service Status INVOKED]");

		URL baseUrl =  it.pervoice.ws.audiomabox.service.status._1.StatusWSService.class.getResource(".");
		URL url = new URL(baseUrl, serviceStatusUrl);
		
		LOGGER.info("----------------- url  " + url.toString());
		
		StatusWSService service = new StatusWSService();
		//service = new StatusWSService(url, new QName("http://ws.pervoice.it/audiomabox/service/Status/1.0", "StatusWSService"));

		LOGGER.info("----------------- service istanziato");
		
		statusWS = service.getStatusWSSoap11();
		
		// Add username and password for Basic Authentication
		Map<String, Object> reqContext = ((BindingProvider) statusWS).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, username);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		BindingProvider bp = (BindingProvider)statusWS;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceStatusUrl);
		Binding binding = bp.getBinding();

		// Add the logging handler
		List<Handler> handlerList = binding.getHandlerChain();
		if (handlerList == null)
			handlerList = new ArrayList();
		LoggingHandler loggingHandler = new LoggingHandler();
		handlerList.add(loggingHandler);
		binding.setHandlerChain(handlerList);

		LOGGER.info("[Costrutture Service Status ENDED]");

	}

	public StatusWS getService() {
		return statusWS;
	}

	
	

}

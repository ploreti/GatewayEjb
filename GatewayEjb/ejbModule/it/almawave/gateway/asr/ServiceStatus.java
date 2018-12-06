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

import it.pervoice.ws.audiomabox.service.status._1.StatusWS;
import it.pervoice.ws.audiomabox.service.status._1.StatusWSService;

public class ServiceStatus {
	
	
	private static final Logger LOGGER = Logger.getLogger(ServiceStatus.class);
	private StatusWS statusWS = null;
	//TODO: configurare con le properties
	private String serviceStatusUrl = "http://10.121.193.80:80/audioma-ws/statusService/";

	public ServiceStatus() throws MalformedURLException {
		
		LOGGER.info("[Costrutture Service Status INVOKED]");

		URL baseUrl =  it.pervoice.ws.audiomabox.service.status._1.StatusWSService.class.getResource(".");
		URL url = new URL(baseUrl, serviceStatusUrl);
		
		StatusWSService service = null;
		service = new StatusWSService(url, new QName("http://ws.pervoice.it/audiomabox/service/Status/1.0", "StatusWSService"));

		StatusWS port = service.getPort(StatusWS.class);
		BindingProvider bp = (BindingProvider)port;
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

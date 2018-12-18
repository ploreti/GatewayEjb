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

import it.almawave.gateway.configuration.PropertiesBean;
import it.pervoice.audiomabox.services.common._1.EnumStatusType;
import it.pervoice.audiomabox.services.common._1.FaultType;
import it.pervoice.audiomabox.services.status._1.JobFileType;
import it.pervoice.audiomabox.services.status._1.StatusRequest;
import it.pervoice.audiomabox.services.status._1.StatusResponse;
import it.pervoice.ws.audiomabox.service.status._1.StatusFault;
import it.pervoice.ws.audiomabox.service.status._1.StatusWS;
import it.pervoice.ws.audiomabox.service.status._1.StatusWSService;

public class ServiceStatus {
	
	
	private static final Logger LOGGER = Logger.getLogger(ServiceStatus.class);
	private StatusWS statusWS = null;

	public ServiceStatus(String serviceStatusUrl, String username, String password, Boolean isMokcServicesAsr) throws MalformedURLException {
		
		LOGGER.info("[Costrutture Service Status INVOKED]");

		URL baseUrl =  it.pervoice.ws.audiomabox.service.status._1.StatusWSService.class.getResource(".");
		URL url = new URL(baseUrl, serviceStatusUrl);
		
		StatusWSService service = null;
		if (isMokcServicesAsr)
			service = new StatusWSService(url, new QName("http://ws.mock.asr.visiteinlinea.it/", "ServiceStatus"));
		else
			service = new StatusWSService(url, new QName("http://ws.pervoice.it/audiomabox/service/Status/1.0", "StatusWSService"));
		
		statusWS = service.getPort(StatusWS.class);
		
		BindingProvider bp = (BindingProvider)statusWS;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceStatusUrl);
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

		LOGGER.info("[Costrutture Service Status ENDED]");

	}

	public StatusWS getService() {
		return statusWS;
	}
	
	
	public StatusRequest initStatusRequest(String identificativo, PropertiesBean propertiesBean) {
		
		StatusRequest statusRequest = new StatusRequest();
		statusRequest.setClientInfo(UtilsAsr.popolaclientInfo(propertiesBean));
		statusRequest.setJobId(Long.parseLong(identificativo));
		
		return statusRequest;
		
	}
	
	public String elaboraResonse(StatusResponse statusResponse, String identificativo) throws StatusFault {
		String stato = "";
		
		if (statusResponse.getJob().isEmpty()) {
			LOGGER.error("_______ job NON trovato ________");
			throw new StatusFault("nessun job trovato per identificativo inserito " + identificativo, null) ;
		} 

		JobFileType job = statusResponse.getJob().get(0);

		//job in errore 
		if(job.getStatus().value().equals(EnumStatusType.FAILED.value())) {
			LOGGER.info("_______ job terminato con errore : " + job.getErrCode());
			FaultType fault = new FaultType();
			fault.setErrorCode(String.valueOf( job.getErrCode() ));
			fault.setErrorMessage(job.getStatusReason());
			throw new StatusFault("job terminato con errore", fault) ;
		}
		
		stato = job.getStatus().value();
		
		return stato;
		
	}

	
	

}

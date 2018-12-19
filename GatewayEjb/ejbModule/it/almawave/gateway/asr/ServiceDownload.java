package it.almawave.gateway.asr;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import it.almawave.gateway.configuration.PropertiesBean;
import it.pervoice.audiomabox.commontypes._1.OutputType;
import it.pervoice.audiomabox.services.download._1.DownloadRequest;
import it.pervoice.audiomabox.services.download._1.DownloadResponse;
import it.pervoice.ws.audiomabox.service.download._1.DownloadWS;
import it.pervoice.ws.audiomabox.service.download._1.DownloadWSService;

public class ServiceDownload {
	
	
	private static final Logger LOGGER = Logger.getLogger(ServiceDownload.class);
	private DownloadWS downloadWS = null;


	public ServiceDownload(String serviceDownloadUrl, String username, String password, Boolean isMokcServicesAsr) throws MalformedURLException {
		
		LOGGER.info("[Costrutture Service Download INVOKED]");

		URL baseUrl =  it.pervoice.ws.audiomabox.service.download._1.DownloadWSService.class.getResource(".");
		URL url = new URL(baseUrl, serviceDownloadUrl);
		

		LOGGER.info("url " + url.toString());
		
		DownloadWSService service = null;
		if (isMokcServicesAsr)
			service = new DownloadWSService(url, new QName("http://ws.mock.asr.visiteinlinea.it/", "ServiceDownload"));
		else
			service = new DownloadWSService(url, new QName("http://ws.pervoice.it/audiomabox/service/Download/1.0/", "DownloadWSService"));

		downloadWS = service.getPort(DownloadWS.class);
		
		BindingProvider bp = (BindingProvider)downloadWS;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceDownloadUrl);
		// Add username and password for Basic Authentication
		bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
		bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		
//		bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 10000);
//		bp.getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 10000);
//		bp.getRequestContext().put("com.sun.xml.ws.request.timeout", 10000);
//		bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", 10000);
		
//		//Set timeout until a connection is established
//		bp.getRequestContext().put("javax.xml.ws.client.connectionTimeout", "6000");
//		//Set timeout until the response is received
//		bp.getRequestContext().put("javax.xml.ws.client.receiveTimeout", "3000");
		
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
	
	public DownloadRequest initDownloadRequest(String identificativo, PropertiesBean propertiesBean) {
		
		DownloadRequest downloadRequest = new DownloadRequest();
		
		downloadRequest.setClientInfo(UtilsAsr.popolaclientInfo(propertiesBean));
		downloadRequest.setJobId(Long.parseLong(identificativo));
		downloadRequest.setFormat(OutputType.PVT);
		
		return downloadRequest;
		
	}
	
	
	public String[] elaboraResonse(DownloadResponse downloadResponse, String identificativo) throws IOException, ParserConfigurationException, SAXException {
		
		String[] testi = new String[2];
		
		//formattare il testo dalle response
		DataHandler dataHandler = downloadResponse.getTranscription().getData();
		
		final InputStream in = dataHandler.getInputStream();
		byte[] byteArray = org.apache.commons.io.IOUtils.toByteArray(in);
		
		String filePvt = new String(byteArray, "UTF-8");
		testi[0] = filePvt;
				
		Document fileXml = UtilsAsr.convertStringToDocument(filePvt);
		fileXml.getDocumentElement().normalize();
		String testo = UtilsAsr.concatena(fileXml);
		testi[1] = testo;
		
		return testi;
		
	}

	
	

}

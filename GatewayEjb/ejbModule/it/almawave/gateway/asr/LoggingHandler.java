package it.almawave.gateway.asr;

import java.io.ByteArrayOutputStream;
import java.util.Set;
import org.jboss.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class LoggingHandler implements SOAPHandler<SOAPMessageContext>{

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		ByteArrayOutputStream outputLog = null;

		Logger logger = Logger.getLogger(LoggingHandler.class);

		SOAPMessage msg = context.getMessage();

		boolean request = ((Boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

		int appoFlagIO = 0;
		try {
			outputLog = new ByteArrayOutputStream();
			try {
				if (request) { // This is a request message.
					appoFlagIO = 0;
					// Write the message to the output stream
					msg.writeTo(outputLog);

				} else { // This is the response message
					appoFlagIO = 1;
					msg.writeTo(outputLog);
				}
			} finally {
				if (outputLog != null) {
					outputLog.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	   
	   
	    String appoSOAP = new String(outputLog.toByteArray());

		if (appoFlagIO == 0){

			logger.info("-----SOAP request-----"+ appoSOAP);
		} else {

			logger.info("-----SOAP respons-----"+ appoSOAP);
		}
		
		return true;

	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
	   SOAPMessage msg = context.getMessage();
	   try {
	      msg.writeTo (System.out);
	   }
	   catch (Exception e) {e.printStackTrace();}
	   return true;
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

}

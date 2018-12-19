package it.almawave.gateway;

import java.io.IOException;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.jboss.logging.Logger;

import it.almawave.gateway.asr.UtilsAsr;
import it.almawave.gateway.db.excption.DbException;

@Stateless
@LocalBean
public class GatewayScheduler {
	
	private static final Logger LOGGER = Logger.getLogger(GatewayScheduler.class);
	
	@Resource
	TimerService timerservice;
	
	@EJB
	GatewayExcutor job;

    /**
     * Default constructor. 
     */
    public GatewayScheduler() {
        
    }
    /**
     * Inizializza il timer
     * @param idVoice identificativo della richeista di Voise 
     * @param idDifformita identificativo della ifformita
     * @param start numero di millesecondi
     * @throws IOException
     * @throws DbException
     */
    public void init(String idVoice, String idDifformita, Long start) {
    	TimerConfig timerConfig = new TimerConfig();
    	timerConfig.setInfo("Job_"+idDifformita+"_"+idVoice);
    	timerservice.createSingleActionTimer(start, timerConfig);
    	
    	LOGGER.info(UtilsAsr.composeHeaderMsg( "timer initialized " + timerConfig.getInfo()));
    }
    
    /**
     * metodo invoato al timeout del timer
     * @param timer
     */
    @Timeout
    public void metodoTimeout(Timer timer) throws DbException {
    	job.execute((String)timer.getInfo());
    }
    
    
//	public void stop(String nometimer) {
//		for (Object obj : timerservice.getTimers()) {
//			Timer timer = (Timer) obj;
//			String scheduled = (String) timer.getInfo();
//			if (scheduled.equals(nometimer)) {
//				timer.cancel();
//			}
//		}
//	}
	


}
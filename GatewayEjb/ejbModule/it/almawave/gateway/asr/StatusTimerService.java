package it.almawave.gateway.asr;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.jboss.logging.Logger;

import it.almawave.gateway.configuration.PropertiesBean;

/**
 * Session Bean implementation class StatusTimerService
 */
@Singleton
@LocalBean
public class StatusTimerService {
	@EJB
	PropertiesBean propertiesBean;
	
	private static final Logger LOGGER = Logger.getLogger(StatusTimerService.class);

	//AUDIOMA_ID
	private String identificativo = null;
	//EXT_ID
	private String idDifformita;
	int count;
    
    @Resource
    private TimerService timerService;
    
    public StatusTimerService() {

    }
    
    public StatusTimerService(String id, String idDifformita) {
//        this.identificativo = id;
//        this.idDifformita = idDifformita;
//        
//        TimerConfig timerConfig = new TimerConfig();
//    	timerConfig.setInfo("StatusTimerService_"+this.getIdentificativo());
//    	timerService.createIntervalTimer(5000, 5000, timerConfig); //ogni 5 sec 
    }

    //@PostConstruct
    public void init(String id, String idDifformita) {
        this.identificativo = id;
        this.idDifformita = idDifformita;
        
        TimerConfig timerConfig = new TimerConfig();
    	timerConfig.setInfo("StatusTimerService_"+this.getIdentificativo());
    	LOGGER.info("Timer initial duration and internal duration: "+propertiesBean.getInitialDuration()+" "+ propertiesBean.getInternalDuration());
    	timerService.createIntervalTimer(propertiesBean.getInitialDuration(), propertiesBean.getInternalDuration(), timerConfig); //ogni 5 sec 
    	
    	count=0;
    }
    
	@Timeout
	public void execute(Timer timer) {
		
		//chiamare il servizio statusService con id
		
		//memorizzare lo stato nel db per id esterno
		
		//verificare lo stato, se Completata o in errore fermare il timer
		
		//timer.cancel();
		
		LOGGER.info("Timer Service : " + timer.getInfo());
		LOGGER.info("Execution Time : " + new Date());
		LOGGER.info("____________________________________________");
		
		if(count<3) { count++;}
		else {
			timer.cancel();
		}
		
		if (identificativo == null)
			timer.cancel();
	}

        
	public String getIdentificativo() {
		return identificativo;
	}

	public void setIdentificativo(String identificativo) {
		this.identificativo = identificativo;
	}

	public String getIdDifformita() {
		return idDifformita;
	}

	public void setIdDifformita(String idDifformita) {
		this.idDifformita = idDifformita;
	}
 


}

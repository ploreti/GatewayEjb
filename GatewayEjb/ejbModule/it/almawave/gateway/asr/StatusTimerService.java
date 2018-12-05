package it.almawave.gateway.asr;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.jboss.logging.Logger;

/**
 * Session Bean implementation class StatusTimerService
 */
@Singleton
@LocalBean
public class StatusTimerService {
	
	private static final Logger LOGGER = Logger.getLogger(StatusTimerService.class);

	//AUDIOMA_ID
	private String identificativo = null;
	//EXT_ID
	private String idDifformita;
    
    
    @Resource
    private TimerService timerService;
    
    public StatusTimerService() {

    }
    public StatusTimerService(String id, String idDifformita) {
        this.identificativo = id;
        this.idDifformita = idDifformita;
        
        TimerConfig timerConfig = new TimerConfig();
    	timerConfig.setInfo("StatusTimerService_"+this.getIdentificativo());
    	timerService.createIntervalTimer(5000, 5000, timerConfig); //ogni 5 sec 
    }

    @PostConstruct
    private void init() {
    	
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

package it.almawave.gateway.configuration;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import it.almawave.gateway.db.DbManager;
import it.almawave.gateway.internal.Parameters;

@Singleton
@Startup
public class PropertiesBean {
	public enum States {BEFORESTARTED, STARTED, PAUSED, SHUTTINGDOWN};
	private States state;
	
	private ConcurrentHashMap<String,Parameters> hashParametroSistema = null;
	
	@EJB
	DbManager dbM;
    
	@PostConstruct
	public void initialize() {
		state = States.BEFORESTARTED;
		// Perform intialization
		state = States.STARTED;

		try {
			
			this.getHashParametroSistema();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void terminate() {
		state = States.SHUTTINGDOWN;
		resetHashParametroSistema();
		// Perform termination
		System.out.println("Shut down in progress");
	}
	

	public ConcurrentHashMap<String,Parameters> getHashParametroSistema() {
		try{
			if(hashParametroSistema == null || hashParametroSistema.isEmpty()){
				List<Parameters> elencoParametriSistema = dbM.getElencoParametriSistema();
				hashParametroSistema = new ConcurrentHashMap<String,Parameters>();
				
				for(int i=0; i<elencoParametriSistema.size(); i++){
					Parameters parametroBean = elencoParametriSistema.get(i);
					hashParametroSistema.put(parametroBean.getKEY(), parametroBean);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return hashParametroSistema;
	}
	
	public String getValore(Parametri parametro) {
		return getHashParametroSistema().get(parametro.name()).getVALUE();
	}
	

	public void resetHashParametroSistema() {
		hashParametroSistema = null;
	}

	public States getState() {
		return state;
	}
	public void setState(States state) {
		this.state = state;
	}

	
}
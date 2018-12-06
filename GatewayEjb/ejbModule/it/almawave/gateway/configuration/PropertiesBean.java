package it.almawave.gateway.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class PropertiesBean {
	public enum States {BEFORESTARTED, STARTED, PAUSED, SHUTTINGDOWN};
	private States state;
    
	private String crmHost;
	private int crmPort;
	private String crmClassificationEndPoint;
	private int initialDuration;
	private int internalDuration;
	
	@PostConstruct
	public void initialize() {
		state = States.BEFORESTARTED;
		// Perform intialization
		state = States.STARTED;
		InputStream is;
		try {
			is = new FileInputStream(System.getProperty("gatewayPropertiesFileName"));
			Properties prop=new Properties();
			prop.load(is);
			
			this.setCrmHost(prop.getProperty("crmHost"));
			this.setCrmPort(Integer.parseInt(prop.getProperty("crmPort")));
            this.setInitialDuration(Integer.parseInt(prop.getProperty("initialDuration")));
            this.setInternalDuration(Integer.parseInt(prop.getProperty("internalDuration")));
            this.setCrmClassificationEndPoint(prop.getProperty("crmClassificationEndPoint"));
			
		} catch (FileNotFoundException e) {
				e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void terminate() {
		state = States.SHUTTINGDOWN;
		// Perform termination
		System.out.println("Shut down in progress");
	}

	public States getState() {
		return state;
	}
	public void setState(States state) {
		this.state = state;
	}

	public String getCrmHost() {
		return crmHost;
	}

	public void setCrmHost(String crmHost) {
		this.crmHost = crmHost;
	}

	public int getInitialDuration() {
		return initialDuration;
	}

	public void setInitialDuration(int initialDuration) {
		this.initialDuration = initialDuration;
	}

	public int getInternalDuration() {
		return internalDuration;
	}

	public void setInternalDuration(int internalDuration) {
		this.internalDuration = internalDuration;
	}

	public int getCrmPort() {
		return crmPort;
	}

	public void setCrmPort(int crmPort) {
		this.crmPort = crmPort;
	}

	public String getCrmClassificationEndPoint() {
		return crmClassificationEndPoint;
	}

	public void setCrmClassificationEndPoint(String crmClassificationEndPoint) {
		this.crmClassificationEndPoint = crmClassificationEndPoint;
	}
}
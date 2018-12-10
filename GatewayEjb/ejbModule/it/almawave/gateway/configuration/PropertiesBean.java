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
	private String crmUser;
	private String crmPassword;
	private String asrUploadUrl;
	private String asrDownloadUrl;
	private String asrStatusUrl;
	private String asrUser;
	private String asrPassword;
	
	
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
            this.setCrmUser(prop.getProperty("crmUser"));
            this.setCrmPassword(prop.getProperty("crmPassword"));
            
            this.setAsrUploadUrl(prop.getProperty("asrUploadUrl"));
            this.setAsrDownloadUrl(prop.getProperty("asrDownloadUrl"));
            this.setAsrStatusUrl(prop.getProperty("asrStatusUrl"));
            this.setAsrUser(prop.getProperty("asrUser"));
            this.setAsrPassword(prop.getProperty("asrPassword"));
			
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

	public String getCrmUser() {
		return crmUser;
	}

	public void setCrmUser(String crmUser) {
		this.crmUser = crmUser;
	}

	public String getCrmPassword() {
		return crmPassword;
	}

	public void setCrmPassword(String crmPassword) {
		this.crmPassword = crmPassword;
	}

	public String getAsrUploadUrl() {
		return asrUploadUrl;
	}

	public void setAsrUploadUrl(String asrUploadUrl) {
		this.asrUploadUrl = asrUploadUrl;
	}

	public String getAsrDownloadUrl() {
		return asrDownloadUrl;
	}

	public void setAsrDownloadUrl(String asrDownloadUrl) {
		this.asrDownloadUrl = asrDownloadUrl;
	}

	public String getAsrStatusUrl() {
		return asrStatusUrl;
	}

	public void setAsrStatusUrl(String asrStatusUrl) {
		this.asrStatusUrl = asrStatusUrl;
	}

	public String getAsrUser() {
		return asrUser;
	}

	public void setAsrUser(String asrUser) {
		this.asrUser = asrUser;
	}

	public String getAsrPassword() {
		return asrPassword;
	}

	public void setAsrPassword(String asrPassword) {
		this.asrPassword = asrPassword;
	}
	
	
}
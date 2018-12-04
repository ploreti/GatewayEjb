package it.almawave.gateway.db.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "doRequestBean") 
public class DoRequestBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5317982815182288491L;
	
	private String percorsoFileAudio;
	private String idDifformita;
	private String tipoVisita; 
	private String dtp;
	private String specializzazione;
	
	public String getPercorsoFileAudio() {
		return percorsoFileAudio;
	}
	@XmlElement
	public void setPercorsoFileAudio(String percorsoFileAudio) {
		this.percorsoFileAudio = percorsoFileAudio;
	}
	public String getIdDifformita() {
		return idDifformita;
	}
	@XmlElement
	public void setIdDifformita(String idDifformita) {
		this.idDifformita = idDifformita;
	}
	public String getTipoVisita() {
		return tipoVisita;
	}
	@XmlElement
	public void setTipoVisita(String tipoVisita) {
		this.tipoVisita = tipoVisita;
	}
	public String getDtp() {
		return dtp;
	}
	@XmlElement
	public void setDtp(String dtp) {
		this.dtp = dtp;
	}
	public String getSpecializzazione() {
		return specializzazione;
	}
	@XmlElement
	public void setSpecializzazione(String specializzazione) {
		this.specializzazione = specializzazione;
	}
	
	


}

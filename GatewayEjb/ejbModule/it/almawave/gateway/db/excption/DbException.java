package it.almawave.gateway.db.excption;

public class DbException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String messaggio;
	private Integer codice;

	public DbException(String messaggio, Integer codice) {
		this.setMessaggio(messaggio);
		this.setCodice(codice);
	}
	public DbException(String messaggio, Integer codice, Exception e) {
		this.setMessaggio(messaggio);
		this.setCodice(codice);
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}
	public Integer getCodice() {
		return codice;
	}
	public void setCodice(Integer codice) {
		this.codice = codice;
	}

}

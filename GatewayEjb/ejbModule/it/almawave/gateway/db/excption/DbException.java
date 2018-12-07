package it.almawave.gateway.db.excption;

public class DbException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String messaggio;
	public DbException(String messaggio) {
		this.setMessaggio(messaggio);
	}
	public DbException(String messaggio, Exception e) {
		this.setMessaggio(messaggio);
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

}

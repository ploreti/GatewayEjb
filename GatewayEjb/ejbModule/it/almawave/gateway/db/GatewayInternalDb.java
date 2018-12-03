package it.almawave.gateway.db;

import java.util.Date;


import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;

import it.almawave.gateway.internal.Request;
import it.almawave.gateway.internal.RequestStatus;

/**
 * Session Bean implementation class GatewayInternalDb
 */
@Stateless
@LocalBean
public class GatewayInternalDb implements GatewayInternalDbRemote, GatewayInternalDbLocal {

	@PersistenceContext(unitName = "GatewayJpa")
	EntityManager em;

	//	@Resource
	//	private SessionContext sessionContext;
	/**
	 * Default constructor. 
	 */
	public GatewayInternalDb() {

	}

	public void insertRequest() {
		Request request=new Request();
		request.setID(2);
		request.setEXT_ID("aaaa111");
		request.setNODE_ID(1);
		request.setFILE_URI("metto file uri");
		request.setTIPO_VISITA("metto tipo visita");
		request.setDTP("metto DTP");
		request.setSPECIALIZZAZIONE("METTO SPECIALIZZAZIONE");
		
		request.setSTART_DATE(new Date(System.currentTimeMillis()));
		em.persist(request);
	}

	public void insertRequestStatus() throws IllegalStateException, SecurityException, SystemException {
		RequestStatus requestStatus=new RequestStatus();
		requestStatus.setID(1);
		requestStatus.setEXT_ID("aaaa111");
		requestStatus.setNODE_ID(1);
		requestStatus.setSTATUS(100);
		requestStatus.setSYSTEM_ID(1);
		requestStatus.setINSERT_DATE(new Date(System.currentTimeMillis()));
		em.persist(requestStatus);
	}
}

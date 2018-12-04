package it.almawave.gateway.db;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;

import it.almawave.gateway.db.bean.DoRequestBean;
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
	
	/**
	 * il servizio avvia il processo di trascrizione/classificazione. 
	 * @param request
	 * @return Identificativo univoco della richiesta.
	 */
	@TransactionAttribute(value=TransactionAttributeType.REQUIRES_NEW)
	public String doRequest(DoRequestBean request, String id) {
		
		try {
			
			Request _request = new Request();
			_request.setEXT_ID(request.getIdDifformita());
			_request.setNODE_ID(1);
			_request.setFILE_URI(request.getPercorsoFileAudio());
			_request.setTIPO_VISITA(request.getTipoVisita());
			_request.setDTP(request.getDtp());
			_request.setSPECIALIZZAZIONE(request.getSpecializzazione());
			
			RequestStatus _requestStatus = new RequestStatus();
			
			_requestStatus.setEXT_ID(request.getIdDifformita());
			_requestStatus.setINSERT_DATE(new Date());
			_requestStatus.setSTATUS(1);
			_requestStatus.setSYSTEM_ID(1);
						
			em.persist(_request);
			em.persist(_requestStatus);
			
			
		}catch (Exception e) {
			
		}finally {
			
		}
		
		
		return null;
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

//	public void insertRequestStatus() throws IllegalStateException, SecurityException, SystemException {
//		RequestStatus requestStatus=new RequestStatus();
//		requestStatus.setID(1);
//		requestStatus.setEXT_ID("aaaa111");
//		requestStatus.setNODE_ID(1);
//		requestStatus.setSTATUS(100);
//		requestStatus.setSYSTEM_ID(1);
//		requestStatus.setINSERT_DATE(new Date(System.currentTimeMillis()));
//		em.persist(requestStatus);
//	}
}

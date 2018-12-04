package it.almawave.gateway.db;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
	 * Il servizio avvia il processo di trascrizione/classificazione. 
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
			
			return request.getIdDifformita();
			
			
		}catch (Exception e) {
			return "Nessuna richiesta è stata inserita";
		}finally {
			
		}
		
	}
	
	/**
	 * Il servizio verifica lo stato della richiesta di trascrizione/classificazione. 
	 * @param id
	 * @return Stato della richiesta
	 */
	public String getStatus(String id) {
		
		try {
			
			Query query = em.createNamedQuery("RequestStatus.findStatusByExtId");
			query.setParameter("extID", id);
			List results = query.getResultList();
			
			if (!results.isEmpty())
				return "Nessuna richiesta è stata trovata";
			
			return ((Integer)results.get(0)).toString();
			
			
		}catch (Exception e) {
			return "Errore nel recupero dello sto della richiesta";
		}finally {
			
		}
		
	}
	


}

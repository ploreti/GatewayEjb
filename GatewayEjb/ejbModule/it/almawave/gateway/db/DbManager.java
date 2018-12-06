package it.almawave.gateway.db;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.almawave.gateway.db.bean.DoRequestBean;
import it.almawave.gateway.internal.Request;
import it.almawave.gateway.internal.RequestStatus;

/**
 * Session Bean implementation class DbManager
 */
@Stateless
@LocalBean
public class DbManager {

	@PersistenceContext(unitName = "GatewayJpa")
	EntityManager em;
    /**
     * Default constructor. 
     */
    public DbManager() {
        
    }
    
    public void inserisciRequest(DoRequestBean request, String id) {
    	
    	
    	Request _request = new Request();
		_request.setEXT_ID(request.getIdDifformita());
		_request.setNODE_ID(1);
		_request.setFILE_URI(request.getPercorsoFileAudio());
		_request.setTIPO_VISITA(request.getTipoVisita());
		_request.setDTP(request.getDtp());
		_request.setSPECIALIZZAZIONE(request.getSpecializzazione());
		_request.setAUDIOMA_ID(Long.valueOf(id));

		RequestStatus _requestStatus = new RequestStatus();

		_requestStatus.setEXT_ID(request.getIdDifformita());
		_requestStatus.setINSERT_DATE(new Date());
		_requestStatus.setSTATUS(1);
		_requestStatus.setSYSTEM_ID(1);

		em.persist(_request);
		em.persist(_requestStatus);
    	
    }
    
    
    public void modificaStato(String idDifformita, int stato) {
    	
    	RequestStatus _requestStatus = new RequestStatus();

		_requestStatus.setEXT_ID(idDifformita);
		_requestStatus.setINSERT_DATE(new Date());
		_requestStatus.setSTATUS(stato);
		_requestStatus.setSYSTEM_ID(1);
		
		em.persist(_requestStatus);
    	
    }
    

    
    
    public String leggiStato(String idDifformit�) {
    	Query query = em.createNamedQuery("RequestStatus.findStatusByExtId");
		query.setParameter("extID", idDifformit�);
		List results = query.getResultList();

		if (!results.isEmpty())
			return "Nessuna richiesta � stata trovata";

		return ((Integer)results.get(0)).toString();
    }

}

package it.almawave.gateway.db;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.almawave.gateway.bean.GatewayResponse;
import it.almawave.gateway.bean.Tuple;
import it.almawave.gateway.db.bean.DoRequestBean;
import it.almawave.gateway.db.excption.DbException;
import it.almawave.gateway.internal.Parameters;
import it.almawave.gateway.internal.Request;
import it.almawave.gateway.internal.RequestStatus;
import it.almawave.gateway.internal.Response;
import it.almawave.gateway.internal.Status;

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
    
    public void inserisciRequest(DoRequestBean request, String id) throws DbException {
    	try {
    		
    		Properties p = System.getProperties();
			String nodo = p.getProperty("jboss.server.name");
    	
	    	Request _request = new Request();
			_request.setEXT_ID(request.getIdDifformita());
			_request.setNODE_ID(nodo);
			_request.setFILE_URI(request.getPercorsoFileAudio());
			_request.setTIPO_VISITA(request.getTipoVisita());
			_request.setDTP(request.getDtp());
			_request.setSPECIALIZZAZIONE(request.getSpecializzazione());
			_request.setAUDIOMA_ID(Long.valueOf(id));
			_request.setSTART_DATE(new Date());
	
			RequestStatus _requestStatus = new RequestStatus();
	
			_requestStatus.setEXT_ID(request.getIdDifformita());
			_requestStatus.setINSERT_DATE(new Date());
			Status stato = em.find(Status.class, 100);
			_requestStatus.setSTATUS(stato);
	
			em.persist(_request);
			em.persist(_requestStatus);
		
    	} catch (Exception e) {
 			e.printStackTrace();
 			throw new DbException("ERRORE nel salvattaggio della richiesta ", 133);
 		}
    	
    }
    
    
    public void modificaStato(String idDifformita, int idstato) throws DbException {
    	try {
    	
	    	RequestStatus _requestStatus = new RequestStatus();
	
			_requestStatus.setEXT_ID(idDifformita);
			_requestStatus.setINSERT_DATE(new Date());
			Status stato = em.find(Status.class, idstato);
			_requestStatus.setSTATUS(stato);
			
			em.persist(_requestStatus);
    	} catch (Exception e) {
 			e.printStackTrace();
 			throw new DbException("ERRORE nel salvattaggio dello stato della richiesta ", 132);
 		}
    	
    }
    
    public void inserisciFilePVTandTesto(String idDifformita, String testoPvt, String testo) throws DbException {
    	
    	try {
	    	//leggere la Request
	    	Query query = em.createNamedQuery("Request.findByExtId");
	    	query.setParameter("extID", idDifformita);
	    	List<Request> results = query.getResultList();
	
			if (results.isEmpty())
				throw new DbException("Nessuna richiesta è stata trovata", 130);
			
			Request richiesta = results.get(0);
	    	
	    	//converite il pvt e il testo in array di byte
	    	byte[] pvt = testoPvt.getBytes(Charset.forName("UTF-8"));
	    	byte[] txt = testo.getBytes(Charset.forName("UTF-8"));
	    	
	    	richiesta.setTXT(txt);
	    	richiesta.setPVT(pvt);
	    	
	    	em.merge(richiesta);
	    } catch (Exception e) {
			e.printStackTrace();
			throw new DbException("ERRORE nel salvattaggio della testo del CRM ", 130);
		}
    	
    }
    
    public void inserisciResponse(String idDifformita, GatewayResponse gatewayResponse) throws DbException {
    	try {
	    	//leggere la Request
	    	Query query = em.createNamedQuery("Request.findByExtId");
	    	query.setParameter("extID", idDifformita);
	    	List<Request> results = query.getResultList();
	    	
			if (results.isEmpty())
				throw new DbException("Nessuna richiesta è stata trovata", 131);
			
			Request richiesta = results.get(0);
			
			Response risposta = new Response();
			
			risposta.setEXT_ID(idDifformita);
			if (gatewayResponse.getPlainText() != null) {
				byte[] testo = gatewayResponse.getPlainText().getBytes(Charset.forName("UTF-8"));
				risposta.setTESTO(testo);
			}
			if (!gatewayResponse.getTuples().isEmpty()) {
				List<Tuple> tuple = gatewayResponse.getTuples();
				for (int i = 0; i < tuple.size(); i++) {
					switch (i) {
					case 0:
						risposta.setTUPLA1(tuple.get(i).getValue());
						risposta.setRANK_TUPLA1(tuple.get(i).getRank());
						break;
					case 1:
						risposta.setTUPLA2(tuple.get(i).getValue());
						risposta.setRANK_TUPLA2(tuple.get(i).getRank());
						break;
					case 2:
						risposta.setTUPLA2(tuple.get(i).getValue());
						risposta.setRANK_TUPLA2(tuple.get(i).getRank());
						break;
					default:
						break;
					}
				}
			}
			em.persist(risposta);
			richiesta.setCRM_RESPONSE(risposta.getID());
			richiesta.setEND_DATE(new Date());
			em.merge(richiesta);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new DbException("ERRORE nel salvattaggio della response ", 131);
		}
    }
    
    
    public String leggiStato(String idDifformita) throws DbException {
    	try {
	    	Query query = em.createNamedQuery("RequestStatus.findStatusByExtId");
			query.setParameter("extID", idDifformita);
			List<Status> results = query.getResultList();
	
			if (results.isEmpty())
				return "Nessuna richiesta è stata trovata";
	
			return results.get(0).getDESCRIZIONE();
    	}catch (Exception e) {
    		e.printStackTrace();
    		throw new DbException("ERRORE nel recupero dello stato ", 120);
		}
    }
    
    public String leggiResponse(String idDifformita) throws DbException {
    	try {
    		
    		String response = "";
    		//leggere la Request
	    	Query query = em.createNamedQuery("Request.findByExtId");
	    	query.setParameter("extID", idDifformita);
	    	List<Request> results = query.getResultList();
	    	
			if (results.isEmpty())
				return "Nessuna richiesta è stata trovata";
			
			Integer idResponse = results.get(0).getCRM_RESPONSE();
			
			if (idResponse == null || idResponse.intValue()==0)
				return "Nessuna risposta è stata trovata";
			
			Response  risposta = em.find(Response.class, idResponse);
			
			GatewayResponse gr = new GatewayResponse();
			
			if (risposta.getTESTO() != null) {
				String txt = new String(risposta.getTESTO(), "UTF-8");
				gr.setPlainText(txt);
			}
			
			if (risposta.getTUPLA1() != null && !risposta.getTUPLA1().trim().equals("")) {
				List<Tuple> tuples = new ArrayList<Tuple>();
				Tuple tupla = new Tuple();
				tupla.setValue(risposta.getTUPLA1());
				tupla.setRank(risposta.getRANK_TUPLA1());
				tuples.add(tupla);
				//se presenti aggingo anche tupla2 e tupla3
				if (risposta.getTUPLA2() != null && !risposta.getTUPLA2().trim().equals("")) {
					tupla = new Tuple();
					tupla.setValue(risposta.getTUPLA2());
					tupla.setRank(risposta.getRANK_TUPLA2());
					tuples.add(tupla);
				}
				if (risposta.getTUPLA3() != null && !risposta.getTUPLA3().trim().equals("")) {
					tupla = new Tuple();
					tupla.setValue(risposta.getTUPLA3());
					tupla.setRank(risposta.getRANK_TUPLA3());
					tuples.add(tupla);
				}
				gr.setTuples(tuples);
			}
			
			if (risposta.getURGENTE()!= null && !risposta.getURGENTE().trim().equals(""))
				gr.setIsUrgent(Boolean.valueOf(risposta.getURGENTE()));
			
			if (risposta.getKM()!= null && !risposta.getKM().trim().equals(""))
				gr.setKm(Integer.valueOf(risposta.getKM()));
			
			ObjectMapper om=new ObjectMapper();
			response = om.writeValueAsString(gr);
			
			return response;
    	}catch (Exception e) {
    		e.printStackTrace();
    		throw new DbException("ERRORE nel recupero della rispsota ", 120);
		}
    }
    
    public List<Parameters> getElencoParametriSistema() throws DbException {
    	try {
    	
	        Query query = em.createQuery("SELECT p FROM Parameters p");
	        List<Parameters> lista = query.getResultList();
	        return lista;
    	}catch (Exception e) {
    		e.printStackTrace();
    		throw new DbException("ERRORE nel recupero dei paramtri ", 999);
		}
    	
    }

}

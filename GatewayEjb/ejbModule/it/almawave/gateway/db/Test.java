package it.almawave.gateway.db;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class Test
 */
@Stateless
@LocalBean
public class Test implements TestLocal {

    /**
     * Default constructor. 
     */
    public Test() {
        // TODO Auto-generated constructor stub
    }
    
    public void callEJB() {
    	System.out.println("------------------richiamato");
    }

}

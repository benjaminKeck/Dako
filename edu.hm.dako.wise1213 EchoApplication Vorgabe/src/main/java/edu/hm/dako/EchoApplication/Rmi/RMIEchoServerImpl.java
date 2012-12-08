package edu.hm.dako.EchoApplication.Rmi;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;
	
/**
 * Klasse RMIEchoServerImpl
 * @author Mandl
 */
 public class RMIEchoServerImpl extends UnicastRemoteObject implements RMIEchoServerInterface 
{	 
	private static final long serialVersionUID = 99L;
	private static Log log = LogFactory.getLog(RMIEchoServerImpl.class);
		
	// Verbindungstabelle: Hier werden alle aktiven Verbindungen zu Clients verwaltet
	private static Map<String, String> connections = new ConcurrentHashMap<String, String>();
	 
	/**
	 * Echo-Methode
	 */
	public RMIEchoServerImpl() throws RemoteException 
	{	
		super();
	}

	/**
	 * Echo-Methode
	 */
	public EchoPDU echo(EchoPDU message) throws RemoteException 
	{	
   	    long startTime = System.nanoTime();
   	    
   	    //Verbingspartner in Map eintragen
   	    connections.put(message.getClientName(), "1");
   	    
   	    EchoPDU sendPdu = message;	    	
			
   	    sendPdu.setMessage(message.getMessage()+"_vom Server zurueck");
   	    sendPdu.setServerThreadName(message.getServerThreadName());
   	    
   	    sendPdu.setServerTime(System.nanoTime() - startTime);
   	    
   	    //Wenn es die letzte Nachricht ist
   	    if(sendPdu.getLastRequest()){
   	    	System.out.println("Letzte Nachricht zurueck an "+message.getClientName());
   	    	
   	    	//Eintrag wird aus Map entfernt
   	    	connections.remove(message.getClientName());
   	    	
   	    	//Wenn alle Verbindungen beendet sind
   	    	if(connections.isEmpty()){
   	    		System.out.println("Server hier beenden");
   	    		//super.unexportObject(this, true);
   	    	}
   	    	
   	    }
		//TODO
	   	 return sendPdu;  
	}
	
	
}
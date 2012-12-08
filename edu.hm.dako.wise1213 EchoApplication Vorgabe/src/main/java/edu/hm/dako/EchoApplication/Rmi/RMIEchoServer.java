package edu.hm.dako.EchoApplication.Rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;

/**
 * Klasse RMIEchoServer
 *  
 * @author Mandl
 *
 */

	/**
	 * Klasse RMIEchoServer: Startet das RMI-Registry lokal und den RMI-Echo-Server.
	 * 
	 * @author Mandl
	 * 
	 * TODO: -Djava.rmi.server.codebase= ueberarbeiten!
	 */
public class RMIEchoServer
{
	private static Log log = LogFactory.getLog(RMIEchoServerImpl.class);
	/**
	 * Start des Echo-Servers
	 * @param args
	 * @throws  
	 */   	
	public static void main (String args[]) 
   	{
		PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);
		Registry rmiRegistry = null;
		
		
   	    // RMI Registry lokal starten
   	    try {
   	    	rmiRegistry = java.rmi.registry.LocateRegistry.createRegistry(1099);
   	    	System.out.println("RMI registry bereit und wartet");
   	    	log.debug("RMI registry bereit");
   	    } catch (Exception e) {
   	    	e.printStackTrace();
   	    	//System.out.println("Exception beim Starten der RMI registry:");
   	    	log.error("Exception beim Starten der RMI registry: " + e);
   	    	System.exit(1);
   	    }


   	
   	    //TODO
   	 try {
			
   		 	RMIEchoServerImpl obj = new RMIEchoServerImpl();
			rmiRegistry.rebind("Server",obj);
//	    	RemoteServer.setLog(System.out);
	    	
	  
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	}
}
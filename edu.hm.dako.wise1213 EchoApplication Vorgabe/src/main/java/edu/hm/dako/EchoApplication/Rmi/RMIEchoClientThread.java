package edu.hm.dako.EchoApplication.Rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.EchoApplication.Basics.AbstractClientThread;
import edu.hm.dako.EchoApplication.Basics.EchoPDU;
import edu.hm.dako.EchoApplication.Basics.SharedClientStatistics;

/**
 * Klasse RMIEchoClientThread
 *  
 * @author Benjamin Keckes
 *
 */
public class RMIEchoClientThread extends AbstractClientThread
{	 
		private static Log log = LogFactory.getLog(RMIEchoClientThread.class);
				
	    /** Name des Threads */
	    private String threadName;
	    
	    /** Nummer des Echo-Clients */
	    private int numberOfClient;
	    
	    /** Laenge einer Nachricht */
	    private int messageLength;
	    
	    /** Anzahl zu sendender Nachrichten je Thread */
	    private int numberOfMessages;
	    
	    /** Adresse des Servers (String) */
	    private String remoteServerAddress;
	    
	    /** Well-known Port der RMI Registry, derzeit nicht genutzt, 
	     * da Standard verwendet wird
	     * 
	     */
		private static int RMIRegistryPort = 1099;
	    
	    /** RMI-Referenz auf den Server */
	    private RMIEchoServerInterface echoServer;
	    
	    /** Denkzeit des Clients zwischen zwei Requests in ms */
	    private int clientThinkTime;
	    	     
	    /** Gemeinsame Daten der Threads */
	    private SharedClientStatistics sharedData;
	  
	    /** Zeitstempel fŸr RTT-Berechnung */
		private long rttStartTime;
		private long rtt;
		
	    /**
	     * initialize
	     * 
	     * @param serverPort: Port des Servers
	     * @param remoteServerAddress: Adresse des Servers
	     * @param numberOfClient: Laufende Nummer des Test-Clients
	     * @param messagelength: Laenge einer Nachricht
	     * @param numberOfMessages: Anzahl zu sendender Nachrichten je Thread
	     * @param clientThinkTime: Denkzeit des Test-Clients
	     * @param sharedData: Gemeinsame Daten der Threads 
	     * @throws MalformedURLException 
	     */
		
		@Override
		public void initialize(
				int serverPort,
				String remoteServerAddress,
				int numberOfClient, 
				int messageLength, 
				int numberOfMessages,
				int clientThinkTime,
				SharedClientStatistics sharedData)
			{		
				this.RMIRegistryPort = serverPort;
			    this.remoteServerAddress = remoteServerAddress;
			    this.numberOfClient = numberOfClient;
			    this.messageLength = messageLength;
			    this.numberOfMessages = numberOfMessages;
			    this.clientThinkTime = clientThinkTime;
			    this.sharedData = sharedData;
				
			    this.setName("RMIEchoClient-".concat(String.valueOf(numberOfClient+1)));    
				threadName = getName();
				
				
				try {
					/*Registry holen */
					Registry rmiRegistry = LocateRegistry.getRegistry(remoteServerAddress, serverPort);
					
					/*Registry erkennen */
					echoServer = (RMIEchoServerInterface)rmiRegistry.lookup("Server");
					System.out.println(this.getName()+": Verbindung mit echoServer");
				}  catch (RemoteException e) {
					
					e.printStackTrace();
				} catch (NotBoundException e) {
					
					e.printStackTrace();
				}
			}
	
		@Override
		public void run() 
		{   
			/*zähler inkrementieren */
			sharedData.incrNumberOfLoggedInClients();
			
	        /**
	         * Synchronisation mit allen anderen Client-Threads:
	         * Warten, bis alle Clients angemeldet sind und dann
	         * erst mit der Lasterzeugung beginnen
	         * 
	         */
	        while (!sharedData.allClientsLoggedIn())
	       	{
	        	try {     
			        Thread.sleep(100);
	        	}
		        catch (InterruptedException e) {
		        	log.error("Sleep unterbrochen");
		        }
	        }
	        
	        /*Nachrichten hintereinander senden */
	        for(int i=0; i<numberOfMessages; i++){
	        	/* RTT-Startzeit ermitteln */
	        	rttStartTime = System.nanoTime();
	        	
	        	/*
				 * Neues EchoPDU erzeugen
				 * EchoPDU ClientName setzen
				 * EchoPDU Nachricht setzen
				 *  
				 */
	        	EchoPDU send = new EchoPDU();
				send.setClientName(this.getName());
				send.setMessage(send.getMessageText(this.messageLength)+(i+1));
				
				/* Letzter Request? */
				if(i>=numberOfMessages-1){
					send.setLastRequest(true);
				}
				
				try {
					/*senden echoPDU->Rückgabewert ist Antwort des Servers */
					EchoPDU reply = echoServer.echo(send);
					
					/* RTT berechnen */
					rtt = System.nanoTime() - rttStartTime;
					
					/* Response-Zaehler erhoehen */
					sharedData.incrSentMsgCounter(numberOfClient);
					sharedData.incrReceivedMsgCounter(numberOfClient, rtt, reply.getServerTime());
					
					System.out.println(this.getName()+ ": Nachricht '"+reply.getMessage()+"' vom Server empfangen");
			
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				/*Wartezeit bevor die nächste Nachricht geschickt wird */
				try {
					Thread.sleep(clientThinkTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
	        }
	        
	        /*Ausgabe bei Threadende */
	        System.out.println(this.getName()+": ist fertig");
	        
						
		}	
 }
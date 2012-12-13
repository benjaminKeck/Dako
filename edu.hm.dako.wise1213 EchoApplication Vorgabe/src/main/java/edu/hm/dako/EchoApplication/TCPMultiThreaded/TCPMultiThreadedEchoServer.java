package edu.hm.dako.EchoApplication.TCPMultiThreaded;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;

/**
 * Klasse TCPMultiThreadedEchoServer
 *  
 * @author Benjamin Keckes
 *
 */
public class TCPMultiThreadedEchoServer extends Thread
{	 
		private static Log log = LogFactory.getLog(TCPMultiThreadedEchoServer.class);

		private static int serverPort = 50000;
		
		private static int numberOfWorkerThread = 0;
		
		/** Verbindungstabelle: Hier werden alle aktiven Verbindungen zu Clients verwaltet */
	    private static Map<String, Socket> connections = new ConcurrentHashMap<String, Socket>();
	 
	    /** TCP-Socket des Servers (Listen-Socket) */
	    private static ServerSocket serverSocket;
	    
		/** Transportverbindung und Streams fuer einen Client */
	    private Socket con;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		
		/** Groesse des Empfangspuffers einer TCP-Verbindung in Byte */
		private static final int receiveBufferSize = 300000;
	
	    /**
	     * Konstruktor
	     */
		public TCPMultiThreadedEchoServer(Socket incoming)
		{		
			 this.con = incoming;

			 /* Ein- und Ausgabe-Objektstrom erzeugen */
			 try {
		        out = new ObjectOutputStream(incoming.getOutputStream());
		        in = new ObjectInputStream(incoming.getInputStream());
		        System.out.println("Verbindung angelegt: "+incoming.getPort()); 
		      }  
		      catch (Exception e) { 
		    	  e.printStackTrace();
		      }
		}

   	    public static void main (String args[])
   	    {
   	    	PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);
   	    	
   	    	try {
   	    		serverSocket = new ServerSocket(serverPort);
   	    		System.out.println("TCPMultiThreadedEchoServer wartet auf Clients...");
   	    	} catch (IOException e) { 
   	    	   log.debug("Exception bei der Socket-Erzeugung: " + e);
	           System.exit(9);
   	    	} 
   	    	
   	    	
   	    	while (true) {
   	    		/* Auf ankommende Verbindungsaufbauwuensche warten und diese in eigenen Threads bearbeiten */
   	    		
   				try {
   					/** Eine Socketanfrage entgegen nehmen */
   					Socket socket1 = serverSocket.accept();
   					
   					/**neuen Serverthread erstellen */
					TCPMultiThreadedEchoServer thread = new TCPMultiThreadedEchoServer(socket1);
					
					/**connection in Liste eintragen */
					connections.put(thread.getName(), socket1);
					
					numberOfWorkerThread++;
					
					/**Serverthread starten */
					thread.start(); 					
					
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println("Serverprozess kann keine Verbindungen mehr akzeptieren -> Beendet sich");
					break;
				}
   				
   				
   	    	 }
   	    }  
   	    
   	    /**
   	     * Die Threadmethode
   	     */
   	    public void run()
   	    {	
   	    	boolean finished = false;
   	    	EchoPDU receivedPdu = new EchoPDU();
   	    	long startTime;
  
   	    	System.out.println(this.getName() + ": Verbindung mit neuem Client aufgebaut, Remote-TCP-Port " + con.getPort());
   	    	 	    	
   	    	try {
   	    		log.debug("Standardgroesse des Empfangspuffers der Verbindung: " + con.getReceiveBufferSize() + " Byte");
   	    		con.setReceiveBufferSize(receiveBufferSize);
   	    		log.debug("Eingestellte Groesse des Empfangspuffers der Verbindung: " + con.getReceiveBufferSize() + " Byte");
   	    		
   	    	} catch (SocketException e){
   	    		log.debug("Socketfehler: " + e);
   	    	}
   	    	
   	    	while (!finished) {
   	    		
   	    		try {	
   	    			/* Echo-Request entgegennehmen */
   	    			receivedPdu = (EchoPDU) in.readObject();
   	    			startTime = System.nanoTime();
   	    			log.debug("Request empfangen von " + receivedPdu.getClientName() + ": " + receivedPdu.getMessage());
   	    		} 
   	   	    	catch (IOException e) { 
   	   	    		log.debug("Empfangen einer Nachricht nicht moeglich: " + e);
   	   	    		finished = true;
   	   	    		continue;
   	    		}
   	   	    	catch (ClassNotFoundException e) {
   	   	    		log.debug("Unbekannte Objektklasse empfangen: " + e);
  	    			finished = true;
  	    			continue;
				}            
   	    		try {
   	    			/*
   	    			* Neues EchoPDU erzeugen
   	    			* EchoPDU ServerThreadName setzen
   	    			* EchoPDU ClientName setzen
   	    			* EchoPDU Nachricht setzen
   	    			* EchoPDU ServerZeit setzen
   	    			*  
   	    			*/
   	    			EchoPDU sendPdu = new EchoPDU();
   	    			log.debug("Serverzeit: " + (System.nanoTime() - startTime) + " ns"); 
   	    			sendPdu.setServerThreadName(this.getName()); 
   	    		    sendPdu.setClientName(receivedPdu.getClientName());
   	    		    sendPdu.setMessage(receivedPdu.getMessage()+"_vomServerZurueck");
   	    			sendPdu.setServerTime(System.nanoTime() - startTime); 
   	    			
   	    			/* EchoPDU an den Client zurück senden */
   	    			out.writeObject(sendPdu);
   	    			out.flush();
   	    			log.debug("Response gesendet");   	    			
   	    		} 
   	    		catch (IOException e) {
   	    			log.error("Senden einer Nachricht nicht moeglich: " + e);
   	    			finished = true;
   	    		}            
   	    		
   	    		/* 
   				 * Wenn die letzte Nachricht eingetroffen ist, dann beende die Schleife
   				 */
   	    		if (receivedPdu.getLastRequest()) {
	    			System.out.println("Letzter Request des Clients " + receivedPdu.getClientName());
	    			finished = true;
   	    		}
   	    	}
   	    	
   	    	/* Kurz warten */
   	    	try {
    			Thread.sleep(1000);
    		}
    		catch (InterruptedException e3){}
    		  	
    		System.out.println(this.getName() + ": Verbindung mit Client abbauen, Remote-TCP-Port " + con.getPort());
    		
    		/* Verbindung abbauen */
   	    	try {
   	    		out.flush();
   	    		con.close();
   	    	}
   	    	catch (IOException e) {
   	    		System.out.println("Exception bei close: " + e);
   	    	}   	

	    	log.debug(this.getName() + " beendet sich");  
	    	System.out.println(this.getName()+" beendet sich");
	    	
	    	/*Verbindung wird aus der Liste gelöscht */
	    	connections.remove(this.getName());
	    	
	    	/*Wenn nun in Der Verbindungsliste kein Eintrag mehr vorhanden ist
	    	 * bedeutet das das dies der letzte Thread ist und nun keiner mehr kommt.
	    	 * 
	    	 */
			if(connections.isEmpty()){
				System.out.println("alle ServerThreads fertig");
//				try {
//					//Das serverSocket wird beendet.
//					//Da in der Main-Methode mit dem Accept im Moment aber auf eine Verbindung gewartet wird
//					//wird eine Exception in der Main geworfen
//					serverSocket.close();
//				} 
//				catch (IOException e) {
//					e.printStackTrace();
//				}
			}
			
			/* Der Thread wird beendet */
	    	this.stop();
	    	
	 	} 	    
 }
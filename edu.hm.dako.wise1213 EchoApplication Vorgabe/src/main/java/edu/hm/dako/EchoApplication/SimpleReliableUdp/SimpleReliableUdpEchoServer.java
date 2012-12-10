package edu.hm.dako.EchoApplication.SimpleReliableUdp;


import java.io.IOException;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.EchoApplication.UDPSingleThreaded.UdpSocket;

/**
 * Klasse SimpleReliableUdpEchoServer
 * 
 * 
 * @author Teich
 *
 */
public class SimpleReliableUdpEchoServer extends Thread
{	 
		/**
		 * Logdatei
		 */
		private static Log log = LogFactory.getLog(SimpleReliableUdpEchoServer.class);

		/**
		 * Lokaler Serverport
		 */
		private static int serverPort = 50000;
		
		/**
		 *  Verbindungstabelle: Hier werden alle aktiven Verbindungen zu Clients verwaltet
		 */
	    private static Map<String, String> connections = new ConcurrentHashMap<String, String>();
	 
	    /**
	     *  Datagram-Socket des Servers (Listen-Socket)
	     */
	    private static UdpSocket serverSocket;    
	    
	    /**
	     * Variable für Empfangenes PDU
	     */
	    private UdpRemoteObject pdu = null;
	    
	    /**
	     * Festlegung, wie viele Sendewiederholungen es geben soll
	     */
	    private static int resendAttempts = 3;	    
	    
	    /**
	     * Eintrag, welchen Client der Server-Thread bedient
	     */
	    private String clientServed;
	    
	    /**
	     * Konstruktor für Serverklasse
	     */
		public SimpleReliableUdpEchoServer(UdpRemoteObject receivedPdu, String servedClient)
		{			
			pdu = receivedPdu;
			clientServed = servedClient;
		}

		/**
		 * Main-Methode des Echo-Servers
		 * @param args
		 */
   	    public static void main (String args[])
   	    {
   	    	// Log initialisieren
   	    	PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);
   	    	
   	    	// Damit Variable initialisiert ist:
   	    	serverSocket = null;

        	// UDP-Serversocket zum Empfang der Erstpakete mit ID 0 an Port 50000 registrieren.
   	    	try {

   				// UdpSocket ist gekapseltes Datagram-Socket!
   	    		serverSocket = new UdpSocket(serverPort, 200000, 300000);
   	    		log.debug("UDP-Serversocket an Port " + serverPort + " erfolgreich erzeugt.");
   				System.out.println("UdpMultiThreadedEchoServer wartet auf Clients...");
   			} catch (IOException e) {
   				log.error("Exception bei der Socket-Erzeugung: " + e);
   				System.exit(9);
   			}
   	    	
   	    	// Variablen für Threaderzeugungsroutine
   	    	boolean finished = false;
   	    	ExtEchoPDU receivedPdu = null;
   	    	int threadNumber = 0;
   	    	
   	    	// Solange finished noch nicht auf true gesetzt wurde...
   	    	while (!finished) {

   	    		Object receivedObject = null;
   	    		
				try {
					// Ankommendes Objekt empfangen
					receivedObject = serverSocket.receive(5000);
					
					// Startzeit setzen
					long startTime = System.nanoTime();
					
					// Empfangenes Objekt in UDP-Remoteobjekt umwandeln
					UdpRemoteObject receivedUdpRemoteObject = new UdpRemoteObject(serverSocket.getRemoteAddress(), serverSocket.getRemotePort(), startTime, receivedObject);
					
					// Das empfangene Objekt ist eigentlich nur ein EchoPDU
					receivedPdu = (ExtEchoPDU) receivedObject;
					
					// Wenn ein empfangenes PDU nicht das erste einer Sendereihe ist, wird es verworfen.
					if (receivedPdu.getPduID() != 0)
					{
						continue;
					}
					
					// Neuen Server-Thread erzeugen
					SimpleReliableUdpEchoServer newServerThread = new SimpleReliableUdpEchoServer(receivedUdpRemoteObject, receivedPdu.getClientName());
	   	    		
	   	    		// Threadnummer erhöhen
	   	    		threadNumber++;
	   	    		
	   	    		// Neuem Thread einen Namen geben 
	   	    		newServerThread.setName("Workerthread " + threadNumber);
					
	   	    		// Verbindung in Liste eintragen (eindeutiger Schluessel ist der Server-Threadname)
	   	    		connections.put(newServerThread.getName(), receivedPdu.getClientName());
	   	    		log.debug("Aktuell angemeldete Clients: " + connections.size());
	   	    		// System.out.println(connections.size());
					
	   	    		// Thread starten
	   	    		newServerThread.start();
					
				} catch (IOException e) {
					/* 
					 * Falls beim receive ein Timeout auftritt und keine Verbindungen mehr vorhanden sind,
					 * wird die Schleife beendet.
					 */
					if (connections.size() == 0){
					finished = true;
					
					System.out.println("Timeout / keine Verbindungen (mehr) vorhanden!");
					System.out.println("Insgesamt seit Serverstart erzeugte Threads: " + threadNumber);
					log.debug("Timeout / Keine Verbindungen mehr vorhanden!");
					log.debug("Insgesamt seit Serverstart erzeugte Threads: " + threadNumber);
					
					}
					
				}
   	    		
	   	   	}
	    
	   	   	// Listener-Socket schliessen
	   	   	serverSocket.close();
	    	log.debug("UDPEchoServer Listener beendet sich");    
	      	System.out.println("UDPMultiThreadedEchoServer Listener beendet sich");    
   	    }
   	    
   	    /**
   	     * Worker-Thread-Methode fuer die Bearbeitung eines Requests
   	     */   	    
   	    public void run()
   	    {	
   	    	// lokaler Port des Servers (Null: nächster freier Port)
   	    	int localPort = 0;
   	    	
   	    	// Variable für neues Socket
			UdpSocket newSocket = null;
			   	    	
   	    	// neuen Socket an freiem Port erzeugen
			try {
				 newSocket = new UdpSocket(localPort, 20000, 30000);
				 System.out.println("Neuer Port: " + newSocket.getLocalPort());
				 log.debug("Thread " + this.getName() + " hat einen neuen Socket an Port " + newSocket.getLocalPort() + "erzeugt.");
			} catch (SocketException e1) {
				log.error("Thread " + this.getName() + " konnte keinen neuen Socket erzeugen.");
			}
   	    	
   	    	
			// Flag für Fortsetzung oder Abbruch der While-Schleife
   	    	Boolean contin = true;
   	    	
   	    	// Nummer des erwarteten nächsten PDUs
   	    	int expectedPduId = 0;
   	    	
   	    	// Zähler für Sendewiederholungen
   	    	int resendCounter = 0;
   	    	
   	    	// Variable für zu empfangende ExtEchoPDU
   	    	ExtEchoPDU receivedPdu = null;
   	    	
   	    
   	    // Solange contin noch nicht auf FALSE gesetzt wurde...
   	    while(contin){
			
			// EchoPdu aus dem UdpRemoteObject wieder herauskopieren
   	    	receivedPdu = (ExtEchoPDU) pdu.getObject();

   	    	System.out.println(this.getName() + ": WorkerThread uebernimmt Request Nr. " + receivedPdu.getPduID() + " von " + receivedPdu.getClientName());

   	    	// 	Echo-Response aufbauen
			ExtEchoPDU sendPdu = new ExtEchoPDU();
			
			sendPdu.setClientName(receivedPdu.getClientName());
    		sendPdu.setMessage(receivedPdu.getMessage());
			sendPdu.setServerThreadName(this.getName());
			sendPdu.setServerTime(System.nanoTime() - pdu.getStartTime());
			
			// Antwort-PDU mit PDU-ID aus empfangenen Paket befüllen. 
			sendPdu.setPduId(receivedPdu.getPduID());
   	    	
   	    	
			try 
			{
				// Antwort-PDU über neuen Socket versenden
				newSocket.send(pdu.getRemoteAddress(), pdu.getRemotePort(), sendPdu);
				
				log.debug("Response PDU gesendet an Adresse" + pdu.getRemoteAddress() + " Port: " + pdu.getRemotePort());
				
				// Da die Antowrt-PDU nun gesendet wurde, sollte als nächstes eine PDU mit deren ID + 1 eintreffen.
				expectedPduId = (sendPdu.getPduID() + 1); //expectedPduId++;
				
				System.out.println(this.getName() + ": Neue erwartete PDU: " + expectedPduId);
			} 
			
			// Fehlerbehandlung
			catch (IOException e) {
				log.error("Fehler beim Versenden der Antwort");
				
				// Bei Fehler neu versenden, solange Sendeversuche möglich sind.
				if (resendCounter < resendAttempts)
				{
					resendCounter++;
					continue;
				}
				
				// Ansonsten abbrechen.
				else
				{
					break;
				}
			}
			
			log.debug(this.getName() + ": Response PDU Nr. " + sendPdu.getPduID() + " gesendet!");
			System.out.println(this.getName() + ": Response PDU Nr. " + sendPdu.getPduID() + " gesendet!");
   	    	
			// Variable für zu empfangendes Antwortobjekt
			Object receivedObject = null;
			
			// Variable für zu empfangende Antwort-ExtEchoPDU
			ExtEchoPDU newReceivedPdu = null;
			
			// Ankommendes Objekt empfangen
			try {
							
				receivedObject = newSocket.receive(5000);
				
				// Obekt in ExtEchoPDU umwandeln 
				newReceivedPdu = (ExtEchoPDU) receivedObject;
				
				System.out.println(this.getName() + ": PDU mit ID " + newReceivedPdu.getPduID() + " empfangen.");
				
				/* 
				 * Prüfen, ob PDU vom richtigen Client kommt.
				 * Falls nicht, keine weitere Bearbeitung
				 */
				if (newReceivedPdu.getClientName().equals(clientServed))
				{
					// Wenn die erwartete PDU-Nummer angekommen ist, wird normal weiterverfahren.
					if (newReceivedPdu.getPduID() == expectedPduId)
					{
						// Wiederholungscounter zurücksetzen, falls erforderlich.
						resendCounter = 0;
						
						// Startzeit setzen
						long startTime = System.nanoTime();
				
						// Empfangenes Objekt in UDP-Remoteobjekt umwandeln
						UdpRemoteObject receivedUdpRemoteObject = new UdpRemoteObject(newSocket.getRemoteAddress(), newSocket.getRemotePort(), startTime, receivedObject);
						
						// Empfangenens Objekt in PDU-Attribut des Objekts speichern.
						this.pdu = receivedUdpRemoteObject;
					}
					
					/*
					 *  Falls nochmal die letzte PDU angekommen ist, muss die Antwort nicht angekommen sein.
					 *  Dann wird die Antwort nochmal gesendet.
					 */					
					else if (newReceivedPdu.getPduID() == (expectedPduId-1))
					{
						System.out.println(this.getName() + ": Client hat erneutes Senden von PDU " + newReceivedPdu.getPduID() + " angefordert. Ich sende nochmal.");
						log.info(this.getName() + ": Client hat erneutes Senden angefordert. Ich sende nochmal.");
						
						/*
						 *  Der Expected-Zähler wird in den Zustand zurückversetzt, als wäre die aktuell
						 *  empfangene PDU die erwartete PDU gewesen.
						 */
						expectedPduId = newReceivedPdu.getPduID(); // expectedPduId-1;
						System.out.println("Neue erwartete Pdu: " + expectedPduId);
						
						// Startzeit setzen
						long startTime = System.nanoTime();
				
						// Empfangenes Objekt in UDP-Remoteobjekt umwandeln
						UdpRemoteObject receivedUdpRemoteObject = new UdpRemoteObject(newSocket.getRemoteAddress(), newSocket.getRemotePort(), startTime, receivedObject);
				
						// Empfangenens Objekt in PDU-Attribut des Objekts speichern.
						this.pdu = receivedUdpRemoteObject;
						
						// Schleife neu starten
						continue;
					}
					
					// Falls eine ganz andere PDU-Nummer angekommen ist, wird ein Fehler geworfen.
					else
					{
						System.out.println("Fehler beim Server: PDU mit unerwarteter Nummer empfangen! Erwartet: " + expectedPduId + " bekommen: " + newReceivedPdu.getPduID());
						throw new IOException("1");
					}
				}
			}
			
			// Fehlerbehandlung
			catch (IOException e) {
				
				// Fehlerbehandlung für falsche PDU-Nummer
				if (e.getMessage().equals("1"))
				{
					log.error("Fehler beim Server: PDU mit unerwarteter Nummer empfangen!");
					//System.out.println("Fehler beim Client: PDU mit unerwarteter Nummer empfangen!");
					//continue;
				}
				
				/* 
				 * Falls ein Empfangstimeout aufgetreten ist, aber der Grund dafür ist, dass
				 * die letzte empfangene PDU auch die letzte sein sollte, so wird der Server-Thread beendet.
				 * Er wurde nicht sofort nach Empfang beendet, weil es ja hätte sein können, dass noch einmal
				 * ein Resend-Request kommt.
				 */
				else if (receivedPdu.getLastRequest() == true){
				contin = false;
				
				// Verbindung aus Liste entfernen
				connections.remove(this.getName());
				System.out.println(this.getName() + " hat seine Arbeit beendet und wird geschlossen. Verbleibende Threads: " + connections.size());
				log.debug(this.getName() + " hat seine Arbeit beendet und wird geschlossen. Verbleibende Threads: " + connections.size());
				
				// Socket schließen
				newSocket.close();
				
				}
				
				/* 
				 * Falls ein Empfangstimeout aufgetreten ist, aber die Übertragung noch nicht beendet werden sollte,
				 * ist wahrscheinlich die letzte Antwort nicht angekommen. Diese wird nochmal gesendet, falls
				 * noch Sendeversuche möglich sind.    
				 */
				else
				{
					if (resendCounter < resendAttempts)
					{
						resendCounter++;
						System.out.println(this.getName() + ": Empfangstimeout beim Server. Letzte Meldung wird erneut gesendet. Versuch: " + resendCounter);
						log.debug(this.getName() + ": Empfangstimeout beim Server. Letzte Meldung wird erneut gesendet. Versuch: " + resendCounter);
						continue;
					}
					
					// Falls alle Wiederholungsversuche fehlgeschlagen sind, wird abgebrochen.
					else
					{
						System.out.println(this.getName() + ": Empfangstimeout beim Server. Kein erneutes Senden mehr möglich. Breche ab.");
						log.debug(this.getName() + ": Empfangstimeout beim Server. Kein erneutes Senden mehr möglich. Breche ab.");
						connections.remove(this.getName());
						break;
					}
				}
			}
						
   	    	
   	    	}
	 	}
 } 
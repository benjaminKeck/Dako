package edu.hm.dako.EchoApplication.SimpleReliableUdp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.EchoApplication.Basics.AbstractClientThread;
import edu.hm.dako.EchoApplication.Basics.SharedClientStatistics;

/**
 * Klasse SimpleReliableUdpEchoClientThread
 *  
 * @author Teich
 *
 */
public class SimpleReliableUdpEchoClientThread extends AbstractClientThread
{	 
		/**
		 * Logdatei
		 */
		private static Log log = LogFactory.getLog(SimpleReliableUdpEchoClientThread.class);
	    		
	    /**
	     * Name des Client-Threads
	     */
	    private String threadName;
	    
	    /**
	     * Nummer des Echo-Clients
	     */
	    private int numberOfClient;
	    
	    /**
	     * Länge einer Nachricht
	     */
	    private int messageLength;
	    
	    /**
	     * Anzahl der Nachrichten
	     */
	    private int numberOfMessages;
	  
	    /**
	     * Lokaler Port des Threads
	     */
	    private int localPort;
	    
	    /**
	     * Port des Servers
	     */
	    private int serverPort;
	    
	    /**
	     * Adresse der Servers als String
	     */
	    private String remoteServerAddress;
	    
	    /**
	     *  Inet-Addresse des Servers
	     */
	    @SuppressWarnings("unused")
		private InetAddress remoteInetAddress;
	    
	    /**
	     *  Denkzeit des Clients zwischen zwei Requests im ms
	     */
	    private int clientThinkTime;
	    
	    /**
	     *  Timeout für UDP-Receive
	     */
	    private static final int receivingTimeout = 20000;
	    
	    /**
	     * Gemeinsame Daten der Threads
	     */
	    private SharedClientStatistics sharedData;
	    
	    /**
	     *  Lokales Datagramm-Socket
	     */
	    private UdpSocket con;
	  
	    /**
	     *  Zeitstempel für RTT-Berechnung
	     */
		private long rttStartTime;
		
		/**
		 * RTT-Zeit
		 */
		private long rtt;
		
		/**
		 * Maximale Anzahl der möglichen Sendewiederholungen
		 */
		private int resendAttempts = 3;
		
	    /**
	     * Thread-Initialisierung
	     * 
	     * @param serverPort: Port des Servers
	     * @param remoteServerAddress: Adresse des Servers
	     * @param port: Portnummer des Threads zur Kommunikation mit dem Server
	     * @param numberOfClient: Laufende Nummer des Test-Clients
	     * @param messagelength: Laenge einer Nachricht
	     * @param numberOfMessages: Anzahl zu sendender Nachrichten je Thread
	     * @param clientThinkTime: Denkzeit des Test-Clients
	     * @param sharedData: Gemeinsame Daten der Threads 
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
		    this.serverPort = serverPort;
		    this.remoteServerAddress = remoteServerAddress;
		    
		    try {
				remoteInetAddress = InetAddress.getByName(remoteServerAddress);
			} catch (UnknownHostException e) {
				 log.error("Exception bei Adressebelegung: " + e);
				 System.out.println("Exception bei Adressebelegung: " + e);
			}
			
		    this.numberOfClient = numberOfClient;
		    this.messageLength = messageLength;
		    this.numberOfMessages = numberOfMessages;
		    this.clientThinkTime = clientThinkTime;
		    this.sharedData = sharedData;
			this.setName("EchoClient-".concat(String.valueOf(numberOfClient+1)));    
			threadName = getName();
			
			// UDP-Socket amn nächsten freien Port registrieren 	    	
   	    	try {
   	    		con = new UdpSocket(localPort, 200000, 300000);
   	    		localPort = con.getLocalPort();
   	    		System.out.println(threadName + ": UDP-Port " + localPort + " registriert");  
   	    	} catch (IOException e) { 
   	    	   log.debug("Exception bei der DatagramSocket-Erzeugung: " + e);
   	    	   System.out.println("Exception bei der DatagramSocket-Erzeugung: " + e);
	           System.exit(9);
   	    	}    	
		}

		/**
		 * Run-Methode fuer den Thread: 
		 * Client-Thread sendet alle Requests und wartet auf Antworten
		 */
		public void run() 
		{   
			sharedData.incrNumberOfLoggedInClients();
			  
	        /**
	         * Synchronisation mit allen anderen Client-Threads:
	         * Warten, bis alle Clients angemeldet sind und dann
	         * erst mit der Lasterzeugung beginnen
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
	        
	        /**
	         * Senden von Echo-Nachrichten
	         */
	        
	        // Zähler für gesendete PDUs
	        int pduCounter = 0;
	        
	        // Zähler für Sendewiederholungen
	        int resendCounter = 0;
	        
	        // Schleife für PDU-Versendung
	        while (pduCounter < numberOfMessages) 
	        {

	        	// RTT-Startzeit ermitteln
				rttStartTime = System.nanoTime();
				
				// Datenpaket erzeugen
				ExtEchoPDU outgoingPdu = new ExtEchoPDU();
				outgoingPdu.setClientName(threadName);
			    outgoingPdu.setPduId(pduCounter);
				
				String myMessage = "";
				
				// Message erzeugen
				for(int k=0; k<messageLength; k++)
				{
					myMessage = myMessage + "a";
				}
			
				outgoingPdu.setMessage(myMessage);

				// Letzter Request?
				if (pduCounter == numberOfMessages - 1) 
				{
				outgoingPdu.setLastRequest(true);
				}
				
				// Datenpaket senden
				try {
					con.send(InetAddress.getByName(remoteServerAddress), serverPort, outgoingPdu);
					sharedData.incrSentMsgCounter(numberOfClient);
					//System.out.println(this.getName() + ": PDU Nr. " + outgoingPdu.getPduID() + " gesendet.");
					log.info(this.getName() + ": PDU Nr. " + outgoingPdu.getPduID() + " gesendet.");
					
				// Fehlerbehandlung für Unknown Host
				} catch (UnknownHostException e) {
					log.error("Client: Fehler beim Senden: unbekannter Host!");
					System.out.println("Client: Fehler beim Senden: unbekannter Host!");
					//e.printStackTrace();
					
				// Fehlerbehandlung bei Timeout
				} catch (IOException e) {
					log.error("Client: Fehler beim Senden der EchoPDU!");
					System.out.println("Client: Fehler beim Senden der EchoPDU!");
					
					// Prüfen, ob erneut gesendet werden kann.
					if (resendCounter < 3)
					{
						resendCounter++;
						continue;
					}
					
					// Falls nicht, abbrechen.
					else
					{
						break;
					}
					//e.printStackTrace();
				}
				
				
				
				/*
				 *  PDU wieder empfangen
				 */
				
				// Objet, das empfangen werden soll
				Object myReturnedObject = null;
				
				// Daraus zu erzeugendes ExtEchoPDU
				ExtEchoPDU receivedPdu = null;
				
				// Objekt empfangen und in ExtEchoPDU umwandeln
				try {
					myReturnedObject = con.receive(receivingTimeout);
					receivedPdu = (ExtEchoPDU) myReturnedObject;
					
					// Prüfen, ob die PDU, die zurückgekommen ist, von diesem Thread erzeugt wurde.
					if (receivedPdu.getClientName().equals(threadName))
					{	
						// Regulärer Fall: Die Echo PDU hat die gleiche ID, die gerade versendet worden ist.
						if (receivedPdu.getPduID() == pduCounter)
						{
							//System.out.println(this.getName() + ": PDU Nr. " + receivedPdu.getPduID() + " erfolgreich empfangen.");
							
							pduCounter++; 		// Erfolgreicher Durchlauf. Übergehen zum nächsten PDU.
							resendCounter = 0;	// Sendewiederholungscounter aus Null setzen.
						}
						
						/* 
						 * Normaler Fehlerfall: Server hat letztes Paket nochmal geschickt.
						 * Das aktuelle PDU muss also nochmal gesendet werden.
						 */
						else if(receivedPdu.getPduID() == (pduCounter-1))
						{
							//resendCounter++;  // Dies kann man als Sendewiederholung zählen oder auch nicht.
							System.out.println(this.getName() + ": Server " + receivedPdu.getServerThreadName() + " hat erneutes Senden von PDU " + pduCounter + " angefordert. Ich sende nochmal.");
							log.info(this.getName() + ": Server hat erneutes Senden von PDU " + pduCounter + " angefordert. Ich sende nochmal.");
							continue;
						}
						
						/*
						 * Irregulärer Fehlerfall: Es ist eine PDU mit einer unerwarteten ID angekommen.
						 * Hier wird ein Fehler geworfen.
						 */
						else
						{
							throw new IOException("1");
						}
					}
					
					
					/*
					 *  Bei der ersten Antwort des Servers mit der ID 0 wird der
					 *  Serverport auf den Port des Threads umgestellt, der geantwortet hat,
					 *  da dies jetzt der zuständige Server-Thread für diesen Client-Thread ist.
					 */
					if ((receivedPdu.getPduID() == 0) && (serverPort != con.getRemotePort()))
					{
						serverPort = con.getRemotePort();
					}
				} 
				
				// Fehlerbehandlung
				catch (IOException e) {
					
					// Fehler: unerwartete PDU-Nummer
					if (e.getMessage().equals("1"))
					{
						log.error("Fehler beim Client: PDU mit unerwarteter Nummer empfangen!");
						System.out.println("Fehler beim Client: PDU mit unerwarteter Nummer empfangen!");
						break;
					}
					
					// Sonstiger Fehler: ein Receive Timeout.
					else
					{
						log.error("Timeout beim Client: keine Antwort-PDU empfangen!");
						System.out.println("Timeout beim Client: keine Antwort-PDU empfangen!");
					
						// In diesem Fall erneut senden, solange noch Sendewiederholungen möglich sind.
						if (resendCounter < resendAttempts)
						{
							resendCounter++;
							continue;
						}
						
						// Ansonsten abbrechen.
						else
						{
							log.error(this.getName() + ": Maximale Anzahl Sendewiederholungen erreicht! Breche ab!");
							System.out.println(this.getName() + ": Maximale Anzahl Sendewiederholungen erreicht! Breche ab!");
							break;
						}
					}
					//e.printStackTrace();
				}
				
								
				// RTT berechnen
				rtt = System.nanoTime() - rttStartTime;

				// Response-Zaehler erhoehen
				sharedData.incrReceivedMsgCounter(numberOfClient, rtt, receivedPdu.getServerTime());
				
				// Denkzeit
				try {
					Thread.sleep(clientThinkTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
			// Socket schließen
			 con.close();
	        
		}			
 }
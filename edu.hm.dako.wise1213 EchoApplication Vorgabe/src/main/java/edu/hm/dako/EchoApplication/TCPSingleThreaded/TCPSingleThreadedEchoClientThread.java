package edu.hm.dako.EchoApplication.TCPSingleThreaded;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.core.IsInstanceOf;

import edu.hm.dako.EchoApplication.Basics.AbstractClientThread;
import edu.hm.dako.EchoApplication.Basics.EchoPDU;
import edu.hm.dako.EchoApplication.Basics.SharedClientStatistics;

/**
 * Klasse TCPSingleThreadedEchoClientThread
 * 
 * @author Man
 * 
 */
public class TCPSingleThreadedEchoClientThread extends AbstractClientThread {
	private static Log log = LogFactory
			.getLog(TCPSingleThreadedEchoClientThread.class);

	// Lokaler Port zur Kommunikation mit dem Echo-Server
	private int localPort;

	// Name des Threads
	private String threadName;

	// Nummer des Echo-Clients
	private int numberOfClient;

	// Laenge einer Nachricht
	private int messageLength;

	// Anzahl zu sendender Nachrichten je Thread
	private int numberOfMessages;

	// Serverport
	private int serverPort;

	// Adresse des Servers
	private String remoteServerAddress;

	// Denkzeit des Clients zwischen zwei Requests in ms
	private int clientThinkTime;

	// Gemeinsame Daten der Threads
	private SharedClientStatistics sharedData;

	// Socket-Verbindung
	private Socket con;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	// Zeitstempel für RTT-Berechnung und Kalender
	private long rttStartTime;
	private long rtt;

	/**
	 * initialize
	 * 
	 * @param serverPort
	 *            : Port des Servers
	 * @param remoteServerAddress
	 *            : Adresse des Servers
	 * @param numberOfClient
	 *            : Laufende Nummer des Test-Clients
	 * @param messagelength
	 *            : Laenge einer Nachricht
	 * @param numberOfMessages
	 *            : Anzahl zu sendender Nachrichten je Thread
	 * @param clientThinkTime
	 *            : Denkzeit des Test-Clients
	 * @param sharedData
	 *            : Gemeinsame Daten der Threads
	 */
	@Override
	public void initialize(int serverPort, String remoteServerAddress,
			int numberOfClient, int messageLength, int numberOfMessages,
			int clientThinkTime, SharedClientStatistics sharedData) {
		this.serverPort = serverPort;
		this.remoteServerAddress = remoteServerAddress;
		this.numberOfClient = numberOfClient;
		this.messageLength = messageLength;
		this.numberOfMessages = numberOfMessages;
		this.clientThinkTime = clientThinkTime;
		this.sharedData = sharedData;
		this.setName("EchoClient-".concat(String.valueOf(numberOfClient + 1)));
		threadName = getName();
	}

	/**
	 * Run-Methode fuer den Test-Thread: Client-Thread sendet hier alle Requests
	 * und wartet auf Antworten
	 */
	public void run() {
		sharedData.incrNumberOfLoggedInClients();
		
		/**
		 * Synchronisation mit allen anderen Client-Threads: Warten, bis alle
		 * Clients angemeldet sind und dann erst mit der Lasterzeugung beginnen
		 */
		while (!sharedData.allClientsLoggedIn()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("Sleep unterbrochen");
			}
		}
		
	
		/**
		 * Senden von Echo-Nachrichten
		 */

		for (int i = 0; i < numberOfMessages; i++) {
			// RTT-Startzeit ermitteln
			rttStartTime = System.nanoTime();
			
			try {
				// Verbindung zum Server aufbauen, solange versuchen, bis Verbindung
				// steht.
				// Der Server koennte gerade nicht verfuegbar sein und der Verbindungsaufbauversuch 
				// auf einen Timeout laufen, wenn viele Clients aktiv sind.
				con = new Socket(remoteServerAddress, serverPort);
				
				//In- und Outputstream
				out = new ObjectOutputStream(con.getOutputStream());
				in = new ObjectInputStream(con.getInputStream());
				
				//Verbindung ausgeben
				System.out.println("Connection von "+this.getName()+" zu "+remoteServerAddress+":"+serverPort+" aufgebaut");
				
				// Echo-Nachricht aufbauen
				EchoPDU echoSend = new EchoPDU();
				echoSend.setClientName(this.getName());
				echoSend.setMessage("MESSAGE:"+(i+1));
				
				// Letzter Request?
				if (i == numberOfMessages - 1) {
					echoSend.setLastRequest(true);
				}
				
				// Senden der Nachricht an den Server				
				out.writeObject(echoSend);
				
				// Antwort entgegennehmen
				EchoPDU echoRec = (EchoPDU)in.readObject();
				
				//System.out.println("Client "+this.getName()+": "+echoRec.getMessage()+" von "+echoRec.getServerThreadName());
				
				// RTT berechnen
				rtt = System.nanoTime() - rttStartTime;
				
				// Response-Zaehler erhoehen
				sharedData.incrSentMsgCounter(numberOfClient);
				sharedData.incrReceivedMsgCounter(numberOfClient, rtt, echoRec.getServerTime());
 
				// Transportverbindung abbauen
				out.close();
				in.close();
				con.close();
				
				
				
				// Denkzeit
				try {
					Thread.sleep(clientThinkTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}		
			catch (IOException e1) {
				
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("Fehler in Typkonvertierung");
				e.printStackTrace();
			} 
			
		}

		// Statistikdaten des Clients ausgeben
		// sharedData.printClientStatistic(numberOfClient);
	}
}
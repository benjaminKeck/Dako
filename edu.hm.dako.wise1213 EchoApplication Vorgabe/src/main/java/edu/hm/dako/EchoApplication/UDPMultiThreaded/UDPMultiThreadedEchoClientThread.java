package edu.hm.dako.EchoApplication.UDPMultiThreaded;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.EchoApplication.Basics.AbstractClientThread;
import edu.hm.dako.EchoApplication.Basics.EchoPDU;
import edu.hm.dako.EchoApplication.Basics.SharedClientStatistics;

/**
 * Klasse UDPMultiThreadedEchoClientThread
 * 
 * @author Mandl
 * 
 */
public class UDPMultiThreadedEchoClientThread extends AbstractClientThread {
	private static Log log = LogFactory
			.getLog(UDPMultiThreadedEchoClientThread.class);

	// Name des Threads
	private String threadName;

	// Nummer des Echo-Clients
	private int numberOfClient;

	// Laenge einer Nachricht
	private int messageLength;

	// Anzahl zu sendender Nachrichten je Thread
	private int numberOfMessages;

	// Portnummer des Threads
	private int localPort;

	// Serverport
	private int serverPort;

	// Adresse des Servers (String)
	private String remoteServerAddress;

	// Inet-Address des Servers
	private InetAddress remoteInetAddress;

	// Denkzeit des Clients zwischen zwei Requests im ms
	private int clientThinkTime;

	// Timeout für UDP-Receive
	private static final int receivingTimeout = 20000;

	// Gemeinsame Daten der Threads
	private SharedClientStatistics sharedData;

	// Lokales Datagramm-Socket
	private UdpSocket con;

	// Zeitstempel für RTT-Berechnung
	private long rttStartTime;
	private long rtt;

	/**
	 * initialize
	 * 
	 * @param serverPort
	 *            : Port des Servers
	 * @param remoteServerAddress
	 *            : Adresse des Servers
	 * @param port
	 *            : Portnummer des Threads zur Kommunikation mit dem Server
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

		try {
			remoteInetAddress = InetAddress.getByName(remoteServerAddress);
		} catch (UnknownHostException e) {
			log.debug("Exception bei Adressebelegung: " + e);
			System.out.println("Exception bei Adressebelegung: " + e);
		}

		this.numberOfClient = numberOfClient;
		this.messageLength = messageLength;
		this.numberOfMessages = numberOfMessages;
		this.clientThinkTime = clientThinkTime;
		this.sharedData = sharedData;
		this.setName("EchoClient-".concat(String.valueOf(numberOfClient + 1)));
		threadName = getName();

		// UDP-Socket registrieren
		try {
			con = new UdpSocket(localPort, 200000, 300000);
			localPort = con.getLocalPort();
			System.out.println(threadName + ": UDP-Port " + localPort
					+ " registriert");
		} catch (IOException e) {
			log.debug("Exception bei der DatagramSocket-Erzeugung: " + e);
			System.out.println("Exception bei der DatagramSocket-Erzeugung: "
					+ e);
			System.exit(9);
		}
	}

	/**
	 * Run-Methode fuer den Thread: Client-Thread sendet alle Requests und
	 * wartet auf Antworten
	 */
	public void run() {

		sharedData.incrNumberOfLoggedInClients();

		while (!sharedData.allClientsLoggedIn()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("Sleep unterbrochen");
			}
		}

		for (int i = 0; i < numberOfMessages; i++) {
			// RTT-Startzeit ermitteln
			rttStartTime = System.nanoTime();

			try {

				// Echo-Nachricht aufbauen
				EchoPDU echoSend = new EchoPDU();
				echoSend.setClientName(this.getName());
				String message = "Laenge: " + messageLength;
				echoSend.setMessage(message);

				// Das Echo an den Server senden
				if (i == numberOfMessages - 1) {
					echoSend.setLastRequest(true);
				}
				
				con.send(remoteInetAddress, localPort, echoSend);

				// Das Echo des Servers empfangen
				EchoPDU echoRec = (EchoPDU) con.receive(receivingTimeout);

				// RTT berechnen
				rtt = System.nanoTime() - rttStartTime;
				
				// Response-Zaehler erhoehen
				sharedData.incrSentMsgCounter(numberOfClient);
				sharedData.incrReceivedMsgCounter(numberOfClient, rtt,echoRec.getServerTime());

			} catch (IOException ioe) {
				System.out.println(ioe);
			}

		}
	}
}
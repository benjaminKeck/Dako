package edu.hm.dako.EchoApplication.ReliableUdpSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.varia.ReloadingPropertyConfigurator;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;
import edu.hm.dako.EchoApplication.ReliableUdpSocket.ReliableUdpSocket.ReceivedPacketProcessorThread;

/**
 * Klasse ReliableUDPSeverSocket
 * 
 * Entspricht einer stark vereinfachten TCP Socket-Implementierung. Es werden
 * die selben Methoden bereitgestellt. Als Sicherungsmassnahme wird ein
 * Stop-And-Go Verfahren mit einer 3-maligen Sendewiederholung realisiert.
 * 
 * @author Weiss
 * 
 * @version 1.0.1
 */
public class ReliableUdpServerSocket {
	private static Log log = LogFactory.getLog(ReliableUdpServerSocket.class);

	/**
	 * Map der lokal verwendeten Ports und der zugehoerigen PortListener, die
	 * alle ankommenden Nachrichten auf diesen Port annehmen
	 */
	public static Map<Integer, LocalPortListener> aktivePortsUndDerenListener = new HashMap<Integer, LocalPortListener>();

	/**
	 * Der unreliable UDP Socket fuer diesen Port
	 */
	UnreliableUdpSocket unreliableSocket;
	/**
	 * Eine Map aller zu diesem Socket/Port gehoerenden aktiven
	 * ReliableUDPSockets, hinterlegt mit ihren ConnectionStrings als Schluessel
	 */
	protected Map<String, ReliableUdpSocket> reliableSockets = new ConcurrentHashMap<String, ReliableUdpSocket>();

	/**
	 * Eine BlockingQueue mit aller zu diesem Port gehoerenden
	 * ReliableUDPSockets, deren Verbindung noch nicht aufgebaut wurde,
	 * hinterlegt mit ihren ConnectionStrings als Schluessel
	 */
	private BlockingQueue<ReliableUdpSocket> waitingSockets = new ArrayBlockingQueue<ReliableUdpSocket>(
			1000);

	/**
	 * Der Port dieses Sockets
	 */
	Integer port;
	boolean intializedFromClient;

	/**
	 * Oeffentlicher Konstruktor zu Initalisierung als Server
	 * 
	 * @param localPort
	 * @throws SocketException
	 */
	public ReliableUdpServerSocket(Integer localPort) throws SocketException {
		if (aktivePortsUndDerenListener.containsKey(localPort)) {
			throw new SocketException("Port is already in use");
		}
		this.port = localPort;
		unreliableSocket = new UnreliableUdpSocket(port, 200000, 500000);
		LocalPortListener sl = new LocalPortListener(this);
		sl.start();
		aktivePortsUndDerenListener.put(localPort, sl);
		intializedFromClient = false;
	}

	/**
	 * Konstruktor zu Initalisierung eines ServerSockets, wenn ein Client eine
	 * Verbindung öffnet. Der ServerSocket läuft dann im Hintergrund und lauscht
	 * auf dem zugeörigen Port.
	 * 
	 * @param localPort
	 * @throws SocketException
	 */
	protected ReliableUdpServerSocket(Integer localPort, ReliableUdpSocket reliableUdpSocket) throws SocketException {
		// normalen Konstruktor aufrufen
		this(localPort);
		// Den Server beim ReliableUdpSocket setzen
		reliableUdpSocket.socket = unreliableSocket;
		// Die Verbindung muss nicht mehr in den Status waiting
		reliableSockets.put(reliableUdpSocket.getConnectionString(),
				reliableUdpSocket);
		intializedFromClient = true;
	}

	/**
	 * Gibt die nächste wartende Verbindung zurueck oder blockiert bis eine neue
	 * Verbindung aufgebaut ist.
	 * 
	 * @return
	 */
	public ReliableUdpSocket accept() {
		ReliableUdpSocket poll = null;
		try {
			poll = waitingSockets.take();

		} catch (InterruptedException e) {
			log.debug("Programm wurde waehrend des accept-Auufrufs unterbrochen");
		}

		String connectionString = poll.getConnectionString();
		log.info("PUT CONNECTION TO SOCKETS: " + connectionString);
		System.out.println("PUT CONNECTION TO SOCKETS: " + connectionString);
		reliableSockets.put(connectionString, poll);
		poll.accept();
		return poll;
	}

	/**
	 * Aufraeumen des Sockets. Wenn der Client den Socket erzeugt hat, kann der
	 * UnreliableUDPSocket freigegeben werden.
	 * 
	 * @param reliableUdpSocket
	 */
	public void shutdownReliableUdpSocket(ReliableUdpSocket reliableUdpSocket) {
		reliableSockets.remove(reliableUdpSocket.getConnectionString());
		if (intializedFromClient) {
			log.info("SHUTTING DOWN PORT:" + port);
			LocalPortListener remove = aktivePortsUndDerenListener.remove(port);
			remove.interrupt();
			unreliableSocket.close();
		}

	}

	public static String getConnectionString(String localAdress, int localPort,
			String remoteAdress, int remotePort) {
		return localAdress + ":" + localPort + "-" + remoteAdress + ":"
				+ remotePort;
	}

	/**
	 * Der LocalPortListener-Thread empfaengt alle eingehenden Nachrichten auf
	 * diesem UDPSocket und verteilt diese dann an die zugehoerigen Verbindungen
	 * (ReliableUdpSockets). Falls keine Verbindung existiert wird ein neuer
	 * ReliableUDPSocket erstellt und zu den wartenden Verbindungen
	 * hinzugefuegt.
	 * 
	 */
	public class LocalPortListener extends Thread {
		ReliableUdpServerSocket basisSocket;
		UnreliableUdpSocket unreliableSocket;

		public LocalPortListener(ReliableUdpServerSocket basisSocket) {
			this.basisSocket = basisSocket;
			this.unreliableSocket = basisSocket.unreliableSocket;
			setName("LocalPortListener: " + unreliableSocket.getLocalPort());
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				try {
					ReliableUdpObject receivedPDU = (ReliableUdpObject) unreliableSocket.receive(100);
					// TODO
										
					String remoteAdress = ""+unreliableSocket.getRemoteAddress();
					int remotePort = unreliableSocket.getRemotePort();
					remoteAdress = remoteAdress.substring(1);
					//System.out.println("remoteAdress: "+remoteAdress+", port: "+remotePort);
					
					ReliableUdpSocket s = new ReliableUdpSocket(remoteAdress, remotePort);
					s.process(receivedPDU);
					s.accept();
				//	s.inputStreamVonDerOberenSchicht
					//s.inputStreamDerOberenSchicht.=receivedPDU.getData();
					if(!waitingSockets.contains(s))
						waitingSockets.add(s);
					
					System.out.println("M: "+((EchoPDU)receivedPDU.getData()).getMessage());
					//waitingSockets.add(((EchoPDU)receivedPDU).getServerThreadName());
					//reliableSockets.
					//waitingSockets.add(new ReliableUdpSocket(receivedPDU., serverPort))
					
					receivedPDU.setAck(true);

				} catch (SocketTimeoutException e) {
					// Der Timeout ist abgelaufen, einfach nochmal versuchen
					// Die Schleife beendet sich, wenn in der Zwischenzeit
					// interrupt aufgerufen wurde.
					continue;
				} catch (SocketException e) {
					if (!isInterrupted()) {
						log.error("SocketException", e);
						e.printStackTrace();
					}
				} catch (Exception e) {
					log.error("SocketIOException", e);
					e.printStackTrace();
				} 
			}
		}

		private String generateConnectionString() {
			return ReliableUdpServerSocket.getConnectionString(unreliableSocket
					.getLocalAddress(), unreliableSocket.getLocalPort(),
					unreliableSocket.getRemoteAddress().getHostAddress(),
					unreliableSocket.getRemotePort());

		}
	}
}
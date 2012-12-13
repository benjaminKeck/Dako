package edu.hm.dako.EchoApplication.UDPSingleThreaded;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;

/**
 * Klasse UDPSingleThreadedEchoServer
 * 
 * Single-Threaded Server
 * 
 * @author Thorben Knichwitz
 * 
 */
public class UDPSingleThreadedEchoServer extends Thread {

	private static Log log = LogFactory
			.getLog(UDPSingleThreadedEchoServer.class);

	/**
	 * Server-Port
	 */
	private static int serverPort = 50000;

	/**
	 * Verbindungstabelle: Hier werden alle aktiven Verbindungen zu Clients
	 * verwaltet
	 */
	private static Map<String, String> connections = new ConcurrentHashMap<String, String>();

	/**
	 * UdpSocket des Servers
	 */
	private static UdpSocket serverSocket;

	/**
	 * Verbindungszaehler
	 */
	private static long nrConnections = 0;

	public static void main(String args[]) throws IOException {

		PropertyConfigurator.configureAndWatch("log4j.server.properties",
				60 * 1000);

		try {

			/** Neuen UdpSocket erzeugen */
			serverSocket = new UdpSocket(serverPort, 200000, 300000);
			System.out
					.println("UDPSingleThreadedEchoServer wartet auf Clients...");

		} catch (IOException e) {
			log.debug("Exception bei der Socket-Erzeugung: " + e);
			System.exit(9);
		}

		while (true) {

			EchoPDU echoRec = null;

			try {

				/** Das Echo des Clients empfangen */
				echoRec = (EchoPDU) serverSocket.receive(200000);

				/** Startzeit festlegen */
				long startTime = System.nanoTime();

				if (echoRec != null) {

					/** Connection in die Liste eintragen */
					if (!connections.containsKey(echoRec.getClientName())) {
						connections.put(echoRec.getClientName(),
								serverSocket.getLocalAddress());
						nrConnections++;
					}
				}

				if (echoRec.getLastRequest()) {
					System.out.println("Letzter Request des Clients "
							+ echoRec.getClientName());

					/** Verbindung wird aus der Liste gelöscht */
					connections.remove(echoRec.getClientName());

				}

				/** Nachricht die der Server erhalten hat */
				System.out
						.println("Server empfaengt von "
								+ echoRec.getClientName() + ": "
								+ echoRec.getMessage());

				/**
				 * Neues EchoPDU erzeugen
				 * EchoPDU ServerZeit setzen
				 * EchoPDU Nachricht setzen
				 * EchoPDU ServerThreadName setzen
				 * EchoPDU ClientName setzen
				 *  
				 */
				EchoPDU echoSend = new EchoPDU();
				echoSend.setServerTime(System.nanoTime() - startTime);
				echoSend.setMessage(echoRec.getMessage() + "_Server");
				echoSend.setClientName(echoRec.getClientName());
				echoSend.setServerThreadName("SingleServerThread");

				try {

					/** Das Echo an den Clients zurück senden */
					serverSocket.send(serverSocket.getRemoteAddress(),
							serverSocket.getRemotePort(), echoSend);

					/** Wenn es die Letzte Nachricht war Hashmap leeren */
					if (echoRec.getLastRequest() == true) {
						connections.remove(echoRec.getClientName());
						nrConnections--;
					}

				} catch (Exception e) {
					log.error("Socket Exception: " + e);
				}

			} catch (Exception e) {
				log.error("Socket Exception: " + e);
			}
		}
	}
}
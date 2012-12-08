package edu.hm.dako.EchoApplication.UDPSingleThreaded;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;
import edu.hm.dako.EchoApplication.UDPMultiThreaded.UdpSocket;

/**
 * Klasse UDPSingleThreadedEchoServer
 * 
 * Single-Threaded Server
 * 
 * @author Mandl
 * 
 */
public class UDPSingleThreadedEchoServer extends Thread {

	private static Log log = LogFactory
			.getLog(UDPSingleThreadedEchoServer.class);

	private static int serverPort = 50000;

	// Verbindungstabelle: Hier werden alle aktiven Verbindungen zu Clients
	// verwaltet
	private static Map<String, UdpSocket> connections = new ConcurrentHashMap<String, UdpSocket>();

	
	
	// Datagram-Socket des Servers (Listen-Socket)
	private static UdpSocket serverSocket;

	// TODO: Ganze Klasse programmieren
	public static void main(String args[]) throws IOException {

		PropertyConfigurator.configureAndWatch("log4j.server.properties",
				60 * 1000);

		try {

			serverSocket = new UdpSocket(serverPort, 200000, 300000);
			System.out
					.println("UDPSingleThreadedEchoServer wartet auf Clients...");
		} catch (IOException e) {
			log.debug("Exception bei der Socket-Erzeugung: " + e);
			System.exit(9);
		}

		while (true) {
			// Das Echo des Clients empfangen
			EchoPDU echoRec = (EchoPDU) serverSocket.receive(200000);

			long startTime = System.nanoTime();
			// Echo-Nachricht aufbauen
			System.out.println("Server empfaengt von "+ echoRec.getClientName() + ": " + echoRec.getMessage());
			connections.put(echoRec.getClientName(), serverSocket);
			log.debug("Aktuell angemeldete Clients: " + connections.size());
			EchoPDU echoSend = new EchoPDU();
			echoSend.setServerTime(System.nanoTime() - startTime);
			echoSend.setMessage(echoRec.getMessage());
			echoSend.setServerThreadName("SingleServerThread");

			// Das Echo an den Clients senden
			serverSocket.send(serverSocket.getRemoteAddress(), serverPort,
					echoSend);
			log.debug("versendet: " + serverSocket.getRemoteAddress() + " " + serverPort);

			if (echoRec.getLastRequest() == true) {
				connections.remove(echoRec.getClientName());

				serverSocket.close();
			}
			//
			// if (connections.isEmpty()) {
			 log.debug("UDPEchoServer beendet sich");
			// System.out.println("UDPMultiThreadedEchoServer beendet sich");
			// break;
			// }

			log.debug("Aktuell angemeldete Clients: " + connections.size());
			// }
		}
	}

}
package edu.hm.dako.EchoApplication.UDPMultiThreaded;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;


/**
 * Klasse UDPMultiThreadedEchoServer.
 *
 * @author Thorben Knichwitz, Daniel Ostertag
 */
public class UDPMultiThreadedEchoServer extends Thread {
	
	/** Der Logfile */
	private static Log log = LogFactory
			.getLog(UDPMultiThreadedEchoServer.class);

	/** ServerPort */
	private static int serverPort = 50000;


	/** Verbindungstabelle: Hier werden alle aktiven Verbindungen zu Clients verwaltet */
	private static Map<String, String> connections = new ConcurrentHashMap<String, String>();

	/**  Datagram-Socket des Servers (Listen-Socket) */
	private static UdpSocket serverSocket;

	/** Timeout für UDP-Receive */
	private static final int receivingTimeout = 20000;

	/** UDPRemoteObject */
	private UdpRemoteObject pdu = null;
	
	private static int numberOfWorkerThread = 0;

	/** Die Startzeit zur Messung der Serverzeit */
	long startTime;

	/**
	 * Konstruktor.
	 *
	 * @param receivedPdu the received pdu
	 */
	public UDPMultiThreadedEchoServer(UdpRemoteObject receivedPdu) {
		pdu = receivedPdu;
	}

	/**
	 * main.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		PropertyConfigurator.configureAndWatch("log4j.server.properties",
				60 * 1000);

		/**
		 * UDP-Serversocket registrieren
		 * 200000 SendBufferSize
		 * 300000 SendBufferSize
		 * 
		 */
		try {

			serverSocket = new UdpSocket(serverPort, 200000, 300000);
			System.out
					.println("UDPMultiThreadedEchoServer wartet auf Clients...");
		} catch (IOException e) {
			log.debug("Exception bei der Socket-Erzeugung: " + e);
			System.exit(9);
		}

		/**
		 * EchoPDU wird initialisiert
		 * Bedingung für die Endlosschleife wird initialisiert
		 * 
		 */
		EchoPDU receivedPdu = null;
		boolean finished = false;

		while (!finished) {
			
			/**
			 * Auf ankommende Verbindungsaufbauwuensche warten und diese
			 * in eigenen Threads bearbeiten
			 *  
			 */

			try {
				
				/**
				 * Verbindung und EchoPDU entgegennehmen
				 *  
				 */
				receivedPdu = (EchoPDU) serverSocket.receive(receivingTimeout);

				/**
				 * Neues RemoteObject anlegen
				 *  
				 */
				UdpRemoteObject receivedRemoteObject = new UdpRemoteObject(
						serverSocket.getRemoteAddress(),
						serverSocket.getRemotePort(), receivedPdu);

				
				/**
				 * Wenn das erhaltene PDU nicht leer war erzeuge einen neuen serverThread
				 * Erhöhe die Anzahl der Threads
				 * Starte den Thread
				 *  
				 */
				if (receivedPdu != null) {

					UDPMultiThreadedEchoServer serverThread = new UDPMultiThreadedEchoServer(receivedRemoteObject);

					numberOfWorkerThread++;

					// Serverthread starten
					serverThread.start();
				}

			} catch (IOException e) {

				finished = true;

			}

		}



	}

	/**
	 * Worker-Thread-Methode fuer die Bearbeitung eines Clients.
	 */

	public void run() {

		

		try {
			
			/**
			 * EchoPDU aus dem RemoteObjekt holen
			 *  
			 */
			EchoPDU echoPdu = (EchoPDU) pdu.getObject();
			
			/**
			 * WorkerThread Namen zuweisen
			 *  
			 */
			setName("WorkerThread-" + echoPdu.getClientName());

			System.out.println(this.getName() + ": WorkerThread uebernimmt Request von " + echoPdu.getClientName());

			/**
			 * Startzeit initialisieren
			 *  
			 */
			long startTime = System.nanoTime();

			/**
			 * Neues EchoPDU erzeugen
			 * EchoPDU ServerThreadName setzen
			 * EchoPDU ClientName setzen
			 * EchoPDU Nachricht setzen
			 * EchoPDU ServerZeit setzen
			 *  
			 */
			EchoPDU echoSend = new EchoPDU();
			log.debug("Serverzeit: " + (System.nanoTime() - startTime) + " ns");
			echoSend.setServerThreadName(this.getName());
			echoSend.setClientName(echoPdu.getClientName());
			echoSend.setMessage(echoPdu.getMessage() + "_S");
			echoSend.setServerTime(System.nanoTime() - startTime);

			/** Connection in die Liste eintragen */
			connections.put(echoPdu.getClientName(),
					serverSocket.getLocalAddress());
			
			/** 
			 * Wenn die letzte Nachricht eingetroffen ist, dann lösche die Verbindung aus der Map
			 */
			if (echoPdu.getLastRequest()) {
				System.out.println("Letzter Request des Clients " + echoPdu.getClientName());
				
				connections.remove(this.getName());

			}

			/** Sende die Neue Nachricht an den Client zurück */
			serverSocket.send(pdu.getRemoteAddress(), pdu.getRemotePort(),
					echoSend);

		} catch (IOException e) {

			e.printStackTrace();
		}

		/** Kurze Wartezeit */
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e3) {
		}

		/**
		 * Wenn nun in Der Verbindungsliste kein Eintrag mehr vorhanden ist
		 * bedeutet das das dies der letzte Thread ist und nun keiner mehr
		 * kommt.
		 *  
		 */
		if (connections.isEmpty()) {
			System.out.println("alle ServerThreads fertig");

		}

	}
}
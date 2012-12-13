package edu.hm.dako.EchoApplication.ReliableUdpMultiThreaded;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import sun.security.action.GetLongAction;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;
import edu.hm.dako.EchoApplication.ReliableUdpSocket.ReliableUdpServerSocket;
import edu.hm.dako.EchoApplication.ReliableUdpSocket.ReliableUdpSocket;

/**
 * Klasse ReliableUDPMultiThreadedEchoServer
 * 
 * @author Weiss, Mandl
 * 
 */
// TODO Ganze Klasse implementieren
public class ReliableUdpMultiThreadedEchoServer extends Thread {
	private static Log log = LogFactory
			.getLog(ReliableUdpMultiThreadedEchoServer.class);

	private static int serverPort = 50000;

	private static int numberOfWorkerThread = 0;

	// Verbindungstabelle: Hier werden alle aktiven Verbindungen zu Clients
	// verwaltet
	private static Map<String, ReliableUdpSocket> connections = new ConcurrentHashMap<String, ReliableUdpSocket>();

	// TCP-Socket des Servers (Listen-Socket)
	private static ReliableUdpServerSocket serverSocket;

	// Transportverbindung und Streams fuer einen Client
	private ReliableUdpSocket con;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	// Groesse des Empfangspuffers einer TCP-Verbindung in Byte
	private static final int receiveBufferSize = 300000;

	/**
	 * Konstruktor
	 */
	public ReliableUdpMultiThreadedEchoServer(ReliableUdpSocket incoming) {
		this.con = incoming;

		// Ein- und Ausgabe-Objektstrom erzeugen
		try {
			out = new ObjectOutputStream(incoming.getOutputStream());
			in = new ObjectInputStream(incoming.getInputStream());
			System.out.println("Server_"+this.getName()+"_Verbindung angelegt: " + incoming.getPort());
		} catch (Exception e) {
			// TODO
		}
	}

	public static void main(String args[]) {
		PropertyConfigurator.configureAndWatch("log4j.server.properties",
				60 * 1000);

		try {
			serverSocket = new ReliableUdpServerSocket(serverPort);
			System.out.println("TCPMultiThreadedEchoServer wartet auf Clients...");
		} catch (IOException e) {
			log.debug("Exception bei der Socket-Erzeugung: " + e);
			System.exit(9);
		}

		while (true) {
			// Auf ankommende Verbindungsaufbauwuensche warten und diese
			// in eigenen Threads bearbeiten

			// Eine Socketanfrage entgegen nehmen
			ReliableUdpSocket socket1 = serverSocket.accept();

			// neuen Serverthread erstellen
			ReliableUdpMultiThreadedEchoServer thread = new ReliableUdpMultiThreadedEchoServer(socket1);
			
			// connection in Liste eintragen
			connections.put(thread.getName(), socket1);

			numberOfWorkerThread++;

			// Serverthread starten
			thread.start();

		}
	}

	public void run() {
		boolean finished = false;
		EchoPDU receivedPdu = new EchoPDU();
		long startTime;

		System.out.println(this.getName()+ ": Verbindung mit neuem Client aufgebaut, Remote-TCP-Port "+ con.getPort());

		try {
			log.debug("Standardgroesse des Empfangspuffers der Verbindung: "
					+ con.getReceiveBufferSize() + " Byte");
			con.setReceiveBufferSize(receiveBufferSize);
			log.debug("Eingestellte Groesse des Empfangspuffers der Verbindung: "
					+ con.getReceiveBufferSize() + " Byte");

		} catch (SocketException e) {
			log.debug("Socketfehler: " + e);
		}

		while (!finished) {
			
			try {
				// Echo-Request entgegennehmen
				//Über Streams lesen und schreiben...
				receivedPdu = (EchoPDU) in.readObject();
				System.out.println("YAY_: "+receivedPdu.getMessage());
				startTime = System.nanoTime();
				log.debug("Request empfangen von "
						+ receivedPdu.getClientName() + ": "
						+ receivedPdu.getMessage());
			} catch (IOException e) {
				log.debug("Empfangen einer Nachricht nicht moeglich: " + e);
				finished = true;
				continue;
			} catch (ClassNotFoundException e) {
				log.debug("Unbekannte Objektklasse empfangen: " + e);
				finished = true;
				continue;
			} 
			try {
				// Echo-Response senden
				EchoPDU sendPdu = new EchoPDU();
				log.debug("Serverzeit: " + (System.nanoTime() - startTime)
						+ " ns");
				sendPdu.setServerThreadName(this.getName());
				sendPdu.setClientName(receivedPdu.getClientName());
				sendPdu.setMessage(receivedPdu.getMessage()
						+ "_vomServerZurueck");
				sendPdu.setServerTime(System.nanoTime() - startTime);

				out.writeObject(sendPdu);
				out.flush();
				//log.debug("Response gesendet");
			} catch (IOException e) {
				log.error("Senden einer Nachricht nicht moeglich: " + e);
				finished = true;
			}

			if (receivedPdu.getLastRequest()) {
				System.out.println("Letzter Request des Clients "
						+ receivedPdu.getClientName());
				finished = true;
			}
		}

		// Kurz warten
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e3) {
		}

		System.out.println(this.getName()
				+ ": Verbindung mit Client abbauen, Remote-TCP-Port "
				+ con.getPort());

		try {
			out.flush();
			con.close();
		} catch (IOException e) {
			System.out.println("Exception bei close: " + e);
		}

		log.debug(this.getName() + " beendet sich");
		System.out.println(this.getName() + " beendet sich");

		// Verbindung wird aus der Liste gelöscht
		connections.remove(this.getName());

		// Wenn nun in Der Verbindungsliste kein Eintrag mehr vorhanden ist
		// bedeutet das das dies der letzte Thread ist und nun keiner mehr
		// kommt.
		if (connections.isEmpty()) {
			System.out.println("alle ServerThreads fertig");
			// try {
			// //Das serverSocket wird beendet.
			// //Da in der Main-Methode mit dem Accept im Moment aber auf eine
			// Verbindung gewartet wird
			// //wird eine Exception in der Main geworfen
			// serverSocket.close();
			// }
			// catch (IOException e) {
			// e.printStackTrace();
			// }
		}

		// Der Thread wird beendet
		this.stop();

	} // run

}
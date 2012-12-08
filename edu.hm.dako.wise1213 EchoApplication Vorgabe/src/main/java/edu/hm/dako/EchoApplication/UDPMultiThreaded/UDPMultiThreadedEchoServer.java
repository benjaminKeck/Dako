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
import edu.hm.dako.EchoApplication.TCPMultiThreaded.TCPMultiThreadedEchoServer;

/**
 * Klasse UDPMultiThreadedEchoServer
 * 
 * @author Mandl
 * 
 */
public class UDPMultiThreadedEchoServer extends Thread {
	private static Log log = LogFactory
			.getLog(UDPMultiThreadedEchoServer.class);

	private static int serverPort = 50000;

	// Verbindungstabelle: Hier werden alle aktiven Verbindungen zu Clients
	// verwaltet
	private static Map<String, String> connections = new ConcurrentHashMap<String, String>();

	// Datagram-Socket des Servers (Listen-Socket)
	private static UdpSocket serverSocket;

	// Timeout f�r UDP-Receive
	private static final int receivingTimeout = 20000;

	private UdpRemoteObject pdu = null;

	private static int numberOfWorkerThread = 0;

	long startTime;

	/**
	 * Konstruktor
	 */
	public UDPMultiThreadedEchoServer(UdpRemoteObject receivedPdu) {
		pdu = receivedPdu;
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		PropertyConfigurator.configureAndWatch("log4j.server.properties",
				60 * 1000);

		// TODO UDP-Serversocket registrieren
		try {

			serverSocket = new UdpSocket(serverPort, 200000, 300000);
			System.out
					.println("UDPMultiThreadedEchoServer wartet auf Clients...");
		} catch (IOException e) {
			log.debug("Exception bei der Socket-Erzeugung: " + e);
			System.exit(9);
		}

		boolean finished = false;
		EchoPDU receivedPdu;

		while (!finished) {

			try {
				receivedPdu = (EchoPDU) serverSocket.receive(receivingTimeout);

				UdpRemoteObject receivedRemoteObject = new UdpRemoteObject(
						serverSocket.getRemoteAddress(),
						serverSocket.getRemotePort(), receivedPdu);

				if (receivedPdu != null) {
					UDPMultiThreadedEchoServer serverThread = new UDPMultiThreadedEchoServer(
							receivedRemoteObject);
					serverThread.start();
					connections.put(serverThread.getName(),
					serverSocket.getLocalAddress());
					numberOfWorkerThread++;
				}

				if (receivedPdu.getLastRequest()) {
					System.out.println("Letzter Request des Clients "
							+ receivedPdu.getClientName());
					finished = true;
				}

			} catch (IOException e) {
				// e.printStackTrace();

			}

		}

	}

	/**
	 * Worker-Thread-Methode fuer die Bearbeitung eines Clients
	 */

	public void run() {

		boolean finished = false;
		long startTime = System.nanoTime();
		EchoPDU echoPdu = (EchoPDU) pdu.getObject();

		
		while (!finished) {
			setName("WorkerThread-" + echoPdu.getClientName());
			System.out.println(this.getName()
					+ ": WorkerThread uebernimmt Request von "
					+ echoPdu.getClientName());

			try {
				EchoPDU echoSend = new EchoPDU();
				log.debug("Serverzeit: " + (System.nanoTime() - startTime)
						+ " ns");
				echoSend.setServerThreadName(this.getName());
				echoSend.setClientName(echoPdu.getClientName());
				echoSend.setMessage(echoPdu.getMessage() + "_vomServerZurueck");
				echoSend.setServerTime(System.nanoTime() - startTime);

				serverSocket.send(serverSocket.getRemoteAddress(), serverPort,
						echoSend);

			} catch (IOException e) {

				e.printStackTrace();
			}

			// Kurz warten
   	    	try {
    			Thread.sleep(1000);
    		}
    		catch (InterruptedException e3){}
    		  	
    		System.out.println(this.getName() + ": Verbindung mit Client abbauen, Remote-UDP-Port " + serverSocket.getRemotePort());
    		
   	    	serverSocket.close();   	

	    	log.debug(this.getName() + " beendet sich");  
	    	System.out.println(this.getName()+" beendet sich");
	    	
	    	//Verbindung wird aus der Liste gel�scht
	    	connections.remove(this.getName());
	    	
	    	//Wenn nun in Der Verbindungsliste kein Eintrag mehr vorhanden ist
	    	//bedeutet das das dies der letzte Thread ist und nun keiner mehr kommt.
			if(connections.isEmpty()){
				System.out.println("alle ServerThreads fertig");
			}
		}// TODO

	} // run
}
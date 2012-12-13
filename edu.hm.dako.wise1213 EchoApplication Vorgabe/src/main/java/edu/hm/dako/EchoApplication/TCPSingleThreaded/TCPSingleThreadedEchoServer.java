package edu.hm.dako.EchoApplication.TCPSingleThreaded;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;

/**
 * Klasse TCPSingleThreadedEchoServer
 * 
 * Nur ein Thread bedient alle Clients
 * 
 * @author Mandl
 * 
 */
public class TCPSingleThreadedEchoServer {
	private static Log log = LogFactory
			.getLog(TCPSingleThreadedEchoServer.class);

	private static int serverPort = 50000;

	/** Verbindungstabelle: Hier werden alle aktiven Verbindungen zu Clients
	 * verwaltet 
	 * 
	 */
	private static Map<String, Socket> connections = new ConcurrentHashMap<String, Socket>();

	/** TCP-Socket des Servers (Listen-Socket)*/
	private static ServerSocket serverSocket;

	/** Laenge der Queue des Server-Sockets fuer ankommende 
	 * Verbindungsaufbauwuensche
	 * 
	 */
	private static final int backlog = 20;

	/** Verbindungszaehler */
	private static long nrConnections = 0;

	/** Streams fuer TCP-Verbindung */
	private static ObjectOutputStream out;
	private static ObjectInputStream in;

	/**
	 * Konstruktor
	 */
	public TCPSingleThreadedEchoServer() {
	}

	/**
	 * MainMethode
	 * 
	 * @param args
	 */

	public static void main(String args[]) {
		PropertyConfigurator.configureAndWatch("log4j.server.properties",
				60 * 1000);

		/* TCP-Serversocket registrieren */
		try {

			serverSocket = new ServerSocket(serverPort, backlog);
			System.out.println("TCPSingleThreadedEchoServer wartet auf Clients.....");
		} catch (IOException e) {
			log.debug("Exception bei der Socket-Erzeugung: " + e);
			System.exit(9);
		}

		while (true) {
			try {
				
				
				/* Auf ankommende Verbindungsaufbauwuensche warten */
				Socket socket = serverSocket.accept();
				
				/*Servernamen ausgeben */
				System.out.println("Verbindung hergestellt zu "+socket.getInetAddress().getHostName());
				log.info("Verbindung hergestellt zu "+socket.getInetAddress().getHostName());
				
				/*In- und Outputstreams festlegen */
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				
				/* Echo-Request entgegennehmen */
				EchoPDU echoRec = (EchoPDU)in.readObject();
				
				/*Serverzeit messen */
				long startTime=System.nanoTime();
				
				/*Nachricht auf Konsole ausgeben */
				System.out.println("Server empfaengt von "+echoRec.getClientName()+": "+echoRec.getMessage());
				
				/*Verbindung in Map speichern */
				connections.put(echoRec.getClientName(), socket);
				
				/*Antwort erstellen */
				EchoPDU echoSend = new EchoPDU();
				
				/*Serverzeit setzen */
				echoSend.setServerTime(System.nanoTime()-startTime);
				
				/*Message definieren */
				echoSend.setMessage(echoRec.getMessage()+"_zurueck_");
				
				/* Threadname setzen */
				echoSend.setServerThreadName("SingleServerThread");
				
				/* Echo-Response senden */
				out.writeObject(echoSend);
				
				/* Verbindung schliessen */
				in.close();
				out.close();
				socket.close(); 
		         
				
				if(echoRec.getLastRequest()){
					connections.remove(echoRec.getClientName());
				}
				
				if(connections.isEmpty()){
					System.out.println("Server fertig");
					
					//Server selber beendeen
					//break;
				}
			}
			
			catch (Exception e) {
				log.error("Socket Exception: " + e);
			}
		}
		
	}
}
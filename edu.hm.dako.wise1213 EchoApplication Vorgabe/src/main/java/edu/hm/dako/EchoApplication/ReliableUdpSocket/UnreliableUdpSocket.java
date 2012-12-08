package edu.hm.dako.EchoApplication.ReliableUdpSocket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Klasse UnreliableUdpSocket
 * 
 * Diese Klasse kapselt die Datagram-Sockets und stellt eine etwas komfortablere
 * Schnittstelle zur Verfuegung.
 * 
 * Der Mehrwert dieser Klasse im Vergleich zur Standard-DatagramSocket-Klasse
 * ist die Nutzung eines Objektstroms zur Kommunikation ueber UDP.
 * 
 * Achtung: Maximale Datagramlaenge: 64 KByte
 * 
 * @author Mandl
 * 
 * @version 1.1.0
 */
public class UnreliableUdpSocket {
	private static Log log = LogFactory.getLog(UnreliableUdpSocket.class);
	private DatagramSocket socket;
	private InetAddress remoteAddress;
	private int remotePort;
	private Random random = new Random();

	/**
	 * Konstruktor
	 * 
	 * @param port
	 * 			UDP-Port, der lokal fuer das Datagramm-Socket verwendet werden
	 * 			soll
	 */
	public UnreliableUdpSocket(int port) throws SocketException {
		socket = new DatagramSocket(port);
		try {
			log.debug("Groesse des Empfangspuffers des Datagram-Sockets: "
					+ socket.getReceiveBufferSize() + " Byte");
			log.debug("Groesse des Sendepuffers des Datagram-Sockets: "
					+ socket.getSendBufferSize() + " Byte");
		} catch (SocketException e) {
			log.debug("Socketfehler: " + e);
		}
	}

	/**
	 * Konstruktor
	 * 
	 * @param port
	 *            UDP-Port, der lokal fuer das Datagramm-Socket verwendet werden
	 *            soll
	 * @param sendBufferSize
	 *            Groesse des Sendepuffers in Byte
	 * @param receiveBufferSize
	 *            Groesse des Empfangspuffers in Byte
	 */

	public UnreliableUdpSocket(int port, int sendBufferSize, int receiveBufferSize)
			throws SocketException {
		socket = new DatagramSocket(port);
		try {
			socket.setReceiveBufferSize(receiveBufferSize);
			socket.setSendBufferSize(sendBufferSize);

			log.debug("Groesse des Empfangspuffers des Datagram-Sockets: "
					+ socket.getReceiveBufferSize() + " Byte");
			log.debug("Groesse des Sendepuffers des Datagram-Sockets: "
					+ socket.getSendBufferSize() + " Byte");
			
		} catch (SocketException e) {
			log.debug("Socketfehler: " + e);
		}
	}

	/**
	 * Empfangen einer Nachricht ueber UDP
	 * 
	 * @return Referenz auf Nachricht, die empfangen wurde
	 * @param timeout
	 *            Wartezeit in ms
	 *            timout = 0 bedeutet unbegrenztes Warten, bis ein Paket ankommt.
	 * @throws IOException
	 */
	public  Object receive(int timeout) throws IOException,
			SocketTimeoutException {
		// Maximale Wartezeit fuer Receive am Socket einstellen
		try {
//			log.info("RECEIVE MIT TIMEOUT: "+timeout);
			socket.setSoTimeout(timeout);
			// System.out.println("RECEIVE: Maximale Wartezeit: " +
			// timeout +" ms");
		} catch (SocketException e) {
			log.error("RECEIVE: "
					+ "Fehler beim Einstellen der maximalen Wartezeit");
			throw e;
		}

		byte[] bytes = new byte[65527];
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

		try {
			// Blockiert nur, bis Timeout abgelaufen ist
			socket.receive(packet);
			log.debug("RECEIVE: Empfangene Datenlaenge:  " + packet.getLength());

		} catch (IOException e2) {
//			log.error("RECEIVE: " + "Fehler beim Empfangen einer PDU ueber UDP",e2);
			throw e2;
		}

		ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
		ObjectInputStream ois = new ObjectInputStream(bais);

		Object pdu;
		try {

			// System.out.println("RECEIVE: " +
			// "Verfuegbare Bytes im Inputstream des UDP-Sockets:" +
			// ois.available());

			pdu = ois.readObject();

			remoteAddress = packet.getAddress();
			remotePort = packet.getPort();

			log.debug("RECEIVE: " + packet.getPort() + "->"
					+ socket.getLocalPort());

		} catch (ClassNotFoundException e) {
			log.error("RECEIVE: " + "ClassNotFoundException:", e);
			return null;
		}

		log.info("RECEIVE MIT TIMEOUT ENDE: "+timeout);
		return pdu;
	}

	/**
	 * Senden einer Nachricht ueber UDP
	 * 
	 * @param remoteAddress
	 *            : Adresse des Empfaengers
	 * @param remotePort
	 *            : Port des Empfaengers
	 * @param pdu
	 *            : Zu sendende PDU
	 * @throws IOException
	 */
	public void send(InetAddress remoteAddress, int remotePort, Object pdu)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(pdu);
		byte[] bytes = out.toByteArray();

		log.debug("SEND: zu sendende Bytes: " + bytes.length);
//		if(random.nextInt()%20==0){
//			log.warn("NACHRICHT WURDE VERWORFEN");
//			System.out.println("NACHRICHT WURDE VERWORFEN");
//			return;
//		}
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
				remoteAddress, remotePort);

		log.debug("SEND: " + packet.getAddress() + ":" + packet.getPort());

		try {
			socket.send(packet);
		} catch (IOException e) {
			log.error("SEND: " + "Fehler beim Senden einer PDU");
			throw e;
		}
	}

	/**
	 * Datagram-Socket schlieﬂen
	 */
	public void close() {
		log.debug("CLOSE: " + "Socket wird geschlossen");
		socket.close();
	}

	/**
	 * @return Lokale Adresse
	 */
	public String getLocalAddress() {
		return socket.getLocalAddress().getHostAddress();
	}

	/**
	 * @return lokalen Port
	 */
	public int getLocalPort() {
		return socket.getLocalPort();
	}

	/**
	 * @return Remote Adresse
	 */
	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * @return Remote Port
	 */
	public int getRemotePort() {
		return remotePort;
	}
}
package edu.hm.dako.EchoApplication.UDPSingleThreaded;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Klasse UdpSocket
 * 
 * Diese Klasse kapselt die Datagram-Sockets und stellt
 * eine etwas komfortablere Schnittstelle zur Verfuegung.
 * 
 * Der Mehrwert dieser Klasse im Vergleich zur Standard-DatagramSocket-Klasse
 * ist die Nutzung eines Objektstroms zur Kommunikation ueber UDP.
 * 
 * Achtung: Maximale Datagramlaenge: 64 KByte
 * @author Mandl
 * 
 * @version 1.0.0
 */
public class UdpSocket 
{
    private static Log log = LogFactory.getLog(UdpSocket.class);
    private DatagramSocket socket;
    private InetAddress remoteAddress;
    private int remotePort;
    
    // Anzahl Wiederholungen beim Empfangen eines UDP-Paketes
    private static int numberOfRetries = 3;
    
    /**
     * Konstruktor
     * @param port UDP-Port, der lokal fuer das Datagramm-Socket verwendet werden soll
     */
    public UdpSocket(int port) throws SocketException {
        socket = new DatagramSocket(port);
        try {
	    	log.debug("Groesse des Empfangspuffers des Datagram-Sockets: " + socket.getReceiveBufferSize() + " Byte");
	    	log.debug("Groesse des Sendepuffers des Datagram-Sockets: " + socket.getSendBufferSize() + " Byte");
	    } catch (SocketException e){
	    	log.debug("Socketfehler: " + e);
	    }
    }
    
    /**
     * Konstruktor
     * @param port UDP-Port, der lokal fuer das Datagramm-Socket verwendet werden soll
     * @param sendBufferSize Groesse des Sendepuffers in Byte
     * @param receiveBufferSize Groesse des Empfangspuffers in Byte
     */
    
    public UdpSocket(int port, int sendBufferSize, int receiveBufferSize) throws SocketException {
        socket = new DatagramSocket(port);
        try {
        	socket.setReceiveBufferSize(receiveBufferSize);
        	socket.setSendBufferSize(sendBufferSize);
    
	    	System.out.println("Groesse des Empfangspuffers des Datagram-Sockets: " + socket.getReceiveBufferSize() + " Byte");
	    	System.out.println("Groesse des Sendepuffers des Datagram-Sockets: " + socket.getSendBufferSize() + " Byte");
	    } catch (SocketException e){
	    	log.debug("Socketfehler: " + e);
	    }
    }

    /**
     * Empfangen einer Nachricht ueber UDP
     * 
     * @return Referenz auf Nachricht, die empfangen wurde
     * @param timeout Wartezeit in ms
     * @throws IOException
     */
    public Object receive(int timeout) throws IOException 
    {
    	// Maximale Wartezeit fuer Receive am Socket einstellen
    	
    	try {
    		socket.setSoTimeout(timeout);
    		System.out.println("RECEIVE: Maximale Wartezeit: " + timeout +" ms");
    	}
    	catch (SocketException e) {
    		log.error("RECEIVE: " + "Fehler beim Einstellen der maximalen Wartezeit");
        	throw e;
    	}   
        
        byte[] bytes = new byte[65527];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

        for (int i = 0; i < numberOfRetries; i++) {

        	try {
        		// Blockiert nur, bis Timeout abgelaufen ist
        		socket.receive(packet);
        		log.debug("RECEIVE: Empfangene Datenlaenge:  " + packet.getLength());
        		log.debug("RECEIVE: Empfangene Datenlaenge:  " + packet.getLength());
        		break;
        	}
        	catch (SocketTimeoutException e1) {
        		log.debug("RECEIVE: " + "Socket Timeout " + (i+1));
        		System.out.println("RECEIVE: " + "Socket Timeout " + (i+1));
        		if (i == (numberOfRetries-1)) {
        			// Alle Versuche ausgereizt
        			log.debug("RECEIVE: Wartezeit von " + timeout + " ms abgelaufen");
        			throw e1;
        		}
        	}
        	catch (IOException e2) {
        		log.error("RECEIVE: " + "Fehler beim Empfangen einer PDU ueber UDP");
        		throw e2;
        	}
        }
         
        ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
        ObjectInputStream ois = new ObjectInputStream(bais);
        
        Object pdu;
        try {
        	
        	System.out.println("RECEIVE: " + "Verfuegbare Bytes Inputstream des UDP-Sockets:" + ois.available());
        	
            pdu = ois.readObject();
       
            remoteAddress = packet.getAddress();
            remotePort = packet.getPort();
           
                        
            log.debug("RECEIVE: " + packet.getPort()
                      + "->"
                      + socket.getLocalPort());
                      
        } catch (ClassNotFoundException e) {
            log.error("RECEIVE: " + "ClassNotFoundException:", e);
            return null;
        }
        return pdu;
    }
    
	/**
	 * Senden einer Nachricht ueber UDP
	 * @param remoteAddress: Adresse des Empfaengers
	 * @param remotePort: Port des Empfaengers
	 * @param pdu: Zu sendende PDU
	 * @throws IOException
	 */
    public void send(InetAddress remoteAddress, int remotePort, Object pdu) throws IOException 
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(pdu);
        byte[] bytes = out.toByteArray();
        
        log.debug("SEND: zu sendende Bytes: " + bytes.length);
        
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, remoteAddress, remotePort);
        
        log.debug("SEND: " + packet.getAddress() + ":" + packet.getPort());
                
        try {
        	socket.send(packet);
        }
        catch (IOException e) {
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
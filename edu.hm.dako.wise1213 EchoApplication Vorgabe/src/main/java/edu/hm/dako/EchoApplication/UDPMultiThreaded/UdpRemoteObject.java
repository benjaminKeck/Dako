package edu.hm.dako.EchoApplication.UDPMultiThreaded;

import java.net.InetAddress;

/**
 * Klasse UdpRemoteObject
 * 
 * Diese Klasse repraesentiert eine UDP-Nachricht.
 * 
 * @author Weiss
 */
public class UdpRemoteObject {

	private InetAddress remoteAddress;
	private int remotePort;
	private Object object;
	
	public UdpRemoteObject(){
		
	}
	
	public UdpRemoteObject(InetAddress remoteAddress, int remotePort,
			Object object) {
		super();
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		this.object = object;
	}
	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(InetAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	public int getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}	
}

package edu.hm.dako.EchoApplication.SimpleReliableUdp;

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
	private long startTime;
	private Object object;
	
	public UdpRemoteObject(){
		
	}
	
	public UdpRemoteObject(InetAddress remoteAddress, int remotePort, long startTime,
			Object object) {
		super();
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		this.startTime = startTime;
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

	public long getStartTime() {
		// TODO Auto-generated method stub
		return startTime;
	}	
}

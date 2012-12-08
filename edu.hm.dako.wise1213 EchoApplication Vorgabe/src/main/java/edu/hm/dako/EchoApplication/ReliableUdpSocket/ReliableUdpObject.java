package edu.hm.dako.EchoApplication.ReliableUdpSocket;

import java.io.Serializable;

/**
 * Objekt fuer die zuverlaessige Kommunikation zwischen Client und Server.
 *   
 * @author Weiss
 * 
 * @version 1.0.1
 */

class ReliableUdpObject implements Serializable{

	private static final long serialVersionUID = 1000011111L;
	public long id;
	public Object data;
	boolean ack=false;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public boolean isAck() {
		return ack;
	}
	public void setAck(boolean ack) {
		this.ack = ack;
	}
	
}
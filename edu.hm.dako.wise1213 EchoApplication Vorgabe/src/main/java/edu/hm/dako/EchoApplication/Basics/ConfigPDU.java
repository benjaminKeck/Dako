package edu.hm.dako.EchoApplication.Basics;

import java.io.Serializable;

import edu.hm.dako.EchoApplication.TestAndBenchmarking.UserInterfaceInputParameters.ImplementationType;

/**
 * Klasse ConfigPDU
 *  
 * Dient der Uebertragung einer Config-Nachricht an den Automatic Server (Request und Response)
 * Diese Klasse muss nicht angepasst werden.
 * 
 * @author Rottmüller
 *
 */
public class ConfigPDU implements Serializable
{	 
	private static final long serialVersionUID = -5405627371072481281L;
	
	private String message;
	private String remoteServerAddress;
	private int remoteServerPort;
	private boolean stopServer;
	private boolean startServer;
	private ImplementationType implementationType;
  
	public ConfigPDU()
	{
		message = null;
		remoteServerAddress = "";
		remoteServerPort = 0;
		stopServer = false;
		startServer = false;
		implementationType = null;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isStopServer() {
		return stopServer;
	}

	public String getRemoteServerAddress() {
		return remoteServerAddress;
	}

	public void setRemoteServerAddress(String remoteServerAddress) {
		this.remoteServerAddress = remoteServerAddress;
	}

	public int getRemoteServerPort() {
		return remoteServerPort;
	}

	public void setRemoteServerPort(int remoteServerPort) {
		this.remoteServerPort = remoteServerPort;
	}

	public void setStopServer(boolean stopServer) {
		this.stopServer = stopServer;
	}

	public boolean isStartServer() {
		return startServer;
	}

	public void setStartServer(boolean startServer) {
		this.startServer = startServer;
	}

	public ImplementationType getImplementationType() {
		return implementationType;
	}

	public void setImplementationType(ImplementationType implementationType) {
		this.implementationType = implementationType;
	}
} 
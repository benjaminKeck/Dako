package edu.hm.dako.EchoApplication.Basics;

import java.io.Serializable;

/**
 * Klasse EchoPDU
 *  
 * Dient der Uebertragung einer Echo-Nachricht (Request und Response)
 *  
 * @author Mandl
 *
 */
public class EchoPDU  implements Serializable
{	 
	private static final long serialVersionUID = -6172619032079227583L;
	
	String clientName; 		 // Name des Client-Threads, der den Request absendet
	String serverThreadName; // Name des Threads, der den Request im Server bearbeitet
	String message; 		 // Echo-Nachricht (eigentliche Nachricht in Textform)
	boolean lastRequest;	 // Kennzeichen, ob Client letzte Nachricht sendet. Dieses Kennzeichen
							 // dient dem Server dazu, um festzustellen, ob sich der Client nach der Nachricht beendet
	private long serverTime; // Zeit in Nanosekunden, die der Server benoetigt. Diese
							 // Zeit wird vom Server vor dem Absenden der Response eingetragen
  
	public EchoPDU()
	{
		clientName = null;
		serverThreadName = null;
		message = null;
		serverTime = 0;
		lastRequest = false;
	}
	
	public void setClientName(String name) 
	{
		this.clientName = name;
	}
	
	public void setServerThreadName(String name) 
	{
		this.serverThreadName = name;
	}
	
	public void setMessage(String msg) 
	{
		this.message= msg;
	}
	
	public void setServerTime(long time) 
	{
		this.serverTime = time;
	}
	
	public void setLastRequest (boolean last) 
	{
		this.lastRequest = last;
	}
	
	public String getClientName() 
	{
		return(clientName);
	}
	
	public String getServerThreadName() 
	{
		return(serverThreadName);
	}
	
	public String getMessage() 
	{
		return(message);
	}
	
	public long getServerTime() 
	{
		return(serverTime);
	}
	
	public boolean getLastRequest() 
	{
		return(lastRequest);
	}
	public String getMessageText(int len){
		String t="";
		for(int i=0;i<len; i++)
			t+="M";
		
		return t;
	}
} 
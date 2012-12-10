package edu.hm.dako.EchoApplication.SimpleReliableUdp;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;

/**
 * Klasse ExtEchoPDU
 *  
 * Dient der Uebertragung einer Echo-Nachricht (Request und Response)
 * Abgeleitet aus der EchoPDU, nur um eine PduID ergänzt.
 *  
 * @author Teich
 * @version 1.1
 *
 */
public class ExtEchoPDU  extends EchoPDU //implements Serializable
{	 
	/**
	 * Serialisierungs-ID 
	 */
	private static final long serialVersionUID = 1903197517910158175L;
	
	/**
	 * Feld,um der PDU eine ID zuzuweisen
	 */
	private int pduID;		
	
	public ExtEchoPDU()
	{
		super();
		pduID = 0;
	}
	
	
	public void setPduId (int id) 
	{
		this.pduID = id;
	}
	
	public int getPduID() 
	{
		return(pduID);
	}
} 
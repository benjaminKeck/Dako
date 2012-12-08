package edu.hm.dako.EchoApplication.Rmi;


import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.hm.dako.EchoApplication.Basics.EchoPDU;

/**
 * Remote-Interface fuer Echo-Server 
 * @author Mandl
 */
public interface RMIEchoServerInterface extends Remote
{
	public EchoPDU echo(EchoPDU message) throws RemoteException;
	//public void closeServer(EchoPDU message) throws RemoteException;
}

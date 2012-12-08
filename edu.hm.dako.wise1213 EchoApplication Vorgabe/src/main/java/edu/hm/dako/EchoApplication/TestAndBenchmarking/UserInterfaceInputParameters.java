package edu.hm.dako.EchoApplication.TestAndBenchmarking;


/**
 * Konfigurationsparameter fuer Lasttest 
 * 
 * @author Mandl
 *
 */
public class UserInterfaceInputParameters {

	int numberOfClients; 	   	// Anzahl zu startender Client-Threads	
	int messageLength;		   	// Nachrichtenlaenge
	int clientThinkTime; 	   	// Denkzeit zwischen zwei Requests
	int numberOfMessages;	   	// Anzahl der Nachrichtne pro Client-Thread
								// Typ der Implementierung 
	ImplementationType implementationType; 								
								// Typ der Messung fuer das Messprotokoll 
	MeasurementType measurementType;
	int remoteServerPort;	   	// UDP- oder TCP-Port des Servers, Default: 50000 
	String remoteServerAddress;	// Server-IP-Adresse, Default: "127.0.0.1"
	
	/**
	 * Implementierungsvarianten des Lasttests mit verschiedenen Transportprotokollen
	 * 
	 * @author Mandl
	 */
	public enum ImplementationType {
		TCPSingleThreaded, 
		TCPMultiThreaded,
		UDPSingleThreaded, 
		UDPMultiThreaded,
		LwtrtMultiThreaded,
		RmiMultiThreaded,
		ReliableUdpMultiThreaded;
	}
	
	/**
	 * Konstruktor
	 * Belegung der Inputparameter mit Standardwerten
	 */
	public UserInterfaceInputParameters()
	{
		numberOfClients = 2; 
		clientThinkTime = 1;
		messageLength = 50;	
		numberOfMessages = 5;
		remoteServerPort = 50000; 
		remoteServerAddress = new String("127.0.0.1");
		implementationType = ImplementationType.ReliableUdpMultiThreaded;
		measurementType = MeasurementType.VarThreads;
	}
	
	/**
	 * Abbildung der Implementierungstypen auf Strings
	 * 
	 * @param type Implementierungstyp
	 * 
	 * @return Passender String fuer Implementierungstyp
	 */
	public String mapImplementationTypeToString(ImplementationType type)
	{
		String returnString = null;
		
		switch (type) {		
		case TCPSingleThreaded: 
			returnString = new String("Single-threaded TCP");
			break;		
		case TCPMultiThreaded: 
			returnString = new String("Multi-threaded TCP");
			break;
		case UDPSingleThreaded: 
			returnString = new String("Single-threaded UDP");
			break;			
		case UDPMultiThreaded: 
			returnString = new String("Multi-threaded UDP");
			break;						
		case LwtrtMultiThreaded: 
			returnString = new String("Multi-threaded LWTRT");
			break;
		case RmiMultiThreaded: 
			returnString = new String("Multi-threaded RMI");
			break;	
		case ReliableUdpMultiThreaded: 
			returnString = new String("Multi-threaded ReliableUdp");
			break;			
		default:
			break;
		}
		
		return returnString;
	}

	/**
	 * Typen von unterstuetzten Messungen
	 * @author Mandl
	 *
	 */
	public enum MeasurementType {
		VarThreads,		// Variation der Threadanzahl
		VarMsgLength 	// Variation der Nachrichtenlaenge
	}
	
	/**
	 * Abbildung der Messungstypen auf Strings
	 * 
	 * @param type Messungstyp
	 * 
	 * @return Passender String fuer Messungstyp
	 */
	public String mapMeasurementTypeToString(MeasurementType type)
	{
		String returnString = null;
	
		switch (type) {
		case VarThreads: 
			returnString = new String("Variation der Threadanzahl");
			break;
		case VarMsgLength: 
			returnString = new String("Variation der Nachrichtenlaenge");
			break;
		default:
			break;
		}

		return returnString;
	}
	
	public int getNumberOfClients()
	{
		return numberOfClients;
	}
	
	public void setNumberOfClients(int numberOfClients)
	{
		this.numberOfClients = numberOfClients;
	}
	
	public int getMessageLength()
	{
		return messageLength;
	}
	
	public void setMessageLength(int messageLength)
	{
		this.messageLength = messageLength;
	}
	
	public void getMessageLength(int numberOfClients)
	{
		this.numberOfClients = numberOfClients;
	}
	
	public int getClientThinkTime()
	{
		return clientThinkTime;
	}
	
	public void setClientThinkTime(int clientThinkTime)
	{
		this.clientThinkTime = clientThinkTime;
	}
	
	public int getNumberOfMessages()
	{
		return numberOfMessages;
	}
	
	public void setNumberOfMessages(int numberOfMessages)
	{
		this.numberOfMessages = numberOfMessages;
	}
	
	public ImplementationType getImplementationType()
	{
		return implementationType;
	}
	
	public void setImplementationType(ImplementationType implementationType)
	{
		this.implementationType = implementationType;
	}
	
	public MeasurementType getMeasurementType()
	{
		return measurementType;
	}
	
	public void setMeasurementType(MeasurementType measurementType)
	{
		this.measurementType = measurementType;
	}
	
	public int getRemoteServerPort()
	{
		return remoteServerPort;
	}
	
	public void setRemoteServerPort(int remoteServerPort)
	{
		this.remoteServerPort = remoteServerPort;
	}
		
	public String getRemoteServerAddress()
	{
		return remoteServerAddress;
	}
	
	public void setRemoteServerAddress(String remoteServerAddress)
	{
		this.remoteServerAddress = remoteServerAddress;
	}
}
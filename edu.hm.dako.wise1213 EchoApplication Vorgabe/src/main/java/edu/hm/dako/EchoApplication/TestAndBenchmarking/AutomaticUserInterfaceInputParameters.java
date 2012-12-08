package edu.hm.dako.EchoApplication.TestAndBenchmarking;

import java.util.ArrayList;
import java.util.List;

import edu.hm.dako.EchoApplication.TestAndBenchmarking.UserInterfaceInputParameters.ImplementationType;
import edu.hm.dako.EchoApplication.TestAndBenchmarking.UserInterfaceInputParameters.MeasurementType;


/**
 * Konfigurationsparameter fuer Lasttest des Client
 * Diese Klasse muss nicht angepasst werden. 
 * 
 * @author Rottmüller
 *
 */
public class AutomaticUserInterfaceInputParameters {

	int durchlaeufe;									// Wie oft sollen die jeweiligen Server befragt werden
	int minValue;										// minimaler Wert der Nachrichten oder Clients
	int maxValue;										// maximaler Wert der Nachrichten oder Clients
	int difValue;										// schrittweite mit der der jeweilige Wert der Nachrichten oder Clients erhöht wird
	int maxNumberOfMessages;							// maximale Anzahl an Nachrichten
	int remoteServerPort;	   							// UDP- oder TCP-Port des Servers, Default: 50000 
	String remoteServerAddress;							// Server-IP-Adresse, Default: "127.0.0.1"
	int clientThinkTime; 	   							// Denkzeit zwischen zwei Requests
	int messageLength;		   							// Nachrichtenlaenge
	List<ImplementationType> implementationTypeList;	// Typ der Implementierung 
	List<MeasurementType> measurementTypeList;			// Typ der Messung fuer das Messprotokoll 
	
	// wird erst wieder benötigt, wenn die andere Messmethode genutzt wird.
	//int numberOfClients; 	   							// Anzahl zu startender Client-Threads	
	
	/**
	 * Konstruktor
	 * Belegung der Inputparameter mit Standardwerten
	 */
	public AutomaticUserInterfaceInputParameters()
	{
		implementationTypeList = new ArrayList<ImplementationType>();
		measurementTypeList = new ArrayList<MeasurementType>(); 
	}

	public int getDurchlaeufe() {
		return durchlaeufe;
	}

	public void setDurchlaeufe(int durchlaeufe) {
		this.durchlaeufe = durchlaeufe;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getDifValue() {
		return difValue;
	}

	public void setDifValue(int difValue) {
		this.difValue = difValue;
	}

	public int getMaxNumberOfMessages() {
		return maxNumberOfMessages;
	}

	public void setMaxNumberOfMessages(int maxNumberOfMessages) {
		this.maxNumberOfMessages = maxNumberOfMessages;
	}

	public int getRemoteServerPort() {
		return remoteServerPort;
	}

	public void setRemoteServerPort(int remoteServerPort) {
		this.remoteServerPort = remoteServerPort;
	}

	public String getRemoteServerAddress() {
		return remoteServerAddress;
	}

	public void setRemoteServerAddress(String remoteServerAddress) {
		this.remoteServerAddress = remoteServerAddress;
	}

	public int getClientThinkTime() {
		return clientThinkTime;
	}

	public void setClientThinkTime(int clientThinkTime) {
		this.clientThinkTime = clientThinkTime;
	}

	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public List<ImplementationType> getImplementationTypeList() {
		return implementationTypeList;
	}

	public void setImplementationTypeList(
			List<ImplementationType> implementationTypeList) {
		this.implementationTypeList = implementationTypeList;
	}

	public List<MeasurementType> getMeasurementTypeList() {
		return measurementTypeList;
	}

	public void setMeasurementTypeList(List<MeasurementType> measurementTypeList) {
		this.measurementTypeList = measurementTypeList;
	}
}
package edu.hm.dako.EchoApplication.TestAndBenchmarking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.EchoApplication.Basics.ConfigPDU;
import edu.hm.dako.EchoApplication.TestAndBenchmarking.UserInterfaceInputParameters.ImplementationType;
import edu.hm.dako.EchoApplication.TestAndBenchmarking.UserInterfaceInputParameters.MeasurementType;

/**
 * Automatische Steuerung der Clients für den Lasttest
 * Diese Klasse muss nicht angepasst werden.
 * 
 * @author Rottmüller
 *
 */
public class AutomaticBenchmarkingGuiSimulation implements BenchmarkingClientUserInterface {
	private static Log log = LogFactory.getLog(AutomaticBenchmarkingGuiSimulation.class);
	
	/**
	 * VARIABLE WERTE
	 */
	// sollte mindestens auf 5 stehen, wie in der Studienarbeit beschrieben.
	private final int ANZAHL_DER_DURCHLAEUFE;
	
	// Port auf dem der Server lauscht
	private final int REMOTE_SERVER_PORT; 
	
	// Adresse des Servers als String
	private final String REMOTE_SERVER_ADDRESS;	
	
	/**
	 * FIXE WERTE, DIESE NICHT ÄNDERN
	 */
	private int timeCounter = 0;
	
	// Dieser Wert soll so bleiben, dient der Verzögerung bis die Serverseite den entsprechenden Server
	// gestartet bzw. beendet hat.
	private final int TIMER_VALUE = 10;
		
	private final int CLIENT_THINK_TIME;
	private final int MAX_NUMBER_OF_MESSAGES;
	
	private final int CONFIG_SERVER_PORT = 13000;
	
	// Werte für die Iteration der Threads oder Message Länge
	private final int MIN_VALUE; 
	private final int MAX_VALUE; 
	private final int DIFF_VALUE; 
	
	private final int MESSAGE_LENGTH;	
//	private final int CLIENT_NUMBER = 500;
	
	private final List<ImplementationType> implementationTypeList;
	private final List<MeasurementType> measurementTypeList;
	
	public AutomaticBenchmarkingGuiSimulation() {
		this.ANZAHL_DER_DURCHLAEUFE = 5;
		this.CLIENT_THINK_TIME = 100;
		this.DIFF_VALUE = 100;
		this.MAX_NUMBER_OF_MESSAGES = 100;
		this.MAX_VALUE = 500;
		this.MESSAGE_LENGTH = 50;
		this.MIN_VALUE = 100;
		this.REMOTE_SERVER_ADDRESS = "localhost";
		this.REMOTE_SERVER_PORT = 50000;
		
		this.implementationTypeList = getImplementationTypeList();
		this.measurementTypeList = getMeasurementTypeList();
	}
	
	public AutomaticBenchmarkingGuiSimulation(AutomaticUserInterfaceInputParameters iParm) {
		this.ANZAHL_DER_DURCHLAEUFE = iParm.getDurchlaeufe();
		this.CLIENT_THINK_TIME = iParm.getClientThinkTime();
		this.DIFF_VALUE = iParm.getDifValue();
		this.MAX_NUMBER_OF_MESSAGES = iParm.getMaxNumberOfMessages();
		this.MAX_VALUE = iParm.getMaxValue();
		this.MESSAGE_LENGTH = iParm.getMessageLength();
		this.MIN_VALUE = iParm.getMinValue();
		this.REMOTE_SERVER_ADDRESS = iParm.getRemoteServerAddress();
		this.REMOTE_SERVER_PORT = iParm.getRemoteServerPort();
		
		this.implementationTypeList = iParm.getImplementationTypeList();
		this.measurementTypeList = iParm.getMeasurementTypeList();
	}

	@Override
	public void showStartData(UserInterfaceStartData data) {
		System.out.println("Testbeginn: " + data.getStartTime());	
		System.out.println("Geplante Requests: " + data.getNumberOfRequests());
	}

	@Override
	public void showResultData(UserInterfaceResultData data) {
		System.out.println("Testende: " + data.getEndTime());
		System.out.println("Testdauer in s: " + data.getElapsedTime());	
		
		System.out.println("Gesendete Requests: " + data.getNumberOfSentRequests());
		System.out.println("Anzahl Responses: " + data.getNumberOfResponses());
		System.out.println("Anzahl verlorener Responses: " + data.getNumberOfLostResponses());
		
		System.out.println("Mittlere RTT in ms: " + data.getAvgRTT());
		System.out.println("Maximale RTT in ms: " + data.getMaxRTT());
		System.out.println("Minimale RTT in ms: " + data.getMinRTT());
		System.out.println("Mittlere Serverbearbeitungszeit in ms: " + data.getAvgServerTime());
		
		System.out.println("Maximale Heap-Belegung in MByte: " + data.getMaxHeapSize());
		System.out.println("Maximale CPU-Auslastung in %: " + data.getMaxCpuUsage());
	}
	
	@Override
	public void setMessageLine(String message) {
		System.out.println("*** Meldung: " + message+ " ***");
	}
	
	@Override
	public void addCurrentRunTime (long sec) {
		//TODO Feld Testdauer um sec erhoehen
		timeCounter += sec;
		System.out.println("Laufzeitzaehler: " + timeCounter);
	}
	
	@Override
	public void resetCurrentRunTime () {
		//TODO Feld Testdauer auf 0 setzen
		timeCounter = 0;
	}
	
	/**
	 * main
	 * @param args
	 */
	public static void main(String args[]) 
	{
		System.out.println("Argumentlänge: " + args.length);
		
		List<ImplementationType> implementationTypeListToBenchmark = null;
		
		if (args.length > 0) {
			implementationTypeListToBenchmark = new ArrayList<ImplementationType>();
			
			for (int i = 0; i < args.length; i++) {
				implementationTypeListToBenchmark.add(ImplementationType.valueOf(args[i]));
			}
		}
		PropertyConfigurator.configureAndWatch("log4j.client.properties", 60 * 1000);
		new AutomaticBenchmarkingGuiSimulation().doWork();		
	}
	
	public void start(AutomaticUserInterfaceInputParameters iParm) {
		new AutomaticBenchmarkingGuiSimulation(iParm).doWork();	
	}

	public void doWork()
	{
		// Input-parameter aus GUI
		UserInterfaceInputParameters parm = new UserInterfaceInputParameters();
		
		parm.setRemoteServerAddress(REMOTE_SERVER_ADDRESS);
		parm.setRemoteServerPort(REMOTE_SERVER_PORT);
		
		// hier startet die erste Schleife für den die Art der Messung
		// wird im WiSe 2012 / 2013 nicht beachtet
		for (MeasurementType measurementTypeItem : measurementTypeList) {
			parm.setMeasurementType(measurementTypeItem);
			
			// hier beginnt die Schleife für die jeweiligen Implementierungen
			for (ImplementationType implementationTypeItem : implementationTypeList) {
				parm.setImplementationType(implementationTypeItem);
				
				System.out.println("folgender Server wird gestartet: " + implementationTypeItem.toString());
				
				ConfigPDU configPdu = new ConfigPDU();
				configPdu.setStartServer(true);
				configPdu.setImplementationType(implementationTypeItem);
				configPdu.setRemoteServerAddress(REMOTE_SERVER_ADDRESS);
				configPdu.setRemoteServerPort(REMOTE_SERVER_PORT);
				configPdu.setStopServer(false);
				
				Socket tcpCon;
				try {
					tcpCon = new Socket(REMOTE_SERVER_ADDRESS, CONFIG_SERVER_PORT);
					ObjectInputStream in = new ObjectInputStream(tcpCon.getInputStream());
					ObjectOutputStream out = new ObjectOutputStream(tcpCon.getOutputStream());
						    
					out.writeObject(configPdu);
					out.flush();
						
					configPdu = (ConfigPDU) in.readObject();
						
					System.out.println("Nachricht vom Serverdienst: " + configPdu.getMessage());
					
					out.close();
					tcpCon.close();
				} catch (UnknownHostException e) {
					log.error("Host nicht bekannt: ", e);
				} catch (IOException e) {
					log.error("Abbruch, Senden oder Empfangen von Nachrichten nicht moeglich: ", e);
				} catch (ClassNotFoundException e) {
					log.error("configPdu Klasse nicht gefunden: ", e);
				}
				
				timerForManualAction();
				
				System.out.println("Jetzt gehts los!");
				
				for (int runCount = 0; runCount < ANZAHL_DER_DURCHLAEUFE; runCount++) {
					parm.setClientThinkTime(CLIENT_THINK_TIME);
					parm.setNumberOfMessages(MAX_NUMBER_OF_MESSAGES);
					
					if (measurementTypeItem.equals(MeasurementType.VarThreads)) {
						parm.setMessageLength(MESSAGE_LENGTH);
						
						for (int clients = MIN_VALUE; clients <= MAX_VALUE; clients += DIFF_VALUE) {
							parm.setNumberOfClients(clients);
							
							// Benchmarking-Client instanzieren und Benchmark starten
							BenchmarkingClient benchClient = new BenchmarkingClient();
							benchClient.executeTest(parm, this);		
						}
					}
					
//					if (measurementTypeItem.equals(MeasurementType.VarMsgLength)) {
//						parm.setNumberOfClients(CLIENT_NUMBER);
//						
//						for (int messages = MIN_VALUE; messages <= MAX_VALUE; messages += DIFF_VALUE) {
//							parm.setMessageLength(messages);
//							
//							// Benchmarking-Client instanzieren und Benchmark starten
//							BenchmarkingClient benchClient = new BenchmarkingClient();
//							benchClient.executeTest(parm, this);		
//						}
//					}
				}
				
				System.out.println("Der Server " + implementationTypeItem.toString() + " wird beendet!");
				
				configPdu.setStartServer(false);
				configPdu.setStopServer(true);
				configPdu.setMessage("shutdown");
				
				try {
					tcpCon = new Socket(REMOTE_SERVER_ADDRESS, CONFIG_SERVER_PORT);
					ObjectInputStream in = new ObjectInputStream(tcpCon.getInputStream());
					ObjectOutputStream out = new ObjectOutputStream(tcpCon.getOutputStream());
						    
					out.writeObject(configPdu);
					out.flush();
						
					configPdu = (ConfigPDU) in.readObject();
						
					System.out.println("Nachricht vom Serverdienst: " + configPdu.getMessage());
					
					out.close();
					tcpCon.close();
				} catch (UnknownHostException e) {
					log.error("Host nicht bekannt: ", e);
				} catch (IOException e) {
					log.error("Abbruch, Senden oder Empfangen von Nachrichten nicht moeglich: ", e);
				} catch (ClassNotFoundException e) {
					log.error("configPdu Klasse nicht gefunden: ", e);
				}
				
				timerForManualAction();
			}
		}
		
		// Server Controller beenden mit message = close
		System.out.println("automatische Server Kontrolle beenden:");
		
		ConfigPDU configPdu = new ConfigPDU();
		configPdu.setStartServer(false);
		configPdu.setRemoteServerAddress(REMOTE_SERVER_ADDRESS);
		configPdu.setRemoteServerPort(REMOTE_SERVER_PORT);
		configPdu.setStopServer(false);
		configPdu.setMessage("close");
		
		Socket tcpCon;
		try {
			tcpCon = new Socket(REMOTE_SERVER_ADDRESS, CONFIG_SERVER_PORT);
			ObjectInputStream in = new ObjectInputStream(tcpCon.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(tcpCon.getOutputStream());
				    
			out.writeObject(configPdu);
			out.flush();
				
			configPdu = (ConfigPDU) in.readObject();
				
			System.out.println("Nachricht vom Serverdienst: " + configPdu.getMessage());
			
			out.close();
			tcpCon.close();
		} catch (UnknownHostException e) {
			log.error("Host nicht bekannt: ", e);
		} catch (IOException e) {
			log.error("Abbruch, Senden oder Empfangen von Nachrichten nicht moeglich: ", e);
		} catch (ClassNotFoundException e) {
			log.error("configPdu Klasse nicht gefunden: ", e);
		}
	}

	private void timerForManualAction() {
		for (int timer = TIMER_VALUE; timer > 0; timer -= 2) {
			System.out.println("noch " + timer + " Sekunden!");
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				log.error("Aktion wurde unterbrochen: ", e);
			}
		}
	}
	
	private List<ImplementationType> getImplementationTypeList() {
		List<ImplementationType> resultList = new ArrayList<ImplementationType>();
		
		resultList.add(ImplementationType.TCPMultiThreaded);
		resultList.add(ImplementationType.UDPMultiThreaded);
		resultList.add(ImplementationType.RmiMultiThreaded);
		resultList.add(ImplementationType.ReliableUdpMultiThreaded);
		
		return resultList;
	}

	private List<MeasurementType> getMeasurementTypeList() {
		List<MeasurementType> resultList = new ArrayList<MeasurementType>();
		
		resultList.add(MeasurementType.VarThreads);
		
		return resultList;
	}
}
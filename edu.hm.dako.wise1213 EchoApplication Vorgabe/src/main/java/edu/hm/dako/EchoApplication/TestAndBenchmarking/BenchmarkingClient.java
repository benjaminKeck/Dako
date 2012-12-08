package edu.hm.dako.EchoApplication.TestAndBenchmarking;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.EchoApplication.TestAndBenchmarking.UserInterfaceInputParameters.ImplementationType;


import edu.hm.dako.EchoApplication.Basics.AbstractClientThread;
import edu.hm.dako.EchoApplication.Basics.SharedClientStatistics;
import edu.hm.dako.EchoApplication.ReliableUdpMultiThreaded.ReliableUdpMultiThreadedEchoClientThread;
import edu.hm.dako.EchoApplication.Rmi.RMIEchoClientThread;
import edu.hm.dako.EchoApplication.TCPMultiThreaded.TCPMultiThreadedEchoClientThread;
import edu.hm.dako.EchoApplication.TCPSingleThreaded.TCPSingleThreadedEchoClientThread;
import edu.hm.dako.EchoApplication.UDPMultiThreaded.UDPMultiThreadedEchoClientThread;
import edu.hm.dako.EchoApplication.UDPSingleThreaded.UDPSingleThreadedEchoClientThread;

/**
 * Klasse BenchmarkingClient Basisklasse zum Starten eines Benchmarks
 * 
 * @author Mandl
 */
public class BenchmarkingClient implements BenchmarkingStartInterface {
	private static Log log = LogFactory.getLog(BenchmarkingClient.class);

	// Daten aller Client-Threads zur Verwaltung der Statistik
	SharedClientStatistics sharedData;

	/**
	 * Methode liefert die aktuelle Zeit als String
	 * 
	 * @param cal
	 *            Kalender
	 * @return String mit Zeit
	 */
	private String getCurrentTime(Calendar cal) {
		return (cal.get(Calendar.DAY_OF_MONTH) + "."
				+ (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR)
				+ " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
		// + ":" + cal.get(Calendar.MILLISECOND) );
	}

	@Override
	public void executeTest(UserInterfaceInputParameters parm,
			BenchmarkingClientUserInterface clientGui) {
		clientGui.setMessageLine(parm.mapImplementationTypeToString(parm
				.getImplementationType()) + ": Benchmark gestartet");

		AbstractClientThread clientThreads[] = null;

		// Anzahl aller erwarteten Requests ermitteln
		long numberOfAllMessages = parm.getNumberOfClients()
				* parm.getNumberOfMessages();

		// Gemeinsamen Datenbereich fuer alle Threads anlegen
		sharedData = new SharedClientStatistics(parm.getNumberOfClients(),
				parm.getNumberOfMessages(), parm.getClientThinkTime());

		/**
		 * Startzeit ermitteln
		 */

		long startTime = 0;
		Calendar cal = Calendar.getInstance();
		startTime = cal.getTimeInMillis();
		String startTimeAsString = getCurrentTime(cal);

		/**
		 * Laufzeitzaehler-Thread erzeugen
		 */

		TimeCounterThread timeCounterThread = new TimeCounterThread(clientGui);
		timeCounterThread.start();

		/**
		 * Client-Threads in Abhaengigkeit des Implementierungstyps
		 * instanziieren und starten
		 */
		clientThreads = new AbstractClientThread[parm.getNumberOfClients()];
		for (int i = 0; i < parm.getNumberOfClients(); i++) {
			AbstractClientThread oneClientThread =getClientThreadImplementation(parm.getImplementationType());
			oneClientThread.initialize(parm.getRemoteServerPort(),
					parm.getRemoteServerAddress(), i, parm.getMessageLength(),
					parm.getNumberOfMessages(), parm.getClientThinkTime(),
					sharedData);

			oneClientThread.start();
			clientThreads[i] = oneClientThread;
		}

		/**
		 * Startwerte anzeigen
		 */

		UserInterfaceStartData startData = new UserInterfaceStartData();
		startData.setNumberOfRequests(numberOfAllMessages);
		startData.setStartTime(getCurrentTime(cal));
		clientGui.showStartData(startData);

		clientGui.setMessageLine("Alle " + parm.getNumberOfClients()
				+ " Clients-Threads gestartet");

		/**
		 * Auf das Ende aller Clients warten
		 */

		for (int i = 0; i < parm.getNumberOfClients(); i++) {
			try {
				clientThreads[i].join();
				log.debug("Client " + i + " nach join, beendet");
			} catch (InterruptedException e) {
				log.error("join fuer " + clientThreads[i].getName()
						+ " wurde unterbrochen: ", e);
				e.printStackTrace();
			}
		}

		/**
		 * Laufzeitzaehler-Thread beenden
		 */
		timeCounterThread.stopThread();

		/**
		 * Analyse der Ergebnisse durchfuehren, Statistikdaten berechnen und
		 * ausgeben
		 */
		// sharedData.printStatistic();

		/**
		 * Testergebnisse ausgeben
		 */

		clientGui.setMessageLine("Alle Clients-Threads beendet");

		UserInterfaceResultData resultData = new UserInterfaceResultData();

		resultData.setAvgRTT(sharedData.getAverageRTT() / 1000000);
		resultData.setMaxRTT(sharedData.getMaximumRTT() / 1000000);
		resultData.setMinRTT(sharedData.getMinimumRTT() / 1000000);
		resultData.setAvgServerTime(sharedData.getAverageServerTime()
				/ parm.getNumberOfClients() / 1000000);

		cal = Calendar.getInstance();
		resultData.setEndTime(getCurrentTime(cal));

		long elapsedTimeInSeconds = (cal.getTimeInMillis() - startTime) / 1000;
		resultData.setElapsedTime(elapsedTimeInSeconds);

	
		// TODO CPU-Usage kann momentan noch nicht
		// ermittelt werden (nicht Bestandteil der Studienarbeit)
		resultData.setMaxCpuUsage(0); 
		
		resultData.setMaxHeapSize(sharedData.getMaxHeapSize() / (1024 * 1024));

		resultData.setNumberOfResponses(sharedData
				.getSumOfAllReceivedMessages());
		resultData
				.setNumberOfSentRequests(sharedData.getNumberOfSentRequests());
		resultData.setNumberOfLostResponses(sharedData
				.getNumberOfLostResponses());

		clientGui.showResultData(resultData);
		clientGui.setMessageLine(parm.mapImplementationTypeToString(parm
				.getImplementationType()) + ": Benchmark beendet");

		/**
		 * Datensatz fuer Benchmark-Lauf auf Protokolldatei schreiben
		 */

		sharedData.writeStatisticSet("Benchmarking-EchoApp-Protokolldatei", 
						parm.mapImplementationTypeToString(parm.getImplementationType()), 
						parm.mapMeasurementTypeToString(parm.getMeasurementType()),
						startTimeAsString,
						resultData.getEndTime());

	}

	/*
	 * Liefert die ausgebbaren String fuer einen Implementierungstyp
	 */
	private AbstractClientThread getClientThreadImplementation(
			ImplementationType type) {
		switch (type) {
		case TCPSingleThreaded:
			return new TCPSingleThreadedEchoClientThread();
		case TCPMultiThreaded:
			return new TCPMultiThreadedEchoClientThread();
		case UDPSingleThreaded:
			return new UDPSingleThreadedEchoClientThread();
		case UDPMultiThreaded:
			return new UDPMultiThreadedEchoClientThread();
		case ReliableUdpMultiThreaded:
			return new ReliableUdpMultiThreadedEchoClientThread();
		case RmiMultiThreaded:
			return new RMIEchoClientThread();
		default:
			return null;
		}
	}
}
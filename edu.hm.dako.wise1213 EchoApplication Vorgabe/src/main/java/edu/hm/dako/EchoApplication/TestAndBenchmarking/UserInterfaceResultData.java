package edu.hm.dako.EchoApplication.TestAndBenchmarking;

public class UserInterfaceResultData {

	long numberOfSentRequests;  // Anzahl gesendeter Requests
	long numberOfResponses;		// Anzahl empfangener Responses
	long numberOfLostResponses;	// Anzahl verlorener (nicht empfangener) Responses
	String endTime;				// Testende als Datum/Uhrzeit-String
	long elapsedTime;   		// Testdauer in Sekunden
	long avgRTT;				// Mittlere RTT in ms
	long maxRTT;				// Maximale RTT in ms
	long minRTT;				// Minimale RTT in ms
	long avgServerTime;			// Mittlere Serverbearbeitungszeit in ms
	long maxHeapSize;			// Maximale Heap-Belegung waehrend des Testlaufs in MByte
	long maxCpuUsage;			// Maximale CPU-Auslastung waehrend des Testlaufs in Prozent
	
	
	public long getNumberOfSentRequests()
	{
		return numberOfSentRequests;
	}
	
	public void setNumberOfSentRequests(long numberOfSentRequests)
	{
		this.numberOfSentRequests = numberOfSentRequests;
	}
	
	public long getNumberOfResponses()
	{
		return numberOfResponses;
	}
	
	public void setNumberOfResponses(long numberOfResponses)
	{
		this.numberOfResponses = numberOfResponses;
	}	
	
	public long getNumberOfLostResponses()
	{
		return numberOfLostResponses;
	}
	
	public void setNumberOfLostResponses(long numberOfLostResponses)
	{
		this.numberOfLostResponses = numberOfLostResponses;
	}	
	
	public long getElapsedTime()
	{
		return elapsedTime;
	}
	
	public void setElapsedTime(long elapsedTime)
	{
		this.elapsedTime = elapsedTime;
	}
	
	public String getEndTime()
	{
		return endTime;
	}
	
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}
	
	public long getAvgRTT()
	{
		return avgRTT;
	}
	
	public void setAvgRTT(long avgRTT)
	{
		this.avgRTT = avgRTT;
	}
	
	public long getMaxRTT()
	{
		return maxRTT;
	}
	
	public void setMaxRTT(long maxRTT)
	{
		this.maxRTT = maxRTT;
	}
	
	public long getMinRTT()
	{
		return maxRTT;
	}
	
	public void setMinRTT(long minRTT)
	{
		this.minRTT = minRTT;
	}
	
	public long getAvgServerTime()
	{
		return avgServerTime;
	}
	
	public void setAvgServerTime(long avgServerTime)
	{
		this.avgServerTime = avgServerTime;
	}
	
	public long getMaxHeapSize()
	{
		return maxHeapSize;
	}
	
	public void setMaxHeapSize(long maxHeapSize)
	{
		this.maxHeapSize = maxHeapSize;
	}
	
	public long getMaxCpuUsage()
	{
		return maxCpuUsage;
	}
	
	public void setMaxCpuUsage(long maxCpuUsage)
	{
		this.maxCpuUsage = maxCpuUsage;
	}
}
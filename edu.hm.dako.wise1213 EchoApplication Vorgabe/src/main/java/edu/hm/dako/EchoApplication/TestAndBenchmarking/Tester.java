package edu.hm.dako.EchoApplication.TestAndBenchmarking;

import edu.hm.dako.EchoApplication.TestAndBenchmarking.UserInterfaceInputParameters.ImplementationType;
import junit.framework.Assert;
import edu.hm.dako.EchoApplication.ReliableUdpMultiThreaded.ReliableUdpMultiThreadedEchoServer;
import edu.hm.dako.EchoApplication.Rmi.RMIEchoServer;
import edu.hm.dako.EchoApplication.TCPMultiThreaded.TCPMultiThreadedEchoServer;
import edu.hm.dako.EchoApplication.TCPSingleThreaded.TCPSingleThreadedEchoServer;
import edu.hm.dako.EchoApplication.UDPMultiThreaded.UDPMultiThreadedEchoServer;
import edu.hm.dako.EchoApplication.UDPSingleThreaded.UDPSingleThreadedEchoServer;

public class Tester implements BenchmarkingClientUserInterface {
	
	UserInterfaceResultData data;   // Ergebnisdaten des Tests
	
	public class TestStarterEchoServer extends Thread{
		
		ImplementationType type;
		public TestStarterEchoServer(ImplementationType type){
			this.type=type;
		}
		@Override
		public void run() {
			try {
				switch (type) {

				case TCPSingleThreaded:
					TCPSingleThreadedEchoServer.main(null);
					break;
				case TCPMultiThreaded:
					TCPMultiThreadedEchoServer.main(null);
					break;
				case UDPSingleThreaded:
					UDPSingleThreadedEchoServer.main(null);
					break;
				case UDPMultiThreaded:
					UDPMultiThreadedEchoServer.main(null);
					break;
				case RmiMultiThreaded:
					RMIEchoServer.main(null);
					break;
				case ReliableUdpMultiThreaded:
					ReliableUdpMultiThreadedEchoServer.main(null);
					break;
				default:
					throw new RuntimeException("Unknown type: " + type);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("Server started");
		}
	}
	public void testTcp(){
		ImplementationType type = ImplementationType.TCPSingleThreaded;
		runTests(type);
	}
	public void testTcpMulti(){
		ImplementationType type = ImplementationType.TCPMultiThreaded;
		runTests(type);
	}
	public void testReliableUdpMulti(){
		ImplementationType type = ImplementationType.ReliableUdpMultiThreaded;
		runTests(type);
	}
	
	public void testUdp(){
		ImplementationType type = ImplementationType.UDPSingleThreaded;
		
		runTests(type);
	}
	public void testUdpMulti(){
		ImplementationType type = ImplementationType.UDPMultiThreaded;
		
		runTests(type);
	}
	public void testRmiMultiThreaded(){
		ImplementationType type = ImplementationType.RmiMultiThreaded;
		
		runTests(type);
	}
	public void testLWTRT(){
		ImplementationType type = ImplementationType.LwtrtMultiThreaded;
		
		runTests(type);
	}

	private void runTests(ImplementationType type) {
		TestStarterEchoServer t = new TestStarterEchoServer(type);
		t.start();
		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Input-Parameter
		UserInterfaceInputParameters parm = new UserInterfaceInputParameters();
		int numberOfMessages=100;
		parm.setNumberOfMessages(numberOfMessages);
		int numberOfClients=2000;
		parm.setNumberOfClients(numberOfClients);
		parm.setImplementationType(type);
		
		// Benchmarking-Client instanzieren und Benchmark starten
		
		BenchmarkingClient benchClient = new BenchmarkingClient();
		benchClient.executeTest(parm, this);
		Assert.assertEquals(data.getNumberOfSentRequests(), data.getNumberOfResponses());
		Assert.assertTrue(data.getNumberOfSentRequests()>=numberOfMessages*numberOfClients);
		t.interrupt();
	}	
	
	
	@Override
	public void showStartData(UserInterfaceStartData data) {
		// Auto-generated method stub
		
	}
	@Override
	public void showResultData(UserInterfaceResultData data) {
		this.data=data;
		
	}
	@Override
	public void setMessageLine(String message) {
		// Auto-generated method stub
		
	}
	@Override
	public void resetCurrentRunTime() {
		// Auto-generated method stub
		
	}
	@Override
	public void addCurrentRunTime(long sec) {
		// Auto-generated method stub
	}
}
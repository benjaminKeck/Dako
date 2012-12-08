package edu.hm.dako.EchoApplication.TestAndBenchmarking;

import org.junit.Test;

public class TesterSingleAll extends Tester {

	@Override
	@Test
	public void testRmiMultiThreaded() {
		super.testRmiMultiThreaded();
	}

	@Override
	@Test
	public void testTcp() {
		super.testTcp();
	}

	@Override
	@Test
	public void testUdp() {
		super.testUdp();
	}
	

}

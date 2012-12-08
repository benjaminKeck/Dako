package edu.hm.dako.EchoApplication.Basics;


public abstract class AbstractClientThread extends Thread{

	public abstract void initialize(
			int serverPort,
			String remoteServerAddress, int numberOfClient, int messageLength,
			int numberOfMessages, int clientThinkTime,
			SharedClientStatistics sharedData);



}
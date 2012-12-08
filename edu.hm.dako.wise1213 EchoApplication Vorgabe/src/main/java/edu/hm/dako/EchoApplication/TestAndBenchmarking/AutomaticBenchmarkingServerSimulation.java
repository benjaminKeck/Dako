package edu.hm.dako.EchoApplication.TestAndBenchmarking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.EchoApplication.Basics.ConfigPDU;
import edu.hm.dako.EchoApplication.TestAndBenchmarking.UserInterfaceInputParameters.ImplementationType;

/**
 * Automatische Steuerung der Server für den Lasttest
 * Diese Klasse muss nicht angepasst werden.
 * 
 * @author Rottmüller
 *
 */
public class AutomaticBenchmarkingServerSimulation {
	private static Log log = LogFactory.getLog(AutomaticBenchmarkingServerSimulation.class);
	
	private final int SERVER_PORT = 13000;
	
	private static ServerSocket serverSocketConfig;
	private static ObjectOutputStream outConfig;
	private static ObjectInputStream inConfig;
		
	private Process myStartedServerProcess;
	
	private final String javaExecutableDir;
	private final String workingDir;
	
	public AutomaticBenchmarkingServerSimulation(AutomaticServerInterfaceInputParameters iParm) {
		this.javaExecutableDir = iParm.getJavaExecutableDir();
		this.workingDir = iParm.getWorkingDir();
	}

	public AutomaticBenchmarkingServerSimulation(String javaPfad, String programPfad) {
		this.javaExecutableDir = javaPfad;
		this.workingDir = programPfad;
	}

	public AutomaticBenchmarkingServerSimulation() {
		this.javaExecutableDir = System.getProperty("java.home");
		this.workingDir = System.getProperty("user.dir");
	}

	/**
	 * @param args
	 * 	<br>- erster Paramter: Verzeichnis in dem sich die JAVA Umgebung befindet ohne "bin" Verzeichnis
	 * 	<br>- zweiter Parameter: Verzeichnis in dem sich der Code befindet
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);
		new AutomaticBenchmarkingServerSimulation(args[0], args[1]).doWork();
	}

	public void start(AutomaticServerInterfaceInputParameters iParm) {
		new AutomaticBenchmarkingServerSimulation(iParm).doWork();
	}
	
	public void doWork() {
		Socket ic = null;
		
		try {
			serverSocketConfig = new ServerSocket(SERVER_PORT, 10);
			System.out.println("ServerManager wartet auf Aufträge");
		} catch (IOException e) {
			log.error("Abbruch, Senden oder Empfangen von Nachrichten nicht moeglich: ", e);
		}
		
		while (true) {
		
			try {
				ic = serverSocketConfig.accept();
				
				outConfig = new ObjectOutputStream(ic.getOutputStream());
				inConfig = new ObjectInputStream(ic.getInputStream());
	   			
	  			ConfigPDU configPdu = (ConfigPDU) inConfig.readObject();
	  			
	  			if (configPdu.isStartServer()) {
					new Thread(new StartServerInstance(configPdu.getImplementationType(), javaExecutableDir, workingDir)).start();
					
					configPdu.setMessage("Server " + configPdu.getImplementationType() + " wird gestartet");
				}
	  			
	  			if (configPdu.isStopServer()) {
	  				configPdu.setStartServer(false);
	  				configPdu.setMessage("stoppe Server: " + configPdu.getImplementationType());
	  				
	  				myStartedServerProcess.destroy();
	  				
	  				myStartedServerProcess.destroy();
	  			}
	  			
	  			outConfig.writeObject(configPdu);
  				outConfig.flush();
  				
  				if (configPdu.getMessage().equals("close")) {
					outConfig.close();
					inConfig.close();
					serverSocketConfig.close();
					
					System.exit(0);
				}
					
			} catch (IOException e) {
				log.error("Abbruch, Senden oder Empfangen von Nachrichten nicht moeglich: ", e);
			} catch (ClassNotFoundException e) {
				log.error("configPdu Klasse nicht gefunden: ", e);
			}
		}
	}

    private int launch(String cmdString) throws IOException, InterruptedException {
    	byte[] buffer = new byte[1024];

        myStartedServerProcess = Runtime.getRuntime().exec(cmdString);
        InputStream in = myStartedServerProcess.getInputStream();
        while (true) {
                int r = in.read(buffer);
                if (r <= 0) {
                        break;
                }
                System.out.write(buffer, 0, r);
        }
        return myStartedServerProcess.waitFor();
    }
    
    private class StartServerInstance implements Runnable {

    	private ImplementationType implementationType;
    	private String javaExecutableDir;
    	private String workingDir;
    	
    	public StartServerInstance(ImplementationType implementationType, String javaExecutableDir, String workingDir) {
    		this.implementationType = implementationType;
    		this.javaExecutableDir = javaExecutableDir;
    		this.workingDir = workingDir;
    	}
    	
		@Override
		public void run() {
			String serverClassName = implementationType.name();
			String completeServerClassName = serverClassName + "EchoServer";
			
			if (implementationType.equals(ImplementationType.RmiMultiThreaded)) {
				serverClassName = "Rmi";
				completeServerClassName = "RMIEchoServer";
			}
			
			String serverSimulationPackageName = this.getClass().getPackage().getName();
			String packageString = serverSimulationPackageName.substring(0, serverSimulationPackageName.lastIndexOf("."));
			
			try {
	          String cmdString = javaExecutableDir + "\\bin\\java.exe -cp \"" + workingDir + "\"\\bin;\"" + workingDir + "\"\\lib\\* " + packageString + "." + serverClassName + "." + completeServerClassName;
	          
	          System.out.println("Befehl: " + cmdString);
	          
	          int retValue = launch(cmdString);
	          if (retValue != 0) {
	        	  System.err.println("Error code " + retValue);
	        	  log.debug("Error Code " + retValue + ", aus der Methode run der Klasse " + this.getClass().getName());
	          }
	          System.out.println("OK");
		    } catch (IOException e) {
		    	log.error("Abbruch, Senden oder Empfangen von Nachrichten nicht moeglich: ", e);
		    } catch (InterruptedException e) {
		    	log.error("Aktion wurde unterbrochen: ", e);
		    }
		}
    	
    }
}

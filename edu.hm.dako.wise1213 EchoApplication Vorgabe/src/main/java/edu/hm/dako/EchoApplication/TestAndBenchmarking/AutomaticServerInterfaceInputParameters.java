package edu.hm.dako.EchoApplication.TestAndBenchmarking;



/**
 * Konfigurationsparameter fuer Lasttest des Servers
 * Diese Klasse muss nicht angepasst werden.
 * 
 * @author Rottmüller
 *
 */
public class AutomaticServerInterfaceInputParameters {

	private String javaExecutableDir;	// Java Pfad
	private String workingDir;			// Programm Pfad
	
	public String getJavaExecutableDir() {
		return javaExecutableDir;
	}
	public void setJavaExecutableDir(String javaExecutableDir) {
		this.javaExecutableDir = javaExecutableDir;
	}
	public String getWorkingDir() {
		return workingDir;
	}
	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}
}
/**
 * Sehr einfaches und noch nicht ausgereiftes Swing-basiertes GUI 
 * fuer den Anstoss von Testlaeufen im Benchmarking-Client
 * 
 * TODO Refactoring und Fehlerbereinigung (nicht Bestandteil der Studienarbeit):
 *  - Buttons an der Oberflaeche kleiner machen
 *  - Pruefen, ob alle Eingabeparameter ordnungsgemaess erfasst wurden (Plausibilitaetspruefung)
 * 	- Namensgebung für Variablen verbessern
 * 	- Compiler-Warnungs entfernen
 * 	- ImplementationType und MeasurementType besser erfassen (redundanten Code vermeiden)
 * 	- Problem bei langen Tests - GUI hat nach Fensterwechsel nicht mehr den Fokus - beseitigen
 * 	- Fehlerbereinigung insgesamt
 */
package edu.hm.dako.EchoApplication.TestAndBenchmarking;

import edu.hm.dako.EchoApplication.TestAndBenchmarking.UserInterfaceInputParameters.*;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.*;

import java.awt.Button;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;

import javax.swing.*;

import org.apache.log4j.PropertyConfigurator;

public class BenchmarkingClientGui extends JPanel 
	implements BenchmarkingClientUserInterface, ActionListener {
	
	private long timeCounter = 0; // Zeitzaehler für Testlaufzeit 
	
	private static JFrame f; // Frame fuer Echo-Anwendugs-GUI
	private static JPanel panel;
	
	/**
	 *  GUI-Komponenten
	 */
	
	private JComboBox optionList1;
	private JComboBox optionList2;
	private JTextField one;
	private JFormattedTextField text;
	private JFormattedTextField text1;
	private JFormattedTextField text2;
	private JFormattedTextField text3;
	private JTextField text4;
	private JTextField text5;
	private JFormattedTextField text6;
	private JFormattedTextField text7;
	private JFormattedTextField text8;
	private JFormattedTextField text9;
	private JFormattedTextField text10;
	private JFormattedTextField text11;
	private JFormattedTextField text12;
	private JFormattedTextField text13;
	private JFormattedTextField text14;
	private JFormattedTextField text16;
	private JFormattedTextField text17;
	private JFormattedTextField text18;
	private JFormattedTextField text19;
	private JFormattedTextField text20;
	
	private JTextArea messageArea;
	private JScrollPane scrollPane;
	
	private Button startButton;
	private Button newButton;
	private Button beendenButton;
	
	private static final long serialVersionUID = 100001000L;
	
	public BenchmarkingClientGui() {
		super (new BorderLayout());
	}

	private void initComponents() {
	
		/**
		 * Erzeugen der GUI-Komponenten
		 */
	     String[] optionStrings = {
	    		 "SingleThreaded-TCP",
	    		 "MultiThreaded-TCP",
	    		 "SingleThreaded-UDP",
	    		 "MultiThreaded-UDP",
	    		 "MultiThreaded-UDPReliable",
	    		 "MultiThreaded-LWTRT",
	    		 "MultiThreaded-RMI"};
		 optionList1 = new JComboBox(optionStrings);

		 String[] optionStrings1 = {
				 "Variable Threads",
				 "Variable Length"};
		 optionList2 = new JComboBox(optionStrings1);

		 one = new JTextField();
		 text = new	 JFormattedTextField();
		 text1 = new JFormattedTextField();
		 text2 = new JFormattedTextField();
		 text3 = new JFormattedTextField();
		 text4 = new JTextField();
		 text5 = new JTextField();
		 text6 = new JFormattedTextField();
		 text7 = new JFormattedTextField();
		 text8 = new JFormattedTextField();
		 text9 = new JFormattedTextField();
		 text10 = new JFormattedTextField();
		 text11 = new JFormattedTextField();
		 text12 = new JFormattedTextField();
		 text13 = new JFormattedTextField();
		 text14 = new JFormattedTextField();
		 text16 = new JFormattedTextField();
		 text17 = new JFormattedTextField();
		 text18 = new JFormattedTextField();
		 text19 = new JFormattedTextField();
		 text20 = new JFormattedTextField();
		 
		 // Nachrichtenbereich mit Scrollbar
		 messageArea = new JTextArea("", 5, 100);		 
		 
		 //messageArea.setLineWrap(true);
		 scrollPane = new JScrollPane(messageArea);
		 scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		 // Buttons
		 startButton = new Button("Starten");
		 newButton = new Button("Neu");
		 beendenButton = new Button("Beenden"); 
	}
	
	public static void main(String[] args) {
		
		PropertyConfigurator.configureAndWatch("log4j.client.properties", 60 * 1000);
		
		try{
            //UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
		}
	
        f = new JFrame("Benchmarking Client GUI");
		f.setTitle("Benchmark");
		f.add(new BenchmarkingClientGui());
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JComponent panel = new BenchmarkingClientGui().buildPanel();
        f.getContentPane().add(panel);
		f.pack();
		f.setVisible(true);
	}
		
	/**
	 * buildPanel
	 * Panel anlegen
	 * @return
	 */
	public JComponent buildPanel() {
	   
		initComponents();
		
		// Layout definieren
	    FormLayout layout = new FormLayout(
	                "right:max(40dlu;pref), 3dlu, 70dlu, 7dlu, "
	              + "right:max(40dlu;pref), 3dlu, 70dlu",
	                "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
	                "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
	                "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
	                "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
	                "p, 3dlu, p, 3dlu, " +
	                "p, 3dlu, p, 3dlu, p");
	                
	        panel = new JPanel(layout);
	        panel.setBorder(Borders.DIALOG_BORDER);

	        /*
	         *  Panel mit Labels und Komponenten fuellen
	         */
	    
	        CellConstraints cc = new CellConstraints();
	        panel.add (createSeparator("Eingabeparameter"),  cc.xyw(1,  1, 7));
	        panel.add(new JLabel("Implementierungsstyp"),  cc.xy(1,  3));
			panel.add (optionList1,           cc.xyw(3,  3, 1));
			panel.add (new JLabel("Anzahl Client-Threads"),  cc.xy(5, 3));
			panel.add(one,    cc.xy(7, 3));
			one.setText("10");
			panel.add (new JLabel("Art der Messung"),   cc.xy(1, 5));
			panel.add (optionList2,     cc.xyw(3,5,1));
			panel.add (new JLabel("Anzahl Nachrichten je Client"), cc.xy(5, 5));
			panel.add(text3,   cc.xy(7, 5));
			text3.setText("100");
			panel.add(new JLabel("Serverport"), cc.xy(1, 7));
			panel.add(text4,    cc.xy(3, 7));
			text4.setText("50000");
			panel.add(new JLabel("Denkzeit [ms]"), cc.xy(5, 7));
			panel.add(text5,    cc.xy(7, 7));
			text5.setText("100");
			panel.add(new JLabel("Server-IP-Adresse"), cc.xy(1, 9));
			panel.add(text6,    cc.xy(3, 9));
			text6.setText("localhost");
			panel.add(new JLabel("Nachrichtenlaenge [Byte]"),  cc.xy(5, 9));
			panel.add(text7,  cc.xy(7, 9));
			text7.setText("50");
			
			panel.add(createSeparator("Laufzeiteinstellungen"), cc.xyw(1, 15, 7));
			panel.add(new JLabel ("Geplante Requests"),cc.xy(1, 17));
			panel.add(text9,cc.xy(3, 17));
			text9.setEditable(false);
			panel.add(new JLabel("Testbeginn"), cc.xy(5,17));
			panel.add (text10,cc.xy(7, 17));
			text10.setEditable(false);
			panel.add(new JLabel("Gesendete Requests"),cc.xy(1, 19));
			panel.add(text11,cc.xy(3, 19));
			text11.setEditable(false);
			panel.add(new JLabel("Testende"),cc.xy(5, 19));
			panel.add (text12,cc.xy(7, 19));
			text12.setEditable(false);
			panel.add(new JLabel("Empfangene Responses"),cc.xy(1, 21));
			panel.add (text13,cc.xy(3, 21));
			text13.setEditable(false);
			panel.add (new JLabel("Testdauer [s]"),cc.xy(5, 21));
			panel.add (text14,  cc.xy(7, 21));
			text14.setEditable(false);
			panel.add(createSeparator("Messergebnisse"),cc.xyw(1, 23, 7));
			panel.add (new JLabel("Mittlere RTT [ms]"),cc.xy(1,25));
			panel.add (text16,cc.xy(3, 25));
			text16.setEditable(false);
			panel.add (new JLabel("Mittlere Serverzeit [ms]"),cc.xy(5, 25));
			panel.add (text17,cc.xy(7, 25));
			text17.setEditable(false);
			panel.add (new JLabel("Maximale RTT [ms]"),cc.xy(1,27));
			panel.add (text18,cc.xy(3, 27));
			text18.setEditable(false);
			panel.add (new JLabel("Maximale Heap-Belegung [MByte]"),cc.xy(5,27));
			panel.add (text19,cc.xy(7, 27));
			text19.setEditable(false);
			panel.add (new JLabel("Minimale RTT [ms]"),cc.xy(1,29));
			panel.add (text20,cc.xy(3, 29));
			text20.setEditable(false);
			panel.add(new JLabel("Maximale CPU-Auslastung [%]"),cc.xy(5,29));
			panel.add(text1,cc.xy(7,29));
			text1.setEditable(false);
			panel.add(createSeparator(""),cc.xyw(1,31, 7));
			
			// Meldungsbereich
			
			panel.add(scrollPane,cc.xyw(1, 33,7));	
			
			messageArea.setLineWrap(true);
			messageArea.setWrapStyleWord(true);
			messageArea.setEditable(false);
			messageArea.setCaretPosition(0);
		
			panel.add(createSeparator(""),cc.xyw(1,35, 7));
		
			panel.add (startButton,cc.xyw(2,37,2));   //Starten
			panel.add (newButton,cc.xyw(4,37,2)); 	  //Loeschen
			panel.add (beendenButton,cc.xyw(6,37,2)); //Abbrechen
			
			startButton.addActionListener(this);
			newButton.addActionListener(this);
			beendenButton.addActionListener(this);
	        return panel; 
	}
	    
	/**
	 * actionPerformed
	 * Listener-Methode zur Bearbeitung der Button-Aktionen
	 * Reagiert auf das Betaetigen eines Buttons 
	 * @param e Ereignis
	 */
	//@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		
		//Analysiere Ereignis und fuehre entsprechende Aktion aus
		
		if (e.getActionCommand().equals("Starten"))
		{
			startAction(e);
			startButton.setEnabled(false);
		}
		else if (e.getActionCommand().equals("Neu"))
		{
			newAction(e);
			startButton.setEnabled(true);
		}
		else if (e.getActionCommand().equals("Beenden"))
			finishAction(e);	
	}

	/**
	 * startAction
	 * Aktion bei Betaetigung des "Start"-Buttons ausfuehren
	 * @param e Ereignis
	 */
	private void startAction(ActionEvent e) 
	{	
		// Input-Parameter aus GUI lesen
		UserInterfaceInputParameters iParm = new UserInterfaceInputParameters();
	
		// GUI sammmelt Eingabedaten 
	
		Integer iThinkTime = new Integer(text5.getText());
		System.out.println("Denkzeit: " + iThinkTime+" ms");
		iParm.setClientThinkTime(iThinkTime.intValue()); //!!!!
		
		Integer iServerPort = new Integer(text4.getText());
		System.out.println("Serverport: " + iServerPort);
		iParm.setRemoteServerPort(iServerPort.intValue()); //!!!
	
		Integer iClientThreads = new Integer(one.getText());
		System.out.println("Anzahl Client-Threads:"+ iClientThreads);
		iParm.setNumberOfClients(iClientThreads.intValue()); //!!!
		
		Integer iNumberOfMessages = new Integer(text3.getText());
		System.out.println("Anzahl Nachrichten:"+iNumberOfMessages);
		iParm.setNumberOfMessages(iNumberOfMessages.intValue()); //!!!
			
		Integer iMessageLength = new Integer(text7.getText());
		System.out.println("Nachrichtenlaenge:"+iMessageLength+" Byte");
		iParm.setMessageLength(iMessageLength.intValue()); //!!!
		
		System.out.println("RemoteServerAdress:"+text6.getText());
		iParm.setRemoteServerAddress(text6.getText());
		
		// Benchmarking-Client instanziieren und Benchmark starten
	
		// Eingegebenen Implementierungstyp auslesen
		
		String item1 = (String) optionList1.getSelectedItem();
		System.out.println("Implementierungstyp eingegeben: " + item1);
		
		if (item1 == "SingleThreaded-TCP")
			iParm.implementationType = ImplementationType.TCPSingleThreaded;
		if (item1 == "MultiThreaded-TCP")
			iParm.implementationType = ImplementationType.TCPMultiThreaded;	
		if (item1 == "SingleThreaded-UDP")
			iParm.implementationType = ImplementationType.UDPSingleThreaded;
		if (item1 == "MultiThreaded-UDP")
			iParm.implementationType = ImplementationType.UDPMultiThreaded;
		if (item1 == "MultiThreaded-UDPReliable")
			iParm.implementationType = ImplementationType.ReliableUdpMultiThreaded;
		if (item1 == "MultiThreaded-RMI")
			iParm.implementationType = ImplementationType.RmiMultiThreaded;

		// Eingegebenen Messungstyp auslesen
		
		String item2 = (String) optionList2.getSelectedItem();
		System.out.println("Messungstyp eingegeben: " + item2);
	 
		if (item1 == "Variable Threads")
			iParm.measurementType = MeasurementType.VarThreads;
		if (item1 == "Variable Length")
			iParm.measurementType = MeasurementType.VarMsgLength;

		// Aufruf des Benchmarks

		BenchmarkingClient benchClient = new BenchmarkingClient();
		benchClient.executeTest(iParm, this);
	}
	
	/**
	 * newAction
	 * Aktion bei Betaetigung des "Neu"-Buttons ausfuehren
	 * @param e Ereignis
	 */
	private void newAction(ActionEvent e) 
	{
		/*
		 * Loeschen bzw. initialisieren aller Ergebnisfelder
		 */
		text.setText("");
		one.setText("10"); //  Anzahl Clients
		text1.setText("");
		text2.setText("");
		text3.setText("100"); // Anzahl Nachrichten je Client
		text4.setText("50000"); // Serverport;
		text5.setText("100"); // Denkzeit
		text6.setText("localhost"); // IP-Adresse des Servers
		text7.setText("50"); // Nachrichtenlaenge  in Byte
		text8.setText("");
		text9.setText("");
		text10.setText("");
		text11.setText("");
		text12.setText("");
		text13.setText("");
		text14.setText("");
		text16.setText("");
		text17.setText("");
		text18.setText("");
		text19.setText("");
		text20.setText("");
	}
	
	/**
	 * Aktion bei Betaetigung des "Beenden"-Buttons ausfuehren
	 * @param e Ereignis
	 */
	private void finishAction(ActionEvent e) 
	{	
		setMessageLine("Programm wird beendet...");
		
		// Ein bisschen warten
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// Programm beenden
		System.exit(0);
	}
	
	/**
	 * Schließen des Fensters und Beenden des Programms
	 * @param e
	 */
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
	public void windowOpened(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}

	@Override
	public synchronized void showStartData(UserInterfaceStartData data) 
	{
		String text9String = (new Long(data.getNumberOfRequests())).toString();
		text9.setText(text9String);
		text10.setText(data.getStartTime());

		// Aktualisieren der Ausgabefelder auf dem Bildschirm
		text9.update(text9.getGraphics());
		text10.update(text10.getGraphics());
	}

	@Override
	public synchronized void showResultData(UserInterfaceResultData data) 
	{
		String text11String = (new Long(data.getNumberOfSentRequests())).toString();
		String text13String = (new Long(data.getNumberOfResponses())).toString();
		String text16String = (new Long(data.getAvgRTT())).toString();
		String text17String = (new Long(data.getAvgServerTime())).toString();
		String text18String = (new Long(data.getMaxRTT())).toString();
		String text19String = (new Long(data.getMaxHeapSize())).toString();
		String text20String = (new Long(data.getMinRTT())).toString();
		String text1String = (new Long(data.getMaxCpuUsage())).toString();
		
		text11.setText(text11String);
		text12.setText(data.getEndTime());
		text13.setText(text13String);
		text16.setText(text16String);
		text17.setText(text17String);
		text18.setText(text18String);
		text19.setText(text19String);
		text20.setText(text20String);
		text1.setText(text1String);

		// Aktualisieren des Frames auf dem Bildschirm
		f.update(f.getGraphics());
	}

	@Override
	public synchronized void setMessageLine(String message) {	
		messageArea.append(message + "\n");
		messageArea.update(messageArea.getGraphics());
	}

	@Override
	public synchronized void resetCurrentRunTime() {
		timeCounter = 0;
		String text14String = (new Long(timeCounter)).toString();
		text14.setText(text14String);
		
		// Aktualisieren des Ausgabefeldes auf dem Bildschirm
		text14.update(text14.getGraphics());
	}

	@Override
	public synchronized void addCurrentRunTime(long sec) {
		timeCounter += sec;
		String text14String = (new Long(timeCounter)).toString();
		text14.setText(text14String);
		
		// Aktualisieren des Ausgabefeldes auf dem Bildschirm
		text14.update(text14.getGraphics());
	}
	   	
	private Component createSeparator(String text) {
	        return DefaultComponentFactory.getInstance().createSeparator(text);
   }
}
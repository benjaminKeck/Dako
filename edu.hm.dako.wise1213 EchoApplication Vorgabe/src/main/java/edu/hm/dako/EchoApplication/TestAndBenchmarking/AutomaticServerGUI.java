package edu.hm.dako.EchoApplication.TestAndBenchmarking;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.log4j.PropertyConfigurator;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Server GUI Programm mit dem die möglichen Parameter leicher eingegeben werden können.
 * Diese Klasse muss nicht angepasst werden.
 * 
 * @author Rottmüller
 *
 */
public class AutomaticServerGUI extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6063169838810630897L;
	private static JFrame jFrame; // Frame fuer Echo-Anwendugs-GUI
	private static JPanel jPanel;
	
	
	// GUI Komponenten
	private JFormattedTextField javaPfad;
	private JFormattedTextField programPfad;
	
	private Button startButton;
	private Button newButton;
	private Button beendenButton;
	
	public AutomaticServerGUI() {
		super (new BorderLayout());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configureAndWatch("log4j.client.properties", 60 * 1000);
		
		try{
            //UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
		}
	
		jFrame = new JFrame("Automatic Server GUI");
		jFrame.setTitle("Automatic Server GUI");
		jFrame.add(new AutomaticServerGUI());
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JComponent panel = new AutomaticServerGUI().buildPanel();
        jFrame.getContentPane().add(panel);
        jFrame.pack();
        jFrame.setVisible(true);
	}

	private JComponent buildPanel() {
		initComponents();
		
		// Layout definieren
	    FormLayout layout = new FormLayout(
	                "left:pref, right:max(40dlu;pref), 3dlu, 70dlu, 7dlu, left:pref, right:max(40dlu;pref), 3dlu, 70dlu", // columns
	                "p, 9dlu, p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, p");
	                
	    jPanel = new JPanel(layout);
	    jPanel.setBorder(Borders.DIALOG_BORDER);
	    
	    CellConstraints cc = new CellConstraints();
	    
	    jPanel.add(createSeparator("Eingabeparameter"),  cc.xyw(1, 1, 9));
	    
	    
	    
	    jPanel.add(createSeparator("Pfaddaten"),  cc.xyw(2, 3, 8));
	    
	    jPanel.add(new JLabel("Java Pfad:"), cc.xy(2, 5));
	    jPanel.add(javaPfad, cc.xyw(4, 5, 6));
	    javaPfad.setText(findJavaPfad());
	    
	    jPanel.add(new JLabel("Programm Pfad:"), cc.xy(2, 7));
	    jPanel.add(programPfad, cc.xyw(4, 7, 6));
	    programPfad.setText(findProgramPfad());
	    
	    
	    
	    jPanel.add(createSeparator("Aktionen"),cc.xyw(1, 9, 9));
		
	    jPanel.add (startButton, cc.xyw(3, 11, 2));   //Starten
	    jPanel.add (newButton, cc.xyw(5, 11, 3)); 	  //Loeschen
	    jPanel.add (beendenButton, cc.xyw(8, 11, 2)); //Abbrechen
		
		startButton.addActionListener(this);
		newButton.addActionListener(this);
		beendenButton.addActionListener(this);
		
	    return jPanel; 
	}

	private String findProgramPfad() {
		return System.getProperty("user.dir");
	}

	private String findJavaPfad() {
//		System.out.println(System.getProperty("java.home"));
//		System.out.println(System.getProperty("java.library.path"));
//		System.out.println(System.getProperty("sun.boot.library.path"));
//		System.out.println(System.getProperty("java.class.path"));
		return System.getProperty("java.home");
	}

	private JComponent createSeparator(String text) {
		return DefaultComponentFactory.getInstance().createSeparator(text);
	}

	private void initComponents() {
		// Felder
		javaPfad = new JFormattedTextField();
		programPfad = new JFormattedTextField();
		
		// Buttons
		startButton = new Button("Starten");
		newButton = new Button("Neu");
		newButton.setEnabled(false);
		beendenButton = new Button("Beenden"); 
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Starten")) {
			startButton.setEnabled(false);
			beendenButton.setEnabled(false);
			startAction(e);
			newButton.setEnabled(true);
			beendenButton.setEnabled(true);
		}
		
		if (e.getActionCommand().equals("Neu")) {
			startButton.setEnabled(true);
		}
		
		if (e.getActionCommand().equals("Beenden")) {
			finishAction(e);
		}
	}

	private void startAction(ActionEvent e) {
		// Input-Parameter aus GUI lesen
		AutomaticServerInterfaceInputParameters iParm = new AutomaticServerInterfaceInputParameters();
		
		// GUI sammmelt Eingabedaten 
		System.out.println("JAVA Pfad: " + javaPfad.getText());
		iParm.setJavaExecutableDir(javaPfad.getText());
		
		System.out.println("Programpfad: " + programPfad.getText());
		iParm.setWorkingDir(programPfad.getText());
				
		
		// Benchmarking-Client instanziieren und Benchmark starten
		AutomaticBenchmarkingServerSimulation automaticServer = new AutomaticBenchmarkingServerSimulation();
		automaticServer.start(iParm);
	}

	private void finishAction(ActionEvent e) {
		// Programm beenden
		System.exit(0);
	}
	
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
	
}

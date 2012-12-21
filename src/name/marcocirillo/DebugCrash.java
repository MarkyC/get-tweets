package name.marcocirillo;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DebugCrash {
	
	public static void printDebugInfo(String errMsg, Exception error) {
		// Post crash info to the command line
        System.out.println(errMsg + ": " + error.getMessage());
        System.out.println("Please paste this stackTrace when filing a bug report: ");
		error.printStackTrace();
		
		// Create a window and post crash info to it
		showCrashWindow(errMsg, error);
		
		//System.exit(-1);
	}

	public static void showCrashWindow(String errMsg, Exception error) {
		JFrame crashWindow = new JFrame(Constants.CRASH_WINDOW_TITLE);
		
		// Create textarea to display error message
		JTextArea errorArea = new JTextArea(errMsg + "\n" + error.getMessage(), 10, 10);
		errorArea.setWrapStyleWord(true);
		
		JScrollPane scrollPane = new JScrollPane(errorArea);
		crashWindow.getContentPane().add(scrollPane);
		
		crashWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		crashWindow.setSize(new Dimension(500,300));
		crashWindow.setLocationRelativeTo(null);
		crashWindow.setVisible(true);
		
	}

}

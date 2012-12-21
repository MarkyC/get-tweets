package name.marcocirillo;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
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
		JTextArea errorArea = new JTextArea(errMsg + "\n" + error.getMessage(), 10, 30);
		
		errorArea.setWrapStyleWord(true);
		crashWindow.add(errorArea);
		crashWindow.addWindowListener(new WindowListener() {

			// exit when window is closed
			@Override
			public void windowClosed(WindowEvent arg0) {System.exit(-1);} 

			// Do nothing otherwise
			@Override
			public void windowClosing(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowOpened(WindowEvent arg0) {}
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
		//crashWindow.pack();
		crashWindow.setLocationRelativeTo(null);
		crashWindow.setVisible(true);
		
	}

}

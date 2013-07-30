package name.marcocirillo;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class ProgressWindow extends JFrame {
	
	private static final long serialVersionUID = -4852217962773704720L;
	private int progress;
	private JProgressBar progressBar;
	
	/**
	 * Spawns a new progress window starting from 0% progress
	 * @param username - The username of the twitter user who's tweets are being downloaded
	 * @throws HeadlessException if no display is found.
	 */
	public ProgressWindow(String username) throws HeadlessException {
		// set window properties
		super(Constants.DOWNLOADING_TEXT + " " + username);		// construct with title
		this.setResizable(false);					// do not allow user to resize window
		// do not allow user to close window
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);	
		
		// Build progress bar
		this.progressBar = new JProgressBar();
		this.progressBar.setStringPainted(true);	// Show a completion percentage
		this.progress = 0;							// initial progress is 0
		this.progressBar.setString("" + this.progress + "%");	// initially 0%
		
		// Add components
		this.add(progressBar);
		
		// Beautify
		this.pack();
		this.setSize(400, this.getSize().height);	// set wider
		this.setVisible(true);				// show window
		this.setLocationRelativeTo(null); 	// center window
	}
	
	/**
	 * Updated the progress bar in the window
	 * @param addition the amount to increment the progress bar by
	 */
	public void updateProgress(int addition) {
		this.progress += addition;		// add addition to progress to get new progress
		this.progressBar.setValue(this.progress);	// update the progress bar
		this.progressBar.setString("" + this.progress + "%");	// set completion to [progress]%
	}
	
	/**
	 * Sets the progress bar to the specified value
	 * @param progress - The new value to set the progress bar to.
	 */
	public void setProgress(int progress) {
		this.progress = progress;					// set new progress
		this.progressBar.setValue(this.progress);	// update the progress bar
		this.progressBar.setString("" + this.progress + "%");	// set completion to [progress]%
	}
	

}

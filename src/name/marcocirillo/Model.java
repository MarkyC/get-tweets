package name.marcocirillo;

import java.awt.Desktop;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Model {
	
	private BufferedWriter outputFile;
	private String username;
	private Twitter twitter;
	
	private int maxStatuses;
	private int statusPerPage;
	
	PropertyChangeListener progressListener;

	public Model(String username) {
		this.username = username;		// Set username for this instance
		
		this.outputFile = buildOutWriter(username); // The writer that will write the out file
		
		
		// Set our pagination properties via constants
		this.maxStatuses = Constants.MAX_STATUSES;
		this.statusPerPage = Constants.STATUS_PER_PAGE;
				
		if (outputFile == null) {
			// Should never be reached, but might as well make double sure, right?
			DebugCrash.printDebugInfo("Could not create output file",
										new NullPointerException());
		}
		

		// gets Twitter instance with default credentials
        twitter = new TwitterFactory().getInstance();
        
        if (twitter == null) {
        	DebugCrash.printDebugInfo("Could not get Twitter instance",
					new NullPointerException());
        }
		
		
	}
	
	/**
	 * Returns the specified username this class belongs to
	 * @return the username this class belongs to
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 *  Sets the specified username this class belongs to
	 * @param username - the username this class belongs to
	 */
	public void setUsername(String username) {
		this.username = username;
		
		try {
			this.outputFile.close();
		
		// It's okay if we can't close the other file, the program can still run correctly
		} catch (IOException e) {} 
		
		// rebuild output file based on new username
		this.outputFile = this.buildOutWriter(username);	
	}
	
	/**
	 * Returns the maximum number of statuses retrieveAndWriteStatuses() will print
	 * @return the maximum number of statuses retrieveAndWriteStatuses() will print
	 */
	public int getMaxStatuses() {
		return this.maxStatuses;
	}
	
	/**
	 * Sets the maximum number of statuses retrieveAndWriteStatuses() will print
	 * @param maxStatuses - the maximum number of statuses retrieveAndWriteStatuses() will print
	 */
	public void setMaxStatuses(int maxStatuses) {
		this.maxStatuses = maxStatuses;
	}
	
	/**
	 * Returns the statuses per page retrieveAndWriteStatuses() will retrieve. 
	 * @return the statuses per page retrieveAndWriteStatuses() will retrieve
	 */
	public int getStatusPerPage() {
		return this.statusPerPage;
	}
	
	/**
	 * 
	 * Sets the statuses per page retrieveAndWriteStatuses() will retrieve. 
	 * Setting this too high can lead to unexpected results due to timeout errors.
	 * @return the statuses per page retrieveAndWriteStatuses() will retrieve
	 */
	public void setStatusPerPage(int statusPerPage) {
		this.statusPerPage = statusPerPage;
	}
	
	public boolean userExists() {
		boolean userExists = false;
		try{
		  twitter.showUser(username);
		  userExists = true;
		}catch(TwitterException te) {}
		
		return userExists;
	}
	
	/**
	 * Prints twitter statuses for the specified username this class belongs to
	 */
	public void retrieveAndWriteStatuses() {
		for (int i = 1; i < ((float) (this.maxStatuses/this.statusPerPage)); i++) {
	     	// Get the next 100 statuses from the specified user 
	        List<Status> statuses = null;
	        
	        statuses = this.getStatusPageList(i);	
			
	        if (statuses == null) {
	        	DebugCrash.printDebugInfo("Failed to get status list", 
	        		new NullPointerException());
	        }
	        		 
	        this.writeStatusesToFile(statuses);
	        
	        this.updateProgress((float) this.statusPerPage / this.maxStatuses);
	    }
	    
	  
		this.closeOutputFile();
		this.showUserOutput();
	}

	private void updateProgress(float f) {
		// TODO: make progress dialog
		//this.oldProgress = f;
	}
	
	/** Closes the output file and displays to the user */
	public void finishAndShow() {
		this.closeOutputFile();
		this.showUserOutput();
	}
	
	private void closeOutputFile() {
		try {
			outputFile.flush();
			outputFile.close();
		} catch (IOException e) {
			DebugCrash.printDebugInfo("Could not write to output file", e);
		}
	}

	private void showUserOutput() {
		try {
			Desktop.getDesktop().edit(new File(username + ".txt"));
		} catch (IOException e) {
			DebugCrash.printDebugInfo("Could not open output file", e);
		}
	}

	/**
	 * Writes a List<Status to the output file
	 * @param statuses - statuses to write into the output file
	 */
	public void writeStatusesToFile(List<Status> statuses) {
		for (Status status : statuses) {
        	try {
				outputFile.append(status.getText());
				outputFile.newLine();
			} catch (IOException e) {
				// We can't write to the output file, tell the user something is wrong
	        	DebugCrash.printDebugInfo("Could not write to output file", e);
			}
        }
	}

	/**
	 * Returns List<Status> containing statuses from index to index + statusPerPage
	 * @param index - starting index
	 * @return List<Status> containing statuses from index to index + statusPerPage
	 */
	public List<Status> getStatusPageList(int index) {
		List<Status> statuses = null;
		
		try {
			statuses = twitter.getUserTimeline(username, new Paging(index, this.statusPerPage));
		} catch (TwitterException e) {
			DebugCrash.printDebugInfo("Failed to get timeline", e);
		}
		
		return statuses;
	}


	private BufferedWriter buildOutWriter(String username) {
		BufferedWriter out = null;	// temporarily set writer to null
		
		try {
			// Create output file TODO: make this based on entered username
			out = new BufferedWriter(new FileWriter(username + ".txt"));
		} catch (IOException e) {
			DebugCrash.printDebugInfo("Could not write to output file", e);
		}
		
		return out;
	}
}

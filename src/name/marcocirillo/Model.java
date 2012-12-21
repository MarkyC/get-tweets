package name.marcocirillo;

import java.awt.Desktop;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Model {
	
	private File outputFile;
	private BufferedWriter outputFileWriter;
	private String username;
	private Twitter twitter;
	
	private int maxStatuses;
	private int statusPerPage;
	
	private boolean ignoreRT;
	private boolean ignoreSP;
	
	PropertyChangeListener progressListener;

	public Model(String username) {
		this.username = username;		// Set username for this instance
		
		this.outputFileWriter = buildOutWriter(username); // The writer that will write the out file
		
		// Initially set to not ignore sponsored and retweeted tweets
		this.ignoreRT = false;
		this.ignoreSP = false;
		
		
		// Set our pagination properties via constants
		this.maxStatuses = Constants.MAX_STATUSES;
		this.statusPerPage = Constants.STATUS_PER_PAGE;
				
		if (outputFileWriter == null) {
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
	 * @return true if this Model is set to ignore retweets, false otherwise
	 */
	public boolean isIgnoreRT() {
		return ignoreRT;
	}

	/**
	 * @param ignoreRT - true if this Model is set to ignore retweets, false otherwise
	 */
	public void setIgnoreRT(boolean ignoreRT) {
		this.ignoreRT = ignoreRT;
	}

	/**
	 * @return true if this Model is set to ignore sponsored tweets, false otherwise
	 */
	public boolean isIgnoreSP() {
		return ignoreSP;
	}

	/**
	 * @param ignoreSP - true if this Model is set to sponsored tweets, false otherwise
	 */
	public void setIgnoreSP(boolean ignoreSP) {
		this.ignoreSP = ignoreSP;
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
			this.outputFileWriter.close();
		
		// It's okay if we can't close the other file, the program can still run correctly
		} catch (IOException e) {} 
		
		// rebuild output file based on new username
		this.outputFileWriter = this.buildOutWriter(username);	
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
	    
	  
		this.closeoutputFileWriter();
		this.showUserOutput();
	}

	private void updateProgress(float f) {
		// TODO: make progress dialog
		//this.oldProgress = f;
	}
	
	/** Closes the output file and displays to the user */
	public void finishAndShow() {
		this.closeoutputFileWriter();
		this.showUserOutput();
	}
	
	private void closeoutputFileWriter() {
		try {
			outputFileWriter.flush();
			outputFileWriter.close();
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
				outputFileWriter.append(status.getText());
				outputFileWriter.newLine();
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
		
		statuses = Collections.synchronizedList(statuses);
		
		return statuses;
	}


	private BufferedWriter buildOutWriter(String username) {
		BufferedWriter out = null;	// temporarily set writer to null
		
		this.outputFile = new File(username + ".txt");
		
		// If outputFile exists, let's make a new File so as to not overwrite the tweets
		int i = 1;
		while(this.outputFile.exists()) {
			this.outputFile = new File(username + '-'+ i++ +".txt");
		}
		
		
		try {
			out = new BufferedWriter(new FileWriter(this.outputFile));
		} catch (IOException e) {
			DebugCrash.printDebugInfo("Could not write to output file", e);
		}
		
		return out;
	}

	/**
	 * Attempts to remove retweets from a Status List
	 * @param statuses - The list of statuses to remove the retweets from
	 * @return A new Status List with the retweets removed
	 */
	public static List<Status> removeRT(List<Status> statuses) {
		ListIterator<Status> it = statuses.listIterator(); 
		while(it.hasNext()) {
			Status status = it.next();
			String statusText = status.getText().toLowerCase();
			
			// Remove retweets 
			Pattern p = Pattern.compile("(^|\\s)RT\\b", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(statusText);
			if (m.find()) {
				System.out.println("Attemping to remove retweet: " + statusText);
				it.remove();
			} 
		}
		return statuses;
	}
	
	/** Attempts to remove sponsored ads from a Status List
	 * Currently checks for the following (Case insensitive):
	 * <ul>
	 * <li>#SPON</li>
	 * <li>#SP</li>
	 * </ul>
	 * @param statuses - The list of statuses to remove the sponsored ads from
	 * @return A new Status List with the sponsored ads removed
	 */
	public static List<Status> removeSP(List<Status> statuses) {
		ListIterator<Status> it = statuses.listIterator(); 
		while(it.hasNext()) {
			
			Status status = it.next();
			String statusText = status.getText().toLowerCase();
			
			// Remove any tweets containing #sp (case insensitive)
			Pattern p = Pattern.compile("(^|\\s)#SP\\b", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(statusText);
			if (m.find()) {
				System.out.println("Attemping to remove sponsored ad: " + statusText);
				it.remove();
			} 
			
			// Remove tweets containing #SPON (Case insensitive)
			p = Pattern.compile("(^|\\s)#SPON\\b", Pattern.CASE_INSENSITIVE);
			m = p.matcher(statusText);
			if (m.find()) {
				System.out.println("Attemping to remove sponsored ad: " + statusText);
				it.remove();
			} 
		}
		return statuses;
	}
}

package name.marcocirillo;

import java.awt.Desktop;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

	/**
	 * Attempts to remove conversations from tweets (checks for @ sign)
	 * 
	 * @param statuses
	 *          - The list of statuses to remove the conversations from
	 * @return A new Status List with the conversations removed
	 */
	public static List<Status> removeConversations(List<Status> statuses) {
		ListIterator<Status> it = statuses.listIterator();

		while (it.hasNext()) {
			Status status = it.next();
			String statusText = status.getText()
			    .toLowerCase();

			// Check for conversation by checking for @ sign
			Pattern p = Pattern.compile("(^|\\s)@\\w+", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(statusText);
			if (m.find()) {
				System.out.println("Attemping to remove conversation: " + statusText);
				it.remove();
			}

		}

		return statuses;
	}

	/**
	 * Attempts to remove tweets with links
	 * 
	 * @param statuses
	 *          - The list of statuses to remove the tweets from
	 * @return A new Status List with the tweets with links removed
	 */
	public static List<Status> removeLinks(List<Status> statuses) {
		ListIterator<Status> it = statuses.listIterator();

		while (it.hasNext()) {
			Status status = it.next();
			String statusText = status.getText()
			    .toLowerCase();

			// Check for links
			Matcher m = Pattern.compile(
			    // Magic regex taken from:
			    // stackoverflow.com/questions/163360/regular-expresion-to-match-urls-in-java
			    "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
			    Pattern.CASE_INSENSITIVE)
			    .matcher(statusText);
			if (m.find()) {
				System.out.println("Attemping to remove tweet with link: " + statusText);
				it.remove();
			}

		}

		return statuses;
	}

	/**
	 * Attempts to remove retweets from a Status List
	 * 
	 * @param statuses
	 *          - The list of statuses to remove the retweets from
	 * @return A new Status List with the retweets removed
	 */
	public static List<Status> removeRT(List<Status> statuses) {
		ListIterator<Status> it = statuses.listIterator();
		while (it.hasNext()) {
			Status status = it.next();
			String statusText = status.getText()
			    .toLowerCase();

			if (status.isRetweet()) {
				// Most clients set this field
				it.remove();
			} else {
				// Manually check for retweet if field not set
				Pattern p = Pattern.compile("(^|\\s)RT\\b", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(statusText);
				if (m.find()) {
					System.out.println("Attemping to remove retweet: " + statusText);
					it.remove();
				}
			}
		}

		return statuses;
	}

	/**
	 * Attempts to remove sponsored ads from a Status List Currently checks for
	 * the following (Case insensitive):
	 * <ul>
	 * <li>#SPON</li>
	 * <li>#SP</li>
	 * <li>- sp</li>
	 * </ul>
	 * 
	 * @param statuses
	 *          - The list of statuses to remove the sponsored ads from
	 * @return A new Status List with the sponsored ads removed
	 */
	public static List<Status> removeSP(List<Status> statuses) {
		ListIterator<Status> it = statuses.listIterator();
		while (it.hasNext()) {

			Status status = it.next();
			String statusText = status.getText()
			    .toLowerCase();

			// build matchers to remove tweets (case insensitive)
			Matcher m1 = Pattern.compile("(^|\\s)#SP\\b", // containing #sp
			    Pattern.CASE_INSENSITIVE)
			    .matcher(statusText);
			Matcher m2 = Pattern.compile("(^|\\s)#SPON\\b", // containing #spon
			    Pattern.CASE_INSENSITIVE)
			    .matcher(statusText);
			Matcher m3 = Pattern.compile("(^|\\s)- sp\\b", // containing - sp
			    Pattern.CASE_INSENSITIVE)
			    .matcher(statusText);

			if (m1.find() || m2.find() || m3.find()) {
				System.out.println("Attemping to remove sponsored ad: " + statusText);
				it.remove();
			}
		}
		return statuses;
	}

	private File outputFile;
	private BufferedWriter outputFileWriter;
	private String username;
	private Twitter twitter;
	private int maxStatuses;
	private int statusPerPage;
	private boolean ignoreRT;
	private boolean ignoreSP;
	private boolean ignoreConvo;
	private boolean ignoreLinks;
	private boolean printTime;
	private boolean statusId;
	private boolean inReplyToUserId;
	private boolean inReplyToScreenName;
	private boolean inReplyToStatusId;
	private boolean source;
	private boolean text;
	private String delimiter;
	private View view;

	PropertyChangeListener progressListener;

	public Model(String username, View v) {
		this.username = username; // Set username for this instance
		this.view = v;
		this.outputFileWriter = buildOutWriter(username); // The writer that will
		                                                  // write the out file

		// Initially set to not ignore sponsored and retweeted tweets
		this.ignoreRT = false;
		this.ignoreSP = false;
		this.ignoreConvo = false;
		this.ignoreLinks = false;

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

	private BufferedWriter buildOutWriter(String username) {
		BufferedWriter out = null; // temporarily set writer to null

		this.outputFile = new File(username + ".txt");

		// If outputFile exists, let's make a new File so as to not overwrite the
		// tweets
		int i = 1;
		try {
			while (!this.outputFile.createNewFile()) {
				this.outputFile = new File(username + '-' + i++ + ".txt");
			}
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			DebugCrash.printDebugInfo("Could not create output file", e1);
		}

		try {
			out = new BufferedWriter(new FileWriter(this.outputFile));
		}
		catch (IOException e) {
			DebugCrash.printDebugInfo("Could not write to output file", e);
		}

		return out;
	}

	private void closeoutputFileWriter() {
		try {
			outputFileWriter.flush();
			outputFileWriter.close();
		}
		catch (IOException e) {
			DebugCrash.printDebugInfo("Could not write to output file", e);
		}
	}

	/** Closes the output file and displays to the user */
	public void finishAndShow() {
		this.closeoutputFileWriter();
		this.showUserOutput();
	}

	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * Returns the maximum number of statuses retrieveAndWriteStatuses() will
	 * print
	 * 
	 * @return the maximum number of statuses retrieveAndWriteStatuses() will
	 *         print
	 */
	public int getMaxStatuses() {
		return this.maxStatuses;
	}

	/**
	 * Returns List<Status> containing statuses from index to index +
	 * statusPerPage
	 * 
	 * @param index
	 *          - starting index
	 * @return List<Status> containing statuses from index to index +
	 *         statusPerPage
	 */
	public List<Status> getStatusPageList(int index) {
		List<Status> statuses = null;

		try {
			statuses = twitter.getUserTimeline(username, new Paging(index,
			    this.statusPerPage));
		}
		catch (TwitterException e) {
			DebugCrash.printDebugInfo("Failed to get timeline", e);
		}

		statuses = Collections.synchronizedList(statuses);

		return statuses;
	}

	/**
	 * Returns the statuses per page retrieveAndWriteStatuses() will retrieve.
	 * 
	 * @return the statuses per page retrieveAndWriteStatuses() will retrieve
	 */
	public int getStatusPerPage() {
		return this.statusPerPage;
	}

	/**
	 * Returns the specified username this class belongs to
	 * 
	 * @return the username this class belongs to
	 */
	public String getUsername() {
		return this.username;
	}

	public View getView() {
		return view;
	}

	/**
	 * @return true if this Model is set to ignore conversations, false otherwise
	 */
	public boolean isIgnoreConversations() {
		return ignoreConvo;
	}

	/**
	 * @return true if this Model is set to ignore tweets with links, false
	 *         otherwise
	 */
	public boolean isIgnoreLinks() {
		return ignoreLinks;
	}

	/**
	 * @return true if this Model is set to ignore retweets, false otherwise
	 */
	public boolean isIgnoreRT() {
		return ignoreRT;
	}

	/**
	 * @return true if this Model is set to ignore sponsored tweets, false
	 *         otherwise
	 */
	public boolean isIgnoreSP() {
		return ignoreSP;
	}

	public boolean isInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public boolean isInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public boolean isInReplyToUserId() {
		return inReplyToUserId;
	}

	/**
	 * @return true if this Model is set to print timestamps, false otherwise
	 */
	public boolean isPrintTime() {
		return this.printTime;
	}

	public boolean isSource() {
		return source;
	}

	public boolean isStatusId() {
		return statusId;
	}

	public boolean isText() {
		return text;
	}

	private void printTime(Status status) throws IOException {
		// TODO: allow user to customize the date format
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String date = df.format(status.getCreatedAt());
		outputFileWriter.append(date + delimiter);
	}

	/**
	 * Prints twitter statuses for the specified username this class belongs to
	 */
	public void retrieveAndWriteStatuses() {
		for (int i = 1; i < ((float) (this.maxStatuses / this.statusPerPage)); i++) {
			// Get the next 100 statuses from the specified user
			List<Status> statuses = null;

			statuses = this.getStatusPageList(i);

			if (statuses == null) {
				DebugCrash.printDebugInfo("Failed to get status list",
				    new NullPointerException());
			}

			this.writeStatusesToFile(statuses, (i == 1) ? true : false);

			this.updateProgress((float) this.statusPerPage / this.maxStatuses);
		}

		this.closeoutputFileWriter();
		this.showUserOutput();
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @param removeConvo
	 *          - true if this Model is set to ignore conversations, false
	 *          otherwise
	 */
	public void setIgnoreConversations(boolean ignoreConvo) {
		this.ignoreConvo = ignoreConvo;
	}

	/**
	 * @param removeConvo
	 *          - true if this Model is set to ignore tweets with links, false
	 *          otherwise
	 */
	public void setIgnoreLinks(boolean ignoreLinks) {
		this.ignoreLinks = ignoreLinks;
	}

	/**
	 * @param ignoreRT
	 *          - true if this Model is set to ignore retweets, false otherwise
	 */
	public void setIgnoreRT(boolean ignoreRT) {
		this.ignoreRT = ignoreRT;
	}

	/**
	 * @param ignoreSP
	 *          - true if this Model is set to ignore sponsored tweets, false
	 *          otherwise
	 */
	public void setIgnoreSP(boolean ignoreSP) {
		this.ignoreSP = ignoreSP;
	}

	public void setInReplyToScreenName(boolean inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	public void setInReplyToStatusId(boolean inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	public void setInReplyToUserId(boolean inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	/**
	 * Sets the maximum number of statuses retrieveAndWriteStatuses() will print
	 * 
	 * @param maxStatuses
	 *          - the maximum number of statuses retrieveAndWriteStatuses() will
	 *          print
	 */
	public void setMaxStatuses(int maxStatuses) {
		this.maxStatuses = maxStatuses;
	}

	/**
	 * @param printTime
	 *          - true if this Model is set to print timestamps, false otherwise
	 */
	public void setPrintTime(boolean printTime) {
		this.printTime = printTime;
	}

	public void setSource(boolean source) {
		this.source = source;
	}

	public void setStatusId(boolean statusId) {
		this.statusId = statusId;
	}

	/**
	 * 
	 * Sets the statuses per page retrieveAndWriteStatuses() will retrieve.
	 * Setting this too high can lead to unexpected results due to timeout errors.
	 * 
	 * @return the statuses per page retrieveAndWriteStatuses() will retrieve
	 */
	public void setStatusPerPage(int statusPerPage) {
		this.statusPerPage = statusPerPage;
	}

	public void setText(boolean text) {
		this.text = text;
	}

	/**
	 * Sets the specified username this class belongs to
	 * 
	 * @param username
	 *          - the username this class belongs to
	 */
	public void setUsername(String username) {
		this.username = username;

		try {
			this.outputFileWriter.close();

			// It's okay if we can't close the other file, the program can still run
			// correctly
		}
		catch (IOException e) {
		}

		// rebuild output file based on new username
		this.outputFileWriter = this.buildOutWriter(username);
	}

	public void setView(View view) {
		this.view = view;
	}

	private void showUserOutput() {
		try {
			Desktop.getDesktop()
			    .edit(outputFile);
		}
		catch (IOException e) {
			DebugCrash.printDebugInfo("Could not open output file", e);
		}
	}

	private void updateProgress(float f) {
		// TODO: make progress dialog
		// this.oldProgress = f;
	}

	public boolean userExists() {
		boolean userExists = false;
		try {
			twitter.showUser(username);
			userExists = true;
		}
		catch (TwitterException te) {
		}

		return userExists;
	}

	/**
	 * Writes a List<Status to the output file
	 * 
	 * @param statuses
	 *          - statuses to write into the output file
	 */
	public void writeStatusesToFile(List<Status> statuses, boolean headers) {
		if (statuses.size() > 0) {
			try {
				if (printTime)
					outputFileWriter.append("Time" + delimiter);
				if (statusId)
					outputFileWriter.append("StatusId" + delimiter);
				if (inReplyToUserId)
					outputFileWriter.append("InReplyToUserId" + delimiter);
				if (inReplyToScreenName)
					outputFileWriter.append("InReplyToScreenName" + delimiter);
				if (inReplyToStatusId)
					outputFileWriter.append("InReplyToStatusId" + delimiter);
				if (source)
					outputFileWriter.append("Source" + delimiter);
				if (text)
					outputFileWriter.append("Text" + delimiter);
				outputFileWriter.newLine();
			}
			catch (Exception e) {

			}
			for (Status status : statuses) {
				try {
					String statusText = status.getText();
					if (!statusText.equals("")) {
						// status not blank, write to file
						statusText = statusText.replaceAll("\\r\\n|\\r|\\n", " ");
						if (printTime)
							printTime(status);
						if (statusId)
							outputFileWriter.append(status.getId() + delimiter);
						if (inReplyToUserId)
							outputFileWriter.append(status.getInReplyToUserId() + delimiter);
						if (inReplyToScreenName)
							outputFileWriter.append(status.getInReplyToScreenName()
							    + delimiter);
						if (inReplyToStatusId)
							outputFileWriter.append(status.getInReplyToStatusId() + delimiter);
						if (source)
							outputFileWriter.append(status.getSource() + delimiter);
						if (text)
							outputFileWriter.append(statusText + delimiter);
						outputFileWriter.newLine();
					}
				}
				catch (IOException e) {
					// We can't write to the output file, tell the user something is wrong
					DebugCrash.printDebugInfo("Could not write to output file", e);
				}
			}
		}
	}
}

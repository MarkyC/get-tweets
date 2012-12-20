package name.marcocirillo;

public class GetTweets {

	public static void main(String[] args) {
		/*View v = new View(Constants.WINDOW_TITLE);
		v.setVisible(true);
		v.setSize(v.getPreferredSize());
		v.setLocationRelativeTo(null);		// center window
		*/
		String username = "google";
		Model m = new Model(username);
		m.retrieveAndWriteStatuses();
		
	}


}

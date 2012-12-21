package name.marcocirillo;

import javax.swing.SwingUtilities;

public class GetTweets {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        createAndShowGUI();
		    }
		});
		
		/*String username = "nonexistent username";
		Model m = new Model(username);
		m.retrieveAndWriteStatuses();*/
		
	}

	private static void createAndShowGUI() {
		View v = new View(Constants.WINDOW_TITLE);
		v.setVisible(true);
		v.setSize(v.getPreferredSize());
		v.setResizable(false);
		v.setLocationRelativeTo(null);		// center window
	}


}

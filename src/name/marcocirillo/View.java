package name.marcocirillo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class View extends JFrame {
	
	/** Generated Serial Version ID */
	private static final long serialVersionUID = -3036977808237399813L;

	// Main GUI components
	//private JLabel usernameLabel;
	private JTextField usernameField;
	private JButton submitButton;
	
	// Holds user defined values to remove or keep retweets or sponsored ads
	private boolean removeRT;
	private boolean removeSP;
	

	public View(String title) throws HeadlessException {
		super(title);		// invoke super constructor
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set OS look and feel
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e)	{} 	// Leave LaF as default Java LaF on error
		
		// build and set the menu bar
		this.setJMenuBar(this.buildMenu());
		
		// Build main program components
		//this.usernameLabel = new JLabel(Constants.USERNAME_TEXT);
		this.usernameField = new JTextField("", 10);
		this.submitButton = new JButton(Constants.GET_TWEETS);
		
		// Keep retweets and sponsored ads in the file
		this.removeRT = false;
		this.removeSP = false;
		
		// Set up listener
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Model currentUser = new Model(usernameField.getText());
				currentUser.setIgnoreRT(removeRT);
				currentUser.setIgnoreSP(removeSP);
				
				if (currentUser.userExists()) {
					new ModelThread(currentUser).start();
				} else {
					printError("Invalid Username");
					usernameField.setText("");
				}
				
				
			}
		});
		
		// Build main panel and set layout and border
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), // border style
				Constants.USERNAME_TEXT));			// border title
		
		// Beautify components by making font larger
		Font font = usernameField.getFont();
		font = new Font(font.getFamily(), font.getStyle(), 20);
		Font smallerFont = (new Font(font.getFamily(), font.getStyle(), 14));
		//usernameLabel.setFont(smallerFont);
		usernameField.setFont(font);
		submitButton.setFont(smallerFont);
		
		// beautify the submit button
		JPanel submitButtonPanel = new JPanel();
		submitButtonPanel.setLayout(new BoxLayout(submitButtonPanel, BoxLayout.X_AXIS));
		submitButtonPanel.add(Box.createHorizontalGlue());
		submitButtonPanel.add(submitButton);
		submitButtonPanel.add(Box.createHorizontalGlue());
		
		// Add components to main panel
		//mainPanel.add(usernameLabel);
		mainPanel.add(usernameField);
		mainPanel.add(submitButtonPanel);
		//mainPanel.add(submitButton);
		
		// Add main panel to this content pane
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		this.getContentPane().add(this.buildSettingsPanel(), BorderLayout.EAST);
		
		
	}

	/**
	 * Builds a panel to hold the settings of the program, 
	 * such as "remove RT's" and "remove sponsored ads".
	 * @return A JPanel that allows the user to change settings
	 */
	private JPanel buildSettingsPanel() {
		JPanel settingsPanel = new JPanel();	// The panel that will hold our settings
		
		// Build checkbox components
		JCheckBox rtBox = new JCheckBox(Constants.REMOVE_RT_TEXT, false);
		JCheckBox spBox = new JCheckBox(Constants.REMOVE_SP_TEXT, false);
		
		// Set up action listeners for checkboxes
		rtBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox rt = (JCheckBox) e.getSource();
				
				rt.setSelected(!removeRT);	// toggle selection
			}
		});
		
		spBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox sp = (JCheckBox) e.getSource();
				
				sp.setSelected(!removeSP);	// toggle selection
			}
		});
		
		// Set layout for panel
		settingsPanel.setLayout(new BoxLayout(settingsPanel,BoxLayout.Y_AXIS));
		settingsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), // Border style
				Constants.SETTINGS_TEXT));			// Border title
		
		// add components to panel
		settingsPanel.add(rtBox);
		settingsPanel.add(spBox);
				
		return settingsPanel;
	}


	/** Builds a menu for this View */
	private JMenuBar buildMenu() {
		// Build the menu bar
		JMenuBar menuBar = new JMenuBar();
		
		// Build the menu bar's menu's
		JMenu fileMenu = new JMenu(Constants.FILE_TEXT);
		JMenu helpMenu = new JMenu(Constants.HELP_TEXT);
		
		// Build menu items
		JMenuItem exitMenuItem = new JMenuItem(Constants.EXIT_TEXT);
		JMenuItem aboutMenuItem = new JMenuItem(Constants.ABOUT_TEXT);
		
		// Add listeners to menu item's
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);		// The exit menu item causes the program to exit
			}
		});
		
		// Add menu item's to menu's
		fileMenu.add(exitMenuItem);
		helpMenu.add(aboutMenuItem);
		
		// Add menu's to menu bar
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		
		return menuBar;
		
	}
	
	public void printError(String string) {
		JOptionPane.showMessageDialog(this, string, "Error", JOptionPane.ERROR_MESSAGE);
	}

}

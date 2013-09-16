package name.marcocirillo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private View instance;
	// Main GUI components
	//private JLabel usernameLabel;
	private JTextField usernameField;
	private JButton submitButton;
	
	// Holds user defined values to remove or keep retweets or sponsored ads
	private boolean removeRT;
	private boolean removeSP;
	private boolean removeConvo;
	private boolean removeLinks;
	private boolean printTime;
	private boolean inReplyToScreenName;
	private boolean inReplyToStatusId;
	private boolean inReplyToUserId;
	private boolean source;
	private boolean statusId;
	private boolean text;
	private String delimiter;
	

	public View(String title) throws HeadlessException {
		super(title);		// invoke super constructor
		this.instance = this;
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
		this.removeRT 		= false;
		this.removeSP	 	= false;
		this.removeConvo 	= false;
		this.removeLinks 	= false;
		this.printTime 		= true;
		this.inReplyToScreenName = false;
		this.inReplyToStatusId = false;
		this.inReplyToUserId = false;
		this.source = false;
		this.statusId = false;
		this.text = true;
		this.delimiter = "§";
		
		// Set up listener
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Model currentUser = new Model( usernameField.getText(), instance);
				currentUser.setIgnoreRT( removeRT );
				currentUser.setIgnoreSP( removeSP );
				currentUser.setIgnoreConversations( removeConvo );
				currentUser.setIgnoreLinks( removeLinks );
				currentUser.setPrintTime( printTime );
				currentUser.setInReplyToScreenName(inReplyToScreenName);
				currentUser.setInReplyToStatusId(inReplyToStatusId);
				currentUser.setInReplyToUserId(inReplyToUserId);
				currentUser.setSource(source);
				currentUser.setStatusId(statusId);
				currentUser.setText(text);
				currentUser.setDelimiter(delimiter);
				
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
		JLabel delimiterLabel = new JLabel(Constants.DELIMITER_TEXT);
		final JTextField delimiterTextField = new JTextField("§");
		JCheckBox rtBox		= new JCheckBox( Constants.REMOVE_RT_TEXT, false );
		JCheckBox spBox 	= new JCheckBox( Constants.REMOVE_SP_TEXT, false );
		JCheckBox convoBox 	= new JCheckBox( Constants.REMOVE_CONVERSATION_TEXT, false );
		JCheckBox linkBox 	= new JCheckBox( Constants.REMOVE_LINK_TEXT, false );
		JCheckBox timeBox 	= new JCheckBox( Constants.PRINT_TIME_TEXT, true);
		JCheckBox inReplyToScreenNameBox 	= new JCheckBox( Constants.IN_REPLY_TO_SCREEN_NAME_TEXT, false);
		JCheckBox inReplyToStatusIdBox 	= new JCheckBox( Constants.IN_REPLY_TO_STATUS_ID_TEXT, false);
		JCheckBox inReplyToUserIdBox 	= new JCheckBox( Constants.IN_REPLY_TO_USER_ID_TEXT, false);
		JCheckBox sourceBox 	= new JCheckBox( Constants.SOURCE_TEXT, false);
		JCheckBox statusIdBox 	= new JCheckBox( Constants.STATUS_ID_TEXT, false);
		JCheckBox textBox 	= new JCheckBox( Constants.TEXT_TEXT, true);
		
		// Set up action listeners for checkboxes
		rtBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeRT = !removeRT;
				( ( JCheckBox ) e.getSource() ).setSelected( removeRT );	// toggle selection
			}
		});
		
		spBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeSP = !removeSP;
				( ( JCheckBox ) e.getSource() ).setSelected( removeSP );	// toggle selection
			}
		});
		
		convoBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeConvo = !removeConvo;
				( ( JCheckBox ) e.getSource() ).setSelected( removeConvo );	// toggle selection
			}
		});
		
		linkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeLinks = !removeLinks;
				( ( JCheckBox ) e.getSource() ).setSelected( removeLinks );	// toggle selection
			}
		});
		
		timeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				printTime = !printTime;
				( ( JCheckBox ) e.getSource() ).setSelected( printTime );	// toggle selection
			}
		});
		
		inReplyToScreenNameBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inReplyToScreenName = !inReplyToScreenName;
				( ( JCheckBox ) e.getSource() ).setSelected( inReplyToScreenName );	// toggle selection
			}
		});
		
		inReplyToStatusIdBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inReplyToStatusId = !inReplyToStatusId;
				( ( JCheckBox ) e.getSource() ).setSelected( inReplyToStatusId );	// toggle selection
			}
		});
		
		inReplyToUserIdBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inReplyToUserId = !inReplyToUserId;
				( ( JCheckBox ) e.getSource() ).setSelected( inReplyToUserId );	// toggle selection
			}
		});
		
		sourceBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				source = !source;
				( ( JCheckBox ) e.getSource() ).setSelected( source );	// toggle selection
			}
		});
		
		statusIdBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				statusId = !statusId;
				( ( JCheckBox ) e.getSource() ).setSelected( statusId );	// toggle selection
			}
		});
		
		textBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				text = !text;
				( ( JCheckBox ) e.getSource() ).setSelected( text );	// toggle selection
			}
		});
		
		delimiterTextField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				delimiter = delimiterTextField.getText();
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				delimiter = delimiterTextField.getText();
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				delimiter = delimiterTextField.getText();
			}
		});
		
		
		// Set layout for panel
		//settingsPanel.setLayout(new BoxLayout(settingsPanel,BoxLayout.Y_AXIS));
		settingsPanel.setLayout( new java.awt.GridLayout( 7, 2 ) ); // 6 rows, 2 columns
		settingsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), // Border style
				Constants.SETTINGS_TEXT));			// Border title
		
		// add components to panel
		settingsPanel.add(delimiterLabel);
		settingsPanel.add(delimiterTextField);
		settingsPanel.add(rtBox);
		settingsPanel.add(spBox);
		settingsPanel.add(convoBox);
		settingsPanel.add(linkBox);
		settingsPanel.add(timeBox);
		settingsPanel.add(inReplyToScreenNameBox);
		settingsPanel.add(inReplyToStatusIdBox);
		settingsPanel.add(inReplyToUserIdBox);
		settingsPanel.add(sourceBox);
		settingsPanel.add(statusIdBox);
		settingsPanel.add(textBox);
				
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

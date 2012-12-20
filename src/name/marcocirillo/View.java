package name.marcocirillo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class View extends JFrame {
	
	/** Generated Serial Version ID */
	private static final long serialVersionUID = -3036977808237399813L;

	JLabel usernameLabel;
	
	JTextField usernameField;
	
	JButton submitButton;

	public View(String title) throws HeadlessException {
		super(title);		// invoke super constructor
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set OS look and feel
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e)	{} 	// Leave LaF as default Java LaF on error
		
		// build and set the menu bar
		this.setJMenuBar(this.buildMenu());
		
		// Build main program components
		this.usernameLabel = new JLabel(Constants.USERNAME_TEXT);
		this.usernameField = new JTextField();
		this.submitButton = new JButton(Constants.GET_TWEETS);
		
		// Set up listener
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Do nothing for now
			}
		});
		
		// Build main panel and set layout
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		// Adjust component sizes
		Dimension d = new Dimension();
		d.width = this.getLargestComponentWidth();
		
		// Use the calculated largest width and the components preferred height to 
		// adjust component preferred size
		usernameLabel.setPreferredSize(new Dimension(d.width,
					usernameLabel.getPreferredSize().height));
		usernameField.setPreferredSize(new Dimension(d.width,
				usernameLabel.getPreferredSize().height));
		submitButton.setPreferredSize(new Dimension(d.width,
				usernameLabel.getPreferredSize().height));
		
		// Add components to main panel
		mainPanel.add(usernameLabel);
		mainPanel.add(usernameField);
		mainPanel.add(submitButton);
		
		// Add main panel to this content pane
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
	}

	/** Returns the largest width between usernameLabel, usernameField 
	 * and submitButton.
	 */
	private int getLargestComponentWidth() {
		Dimension d = new Dimension();
		
		d.width = this.usernameLabel.getPreferredSize().width;
		
		if (this.usernameField.getPreferredSize().width > d.width) {
			d.width = this.usernameField.getPreferredSize().width;
		} else if (this.submitButton.getPreferredSize().width > d.width) {
			d.width = this.submitButton.getPreferredSize().width;
		}
		
		return d.width;
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

}

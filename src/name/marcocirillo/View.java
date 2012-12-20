package name.marcocirillo;

import java.awt.BorderLayout;
import java.awt.Font;
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
import javax.swing.JOptionPane;
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
				Model currentUser = new Model(usernameField.getText());
				
				if (currentUser.userExists()) {
					new ModelThread(currentUser).start();
				} else {
					printError("Invalid Username");
					usernameField.setText("");
				}
				
				
			}
		});
		
		// Build main panel and set layout
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		Font font = usernameField.getFont();
		font = new Font(font.getFamily(), font.getStyle(), 20);
		usernameLabel.setFont(font);
		usernameField.setFont(font);
		submitButton.setFont(font);
		
		// Add components to main panel
		mainPanel.add(usernameLabel);
		mainPanel.add(usernameField);
		mainPanel.add(submitButton);
		
		// Add main panel to this content pane
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		
	}

	/*private int getDisplayedSize(JComponent comp) {
		return comp.getPreferredSize().height + 
				comp.getInsets().top +
				comp.getInsets().left +
				comp.getInsets().bottom +
				comp.getInsets().right ;
	}*/

	/** Returns the largest width between usernameLabel, usernameField 
	 * and submitButton.
	 *//*
	private int getLargestComponentWidth() {
		Dimension d = new Dimension();
		
		d.width = this.usernameLabel.getPreferredSize().width;
		
		if (this.usernameField.getPreferredSize().width > d.width) {
			d.width = this.usernameField.getPreferredSize().width;
		} else if (this.submitButton.getPreferredSize().width > d.width) {
			d.width = this.submitButton.getPreferredSize().width;
		}
		
		return d.width;
	}*/

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

/* 
 * Java LibUsb-Win32 wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.usbView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;

import ch.ntb.usb.LibusbWin;
import ch.ntb.usb.Usb_Bus;

public class UsbView extends JFrame {

	private static final long serialVersionUID = 4693554326612734263L;

	private static final int APP_WIDTH = 600, APP_HIGHT = 800;

	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu commandsMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem updateMenuItem = null;
	private JTree usbTree = null;
	private JSplitPane jSplitPane = null;

	private JTextArea jPropertiesArea = null;
	
	UsbTreeModel treeModel;

	/**
	 * This is the default constructor
	 */
	public UsbView() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getJJMenuBar());
		this.setSize(APP_WIDTH, APP_HIGHT);
		this.setContentPane(getJContentPane());
		this.setTitle("USB View");
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (commandsMenu == null) {
			commandsMenu = new JMenu();
			commandsMenu.setText("Commands");
			commandsMenu.add(getUpdateMenuItem());
			commandsMenu.add(getExitMenuItem());
		}
		return commandsMenu;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUpdateMenuItem() {
		if (updateMenuItem == null) {
			updateMenuItem = new JMenuItem();
			updateMenuItem.setText("Update");
			updateMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_F5, 0, true));
			updateMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// open bus
							LibusbWin.usb_init();
							LibusbWin.usb_find_busses();
							LibusbWin.usb_find_devices();

							Usb_Bus bus = LibusbWin.usb_get_busses();
							if (bus != null) {
								treeModel.fireTreeStructureChanged(bus);
							}
						}
					});
		}
		return updateMenuItem;
	}

	/**
	 * This method initializes usbTree
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getUsbTree() {
		if (usbTree == null) {
			// open bus
			LibusbWin.usb_init();
			LibusbWin.usb_find_busses();
			LibusbWin.usb_find_devices();

			Usb_Bus bus = LibusbWin.usb_get_busses();

			treeModel = new UsbTreeModel(bus, jPropertiesArea);
			usbTree = new JTree(treeModel);
			usbTree.addTreeSelectionListener(treeModel);
		}
		return usbTree;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setContinuousLayout(true);
			jSplitPane.setDividerLocation(APP_HIGHT / 2);
			jSplitPane.setBottomComponent(getJPropertiesArea());
			jSplitPane.setTopComponent(getUsbTree());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jPropertiesArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJPropertiesArea() {
		if (jPropertiesArea == null) {
			jPropertiesArea = new JTextArea();
		}
		return jPropertiesArea;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		UsbView application = new UsbView();
		application.setVisible(true);
	}

}

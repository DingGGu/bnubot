/**
 * This file is distributed under the GPL 
 * $Id$
 */

package net.bnubot.bot.gui.main;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.bnubot.bot.database.Database;
import net.bnubot.bot.gui.AboutWindow;
import net.bnubot.bot.gui.GlobalConfigurationFrame;
import net.bnubot.bot.gui.GuiEventHandler;
import net.bnubot.bot.gui.database.DatabaseAccountEditor;
import net.bnubot.bot.gui.database.DatabaseRankEditor;
import net.bnubot.bot.gui.icons.BNetIcon;
import net.bnubot.bot.gui.icons.IconsDotBniReader;
import net.bnubot.core.Profile;
import net.bnubot.settings.ConnectionSettings;
import net.bnubot.util.Out;
import net.bnubot.vercheck.CurrentVersion;
import net.bnubot.vercheck.VersionCheck;

public class GuiDesktop extends JFrame {
	private static final long serialVersionUID = -7144648179041514994L;
	private static final ArrayList<GuiEventHandler> guis = new ArrayList<GuiEventHandler>();
	private static GuiEventHandler selectedGui = null;
	private static final JTabbedPane tabs = new JTabbedPane();
	private static final JMenuBar menuBar = new JMenuBar();
	private static final GuiDesktop instance = new GuiDesktop();
	private static TrayIcon tray = null;
	
	private GuiDesktop() {
		super();
		setTitle();
		initializeGui();
		initializeSystemTray();
        setVisible(true);
	}
	
	private void initializeGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setContentPane(tabs);
		setSize(800, 500);
		
		tabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JTabbedPane jtp = (JTabbedPane)e.getSource();
				JPanel jp = (JPanel)jtp.getSelectedComponent();
				for(GuiEventHandler gui : guis) {
					if(jp != gui.getFrame())
						continue;
					
					// Swap out the menus
					if(selectedGui != null)
						selectedGui.getMenuBar().setVisible(false);
					gui.getMenuBar().setVisible(true);
					
					// Store the selected GUI
					selectedGui = gui;
					
					// Set the title to the title for the selected GUI
					setTitle();
					
					break;
				}
			}
		});
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if(e.getModifiers() != InputEvent.CTRL_MASK)
					return false;
				
				if(e.getID() != KeyEvent.KEY_RELEASED)
					return false;
				
				int idx;
				switch(e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					idx = tabs.getSelectedIndex() - 1;
					break;
				case KeyEvent.VK_RIGHT:
					idx = tabs.getSelectedIndex() + 1;
					break;
				default:
					return false;
				}
				
				if(idx < 0)
					idx = tabs.getTabCount() - 1;
				else if(idx >= tabs.getTabCount())
					idx = 0;
				
				tabs.setSelectedIndex(idx);
				return true;
			}
		});
        
		menuBar.setOpaque(true);
		{
			JMenu menu = new JMenu("File");
			{
				JMenuItem menuItem = new JMenuItem("New Profile");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							ConnectionSettings.numBots++;
							ConnectionSettings cs = new ConnectionSettings(ConnectionSettings.numBots);
							Thread.yield();
							Profile.add(cs);
						} catch(Exception ex) {
							Out.exception(ex);
						}
					} });
				menu.add(menuItem);
				
				menuItem = new JMenuItem("Settings");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						new GlobalConfigurationFrame().setVisible(true);
					} });
				menu.add(menuItem);
				
				menuItem = new JMenuItem("Exit");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);
			
			menu = new JMenu("Database");
			{
				JMenuItem menuItem = new JMenuItem("Rank editor");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						Database d = Database.getInstance();
						if(d != null)
							new DatabaseRankEditor(d);
						else
							Out.error(GuiDesktop.class, "There is no database initialized.");
					} });
				menu.add(menuItem);
				
				menuItem = new JMenuItem("Account editor");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						Database d = Database.getInstance();
						if(d != null)
							new DatabaseAccountEditor(d);
						else
							Out.error(GuiDesktop.class, "There is no database initialized.");
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);
		
			menu = new JMenu("Debug");
			{
				JMenuItem menuItem = new JMenuItem("Show Icons");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						IconsDotBniReader.showWindow();
					} });
				menu.add(menuItem);
				
				menuItem = new JMenuItem((Out.isDebug() ? "Dis" : "En") + "able debug logging");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						Out.setDebug(!Out.isDebug());
						
						JMenuItem jmi = (JMenuItem)event.getSource();
						jmi.setText((Out.isDebug() ? "Dis" : "En") + "able debug logging");
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);
			
			menu = new JMenu("Help");
			{
				JMenuItem menuItem = new JMenuItem("Complain about scrollbars");
				menu.add(menuItem);
				
				menu.addSeparator();
				
				menuItem = new JMenuItem("Check for updates");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						try {
							VersionCheck.checkVersion();
						} catch (Exception e) {
							Out.exception(e);
						}
					} });
				menu.add(menuItem);
				
				menuItem = new JMenuItem("About");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						new AboutWindow();
					} });
				menu.add(menuItem);
			}
			menuBar.add(menu);
			
			menu = new JMenu();
			menu.setVisible(false);
			menuBar.add(menu);
		}
		setJMenuBar(menuBar);
	}

	private void initializeSystemTray() {
		if(tray != null)
			return;
		
		try {
			if(!SystemTray.isSupported())
				throw new NoClassDefFoundError();
		} catch(NoClassDefFoundError e) {
			Out.info(GuiEventHandler.class, "System tray is not supported");
			return;
		}
		
		Image image = Toolkit.getDefaultToolkit().getImage("tray.gif");
		
		PopupMenu pm = new PopupMenu("title");
		{
			MenuItem mi = new MenuItem("BNU-Bot " + CurrentVersion.version().toString());
			mi.setEnabled(false);
			pm.add(mi);
			
			pm.addSeparator();
			
			mi = new MenuItem("Hide/show");
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GuiDesktop f = GuiDesktop.getInstance();
					f.setVisible(!f.isVisible());
					if(f.isVisible())
						f.toFront();
				}
			});
			pm.add(mi);
			
			mi = new MenuItem("Exit");
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			pm.add(mi);
		}
		
		tray = new TrayIcon(image);
		tray.setPopupMenu(pm);
		tray.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 1) {
					instance.setVisible(!instance.isVisible());
					if(instance.isVisible())
						instance.toFront();
				}
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		tray.setImageAutoSize(true);
		
		try {
			SystemTray.getSystemTray().add(tray);
		} catch(AWTException e) {
			Out.exception(e);
		}
		
		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				if((e.getNewState() & java.awt.Frame.ICONIFIED) != 0) {
					instance.setVisible(false);
					instance.setState(e.getNewState() & ~java.awt.Frame.ICONIFIED);
				}
			}
		});
	}
	
	public static GuiDesktop getInstance() {
		return instance;
	}
	
	public static TrayIcon getTray() {
		return tray;
	}
	
	public static void add(GuiEventHandler geh) {
		geh.getMenuBar().setVisible(false);
		menuBar.add(geh.getMenuBar());
		
		guis.add(geh);
		tabs.addTab(geh.toString(), geh.getFrame());
	}
	
	private void setTitle() {
		String title = "BNU-Bot " + CurrentVersion.version();
		if(selectedGui != null) {
			title += " - ";
			title += selectedGui.toString();
		}
		setTitle(title);
		
		if(tray != null)
			tray.setToolTip(title);
	}
	
	public static void setTitle(GuiEventHandler geh, int product) {
		instance.setTitle();
		
		Icon icon = null;
		for(BNetIcon element : IconsDotBniReader.getIcons()) {
			if(element.useFor(0, product)) {
				icon = element.getIcon();
				break;
			}
		}
		
		JPanel component = geh.getFrame();
		for(int i = 0; i < tabs.getTabCount(); i++) {
			if(component == tabs.getComponentAt(i)) {
				tabs.setTitleAt(i, geh.toString());
				tabs.setIconAt(i, icon);
				geh.getMenuBar().setIcon(icon);
				break;
			}
		}
	}
}
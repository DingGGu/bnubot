package bnubot.bot.gui.components;

import java.awt.FlowLayout;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.*;

import bnubot.bot.gui.ColorScheme.ColorScheme;
import bnubot.bot.gui.icons.BNetIcon;
import bnubot.bot.gui.icons.IconsDotBniReader;
import bnubot.core.Connection;
import bnubot.core.StatString;
import bnubot.core.bncs.ProductIDs;

@SuppressWarnings("serial")
public class UserList extends JPanel {
	private class UserInfo {
		int flags;
		int priority;
		int ping;
		StatString statstr;
		JLabel label;
	}
	
	private Hashtable<String, UserInfo> users = null;
	private Box b = null;
	private ColorScheme cs = null;
	Connection c = null;
	
	public UserList(ColorScheme cs, Connection c) {
		super(new FlowLayout(FlowLayout.LEFT));
		this.users = new Hashtable<String, UserInfo>();
		this.cs = cs;
		this.c = c;
		setBackground(cs.getBackgroundColor());
		b = new Box(BoxLayout.Y_AXIS);
		add(b);
	}
	
	public void clear() {
		Enumeration<UserInfo> e = users.elements();
		while(e.hasMoreElements()) {
			UserInfo ui = e.nextElement();
			b.remove(ui.label);
			ui.label = null;
		}
		users.clear();
		validate();
	}

	private static final int PRIORITY_BLIZZARD_REP = 5;
	private static final int PRIORITY_BNET_REP = 4;
	private static final int PRIORITY_OPERATOR= 3;
	private static final int PRIORITY_SPEAKER = 2;
	private static final int PRIORITY_BIZZARD_GUEST = 1;
	private static final int PRIORITY_NORMAL = 0;
	private int getPrioByFlags(int flags) {
		if((flags & 0x01) != 0)	return PRIORITY_BLIZZARD_REP;
		if((flags & 0x08) != 0)	return PRIORITY_BNET_REP;
		if((flags & 0x02) != 0)	return PRIORITY_OPERATOR;
		if((flags & 0x04) != 0)	return PRIORITY_SPEAKER;
		if((flags & 0x40) != 0)	return PRIORITY_BIZZARD_GUEST;
		return PRIORITY_NORMAL;
	}
	
	private int getInsertPosition(int priority) {
		for(int i = 0; i < b.getComponentCount(); i++) {
			JLabel lbl = (JLabel)b.getComponent(i);
			UserInfo ui = users.get(lbl.getText());
			int pCurrent = getPrioByFlags(ui.flags);
			
			if(priority > pCurrent)
				return i;
		}
		return b.getComponentCount();
	}
	
	public void showUser(String user, int flags, int ping, StatString statstr) {
		UserInfo ui = users.get(user);
		if(ui == null) {
			ui = new UserInfo();
			ui.flags = flags;
			ui.ping = ping;
			ui.statstr = statstr;
			ui.priority = getPrioByFlags(flags);
		}
		if(ui.label == null) {
			ui.label = new JLabel(user);
			ui.label.setForeground(cs.getUserNameListColor(flags));
			b.add(ui.label, getInsertPosition(ui.priority));
		}
		
		//Check if the user's flags updated
		if(ui.flags != flags) {
			//They did; order the list appropriately

			int newPriority = getPrioByFlags(flags);
			if(ui.priority != newPriority) {
				ui.priority = newPriority;
				b.remove(ui.label);
				b.add(ui.label, getInsertPosition(newPriority));
			}
			
			ui.flags = flags;
			
			ui.label.setForeground(cs.getUserNameListColor(flags));
		}
		ui.ping = ping;
		ui.statstr = statstr;
				
		Icon icon = null;
		int product = ui.statstr.getProduct();
		int specialIcon = ui.statstr.getIcon();
		if(specialIcon == product)
			specialIcon = 0;
		
		BNetIcon[] icons = IconsDotBniReader.getIcons();
		boolean keepThisIcon = false;
		for(int i = 0; i < icons.length; i++) {
			//Look for 
			if(icons[i].useFor(flags, specialIcon)) {
				keepThisIcon = true;
				icon = icons[i].getIcon();
				break;
			}
			if(icons[i].useFor(flags, product)) {
				icon = icons[i].getIcon();
			}
		}
		
		if(!keepThisIcon) {
			if(true) { //(product == c.getProductID()) {
				switch(product) {
				case ProductIDs.PRODUCT_STAR:
				case ProductIDs.PRODUCT_SEXP:
				case ProductIDs.PRODUCT_W2BN:
					icons = IconsDotBniReader.getLegacyIcons();
					break;
				case ProductIDs.PRODUCT_WAR3:
					icons = IconsDotBniReader.getIconsWAR3();
					break;
				case ProductIDs.PRODUCT_W3XP:
					icons = IconsDotBniReader.getIconsW3XP();
					break;
				default:
					icons = null;
					break;
				}
				
				if(icons != null) {
					switch(product) {
					case ProductIDs.PRODUCT_STAR:
					case ProductIDs.PRODUCT_SEXP:
						int w = Math.max(Math.min(statstr.getWins(), 10), 0);
						icon = icons[w].getIcon();
						int r = statstr.getLadderRank();
						if(r > 0) {
							if(r == 1)
								icon = icons[IconsDotBniReader.LEGACY_LADDERNUM1].getIcon();
							else
								icon = icons[IconsDotBniReader.LEGACY_LADDER].getIcon();
						}
						break;

					case ProductIDs.PRODUCT_W2BN:
						w = Math.max(Math.min(statstr.getWins(), 10), 0);
						icon = icons[IconsDotBniReader.LEGACY_W2BNWIN + w].getIcon();
						
						r = statstr.getLadderRank();
						if(r > 0) {
							if(r == 1)
								icon = icons[IconsDotBniReader.LEGACY_LADDERNUM1].getIcon();
							else
								icon = icons[IconsDotBniReader.LEGACY_LADDER].getIcon();
						}
						break;
						
					default:
						if(icons != null)
							for(int i = 0; i < icons.length; i++) {
								if(icons[i].useFor(flags, specialIcon)) {
									keepThisIcon = true;
									icon = icons[i].getIcon();
									break;
								}
							}
						break;
					}
				}
			}
		}
		
		if(icon != null)
			ui.label.setIcon(icon);
		
		users.put(user, ui);
		validate();
	}
	
	public void removeUser(String user) {
		UserInfo ui = users.get(user);
		if(ui != null) {
			b.remove(ui.label);
			ui.label = null;
			users.remove(user);
			validate();
		} else {
			System.err.println("Attempted to remove a user that was not in the UserList: " + user);
		}
	}

}

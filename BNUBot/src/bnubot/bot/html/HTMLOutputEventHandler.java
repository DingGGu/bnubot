package bnubot.bot.html;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.SwingUtilities;

import bnubot.bot.EventHandler;
import bnubot.core.Connection;

public class HTMLOutputEventHandler implements EventHandler {
	private class UserInfo {
		String user;
		int flags;
		int ping;
		String statstr;
	}
	String channel;
	boolean generationNeeded;
	Runnable writeUserListRunnable = null;
	
	Hashtable<String, UserInfo> users;

	public void bnetConnected() {
		users = new Hashtable<String, UserInfo>();
		File f = new File("html");
		f.mkdir();
	}
	
	public void bnetDisconnected() {
		users.clear();
		writeUserList();
	}

	public void channelJoin(String user, int flags, int ping, String statstr) {
		UserInfo ui = new UserInfo();
		users.put(user, ui);
		ui.user = user;
		ui.flags = flags;
		ui.ping = ping;
		ui.statstr = statstr;
		
		writeUserList();
	}
	
	public void channelLeave(String user, int flags, int ping, String statstr) {
		users.remove(user);
	}
	
	public void channelUser(String user, int flags, int ping, String statstr) {
		UserInfo ui = users.get(user);
		if(ui == null) {
			ui = new UserInfo();
			users.put(user, ui);
		}
		ui.user = user;
		ui.flags = flags;
		ui.ping = ping;
		ui.statstr = statstr;
		
		writeUserList();
	}
	
	public void initialize(Connection c) {}
	public void joinedChannel(String channel) {
		this.channel = channel;
		users.clear();
	}
	public void recieveChat(String user, int flags, int ping, String text) {}
	public void recieveEmote(String user, int flags, int ping, String text) {}
	public void recieveError(String text) {}
	public void recieveInfo(String text) {}
	public void whisperRecieved(String user, int flags, int ping, String text) {}
	public void whisperSent(String user, int flags, int ping, String text) {}
	
	private String getIcon(String product, int flags) {
		if((flags & 0x01) != 0)	return "blizrep";
		if((flags & 0x08) != 0)	return "bnetrep";
		if((flags & 0x02) != 0)	return "op";
		if((flags & 0x04) != 0)	return "spkr";
		if((flags & 0x40) != 0)	return "blizguest";
		
		if((flags & 0x20) != 0)	return "squelch";
		
		return product;
	}
	
	private String strReverse(String in) {
		String out = "";
		for(int i = in.length() - 1; i >= 0; i--)
			out += in.charAt(i);
		return out;
	}
	
	private void writeUserList() {
		if(writeUserListRunnable == null)
			writeUserListRunnable = new Runnable() {
				public void run() {
					try {
						if(generationNeeded == false)
							return;
						
						File f = new File("html/userlist.html");
						if(f.exists())
							f.delete();
						f.createNewFile();
					
						String out = "<table>";
						
						out += "<tr><td colspan=\"4\"><b>" + channel + "</b> (" + users.values().size() + ")</td></tr>";
						
						Enumeration<UserInfo> en = users.elements();
						while(en.hasMoreElements()) {
							UserInfo ui = en.nextElement();
							
							String product = ui.statstr;
							if(product.length() >= 4)
								product = strReverse(product.substring(0, 4));
							else
								product = "none";
							product = getIcon(product, ui.flags);
							
							out += "<tr>";
							out += "<td><img src=\"images/" + product + ".jpg\"></td>";
							out += "<td>" + ui.user + "</td>";
							out += "<td>" + ui.ping + "ms</td>";
							out += "</tr>";
						}
			
						DataOutputStream fos = new DataOutputStream(new FileOutputStream(f));
						fos.write(out.getBytes());
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			};
		
		generationNeeded = true;
		SwingUtilities.invokeLater(writeUserListRunnable);
	}
}

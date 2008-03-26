/**
 * This file is distributed under the GPL
 * $Id$
 */

package net.bnubot.bot;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.swing.SwingUtilities;

import net.bnubot.bot.gui.colors.ColorScheme;
import net.bnubot.core.Connection;
import net.bnubot.core.EventHandler;
import net.bnubot.core.clan.ClanMember;
import net.bnubot.core.friend.FriendEntry;
import net.bnubot.settings.GlobalSettings;
import net.bnubot.util.BNetUser;
import net.bnubot.util.HexDump;
import net.bnubot.util.Out;
import net.bnubot.util.StatString;

public class HTMLOutputEventHandler implements EventHandler {
	// TODO Allow the user to customize TimeZone
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT-05");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static {
		sdf.setTimeZone(TIME_ZONE);
	}
	private boolean generationNeeded = false;
	private Runnable writeUserListRunnable = null;
	private final ColorScheme cs = ColorScheme.getColors();

	public void bnetConnected(Connection source) {}
	
	public void bnetDisconnected(Connection source) {
		writeUserList(source);
	}

	public void titleChanged(Connection source) {}

	public void channelJoin(Connection source, BNetUser user) {
		writeUserList(source);
		
		if(GlobalSettings.displayJoinParts)
			append(source,
				user.toString() + " has joined the channel" + user.getStatString().toString() + ".",
				cs.getChannelColor());
	}
	
	public void channelLeave(Connection source, BNetUser user) {
		writeUserList(source);
		
		if(GlobalSettings.displayJoinParts)
			append(source,
				user.toString() + " has left the channel.",
				cs.getChannelColor());
	}
	
	public void channelUser(Connection source, BNetUser user) {
		writeUserList(source);
		
		if(GlobalSettings.displayChannelUsers)
			append(source,
				user + user.getStatString().toString() + ".",
				cs.getChannelColor());
	}
	
	public void initialize(Connection source) {
		checkFolder("logs");
		checkFolder("logs/" + source.getProfile().getName());
	}
	public void disable(Connection source) {}

	private void checkFolder(String fName) {
		File f = new File(fName);
		if(!f.exists())
			f.mkdir();
		if(!f.isDirectory())
			Out.fatalException(new Exception(fName + " is not a directory!"));
	}
	
	public void joinedChannel(Connection source, String channel) {
		append(source,
			"Joining channel " + channel + ".", cs.getChannelColor());
	}
	public void recieveError(Connection source, String text) {}
	public void recieveInfo(Connection source, String text) {}
	public void recieveDebug(Connection source, String text) {}
	
	private String getIcon(int product, int icon, int flags) {
		if((flags & 0x01) != 0)	return "blizrep";
		if((flags & 0x08) != 0)	return "bnetrep";
		if((flags & 0x02) != 0)	return "op";
		if((flags & 0x04) != 0)	return "spkr";
		if((flags & 0x40) != 0)	return "blizguest";
		
		if((flags & 0x20) != 0)	return "squelch";
		
		if(icon != 0)
			return HexDump.DWordToPretty(icon);
		return HexDump.DWordToPretty(product);
	}
	
	private void writeUserList(final Connection source) {
		if(writeUserListRunnable == null)
			writeUserListRunnable = new Runnable() {
				public void run() {
					if(!generationNeeded)
						return;
					generationNeeded = false;
					
					try {
						File f = new File("logs/userlist_" + source.getProfile().getName() + ".html");
						DataOutputStream fos = new DataOutputStream(new FileOutputStream(f));
						List<BNetUser> users = source.getUsers();
						
						fos.write("<table><tr><td colspan=\"4\"><b>".getBytes());
						String channel = source.getChannel();
						if(channel != null)
							fos.write(channel.getBytes());
						fos.write("</b> (".getBytes());
						fos.write(Integer.toString(users.size()).getBytes());
						fos.write(")</td></tr>".getBytes());

						for(BNetUser ui : users) {
							StatString ss = ui.getStatString();
							String product = getIcon(ss.getProduct().getDword(), ss.getIcon(), ui.getFlags());
							
							fos.write("<tr>".getBytes());
							fos.write(("<td><img src=\"images/" + product + ".jpg\"></td>").getBytes());
							fos.write(("<td>" + ui.toString(GlobalSettings.bnUserToStringUserList) + "</td>").getBytes());
							fos.write(("<td>" + ui.getPing() + "ms</td>").getBytes());
							fos.write("</tr>".getBytes());
						}
						fos.write("</table>".getBytes());
						fos.close();
					} catch (Exception e) {
						Out.exception(e);
					}
				}
			};
		
		generationNeeded = true;
		SwingUtilities.invokeLater(writeUserListRunnable);
	}

	public void friendsList(Connection source, FriendEntry[] entries) {}
	public void friendsUpdate(Connection source, FriendEntry friend) {}
	public void friendsAdd(Connection source, FriendEntry friend) {}
	public void friendsPosition(Connection source, byte oldPosition, byte newPosition) {}
	public void friendsRemove(Connection source, byte entry) {}
	
	public void logonRealmEx(Connection source, int[] MCPChunk1, int ip, int port, int[] MCPChunk2, String uniqueName) {}
	public void queryRealms2(Connection source, String[] realms) {}

	public boolean parseCommand(Connection source, BNetUser user, String command, boolean wasWhispered) {return false;}

	public void clanMOTD(Connection source, Object cookie, String text) {}
	public void clanMemberList(Connection source, ClanMember[] members) {}
	public void clanMemberRemoved(Connection source, String username) {}
	public void clanMemberStatusChange(Connection source, ClanMember member) {}
	public void clanMemberRankChange(Connection source, byte oldRank, byte newRank, String user) {}

	private void logChat(Connection source, String text) {
		String fName = "logs/" + source.getProfile().getName() + "/";
		fName += sdf.format(new Date());
		fName += ".log";
		File f = new File(fName);
		try {
			if(!f.exists())
				f.createNewFile();
			// Create a new FOS for appending
			FileOutputStream fos = new FileOutputStream(f, true);
			fos.write(text.getBytes());
			fos.close();
		} catch (Exception e) {
			Out.fatalException(e);
		}
	}
	
	private String getDate() {
		return getColor(cs.getForegroundColor()) + String.format("[%1$tH:%1$tM:%1$tS] ", new GregorianCalendar(TIME_ZONE));
	}
	
	private String getColor(Color col) {
		String out = "000000";
		out += Integer.toHexString(col.getRGB());
		out = out.substring(out.length() - 6);
		out = "[COLOR:#" + out + "]";
		return out;
	}
	
	private void append(Connection source, String text, Color col) {
		String out = "\n";
		out += getDate();
		out += getColor(col);
		out += text;
		logChat(source, out);
	}
	
	private void append2(Connection source, String text, Color col, String text2, Color col2) {
		String out = "\n";
		out += getDate();
		out += getColor(col);
		out += text;
		if(!col.equals(col2))
			out += getColor(col2);
		out += text2;
		logChat(source, out);
	}
	
	public void recieveChat(Connection source, BNetUser user, String text) {
		append2(source,
			"<" + user.toString() + "> ",
			cs.getUserNameColor(user.getFlags()),
			text,
			cs.getChatColor(user.getFlags()));
	}

	public void recieveEmote(Connection source, BNetUser user, String text) {
		append(source,
			"<" + user.toString() + " " + text + "> ", cs.getEmoteColor(user.getFlags()));
	}

	public void whisperSent(Connection source, BNetUser user, String text) {
		append2(source,
			"<To: " + user.toString() + "> ",
			cs.getUserNameColor(user.getFlags()),
			text,
			cs.getWhisperColor(user.getFlags()));
	}

	public void whisperRecieved(Connection source, BNetUser user, String text) {
		append2(source,
			"<From: " + user.toString() + "> ",
			cs.getUserNameColor(user.getFlags()),
			text,
			cs.getWhisperColor(user.getFlags()));
	}
}

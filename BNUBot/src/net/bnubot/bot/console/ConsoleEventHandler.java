/**
 * This file is distributed under the GPL
 * $Id$
 */

package net.bnubot.bot.console;

import net.bnubot.core.Connection;
import net.bnubot.core.EventHandler;
import net.bnubot.core.clan.ClanMember;
import net.bnubot.core.friend.FriendEntry;
import net.bnubot.settings.GlobalSettings;
import net.bnubot.util.BNetUser;
import net.bnubot.util.Out;

public class ConsoleEventHandler implements EventHandler {
	Connection c = null;
	
	public synchronized void initialize(Connection c) {
		this.c = c;
		new CLIThread(c).start();
	}

	public void joinedChannel(String channel) {
		Out.info(getClass(), "Joining channel " + channel);
	}

	public void channelUser(BNetUser user) {
		if(GlobalSettings.displayChannelUsers)
			Out.info(getClass(), user.getShortPrettyName() + " (" + user.getPing() + "ms)" + user.getStatString().toString());
	}
	
	public void channelJoin(BNetUser user) {
		if(GlobalSettings.displayJoinParts)
			Out.info(getClass(), user + " has joined the channel" + user.getStatString().toString() + ".");
	}
	public void channelLeave(BNetUser user) {
		if(GlobalSettings.displayJoinParts)
			Out.info(getClass(), user + " has left the channel.");
	}

	public void recieveChat(BNetUser user, String text) {
		Out.info(getClass(), "<" + user.getShortPrettyName() + "> " + text);
	}

	public void recieveEmote(BNetUser user, String text) {
		Out.info(getClass(), "<" + user.getShortPrettyName() + " " + text + ">");
	}

	public void whisperRecieved(BNetUser user, String text) {
		Out.info(getClass(), "<From: " + user.getShortPrettyName() + "> " + text);
	}

	public void whisperSent(BNetUser user, String text) {
		Out.info(getClass(), "<To: " + user.getShortPrettyName() + "> " + text);
	}

	public void recieveInfo(String text) {
		Out.info(getClass(), text);
	}

	public void recieveError(String text) {
		Out.error(getClass(), text);
	}

	public void bnetConnected() {}
	public void bnetDisconnected() {}
	public void titleChanged() {}

	public void parseCommand(BNetUser user, String command, String param, boolean whisperBack) {}

	public void friendsList(FriendEntry[] entries) {}
	public void friendsUpdate(FriendEntry friend) {}
	public void friendsAdd(FriendEntry friend) {}
	public void friendsPosition(byte oldPosition, byte newPosition) {}
	public void friendsRemove(byte entry) {}

	public void clanMOTD(Object cookie, String text) {}
	public void clanMemberList(ClanMember[] members) {}
	public void clanMemberRemoved(String username) {}
	public void clanMemberStatusChange(ClanMember member) {}
	public void clanMemberRankChange(byte oldRank, byte newRank, String user) {}

	public void logonRealmEx(int[] MCPChunk1, int ip, int port, int[] MCPChunk2, String uniqueName) {}
	public void queryRealms2(String[] realms) {}
}

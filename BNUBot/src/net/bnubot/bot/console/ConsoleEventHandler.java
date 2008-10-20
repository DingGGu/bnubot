/**
 * This file is distributed under the GPL
 * $Id$
 */

package net.bnubot.bot.console;

import java.util.HashMap;

import net.bnubot.core.Connection;
import net.bnubot.core.EventHandler;
import net.bnubot.core.Profile;
import net.bnubot.settings.GlobalSettings;
import net.bnubot.util.BNetUser;

/**
 * @author scotta
 */
public class ConsoleEventHandler extends EventHandler {
	private static final HashMap<Connection, CLIThread> threads = new HashMap<Connection, CLIThread>();

	public ConsoleEventHandler(Profile profile) {
		super(profile);
	}

	@Override
	public synchronized void initialize(Connection source) {
		CLIThread thread = new CLIThread(source);
		threads.put(source, thread);
		thread.start();
	}

	@Override
	public void disable(Connection source) {
		CLIThread thread = threads.get(source);
		if(thread != null)
			thread.disable();
	}

	@Override
	public void joinedChannel(Connection source, String channel) {
		System.out.println("Joining channel " + channel);
	}

	@Override
	public void channelUser(Connection source, BNetUser user) {
		if(GlobalSettings.displayChannelUsers)
			System.out.println(user.toStringEx() + user.getStatString().toString());
	}

	@Override
	public void channelJoin(Connection source, BNetUser user) {
		if(GlobalSettings.getDisplayJoinParts())
			System.out.println(user.toStringEx() + " has joined the channel" + user.getStatString().toString() + ".");
	}
	@Override
	public void channelLeave(Connection source, BNetUser user) {
		if(GlobalSettings.getDisplayJoinParts())
			System.out.println(user.toStringEx() + " has left the channel.");
	}

	@Override
	public void recieveChat(Connection source, BNetUser user, String text) {
		System.out.println("<" + user.toString() + "> " + text);
	}

	@Override
	public void recieveEmote(Connection source, BNetUser user, String text) {
		System.out.println("<" + user.toString() + " " + text + ">");
	}

	@Override
	public void whisperRecieved(Connection source, BNetUser user, String text) {
		System.out.println("<From: " + user.toString() + "> " + text);
	}

	@Override
	public void whisperSent(Connection source, BNetUser user, String text) {
		System.out.println("<To: " + user.toString() + "> " + text);
	}

	@Override
	public void recieveDebug(Connection source, String text) {
		System.out.println("(DEBUG) " + text);
	}

	@Override
	public void recieveInfo(Connection source, String text) {
		System.out.println("(INFO) " + text);
	}

	@Override
	public void recieveError(Connection source, String text) {
		System.out.println("(ERROR) " + text);
	}

	@Override
	public void recieveServerInfo(Connection source, String text) {
		System.out.println("(INFO) " + text);
	}

	@Override
	public void recieveServerError(Connection source, String text) {
		System.out.println("(ERROR) " + text);
	}
}

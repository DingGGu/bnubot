/**
 * This file is distributed under the GPL
 * $Id$
 */

package net.bnubot.bot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UnknownFormatConversionException;

import net.bnubot.bot.commands.CommandAccess;
import net.bnubot.bot.commands.CommandAdd;
import net.bnubot.bot.commands.CommandAddAlias;
import net.bnubot.bot.commands.CommandAllSeen;
import net.bnubot.bot.commands.CommandAuth;
import net.bnubot.bot.commands.CommandAutoPromotion;
import net.bnubot.bot.commands.CommandBan;
import net.bnubot.bot.commands.CommandClearQueue;
import net.bnubot.bot.commands.CommandCreateAccount;
import net.bnubot.bot.commands.CommandDisconnect;
import net.bnubot.bot.commands.CommandHome;
import net.bnubot.bot.commands.CommandInfo;
import net.bnubot.bot.commands.CommandInvite;
import net.bnubot.bot.commands.CommandKick;
import net.bnubot.bot.commands.CommandMail;
import net.bnubot.bot.commands.CommandMailAll;
import net.bnubot.bot.commands.CommandPing;
import net.bnubot.bot.commands.CommandPingMe;
import net.bnubot.bot.commands.CommandQuit;
import net.bnubot.bot.commands.CommandReconnect;
import net.bnubot.bot.commands.CommandRecruit;
import net.bnubot.bot.commands.CommandRecruits;
import net.bnubot.bot.commands.CommandRejoin;
import net.bnubot.bot.commands.CommandRenameAccount;
import net.bnubot.bot.commands.CommandSay;
import net.bnubot.bot.commands.CommandSearch;
import net.bnubot.bot.commands.CommandSearchRank;
import net.bnubot.bot.commands.CommandSeen;
import net.bnubot.bot.commands.CommandSetAccount;
import net.bnubot.bot.commands.CommandSetAuth;
import net.bnubot.bot.commands.CommandSetBirthday;
import net.bnubot.bot.commands.CommandSetRank;
import net.bnubot.bot.commands.CommandSetRecruiter;
import net.bnubot.bot.commands.CommandSweepBan;
import net.bnubot.bot.commands.CommandTimeBan;
import net.bnubot.bot.commands.CommandTrigger;
import net.bnubot.bot.commands.CommandUnban;
import net.bnubot.bot.commands.CommandUpdate;
import net.bnubot.bot.commands.CommandVote;
import net.bnubot.bot.commands.CommandVoteBan;
import net.bnubot.bot.commands.CommandVoteCancel;
import net.bnubot.bot.commands.CommandVoteKick;
import net.bnubot.bot.commands.CommandWhisperBack;
import net.bnubot.bot.commands.CommandWhoami;
import net.bnubot.bot.commands.CommandWhois;
import net.bnubot.bot.commands.TimeBan;
import net.bnubot.bot.commands.Vote;
import net.bnubot.core.Connection;
import net.bnubot.core.EventHandler;
import net.bnubot.core.Profile;
import net.bnubot.core.commands.AccountDoesNotExistException;
import net.bnubot.db.Account;
import net.bnubot.db.BNLogin;
import net.bnubot.db.Mail;
import net.bnubot.db.Rank;
import net.bnubot.db.conf.DatabaseContext;
import net.bnubot.logging.Out;
import net.bnubot.settings.GlobalSettings;
import net.bnubot.util.BNetUser;
import net.bnubot.util.TimeFormatter;
import net.bnubot.util.UnloggedException;


/**
 * @author scotta
 */
public class CommandEventHandler extends EventHandler {
	public static final Map<Connection, Boolean> sweepBanInProgress = new HashMap<Connection, Boolean>();
	public static final Map<Connection, Integer> sweepBannedUsers = new HashMap<Connection, Integer>();
	public static final List<TimeBan> timeBannedUsers = new ArrayList<TimeBan>();
	public static final Map<Connection, Vote> votes = new HashMap<Connection, Vote>();

	public CommandEventHandler(Profile profile) {
		super(profile);
		if(DatabaseContext.getContext() == null)
			throw new UnloggedException("Can not enable commands without a database!");
		initializeCommands();
	}

	protected BNLogin touchUser(Connection source, BNetUser user, String action) {
		try {
			BNLogin rsUser = BNLogin.getCreate(user);
			if(rsUser != null) {
				rsUser.setLastSeen(new Date(System.currentTimeMillis()));
				if(action != null)
					rsUser.setLastAction(action);
				rsUser.updateRow();
				return rsUser;
			}
		} catch(Exception e) {
			Out.exception(e);
		}
		return null;
	}

	private static boolean commandsInitialized = false;
	private static void initializeCommands() {
		if(commandsInitialized)
			return;
		commandsInitialized = true;

		Profile.registerCommand("access", new CommandAccess());
		Profile.registerCommand("add", new CommandAdd());
		Profile.registerCommand("addalias", new CommandAddAlias());
		Profile.registerCommand("allseen", new CommandAllSeen());
		Profile.registerCommand("auth", new CommandAuth());
		Profile.registerCommand("autopromotion", new CommandAutoPromotion());
		Profile.registerCommand("ban", new CommandBan());
		Profile.registerCommand("clearqueue", new CommandClearQueue());
		Profile.registerCommand("createaccount", new CommandCreateAccount());
		Profile.registerCommand("disconnect", new CommandDisconnect());
		Profile.registerCommand("home", new CommandHome());
		Profile.registerCommand("info", new CommandInfo());
		Profile.registerCommand("invite", new CommandInvite());
		Profile.registerCommand("kick", new CommandKick());
		Profile.registerCommand("mail", new CommandMail());
		Profile.registerCommand("mailall", new CommandMailAll());
		Profile.registerCommand("ping", new CommandPing());
		Profile.registerCommand("pingme", new CommandPingMe());
		Profile.registerCommand("quit", new CommandQuit());
		Profile.registerCommand("reconnect", new CommandReconnect());
		Profile.registerCommand("recruit", new CommandRecruit());
		Profile.registerCommand("recruits", new CommandRecruits());
		Profile.registerCommand("rejoin", new CommandRejoin());
		Profile.registerCommand("renameaccount", new CommandRenameAccount());
		Profile.registerCommand("say", new CommandSay());
		Profile.registerCommand("search", new CommandSearch());
		Profile.registerCommand("searchrank", new CommandSearchRank());
		Profile.registerCommand("seen", new CommandSeen());
		Profile.registerCommand("setaccount", new CommandSetAccount());
		Profile.registerCommand("setauth", new CommandSetAuth());
		Profile.registerCommand("setbirthday", new CommandSetBirthday());
		Profile.registerCommand("setrank", new CommandSetRank());
		Profile.registerCommand("setrecruiter", new CommandSetRecruiter());
		Profile.registerCommand("sweepban", new CommandSweepBan());
		Profile.registerCommand("timeban", new CommandTimeBan());
		Profile.registerCommand("trigger", new CommandTrigger());
		Profile.registerCommand("unban", new CommandUnban());
		Profile.registerCommand("update", new CommandUpdate());
		Profile.registerCommand("vote", new CommandVote());
		Profile.registerCommand("voteban", new CommandVoteBan());
		Profile.registerCommand("votecancel", new CommandVoteCancel());
		Profile.registerCommand("votekick", new CommandVoteKick());
		Profile.registerCommand("whisperback", new CommandWhisperBack());
		Profile.registerCommand("whoami", new CommandWhoami());
		Profile.registerCommand("whois", new CommandWhois());
	}

	public static Account createAccount(String accountName, Account recruiter, BNLogin rsSubject)
	throws AccountDoesNotExistException {
		Account rsSubjectAccount = Account.get(accountName);
		if(rsSubjectAccount != null)
			throw new AccountDoesNotExistException("That account already exists!");

		try {
			rsSubjectAccount = Account.create(accountName, Rank.get(0), recruiter);
		} catch(Exception e) {}

		if(rsSubjectAccount == null)
			throw new AccountDoesNotExistException("Failed to create account [" + accountName + "] for an unknown reason");

		rsSubject.setAccount(rsSubjectAccount);
		rsSubjectAccount.setRank(Rank.get(GlobalSettings.recruitAccess));

		try {
			rsSubjectAccount.updateRow();
			return rsSubjectAccount;
		} catch(Exception e) {
			throw new AccountDoesNotExistException(e.getMessage());
		}
	}

	public static void doKickBan(Connection source, BNetUser user, String param, boolean isBan, boolean whisperBack) {
		if((param == null) || (param.length() == 0)) {
			if(isBan)
				user.sendChat("Use: %trigger%ban ( <user>[@<realm>] | pattern ) [reason]", whisperBack);
			else
				user.sendChat("Use: %trigger%kick ( <user>[@<realm>] | pattern ) [reason]", whisperBack);
			return;
		}

		// Extract the reason
		String[] params = param.split(" ", 2);
		String reason = params[0];
		if(params.length > 1)
			reason = params[1];

		if(isBan && (params[0].indexOf('*') == -1)) {
			// Regular ban
			String out = (isBan) ? "/ban " : "/kick ";
			BNetUser bnSubject = source.findUser(params[0], user);
			if(bnSubject != null)
				out += bnSubject.getFullLogonName();
			else
				out += params[0];
			out += " " + reason;

			// Send the command
			source.sendChat(out);
			setInfoForwarding(source, user, whisperBack);
		} else {
			// Wildcard kick/ban
			Collection<BNetUser> users = source.findUsersWildcard(params[0], user);
			if(users.size() == 0) {
				user.sendChat("That pattern did not match any users.", whisperBack);
				return;
			}

			int numSkipped = 0;
			for(BNetUser u : users) {
				if((u.getFlags() & 0x02) != 0) {
					numSkipped++;
					continue;
				}

				// Build the command
				String out = (isBan) ? "/ban " : "/kick ";
				out += u.getFullLogonName() + " " + reason;

				// Send the command
				source.sendChat(out);
				setInfoForwarding(source, user, whisperBack);
			}

			if(numSkipped > 0)
				user.sendChat("Skipped " + numSkipped + " users with operator status.", whisperBack);
		}
	}

	/**
	 * Start a vote
	 * @param source The connection to vote on
	 * @param user The user who started the vote
	 * @param target The user to vote against
	 * @param whisperBack
	 * @param isBan Whether to ban if vote succeeds
	 */
	public static void startVote(Connection source, BNetUser user, String target, boolean whisperBack, boolean isBan) {
		if(votes.get(source) != null) {
			user.sendChat("There is already a vote in progress!", whisperBack);
			return;
		}

		BNetUser bnSubject = source.findUser(target, user);
		if(bnSubject == null) {
			user.sendChat("User not found", whisperBack);
			return;
		}

		Vote v = new Vote(source, bnSubject, isBan);
		votes.put(source, v);
	}

	@Override
	public void channelJoin(Connection source, BNetUser user) {
		synchronized(timeBannedUsers) {
			for(TimeBan tb : timeBannedUsers)
				if(tb.getSubject().equals(user)) {
					source.sendChat("/ban " + user.getFullLogonName() + " TimeBan: " + TimeFormatter.formatTime(tb.getTimeLeft(), false) + " left");
					return;
				}
		}

		BNLogin rsUser = touchUser(source, user, "joining the channel");

		if(rsUser == null)
			return;
		if(!source.getConnectionSettings().enableGreetings)
			return;

		try {
			// Fix the case of the user's login if it has changed
			rsUser.setLogin(user.getFullAccountName());

			switch(user.getStatString().getProduct()) {
			case STAR: {
				Integer newWins = user.getStatString().getWins();
				if(newWins != null) {
					Integer oldWins = rsUser.getWinsSTAR();
					if((oldWins == null) || (newWins > oldWins)) {
						rsUser.setWinsSTAR(newWins);
					}
				}
				break;
			}

			case SEXP: {
				Integer newWins = user.getStatString().getWins();
				if(newWins != null) {
					Integer oldWins = rsUser.getWinsSEXP();
					if((oldWins == null) || (newWins > oldWins)) {
						rsUser.setWinsSEXP(newWins);
					}
				}
				break;
			}

			case W2BN: {
				Integer newWins = user.getStatString().getWins();
				if(newWins != null) {
					Integer oldWins = rsUser.getWinsW2BN();
					if((oldWins == null) || (newWins > oldWins)) {
						rsUser.setWinsW2BN(newWins);
					}
				}
				break;
			}

			case D2DV:
			case D2XP: {
				Integer newLevel = user.getStatString().getCharLevel();
				if(newLevel != null) {
					Integer oldLevel = rsUser.getLevelD2();
					if((oldLevel == null) || (newLevel > oldLevel)) {
						rsUser.setLevelD2(newLevel);
					}
				}
				break;
			}

			case WAR3:
			case W3XP: {
				Integer newLevel = user.getStatString().getLevel();
				if(newLevel != null) {
					Integer oldLevel = rsUser.getLevelW3();
					if((oldLevel == null) || (newLevel > oldLevel)) {
						rsUser.setLevelW3(newLevel);
					}
				}
				break;
			}
			}
			try {
				rsUser.updateRow();
			} catch(Exception e) {
				Out.exception(e);
			}

			Account rsAccount = Account.get(user);
			Rank rsRank = (rsAccount == null) ? Rank.get(0) : rsAccount.getRank();

			if(rsRank != null) {
				// Autopromotions
				Integer apDays = rsRank.getApDays();
				// Check that they meet the days requirement
				apBlock: if((apDays != null) && (apDays != 0)) {
					double timeElapsed = 0;
					if(rsAccount == null) {
						timeElapsed = rsUser.getCreated().getTime();
					} else if(rsAccount.getLastRankChange() != null) {
						timeElapsed = rsAccount.getLastRankChange().getTime();
					}

					timeElapsed = System.currentTimeMillis() - timeElapsed;
					timeElapsed /= 1000 * 60 * 60 * 24;
					if(timeElapsed < apDays)
						break apBlock;

					Integer apWins = rsRank.getApWins();
					Integer apD2Level = rsRank.getApD2Level();
					Integer apW3Level = rsRank.getApW3Level();
					if((apWins == null)
					|| (apD2Level == null)
					|| (apW3Level == null))
						break apBlock;
					long wins[];
					if(rsAccount == null)
						wins = new long[] {0, 0, 0};
					else
						wins = rsAccount.getWinsLevels(GlobalSettings.recruitTagPrefix, GlobalSettings.recruitTagSuffix);

					boolean condition = false;
					condition |= ((apWins > 0) && (wins[0] >= apWins));
					condition |= ((apD2Level > 0) && (wins[1] >= apD2Level));
					condition |= ((apW3Level > 0) && (wins[2] >= apW3Level));
					condition |= ((apWins == 0) && (apD2Level == 0) && (apW3Level == 0));

					if(condition) {
						// Check RS
						long rs = 0;
						if(rsAccount != null)
							rs = rsAccount.getRecruitScore(GlobalSettings.recruitAccess);
						Integer apRS = rsRank.getApRecruitScore();
						if((apRS == null) || (apRS == 0) || (rs >= apRS)) {
							int rank = 0;
							if(rsAccount != null)
								rank = rsAccount.getAccess();
							else {
								String name = user.getFullAccountName();
								name = name.substring(0, name.indexOf('@'));
								try {
									rsAccount = createAccount(name, null, rsUser);
								} catch(Exception e) {
									Out.exception(e);
									user.sendChat("I couldn't make an account for you: " + e.getMessage(), true);
									break apBlock;
								}
							}

							// Store the old AP mail message
							String apMail = rsRank.getApMail();

							// Give them a promotion
							rsRank = Rank.get(++rank);
							rsAccount.setRank(rsRank);
							rsAccount.setLastRankChange(new Date(System.currentTimeMillis()));
							try {
								rsAccount.updateRow();
							} catch(Exception e) {
								Out.exception(e);
								break apBlock;
							}
							user.resetPrettyName();	//Reset the presentable name
							source.sendChat("Congratulations " + user.toString(GlobalSettings.bnUserToStringCommandResponse) + ", you've recieved a promotion! Your rank is now " + rsRank.getPrefix() + " (" + rank + ").");
							if((apMail != null) && (apMail.length() > 0))
								Mail.send(null, rsAccount, apMail);
						} else {
							user.sendChat("You need " + Long.toString(apRS - rs) + " more recruitment points to recieve a promotion!", true);
						}
					} else {
						String msg = "You need ";
						switch(user.getStatString().getProduct()) {
						case STAR:
						case SEXP:
						case W2BN:
							msg += Long.toString(apWins - wins[0]) + " more win";
							if(apWins - wins[0] > 1)
								msg += "s";
							break;
						case D2DV:
						case D2XP:
							msg += "to reach Diablo 2 level " + apD2Level;
							break;
						case WAR3:
						case W3XP:
							msg += "to reach Warcraft 3 level " + apW3Level;
							break;
						default:
							break apBlock;
						}
						msg += " to recieve a promotion!";
						user.sendChat(msg, true);
					}
				}

				// Greetings
				String greeting = rsRank.getGreeting();
				if(greeting != null) {
					try {
						greeting = String.format(greeting, user.toString(), user.getPing(), user.getFullLogonName());
						source.sendChat(greeting);
					} catch(NoSuchMethodError e) {
					} catch(UnknownFormatConversionException e) {
						Out.error(getClass(), "String.format() failed: " + e.getMessage() + "\ngreeting=" + greeting + "\nping=" + user.getPing() + "\nuser=" + user.getFullLogonName());
					}
				}
			}

			if(rsAccount == null)
				return;

			//Birthdays
			Date birthday = rsAccount.getBirthday();
			if(birthday != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("M-d");

				Calendar cal = Calendar.getInstance(TimeFormatter.timeZone);
				Date today = cal.getTime();
				String s1 = sdf.format(today);
				String s2 = sdf.format(birthday);

				if(s1.equals(s2)) {
					cal.setTime(birthday);
					int age = cal.get(Calendar.YEAR);
					cal.setTime(today);
					age = cal.get(Calendar.YEAR) - age;
					source.sendChat("Happy birthday, " + user.toString() + "! Today, you are " + age + " years old!");
				}
			}

			//Mail
			int umc = Mail.getUnreadCount(rsAccount);
			if(umc > 0)
				user.sendChat("You have " + umc + " unread messages; type [ %trigger%mail read ] to retrieve them", true);
		} catch (Exception e) {
			Out.exception(e);
		}
	}

	@Override
	public void channelLeave(Connection source, BNetUser user) {
		touchUser(source, user, "leaving the channel");

		Vote vote = votes.get(source);
		if((vote != null) && vote.getSubject().equals(user))
			vote.cancel();
	}

	@Override
	public void channelUser(Connection source, BNetUser user) {
		try {
			BNLogin.getCreate(user);
		} catch (Exception e) {
			Out.exception(e);
		}
	}

	@Override
	public void recieveChat(Connection source, BNetUser user, String text) {
		touchUser(source, user, "chatting in the channel");
	}

	@Override
	public void recieveEmote(Connection source, BNetUser user, String text) {
		touchUser(source, user, "chatting in the channel");
	}


	private static long lastCommandTime = 0;
	private static BNetUser lastCommandUser = null;
	private static boolean lastCommandWhisperBack = true;

	/**
	 * Set the user to forward info/error messages to
	 * @param source
	 * @param user
	 * @param whisperBack
	 */
	public static void setInfoForwarding(Connection source, BNetUser user, boolean whisperBack) {
		if(!user.equals(source.getMyUser())) {
			lastCommandUser = user;
			lastCommandTime = System.currentTimeMillis();
			lastCommandWhisperBack = whisperBack;
		} else {
			// Do not allow forwarding messages to self
			lastCommandUser = null;
		}
	}

	private String lastInfo = null;
	/**
	 * Forward info/error messages to the last user who issued a command
	 * @param source
	 * @param text
	 */
	private void recieveInfoError(Connection source, String text) {
		if(lastCommandUser == null)
			return;
		if(sweepBanInProgress.get(source) == Boolean.TRUE)
			return;

		long timeElapsed = System.currentTimeMillis() - lastCommandTime;
		// 500ms
		if((timeElapsed < 500)
		&& (!text.equals(lastInfo))) {
			lastInfo = text;
			lastCommandUser.sendChat(text, lastCommandWhisperBack);
		}
	}

	@Override
	public void recieveServerError(Connection source, String text) {
		recieveInfoError(source, text);
	}

	/**
	 * If the name is "[NAME]", return "NAME" otherwise pass name through
	 * @param name	The name from the /who response
	 * @return		Name with [] removed
	 */
	private String removeOpUserBrackets(String name) {
		if(name.charAt(0) == '[') {
			if(name.charAt(name.length() - 1) == ']') {
				return name.substring(1, name.length() - 1);
			}
		}
		return name;
	}

	@Override
	public void recieveServerInfo(Connection source, String text) {
		if(sweepBanInProgress.get(source) == Boolean.TRUE) {
			boolean turnItOff = true;

			if(text.length() > 17) {
				if(text.substring(0, 17).equals("Users in channel ")) {
					if(sweepBannedUsers.get(source) == 0) {
						turnItOff = false;
						source.sendChat("Sweepbanning channel " + text.substring(17, text.length() - 1));
					}
				}
			}

			String users[] = text.split(", ");
			if(users.length == 2) {
				if(users[0].indexOf(' ') == -1) {
					if(users[1].indexOf(' ') == -1) {
						source.sendChat("/ban " + removeOpUserBrackets(users[0]));
						source.sendChat("/ban " + removeOpUserBrackets(users[1]));
						sweepBannedUsers.put(source, sweepBannedUsers.get(source) + 2);
						turnItOff = false;
					}
				}
			} else {
				if(text.indexOf(' ') == -1) {
					source.sendChat("/ban " + removeOpUserBrackets(text));
					sweepBannedUsers.put(source, sweepBannedUsers.get(source) + 1);
					turnItOff = true;
				}
			}

			if(turnItOff)
				sweepBanInProgress.put(source, false);
		}

		if(sweepBanInProgress.get(source) == Boolean.TRUE)
			return;

		if(text.contains(" was banned by ")
		|| text.contains(" was unbanned by ")
		|| text.contains(" was kicked out of the channel by "))
			return;

		recieveInfoError(source, text);
	}
}

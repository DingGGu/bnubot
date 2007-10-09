package bnubot.bot.gui.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.util.GregorianCalendar;

import javax.swing.JEditorPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import bnubot.bot.gui.ColorScheme.ColorScheme;
import bnubot.core.BNetUser;

@SuppressWarnings("serial")
public class TextWindow2 extends JScrollPane {
	private class myJEP extends JEditorPane {
		public void paintComponents(Graphics g) {
			if(!disableRedraw)
				super.paintComponents(g);
		}
	}
	
	private ColorScheme cs = null;
	private JEditorPane jep = null;
	private String head = null;
	private String foot = null;
	private String html = null;
	private static Runnable scrollDown = null;
	private static Runnable enableDraw = null;
	private boolean disableRedraw = false;

	public TextWindow2(ColorScheme cs) {
		super();
		this.cs = cs;
		jep = new myJEP();
		jep.setEditable(false);
		jep.setContentType("text/html");
		jep.setBackground(cs.getBackgroundColor());
		Container c = (Container)getComponent(0);
		c.add(jep);
		
		head = "<html><head><style type=\"text/css\">";
		head += " body {font-family: verdana, courier, sans-serif}";
		head += " .timestamp {color: #" + makeColor(cs.getTimeStampColor()) + "}";
		head += " .channel {color: #" + makeColor(cs.getChannelColor()) + "}";
		head += " .info {color: #" + makeColor(cs.getInfoColor()) + "}";
		head += " .error {color: #" + makeColor(cs.getErrorColor()) + "}";
		head += "</style></head><body>";
		html = "";
		foot = "</body></html>";
	}
	
	public void scrollDown() {
		if(scrollDown == null)
			scrollDown = new Runnable() {
				public void run() {
					JScrollBar vsb = getVerticalScrollBar();
					vsb.setValue(vsb.getMaximum());
					validate();
				}
			};
		
		//Scroll to the bottom
		SwingUtilities.invokeLater(scrollDown);
	}
	
	public void setText() {
		disableRedraw = true;
		jep.setText(head + html + foot);
		scrollDown();
		
		if(enableDraw == null)
			enableDraw = new Runnable() {
				public void run() {
					disableRedraw = false;
					validate();
				} };
		SwingUtilities.invokeLater(enableDraw);
		
		if(html.length() > 0x8000) {
			int i = html.indexOf("</div>", 0);
			if(i > 0)
				html = html.substring(i + 6);
		}
	}
	
	public String makeColor(Color c) {
		String color = "000000" + Integer.toHexString(c.getRGB());
		return color.substring(color.length() - 6);
	}
	
	public void makeFont(Color c) {
		html += "<font color=\"#" + makeColor(c) + "\">";
	}
	
	public void appendDate() {
		html += "<div class=\"timestamp\">";
		html += String.format("[%1$tH:%1$tM:%1$tS] ", new GregorianCalendar());
	}
	
	public String safeHtml(String in) {
		return in
			.replaceAll("&", "&amp;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll("\n", "<br>\n");
	}
	
	public void append(String text, Color col) {
		appendDate();
		makeFont(col);
		html += safeHtml(text);
		html += "</font></div>";
		setText();
	}
	
	public void append(String text, String clazz) {
		appendDate();
		html += "<font class=\"" + clazz + "\">";
		html += safeHtml(text);
		html += "</font></div>";
		setText();
	}
	
	public void append2(String text, Color col, String text2, Color col2) {
		appendDate();
		makeFont(col);
		html += safeHtml(text);
		html += "</font>";
		makeFont(col2);
		html += safeHtml(text2);
		html += "</font></div>";
		setText();
	}
	
	public void channelInfo(String text) {
		append(text, "channel");
	}
	
	public void recieveInfo(String text) {
		append(text, "info");
	}
	
	public void recieveError(String text) {
		append(text, "error");
	}
	
	public void userChat(BNetUser user, int flags, String text) {
		append2(
			"<" + user + "> ",
			cs.getUserNameColor(flags),
			text,
			cs.getChatColor(flags));
	}
	
	public void whisperSent(BNetUser user, int flags, String text) {
		append2(
			"<To: " + user + "> ",
			cs.getUserNameColor(flags),
			text,
			cs.getWhisperColor(flags));
	}
	
	public void whisperRecieved(BNetUser user, int flags, String text) {
		append2(
			"<From: " + user + "> ",
			cs.getUserNameColor(flags),
			text,
			cs.getWhisperColor(flags));
	}
	
	public void userEmote(BNetUser user, int flags, String text) {
		append(
			"<" + user + " " + text + "> ",
			cs.getEmoteColor(flags));
	}
}
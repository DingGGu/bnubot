/**
 * This file is distributed under the GPL 
 * $Id$
 */

package net.bnubot.core.chat;

import java.net.Socket;

import net.bnubot.core.*;
import net.bnubot.core.bncs.ProductIDs;
import net.bnubot.core.queue.ChatQueue;
import net.bnubot.util.Out;

public class ChatConnection extends Connection {
	protected Socket s;
	protected BNetInputStream is;
	protected BNetOutputStream os;
	
	public ChatConnection(ConnectionSettings cs, ChatQueue cq) {
		super(cs, cq);
	}
	
	public void run() {
		try {
			s = new Socket(cs.bncsServer, cs.port);
			is = new BNetInputStream(s.getInputStream());
			os = new BNetOutputStream(s.getOutputStream());
			
			//Chat
			//os.writeByte(0x03);
			//os.writeByte(0x04);
			os.writeBytes("c" + cs.username + "\n" + cs.password + "\n");

			os.writeBytes("/join open tech support\n");
			
			Out.info(this.getClass().getName(), "Connected to " + cs.bncsServer + ":" + cs.port);
			
			
			os.writeNTString(cs.username);
			
			while(s.isConnected()) {
				if(is.available() > 0) {
					byte b = is.readByte();
					Out.print(Character.toString((char)b));
				} else {
					yield();
					sleep(10);
				}
			}
			
			Out.info(this.getClass().getName(), "Disconnected");
			
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public boolean isOp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void joinChannel(String channel) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendClanMOTD(Object cookie) throws Exception {
		throw new UnsupportedFeatureException("Chat clients can not use clans");
	}

	@Override
	public void sendClanRankChange(String string, int newRank) throws Exception {
		throw new UnsupportedFeatureException("Chat clients can not use clans");
	}

	@Override
	public void sendClanSetMOTD(String text) throws Exception {
		throw new UnsupportedFeatureException("Chat clients can not use clans");
	}

	@Override
	public int getProductID() {
		return ProductIDs.PRODUCT_CHAT;
	}

	@Override
	public void sendLogonRealmEx(String realmTitle) throws Exception {
		throw new UnsupportedFeatureException("Chat clients can not use realms");
	}

	@Override
	public void sendQueryRealms() throws Exception {
		throw new UnsupportedFeatureException("Chat clients can not use realms");
	}

	@Override
	public void sendProfile(String user) throws Exception {
		throw new UnsupportedFeatureException("Chat clients can not request profiles");
	}

}

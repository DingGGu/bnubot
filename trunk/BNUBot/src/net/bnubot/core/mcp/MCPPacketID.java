/**
 * This file is distributed under the GPL
 * $Id$
 */

package net.bnubot.core.mcp;

public enum MCPPacketID {
	MCP_UNKNOWN_0x00,
	MCP_STARTUP,
	MCP_CHARCREATE,
	MCP_CREATEGAME,
	MCP_JOINGAME,
	MCP_GAMELIST,
	MCP_GAMEINFO,
	MCP_CHARLOGON,
	MCP_UNKNOWN_0x08,
	MCP_UNKNOWN_0x09,
	MCP_CHARDELETE,
	MCP_UNKNOWN_0x0B,
	MCP_UNKNOWN_0x0C,
	MCP_UNKNOWN_0x0D,
	MCP_UNKNOWN_0x0E,
	MCP_UNKNOWN_0x0F,
	MCP_UNKNOWN_0x10,
	MCP_REQUESTLADDERDATA,
	MCP_MOTD,
	MCP_CANCELGAMECREATE,
	MCP_CREATEQUEUE,
	MCP_UNKNOWN_0x15,
	MCP_UNKNOWN_0x16,
	MCP_CHARLIST,
	MCP_CHARUPGRADE,
	MCP_CHARLIST2,
}

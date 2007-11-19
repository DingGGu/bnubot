/**
 * This file is distributed under the GPL
 * $Id$
 */
package net.bnubot.core.bnls;

public enum BNLSPacketId {
	BNLS_NULL,
	BNLS_CDKEY,
	BNLS_LOGONCHALLENGE,
	BNLS_LOGONPROOF,
	BNLS_CREATEACCOUNT,
	BNLS_CHANGECHALLENGE,
	BNLS_CHANGEPROOF,
	BNLS_UPGRADECHALLENGE,
	BNLS_UPGRADEPROOF,
	BNLS_VERSIONCHECK,
	BNLS_CONFIRMLOGON,
	BNLS_HASHDATA,
	BNLS_CDKEY_EX,
	BNLS_CHOOSENLSREVISION,
	BNLS_AUTHORIZE,
	BNLS_AUTHORIZEPROOF,
	BNLS_REQUESTVERSIONBYTE,
	BNLS_VERIFYSERVER,
	BNLS_RESERVESERVERSLOTS,
	BNLS_SERVERLOGONCHALLENGE,
	BNLS_SERVERLOGONPROOF,
	BNLS_RESERVED0,
	BNLS_RESERVED1,
	BNLS_RESERVED2,
	BNLS_VERSIONCHECKEX,
	BNLS_RESERVED3,
	BNLS_VERSIONCHECKEX2,
}

/**
 * This file is distributed under the GPL 
 * $Id$
 */

package bnubot.junit.core;

import bnubot.core.CookieUtility;
import junit.framework.TestCase;

public class CookieUtilityTest extends TestCase {

	public void testCookieUtility() {
		int id = CookieUtility.createCookie(null);
		assertEquals(0, id);
		Object obj = CookieUtility.destroyCookie(id);
		assertEquals(null, obj);
		
		obj = new String("test");
		id = CookieUtility.createCookie(obj);
		assertEquals(1, id);
		Object obj2 = CookieUtility.destroyCookie(id);
		assertEquals(obj, obj2);
	}
}

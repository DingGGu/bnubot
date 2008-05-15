/**
 * This file is distributed under the GPL
 * $Id$
 */

package net.bnubot.db;

import org.apache.cayenne.CayenneDataObject;

public abstract class CustomDataObject extends CayenneDataObject {
	/**
	 * Try to save changes to this object
	 * @throws Exception If a commit error occurs
	 */
	public void updateRow() throws Exception {
		try {
			getObjectContext().commitChanges();
		} catch(Exception e) {
			getObjectContext().rollbackChanges();
			throw e;
		}
	}

	public abstract String toDisplayString();
}
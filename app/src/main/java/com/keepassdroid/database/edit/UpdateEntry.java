/*
 * Copyright 2009-2011 Brian Pellin.
 *     
 * This file is part of KeePassDroid.
 *
 *  KeePassDroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  KeePassDroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeePassDroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.keepassdroid.database.edit;

import com.keepassdroid.database.DatabaseManager;
import com.keepassdroid.database.exception.PwDbOutputException;
import com.keepassdroid.database.model.Entry;
import com.keepassdroid.database.model.Group;

import java.io.IOException;

public class UpdateEntry implements Runnable {
	private DatabaseManager mDb;
	private Entry mOldE;
	private Entry mNewE;
	private Entry backup;
	
	public UpdateEntry(DatabaseManager db, Entry oldE, Entry newE) {
		mDb = db;
		mOldE = oldE;
		mNewE = newE;
		
		// Keep backup of original values in case save fails
		backup = (Entry) mOldE.clone();
	}

	@Override
	public void run() {
		// Update entry with new values
		mOldE.assign(mNewE);
		mOldE.touch(true, true);
		
		
		// Commit to disk
		boolean success = false;
		try {
			mDb.SaveData();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PwDbOutputException e) {
			e.printStackTrace();
		}

		if (success) {
			// Mark group dirty if title or icon changes
			if ( ! backup.getTitle().equals(mNewE.getTitle()) || ! backup.getIcon().equals(mNewE.getIcon()) ) {
				Group parent = backup.getParent();
				if ( parent != null ) {
					// Resort entries
					parent.sortEntriesByName();

					// Mark parent group dirty
					mDb.dirty.add(parent);

				}
			}
		} else {
			// If we fail to save, back out changes to global structure
			mOldE.assign(backup);
		}		
	}
}

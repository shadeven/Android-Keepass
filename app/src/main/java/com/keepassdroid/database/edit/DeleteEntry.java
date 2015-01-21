/*
 * Copyright 2009-2013 Brian Pellin.
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
import com.keepassdroid.database.KDB;
import com.keepassdroid.database.exception.PwDbOutputException;
import com.keepassdroid.database.model.Entry;
import com.keepassdroid.database.model.Group;

import java.io.IOException;

/** Task to delete entries
 * @author bpellin
 *
 */
public class DeleteEntry implements Runnable {

	private DatabaseManager mDb;
	private Entry mEntry;
	private boolean mDontSave;
	
	public DeleteEntry(DatabaseManager db, Entry entry) {
		mDb = db;
		mEntry = entry;
		mDontSave = false;
		
	}
	
	public DeleteEntry(DatabaseManager db, Entry entry, boolean dontSave) {
		mDb = db;
		mEntry = entry;
		mDontSave = dontSave;
		
	}
	
	@Override
	public void run() {
		KDB pm = mDb.kdb;
		Group parent = mEntry.getParent();

		// Remove Entry from parent
		boolean recycle = pm.canRecycle(mEntry);
		if (recycle) {
			pm.recycle(mEntry);
		}
		else {
			pm.deleteEntry(mEntry);
		}
		
		// Commit database
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
			// Mark parent dirty
			if ( parent != null ) {
				mDb.dirty.add(parent);
			}

			if (recycle) {
				Group recycleBin = pm.getRecycleBin();
				mDb.dirty.add(recycleBin);
				mDb.dirty.add(mDb.kdb.rootGroup);
			}
		} else {
			if (recycle) {
				pm.undoRecycle(mEntry, parent);
			}
			else {
				pm.undoDeleteEntry(mEntry, parent);
			}
		}
	}
	
}

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
import com.keepassdroid.database.exception.PwDbOutputException;
import com.keepassdroid.database.model.Entry;
import com.keepassdroid.database.model.Group;

import java.io.IOException;

public class AddEntry implements Runnable {
	protected DatabaseManager mDb;
	private Entry mEntry;
	
	public static AddEntry getInstance(DatabaseManager db, Entry entry) {
		return new AddEntry(db, entry);
	}
	
	protected AddEntry(DatabaseManager db, Entry entry) {
		mDb = db;
		mEntry = entry;
	}
	
	@Override
	public void run() {
		mDb.kdb.addEntryTo(mEntry, mEntry.getParent());
		
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
			Group parent = mEntry.getParent();
			// Mark parent group dirty
			mDb.dirty.add(parent);
		} else {
			mDb.kdb.removeEntryFrom(mEntry, mEntry.getParent());
		}
	}
	
}

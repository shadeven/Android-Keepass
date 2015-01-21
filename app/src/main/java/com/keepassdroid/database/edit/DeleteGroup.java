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
import java.util.ArrayList;
import java.util.List;

public class DeleteGroup implements Runnable {
	
	private DatabaseManager mDb;
	private Group mGroup;
	private boolean mDontSave;
	
	public DeleteGroup(DatabaseManager db, Group group) {
		setMembers(db, group, false);
	}

	public DeleteGroup(DatabaseManager db, Group group, boolean dontSave) {
		setMembers(db, group, dontSave);
	}

	private void setMembers(DatabaseManager db, Group group, boolean dontSave) {
		mDb = db;
		mGroup = group;
		mDontSave = dontSave;
	}
	
	
	
	@Override
	public void run() {
		
		// Remove child entries
		List<Entry> childEnt = new ArrayList<Entry>(mGroup.childEntries);
		for ( int i = 0; i < childEnt.size(); i++ ) {
			DeleteEntry task = new DeleteEntry(mDb, childEnt.get(i), true);
			task.run();
		}
		
		// Remove child groups
		List<Group> childGrp = new ArrayList<Group>(mGroup.childGroups);
		for ( int i = 0; i < childGrp.size(); i++ ) {
			DeleteGroup task = new DeleteGroup(mDb, childGrp.get(i), true);
			task.run();
		}
		
		
		// Remove from parent
		Group parent = mGroup.getParent();
		if ( parent != null ) {
			parent.childGroups.remove(mGroup);
		}
		
		// Remove from PwDatabaseV3
		mDb.kdb.getGroups().remove(mGroup);
		
		// Save
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
			// Remove from group global
			mDb.kdb.groups.remove(mGroup.getId());

			// Remove group from the dirty global (if it is present), not a big deal if this fails
			mDb.dirty.remove(mGroup);

			// Mark parent dirty
			if (mGroup.getParent() != null) {
				mDb.dirty.add(mGroup.getParent());
			}
			mDb.dirty.add(mDb.kdb.rootGroup);
		} else {
			// Let's not bother recovering from a failure to save a deleted group.  It is too much work.
		}
	}
}

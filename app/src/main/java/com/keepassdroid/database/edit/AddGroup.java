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
import com.keepassdroid.database.model.Group;

import java.io.IOException;

public class AddGroup implements Runnable {
	protected DatabaseManager dbm;
	private String mName;
	private int mIconID;
	private Group mGroup;
	private Group mParent;
	protected boolean mDontSave;

	public static AddGroup getInstance(DatabaseManager db, String name, int iconid, Group parent, boolean dontSave) {
		return new AddGroup(db, name, iconid, parent, dontSave);
	}

	private AddGroup(DatabaseManager db, String name, int iconid, Group parent, boolean dontSave) {
		dbm = db;
		mName = name;
		mIconID = iconid;
		mParent = parent;
		mDontSave = dontSave;
	}
	
	@Override
	public void run() {
		KDB kdb = dbm.kdb;
		
		// Generate new group
		mGroup = kdb.createGroup(mName);
		mGroup.icon = kdb.iconFactory.getIcon(mIconID);
		kdb.addGroupTo(mGroup, mParent);
		
		// Commit to disk
		boolean success = false;
		
		try {
			dbm.SaveData();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PwDbOutputException e) {
			e.printStackTrace();
		}

		if (success) {
			// Mark parent group dirty
			dbm.dirty.add(mParent);
		} else {
			kdb.removeGroupFrom(mGroup, mParent);
		}
	}

}

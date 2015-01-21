/*
 * Copyright 2009 Brian Pellin.
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
import com.keepassdroid.database.KDBV3;
import com.keepassdroid.database.PwEncryptionAlgorithm;
import com.keepassdroid.database.exception.PwDbOutputException;

import java.io.IOException;

public class CreateDB implements Runnable {

	private final int DEFAULT_ENCRYPTION_ROUNDS = 300;
	
	private String mFilename;
	private boolean mDontSave;
	
	public CreateDB(String filename, boolean dontSave) {
		mFilename = filename;
		mDontSave = dontSave;
	}

	@Override
	public void run() {
		// Create new database record
		DatabaseManager db = new DatabaseManager();
		
		// Create the PwDatabaseV3
		KDBV3 pm = new KDBV3();
		pm.algorithm = PwEncryptionAlgorithm.Rjindal;
		pm.numKeyEncRounds = DEFAULT_ENCRYPTION_ROUNDS;
		pm.name = "KeePass Password Manager";
		// Build the root group
		pm.constructTree(null);
		
		// Set Database state
		db.kdb = pm;
		db.mFilename = mFilename;
		db.setLoaded();
		
		// Add a couple default groups
		AddGroup internet = AddGroup.getInstance(db, "Internet", 1, pm.rootGroup, true);
		internet.run();
		AddGroup email = AddGroup.getInstance(db, "eMail", 19, pm.rootGroup, true);
		email.run();
		
		// Commit changes
		try {
			db.SaveData();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PwDbOutputException e) {
			e.printStackTrace();
		}
	}

}

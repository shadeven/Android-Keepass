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

import java.io.IOException;


import com.keepassdroid.database.DatabaseManager;
import com.keepassdroid.database.KDB;
import com.keepassdroid.database.exception.InvalidKeyFileException;
import com.keepassdroid.database.exception.PwDbOutputException;

public class SetPassword implements Runnable {
	
	private String mPassword;
	private String mKeyfile;
	private DatabaseManager mDb;
	private boolean mDontSave;
	
	public SetPassword(DatabaseManager db, String password, String keyfile) {
		mDb = db;
		mPassword = password;
		mKeyfile = keyfile;
		mDontSave = false;
	}

	public SetPassword(DatabaseManager db, String password, String keyfile, boolean dontSave) {
		mDb = db;
		mPassword = password;
		mKeyfile = keyfile;
		mDontSave = dontSave;
	}

	@Override
	public void run() {
		KDB pm = mDb.kdb;
		
		byte[] backupKey = new byte[pm.masterKey.length];
		System.arraycopy(pm.masterKey, 0, backupKey, 0, backupKey.length);

		// Set key
		try {
			pm.setMasterKey(mPassword, mKeyfile);
		} catch (InvalidKeyFileException e) {
			erase(backupKey);
			// finish(false, e.getMessage());
			return;
		} catch (IOException e) {
			erase(backupKey);
			// finish(false, e.getMessage());
			return;
		}
		
		// Save Database
		boolean success = false;
		try {
			mDb.SaveData();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PwDbOutputException e) {
			e.printStackTrace();
		}

		if (!success) {
			// Erase the current master key
			erase(mDb.kdb.masterKey);
			mDb.kdb.masterKey = backupKey;
		}
	}
	
	/** Overwrite the array as soon as we don't need it to avoid keeping the extra data in memory
	 * @param array
	 */
	private void erase(byte[] array) {
		if ( array == null ) return;
		
		for ( int i = 0; i < array.length; i++ ) {
			array[i] = 0;
		}
	}

}

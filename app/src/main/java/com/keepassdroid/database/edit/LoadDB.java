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

import java.io.FileNotFoundException;
import java.io.IOException;

import com.keepassdroid.database.DatabaseManager;
import com.keepassdroid.database.exception.ArcFourException;
import com.keepassdroid.database.exception.InvalidAlgorithmException;
import com.keepassdroid.database.exception.InvalidDBException;
import com.keepassdroid.database.exception.InvalidDBSignatureException;
import com.keepassdroid.database.exception.InvalidDBVersionException;
import com.keepassdroid.database.exception.InvalidKeyFileException;
import com.keepassdroid.database.exception.InvalidPasswordException;
import com.keepassdroid.database.exception.KeyFileEmptyException;

public class LoadDB implements Runnable {
	private String mFileName;
	private String mPass;
	private String mKey;
	private DatabaseManager mDb;
	private boolean mRememberKeyfile;
	
	public LoadDB(DatabaseManager db, String fileName, String pass, String key) {
		mDb = db;
		mFileName = fileName;
		mPass = pass;
		mKey = key;
	}

	@Override
	public void run() {
		try {
			mDb.LoadData(mFileName, mPass, mKey);
			
			saveFileData(mFileName, mKey);
		
		} catch (ArcFourException e) {
			e.printStackTrace();
			return;
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (KeyFileEmptyException e) {
			e.printStackTrace();
			return;
		} catch (InvalidAlgorithmException e) {
			e.printStackTrace();
			return;
		} catch (InvalidKeyFileException e) {
			e.printStackTrace();
			return;
		} catch (InvalidDBSignatureException e) {
			e.printStackTrace();
			return;
		} catch (InvalidDBVersionException e) {
			e.printStackTrace();
			return;
		} catch (InvalidDBException e) {
			e.printStackTrace();
			return;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void saveFileData(String fileName, String key) {
		if ( ! mRememberKeyfile ) {
			key = "";
		}

		// TODO App.getFileHistory().createFile(fileName, key);
	}
	


}

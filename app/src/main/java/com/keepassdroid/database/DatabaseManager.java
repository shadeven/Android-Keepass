/*
 * Copyright 2009-2014 Brian Pellin.
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
package com.keepassdroid.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SyncFailedException;
import java.util.HashSet;
import java.util.Set;

import com.keepassdroid.database.exception.InvalidDBException;
import com.keepassdroid.database.exception.PwDbOutputException;
import com.keepassdroid.database.load.Importer;
import com.keepassdroid.database.load.ImporterFactory;
import com.keepassdroid.database.model.Group;
import com.keepassdroid.database.save.PwDbOutput;
import com.keepassdroid.search.SearchDbHelper;

/**
 * @author bpellin
 */
public class DatabaseManager {
	public Set<Group> dirty = new HashSet<Group>();
	public KDB kdb;
	public String mFilename;
	public SearchDbHelper searchHelper;
	public boolean readOnly = false;
	
	private boolean loaded = false;
	
	public boolean Loaded() {
		return loaded;
	}
	
	public void setLoaded() {
		loaded = true;
	}
	
	public void LoadData(String filename, String password, String keyfile) throws IOException, FileNotFoundException, InvalidDBException {
		File file = new File(filename);
		FileInputStream fis = new FileInputStream(file);
		
		LoadData(fis, password, keyfile);
	
		readOnly = !file.canWrite();
		mFilename = filename;
	}

	public void LoadData(InputStream is, String password, String keyfile) throws IOException, InvalidDBException {

		BufferedInputStream bis = new BufferedInputStream(is);
		
		if ( ! bis.markSupported() ) {
			throw new IOException("Input stream does not support mark.");
		}
		
		// We'll end up reading 8 bytes to identify the header. Might as well use two extra.
		bis.mark(10);
		
		Importer imp = ImporterFactory.createImporter(bis);

		bis.reset();  // Return to the start
		
		kdb = imp.openDatabase(bis, password, keyfile);
		if ( kdb != null ) {
			Group root = kdb.rootGroup;
			kdb.populateGlobals(root);
		}
		
		searchHelper = new SearchDbHelper();
		
		loaded = true;
	}
	
	public Group Search(String str, boolean omitBackup) {
		if (searchHelper == null) { return null; }
		
		Group group = searchHelper.search(this, str, omitBackup
		);
		
		return group;
		
	}
	
	public void SaveData() throws IOException, PwDbOutputException {
		SaveData(mFilename);
	}
	
	public void SaveData(String filename) throws IOException, PwDbOutputException {
		File tempFile = new File(filename + ".tmp");
		FileOutputStream fos = new FileOutputStream(tempFile);
		//BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		//PwDbV3Output pmo = new PwDbV3Output(kdb, bos, App.getCalendar());
		PwDbOutput pmo = PwDbOutput.getInstance(kdb, fos);
		pmo.output();
		//bos.flush();
		//bos.close();
		fos.close();
		
		// Force data to disk before continuing
		try {
			fos.getFD().sync();
		} catch (SyncFailedException e) {
			// Ignore if fsync fails. We tried.
		}
		
		File orig = new File(filename);
		
		if ( ! tempFile.renameTo(orig) ) {
			throw new IOException("Failed to store database.");
		}
		
		mFilename = filename;
		
	}
	
	public void clear() {
		dirty.clear();
		
		kdb = null;
		mFilename = null;
		loaded = false;
	}
	
	public void markAllGroupsAsDirty() {
		for ( Group group : kdb.getGroups() ) {
			dirty.add(group);
		}
		
		// TODO: This should probably be abstracted out
		// The root group in v3 is not an 'official' group
		if ( kdb instanceof KDBV3) {
			dirty.add(kdb.rootGroup);
		}
	}
	
	
}

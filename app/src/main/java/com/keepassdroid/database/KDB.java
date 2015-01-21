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
package com.keepassdroid.database;

import com.keepassdroid.crypto.finalkey.FinalKey;
import com.keepassdroid.crypto.finalkey.FinalKeyFactory;
import com.keepassdroid.database.exception.InvalidKeyFileException;
import com.keepassdroid.database.exception.KeyFileEmptyException;
import com.keepassdroid.database.model.Entry;
import com.keepassdroid.database.model.Group;
import com.keepassdroid.stream.NullOutputStream;

import java.io.*;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class KDB {

	public byte masterKey[] = new byte[32];
	public byte[] finalKey;
	public String name = "KeePass database";
	public Group rootGroup;
	public PwIconFactory iconFactory = new PwIconFactory();
	public Map<String, Group> groups = new HashMap<String, Group>();
	public Map<UUID, Entry> entries = new HashMap<UUID, Entry>();
	
	
	public void makeFinalKey(byte[] masterSeed, byte[] masterSeed2, int numRounds) throws IOException {

		// Write checksum Checksum
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("SHA-256 not implemented here.");
		}
		NullOutputStream nos = new NullOutputStream();
		DigestOutputStream dos = new DigestOutputStream(nos, md);

		byte[] transformedMasterKey = transformMasterKey(masterSeed2, masterKey, numRounds); 
		dos.write(masterSeed);
		dos.write(transformedMasterKey);

		finalKey = md.digest();
	}
	
	/**
	 * Encrypt the master key a few times to make brute-force key-search harder
	 * @throws IOException 
	 */
	private static byte[] transformMasterKey( byte[] pKeySeed, byte[] pKey, int rounds ) throws IOException
	{
		FinalKey key = FinalKeyFactory.createFinalKey();
		
		return key.transformMasterKey(pKeySeed, pKey, rounds);
	}


	public abstract byte[] getMasterKey(String key, String keyFileName) throws InvalidKeyFileException, IOException;
	
	public void setMasterKey(String key, String keyFileName)
			throws InvalidKeyFileException, IOException {
				assert( key != null && keyFileName != null );
			
				masterKey = getMasterKey(key, keyFileName);
			}

	protected byte[] getCompositeKey(String key, String keyFileName)
			throws InvalidKeyFileException, IOException {
				assert(key != null && keyFileName != null);
				
				byte[] fileKey = getFileKey(keyFileName);
				
				byte[] passwordKey = getPasswordKey(key);
				
				MessageDigest md;
				try {
					md = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
					throw new IOException("SHA-256 not supported");
				}
				
				md.update(passwordKey);
				
				return md.digest(fileKey);
	}

	protected byte[] getFileKey(String fileName)
			throws InvalidKeyFileException, IOException {
				assert(fileName != null);
				
				File keyfile = new File(fileName);
				
				if ( ! keyfile.exists() ) {
					throw new InvalidKeyFileException();
				}
				
				byte[] key = loadXmlKeyFile(fileName);
				if ( key != null ) {
					return key;
				}
								
				FileInputStream fis;
				try {
					fis = new FileInputStream(keyfile);
				} catch (FileNotFoundException e) {
					throw new InvalidKeyFileException();
				}
				
				BufferedInputStream bis = new BufferedInputStream(fis, 64);
				
				long fileSize = keyfile.length();
				if ( fileSize == 0 ) {
					throw new KeyFileEmptyException();
				} else if ( fileSize == 32 ) {
					byte[] outputKey = new byte[32];
					if ( bis.read(outputKey, 0, 32) != 32 ) {
						throw new IOException("Error reading key.");
					}
					
					return outputKey;
				} else if ( fileSize == 64 ) {
					byte[] hex = new byte[64];
					
					bis.mark(64);
					if ( bis.read(hex, 0, 64) != 64 ) {
						throw new IOException("Error reading key.");
					}
			
					try {
						return hexStringToByteArray(new String(hex));
					} catch (IndexOutOfBoundsException e) {
						// Key is not base 64, treat it as binary data
						bis.reset();
					}
				}
			
				MessageDigest md;
				try {
					md = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
					throw new IOException("SHA-256 not supported");
				}
				//SHA256Digest md = new SHA256Digest();
				byte[] buffer = new byte[2048];
				int offset = 0;
				
				try {
					while (true) {
						int bytesRead = bis.read(buffer, 0, 2048);
						if ( bytesRead == -1 ) break;  // End of file
						
						md.update(buffer, 0, bytesRead);
						offset += bytesRead;
						
					}
				} catch (Exception e) {
					System.out.println(e.toString());
				}
			
				return md.digest();
			}

	protected abstract byte[] loadXmlKeyFile(String fileName);

	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	protected byte[] getPasswordKey(String key, String encoding) throws IOException {
		assert(key!=null);
		
		if ( key.length() == 0 )
		    throw new IllegalArgumentException( "Key cannot be empty." );
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("SHA-256 not supported");
		}

		byte[] bKey;
		try {
			bKey = key.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			assert false;
			bKey = key.getBytes();
		}
		md.update(bKey, 0, bKey.length );

		return md.digest();
	}
	
	public abstract byte[] getPasswordKey(String key) throws IOException;

	public abstract List<Group> getGrpRoots();
	
	public abstract List<Group> getGroups();

	public abstract List<Entry> getEntries();
	
	public abstract long getNumRounds();
	
	public abstract void setNumRounds(long rounds) throws NumberFormatException;
	
	public abstract boolean appSettingsEnabled();
	
	public abstract PwEncryptionAlgorithm getEncAlgorithm();
	
	public void addGroupTo(Group newGroup, Group parent) {
		// Add group to parent group
		if ( parent == null ) {
			parent = rootGroup;
		}
		
		parent.childGroups.add(newGroup);
		newGroup.setParent(parent);
		groups.put(newGroup.getId(), newGroup);
		
		parent.touch(true, true);
	}
	
	public void removeGroupFrom(Group remove, Group parent) {
		// Remove group from parent group
		parent.childGroups.remove(remove);
		
		groups.remove(remove.getId());
	}
	
	public void addEntryTo(Entry newEntry, Group parent) {
		// Add entry to parent
		if (parent != null) {
			parent.childEntries.add(newEntry);
		}
		newEntry.setParent(parent);
		
		entries.put(newEntry.getUUID(), newEntry);
	}
	
	public void removeEntryFrom(Entry remove, Group parent) {
		// Remove entry for parent
		if (parent != null) {
			parent.childEntries.remove(remove);
		}
		entries.remove(remove.getUUID());
	}

	public abstract String newGroupId();
	
	/**
	 * Determine if an id number is already in use
	 * 
	 * @param id
	 *            ID number to check for
	 * @return True if the ID is used, false otherwise
	 */
	protected boolean isGroupIdUsed(String id) {
		List<Group> groups = getGroups();
		
		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);
			if (group.getId().equals(id)) {
				return true;
			}
		}

		return false;
	}
	
	public abstract Group createGroup(String name);
	
	public abstract boolean isBackup(Group group);
	
	public void populateGlobals(Group currentGroup) {
		List<Group> childGroups = currentGroup.childGroups;
		List<Entry> childEntries = currentGroup.childEntries;
		
		for (int i = 0; i < childEntries.size(); i++ ) {
			Entry cur = childEntries.get(i);
			entries.put(cur.getUUID(), cur);
		}
		
		for (int i = 0; i < childGroups.size(); i++ ) {
			Group cur = childGroups.get(i);
			groups.put(cur.getId(), cur);
			populateGlobals(cur);
		}
	}
	
	public boolean canRecycle(Group group) {
		return false;
	}
	
	public boolean canRecycle(Entry entry) {
		return false;
	}
	
	public void recycle(Entry entry) {
		// Assume calls to this are protected by calling inRecyleBin
		throw new RuntimeException("Call not valid for .kdb databases.");
	}
	
	public void undoRecycle(Entry entry, Group origParent) {
		throw new RuntimeException("Call not valid for .kdb databases.");
	}
	
	public void deleteEntry(Entry entry) {
		Group parent = entry.getParent();
		removeEntryFrom(entry, parent);
		parent.touch(false, true);
		
	}
	
	public void undoDeleteEntry(Entry entry, Group origParent) {
		addEntryTo(entry, origParent);
	}
	
	public Group getRecycleBin() {
		return null;
	}
	
	public boolean isGroupSearchable(Group group, boolean omitBackup) {
		return group != null;
	}
	
}

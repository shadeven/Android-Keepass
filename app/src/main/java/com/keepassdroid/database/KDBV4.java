/*
 * Copyright 2010-2013 Brian Pellin.
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

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.keepassdroid.database.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import biz.source_code.base64Coder.Base64Coder;

import com.keepassdroid.database.exception.InvalidKeyFileException;


public class KDBV4 extends KDB {

	public static final Date DEFAULT_NOW = new Date();
    public static final UUID UUID_ZERO = new UUID(0,0);
	private static final int DEFAULT_HISTORY_MAX_ITEMS = 10; // -1 unlimited
	private static final long DEFAULT_HISTORY_MAX_SIZE = 6 * 1024 * 1024; // -1 unlimited
	private static final String RECYCLEBIN_NAME = "RecycleBin";
	
	public UUID dataCipher;
	public PwCompressionAlgorithm compressionAlgorithm;
    public long numKeyEncRounds;
    public long nameChanged = DEFAULT_NOW.getTime();
    public String description;
    public long descriptionChanged = DEFAULT_NOW.getTime();
    public String defaultUserName;
    public long defaultUserNameChanged = DEFAULT_NOW.getTime();
    
    public long keyLastChanged = DEFAULT_NOW.getTime();
    public long keyChangeRecDays = -1;
    public long keyChangeForceDays = 1;
    
    public long maintenanceHistoryDays = 365;
    public String color = "";
    public boolean recycleBinEnabled;
    public UUID recycleBinUUID = null;
    public long recycleBinChanged = DEFAULT_NOW.getTime();
    public UUID entryTemplatesGroup;
    public long entryTemplatesGroupChanged = DEFAULT_NOW.getTime();
    public int historyMaxItems = DEFAULT_HISTORY_MAX_ITEMS;
    public long historyMaxSize = DEFAULT_HISTORY_MAX_SIZE;
    public UUID lastSelectedGroup;
    public UUID lastTopVisibleGroup;
    public MemoryProtectionConfig memoryProtection = new MemoryProtectionConfig();
    public List<PwDeletedObject> deletedObjects = new ArrayList<PwDeletedObject>();
    public List<PwIconCustom> customIcons = new ArrayList<PwIconCustom>();
    public Map<String, String> customData = new HashMap<String, String>();
    
    public String localizedAppName = "KeePassDroid";
    
    public class MemoryProtectionConfig {
    	public boolean protectTitle = false;
    	public boolean protectUserName = false;
    	public boolean protectPassword = false;
    	public boolean protectUrl = false;
    	public boolean protectNotes = false;
    	
    	public boolean autoEnableVisualHiding = false;
    	
    	public boolean GetProtection(String field) {
    		if ( field.equalsIgnoreCase(PwDefsV4.TITLE_FIELD)) return protectTitle;
    		if ( field.equalsIgnoreCase(PwDefsV4.USERNAME_FIELD)) return protectUserName;
    		if ( field.equalsIgnoreCase(PwDefsV4.PASSWORD_FIELD)) return protectPassword;
    		if ( field.equalsIgnoreCase(PwDefsV4.URL_FIELD)) return protectUrl;
    		if ( field.equalsIgnoreCase(PwDefsV4.NOTES_FIELD)) return protectNotes;
    		
    		return false;
    	}
    }
    
	@Override
	public byte[] getMasterKey(String key, String keyFileName)
			throws InvalidKeyFileException, IOException {
		assert( key != null && keyFileName != null );
		
		byte[] fKey;
		
		if ( key.length() > 0 && keyFileName.length() > 0 ) {
			return getCompositeKey(key, keyFileName);
		} else if ( key.length() > 0 ) {
			fKey =  getPasswordKey(key);
		} else if ( keyFileName.length() > 0 ) {
			fKey = getFileKey(keyFileName);
		} else {
			throw new IllegalArgumentException( "Key cannot be empty." );
		}
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("No SHA-256 implementation");
		}
		
		return md.digest(fKey);
	}

    @Override
	public byte[] getPasswordKey(String key) throws IOException {
		return getPasswordKey(key, "UTF-8");
	}
    
	private static final String RootElementName = "KeyFile";
	//private static final String MetaElementName = "Meta";
	//private static final String VersionElementName = "Version";
	private static final String KeyElementName = "Key";
	private static final String KeyDataElementName = "Data";
	
	@Override
	protected byte[] loadXmlKeyFile(String fileName) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			FileInputStream fis = new FileInputStream(fileName);
			Document doc = db.parse(fis);
			
			Element el = doc.getDocumentElement();
			if (el == null || ! el.getNodeName().equalsIgnoreCase(RootElementName)) {
				return null;
			}
			
			NodeList children = el.getChildNodes();
			if (children.getLength() < 2) {
				return null;
			}
			
			for ( int i = 0; i < children.getLength(); i++ ) {
				Node child = children.item(i);
				
				if ( child.getNodeName().equalsIgnoreCase(KeyElementName) ) {
					NodeList keyChildren = child.getChildNodes();
					for ( int j = 0; j < keyChildren.getLength(); j++ ) {
						Node keyChild = keyChildren.item(j);
						if ( keyChild.getNodeName().equalsIgnoreCase(KeyDataElementName) ) {
							NodeList children2 = keyChild.getChildNodes();
							for ( int k = 0; k < children2.getLength(); k++) {
								Node text = children2.item(k);
								if (text.getNodeType() == Node.TEXT_NODE) {
									Text txt = (Text) text;
									return Base64Coder.decode(txt.getNodeValue());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@Override
	public List<Group> getGroups() {
		List<Group> list = new ArrayList<Group>();
		GroupV4 root = (GroupV4) rootGroup;
		root.buildChildGroupsRecursive(list);
		
		return list;
	}

	@Override
	public List<Group> getGrpRoots() {
		return rootGroup.childGroups;
	}

	@Override
	public List<Entry> getEntries() {
		List<Entry> list = new ArrayList<Entry>();
		GroupV4 root = (GroupV4) rootGroup;
		root.buildChildEntriesRecursive(list);
		
		return list;
	}

	@Override
	public long getNumRounds() {
		return numKeyEncRounds;
	}

	@Override
	public void setNumRounds(long rounds) throws NumberFormatException {
		numKeyEncRounds = rounds;
		
	}

	@Override
	public boolean appSettingsEnabled() {
		return false;
	}

	@Override
	public PwEncryptionAlgorithm getEncAlgorithm() {
		return PwEncryptionAlgorithm.Rjindal;
	}

	@Override
	public String newGroupId() {
		UUID id;
		
		while (true) {
			id = UUID.randomUUID();
			
			if (!isGroupIdUsed(id.toString())) break;
		}
		
		return id.toString();
	}

	@Override
	public Group createGroup(String name) {
		return new GroupV4(name, newGroupId());
	}

	@Override
	public boolean isBackup(Group group) {
		if (!recycleBinEnabled) {
			return false;
		}
		
		return group.isContainedIn(getRecycleBin());
	}

	@Override
	public void populateGlobals(Group currentGroup) {
		groups.put(rootGroup.getId(), rootGroup);
		super.populateGlobals(currentGroup);
	}
	
	/** Ensure that the recycle bin group exists, if enabled and create it
	 *  if it doesn't exist 
	 *  
	 */
	private void ensureRecycleBin() {
		if (getRecycleBin() == null) {
			// Create recycle bin
			
			GroupV4 recycleBin = new GroupV4(RECYCLEBIN_NAME, UUID.randomUUID().toString());
			// GroupV4 recycleBin = new GroupV4(true, true, RECYCLEBIN_NAME, iconFactory.getIcon(PwIconStandard.TRASH_BIN));
			recycleBin.enableAutoType = false;
			recycleBin.enableSearching = false;
			recycleBin.isExpanded = false;
			addGroupTo(recycleBin, rootGroup);
			
			recycleBinUUID = recycleBin.uuid;
		}
	}
	
	@Override
	public boolean canRecycle(Group group) {
		if (!recycleBinEnabled) {
			return false;
		}
		
		Group recycle = getRecycleBin();
		
		return (recycle == null) || (!group.isContainedIn(recycle));
	}

	@Override
	public boolean canRecycle(Entry entry) {
		if (!recycleBinEnabled) {
			return false;
		}
		
		Group parent = entry.getParent();
		return (parent != null) && canRecycle(parent);
	}
	
	@Override
	public void recycle(Entry entry) {
		ensureRecycleBin();
		
		Group parent = entry.getParent();
		removeEntryFrom(entry, parent);
		parent.touch(false, true);
		
		Group recycleBin = getRecycleBin();
		addEntryTo(entry, recycleBin);
		
		entry.touch(false, true);
		entry.touchLocation();
	}

	@Override
	public void undoRecycle(Entry entry, Group origParent) {
		
		Group recycleBin = getRecycleBin();
		removeEntryFrom(entry, recycleBin);
		
		addEntryTo(entry, origParent);
	}

	@Override
	public void deleteEntry(Entry entry) {
		super.deleteEntry(entry);
		
		deletedObjects.add(new PwDeletedObject(entry.getUUID()));
	}

	@Override
	public void undoDeleteEntry(Entry entry, Group origParent) {
		super.undoDeleteEntry(entry, origParent);
		
		deletedObjects.remove(entry);
	}

	@Override
	public GroupV4 getRecycleBin() {
		if (recycleBinUUID == null) {
			return null;
		}

		return (GroupV4) groups.get(recycleBinUUID.toString());
	}

	@Override
	public boolean isGroupSearchable(Group group, boolean omitBackup) {
		if (!super.isGroupSearchable(group, omitBackup)) {
			return false;
		}
		
		GroupV4 g = (GroupV4) group;
		
		return g.isSearchEnabled();
	}
	
}
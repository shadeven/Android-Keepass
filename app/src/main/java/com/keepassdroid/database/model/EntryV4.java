/*
 * Copyright 2010-2014 Brian Pellin.
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
package com.keepassdroid.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.*;

import com.keepassdroid.database.*;
import com.keepassdroid.database.security.ProtectedBinary;
import com.keepassdroid.database.security.ProtectedString;
import com.keepassdroid.utils.SprEngine;

public class EntryV4 extends Entry implements ITimeLogger {
	public static final String STR_TITLE = "Title";
	public static final String STR_USERNAME = "UserName";
	public static final String STR_PASSWORD = "Password";
	public static final String STR_URL = "URL";
	public static final String STR_NOTES = "Notes";
	
	public GroupV4 parent;
	public UUID uuid = KDBV4.UUID_ZERO;
	public HashMap<String, ProtectedString> strings = new HashMap<String, ProtectedString>();
	public HashMap<String, ProtectedBinary> binaries = new HashMap<String, ProtectedBinary>();
	public PwIconCustom customIcon = PwIconCustom.ZERO;
	public String foregroundColor = "";
	public String backgroupColor = "";
	public String overrideURL = "";
	public AutoType autoType = new AutoType();
	public ArrayList<EntryV4> history = new ArrayList<EntryV4>();
	
	private Date parentGroupLastMod = KDBV4.DEFAULT_NOW;
	private Date creation = KDBV4.DEFAULT_NOW;
	private Date lastMod = KDBV4.DEFAULT_NOW;
	private Date lastAccess = KDBV4.DEFAULT_NOW;
	private Date expireDate = KDBV4.DEFAULT_NOW;
	private boolean expires = false;
	private long usageCount = 0;
	public String url = "";
	public String additional = "";
	public String tags = "";

	public class AutoType implements Cloneable {
		private static final long OBF_OPT_NONE = 0;
		
		public boolean enabled = true;
		public long obfuscationOptions = OBF_OPT_NONE;
		public String defaultSequence = "";
		
		private HashMap<String, String> windowSeqPairs = new HashMap<String, String>();
		
		@SuppressWarnings("unchecked")
		public Object clone() {
			AutoType auto;
			try {
				auto = (AutoType) super.clone();
			} 
			catch (CloneNotSupportedException e) {
				assert(false);
				throw new RuntimeException(e);
			}
			
			auto.windowSeqPairs = (HashMap<String, String>) windowSeqPairs.clone();
			
			return auto;
			
		}
		
		public void put(String key, String value) {
			windowSeqPairs.put(key, value);
		}
		
		public Set<Map.Entry<String, String>> entrySet() {
			return windowSeqPairs.entrySet();
		}

	}
	
	public EntryV4() {

	}
	
	public EntryV4(GroupV4 p) {
		this(p, true, true);
	}
	
	public EntryV4(GroupV4 p, boolean initId, boolean initDates) {
		parent = p;
		
		if (initId) {
			uuid = UUID.randomUUID();
		}
		
		if (initDates) {
			Calendar cal = Calendar.getInstance();
			Date now = cal.getTime();
			creation = now;
			lastAccess = now;
			lastMod = now;
			expires = false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Entry clone(boolean deepStrings) {
		EntryV4 entry = (EntryV4) super.clone(deepStrings);
		
		if (deepStrings) {
			entry.strings = (HashMap<String, ProtectedString>) strings.clone();
		}
		
		return entry;
	}
	
	@SuppressWarnings("unchecked")
	public EntryV4 cloneDeep() {
		EntryV4 entry = (EntryV4) clone(true);
		
		entry.binaries = (HashMap<String, ProtectedBinary>) binaries.clone();
		entry.history = (ArrayList<EntryV4>) history.clone();
		entry.autoType = (AutoType) autoType.clone();

		return entry;
	}

	@Override
	public void assign(Entry source) {
		
		if ( ! (source instanceof EntryV4) ) {
			throw new RuntimeException("DB version mix.");
		}
		
		super.assign(source);
		
		EntryV4 src = (EntryV4) source;
		assign(src);
	}

	private void assign(EntryV4 source) {
		parent = source.parent;
		uuid = source.uuid;
		strings = source.strings;
		binaries = source.binaries;
		customIcon = source.customIcon;
		foregroundColor = source.foregroundColor;
		backgroupColor = source.backgroupColor;
		overrideURL = source.overrideURL;
		autoType = source.autoType;
		history = source.history;
		parentGroupLastMod = source.parentGroupLastMod;
		creation = source.creation;
		lastMod = source.lastMod;
		lastAccess = source.lastAccess;
		expireDate = source.expireDate;
		expires = source.expires;
		usageCount = source.usageCount;
		url = source.url;
		additional = source.additional;
		
	}
	
	@Override
	public Object clone() {
		EntryV4 newEntry = (EntryV4) super.clone();
		
		return newEntry;
	}
	
	private String decodeRefKey(boolean decodeRef, String key, KDB db) {
		String text = getString(key);
		if (decodeRef) {
			text = decodeRef(text, db);
		}
		
		return text;
	}

	private String decodeRef(String text, KDB db) {
		if (db == null) { return text; }
		
		SprEngine spr = SprEngine.getInstance(db);
		return spr.compile(text, this, db);
	}

	@Override
	public String getUsername(boolean decodeRef, KDB db) {
		return decodeRefKey(decodeRef, STR_USERNAME, db);
	}

	@Override
	public String getTitle(boolean decodeRef, KDB db) {
		return decodeRefKey(decodeRef, STR_TITLE, db);
	}
	
	@Override
	public String getPassword(boolean decodeRef, KDB db) {
		return decodeRefKey(decodeRef, STR_PASSWORD, db);
	}

	@Override
	public Date getLastAccessTime() {
		return lastAccess;
	}

	@Override
	public Date getCreationTime() {
		return creation;
	}

	@Override
	public Date getExpiryTime() {
		return expireDate;
	}

	@Override
	public Date getLastModificationTime() {
		return lastMod;
	}

	@Override
	public void setTitle(String title, KDB d) {
		KDBV4 db = (KDBV4) d;
		boolean protect = db.memoryProtection.protectTitle;
		
		setString(STR_TITLE, title, protect);
	}

	@Override
	public void setUsername(String user, KDB d) {
		KDBV4 db = (KDBV4) d;
		boolean protect = db.memoryProtection.protectUserName;
		
		setString(STR_USERNAME, user, protect);
	}

	@Override
	public void setPassword(String pass, KDB d) {
		KDBV4 db = (KDBV4) d;
		boolean protect = db.memoryProtection.protectPassword;
		
		setString(STR_PASSWORD, pass, protect);
	}

	@Override
	public void setUrl(String url, KDB d) {
		KDBV4 db = (KDBV4) d;
		boolean protect = db.memoryProtection.protectUrl;
		
		setString(STR_URL, url, protect);
	}

	@Override
	public void setNotes(String notes, KDB d) {
		KDBV4 db = (KDBV4) d;
		boolean protect = db.memoryProtection.protectNotes;
		
		setString(STR_NOTES, notes, protect);
	}

	public void setCreationTime(Date date) {
		creation = date;
	}

	public void setExpiryTime(Date date) {
		expireDate = date;
	}

	public void setLastAccessTime(Date date) {
		lastAccess = date;
	}

	public void setLastModificationTime(Date date) {
		lastMod = date;
	}

	@Override
	public GroupV4 getParent() {
		return parent;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}


	@Override
	public void setUUID(UUID u) {
		uuid = u;
	}
	
	public String getString(String key) {
		ProtectedString value = strings.get(key);
		
		if ( value == null ) return new String("");
		
		return value.toString();
	}

	public void setString(String key, String value, boolean protect) {
		ProtectedString ps = new ProtectedString(protect, value);
		strings.put(key, ps);
	}

	public Date getLocationChanged() {
		return parentGroupLastMod;
	}

	public long getUsageCount() {
		return usageCount;
	}

	public void setLocationChanged(Date date) {
		parentGroupLastMod = date;
	}

	public void setUsageCount(long count) {
		usageCount = count;
	}
	
	@Override
	public boolean expires() {
		return expires;
	}

	public void setExpires(boolean exp) {
		expires = exp;
	}

	@Override
	public String getNotes(boolean decodeRef, KDB db) {
		return decodeRefKey(decodeRef, STR_NOTES, db);
	}

	@Override
	public String getUrl(boolean decodeRef, KDB db) {
		return decodeRefKey(decodeRef, STR_URL, db);
	}

	@Override
	public PwIcon getIcon() {
		if (customIcon == null || customIcon.uuid.equals(KDBV4.UUID_ZERO)) {
			return super.getIcon();
		} else {
			return customIcon;
		}
		
	}

	public static boolean IsStandardString(String key) {
		return key.equals(STR_TITLE) || key.equals(STR_USERNAME) 
		  || key.equals(STR_PASSWORD) || key.equals(STR_URL)
		  || key.equals(STR_NOTES);
	}
	
	public void createBackup(KDBV4 db) {
		EntryV4 copy = cloneDeep();
		copy.history = new ArrayList<EntryV4>();
		history.add(copy);
		
		if (db != null) maintainBackups(db);
	}
	
	private boolean maintainBackups(KDBV4 db) {
		boolean deleted = false;
		
		int maxItems = db.historyMaxItems;
		if (maxItems >= 0) {
			while (history.size() > maxItems) {
				removeOldestBackup();
				deleted = true;
			}
		}
		
		long maxSize = db.historyMaxSize;
		if (maxSize >= 0) {
			while(true) {
				long histSize = 0;
				for (EntryV4 entry : history) {
					histSize += entry.getSize();
				}
				
				if (histSize > maxSize) {
					removeOldestBackup();
					deleted = true;
				} else {
					break;
				}
			}
		}
		
		return deleted;
	}
	
	private void removeOldestBackup() {
		Date min = null;
		int index = -1;
		
		for (int i = 0; i < history.size(); i++) {
			Entry entry = history.get(i);
			Date lastMod = entry.getLastModificationTime();
			if ((min == null) || lastMod.before(min)) {
				index = i;
				min = lastMod;
			}
		}
		
		if (index != -1) {
			history.remove(index);
		}
	}
	
	
	private static final long FIXED_LENGTH_SIZE = 128; // Approximate fixed length size
	public long getSize() {
		long size = FIXED_LENGTH_SIZE;
		
		for (Map.Entry<String,ProtectedString> pair : strings.entrySet()) {
			size += pair.getKey().length();
			size += pair.getValue().length();
		}
		
		for (Map.Entry<String,ProtectedBinary> pair : binaries.entrySet()) {
			size += pair.getKey().length();
			size += pair.getValue().length();
		}
		
		size += autoType.defaultSequence.length();
		for (Map.Entry<String,String> pair : autoType.entrySet()) {
			size += pair.getKey().length();
			size += pair.getValue().length();
		}
		
		for (EntryV4 entry : history) {
			size += entry.getSize();
		}
		
		size += overrideURL.length();
		size += tags.length();
		
		return size;
	}

	@Override
	public void touch(boolean modified, boolean touchParents) {
		super.touch(modified, touchParents);
		
		++usageCount;
	}

	@Override
	public void touchLocation() {
		parentGroupLastMod = new Date();
	}
	
	@Override
	public void setParent(Group parent) {
		this.parent = (GroupV4) parent;
	}
	
	public boolean isSearchingEnabled() {
		if (parent != null) {
			return parent.isSearchEnabled();
		}
		
		return GroupV4.DEFAULT_SEARCHING_ENABLED;
	}
  
  /* Parcelable */

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(uuid.toString()); // ID
  }

  public static final Parcelable.Creator<EntryV4> CREATOR
      = new Parcelable.Creator<EntryV4>() {
    public EntryV4 createFromParcel(Parcel in) {
      return new EntryV4(in);
    }

    public EntryV4[] newArray(int size) {
      return new EntryV4[size];
    }
  };

  private EntryV4(Parcel in) {
    
  }
}

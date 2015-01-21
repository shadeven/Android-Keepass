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
package com.keepassdroid.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.keepassdroid.database.ITimeLogger;
import com.keepassdroid.database.KDBV4;
import com.keepassdroid.database.PwIcon;
import com.keepassdroid.database.PwIconCustom;

import java.util.List;
import java.util.UUID;

public class GroupV4 extends Group implements ITimeLogger {

	//public static final int FOLDER_ICON = 48;
	public static final boolean DEFAULT_SEARCHING_ENABLED = true;

	public GroupV4 parent = null;
	public UUID uuid = KDBV4.UUID_ZERO;
	public String notes = "";
	public PwIconCustom customIcon = PwIconCustom.ZERO;
	public boolean isExpanded = true;
	public String defaultAutoTypeSequence = "";
	public Boolean enableAutoType = null;
	public Boolean enableSearching = null;
	public UUID lastTopVisibleEntry = KDBV4.UUID_ZERO;
	private long parentGroupLastMod = System.currentTimeMillis();
	private long creation = System.currentTimeMillis();
	private long lastMod = System.currentTimeMillis();
	private long lastAccess = System.currentTimeMillis();
	private long expireDate = System.currentTimeMillis();
	private boolean expires = false;
	private long usageCount = 0;

	public GroupV4() {
		super();
	}

	public GroupV4(String name, String id) {
		super(name, id);
		uuid = UUID.fromString(id);
		creation = lastMod = lastAccess = System.currentTimeMillis();
	}

	public void AddGroup(GroupV4 subGroup, boolean takeOwnership) {
		AddGroup(subGroup, takeOwnership, false);
	}

	public void AddGroup(GroupV4 subGroup, boolean takeOwnership, boolean updateLocationChanged) {
		if ( subGroup == null ) throw new RuntimeException("subGroup");

		childGroups.add(subGroup);

		if ( takeOwnership ) subGroup.parent = this;

		if ( updateLocationChanged ) subGroup.parentGroupLastMod = System.currentTimeMillis();

	}

	public void AddEntry(EntryV4 pe, boolean takeOwnership) {
		AddEntry(pe, takeOwnership, false);
	}

	public void AddEntry(EntryV4 pe, boolean takeOwnership, boolean updateLocationChanged) {
		assert(pe != null);

		childEntries.add(pe);

		if ( takeOwnership ) pe.parent = this;

		if ( updateLocationChanged ) pe.setLocationChanged(System.currentTimeMillis());
	}

	@Override
	public Group getParent() {
		return parent;
	}

	public void buildChildGroupsRecursive(List<Group> list) {
		list.add(this);

		for ( int i = 0; i < childGroups.size(); i++) {
			GroupV4 child = (GroupV4) childGroups.get(i);
			child.buildChildGroupsRecursive(list);

		}
	}

	public void buildChildEntriesRecursive(List<Entry> list) {
		for ( int i = 0; i < childEntries.size(); i++ ) {
			list.add(childEntries.get(i));
		}

		for ( int i = 0; i < childGroups.size(); i++ ) {
			GroupV4 child = (GroupV4) childGroups.get(i);
			child.buildChildEntriesRecursive(list);
		}

	}

	@Override
	public String getId() {
		return uuid.toString();
	}

	@Override
	public void setId(String id) {
		uuid = UUID.fromString(id);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getLastMod() {
		return parentGroupLastMod;
	}

	public long getCreationTime() {
		return creation;
	}

	public long getExpiryTime() {
		return expireDate;
	}

	public long getLastAccessTime() {
		return lastAccess;
	}

	public long getLastModificationTime() {
		return lastMod;
	}

	public long getLocationChanged() {
		return parentGroupLastMod;
	}

	public long getUsageCount() {
		return usageCount;
	}

	public void setCreationTime(long date) {
		creation = date;

	}

	public void setExpiryTime(long date) {
		expireDate = date;
	}

	@Override
	public void setLastAccessTime(long date) {
		lastAccess = date;
	}

	@Override
	public void setLastModificationTime(long date) {
		lastMod = date;
	}

	public void setLocationChanged(long date) {
		parentGroupLastMod = date;
	}

	public void setUsageCount(long count) {
		usageCount = count;
	}

	public boolean expires() {
		return expires;
	}

	public void setExpires(boolean exp) {
		expires = exp;
	}

	@Override
	public void setParent(Group prt) {
		parent = (GroupV4) prt;
	}

	@Override
	public PwIcon getIcon() {
		if (customIcon == null || customIcon.uuid.equals(KDBV4.UUID_ZERO)) {
			return super.getIcon();
		} else {
			return customIcon;
		}
	}

	public boolean isSearchEnabled() {
		GroupV4 group = this;
		while (group != null) {
			Boolean search = group.enableSearching;
			if (search != null) {
				return search;
			}

			group = group.parent;
		}

		// If we get to the root group and its null, default to true
		return true;
	}
  
  /* Parcelable */

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel out, int flags) {
    super.writeToParcel(out, flags);
    out.writeString(uuid.toString());                 // ID
    out.writeString(lastTopVisibleEntry.toString());  // Last top visible entry ID
    out.writeString(notes);                           // Notes
    out.writeString(defaultAutoTypeSequence);         // Auto type sequence
    out.writeInt(enableAutoType ? 1 : 0);             // Enable auto type
    out.writeInt(enableSearching ? 1 : 0);            // Enable search
    out.writeInt(expires ? 1 : 0);                    // Expires
    out.writeLong(parentGroupLastMod);                // Parent group last mod date
    out.writeLong(creation);                          // Creation date
    out.writeLong(lastMod);                           // Last mod date
    out.writeLong(lastAccess);                        // Last access date
    out.writeLong(expireDate);                        // Expire date
    out.writeLong(usageCount);                        // Usage count
    out.writeParcelable(parent, flags);               // Parent
  }

  public static final Parcelable.Creator<GroupV4> CREATOR
      = new Parcelable.Creator<GroupV4>() {
    public GroupV4 createFromParcel(Parcel in) {
      return new GroupV4(in);
    }

    public GroupV4[] newArray(int size) {
      return new GroupV4[size];
    }
  };

  private GroupV4(Parcel in) {
    super(in);
    uuid = UUID.fromString(in.readString());
    lastTopVisibleEntry = UUID.fromString(in.readString());
    notes = in.readString();
    defaultAutoTypeSequence = in.readString();
    enableAutoType = in.readInt() == 1;
    enableSearching = in.readInt() == 1;
    expires = in.readInt() == 1;
    parentGroupLastMod = in.readLong();
    creation = in.readLong();
    lastMod = in.readLong();
    lastAccess = in.readLong();
    expireDate = in.readLong();
    usageCount = in.readLong();
    parent = in.readParcelable(GroupV4.class.getClassLoader());
  }
  
}

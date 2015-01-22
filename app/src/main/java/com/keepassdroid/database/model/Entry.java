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
package com.keepassdroid.database.model;

import android.os.Parcelable;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import com.keepassdroid.database.KDB;
import com.keepassdroid.database.PwIcon;
import com.keepassdroid.database.PwIconStandard;
import com.keepassdroid.database.iterator.EntrySearchStringIterator;

public abstract class Entry implements Cloneable, Parcelable {

  protected static final String PMS_TAN_ENTRY = "<TAN>";

  public static class EntryNameComparator implements Comparator<Entry> {

    public int compare(Entry object1, Entry object2) {
      return object1.getTitle().compareToIgnoreCase(object2.getTitle());
    }

  }

  public PwIconStandard icon = PwIconStandard.FIRST;

  public static Entry getInstance(Group parent) {
    return Entry.getInstance(parent, true, true);
  }

  public static Entry getInstance(Group parent, boolean initId, boolean initDates) {
    if (parent instanceof GroupV3) {
      return new EntryV3((GroupV3) parent);
    } else if (parent instanceof GroupV4) {
      return new EntryV4((GroupV4) parent);
    } else {
      throw new RuntimeException("Unknow PwGroup instance.");
    }
  }

  @Override
  public Object clone() {
    Entry newEntry;
    try {
      newEntry = (Entry) super.clone();
    } catch (CloneNotSupportedException e) {
      assert (false);
      throw new RuntimeException("Clone should be supported");
    }

    return newEntry;
  }

  public Entry clone(boolean deepStrings) {
    return (Entry) clone();
  }

  public void assign(Entry source) {
    icon = source.icon;
  }

  public abstract UUID getUUID();

  public abstract void setUUID(UUID u);

  public String getTitle() {
    return getTitle(false, null);
  }

  public String getUsername() {
    return getUsername(false, null);
  }

  public String getPassword() {
    return getPassword(false, null);
  }

  public String getUrl() {
    return getUrl(false, null);
  }

  public String getNotes() {
    return getNotes(false, null);
  }

  public abstract String getTitle(boolean decodeRef, KDB db);

  public abstract String getUsername(boolean decodeRef, KDB db);

  public abstract String getPassword(boolean decodeRef, KDB db);

  public abstract String getUrl(boolean decodeRef, KDB db);

  public abstract String getNotes(boolean decodeRef, KDB db);

  public abstract long getCreationTime();

  public abstract long getLastModificationTime();

  public abstract long getLastAccessTime();

  public abstract long getExpiryTime();

  public abstract boolean expires();

  public abstract Group getParent();

  public abstract void setTitle(String title, KDB db);

  public abstract void setUsername(String user, KDB db);

  public abstract void setPassword(String pass, KDB db);

  public abstract void setUrl(String url, KDB db);

  public abstract void setNotes(String notes, KDB db);

  public abstract void setCreationTime(long create);

  public abstract void setLastModificationTime(long mod);

  public abstract void setLastAccessTime(long access);

  public abstract void setExpires(boolean exp);

  public abstract void setExpiryTime(long expires);


  public PwIcon getIcon() {
    return icon;
  }

  public boolean isTan() {
    return getTitle().equals(PMS_TAN_ENTRY) && (getUsername().length() > 0);
  }

  public String getDisplayTitle() {
    if (isTan()) {
      return PMS_TAN_ENTRY + " " + getUsername();
    } else {
      return getTitle();
    }
  }

  public boolean isMetaStream() {
    return false;
  }

  public EntrySearchStringIterator stringIterator() {
    return EntrySearchStringIterator.getInstance(this);
  }

  public void touch(boolean modified, boolean touchParents) {
    long now = System.currentTimeMillis();

    setLastAccessTime(now);

    if (modified) {
      setLastModificationTime(now);
    }

    Group parent = getParent();
    if (touchParents && parent != null) {
      parent.touch(modified, true);
    }
  }

  public void touchLocation() {
  }

  public abstract void setParent(Group parent);

  public boolean isSearchingEnabled() {
    return false;
  }

}

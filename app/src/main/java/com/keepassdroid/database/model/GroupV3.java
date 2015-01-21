/*
 * Copyright 2009 Brian Pellin.

This file was derived from

Copyright 2007 Naomaru Itoi <nao@phoneid.org>

This file was derived from 

Java clone of KeePass - A KeePass file viewer for Java
Copyright 2006 Bill Zwicky <billzwicky@users.sourceforge.net>

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

import com.keepassdroid.database.KDBV3;
import com.keepassdroid.database.PwDate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;



/**
 * @author Brian Pellin <bpellin@gmail.com>
 * @author Naomaru Itoi <nao@phoneid.org>
 * @author Bill Zwicky <wrzwicky@pobox.com>
 * @author Dominik Reichl <dominik.reichl@t-online.de>
 */
public class GroupV3 extends Group {
	
	public static final Date NEVER_EXPIRE = EntryV3.NEVER_EXPIRE;
	
	/** Size of byte buffer needed to hold this struct. */
	public static final int BUF_SIZE = 124;

	// for tree traversing
	public GroupV3 parent = null;

	public int groupId;

	public long tCreation;
	public long tLastMod;
	public long tLastAccess;
	public long tExpire;

	public int level; // short

	/** Used by KeePass internally, don't use */
	public int flags;

	public GroupV3() {
		super();
	}

	public GroupV3(String name, String id) {
		super(name, id);
		long now = System.currentTimeMillis();
		tCreation = now;
		tLastAccess = now;
		tLastMod = now;
		tExpire = NEVER_EXPIRE.getTime();
	}

	public void setGroups(List<Group> groups) {
		childGroups = groups;
	}
	
	@Override
	public Group getParent() {
		return parent;
	}

	@Override
	public String getId() {
		return Integer.toString(groupId);
	}

	@Override
	public void setId(String id) {
		groupId = Integer.parseInt(id);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getLastMod() {
		return tLastMod;
	}

	@Override
	public void setParent(Group prt) {
		parent = (GroupV3) prt;
		level = parent.level + 1;
	}
	
	public void populateBlankFields(KDBV3 db) {
		if (icon == null) {
			icon = db.iconFactory.getIcon(1);
		}
		
		if (name == null) {
			name = "";
		}
		
		if (tCreation == 0) {
			tCreation = EntryV3.DEFAULT_DATE.getTime();
		}
		
		if (tLastMod == 0) {
			tLastMod = EntryV3.DEFAULT_DATE.getTime();
		}
		
		if (tLastAccess == 0) {
			tLastAccess = EntryV3.DEFAULT_DATE.getTime();
		}
		
		if (tExpire == 0) {
			tExpire = EntryV3.DEFAULT_DATE.getTime();
		}
	}

	@Override
	public void setLastAccessTime(long date) {
		tLastAccess = date;
	}

	@Override
	public void setLastModificationTime(long date) {
		tLastMod = date;
	}
	
	@Override
	public String toString() {
		return name;
	}
}

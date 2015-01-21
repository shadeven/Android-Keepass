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
package com.keepassdroid.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import com.keepassdroid.database.*;
import com.keepassdroid.database.model.Entry;
import com.keepassdroid.database.model.Group;
import com.keepassdroid.database.model.GroupV3;
import com.keepassdroid.database.model.GroupV4;

public class SearchDbHelper {
	
	public Group search(DatabaseManager db, String qStr, boolean omitBackup) {
		KDB pm = db.kdb;

		Group group;
		if ( pm instanceof KDBV3) {
			group = new GroupV3();
		} else if ( pm instanceof KDBV4) {
			group = new GroupV4();
		} else {
			return null;
		}
		group.name = "Search Results";
		group.childEntries = new ArrayList<Entry>();
		
		// Search all entries
		Locale loc = Locale.getDefault();
		qStr = qStr.toLowerCase(loc);
		
		Queue<Group> worklist = new LinkedList<Group>();
		if (pm.rootGroup != null) {
			worklist.add(pm.rootGroup);
		}
		
		while (worklist.size() != 0) {
			Group top = worklist.remove();
			
			if (pm.isGroupSearchable(top, omitBackup)) {
				for (Entry entry : top.childEntries) {
					processEntries(entry, group.childEntries, qStr, loc);
				}
				
				for (Group childGroup : top.childGroups) {
					if (childGroup != null) {
						worklist.add(childGroup);
					}
				}
			}
		}
		
		return group;
	}
	
	public void processEntries(Entry entry, List<Entry> results, String qStr, Locale loc) {
		// Search all strings in the entry
		Iterator<String> iter = entry.stringIterator();
		while (iter.hasNext()) {
			String str = iter.next();
			if (str != null && str.length() != 0) {
				String lower = str.toLowerCase(loc);
				if (lower.contains(qStr)) {
					results.add(entry);
					break;
				}
			}
		}
	}
	
}

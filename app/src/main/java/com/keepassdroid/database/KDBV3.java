/*
 * Copyright 2009-2011 Brian Pellin.
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
 *

Derived from

KeePass for J2ME

Copyright 2007 Naomaru Itoi <nao@phoneid.org>

This file was derived from 

Java clone of KeePass - A KeePass file viewer for Java
Copyright 2006 Bill Zwicky <billzwicky@users.sourceforge.net>

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.keepassdroid.database;

// Java
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.keepassdroid.database.exception.InvalidKeyFileException;
import com.keepassdroid.database.model.*;

/**
 * @author Naomaru Itoi <nao@phoneid.org>
 * @author Bill Zwicky <wrzwicky@pobox.com>
 * @author Dominik Reichl <dominik.reichl@t-online.de>
 */
public class KDBV3 extends KDB {
	// Constants
	// private static final int PWM_SESSION_KEY_SIZE = 12;

	// Special entry for settings
	public Entry metaInfo;

	// all entries
	public List<Entry> entries = new ArrayList<Entry>();
	// all groups
	public List<Group> groups = new ArrayList<Group>();
	// Algorithm used to encrypt the database
	public PwEncryptionAlgorithm algorithm;
	public int numKeyEncRounds;
	
	@Override
	public PwEncryptionAlgorithm getEncAlgorithm() {
		return algorithm;
	}

	public int getNumKeyEncRecords() {
		return numKeyEncRounds;
	}

	@Override
	public List<Group> getGroups() {
		return groups;
	}

	@Override
	public List<Entry> getEntries() {
		return entries;
	}

	public void setGroups(List<Group> grp) {
		groups = grp;
	}

	@Override
	public List<Group> getGrpRoots() {
		int target = 0;
		List<Group> kids = new ArrayList<Group>();
		for (int i = 0; i < groups.size(); i++) {
			GroupV3 grp = (GroupV3) groups.get(i);
			if (grp.level == target)
				kids.add(grp);
		}
		return kids;
	}

	public int getRootGroupId() {
		for (int i = 0; i < groups.size(); i++) {
			GroupV3 grp = (GroupV3) groups.get(i);
			if (grp.level == 0) {
				return grp.groupId;
			}
		}

		return -1;
	}

	public List<Group> getGrpChildren(GroupV3 parent) {
		int idx = groups.indexOf(parent);
		int target = parent.level + 1;
		List<Group> kids = new ArrayList<Group>();
		while (++idx < groups.size()) {
			GroupV3 grp = (GroupV3) groups.get(idx);
			if (grp.level < target)
				break;
			else if (grp.level == target)
				kids.add(grp);
		}
		return kids;
	}

	public List<Entry> getEntries(GroupV3 parent) {
		List<Entry> kids = new ArrayList<Entry>();
		/*
		 * for( Iterator i = entries.iterator(); i.hasNext(); ) { PwEntryV3 ent
		 * = (PwEntryV3)i.next(); if( ent.groupId == parent.groupId ) kids.add(
		 * ent ); }
		 */
		for (int i = 0; i < entries.size(); i++) {
			EntryV3 ent = (EntryV3) entries.get(i);
			if (ent.groupId == parent.groupId)
				kids.add(ent);
		}
		return kids;
	}

	public String toString() {
		return name;
	}

	public void constructTree(GroupV3 currentGroup) {
		// I'm in root
		if (currentGroup == null) {
			GroupV3 root = new GroupV3();
			rootGroup = root;

			List<Group> rootChildGroups = getGrpRoots();
			root.setGroups(rootChildGroups);
			root.childEntries = new ArrayList<Entry>();
			root.level = -1;
			for (int i = 0; i < rootChildGroups.size(); i++) {
				GroupV3 grp = (GroupV3) rootChildGroups.get(i);
				grp.parent = root;
				constructTree(grp);
			}
			return;
		}

		// I'm in non-root
		// get child groups
		currentGroup.setGroups(getGrpChildren(currentGroup));
		currentGroup.childEntries = getEntries(currentGroup);

		// set parent in child entries
		for (int i = 0; i < currentGroup.childEntries.size(); i++) {
			EntryV3 entry = (EntryV3) currentGroup.childEntries.get(i);
			entry.parent = currentGroup;
		}
		// recursively construct child groups
		for (int i = 0; i < currentGroup.childGroups.size(); i++) {
			GroupV3 grp = (GroupV3) currentGroup.childGroups.get(i);
			grp.parent = currentGroup;
			constructTree((GroupV3) currentGroup.childGroups.get(i));
		}
		return;
	}

	/*
	public void removeGroup(PwGroupV3 group) {
		group.parent.childGroups.remove(group);
		groups.remove(group);
	}
	*/

	/**
	 * Generates an unused random group id
	 * 
	 * @return new group id
	 */
	@Override
	public String newGroupId() {
		int newId;
		Random random = new Random();

		while (true) {
			newId = random.nextInt();
			if (!isGroupIdUsed(Integer.toString(newId))) break;
		}

		return Integer.toString(newId);
	}

	public byte[] getMasterKey(String key, String keyFileName)
			throws InvalidKeyFileException, IOException {
		assert (key != null && keyFileName != null);

		if (key.length() > 0 && keyFileName.length() > 0) {
			return getCompositeKey(key, keyFileName);
		} else if (key.length() > 0) {
			return getPasswordKey(key);
		} else if (keyFileName.length() > 0) {
			return getFileKey(keyFileName);
		} else {
			throw new IllegalArgumentException("Key cannot be empty.");
		}

	}

	public byte[] getPasswordKey(String key) throws IOException {
		return getPasswordKey(key, "ISO-8859-1");
	}
	
	@Override
	protected byte[] loadXmlKeyFile(String fileName) {
		return null;
	}



	@Override
	public long getNumRounds() {
		return numKeyEncRounds;
	}

	@Override
	public void setNumRounds(long rounds) throws NumberFormatException {
		if (rounds > Integer.MAX_VALUE || rounds < Integer.MIN_VALUE) {
			throw new NumberFormatException();
		}

		numKeyEncRounds = (int) rounds;
	}

	@Override
	public boolean appSettingsEnabled() {
		return true;
	}

	@Override
	public void addEntryTo(Entry newEntry, Group parent) {
		super.addEntryTo(newEntry, parent);
		
		// Add entry to root entries
		entries.add(newEntry);
		
	}

	@Override
	public void addGroupTo(Group newGroup, Group parent) {
		super.addGroupTo(newGroup, parent);
		
		// Add group to root groups
		groups.add(newGroup);
		
	}

	@Override
	public void removeEntryFrom(Entry remove, Group parent) {
		super.removeEntryFrom(remove, parent);
		
		// Remove entry from root entry
		entries.remove(remove);
	}

	@Override
	public void removeGroupFrom(Group remove, Group parent) {
		super.removeGroupFrom(remove, parent);
		
		// Remove group from root entry
		groups.remove(remove);
	}

	@Override
	public Group createGroup(String name) {
		GroupV3 group = new GroupV3(name, newGroupId());
		return group;
	}
	
	// TODO: This could still be refactored cleaner
	public void copyEncrypted(byte[] buf, int offset, int size) {
		// No-op
	}

	// TODO: This could still be refactored cleaner
	public void copyHeader(PwDbHeaderV3 header) {
		// No-op
	}
	@Override
	public boolean isBackup(Group group) {
		GroupV3 g = (GroupV3) group;
		while (g != null) {
			if (g.level == 0 && g.name.equalsIgnoreCase("Backup")) {
				return true;
			}
			
			g = g.parent;
		}
		
		return false;
	}

	@Override
	public boolean isGroupSearchable(Group group, boolean omitBackup) {
		if (!super.isGroupSearchable(group, omitBackup)) {
			return false;
		}
		
		return !(omitBackup && isBackup(group));
	}
}

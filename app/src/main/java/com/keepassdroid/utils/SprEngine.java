/*
 * Copyright 2014 Brian Pellin.
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
package com.keepassdroid.utils;

import com.keepassdroid.database.KDB;
import com.keepassdroid.database.KDBV4;
import com.keepassdroid.database.model.Entry;

public class SprEngine {
	
	private static SprEngineV4 sprV4 = new SprEngineV4();
	private static SprEngine spr = new SprEngine();
	
	public static SprEngine getInstance(KDB db) {
		if (db instanceof KDBV4) {
            return sprV4;
		} 
		else {
            return spr;
		}
	}
	
	public String compile(String text, Entry entry, KDB database) {
		return text;
	}

}

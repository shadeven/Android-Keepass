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

This file was derived from 

Copyright 2007 Naomaru Itoi <nao@phoneid.org>

This file was derived from 

Java clone of KeePass - A KeePass file viewer for Java
Copyright 2006 Bill Zwicky <billzwicky@users.sourceforge.net>

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.keepassdroid.database.model;

// PhoneID
import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import com.keepassdroid.database.KDB;
import com.keepassdroid.database.KDBV3;
import com.keepassdroid.database.PwDate;
import com.keepassdroid.utils.Types;


/**
 * Structure containing information about one entry.
 * 
 * <PRE>
 * One entry: [FIELDTYPE(FT)][FIELDSIZE(FS)][FIELDDATA(FD)]
 *            [FT+FS+(FD)][FT+FS+(FD)][FT+FS+(FD)][FT+FS+(FD)][FT+FS+(FD)]...
 *            
 * [ 2 bytes] FIELDTYPE
 * [ 4 bytes] FIELDSIZE, size of FIELDDATA in bytes
 * [ n bytes] FIELDDATA, n = FIELDSIZE
 * 
 * Notes:
 *  - Strings are stored in UTF-8 encoded form and are null-terminated.
 *  - FIELDTYPE can be one of the FT_ constants.
 * </PRE>
 *
 * @author Naomaru Itoi <nao@phoneid.org>
 * @author Bill Zwicky <wrzwicky@pobox.com>
 * @author Dominik Reichl <dominik.reichl@t-online.de>
 */
public class EntryV3 extends Entry {

	public static final Date NEVER_EXPIRE = getNeverExpire();
	public static final Date NEVER_EXPIRE_BUG = getNeverExpireBug();
	public static final Date DEFAULT_DATE = getDefaultDate();

	/** Size of byte buffer needed to hold this struct. */
	public static final String PMS_ID_BINDESC = "bin-stream";
	public static final String PMS_ID_TITLE   = "Meta-Info";
	public static final String PMS_ID_USER    = "SYSTEM";
	public static final String PMS_ID_URL     = "$";

	public int              groupId;
	public String 					username;
	private byte[]          password;
	private byte[]          uuid;
	public String title;
	public String url;
	public String additional;


	public long             tCreation;
	public long             tLastMod;
	public long             tLastAccess;
	public long             tExpire;

	/** A string describing what is in pBinaryData */
	public String           binaryDesc;
	private byte[]          binaryData;

  // for tree traversing
  public GroupV3 parent = null;

	private static Date getDefaultDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2004);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		return cal.getTime();
	}

	private static Date getNeverExpire() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2999);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH, 28);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		return cal.getTime();
	}
	
	/** This date was was accidentally being written
	 *  out when an entry was supposed to be marked as
	 *  expired. We'll use this to silently correct those
	 *  entries.
	 * @return
	 */
	private static Date getNeverExpireBug() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2999);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH, 30);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		return cal.getTime();
	}

	public static boolean IsNever(long date) {
		return NEVER_EXPIRE.getTime() == date;
	}


	public EntryV3() {
		super();
	}

	/*
	public PwEntryV3(PwEntryV3 source) {
		assign(source);
	}
	*/
	
	public EntryV3(GroupV3 p) {
		this(p, true, true);
	}

	public EntryV3(GroupV3 p, boolean initId, boolean initDates) {

		parent = p;
		groupId = Integer.parseInt(parent.getId());

		if (initId) {
			Random random = new Random();
			uuid = new byte[16];
			random.nextBytes(uuid);
		}
		
		if (initDates) {
			long now = System.currentTimeMillis();
			tCreation = now;
			tLastAccess = now;
			tLastMod = now;
			tExpire = NEVER_EXPIRE.getTime();
		}

	}
	
	/**
	 * @return the actual password byte array.
	 */
	@Override
	public String getPassword(boolean decodeRef, KDB db) {
		if (password == null) {
			return "";
		}
		
		return new String(password);
	}
	
	public byte[] getPasswordBytes() {
		return password;
	}


	/**
	 * fill byte array
	 */
	private static void fill(byte[] array, byte value)
	{
		for (int i=0; i<array.length; i++)
			array[i] = value;
		return;
	}

	/** Securely erase old password before copying new. */
	public void setPassword( byte[] buf, int offset, int len ) {
		if( password != null ) {
			fill( password, (byte)0 );
			password = null;
		}
		password = new byte[len];
		System.arraycopy( buf, offset, password, 0, len );
	}



	@Override
	public void setPassword(String pass, KDB db) {
		byte[] password;
		try {
			password = pass.getBytes("UTF-8");
			setPassword(password, 0, password.length);
		} catch (UnsupportedEncodingException e) {
			assert false;
			password = pass.getBytes();
			setPassword(password, 0, password.length);
		}
	}

	/**
	 * @return the actual binaryData byte array.
	 */
	public byte[] getBinaryData() {
		return binaryData;
	}

	/** Securely erase old data before copying new. */
	public void setBinaryData( byte[] buf, int offset, int len ) {
		if( binaryData != null ) {
			fill( binaryData, (byte)0 );
			binaryData = null;
		}
		binaryData = new byte[len];
		System.arraycopy( buf, offset, binaryData, 0, len );
	}

	// Determine if this is a MetaStream entry
	@Override
	public boolean isMetaStream() {
		if ( binaryData == null ) return false;
		if ( additional == null || additional.length() == 0 ) return false;
		if ( ! binaryDesc.equals(PMS_ID_BINDESC) ) return false;
		if ( title == null ) return false;
		if ( ! title.equals(PMS_ID_TITLE) ) return false;
		if ( username == null ) return false;
		if ( ! username.equals(PMS_ID_USER) ) return false;
		if ( url == null ) return false;
		if ( ! url.equals(PMS_ID_URL)) return false;
		if ( !icon.isMetaStreamIcon() ) return false;

		return true;
	}
	
	@Override
	public void assign(Entry source) {
		
		if ( ! (source instanceof EntryV3) ) {
			throw new RuntimeException("DB version mix");
		}
		
		super.assign(source);
		
		EntryV3 src = (EntryV3) source;
		assign(src);
	
	}

	private void assign(EntryV3 source) {
		title = source.title;
		url = source.url;
		groupId = source.groupId;
		username = source.username;
		additional = source.additional;
		uuid = source.uuid;

		int passLen = source.password.length;
		password = new byte[passLen]; 
		System.arraycopy(source.password, 0, password, 0, passLen);

		tCreation = source.tCreation;
		tLastMod = source.tLastMod;
		tLastAccess = source.tLastAccess;
		tExpire = source.tExpire;

		binaryDesc = source.binaryDesc;

		if ( source.binaryData != null ) {
			int descLen = source.binaryData.length;
			binaryData = new byte[descLen]; 
			System.arraycopy(source.binaryData, 0, binaryData, 0, descLen);
		}

		parent = source.parent;

	}
	
	@Override
	public Object clone() {
		EntryV3 newEntry = (EntryV3) super.clone();
		
		if (password != null) {
			int passLen = password.length;
			password = new byte[passLen]; 
			System.arraycopy(password, 0, newEntry.password, 0, passLen);
		}

		newEntry.tCreation = tCreation;
		newEntry.tLastMod = tLastMod;
		newEntry.tLastAccess = tLastAccess;
		newEntry.tExpire = tExpire;
		
		newEntry.binaryDesc = binaryDesc;

		if ( binaryData != null ) {
			int descLen = binaryData.length;
			newEntry.binaryData = new byte[descLen]; 
			System.arraycopy(binaryData, 0, newEntry.binaryData, 0, descLen);
		}

		newEntry.parent = parent;

		
		return newEntry;
	}

	@Override
	public long getLastAccessTime() {
		return tLastAccess;
	}

	@Override
	public long getCreationTime() {
		return tCreation;
	}

	@Override
	public long getExpiryTime() {
		return tExpire;
	}

	@Override
	public long getLastModificationTime() {
		return tLastMod;
	}

	@Override
	public void setCreationTime(long create) {
		tCreation = create;
		
	}

	@Override
	public void setLastModificationTime(long mod) {
		tLastMod = mod;
		
	}

	@Override
	public void setLastAccessTime(long access) {
		tLastAccess = access;
		
	}

	@Override
	public void setExpires(boolean expires) {
		if (!expires) {
			tExpire = NEVER_EXPIRE.getTime();
		}
	}

	@Override
	public void setExpiryTime(long expires) {
		tExpire = expires;
	}

	@Override
	public GroupV3 getParent() {
		return parent;
	}

	@Override
	public UUID getUUID() {
		return Types.bytestoUUID(uuid);
	}

	@Override
	public void setUUID(UUID u) {
		uuid = Types.UUIDtoBytes(u);
	}

	@Override
	public String getUsername(boolean decodeRef, KDB db) {
		if (username == null) {
			return "";
		}
		
		return username;
	}

	@Override
	public void setUsername(String user, KDB db) {
		username = user;
	}

	@Override
	public String getTitle(boolean decodeRef, KDB db) {
        return title;
	}

	@Override
	public void setTitle(String title, KDB db) {
		this.title = title;
	}

	@Override
	public String getNotes(boolean decodeRef, KDB db) {
		return additional;
	}

	@Override
	public void setNotes(String notes, KDB db) {
		additional = notes;
	}

	@Override
	public String getUrl(boolean decodeRef, KDB db) {
		return url;
	}

	@Override
	public void setUrl(String url, KDB db) {
		this.url = url;
	}

	@Override
	public boolean expires() {
		return ! IsNever(tExpire);
	}
	
	public void populateBlankFields(KDBV3 db) {
		if (icon == null) {
			icon = db.iconFactory.getIcon(1);
		}
		
		if (username == null) {
			username = "";
		}
		
		if (password == null) {
			password = new byte[0];
		}
		
		if (uuid == null) {
			uuid = Types.UUIDtoBytes(UUID.randomUUID());
		}
		
		if (title == null) {
			title = "";
		}
		
		if (url == null) {
			url = "";
		}
		
		if (additional == null) {
			additional = "";
		}
		
		if (tCreation == 0) {
			tCreation = DEFAULT_DATE.getTime();
		}
		
		if (tLastMod == 0) {
			tLastMod = DEFAULT_DATE.getTime();
		}
		
		if (tLastAccess == 0) {
			tLastAccess = DEFAULT_DATE.getTime();
		}
		
		if (tExpire == 0) {
			tExpire = NEVER_EXPIRE.getTime();
		}
		
		if (binaryDesc == null) {
			binaryDesc = "";
		}
		
		if (binaryData == null) {
			binaryData = new byte[0];
		}
	}

	@Override
	public void setParent(Group parent) {
		this.parent = (GroupV3) parent;
	}
  
  /* Parcelable */

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByteArray(uuid);        // ID
    dest.writeByteArray(password);    // Password
    dest.writeByteArray(binaryData);  // Binary data
    dest.writeString(username);       // Username
    dest.writeString(title);          // Title
    dest.writeString(url);            // URL
    dest.writeString(additional);     // Additional
    dest.writeString(binaryDesc);     // Binary description
    dest.writeInt(groupId);           // Group ID
  }

  public static final Parcelable.Creator<EntryV3> CREATOR
      = new Parcelable.Creator<EntryV3>() {
    public EntryV3 createFromParcel(Parcel in) {
      return new EntryV3(in);
    }

    public EntryV3[] newArray(int size) {
      return new EntryV3[size];
    }
  };

  private EntryV3(Parcel in) {

  }
}

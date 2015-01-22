package com.alexfu.keepass;

import android.test.AndroidTestCase;

import com.keepassdroid.database.DatabaseManager;
import com.keepassdroid.database.exception.InvalidDBException;
import com.keepassdroid.database.exception.PwDbOutputException;
import com.keepassdroid.database.model.Entry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class KeepassTest extends AndroidTestCase {
  public void testRead() {
    InputStream is = null;
    try {
      is = getContext().getAssets().open("test.kdb");
      DatabaseManager dbm = new DatabaseManager();
      dbm.LoadData(is, "123456", "");
      assertNotNull(dbm.kdb);
    } catch (IOException | InvalidDBException e) {
      e.printStackTrace();
      fail(e.getMessage());
    } finally {
      if (is != null) {
        try { is.close(); } catch (IOException e) { /* Silent*/ }
      }
    }
  }
  
  public void testWrite() {
    InputStream is = null;
    try {
      // Read original in
      is = getContext().getAssets().open("test.kdb");
      DatabaseManager dbm = new DatabaseManager();
      dbm.LoadData(is, "123456", "");
      assertNotNull(dbm.kdb);
      is.close();
      
      // Modify an Entry
      Entry entry = dbm.kdb.getEntries().get(0);
      String newTitle = Long.toString(System.currentTimeMillis());
      entry.setTitle(newTitle, dbm.kdb);
      
      // Save changes to a temp file
      File tempKdbFile = new File(getContext().getCacheDir(), "temp.kdb");      
      dbm.SaveData(tempKdbFile.getAbsolutePath());
      
      // Read temp file in
      dbm.LoadData(tempKdbFile.getAbsolutePath(), "123456", "");
      assertNotNull(dbm.kdb);
      
      // Verify changes
      entry = dbm.kdb.getEntries().get(0);
      assertEquals(newTitle, entry.getTitle());
      
      // Delete temp file
      assertTrue(tempKdbFile.delete());
    } catch (IOException | InvalidDBException e) {
      e.printStackTrace();
      fail(e.getMessage());
    } catch (PwDbOutputException e) {
      e.printStackTrace();
      fail(e.getMessage());
    } finally {
      if (is != null) {
        try { is.close(); } catch (IOException e) { /* Silent*/ }
      }
    }    
  }
}
package com.alexfu.keepass;

import android.test.AndroidTestCase;

import com.keepassdroid.database.DatabaseManager;
import com.keepassdroid.database.exception.InvalidDBException;

import java.io.IOException;
import java.io.InputStream;

public class KeepassTest extends AndroidTestCase {
  public void testRead() {
    try {
      InputStream is = getContext().getAssets().open("test.kdb");
      DatabaseManager dbm = new DatabaseManager();
      dbm.LoadData(is, "123456", "");
      assertNotNull(dbm.kdb);
    } catch (IOException | InvalidDBException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}
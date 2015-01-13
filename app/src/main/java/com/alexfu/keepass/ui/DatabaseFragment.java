package com.alexfu.keepass.ui;

import android.net.Uri;
import android.os.Bundle;

import pl.sind.keepass.kdb.KeePassDataBase;

public class DatabaseFragment extends BaseFragment {
  
  private KeePassDataBase kdb;
  
  private static final String KDB_URI = "kdb_uri";
  
  public static DatabaseFragment newInstance(Uri kdbUri) {
    Bundle args = new Bundle();
    args.putString(KDB_URI, kdbUri.getPath());
    DatabaseFragment fragment = new DatabaseFragment();
    fragment.setArguments(args);
    return fragment;
  }
}

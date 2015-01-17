package com.alexfu.keepass.ui;

import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

public class DatabaseFragment extends BaseFragment {
  
  private KeePassDataBaseV1 kdb;
  
  public static DatabaseFragment newInstance() {
    DatabaseFragment fragment = new DatabaseFragment();
    return fragment;
  }

  public void setDatabase(KeePassDataBaseV1 kdb) {
    this.kdb = kdb;
  }
}

package com.alexfu.keepass.ui.view;

import android.content.Context;

import pl.sind.keepass.kdb.KeePassDataBase;

public interface DatabaseView extends ViewClient {
  public void showAuthenticationView();
  public void onAuthenticated(KeePassDataBase kdb);
  public Context getAppContext();
}

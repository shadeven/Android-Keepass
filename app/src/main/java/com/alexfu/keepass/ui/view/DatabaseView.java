package com.alexfu.keepass.ui.view;

import android.content.Context;

import com.keepassdroid.database.KDB;

public interface DatabaseView extends ViewClient {
  public void showAuthenticationView();
  public void onAuthenticated(KDB kdb);
  public Context getAppContext();
}

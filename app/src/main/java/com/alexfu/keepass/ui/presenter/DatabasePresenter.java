package com.alexfu.keepass.ui.presenter;

import android.net.Uri;

import com.alexfu.keepass.ui.view.DatabaseView;

public class DatabasePresenter extends ViewPresenter {
  
  private DatabaseView view;
  private Uri kdbUri;
  
  public DatabasePresenter(DatabaseView view) {
    super(view);
    this.view = view;
  }
  
  public void openDatabase(Uri kdbUri) {
    this.kdbUri = kdbUri;
    view.showAuthenticationView();
  }

}

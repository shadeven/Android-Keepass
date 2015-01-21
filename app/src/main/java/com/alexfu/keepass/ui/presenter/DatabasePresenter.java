package com.alexfu.keepass.ui.presenter;

import android.content.ContentResolver;
import android.net.Uri;

import com.alexfu.keepass.ui.view.DatabaseView;
import com.keepassdroid.database.DatabaseManager;
import com.keepassdroid.database.KDB;
import com.keepassdroid.database.exception.InvalidDBException;

import java.io.IOException;
import java.io.InputStream;

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
  
  public void authenticate(String password) {
    try {
      KDB kdb = openDatabase(password);
      view.onAuthenticated(kdb);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidDBException e) {
      e.printStackTrace();
    }
  }
  
  private KDB openDatabase(String password) throws IOException, InvalidDBException {
    String scheme = kdbUri.getScheme();
    
    if (scheme.equals("content")) {
      return openWithContentResolver(password);
    }
    
    return null;
  }
  
  private KDB openWithContentResolver(String password) throws IOException, InvalidDBException {
    ContentResolver resolver = view.getAppContext().getContentResolver();
    InputStream is = resolver.openInputStream(kdbUri);
    DatabaseManager dbm = new DatabaseManager();
    dbm.LoadData(is, password, "");
    return dbm.kdb;
  }

}

package com.alexfu.keepass.ui.presenter;

import android.content.ContentResolver;
import android.net.Uri;

import com.alexfu.keepass.ui.view.DatabaseView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import pl.sind.keepass.exceptions.KeePassDataBaseException;
import pl.sind.keepass.exceptions.UnsupportedDataBaseException;
import pl.sind.keepass.kdb.KeePassDataBase;
import pl.sind.keepass.kdb.KeePassDataBaseManager;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

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
      KeePassDataBase kdb = openDatabase(password);
      view.onAuthenticated(kdb);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (KeePassDataBaseException e) {
      e.printStackTrace();
    }
  }
  
  private KeePassDataBase openDatabase(String password) throws IOException,
      KeePassDataBaseException {
    String scheme = kdbUri.getScheme();
    
    if (scheme.equals("content")) {
      return openWithContentResolver(password);
    }
    
    return null;
  }
  
  private KeePassDataBase openWithContentResolver(String password) throws KeePassDataBaseException, 
      IOException {
    ContentResolver resolver = view.getAppContext().getContentResolver();
    InputStream is = resolver.openInputStream(kdbUri);
    return KeePassDataBaseManager.openDataBase(is, null, password);
  }

}

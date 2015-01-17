package com.alexfu.keepass.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.presenter.DatabasePresenter;
import com.alexfu.keepass.ui.view.DatabaseView;

import pl.sind.keepass.kdb.KeePassDataBase;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

public class DatabaseActivity extends BaseActivity implements DatabaseView, 
    AuthenticationFragment.Callback {
  
  private DatabasePresenter presenter;
  private KeePassDataBaseV1 kdb;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    presenter = new DatabasePresenter(this);
    Uri kdbUri = getIntent().getData();
    presenter.openDatabase(kdbUri);
  }

  @Override
  public void showAuthenticationView() {
    AuthenticationFragment fragment = AuthenticationFragment.newInstance();
    fragment.setCallback(this);
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit();
  }

  @Override
  public void authenticate(String password) {
    presenter.authenticate(password);
  }

  @Override
  public void onAuthenticated(KeePassDataBase kdb) {
    this.kdb = (KeePassDataBaseV1) kdb;
    DatabaseFragment fragment = DatabaseFragment.newInstance();
    fragment.setDatabase(this.kdb);
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit();
  }

  @Override
  public Context getAppContext() {
    return getApplicationContext();
  }
}

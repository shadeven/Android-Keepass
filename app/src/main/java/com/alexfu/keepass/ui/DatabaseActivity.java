package com.alexfu.keepass.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.presenter.DatabasePresenter;
import com.alexfu.keepass.ui.view.DatabaseView;
import com.keepassdroid.database.KDB;

public class DatabaseActivity extends BaseActivity implements DatabaseView, 
    AuthenticationFragment.Callback {
  
  private DatabasePresenter presenter;
  private KDB kdb;
  
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
  public void onAuthenticated(KDB kdb) {
    this.kdb = kdb;
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

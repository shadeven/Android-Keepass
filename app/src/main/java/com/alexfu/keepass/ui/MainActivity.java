package com.alexfu.keepass.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.presenter.MainPresenter;
import com.alexfu.keepass.ui.view.MainView;


public class MainActivity extends BaseActivity implements MainView {

  private MainPresenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    presenter = new MainPresenter(this);
    presenter.fetchDatabases();
  }

  @Override
  public void onDatabasesLoaded() {
    // TODO
  }

  @Override
  public void onShowEmptyView() {
    // Show a list of service providers
    Fragment fragment = DBProviderFragment.newInstance();
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case DBProviderFragment.READ_REQUEST_CODE:
        if (resultCode == Activity.RESULT_OK) {
          // A URI to the file is given to us, instead of the file data.
          if (data != null) {
            Uri uri = data.getData();
            launchDatabaseActivity(uri);
          }
        }
        break;
    }
  } 
  
  private void launchDatabaseActivity(Uri kdbUri) {
    Intent intent = new Intent(this, DatabaseActivity.class);
    intent.setData(kdbUri);
    startActivity(intent);
  }

}

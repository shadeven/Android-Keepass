package com.alexfu.keepass.ui;

import android.net.Uri;
import android.os.Bundle;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.presenter.DatabasePresenter;
import com.alexfu.keepass.ui.view.DatabaseView;

public class DatabaseActivity extends BaseActivity implements DatabaseView {
  
  private DatabasePresenter presenter;
  
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
    // TODO
  }
}

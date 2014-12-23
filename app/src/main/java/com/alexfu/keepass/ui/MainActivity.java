package com.alexfu.keepass.ui;

import android.os.Bundle;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.presenter.DatabaseListPresenter;
import com.alexfu.keepass.ui.view.DatabaseListView;


public class MainActivity extends BaseActivity implements DatabaseListView {

  private DatabaseListPresenter presenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    presenter = new DatabaseListPresenter(this);
    presenter.fetchDatabases();
  }

  @Override
  public void onDatabasesLoaded() {
    // TODO
  }

  @Override
  public void onShowEmptyView() {
    // TODO
  }

}

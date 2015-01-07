package com.alexfu.keepass.ui.presenter;

import com.alexfu.keepass.ui.view.MainView;

public class MainPresenter extends ViewPresenter {

  private MainView view;

  public MainPresenter(MainView view) {
    super(view);
    this.view = view;
  }

  public void fetchDatabases() {
    // TODO:
    view.onShowEmptyView();
  }
}

package com.alexfu.keepass.ui.presenter;

import com.alexfu.keepass.ui.view.ViewClient;

/**
 * The Presenter in MVP.
 */
public abstract class ViewPresenter {

  private ViewClient view;

  public ViewPresenter(ViewClient view) {
    this.view = view;
  }

  public ViewClient getViewClient() {
    return view;
  }

}

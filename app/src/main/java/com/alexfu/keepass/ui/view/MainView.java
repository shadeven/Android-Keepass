package com.alexfu.keepass.ui.view;

/**
 * A specialized view specific to handling events relating to the main view.
 */
public interface MainView extends ViewClient {
  public void onDatabasesLoaded();
  public void onShowEmptyView();
}

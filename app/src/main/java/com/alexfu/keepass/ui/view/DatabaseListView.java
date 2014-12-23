package com.alexfu.keepass.ui.view;

/**
 * A specialized view specific to handling events relating to lists of
 * databases.
 */
public interface DatabaseListView extends ViewClient {
  public void onDatabasesLoaded();
  public void onShowEmptyView();
}

package com.alexfu.keepass.ui.provider;

import android.content.Intent;

/**
 * Provides a reference to a Keepass database file.
 */
public interface DBProvider {
  public String getName();
  public Intent getFilePickerIntent();
}

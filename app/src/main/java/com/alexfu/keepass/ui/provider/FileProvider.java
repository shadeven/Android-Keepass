package com.alexfu.keepass.ui.provider;

import android.content.Intent;

/**
 * Provides a reference to a Keepass database that is directly on the filesystem.
 */
public class FileProvider implements DBProvider {

  private String name;

  public FileProvider(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Intent getFilePickerIntent() {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("*/*");
    return intent;
  }
}

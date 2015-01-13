package com.alexfu.keepass.ui.provider;

import android.content.Intent;

/**
 * Provides a reference to a Keepass database provided by Androids Storage Access Framework.
 */
public class SAFProvider implements DBProvider {

  private String name;

  public SAFProvider(String name) {
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

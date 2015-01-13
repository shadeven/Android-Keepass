package com.alexfu.keepass.ui.provider;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;

/**
 * Provides a reference to a Keepass database provided by Androids Storage Access Framework.
 */
public class SAFProvider implements DBProvider {

  private String name, description;
  
  private SAFProvider(Builder builder) {
    name = builder.name;
    description = builder.description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Intent getFilePickerIntent() {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("*/*");
    return intent;
  }
  
  public static class Builder {
    private Context context;
    private String name, description;
    
    public Builder(Context context) {
      this.context = context.getApplicationContext();
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder name(@StringRes int stringResId) {
      this.name = context.getResources().getString(stringResId);
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder description(@StringRes int stringResId) {
      this.description = context.getResources().getString(stringResId);
      return this;
    }
    
    public SAFProvider build() {
      return new SAFProvider(this);
    }
  }
}

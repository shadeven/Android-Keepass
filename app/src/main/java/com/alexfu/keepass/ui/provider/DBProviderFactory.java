package com.alexfu.keepass.ui.provider;

import android.content.Context;

import com.alexfu.keepass.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A factory that creates various types of DBProviders.
 */
public class DBProviderFactory {
  private DBProviderFactory() {}

  public static List<DBProvider> getAll(Context context) {
    List<DBProvider> providers = new ArrayList<>();
    providers.add(
        new SAFProvider.Builder(context)
            .name(R.string.provider_saf_title)
            .description(R.string.provider_saf_description)
            .build()
    );
    return providers;
  }
}

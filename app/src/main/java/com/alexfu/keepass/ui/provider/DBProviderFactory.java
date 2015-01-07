package com.alexfu.keepass.ui.provider;

import java.util.ArrayList;
import java.util.List;

/**
 * A factory that creates various types of DBProviders.
 */
public class DBProviderFactory {
  private DBProviderFactory() {}

  public static List<DBProvider> getAll() {
    List<DBProvider> providers = new ArrayList<>();
    providers.add(new FileProvider("File"));
    return providers;
  }
}

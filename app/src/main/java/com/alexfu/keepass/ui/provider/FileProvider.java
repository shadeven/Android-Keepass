package com.alexfu.keepass.ui.provider;

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
}

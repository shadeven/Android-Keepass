package com.alexfu.keepass;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

/**
 * An Activity from which other Activities will inherit from.
 * Provides a convenient way to obtain an instance of Actionbar toolbar.
 */
public abstract class BaseActivity extends ActionBarActivity {

  private Toolbar actionBar;

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    getSupportActionbar();
  }

  public Toolbar getSupportActionbar() {
    if (actionBar == null) {
      actionBar = (Toolbar) findViewById(R.id.action_bar);
      setSupportActionBar(actionBar);
    }

    return actionBar;
  }
}

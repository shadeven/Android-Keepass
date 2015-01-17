package com.alexfu.keepass.ui;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.alexfu.keepass.R;

/**
 * An Activity from which other Activities will inherit from.
 * Provides a convenient way to obtain an instance of Actionbar toolbar.
 */
public abstract class BaseActivity extends ActionBarActivity {

  private Toolbar actionBar;

  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    getToolbarActionbar();
  }

  public Toolbar getToolbarActionbar() {
    if (actionBar == null) {
      actionBar = (Toolbar) findViewById(R.id.action_bar);
      setSupportActionBar(actionBar);
    }

    return actionBar;
  }
}

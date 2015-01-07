package com.alexfu.keepass.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * A Fragment from which other Fragments will inherit from.
 * Provides a convenient way to obtain an instance of Actionbar toolbar.
 * Must be attached to a BaseActivity.
 */
public abstract class BaseFragment extends Fragment {
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    boolean isInstanceOfBaseActivity = activity instanceof BaseActivity;
    if (!isInstanceOfBaseActivity) {
      String className = getClass().getName();
      throw new IllegalStateException(className + " must be attached to a BaseActivity!");
    }
  }
}

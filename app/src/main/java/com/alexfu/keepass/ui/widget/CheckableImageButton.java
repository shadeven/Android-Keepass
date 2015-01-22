package com.alexfu.keepass.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageButton;

public class CheckableImageButton extends ImageButton implements Checkable {

  private boolean mChecked;
  private boolean mBroadcasting;

  private OnCheckedChangeListener mOnCheckedChangeListener;

  private static final int[] CHECKED_STATE_SET = {
      android.R.attr.state_checked
  };

  /**
   * Interface definition for a callback to be invoked when the checked state changed.
   */
  public static interface OnCheckedChangeListener {
    /**
     * Called when the checked state has changed.
     *
     * @param button The button view whose state has changed.
     * @param isChecked  The new checked state of button.
     */
    void onCheckedChanged(CheckableImageButton button, boolean isChecked);
  }

  public CheckableImageButton(Context context) {
    super(context);
  }

  public CheckableImageButton(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CheckableImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean performClick() {
    toggle();
    return super.performClick();
  }

  /**
   * Register a callback to be invoked when the checked state of this button
   * changes.
   *
   * @param listener the callback to call on checked state change
   */
  public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
    mOnCheckedChangeListener = listener;
  }

  /**
   * <p>Changes the checked state of this button.</p>
   *
   * @param checked true to check the button, false to uncheck it
   */
  public void setChecked(boolean checked) {
    if (mChecked != checked) {
      mChecked = checked;
      refreshDrawableState();

      // Avoid infinite recursions if setChecked() is called from a listener
      if (mBroadcasting) {
        return;
      }

      mBroadcasting = true;
      if (mOnCheckedChangeListener != null) {
        mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
      }

      mBroadcasting = false;
    }
  }

  @Override
  public boolean isChecked() {
    return mChecked;
  }

  @Override
  public void toggle() {
    setChecked(!mChecked);
  }

  @Override
  public int[] onCreateDrawableState(int extraSpace) {
    final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
    if (isChecked()) {
      mergeDrawableStates(drawableState, CHECKED_STATE_SET);
    }
    return drawableState;
  }
}

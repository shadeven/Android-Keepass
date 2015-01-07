package com.alexfu.keepass.ui.adapter;

import android.view.View;

import butterknife.ButterKnife;

/**
 * A simple ViewHolder that uses ButterKnife to gain references to the views.
 */
public abstract class ButterViewHolder {
  public ButterViewHolder(View source) {
    ButterKnife.inject(this, source);
  }
}

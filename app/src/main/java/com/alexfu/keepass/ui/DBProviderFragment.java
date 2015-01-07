package com.alexfu.keepass.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexfu.keepass.R;

import butterknife.ButterKnife;

public class DBProviderFragment extends BaseFragment {
  public static DBProviderFragment newInstance() {
    DBProviderFragment fragment = new DBProviderFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_list, container, false);
    ButterKnife.inject(this, view);
    return view;
  }
}

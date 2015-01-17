package com.alexfu.keepass.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexfu.keepass.R;

import butterknife.ButterKnife;

public class AuthenticationFragment extends BaseFragment {
  
  public static AuthenticationFragment newInstance() {
    AuthenticationFragment fragment = new AuthenticationFragment();
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_authentication, container, false);
    ButterKnife.inject(this, view);
    return view;
  }
}
package com.alexfu.keepass.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alexfu.keepass.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AuthenticationFragment extends BaseFragment {
  
  @InjectView(R.id.btn_submit) Button submitButton;
  @InjectView(R.id.password) EditText passwordEditText;
  
  private Callback callback;
  
  public interface Callback {
    public void authenticate(String password);
  }
  
  public static AuthenticationFragment newInstance() {
    AuthenticationFragment fragment = new AuthenticationFragment();
    return fragment;
  }

  @Override
  public void onResume() {
    super.onResume();
    getSupportActionBar().setTitle(R.string.title_authentication);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_authentication, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (callback != null) {
          callback.authenticate(passwordEditText.getText().toString());
        }
      }
    });
  }
  
  public void setCallback(Callback callback) {
    this.callback = callback;    
  }
}

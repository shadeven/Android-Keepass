package com.alexfu.keepass.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.widget.CheckableImageButton;
import com.keepassdroid.database.model.Entry;
import com.keepassdroid.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EntryDetailsActivity extends BaseActivity {
  
  @InjectView(R.id.edit_username) EditText usernameEditText;
  @InjectView(R.id.edit_password) EditText passwordEditText;
  @InjectView(R.id.edit_url) EditText urlEditText;
  @InjectView(R.id.edit_created) EditText createdEditText;
  @InjectView(R.id.edit_modified) EditText modifiedEditText;
  @InjectView(R.id.edit_expires) EditText expiresEditText;
  @InjectView(R.id.button_launch_url) ImageButton launchUrlButton;
  @InjectView(R.id.button_password_visibility) CheckableImageButton togglePasswordVisibility;

  private Entry entry;
  
  public static final String EXTRA_ENTRY = "entry";
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_entry_details);
    ButterKnife.inject(this);
    entry = getIntent().getParcelableExtra(EXTRA_ENTRY);
    
    usernameEditText.setText(entry.getUsername());
    passwordEditText.setText(entry.getPassword());
    urlEditText.setText(entry.getUrl());
    
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd yyyy");
    createdEditText.setText(sdf.format(new Date(entry.getCreationTime())));
    modifiedEditText.setText(sdf.format(new Date(entry.getLastModificationTime())));
    expiresEditText.setText(sdf.format(new Date(entry.getExpiryTime())));
    
    launchUrlButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(entry.getUrl());
        if (uri.getScheme() == null) {
          uri = uri.buildUpon().scheme("http").build();
        }
        intent.setData(uri);
        startActivity(intent);
      }
    });
    
    togglePasswordVisibility.setOnCheckedChangeListener(new CheckableImageButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CheckableImageButton button, boolean isChecked) {
        if (isChecked) {
          passwordEditText.setTransformationMethod(new SingleLineTransformationMethod());
        } else {
          passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
        }
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    if (entry.getTitle() != null && !entry.getTitle().isEmpty()) {
      getSupportActionBar().setTitle(entry.getTitle());
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

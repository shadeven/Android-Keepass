package com.alexfu.keepass.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.adapter.DBProviderAdapter;
import com.alexfu.keepass.ui.provider.DBProviderFactory;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DBProviderFragment extends BaseFragment {

  @InjectView(android.R.id.list) ListView listView;

  private DBProviderAdapter listAdapter;

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

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    listAdapter = new DBProviderAdapter(getApplicationContext(), DBProviderFactory.getAll());
    listView.setAdapter(listAdapter);
  }
}

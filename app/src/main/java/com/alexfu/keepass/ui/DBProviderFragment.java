package com.alexfu.keepass.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.adapter.DBProviderAdapter;
import com.alexfu.keepass.ui.provider.DBProvider;
import com.alexfu.keepass.ui.provider.DBProviderFactory;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DBProviderFragment extends BaseFragment implements AdapterView.OnItemClickListener {

  @InjectView(android.R.id.list) ListView listView;

  private DBProviderAdapter listAdapter;
  private List<DBProvider> dbProviders;

  public static final int READ_REQUEST_CODE = 123;

  public static DBProviderFragment newInstance() {
    DBProviderFragment fragment = new DBProviderFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dbProviders = DBProviderFactory.getAll(getActivity());
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
    listAdapter = new DBProviderAdapter(getActivity(), dbProviders);
    listView.setOnItemClickListener(this);
    listView.setAdapter(listAdapter);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Intent intent = dbProviders.get(position).getFilePickerIntent();
    startActivityForResult(intent, READ_REQUEST_CODE);
  }
}

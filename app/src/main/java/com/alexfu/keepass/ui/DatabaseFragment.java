package com.alexfu.keepass.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.adapter.EntryAdapter;
import com.alexfu.keepass.ui.adapter.GroupAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.sind.keepass.kdb.v1.KeePassDataBaseV1;

public class DatabaseFragment extends BaseFragment {
  
  @InjectView(android.R.id.list) ListView listView;
  private Spinner spinner;
  
  private KeePassDataBaseV1 kdb;
  private GroupAdapter spinnerAdapter;
  private EntryAdapter listAdapter;
  
  public static DatabaseFragment newInstance() {
    DatabaseFragment fragment = new DatabaseFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setTitle("");
    
    spinner = new Spinner(getSupportActionBar().getThemedContext());
    getToolbarActionbar().addView(spinner);

    spinnerAdapter = new GroupAdapter(getActivity(), kdb.getGroups());
    spinner.setAdapter(spinnerAdapter);
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
    listAdapter = new EntryAdapter(getActivity(), kdb.getEntries());
    listView.setAdapter(listAdapter);
  }

  public void setDatabase(KeePassDataBaseV1 kdb) {
    this.kdb = kdb;
  }
}

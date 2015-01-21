package com.alexfu.keepass.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.adapter.EntryAdapter;
import com.alexfu.keepass.ui.adapter.GroupAdapter;
import com.keepassdroid.database.KDB;
import com.keepassdroid.database.model.Entry;
import com.keepassdroid.database.model.Group;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DatabaseFragment extends BaseFragment implements AdapterView.OnItemClickListener {
  
  @InjectView(android.R.id.list) ListView listView;
  private Spinner spinner;
  
  private KDB kdb;
  private int selectedGroupIndex;
  private GroupAdapter spinnerAdapter;
  private EntryAdapter listAdapter;
  
  private static final String TAG = "DatabaseFragment";
  
  public static DatabaseFragment newInstance() {
    DatabaseFragment fragment = new DatabaseFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setTitle("");
    
    if (kdb == null) return;

    spinner = new Spinner(getSupportActionBar().getThemedContext());
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedGroupIndex = position;
        listAdapter.replaceDataset(spinnerAdapter.getItem(position).childEntries);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    });
    getToolbarActionbar().addView(spinner);

    spinnerAdapter = new GroupAdapter(getSupportActionBar().getThemedContext(), kdb.getGroups());
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
    
    if (kdb == null) return;
    
    List<Entry> entries = spinnerAdapter.getItem(selectedGroupIndex).childEntries;
    listAdapter = new EntryAdapter(getActivity(), entries);
    listView.setAdapter(listAdapter);
    listView.setOnItemClickListener(this);
  }

  public void setDatabase(KDB kdb) {
    this.kdb = kdb;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    // TODO: Launch entry details
  }
}

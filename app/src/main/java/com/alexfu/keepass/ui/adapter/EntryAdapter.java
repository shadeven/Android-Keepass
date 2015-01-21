package com.alexfu.keepass.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.alexfu.keepass.R;
import com.keepassdroid.database.model.Entry;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class EntryAdapter extends BaseAdapter implements SpinnerAdapter {

  private Context context;
  private List<Entry> entries = new ArrayList<>();

  public EntryAdapter(Context context, List<Entry> entries) {
    this.entries.addAll(entries);
    this.context = context;
  }

  @Override
  public int getCount() {
    return entries.size();
  }

  @Override
  public Entry getItem(int position) {
    return entries.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(context);
      convertView = inflater.inflate(R.layout.single_line_list_item, parent, false);
      convertView.setTag(new ViewHolder(convertView));
    }

    Entry entry = getItem(position);
    ViewHolder viewHolder = (ViewHolder) convertView.getTag();
    
    if (entry.getTitle() == null || entry.getTitle().isEmpty()) {
      viewHolder.name.setText(entry.getUrl());
    } else {
      viewHolder.name.setText(entry.getTitle());
    }

    return convertView;
  }
  
  public void replaceDataset(List<Entry> replaceWith) {
    entries.clear();
    entries.addAll(replaceWith);
    notifyDataSetChanged();
  }

  class ViewHolder extends ButterViewHolder {

    @InjectView(R.id.name) TextView name;

    public ViewHolder(View source) {
      super(source);
    }
  }
}

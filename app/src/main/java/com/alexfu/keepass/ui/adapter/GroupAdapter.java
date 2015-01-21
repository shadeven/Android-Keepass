package com.alexfu.keepass.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.alexfu.keepass.R;
import com.keepassdroid.database.model.Group;

import java.util.List;

import butterknife.InjectView;

public class GroupAdapter extends BaseAdapter implements SpinnerAdapter {
  
  private Context context;
  private List<Group> groups;
  
  public GroupAdapter(Context context, List<Group> groups) {
    this.groups = groups;
    this.context = context;
  }
  
  @Override
  public int getCount() {
    return groups.size();
  }

  @Override
  public Group getItem(int position) {
    return groups.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(context);
      convertView = inflater.inflate(R.layout.single_line_list_item, parent, false);
      convertView.setTag(new ViewHolder(convertView));
    }
    
    Group group = getItem(position);
    ViewHolder viewHolder = (ViewHolder) convertView.getTag();
    
    viewHolder.name.setText(group.name);
    
    return convertView;
  }
  
  class ViewHolder extends ButterViewHolder {
    
    @InjectView(R.id.name) TextView name;
    
    public ViewHolder(View source) {
      super(source);
    }
  }
}

package com.alexfu.keepass.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alexfu.keepass.R;
import com.alexfu.keepass.ui.provider.DBProvider;

import java.util.List;

import butterknife.InjectView;

/**
 * A simple adapter that provides the individual list views for each DBProvider.
 */
public class DBProviderAdapter extends BaseAdapter {

  private Context context;
  private List<DBProvider> providers;

  public DBProviderAdapter(Context context, List<DBProvider> providers) {
    this.context = context;
    this.providers = providers;
  }

  @Override
  public int getCount() {
    return providers.size();
  }

  @Override
  public DBProvider getItem(int position) {
    return providers.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(context);
      convertView = inflater.inflate(R.layout.double_line_list_item, parent, false);

      DBProviderViewHolder viewHolder = new DBProviderViewHolder(convertView);
      convertView.setTag(viewHolder);
    }

    // Bind data to views

    DBProvider provider = getItem(position);
    DBProviderViewHolder viewHolder = (DBProviderViewHolder) convertView.getTag();

    viewHolder.primaryText.setText(provider.getName());
    viewHolder.secondaryText.setText(provider.getDescription());

    return convertView;
  }

  class DBProviderViewHolder extends ButterViewHolder {

    @InjectView(R.id.primary_text) TextView primaryText;
    @InjectView(R.id.secondary_text) TextView secondaryText;

    public DBProviderViewHolder(View source) {
      super(source);
    }
  }
}

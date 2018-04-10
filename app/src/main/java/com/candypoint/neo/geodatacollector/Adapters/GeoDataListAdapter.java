package com.candypoint.neo.geodatacollector.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.candypoint.neo.geodatacollector.Models.GeoDataListViewItem;
import com.candypoint.neo.geodatacollector.R;

import java.util.ArrayList;

/**
 * Created by myown on 2018. 4. 2..
 */

public class GeoDataListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<GeoDataListViewItem> list;
    private int layout;

    public GeoDataListAdapter(Context context, int layout, ArrayList<GeoDataListViewItem> list){
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(layout, parent, false);
        }

        GeoDataListViewItem listViewItem = this.list.get(position);
        TextView name = (TextView)convertView.findViewById(R.id.geo_data_list_view_item_text_view);
        name.setText(listViewItem.getSectionName());

        return convertView;
    }
}

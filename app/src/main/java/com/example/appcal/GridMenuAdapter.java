package com.example.appcal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridMenuAdapter extends BaseAdapter {
    private Context context;
    private final String[] labels;
    private final int[] icons;

    public GridMenuAdapter(Context context, String[] labels, int[] icons) {
        this.context = context;
        this.labels = labels;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return labels.length;
    }

    @Override
    public Object getItem(int position) {
        return labels[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }
        TextView label = convertView.findViewById(R.id.itemLabel);
        ImageView icon = convertView.findViewById(R.id.itemIcon);

        label.setText(labels[position]);
        icon.setImageResource(icons[position]);
        return convertView;
    }
}
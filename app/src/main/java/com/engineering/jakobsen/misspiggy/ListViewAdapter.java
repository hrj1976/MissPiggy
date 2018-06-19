package com.engineering.jakobsen.misspiggy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    public ArrayList<ViewModel> productList;
    Activity activity;

    public ListViewAdapter(Activity activity, ArrayList<ViewModel> productList) {
        super();
        this.activity = activity;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView code;
        TextView text;
        TextView data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row, null);
            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.text);
            holder.data = convertView.findViewById(R.id.data);
            holder.code = convertView.findViewById(R.id.code);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ViewModel item = productList.get(position);
        holder.code.setText(item.getCode().toString());
        holder.text.setText(item.getText().toString());
        holder.data.setText(item.getData().toString());

        return convertView;
    }
}

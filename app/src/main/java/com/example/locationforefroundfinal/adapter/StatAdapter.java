package com.example.locationforefroundfinal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.locationforefroundfinal.R;
import com.example.locationforefroundfinal.model.StatListItem;

import java.util.ArrayList;

public class StatAdapter extends BaseAdapter {


    private ArrayList<StatListItem> statListItems;
    private Context context;
    public StatAdapter(Context context) {
        this.statListItems = populate();
        this.context = context;
    }

    private ArrayList<StatListItem> populate() {
        ArrayList<StatListItem> statListItems = new ArrayList<>();
        StatListItem statListItem = new StatListItem("Recordings", "0");
        StatListItem statListItem2 = new StatListItem( "Equipment", "-");

        statListItems.add(statListItem);
        statListItems.add(statListItem2);
        return statListItems;
    }

    @Override
    public int getCount() {
        return statListItems.size();
    }

    @Override
    public StatListItem getItem(int position) {
        return statListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(context).inflate(R.layout.profile_list_item, parent, false);

        TextView tvLabel = view.findViewById(R.id.label);
        TextView tvDescription = view.findViewById(R.id.description);

        tvLabel.setText(getItem(position).getLabel());
        tvDescription.setText(getItem(position).getDescription());
        return view;
    }
}

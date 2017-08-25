package com.mysampleapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mysampleapp.util.Sermon;
import java.util.List;

/**
 * Created by Jong on 6/14/17.
 */

public class SermonArrayAdapter extends ArrayAdapter<Sermon> {
    Context context;
    int layoutResourceId;
    List<Sermon> data = null;

    public SermonArrayAdapter(Context context, int layoutResourceId, List<Sermon> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SermonHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SermonHolder();
            //holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtSeries = (TextView)row.findViewById(R.id.txtSeries);
            holder.txtVerse = (TextView)row.findViewById(R.id.txtVerse);
            holder.sermonMonth = (TextView)row.findViewById(R.id.sermonMonth);
            holder.sermonDate = (TextView)row.findViewById(R.id.sermonDate);
            holder.sermonYear = (TextView)row.findViewById(R.id.sermonYear);

            row.setTag(holder);
        }
        else {
            holder = (SermonHolder)row.getTag();
        }

        Sermon sermon = data.get(position);
        holder.txtTitle.setText(sermon.getTitle());
        //holder.imgIcon.setImageResource(weather.icon);
        holder.txtSeries.setText(sermon.getSeries());
        holder.txtVerse.setText(sermon.getVerse());
        holder.sermonMonth.setText(sermon.getMonth());
        holder.sermonDate.setText(sermon.getDay());
        holder.sermonYear.setText(sermon.getYear());

        return row;
    }

    static class SermonHolder {
        //ImageView imgIcon;
        TextView txtTitle;
        TextView txtSeries;
        TextView txtVerse;
        TextView sermonMonth;
        TextView sermonDate;
        TextView sermonYear;
    }
}

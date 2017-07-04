package com.mysampleapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Jong on 6/14/17.
 */

public class SermonArrayAdapter extends ArrayAdapter<Sermon> {

    Context context;
    int layoutResourceId;
    Sermon data[] = null;

    public SermonArrayAdapter(Context context, int layoutResourceId, Sermon[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SermonHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SermonHolder();
            //holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtTitle1 = (TextView)row.findViewById(R.id.txtTitle1);
            holder.txtVerse = (TextView)row.findViewById(R.id.txtVerse);
            holder.sermonMonth = (TextView)row.findViewById(R.id.sermonMonth);
            holder.sermonDate = (TextView)row.findViewById(R.id.sermonDate);
            holder.sermonYear = (TextView)row.findViewById(R.id.sermonYear);

            row.setTag(holder);
        }
        else
        {
            holder = (SermonHolder)row.getTag();
        }

        Sermon sermon = data[position];
        holder.txtTitle.setText(sermon.title);
        //holder.imgIcon.setImageResource(weather.icon);
        holder.txtTitle1.setText(sermon.title1);
        holder.txtVerse.setText(sermon.verse);
        holder.sermonMonth.setText(sermon.month);
        holder.sermonDate.setText(sermon.date);
        holder.sermonYear.setText(sermon.year);

        return row;
    }

    static class SermonHolder
    {
        //ImageView imgIcon;
        TextView txtTitle;
        TextView txtTitle1;
        TextView txtVerse;
        TextView sermonMonth;
        TextView sermonDate;
        TextView sermonYear;
    }
}

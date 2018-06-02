package com.mysampleapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.PorterDuff;

import com.mysampleapp.util.Announcement;

import java.util.List;

/**
 * Created by SUNGJIN on 6/28/17.
 */

public class AnnouncementArrayAdapter extends ArrayAdapter<Announcement> {

    Context context;
    int layoutResourceId;
    List<Announcement> data = null;

    public AnnouncementArrayAdapter(Context context, int layoutResourceId, List<Announcement> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AnnouncementHolder holder = null;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AnnouncementHolder();
            //holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txt_announcenment_title);
            holder.aMonth = (TextView)row.findViewById(R.id.aMonth);
            holder.aDate = (TextView)row.findViewById(R.id.aDate);
            holder.aYear = (TextView)row.findViewById(R.id.aYear);
            holder.category = (TextView)row.findViewById(R.id.txt_announcenment_group);
            row.setTag(holder);
        }
        else {
            holder = (AnnouncementHolder) row.getTag();
        }

        Announcement announcement = data.get(position);
        holder.txtTitle.setText(announcement.getTitle());
        //holder.imgIcon.setImageResource(weather.icon);
        holder.aMonth.setText(announcement.getMonth());
        holder.aDate.setText(announcement.getDay());
        holder.aYear.setText(announcement.getYear());
        //holder.category.setText(announcement.getCategory());

        //set background color for categories
        if(holder.category.getText().toString().equals("교회 소식 (전체 공지)")) {
            holder.category.getBackground().setColorFilter(Color.argb(255, 80, 168, 215), PorterDuff.Mode.SRC_ATOP);
        }else{
            holder.category.getBackground().setColorFilter(Color.argb(255, 0, 0, 255), PorterDuff.Mode.SRC_ATOP);
        }

        return row;
    }




     static class AnnouncementHolder {
        //ImageView imgIcon;
        TextView txtTitle;
        TextView aMonth;
        TextView aDate;
        TextView aYear;
        TextView category;
    }
}

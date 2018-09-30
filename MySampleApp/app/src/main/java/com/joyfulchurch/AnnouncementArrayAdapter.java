package com.joyfulchurch;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joyfulchurch.util.Announcement;

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
            holder.groupTitle = (TextView)row.findViewById(R.id.txt_announcenment_group);
            holder.txtTitle = (TextView)row.findViewById(R.id.txt_announcenment_title);
            holder.aMonth = (TextView)row.findViewById(R.id.aMonth);
            holder.aDate = (TextView)row.findViewById(R.id.aDate);
            holder.aYear = (TextView)row.findViewById(R.id.aYear);

            row.setTag(holder);
        }
        else {
            holder = (AnnouncementHolder) row.getTag();
        }

        Announcement announcement = data.get(position);
        holder.groupTitle.setText(announcement.getCategory());
        holder.txtTitle.setText(announcement.getTitle());
        //holder.imgIcon.setImageResource(weather.icon);
        holder.aMonth.setText(announcement.getMonth());
        holder.aDate.setText(announcement.getDay());
        holder.aYear.setText(announcement.getYear());

        return row;
    }

    static class AnnouncementHolder {
        //ImageView imgIcon;
        TextView groupTitle;
        TextView txtTitle;
        TextView aMonth;
        TextView aDate;
        TextView aYear;
    }
}

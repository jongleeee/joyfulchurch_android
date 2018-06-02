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
            holder.category.getBackground().setColorFilter(Color.argb(255, 33, 140, 204), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("죠이플 창")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 255, 139, 139), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("카리스마 대학부")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 174, 227, 129), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("카이로스 청년부")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 255, 165, 112), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("남성 기도회")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 204, 110, 132), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("월요 여성 중보기도 모임")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 207, 163, 245), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("여성 커피브레이크")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 196, 79, 79), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("교육부")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 255, 206, 126), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("찬양팀")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 255, 152, 193), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("여름 선교")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 255, 187, 53), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("겨울 선교")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 177, 167, 228), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("목자 모임")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 182, 204, 235), PorterDuff.Mode.SRC_ATOP);
        }else if (holder.category.getText().toString().equals("사역부장 모임")){
            holder.category.getBackground().setColorFilter(Color.argb(255, 179, 150, 173), PorterDuff.Mode.SRC_ATOP);
        }else{
            holder.category.getBackground().setColorFilter(Color.argb(255, 255, 139, 139), PorterDuff.Mode.SRC_ATOP);
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

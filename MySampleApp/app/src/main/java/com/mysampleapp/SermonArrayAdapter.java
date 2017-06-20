package com.mysampleapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Jong on 6/14/17.
 */

public class SermonArrayAdapter extends ArrayAdapter<Object> {

    private final Context context;
    private final Object[] sermons;

    public SermonArrayAdapter(Context context, Object[] sermons) {
        super(context, -1, sermons);
        this.context = context;
        this.sermons = sermons;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.sermon_row_layout, parent, false);

        // Sermon sermon = sermons[position];
//        TextView textView = (TextView) rowView.findViewById(R.id.label);
//        textView.setText(values[position]);

        return rowView;
    }
}

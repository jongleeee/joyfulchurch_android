package com.joyfulchurch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AnnouncementDetailActivity extends AppCompatActivity {

    private TextView category;
    private TextView title;
    private TextView content;
    private TextView month;
    private TextView day;
    private TextView year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_announcement);

        category = (TextView) findViewById(R.id.activity_detail_category);
        title = (TextView) findViewById(R.id.activity_detail_title);
        content = (TextView) findViewById(R.id.activity_detail_content);
        month = (TextView) findViewById(R.id.activity_detail_month);
        day = (TextView) findViewById(R.id.activity_detail_day);
        year = (TextView) findViewById(R.id.activity_detail_year);

        Bundle bundle = getIntent().getExtras();
        category.setText(bundle.getString("category"));
        title.setText(bundle.getString("title"));
        content.setText(bundle.getString("content"));
        content.setMovementMethod(new ScrollingMovementMethod());
        month.setText(bundle.getString("month"));
        day.setText(bundle.getString("day"));
        year.setText(bundle.getString("year"));
    }
}

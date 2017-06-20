package com.mysampleapp.demo.nosql;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.AnnouncementsDO;

import java.util.Set;

public class DemoNoSQLAnnouncementsResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final AnnouncementsDO result;

    DemoNoSQLAnnouncementsResult(final AnnouncementsDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final String originalValue = result.getAnnouncementId();
        result.setAnnouncementId(DemoSampleDataGenerator.getRandomSampleString("announcementId"));
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setAnnouncementId(originalValue);
            throw ex;
        }
    }

    @Override
    public void deleteItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        mapper.delete(result);
    }

    private void setKeyTextViewStyle(final TextView textView) {
        textView.setTextColor(KEY_TEXT_COLOR);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(5), dp(2), dp(5), 0);
        textView.setLayoutParams(layoutParams);
    }

    /**
     * @param dp number of design pixels.
     * @return number of pixels corresponding to the desired design pixels.
     */
    private int dp(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private void setValueTextViewStyle(final TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(15), 0, dp(15), dp(2));
        textView.setLayoutParams(layoutParams);
    }

    private void setKeyAndValueTextViewStyles(final TextView keyTextView, final TextView valueTextView) {
        setKeyTextViewStyle(keyTextView);
        setValueTextViewStyle(valueTextView);
    }

    private static String bytesToHexString(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02X", bytes[0]));
        for(int index = 1; index < bytes.length; index++) {
            builder.append(String.format(" %02X", bytes[index]));
        }
        return builder.toString();
    }

    private static String byteSetsToHexStrings(Set<byte[]> bytesSet) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (byte[] bytes : bytesSet) {
            builder.append(String.format("%d: ", ++index));
            builder.append(bytesToHexString(bytes));
            if (index < bytesSet.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    @Override
    public View getView(final Context context, final View convertView, int position) {
        final LinearLayout layout;
        final TextView resultNumberTextView;
        final TextView userIdKeyTextView;
        final TextView userIdValueTextView;
        final TextView creationDateKeyTextView;
        final TextView creationDateValueTextView;
        final TextView announcementIdKeyTextView;
        final TextView announcementIdValueTextView;
        final TextView categoryKeyTextView;
        final TextView categoryValueTextView;
        final TextView contentKeyTextView;
        final TextView contentValueTextView;
        final TextView keywordsKeyTextView;
        final TextView keywordsValueTextView;
        final TextView titleKeyTextView;
        final TextView titleValueTextView;
        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            resultNumberTextView = new TextView(context);
            resultNumberTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(resultNumberTextView);


            userIdKeyTextView = new TextView(context);
            userIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(userIdKeyTextView, userIdValueTextView);
            layout.addView(userIdKeyTextView);
            layout.addView(userIdValueTextView);

            creationDateKeyTextView = new TextView(context);
            creationDateValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(creationDateKeyTextView, creationDateValueTextView);
            layout.addView(creationDateKeyTextView);
            layout.addView(creationDateValueTextView);

            announcementIdKeyTextView = new TextView(context);
            announcementIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(announcementIdKeyTextView, announcementIdValueTextView);
            layout.addView(announcementIdKeyTextView);
            layout.addView(announcementIdValueTextView);

            categoryKeyTextView = new TextView(context);
            categoryValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(categoryKeyTextView, categoryValueTextView);
            layout.addView(categoryKeyTextView);
            layout.addView(categoryValueTextView);

            contentKeyTextView = new TextView(context);
            contentValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(contentKeyTextView, contentValueTextView);
            layout.addView(contentKeyTextView);
            layout.addView(contentValueTextView);

            keywordsKeyTextView = new TextView(context);
            keywordsValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(keywordsKeyTextView, keywordsValueTextView);
            layout.addView(keywordsKeyTextView);
            layout.addView(keywordsValueTextView);

            titleKeyTextView = new TextView(context);
            titleValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(titleKeyTextView, titleValueTextView);
            layout.addView(titleKeyTextView);
            layout.addView(titleValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            userIdKeyTextView = (TextView) layout.getChildAt(1);
            userIdValueTextView = (TextView) layout.getChildAt(2);

            creationDateKeyTextView = (TextView) layout.getChildAt(3);
            creationDateValueTextView = (TextView) layout.getChildAt(4);

            announcementIdKeyTextView = (TextView) layout.getChildAt(5);
            announcementIdValueTextView = (TextView) layout.getChildAt(6);

            categoryKeyTextView = (TextView) layout.getChildAt(7);
            categoryValueTextView = (TextView) layout.getChildAt(8);

            contentKeyTextView = (TextView) layout.getChildAt(9);
            contentValueTextView = (TextView) layout.getChildAt(10);

            keywordsKeyTextView = (TextView) layout.getChildAt(11);
            keywordsValueTextView = (TextView) layout.getChildAt(12);

            titleKeyTextView = (TextView) layout.getChildAt(13);
            titleValueTextView = (TextView) layout.getChildAt(14);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        userIdKeyTextView.setText("userId");
        userIdValueTextView.setText(result.getUserId());
        creationDateKeyTextView.setText("creationDate");
        creationDateValueTextView.setText("" + result.getCreationDate().longValue());
        announcementIdKeyTextView.setText("announcementId");
        announcementIdValueTextView.setText(result.getAnnouncementId());
        categoryKeyTextView.setText("category");
        categoryValueTextView.setText(result.getCategory());
        contentKeyTextView.setText("content");
        contentValueTextView.setText(result.getContent());
        keywordsKeyTextView.setText("keywords");
        keywordsValueTextView.setText(result.getKeywords().toString());
        titleKeyTextView.setText("title");
        titleValueTextView.setText(result.getTitle());
        return layout;
    }
}

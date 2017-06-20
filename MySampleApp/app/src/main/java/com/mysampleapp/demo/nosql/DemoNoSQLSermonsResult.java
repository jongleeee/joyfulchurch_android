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
import com.amazonaws.models.nosql.SermonsDO;

import java.util.Set;

public class DemoNoSQLSermonsResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final SermonsDO result;

    DemoNoSQLSermonsResult(final SermonsDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final String originalValue = result.getSermon();
        result.setSermon(DemoSampleDataGenerator.getRandomSampleString("sermon"));
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setSermon(originalValue);
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
        final TextView keywordsKeyTextView;
        final TextView keywordsValueTextView;
        final TextView sermonKeyTextView;
        final TextView sermonValueTextView;
        final TextView sermonIdKeyTextView;
        final TextView sermonIdValueTextView;
        final TextView titleKeyTextView;
        final TextView titleValueTextView;
        final TextView verseKeyTextView;
        final TextView verseValueTextView;
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

            keywordsKeyTextView = new TextView(context);
            keywordsValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(keywordsKeyTextView, keywordsValueTextView);
            layout.addView(keywordsKeyTextView);
            layout.addView(keywordsValueTextView);

            sermonKeyTextView = new TextView(context);
            sermonValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(sermonKeyTextView, sermonValueTextView);
            layout.addView(sermonKeyTextView);
            layout.addView(sermonValueTextView);

            sermonIdKeyTextView = new TextView(context);
            sermonIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(sermonIdKeyTextView, sermonIdValueTextView);
            layout.addView(sermonIdKeyTextView);
            layout.addView(sermonIdValueTextView);

            titleKeyTextView = new TextView(context);
            titleValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(titleKeyTextView, titleValueTextView);
            layout.addView(titleKeyTextView);
            layout.addView(titleValueTextView);

            verseKeyTextView = new TextView(context);
            verseValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(verseKeyTextView, verseValueTextView);
            layout.addView(verseKeyTextView);
            layout.addView(verseValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            userIdKeyTextView = (TextView) layout.getChildAt(1);
            userIdValueTextView = (TextView) layout.getChildAt(2);

            creationDateKeyTextView = (TextView) layout.getChildAt(3);
            creationDateValueTextView = (TextView) layout.getChildAt(4);

            keywordsKeyTextView = (TextView) layout.getChildAt(5);
            keywordsValueTextView = (TextView) layout.getChildAt(6);

            sermonKeyTextView = (TextView) layout.getChildAt(7);
            sermonValueTextView = (TextView) layout.getChildAt(8);

            sermonIdKeyTextView = (TextView) layout.getChildAt(9);
            sermonIdValueTextView = (TextView) layout.getChildAt(10);

            titleKeyTextView = (TextView) layout.getChildAt(11);
            titleValueTextView = (TextView) layout.getChildAt(12);

            verseKeyTextView = (TextView) layout.getChildAt(13);
            verseValueTextView = (TextView) layout.getChildAt(14);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        userIdKeyTextView.setText("userId");
        userIdValueTextView.setText(result.getUserId());
        creationDateKeyTextView.setText("creationDate");
        creationDateValueTextView.setText("" + result.getCreationDate().longValue());
        keywordsKeyTextView.setText("keywords");
        keywordsValueTextView.setText(result.getKeywords().toString());
        sermonKeyTextView.setText("sermon");
        sermonValueTextView.setText(result.getSermon());
        sermonIdKeyTextView.setText("sermonId");
        sermonIdValueTextView.setText(result.getSermonId());
        titleKeyTextView.setText("title");
        titleValueTextView.setText(result.getTitle());
        verseKeyTextView.setText("verse");
        verseValueTextView.setText(result.getVerse());
        return layout;
    }
}

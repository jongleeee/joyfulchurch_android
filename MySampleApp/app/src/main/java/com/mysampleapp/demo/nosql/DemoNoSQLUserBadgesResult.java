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
import com.amazonaws.models.nosql.UserBadgesDO;

import java.util.Set;

public class DemoNoSQLUserBadgesResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final UserBadgesDO result;

    DemoNoSQLUserBadgesResult(final UserBadgesDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final double originalValue = result.getBadgeCount();
        result.setBadgeCount(DemoSampleDataGenerator.getRandomSampleNumber());
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setBadgeCount(originalValue);
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
        final TextView deviceTokenKeyTextView;
        final TextView deviceTokenValueTextView;
        final TextView badgeCountKeyTextView;
        final TextView badgeCountValueTextView;
        final TextView lastUpdatedKeyTextView;
        final TextView lastUpdatedValueTextView;
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

            deviceTokenKeyTextView = new TextView(context);
            deviceTokenValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(deviceTokenKeyTextView, deviceTokenValueTextView);
            layout.addView(deviceTokenKeyTextView);
            layout.addView(deviceTokenValueTextView);

            badgeCountKeyTextView = new TextView(context);
            badgeCountValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(badgeCountKeyTextView, badgeCountValueTextView);
            layout.addView(badgeCountKeyTextView);
            layout.addView(badgeCountValueTextView);

            lastUpdatedKeyTextView = new TextView(context);
            lastUpdatedValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(lastUpdatedKeyTextView, lastUpdatedValueTextView);
            layout.addView(lastUpdatedKeyTextView);
            layout.addView(lastUpdatedValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            userIdKeyTextView = (TextView) layout.getChildAt(1);
            userIdValueTextView = (TextView) layout.getChildAt(2);

            deviceTokenKeyTextView = (TextView) layout.getChildAt(3);
            deviceTokenValueTextView = (TextView) layout.getChildAt(4);

            badgeCountKeyTextView = (TextView) layout.getChildAt(5);
            badgeCountValueTextView = (TextView) layout.getChildAt(6);

            lastUpdatedKeyTextView = (TextView) layout.getChildAt(7);
            lastUpdatedValueTextView = (TextView) layout.getChildAt(8);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        userIdKeyTextView.setText("userId");
        userIdValueTextView.setText(result.getUserId());
        deviceTokenKeyTextView.setText("deviceToken");
        deviceTokenValueTextView.setText(result.getDeviceToken());
        badgeCountKeyTextView.setText("badgeCount");
        badgeCountValueTextView.setText("" + result.getBadgeCount().longValue());
        lastUpdatedKeyTextView.setText("lastUpdated");
        lastUpdatedValueTextView.setText("" + result.getLastUpdated().longValue());
        return layout;
    }
}

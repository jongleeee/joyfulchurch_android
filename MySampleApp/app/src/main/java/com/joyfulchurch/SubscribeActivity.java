package com.joyfulchurch;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobile.push.GCMTokenHelper;
import com.amazonaws.mobile.push.PushManager;
import com.amazonaws.mobile.push.SnsTopic;
import com.amazonaws.regions.Regions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.joyfulchurch.util.User;
import com.joyfulchurch.util.Util;
import org.w3c.dom.Text;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;

import com.amazonaws.mobile.auth.core.IdentityManager;

public class SubscribeActivity extends AppCompatActivity {
    private static final String LOG_TAG = SubscribeActivity.class.getSimpleName();

    ListView listView;
    final List<String> subscribeArray = Util.getAllChannels();
    private Typeface mTypeface;
    private ArrayAdapter<String> channelsAdapter;

    private PushManager pushManager;

    // GOOGLE CLOUD MESSAGING SENDER ID
    public static final String GOOGLE_CLOUD_MESSAGING_SENDER_ID =
            "172205575542";

    // SNS PLATFORM APPLICATION ARN
    public static final String AMAZON_SNS_PLATFORM_APPLICATION_ARN =
            "arn:aws:sns:us-west-1:115981409113:app/GCM/joyfulchurch_MOBILEHUB_687604833";

    // SNS DEFAULT TOPIC ARN
    public static final String AMAZON_SNS_DEFAULT_TOPIC_ARN =
            "arn:aws:sns:us-west-1:115981409113:joyfulchurch_alldevices_MOBILEHUB_687604833";

    public static final Regions AMAZON_SNS_REGION =
            Regions.fromName("us-west-1");

    // Arbitrary activity request ID. You can handle this in the main activity,
    // if you want to take action when a google services result is received.
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1363;

    private User user = User.INSTANCE();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        listView = (ListView)findViewById(R.id.subscribe_listview);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.subscribe_toolbar);
        myToolbar.setTitle("Subscribe");
        setSupportActionBar(myToolbar);
        mTypeface = Typeface.createFromAsset(getAssets(),"the_jung_regular_120.otf");

        GCMTokenHelper gcmTokenHelper = new GCMTokenHelper(SubscribeActivity.this, GOOGLE_CLOUD_MESSAGING_SENDER_ID);

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setUserAgent(Application.awsConfiguration.getUserAgent());
        pushManager =
                new PushManager(SubscribeActivity.this,
                        gcmTokenHelper,
                        IdentityManager.getDefaultIdentityManager().getCredentialsProvider(),
                        AMAZON_SNS_PLATFORM_APPLICATION_ARN,
                        clientConfiguration,
                        AMAZON_SNS_DEFAULT_TOPIC_ARN,
                        Util.AMAZON_SNS_TOPIC_ARNS,
                        AMAZON_SNS_REGION);
        gcmTokenHelper.init();

        Util.saveSnsTopicMap(pushManager.getTopics());

        final GoogleApiAvailability api = GoogleApiAvailability.getInstance();

        final int code = api.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS != code) {
            final String errorString = api.getErrorString(code);
            Log.e(LOG_TAG, "Google Services Availability Error: " + errorString + " (" + code + ")");

            if (api.isUserResolvableError(code)) {
                Log.e(LOG_TAG, "Google Services Error is user resolvable.");
                api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES);
                return;
            } else {
                Log.e(LOG_TAG, "Google Services Error is NOT user resolvable.");
                showErrorMessage(R.string.push_demo_error_message_google_play_services_unavailable);
                return;
            }
        }

        registerDeviceForNotification();

        channelsAdapter = new ArrayAdapter<String>(this, R.layout.listview_subscribe, R.id.subscribe_listview, subscribeArray) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView textview = (TextView) view.findViewById(R.id.subscribe_listview);
                textview.setTextColor(Color.parseColor("#000000"));
                textview.setTextSize(18);
                textview.setTypeface(mTypeface);
                textview.setPadding(15,30,15,30);

                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.subscribe_checkbox);
                checkBox.setChecked(user.getSubscribedChannels(getApplicationContext()).contains(channelsAdapter.getItem(position)));
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SnsTopic snsTopic = Util.getSnsTopicFromChannelName(channelsAdapter.getItem(position));
                        if (snsTopic.isSubscribed()) {
                            checkBox.setChecked(false);
                            user.removeSubscribedChannel(channelsAdapter.getItem(position), getApplicationContext());
                        } else {
                            checkBox.setChecked(true);
                            user.addSubscribedChannel(channelsAdapter.getItem(position), getApplicationContext());
                        }
                        toggleSubscription(channelsAdapter.getItem(position), false);
                    }
                });
                return view;
            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                View view = super.getView(position, convertView, parent);
//                toggleSubscription(channelsAdapter.getItem(position), true);
            }
        });
        listView.setAdapter(channelsAdapter);
    }

    private void toggleSubscription(final String channelName, final boolean showConfirmation) {
        final SnsTopic snsTopic = Util.getSnsTopicFromChannelName(channelName);
        if (snsTopic.isSubscribed() && showConfirmation) {
            new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(getString(R.string.push_demo_confirm_message_unsubscribe,
                            snsTopic.getDisplayName()))
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toggleSubscription(channelName, false);
                        }
                    })
                    .show();
            return;
        }

        final ProgressDialog dialog = showWaitingDialog(
                R.string.push_demo_wait_message_update_subscription);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... params) {
                try {
                    if (snsTopic.isSubscribed()) {
                        pushManager.unsubscribeFromTopic(snsTopic);
                    } else {
                        pushManager.subscribeToTopic(snsTopic);
                    }
                    return null;
                } catch (final AmazonClientException ace) {
                    Log.e(LOG_TAG, "Error occurred during subscription", ace);
                    return ace.getMessage();
                }
            }

            @Override
            protected void onPostExecute(final String errorMessage) {
                dialog.dismiss();
                channelsAdapter.notifyDataSetChanged();

                if (errorMessage != null) {
                    showErrorMessage(R.string.push_demo_error_message_update_subscription,
                            errorMessage);
                }
            }
        }.execute();
    }

    private void registerDeviceForNotification() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... params) {
                // register device first to ensure we have a push endpoint.
                pushManager.registerDevice();

                // if registration succeeded.
                if (pushManager.isRegistered()) {
                    try {
                        pushManager.setPushEnabled(true);
                        // Automatically subscribe to the default SNS topic
                        pushManager.subscribeToTopic(pushManager.getDefaultTopic());

                        if (user.setupRequired(getApplicationContext())) {
                            for (String channel : Util.getDefaultChannels()) {
                                user.addSubscribedChannel(channel, getApplicationContext());
                                pushManager.subscribeToTopic(Util.getSnsTopicFromChannelName(channel));
                            }
                            pushManager.subscribeToTopic(Util.getSermonSnsTopic());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    channelsAdapter.notifyDataSetChanged();
                                }
                            });
                            user.updateSetup(getApplicationContext());
                        }
                        return null;
                    } catch (final AmazonClientException ace) {
                        Log.e(LOG_TAG, "Failed to change push notification status", ace);
                        return ace.getMessage();
                    }
                }
                return "Failed to register for push notifications.";
            }

            @Override
            protected void onPostExecute(final String errorMessage) {
                if (errorMessage != null) {
                    showErrorMessage(R.string.push_demo_error_message_update_notification,
                            errorMessage);
                }
            }
        }.execute();
    }

    private AlertDialog showErrorMessage(final int resId, final Object... args) {
        return new AlertDialog.Builder(this).setMessage(getString(resId, (Object[]) args))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private ProgressDialog showWaitingDialog(final int resId, final Object... args) {
        return ProgressDialog.show(this,
                getString(R.string.push_demo_progress_dialog_title),
                getString(resId, (Object[]) args));
    }
}
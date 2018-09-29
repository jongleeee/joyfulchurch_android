package com.mysampleapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Context;

/**
 * Created by YoungSeokLee on 6/24/17.
 */

public class User extends Activity{

    SharedPreferences myChannels;
    SharedPreferences authorizedUsers;
    SharedPreferences setupRequired;
    List<String> mySubscribedChannels = new ArrayList<>();

    private static volatile User user;

    public static User INSTANCE() {
        if  (user == null) {
            synchronized (User.class) {
                if (user == null) {
                    user = new User();
                }
            }
        }
        return user;
    }

    void loadSharedPreferences(Context context) {
        if (myChannels == null || authorizedUsers == null) {
            myChannels = context.getSharedPreferences("myChannels", context.MODE_PRIVATE);
            authorizedUsers = context.getSharedPreferences("authorizedUsers", context.MODE_PRIVATE);
            setupRequired = context.getSharedPreferences("setupRequired", context.MODE_PRIVATE);
        }
    }

    //subscribed channels
    public List<String> getSubscribedChannels(Context context) {
        loadSharedPreferences(context);

        if (mySubscribedChannels.isEmpty()) {
            Map<String, ?> keys = myChannels.getAll();
            if (!keys.isEmpty()) {
                for (Map.Entry<String, ?> entry : keys.entrySet()) {
                    mySubscribedChannels.add(entry.getKey());
                }
            }
        }
        return mySubscribedChannels;
    }

    public void addSubscribedChannel(String channel, Context context) {
        loadSharedPreferences(context);

        List<String> newSubscribedChannel = getSubscribedChannels(context);
        if (!newSubscribedChannel.contains(channel)) {
            newSubscribedChannel.add(channel);
            updateSubscribedChannels(newSubscribedChannel);
        }
    }

    public void removeSubscribedChannel(String channel, Context context) {
        loadSharedPreferences(context);

        List<String> newSubscribedChannel = getSubscribedChannels(context);
        if (newSubscribedChannel.remove(channel)) {
            updateSubscribedChannels(newSubscribedChannel);
        }
    }

    public void updateSubscribedChannels(List<String> subscribedChannels) {
        myChannels.edit().clear().commit();
        SharedPreferences.Editor edit = myChannels.edit();
        for (String channel : subscribedChannels) {
            edit.putString(channel, "subscribed");
        }
        edit.commit();
    }

    //authorization
    public boolean isAdmin(Context context) {
        loadSharedPreferences(context);
        return authorizedUsers.contains("Admin");
    }

    public boolean isAuthorizedToAddSermon(Context context) {
        loadSharedPreferences(context);
        return authorizedUsers.contains("설교");
    }

    public boolean isAuthorizedToAddAnnouncement(Context context) {
        loadSharedPreferences(context);
        return authorizedUsers.contains("광고");
    }

    public void updateAuthorization(String role, Context context) {
        loadSharedPreferences(context);
        SharedPreferences.Editor edit = authorizedUsers.edit();
            if (role.equals("Admin")) {
                edit.putString("Admin", "true");
                edit.putString("설교", "true");
                edit.putString("광고", "true");
            }
            else {
                edit.putString(role, "true");
            }
        edit.commit();
    }

    public boolean setupRequired(Context context) {
        loadSharedPreferences(context);
        return !setupRequired.contains("setupRequired");
    }

    public void updateSetup(Context context) {
        loadSharedPreferences(context);
        SharedPreferences.Editor edit = setupRequired.edit();
        edit.putString("setupRequired", "false");
        edit.commit();
    }
}

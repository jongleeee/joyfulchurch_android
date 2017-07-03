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
    List<String> mySubscribedChannels;

    public User (Context context) {
        myChannels = context.getSharedPreferences("myChannels", context.MODE_PRIVATE);
        authorizedUsers = context.getSharedPreferences("authorizedUsers", context.MODE_PRIVATE);
    }


    //subscribed channels
    public List<String> getSubscribedChannels() {
        if (mySubscribedChannels == null) {
            mySubscribedChannels = new ArrayList<>();
            Map<String, ?> keys = myChannels.getAll();
            if (!keys.isEmpty()) {
                for (Map.Entry<String, ?> entry : keys.entrySet()) {
                    mySubscribedChannels.add(entry.getKey());
                }
            }
        }
        return mySubscribedChannels;
    }



    public void updateSubscribedChannels(List<String> subscribedChannels) {
        myChannels.edit().clear().commit();
        mySubscribedChannels.clear();
        SharedPreferences.Editor edit = myChannels.edit();
        for (String channel : subscribedChannels) {
            edit.putString(channel, "subscribed");
            mySubscribedChannels.add(channel);
        }
        edit.commit();
    }

    //authorization
    public boolean isAdmin() {
        return authorizedUsers.contains("Admin");
    }

    public boolean isAuthorizedToAddSermon() {
        return authorizedUsers.contains("설교");
    }

    public boolean isAuthorizedToAddAnnouncement() {
        return authorizedUsers.contains("광고");
    }

    public void updateAuthorization(String role) {
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

}

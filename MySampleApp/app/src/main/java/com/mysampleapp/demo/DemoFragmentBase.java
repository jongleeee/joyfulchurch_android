package com.mysampleapp.demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public class DemoFragmentBase extends Fragment {

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final UserSettings userSettings = UserSettings.getInstance(getContext());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                userSettings.loadFromDataset();
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                final View view = getView();
                if (view != null) {
                    view.setBackgroundColor(userSettings.getBackgroudColor());
                }
            }
        }.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                UserSettings.getInstance(getContext()).saveToDataset();
                return null;
            }
        }.execute();
    }
}

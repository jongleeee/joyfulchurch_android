package com.mysampleapp.demo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.mysampleapp.MainActivity;
import com.mysampleapp.R;

import java.util.List;

public class UserSettingsDemoFragment extends Fragment {

    private static class Theme {
        int titleTextColor;
        int titleBarColor;
        int backgroundColor;

        Theme(final int titleTextColor, final int titleBarColor, final int backgroundColor) {
            this.titleTextColor = titleTextColor;
            this.titleBarColor = titleBarColor;
            this.backgroundColor = backgroundColor;
        }
    }

    private View theme1Button;
    private View theme2Button;
    private View theme3Button;
    private View theme4Button;

    /**
     * Logging tag for this class.
     */
    private static final String LOG_TAG = UserSettingsDemoFragment.class.getSimpleName();
    private static final Theme theme1 =
            new Theme(0xFFFFFFFF, 0xFFF58535, 0xFFFFFFFF);
    private static final Theme theme2 =
            new Theme(0xFFDDDDDD, 0xFF00D2FF, 0xFFEEEEEE);
    private static final Theme theme3 =
            new Theme(0xFFFFFFFF, 0xFF00D2A9, 0xFFC8FFFF);
    private static final Theme theme4 =
            new Theme(0xFFFFFF00, 0xFF000000, 0xFFFFFFDC);

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo_user_settings, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        theme1Button = view.findViewById(R.id.settings_imageButton_theme1);
        theme2Button = view.findViewById(R.id.settings_imageButton_theme2);
        theme3Button = view.findViewById(R.id.settings_imageButton_theme3);
        theme4Button = view.findViewById(R.id.settings_imageButton_theme4);

        loadUserSettings();

        theme1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View button) {
                resetSelectedButtons(button);
                setThemeColors(theme1);
            }
        });

        theme2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View button) {
                resetSelectedButtons(button);
                setThemeColors(theme2);
            }
        });

        theme3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View button) {
                resetSelectedButtons(button);
                setThemeColors(theme3);
            }
        });

        theme4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View button) {
                resetSelectedButtons(button);
                setThemeColors(theme4);
            }
        });
    }

    private void resetSelectedButtons(final View selectedButton) {

        theme1Button.setSelected(false);
        theme2Button.setSelected(false);
        theme3Button.setSelected(false);
        theme4Button.setSelected(false);

        selectedButton.setSelected(true);
    }
    private void setThemeColors(final Theme theme) {
        final UserSettings userSettings = UserSettings.getInstance(getContext());
        userSettings.setTitleTextColor(theme.titleTextColor);
        userSettings.setBackgroundColor(theme.backgroundColor);
        userSettings.setTitleBarColor(theme.titleBarColor);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                userSettings.saveToDataset();
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {

                // update color
                ((MainActivity) getActivity()).updateColor();

                // save user settings to remote on background thread
                userSettings.getDataset().synchronize(new Dataset.SyncCallback() {
                    @Override
                    public void onSuccess(Dataset dataset, List<Record> updatedRecords) {
                        Log.d(LOG_TAG, "onSuccess - dataset updated");
                    }

                    @Override
                    public boolean onConflict(Dataset dataset, List<SyncConflict> conflicts) {
                        Log.d(LOG_TAG, "onConflict - dataset conflict");
                        return false;
                    }

                    @Override
                    public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
                        Log.d(LOG_TAG, "onDatasetDeleted - dataset deleted");
                        return false;
                    }

                    @Override
                    public boolean onDatasetsMerged(Dataset dataset, List<String> datasetNames) {
                        Log.d(LOG_TAG, "onDatasetsMerged - datasets merged");
                        return false;
                    }

                    @Override
                    public void onFailure(DataStorageException dse) {
                        Log.e(LOG_TAG, "onFailure - " + dse.getMessage(), dse);
                    }
                });
            }
        }.execute();
    }

    private void loadUserSettings() {
        final UserSettings userSettings = UserSettings.getInstance(getContext());
        final Dataset dataset = userSettings.getDataset();
        final ProgressDialog dialog = ProgressDialog.show(getActivity(),
                getString(R.string.settings_fragment_dialog_title),
                getString(R.string.settings_fragment_dialog_message));
        Log.d(LOG_TAG, "Loading user settings from remote");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(final Dataset dataset, final List<Record> updatedRecords) {
                super.onSuccess(dataset, updatedRecords);
                userSettings.loadFromDataset();
                updateUI(dialog);
            }

            @Override
            public void onFailure(final DataStorageException dse) {
                Log.w(LOG_TAG, "Failed to load user settings from remote, using default.", dse);
                updateUI(dialog);
            }

            @Override
            public boolean onDatasetsMerged(final Dataset dataset,
                                            final List<String> datasetNames) {
                // Handle dataset merge. One can selectively copy records from merged datasets
                // if needed. Here, simply discard merged datasets
                for (String name : datasetNames) {
                    Log.d(LOG_TAG, "found merged datasets: " + name);
                    AWSMobileClient.defaultMobileClient().getSyncManager().openOrCreateDataset(name).delete();
                }
                return true;
            }
        });
    }

    private void updateUI(final ProgressDialog dialog) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }

                ((MainActivity) getActivity()).updateColor();
            }
        });
    }
}

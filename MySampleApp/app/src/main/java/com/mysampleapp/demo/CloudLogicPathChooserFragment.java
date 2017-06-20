package com.mysampleapp.demo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobile.api.CloudLogicAPIFactory;
import com.amazonaws.mobile.api.CloudLogicAPIConfiguration;
import com.mysampleapp.MainActivity;
import com.mysampleapp.R;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Selects one of the Resource Paths under Amazon API Gateway REST API.
 */
public class CloudLogicPathChooserFragment extends DemoFragmentBase
        implements AdapterView.OnItemClickListener, View.OnKeyListener, View.OnClickListener {

    private static final String LOG_TAG = CloudLogicPathChooserFragment.class.getSimpleName();

    public static final String BUNDLE_ARG_API_INDEX = "api-index";
    public static final String BUNDLE_ARG_PATH = "path";

    private CloudLogicAPIConfiguration apiConfiguration;
    private List<String> filteredPaths;
    private EditText pathView;
    private ArrayAdapter adapter;

    public CloudLogicPathChooserFragment() {}

    public static CloudLogicPathChooserFragment newInstance() {
        return new CloudLogicPathChooserFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view =
            inflater.inflate(R.layout.fragment_demo_cloudlogic_path_chooser, container, false);

        final Bundle args = getArguments();
        Log.d(LOG_TAG, "API Index : " + args.getInt(BUNDLE_ARG_API_INDEX));
        Log.d(LOG_TAG, "Path : " + args.getString(BUNDLE_ARG_PATH));

        apiConfiguration = CloudLogicAPIFactory.getAPIs()[args.getInt(BUNDLE_ARG_API_INDEX)];
        filteredPaths = new LinkedList<>();
        filteredPaths.addAll(Arrays.asList(apiConfiguration.getPaths()));

        pathView = (EditText)view.findViewById(R.id.cloudlogic_path_chooser_edit_text);
        pathView.setOnKeyListener(this);

        final String pathValue;

        if (args.containsKey(BUNDLE_ARG_PATH)) {
            pathValue = args.getString(BUNDLE_ARG_PATH);
        } else {
            pathValue = apiConfiguration.getPaths()[0];
        }

        pathView.setText(pathValue);

        pathView.setText(args.getString(BUNDLE_ARG_PATH));

        view.findViewById(R.id.cloudlogic_path_chooser_button).setOnClickListener(this);

        final ListView listView = (ListView)view.findViewById(R.id.cloudlogic_path_chooser_list);
        listView.setDivider(null);

        adapter = new ArrayAdapter<String>(getContext(),
                R.layout.list_item_cloudlogic_path_chooser,
                filteredPaths) {
                    @Override
                    public View getView(final int position, final View convertView, final ViewGroup parent) {

                        final View view;

                        if (convertView == null) {
                            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            view = inflater.inflate(R.layout.list_item_cloudlogic_path_chooser, parent, false);
                        } else {
                            view = convertView;
                        }

                        final TextView pathField = (TextView)view.findViewById(R.id.list_item_cloudlogic_path_chooser);
                        final String pathValue = getItem(position);

                        pathField.setText(pathValue);

                        return view;
                    };
                };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        Log.d(LOG_TAG, "onItemClick: " + position);

        final String pathValue = parent.getAdapter().getItem(position).toString();
        Log.d(LOG_TAG, "Path Value: " + pathValue);

        pathView.setText(pathValue);
        onClick(null);
    }

    @Override
    public void onClick(final View view) {

        final InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pathView.getWindowToken(), 0);

        final String pathValue = pathView.getText().toString();

        final Bundle bundle = new Bundle();
        bundle.putInt(CloudLogicMethodChooserFragment.BUNDLE_ARG_API_INDEX,
                      getArguments().getInt(CloudLogicPathChooserFragment.BUNDLE_ARG_API_INDEX));
        bundle.putString(CloudLogicMethodChooserFragment.BUNDLE_ARG_PATH, pathValue);

        Log.d(LOG_TAG, "Path: " + pathValue);

        // Store result for previous fragment
        ((MainActivity)getActivity()).setFragmentBundle(bundle);

        getFragmentManager().popBackStack();
    }

    @Override
    public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
        final String currentText = pathView.getText().toString();

        filteredPaths.clear();

        for (final String path : apiConfiguration.getPaths()) {
            if (path.toLowerCase(Locale.ENGLISH).contains(currentText.toLowerCase(Locale.ENGLISH))) {
                filteredPaths.add(path);
            }
        }

        Log.d(LOG_TAG, "onKey: filteredPaths = " + filteredPaths);
        adapter.notifyDataSetChanged();

        return false;
    }
}

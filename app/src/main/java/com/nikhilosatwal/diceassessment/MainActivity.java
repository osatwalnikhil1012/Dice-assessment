package com.nikhilosatwal.diceassessment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Repository>>, AdapterView.OnItemSelectedListener,  ConnectionReceiver.ReceiverListener {

    private ListView dataListView;
    private EditText requestTag;
    private TextView errorMsg;
    private ProgressBar progressBar;
    private ImageButton searchQuery;

    private Spinner spinner;
    private RepositoryAdapter repositoryAdapter;

    private static final int GITHUB_SEARCH_LOADER = 1;
    private static final String GITHUB_QUERY_TAG = "query";

    private static final String GITHUB_SORT_TAG = "sort_by";

    String[] sortBy = { "stargazers_count", "watchers_count", "score","name","created_at", "updated_at"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.loading);
        errorMsg =findViewById(R.id.error_message);
        requestTag = findViewById(R.id.request_tag);
        searchQuery = findViewById(R.id.search_repo);
        dataListView =findViewById(R.id.data_list);
        dataListView.setEmptyView(errorMsg);

        repositoryAdapter = new RepositoryAdapter(getApplicationContext());
        dataListView.setAdapter(repositoryAdapter);

        if (savedInstanceState != null) {
            String queryUrl = savedInstanceState.getString(GITHUB_QUERY_TAG);
            String sortBy = savedInstanceState.getString(GITHUB_SORT_TAG);
            requestTag.setText(queryUrl);
        }

        LoaderManager.getInstance(this).initLoader(GITHUB_SEARCH_LOADER, null, this);
        searchQuery.setOnClickListener(view -> {
            hideKeyboard(this);
            if (checkConnection()) {
                makeGithubSearchQuery(0);
            } else {
                Toast.makeText(this, "Not Connected to Internet", Toast.LENGTH_SHORT).show();
            }
        });
        spinner = (Spinner) findViewById(R.id.sort_spinner);
        spinner.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,sortBy);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(GITHUB_QUERY_TAG, requestTag.getText().toString().trim());
        outState.putString(GITHUB_SORT_TAG, spinner.getSelectedItem().toString());
    }

    @SuppressLint("StaticFieldLeak")
    public Loader<List<Repository>> onCreateLoader (int id, final Bundle args) {
        return new AsyncTaskLoader<List<Repository>>(this) {
            List<Repository> mRepoList;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null) {
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if(mRepoList != null) {
                    deliverResult(mRepoList);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Repository> loadInBackground() {
                String searchQuery = args.getString(GITHUB_QUERY_TAG);
                String sortBy = args.getString(GITHUB_SORT_TAG);
                try {
                    List<Repository> gitHubSearchResults = NetworkUtils.getDataFromApi(searchQuery, sortBy);
                    return gitHubSearchResults;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable List<Repository> data) {
                mRepoList = data;
                super.deliverResult(data);
            }
        };
    }

    public void onLoadFinished (Loader<List<Repository>> loader, List<Repository> data) {
        progressBar.setVisibility(View.GONE);
        if(data == null ) {
            showErrorMsg();
        } else {
            repositoryAdapter.clear();
            repositoryAdapter.addAll(data);
            showJsonDataView();
        }
    }


    public void onLoaderReset(Loader<List<Repository>> loader) {

    }
    private void showJsonDataView() {
        errorMsg.setVisibility(View.GONE);
        dataListView.setVisibility(View.VISIBLE);
    }

    private void showErrorMsg() {
        dataListView.setVisibility(View.GONE);
        errorMsg.setVisibility(View.VISIBLE);
    }

    private boolean checkConnection() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new ConnectionReceiver(), intentFilter);

        ConnectionReceiver.Listener = this;
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void makeGithubSearchQuery(int position) {
        String gitHubSearchQuery = requestTag.getText().toString();
        String gitSortBy = spinner.getAdapter().getItem(position).toString();
        Bundle queryBundle = new Bundle();
        queryBundle.putString(GITHUB_QUERY_TAG, gitHubSearchQuery);
        queryBundle.putString(GITHUB_SORT_TAG, gitSortBy);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        Loader<String> gitHubSearchLoader = loaderManager.getLoader(GITHUB_SEARCH_LOADER);
        if (gitHubSearchLoader == null ){
            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (checkConnection()) {
            makeGithubSearchQuery(i);
        } else {
            Toast.makeText(this, "Not Connected to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onNetworkChange(boolean isConnected) {

    }
}
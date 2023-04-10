package com.nikhilosatwal.diceassessment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NetworkUtils {

    private static final String GITHUB_BASE_URL = "https://api.github.com/search/repositories";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_SORT = "sort";
    private static final String SORT_BY = "stars";

    private static URL buildUrl(String githubSearchQuery, String sortBy) {
        Uri buildUri = Uri.parse(GITHUB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, githubSearchQuery)
                .appendQueryParameter(PARAM_SORT, sortBy.isEmpty() ? SORT_BY : sortBy)
                .build();
        URL url = null;
        try{
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String getRepositoryFromHttpUrl (URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }



    private static List<Repository> jsonFormatter(String jsonResponse) {
        List<Repository> resRepositoryList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray items = jsonObject.getJSONArray("items");
            int dataLen = 50;
            if (items.length() < dataLen) {
                dataLen = items.length();
            }
            for (int i=0 ; i<dataLen ; i++) {
                JSONObject currentRepo = items.getJSONObject(i);
                String repoName = currentRepo.getString("name");
                String repoOwner = currentRepo.getJSONObject("owner").getString("login");
                String repoAvatar = currentRepo.getJSONObject("owner").getString("avatar_url");
                String repoLang = currentRepo.getString("language");
                String repoStars = currentRepo.getString("stargazers_count");
                String description = currentRepo.getString("description");

                Log.v("Data", "Number" + 1);

                Repository repository = new Repository(repoName, repoOwner, repoLang, repoStars, description, repoAvatar);

                resRepositoryList.add(repository);
            }
        } catch (JSONException e) {
            Log.v("Error", e.toString());
            Log.v("Network", "Can't Read Json");
        }
        return resRepositoryList;
    }

    public static List<Repository> getDataFromApi(String query, String sortBy) throws IOException {
        URL apiURL = buildUrl(query, sortBy);
        String jsonResponse =  getRepositoryFromHttpUrl(apiURL);
        return jsonFormatter(jsonResponse);
    }
}

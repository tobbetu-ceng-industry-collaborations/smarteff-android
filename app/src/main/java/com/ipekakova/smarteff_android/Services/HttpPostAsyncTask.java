package com.ipekakova.smarteff_android.Services;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HttpPostAsyncTask extends AsyncTask<String, String, String> {
    // This is the JSON body of the post
    JSONObject postData;
    private WeakReference<Context> applicationContext;
    private String broadcastIntent;

    // This is a constructor that allows you to pass in the JSON body
    public HttpPostAsyncTask(WeakReference<Context> context, String broadcastIntent ,JSONObject postData) {
        applicationContext = context;
        this.broadcastIntent = broadcastIntent;
        if (postData != null) {
            this.postData = postData;
        }
    }


    // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
    @Override
    protected String doInBackground(String... params) {

        try {
            // This is getting the url from the string we passed in
            URL url = new URL(params[0]);
            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            // Send the post body
            if (this.postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }
            int statusCode = urlConnection.getResponseCode();
            Log.i("json", postData.toString());
            Log.i("STATUS", String.valueOf(statusCode));
            Log.i("MSG", urlConnection.getResponseMessage());

            if (statusCode ==  200) {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                inputStream.close();
                urlConnection.disconnect();
                return response;
            } else {
                // Status code is not 200
                // Do something to handle the error
                Log.d("statusCode:", String.valueOf(statusCode));
                return null;
            }

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bufferedReader.close();
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Intent intent = new Intent("result");
        intent.putExtra("json", result);

        //LocalBroadcastManager.getInstance(applicationContext.get()).sendBroadcast(intent);
    }
}
package com.ipekakova.smarteff_android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 1.03.2020.
 */

public class HttpRequest extends AsyncTask{
    private Context context;

    public HttpRequest(Context ctx){
        context = ctx;
    }

    /*
        public HashMap<Integer, String> sendGetForUsers(){
            final HashMap<Integer,String> users = new HashMap<Integer, String>();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL (context.getString(R.string.get_users_url));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line).append("\n");
                            }
                            bufferedReader.close();
                            try {
                                while (true) {
                                    JSONObject object = new JSONObject(stringBuilder.toString());
                                    JSONArray usersArray = new JSONArray(object.get("people").toString());

                                    for (int i = 0 ; i < usersArray.length() ; i++) {
                                        JSONObject obj = new JSONObject(usersArray.get(i).toString());
                                        //Log.i("user",obj.toString());
                                        String name = obj.get("name").toString();
                                        Integer id = (Integer) obj.get("id");
                                        //usersDictionary.put(id, name);
                                        Log.i("Id: " , String.valueOf(id));
                                        Log.i("Name: " ,name);
                                        users.put(id, name);
                                    }

                                    break;
                                }
                            } catch (JSONException e) {
                                System.out.println(e.getMessage());
                            }
                        } finally {
                            conn.disconnect();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
            Log.i("hashmap in getRequest", users.toString());
            return users;
        }
    */
    public void sendPostForSuspend(final int user_id, final int device_id, final int hour, final int minute) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(context.getString(R.string.suspend_automation_url));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("PersonId",user_id);
                    jsonParam.put("DeviceId",device_id);
                    jsonParam.put("Until",""+ hour +":" +minute);


                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());
                    System.out.println(" json param" + jsonParam);
                    os.flush();
                    os.close();
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void sendPostForEnable(final int user_id, final int device_id) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(context.getString(R.string.enable_automation_url));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("PersonId",user_id);
                    jsonParam.put("DeviceId",device_id);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());
                    System.out.println(" json param" + jsonParam);
                    os.flush();
                    os.close();
                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    protected String doInBackground(Object[] objects) {
        String url = (String ) objects[0];
        String result = "";
        String inputLine;
        try {
            //Create a URL object holding our url
            URL myUrl = new URL(url);
            //Create a connection
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();

            //Connect to our url
            connection.connect();
            //Create a new InputStreamReader
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine).append("\n");            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
/*

    @Override
    protected HashMap<Integer,String> doInBackground(Object[] objects) {

        final HashMap<Integer,String> users = new HashMap<Integer, String>();
                try {
                    URL url = new URL (context.getString(R.string.get_users_url));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        try {
                            while (true) {
                                JSONObject object = new JSONObject(stringBuilder.toString());
                                JSONArray usersArray = new JSONArray(object.get("people").toString());

                                for (int i = 0 ; i < usersArray.length() ; i++) {
                                    JSONObject obj = new JSONObject(usersArray.get(i).toString());
                                    //Log.i("user",obj.toString());
                                    String name = obj.get("name").toString();
                                    Integer id = (Integer) obj.get("id");
                                    //usersDictionary.put(id, name);
                                    Log.i("Id: " , String.valueOf(id));
                                    Log.i("Name: " ,name);
                                    users.put(id, name);
                                }
                                break;
                            }
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                    } finally {
                        conn.disconnect();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

        Log.i("hashmap in getRequest", users.toString());
        return users;

    }
 */
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}

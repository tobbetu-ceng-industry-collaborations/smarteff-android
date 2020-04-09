package com.ipekakova.smarteff_android.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ipekakova.smarteff_android.Models.User;
import com.ipekakova.smarteff_android.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class UpdateDevicesService extends IntentService {

    private static final String TAG = UpdateDevicesService.class.getSimpleName();
    SharedPreferences sp;
    User currentUser;

    public UpdateDevicesService() {
        super("MyWorkerThread");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "oncreate methodu " + Thread.currentThread().getName() + " threadi üzerinden cagrildi");
        super.onCreate();
        sp = getSharedPreferences("login",MODE_PRIVATE);
        //currentUser = new User(sp.getInt("user_id",0),sp.getString("user_name", "") );
        currentUser = User.getInstance();
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.i(TAG, "onHandleIntent methodu " + Thread.currentThread().getName() + " threadi üzerinden cagrildi");
        int sleepTime = intent.getIntExtra("sleepTime", 1);
        int kontrol = 1;

        while (true) {
            try {
                // 1 dakikada bir istek atar.
                Thread.sleep(30*1000);
                String devices = getUserDevices();
                Intent intent1 = new Intent("my.action");
                intent1.putExtra("veri", devices);
                sendBroadcast(intent1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            kontrol++;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy methodu " + Thread.currentThread().getName() + " threadi üzerinden cagrildi");
        super.onDestroy();
    }

    private String getUserDevices(){
            HttpGet http = new HttpGet();
        String devicesString  = null;
        try {
            devicesString = (String) http.execute( getString(R.string.get_devices_url)+ "/"+ currentUser.getId()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return devicesString;
    }

    private class HttpGet extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
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
                    stringBuilder.append(inputLine).append("\n");
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                connection.disconnect();
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
    }
}
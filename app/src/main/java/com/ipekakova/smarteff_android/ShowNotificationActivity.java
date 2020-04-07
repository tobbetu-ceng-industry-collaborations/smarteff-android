package com.ipekakova.smarteff_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by User on 11.03.2020.
 */

public class ShowNotificationActivity extends AppCompatActivity implements View.OnClickListener {

    Dialog dialog;
    Button yes,no;
    TextView tv_device_info;
    String date;
    int device_id;
    int user_id;
    SharedPreferences sp;


    String TAG = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_dialog);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        user_id = sp.getInt("user_id", 0);
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        tv_device_info = (TextView) findViewById(R.id.tv_device_info);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        date = bundle.getString("date");
        device_id = Integer.parseInt(bundle.getString("device_id"));
        Log.i("showNotification: ", date+ device_id);
        tv_device_info.setText("Your device D:" + device_id + "will be automatically shutdown on: "+ date);
        /*
        View view = getLayoutInflater().inflate(R.layout.notification_dialog,null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        yes = (Button) view.findViewById(R.id.button_suspend);
        no = (Button)view. findViewById(R.id.button_dismiss_suspend);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        */
        yes = (Button) findViewById(R.id.button_suspend);
        no = (Button) findViewById(R.id.button_dismiss_suspend);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_suspend){
            Log.e(TAG,"Suspendend.");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setTitle("Select a time for suspend your shutdown:");
                    builder.setSingleChoiceItems(R.array.suspend_hour_options, -1 ,new DialogInterface.OnClickListener() {
                        String[] hours = getResources().getStringArray(R.array.suspend_hour_options);
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {

                            Log.d(TAG, hours[which]+ " suspended.");

                        }
                    });
            builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("personid",user_id);
                        jsonObject.put("deviceid", device_id);
                        jsonObject.put("until", date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //TODO
                    new HttpPostAsyncTask(new WeakReference<Context>(getApplicationContext()),"json", jsonObject).execute(getString(R.string.suspend_automation_url));

                    //startActivity(intent);

                }
            });
            builder.show();

        }else if (v.getId() == R.id.button_dismiss_suspend){
            Log.e(TAG,"Discard notification.");
        }
    }
}
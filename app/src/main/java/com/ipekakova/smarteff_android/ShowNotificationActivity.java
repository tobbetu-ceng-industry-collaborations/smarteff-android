package com.ipekakova.smarteff_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    int addedHour;
    User currentUser;
    SharedPreferences sp;

    String TAG = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_dialog);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        user_id = sp.getInt("user_id", 0);

        currentUser = User.getInstance();
        Log.d(TAG, "Devices from singleton:"+ currentUser.getDevices().toString());
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        tv_device_info = (TextView) findViewById(R.id.tv_device_info);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        date = bundle.getString("date");
        device_id = Integer.parseInt(bundle.getString("device_id"));
        Log.i("showNotification: ", date + device_id);
        tv_device_info.setText("Your device D: " + device_id + " will be automatically shutdown on: "+ date);
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
                            addedHour = Integer.parseInt(hours[which]);

                        }
                    });
            builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DateParser dateParser = new DateParser(date);
                    JSONObject jsonObject = new JSONObject();
                    String finalDate = dateParser.getFinalDate(addedHour);

                    try {
                        jsonObject.put("personid",user_id);
                        jsonObject.put("deviceid", device_id);
                        jsonObject.put("until", finalDate);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new HttpPostAsyncTask(new WeakReference<Context>(getApplicationContext()),"json", jsonObject).execute(getString(R.string.suspend_automation_url));
                    Intent backToDevices = new Intent(getApplicationContext(), ShowDevicesActivity.class);
                    startActivity(backToDevices);
                    finish();
                }
            });
            builder.show();

        }else if (v.getId() == R.id.button_dismiss_suspend){
            Log.e(TAG,"Discard notification.");
            finish();
        }
    }

    //TODO
   private class DateParser{

    private int year, month, day, hour, minute, second;
        private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";
        private Date date= null;

        private DateParser(String until){
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            try {
                this.date = sdf.parse(currentDateandTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //calendar.add(Calendar.MINUTE, additionalMinute);
            //calendar.add(Calendar.SECOND, additionalSeconds);

           //“until”:”2020-01-29-10-30-00”
           String [] dates = until.split("-");
           year = Integer.parseInt(dates[0]);
           month = Integer.parseInt(dates[1]);
           day = Integer.parseInt(dates[2]);
           hour = Integer.parseInt(dates[3]);
           minute = Integer.parseInt(dates[4]);
           second = Integer.parseInt(dates[5]);
       }
        private String getFinalDate(int addedHour){
            int hour = this.hour + addedHour;
            String date = year+ "-"+ month + "-"+ day+ "-"+ hour + "-"+minute+ "-"+ second;
            return date;
        }
       private Calendar addHour(int additionalHour){
           Calendar calendar = Calendar.getInstance();
           calendar.setTime(date);
           calendar.add(Calendar.HOUR, additionalHour );
           return calendar;
       }

    }

}
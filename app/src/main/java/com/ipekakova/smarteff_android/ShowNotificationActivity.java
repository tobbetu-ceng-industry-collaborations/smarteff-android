package com.ipekakova.smarteff_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by User on 11.03.2020.
 */

public class ShowNotificationActivity extends AppCompatActivity implements View.OnClickListener {

    Dialog dialog;
    Button yes,no;
    String TAG = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.notification_dialog);
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.notification_dialog,null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        yes = (Button) view.findViewById(R.id.start_compaign_yes);
        no = (Button)view. findViewById(R.id.start_compaign_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.start_compaign_yes){
            Log.e(TAG,"customer press yes.");

        }else if (v.getId() == R.id.start_compaign_no){
            Log.e(TAG,"customer press no.");
        }
    }
}
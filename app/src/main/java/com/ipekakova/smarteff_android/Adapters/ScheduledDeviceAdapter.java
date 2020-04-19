package com.ipekakova.smarteff_android.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import com.ipekakova.smarteff_android.DateParser;
import com.ipekakova.smarteff_android.Models.SuspendendDevice;
import com.ipekakova.smarteff_android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 9.04.2020.
 */

public class ScheduledDeviceAdapter extends RecyclerView.Adapter<ScheduledDeviceAdapter.ScheduledDeviceViewHolder>{

    private ArrayList<SuspendendDevice> scheduled_devices = new ArrayList<>();
    private Context mContext;


    public ScheduledDeviceAdapter( Context mContext, ArrayList<SuspendendDevice> scheduled_devices) {
        this.scheduled_devices = scheduled_devices;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ScheduledDeviceAdapter.ScheduledDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scheduled_shutdowns_list_entry, parent, false);
        ScheduledDeviceViewHolder viewHolder = new ScheduledDeviceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduledDeviceAdapter.ScheduledDeviceViewHolder viewHolder, int position) {
        viewHolder.bindDevice(scheduled_devices.get(position));
    }

    @Override
    public int getItemCount() {
        return scheduled_devices.size();
    }


    public class ScheduledDeviceViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.sch_device_name) TextView device_name_tv;
        @Bind(R.id.sch_status) ImageView sch_status_iv;
        @Bind(R.id.sch_time) TextView sch_time_tv;
        private Context mContext;
        private String TAG = "FormatDevices";

        public ScheduledDeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindDevice(SuspendendDevice sch_device) {
            device_name_tv.setText("Device:"+sch_device.getId());
            sch_status_iv.setImageResource(sch_device.getStatus());
            sch_time_tv.setText(sch_device.getExpiration());

        }

    }
}

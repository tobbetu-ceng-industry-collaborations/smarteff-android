package com.ipekakova.smarteff_android.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipekakova.smarteff_android.Models.AutomatedDevice;
import com.ipekakova.smarteff_android.Models.SuspendendDevice;
import com.ipekakova.smarteff_android.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 23.04.2020.
 */

public class AutomatedDeviceAdapter  extends RecyclerView.Adapter<AutomatedDeviceAdapter.AutomatedDeviceViewHolder>{
    private ArrayList<AutomatedDevice> automatedDevices = new ArrayList<>();
    private Context mContext;


    public AutomatedDeviceAdapter( Context mContext, ArrayList<AutomatedDevice> scheduled_devices) {
        this.automatedDevices = scheduled_devices;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AutomatedDeviceAdapter.AutomatedDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scheduled_shutdowns_list_entry, parent, false);
        AutomatedDeviceAdapter.AutomatedDeviceViewHolder viewHolder = new AutomatedDeviceAdapter.AutomatedDeviceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AutomatedDeviceAdapter.AutomatedDeviceViewHolder viewHolder, int position) {
        viewHolder.bindDevice(automatedDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return automatedDevices.size();
    }


    public class AutomatedDeviceViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.sch_device_name)
        TextView device_name_tv;
        @Bind(R.id.sch_status)
        ImageView sch_status_iv;
        @Bind(R.id.sch_device_type) ImageView sch_device_type_iv;
        @Bind(R.id.sch_time) TextView sch_time_tv;
        private Context mContext;
        private String TAG = "FormatDevices";

        public AutomatedDeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindDevice(AutomatedDevice device) {
            device_name_tv.setText("D: "+ device.getId());
            sch_status_iv.setImageResource(device.getStatus());
            sch_time_tv.setText(device.getExpiration());
            String type = device.getDeviceType();
            if (type.equals("Lamp")){
                sch_device_type_iv.setImageResource(R.drawable.ic_light);
            }
            else if(type.equals("Air Conditioner")){
                sch_device_type_iv.setImageResource(R.drawable.ic_airconditioner);
            }else if(type.equals("Desk")){
                sch_device_type_iv.setImageResource(R.drawable.ic_workstation);
            }

        }
    }
}

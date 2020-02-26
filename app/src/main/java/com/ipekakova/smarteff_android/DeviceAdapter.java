package com.ipekakova.smarteff_android;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by User on 2.02.2020.
 */

public class DeviceAdapter extends ArrayAdapter<Device> {
    private final LayoutInflater inflater;
    private final Context context;
    //private idi deneme icin değistirdim.
    public ViewHolder holder;
    //private final ArrayList<Device> devices;

    public ArrayList<Device> devices;

    public DeviceAdapter(Context context, ArrayList<Device> devices) {
        super(context,0, devices);
        this.context = context;
        this.devices = devices;
        inflater = LayoutInflater.from(context);
    }
    public ArrayList<Device> getDevices(){
        return devices;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.device_list_entry, null);
            holder = new ViewHolder();
            holder.status = (ImageView) convertView.findViewById(R.id.status);
            holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            holder.automation = (ImageView) convertView.findViewById(R.id.automation);
            holder.button = (Button) convertView.findViewById(R.id.enable_button);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.suspend_dialog);

                    // custom dialog elemanlarını tanımla - text, image ve button
                    Button btnKaydet = (Button) dialog.findViewById(R.id.button_kaydet);
                    Button btnIptal = (Button) dialog.findViewById(R.id.button_iptal);
                    TextView tvBaslik = (TextView) dialog.findViewById(R.id.textview_baslik);
                    ImageView ivResim = (ImageView) dialog.findViewById(R.id.imageview_resim);
                    // custom dialog elemanlarına değer ataması yap - text, image
                    tvBaslik.setText("Are you sure?");
                    // tamam butonunun tıklanma olayları
                    btnKaydet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Device device = devices.get(position);
                            if (device.getAutomation() == R.drawable.radio_red){
                                Log.i("red", "Clicked on ENABLE button");
                                device.setAutomation(R.drawable.radio_green);
                                device.setSuspendOrEnable("Suspend");

                                Toast.makeText(context, "Enabled shutdown", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.i("red", "Clicked on SUSPEND button");
                                // Automation is on ise kapatti.
                                device.setSuspendOrEnable("Enable");
                                device.setAutomation(R.drawable.radio_red);
                                Toast.makeText(context, "Suspended automated shutdown", Toast.LENGTH_SHORT).show();
                            }

                            holder.automation.setImageResource(device.getAutomation());
                            holder.button.setText(device.getSuspendOrEnable());
                            notifyDataSetChanged();

                        }
                    });
                    // iptal butonunun tıklanma olayları
                    btnIptal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    Toast.makeText(context, "Clicked on BUTTON", Toast.LENGTH_LONG).show();
                    //Toast.LENGTH_LONG yerine 2000 girersek 2 sn gösterecektir.
                }
            });
            //holder.enableShutDown = (Switch) convertView.findViewById(R.id.enable_switch);
            convertView.setTag(holder);
        }
        else{
            //Get viewholder we already created
            holder = (ViewHolder)convertView.getTag();
        }

        final Device device = devices.get(position);
        if(device != null){
            holder.deviceName.setText(device.getId());
            holder.status.setImageResource(device.getStatus());
            holder.automation.setImageResource(device.getAutomation());
            //holder.enableShutDown.setEnabled(device.getEnableShutdown());
            holder.button.setText(device.getSuspendOrEnable());

        }
        Log.i("deviceadapter-device", devices.get(position).toString());
        return convertView;
    }

    @Override
    public int getCount() {
        return devices.size();
    }
    // Hangi satırdaki item için işlem yapıyorsak o satırdaki item’ı döndürür.
    @Override
    public Device getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return devices.get(position).hashCode();
    }


}

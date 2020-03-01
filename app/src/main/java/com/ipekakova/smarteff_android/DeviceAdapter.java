package com.ipekakova.smarteff_android;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by User on 2.02.2020.
 */

public class DeviceAdapter extends ArrayAdapter<Device> {
    private HttpRequest http;
    private final LayoutInflater inflater;
    private final Context context;
    //private idi deneme icin değistirdim.
    public ViewHolder holder;
    private User currentUser;
    //private final ArrayList<Device> devices;
    public ArrayList<Device> devices;


    public DeviceAdapter(Context context, ArrayList<Device> devices, User currentUser) {
        super(context,0, devices);
        this.context = context;
        this.devices = devices;
        this.currentUser = currentUser;
        http = new HttpRequest(context);
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

                    final Device device = devices.get(position);
                    // If selected device button is "Enable" then don't show any dialog, directly activate the automated shutdown
                    if (device.getAutomation() == R.drawable.radio_red){
                        Log.i("red", "Clicked on ENABLE button");
                        device.setAutomation(R.drawable.radio_green);
                        device.setSuspendOrEnable("Suspend");
                        holder.automation.setImageResource(device.getAutomation());
                        holder.button.setText(device.getSuspendOrEnable());

                        //Http Post Request for sending enable automation to the server.
                        http.sendPostForEnable(currentUser.getId(), device.getId());
                        notifyDataSetChanged();
                        Toast.makeText(context, "Enabled shutdown", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.suspend_dialog);

                        // custom dialog elemanlarını tanımla - text, image ve button
                        Button btnKaydet = (Button) dialog.findViewById(R.id.button_kaydet);
                        Button btnIptal = (Button) dialog.findViewById(R.id.button_iptal);
                        TextView tvBaslik = (TextView) dialog.findViewById(R.id.textview_baslik);
                        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.imageview_resim);
                        // custom dialog elemanlarına değer ataması yap - text, image
                        tvBaslik.setText("Are you sure?");
                        // tamam butonunun tıklanma olayları
                        btnKaydet.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                Log.i("red", "Clicked on SUSPEND button");
                                // Automation is on ise kapatti.
                                device.setSuspendOrEnable("Enable");
                                device.setAutomation(R.drawable.radio_red);
                                Toast.makeText(context, "Suspended automated shutdown", Toast.LENGTH_SHORT).show();
                                int hour = timePicker.getHour();
                                int minute = timePicker.getMinute();
                                Toast.makeText(context, hour +": "+ minute, Toast.LENGTH_SHORT).show();

                                //Http Post Request for sending suspension time infos to the server.
                                http.sendPostForSuspend(currentUser.getId(),device.getId(), hour, minute);
                                holder.automation.setImageResource(device.getAutomation());
                                holder.button.setText(device.getSuspendOrEnable());
                                notifyDataSetChanged();
                                dialog.dismiss();
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
                        Log.i("green", "Clicked on SUSPEND button");
                        // Automation is on ise kapatti.
                        device.setSuspendOrEnable("Enable");
                        device.setAutomation(R.drawable.radio_red);
                    }

                }
            });
            convertView.setTag(holder);
        }
        else{
            //Get viewholder we already created
            holder = (ViewHolder)convertView.getTag();
        }

        final Device device = devices.get(position);
        if(device != null){
            holder.deviceName.setText("Device:" + device.getId());
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
    }}



 /*         Alternatif TimePicker Dialog
                    final Calendar cldr = Calendar.getInstance();
                    int hour = cldr.get(Calendar.HOUR_OF_DAY);
                    int minutes = cldr.get(Calendar.MINUTE);
                    TimePickerDialog tpd = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    // hourOfDay ve minute değerleri seçilen saat değerleridir.
                                    // Edittextte bu değerleri gösteriyoruz.
                                    Toast.makeText(context, hourOfDay +": "+ minute, Toast.LENGTH_SHORT).show();

                                }
                            },hour,minutes, true);
                    // dialog penceresinin button bilgilerini ayarlıyoruz ve ekranda gösteriyoruz.
                    tpd.setButton(TimePickerDialog.BUTTON_POSITIVE, "Kaydet", tpd);
                    tpd.setButton(TimePickerDialog.BUTTON_NEGATIVE, "İptal", tpd);
                    tpd.onClick(tpd, TimePickerDialog.BUTTON_POSITIVE){
                    }
                    tpd.show();
                    */

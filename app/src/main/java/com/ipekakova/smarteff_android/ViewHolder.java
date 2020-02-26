package com.ipekakova.smarteff_android;

//Data holder pattern that holds a reference to the data inside of this list item in that way you don't have to constantly keep calling findViewById()
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by User on 2.02.2020.
 */

public class ViewHolder {
    TextView deviceName;
    ImageView status;
    ImageView automation;
    //Switch enableShutDown;
    Button button;

}
package com.ipekakova.smarteff_android;

/**
 * Created by User on 3.03.2020.
 */

public class SuspendendDevice extends Device {

    public String expirationDay;
    public String expirationMonth;
    public String expirationYear;
    public String expirationTime;
    public String expiration;

    public SuspendendDevice(int id, int isOn, int automation, String suspendOrEnable,String expiration) {
        super(id, isOn, automation, suspendOrEnable);
        this.expiration = expiration;
        String []  dates = expiration.split(" ");
        expirationDay = dates[0];
        expirationMonth = dates[1];
        expirationTime = dates[2];
        expirationYear = dates[3];
    }

    @Override
    public String toString() {
        return "SuspendendDevice{" +
                "expirationDay='" + expirationDay + '\'' +
                ", expirationMonth='" + expirationMonth + '\'' +
                ", expirationYear='" + expirationYear + '\'' +
                ", expirationTime='" + expirationTime + '\'' +
                '}';
    }
}

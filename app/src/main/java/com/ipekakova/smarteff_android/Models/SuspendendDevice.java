package com.ipekakova.smarteff_android.Models;

import com.ipekakova.smarteff_android.DateUtils;

import java.util.Calendar;

/**
 * Created by User on 3.03.2020.
 */

public class SuspendendDevice extends Device {

    public String expiration;
    public int day;
    public int month;
    public int year;
    public int hour;
    public int minute;
    public int second;
    private Calendar calendar;

    public SuspendendDevice(int id, int isOn, int automation, String suspendOrEnable,Calendar calendar, String deviceType) {
        super(id, isOn, automation, suspendOrEnable, deviceType);
        this.calendar = calendar;
        //this.expiration = expiration;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
    }

    public SuspendendDevice(int id, int isOn, int automation, String suspendOrEnable,int day, int month, int year, String deviceType) {
        super(id, isOn, automation, suspendOrEnable,deviceType );
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getExpiration() {
        DateUtils dateUtils = new DateUtils();
        return dateUtils.getStringFromDate(calendar.getTime(), "EEE, dd MMM HH:mm");
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public String toString() {
        return "SuspendendDevice{" +
                "id=" + id +
                ", deviceIsOn=" + deviceIsOn +
                ", automation=" + automation +
                ", suspendOrEnable='" + suspendOrEnable +
                ", expiration='" + expiration + '\'' +
                ", device_type='" + super.getDeviceType() + '\'' +
                '}';
    }
}

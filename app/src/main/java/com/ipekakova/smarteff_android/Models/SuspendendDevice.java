package com.ipekakova.smarteff_android.Models;

import java.util.Calendar;

/**
 * Created by User on 3.03.2020.
 */

public class SuspendendDevice extends Device {

    public String expirationDay;
    public String expirationMonth;
    public String expirationYear;
    public String expirationTime;
    public String expiration;
    public int day;
    public int month;
    public int year;
    public int hour;
    public int minute;
    public int second;
    private Calendar calendar;

    public SuspendendDevice(int id, int isOn, int automation, String suspendOrEnable,Calendar calendar, String expiration) {
        super(id, isOn, automation, suspendOrEnable);
        this.calendar = calendar;
        this.expiration = expiration;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
    }

    public SuspendendDevice(int id, int isOn, int automation, String suspendOrEnable,int day, int month, int year) {
        super(id, isOn, automation, suspendOrEnable);
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getExpiration() {
        return expiration;
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
                "expiration='" + expiration + '\'' +
                '}';
    }
}

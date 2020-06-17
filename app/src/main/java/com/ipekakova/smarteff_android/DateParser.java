package com.ipekakova.smarteff_android;

/**
 * Created by User on 8.04.2020.
 */

import android.util.Log;
import java.util.Calendar;
import java.util.Date;


public class DateParser{

    private int year, month, day, hour, minute, second;
    private Date date= null;
    private Calendar calendar = Calendar.getInstance();

    public DateParser(String until){
        String [] dates = until.split("-");
        year = Integer.parseInt(dates[0]);
        month = Integer.parseInt(dates[1]);
        day = Integer.parseInt(dates[2]);
        hour = Integer.parseInt(dates[3]);
        minute = Integer.parseInt(dates[4]);
        second = Integer.parseInt(dates[5]);
        calendar.set(year, month, day, hour, minute,second);
    }
    public String getStringDate(Calendar calendar){
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        String date = year+ "-"+ month + "-"+ day+ "-"+ hour + "-"+minute+ "-"+ second;
        return date;
    }
    public String addHour(int additionalHour){
        calendar.add(Calendar.HOUR_OF_DAY, additionalHour); // adds one hour
        calendar.getTime(); // returns new date object, one hour in the future
        String until = getStringDate(calendar);
        Log.d("Added Date" , until);
        return until;

    }
}
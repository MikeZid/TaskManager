package com.hfad.organaizer;

import androidx.annotation.NonNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Task {

    public final static SimpleDateFormat TASK_FORMAT_TIME = new SimpleDateFormat("HH:mm");
    public final static SimpleDateFormat TASK_FORMAT_DATE = new SimpleDateFormat("yy-MM-dd");
    private String name = "";



    private long timeInMilliSeconds = 0;
    private String timeInFormat = "";
    private String description = "";

    public Task(String name, String timeInFormat) {
        this.name = name;
        this.timeInFormat = timeInFormat;
    }

    public Task(String name, String description, long timeInMilliSeconds) {
        this.name = name;
        this.description = description;
        this.timeInMilliSeconds = timeInMilliSeconds;
        this.timeInFormat = setDateInMilliSeconds();
    }
    public Task() {
        Calendar calendar = Calendar.getInstance();
        this.timeInFormat = TASK_FORMAT_TIME.format(calendar.getTime());
    }
    public Task(Task task) {
        this.name = task.getName();
        this.timeInMilliSeconds = task.getTimeInMilliSeconds();
        this.timeInFormat = task.getTimeInFormat();
    }


    public String getName() {
        return this.name;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: "+ getName() + " Time: " + getTimeInFormat() + "Time in milliseconds: " + getTimeInMilliSeconds();
    }

    public long getTimeInMilliSeconds() {
        return this.timeInMilliSeconds;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getTimeInFormat() {
        return this.timeInFormat;
    }

    public String getDateInFormat() {
        return TASK_FORMAT_DATE.format(new Date(this.timeInMilliSeconds));
    }

    public void setTimeInFormat(String timeInFormat) throws ParseException {
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = TASK_FORMAT_TIME.parse(timeInFormat);
            calendar.set(Calendar.MINUTE, date.getMinutes());
            calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
            this.timeInFormat = TASK_FORMAT_TIME.format(calendar.getTime());
            this.timeInMilliSeconds = calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public String setDateInMilliSeconds() {
        return TASK_FORMAT_TIME.format(new Date(this.timeInMilliSeconds));
    }
    public void setTimeInMilliSeconds(long timeInMilliSeconds) {
        this.timeInMilliSeconds = timeInMilliSeconds;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

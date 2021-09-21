package com.hfad.organaizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrganaizerServiceTest extends Service {

    private static int NOTIFICATION_ID = 0;
    Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_ONE_SHOT);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("ALIVE");
                    try {
                        Calendar currentTime = Calendar.getInstance();
                        ArrayList<Boolean> days;
                        DataBaseHelper helper = new DataBaseHelper(context);
                        Cursor cursor = helper.getAllTasks();
                        while (cursor.moveToNext()) {
                            Task task = new Task(cursor.getString(1), cursor.getString(2), cursor.getLong(3));
                            int id = cursor.getInt(0);
                            if (cursor.getString(4).equals("")) {
                                if (task.getTimeInMilliSeconds() != 0 && (task.getTimeInMilliSeconds() - currentTime.getTimeInMillis() < 0 &&
                                        task.getTimeInMilliSeconds() - currentTime.getTimeInMillis() > -3000)) showNotification(pendingIntent, task);
                            } else {
                                Cursor scheduleCursor = helper.getSchedule(cursor.getString(4));
                                if (scheduleCursor.moveToNext())  {
                                    days = collectIntoArray(scheduleCursor.getInt(3), scheduleCursor.getInt(3), scheduleCursor.getInt(5), scheduleCursor.getInt(6), scheduleCursor.getInt(7), scheduleCursor.getInt(8), scheduleCursor.getInt(9));
                                    if (days.contains(true) && scheduleCursor.getInt(2)==1) {
                                        if (task.getTimeInMilliSeconds() - currentTime.getTimeInMillis() < 3000 &&
                                                task.getTimeInMilliSeconds() - currentTime.getTimeInMillis() > 0) {
                                            showNotification(pendingIntent, task);
                                        } else if (task.getTimeInMilliSeconds() < currentTime.getTimeInMillis()) {
                                            long time = updateTime(task, days ,currentTime);
                                            helper.updateTask(id, time);
                                        }
                                    }
                                }
                            }
                        }
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        Notification notification = new NotificationCompat.Builder(context, AppStarter.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    private long updateTime(Task task, ArrayList<Boolean> days, Calendar currentTime) {
        System.out.println(task.getDateInFormat() + " " + task.getTimeInFormat());
        Calendar taskTime = Calendar.getInstance();
        Date tempDate = new Date(task.getTimeInMilliSeconds());
        taskTime.set(Calendar.MINUTE, tempDate.getMinutes());
        taskTime.set(Calendar.HOUR_OF_DAY, tempDate.getHours());
        taskTime.set(Calendar.SECOND, 0);

        int day = taskTime.get(Calendar.DAY_OF_WEEK);

        while (true) {
            System.out.println("WHILE");
            if (days.get(day) && (currentTime.getTimeInMillis()<taskTime.getTimeInMillis())) {
                break;
            }
            else {
                taskTime.add(Calendar.DAY_OF_WEEK, 1);
                day++;
                if (day>7) day = 1;
            }
        }

        System.out.println(taskTime.getTime());
        return taskTime.getTimeInMillis();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showNotification(PendingIntent pendingIntent, Task task) {
        Notification notification = new NotificationCompat.Builder(context, AppStarter.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setContentText(task.getName())
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
        NOTIFICATION_ID++;
    }

    private ArrayList<Boolean> collectIntoArray(int mon, int tue, int wed, int thu, int fri, int sat, int sun) {
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(false);
        list.add(sun==1);
        list.add(mon==1);
        list.add(tue==1);
        list.add(wed==1);
        list.add(thu==1);
        list.add(fri==1);
        list.add(sat==1);
        return list;
    }
}

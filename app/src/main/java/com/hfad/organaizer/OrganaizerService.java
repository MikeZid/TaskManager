package com.hfad.organaizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.widget.CalendarView;

import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrganaizerService extends Service {

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
                        currentTime.set(Calendar.SECOND, 0);
                        ArrayList<Boolean> days;
                        DataBaseHelper helper = new DataBaseHelper(context);
                        Cursor cursor = helper.getAllTasks();
                        while (cursor.moveToNext()) {
                            Task task = new Task(cursor.getString(1), cursor.getString(2), cursor.getLong(3));
                            if (cursor.getString(4).equals("")) {
                                if (checkTime(task)) showNotification(pendingIntent, task);
                            } else {
                                Cursor scheduleCursor = helper.getSchedule(cursor.getString(4));
                                if (scheduleCursor.moveToNext())  {
                                    days = collectIntoArray(scheduleCursor.getInt(3), scheduleCursor.getInt(3), scheduleCursor.getInt(5),
                                            scheduleCursor.getInt(6), scheduleCursor.getInt(7),
                                            scheduleCursor.getInt(8), scheduleCursor.getInt(9));
                                    Calendar current = Calendar.getInstance();
                                    for (int i = 1; i<days.size(); i++) {
                                        if (days.get(i) && i==current.get(Calendar.DAY_OF_WEEK))
                                            if (checkTime(task)) showNotification(pendingIntent, task); } } } }
                        Thread.sleep(55000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .build();
        notification.defaults = 0;
        startForeground(1, notification);
        return START_NOT_STICKY;
    }


    private boolean checkTime(Task task) {
        if (task.getTimeInMilliSeconds()==0) return false;
        Calendar current = Calendar.getInstance();
        Calendar taskTime = Calendar.getInstance();
        taskTime.setTimeInMillis(task.getTimeInMilliSeconds());

        return (current.get(Calendar.HOUR)==taskTime.get(Calendar.HOUR) &&
                current.get(Calendar.MINUTE)==taskTime.get(Calendar.MINUTE) &&
                current.get(Calendar.DAY_OF_MONTH)==taskTime.get(Calendar.DAY_OF_MONTH) &&
                current.get(Calendar.YEAR)==taskTime.get(Calendar.YEAR) &&
                current.get(Calendar.MONTH)==taskTime.get(Calendar.MONTH));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void showNotification(PendingIntent pendingIntent, Task task) {
        Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_ID)
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

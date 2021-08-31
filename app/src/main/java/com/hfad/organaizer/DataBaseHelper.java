package com.hfad.organaizer;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    // Константы, такие как название БД, талиц, версия
    private static final String DB_NAME = "DATABASE_OF_APP";
    private static final String TASK_TABLE = "TASK_TABLE";
    private static final String SCHEDULE_TABLE = "SCHEDULE_TABLE";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase database;
    Context context;
    // Конструктор
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {                               // При создании БД, создаются таблицы "TASK_TABLE" и "SCHEDULE_TABLE"
        db.execSQL("CREATE TABLE TASK_TABLE (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, "
                + "DESCRIPTION TEXT, "
                + "DATE INTEGER,"
                + "SCHEDULE TEXT);");
        db.execSQL("CREATE TABLE SCHEDULE_TABLE (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, ACTIVE INTEGER, MON INTEGER, TUE INTEGER," +
                " WED INTEGER, THU INTEGER, FRI INTEGER," +
                " SAT INTEGER, SUN INTEGER);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    } // Этот метод не используется
    public void addTask(ContentValues values) { // Добавление задачи в БД
        if (values.getAsString("SCHEDULE").equals(""))  Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        database = getReadableDatabase();
        database.insert(TASK_TABLE, null, values);
    }
    public ArrayList<Task> getListOfTasks(String schedule_name) {   // Получение списка задач
        ArrayList<Task> result = new ArrayList<>();
        try {
            database = getReadableDatabase();
            Cursor cursor = database.query(TASK_TABLE, new String[] {"_id", "NAME", "DESCRIPTION", "DATE"}, "SCHEDULE=?",
                    new String[] {schedule_name}, null, null, "DATE ASC");
            while(cursor.moveToNext()) {
                Task task = new Task(cursor.getString(1), cursor.getString(2), cursor.getLong(3));
                result.add(task);
            }
        } catch (SQLiteException e) {
            return null;
        }
        return result;
    }
    public void addSchedule(ArrayList<Task> tasks, ContentValues nameAndDate) { // Добавления расписания
        database = getReadableDatabase();
        String nameSchedule = nameAndDate.getAsString("NAME");
        Cursor cursor = database.query(SCHEDULE_TABLE, new String[] {"NAME", "ACTIVE"}, "NAME=?", new String[] {nameSchedule}, null, null,null);
        if (cursor.getCount()>0 || nameSchedule.equals("")) {
            Toast.makeText(context, "Name should be unique", Toast.LENGTH_SHORT).show();
            return;
        }

        database.insert(SCHEDULE_TABLE, null, nameAndDate);
        if (tasks.size() != 0) {
            for (Task task: tasks) {
                ContentValues valuesForTask = new ContentValues();
                valuesForTask.put("NAME", task.getName());
                valuesForTask.put("DESCRIPTION", task.getDescription());
                valuesForTask.put("DATE", task.getTimeInMilliSeconds());
                valuesForTask.put("SCHEDULE", nameSchedule);
                addTask(valuesForTask);
                }
            }
        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ScheduleNameForRecyclerView> getAllSchedulesForRecyclerView() {    //Получение расписаний
        database = getReadableDatabase();
        ArrayList<ScheduleNameForRecyclerView> schedules = new ArrayList<>();
        Cursor cursor = database.query(SCHEDULE_TABLE, new String[] {"NAME", "ACTIVE"}, null , null, null, null,null);
        while (cursor.moveToNext()) {
            schedules.add(new ScheduleNameForRecyclerView(cursor.getString(0), cursor.getInt(1)));
        }
        return schedules;
    }
    public void deleteSchedule(String nameSchedule) {   // Удаление расписания
        database = getReadableDatabase();
        deleteTasksInSchedule(nameSchedule);
        database.delete(SCHEDULE_TABLE, "NAME=?", new String[]{nameSchedule});
    }

    public void deleteTasksInSchedule(String nameSchedule) {    // Удаление задач в расписании
        database.delete(TASK_TABLE, "SCHEDULE=?", new String[]{nameSchedule});
    }

    public Cursor getSchedule(String nameSchedule) {    // Получение расписания
        database = getReadableDatabase();
        return database.query(SCHEDULE_TABLE, null, "NAME=?", new String[] {nameSchedule}, null, null, null, null);
    }

    public void editSchedule(ArrayList<Task> tasks, ContentValues nameAndDate, String oldName) {    // Редактирование расписания
        database = getReadableDatabase();
        database.update(SCHEDULE_TABLE, nameAndDate, "NAME=?", new String[]{oldName});
        deleteTasksInSchedule(oldName);
        for (Task task:tasks) {
            ContentValues values = new ContentValues();
            values.put("NAME", task.getName());
            values.put("DESCRIPTION", task.getDescription());
            values.put("DATE", task.getTimeInMilliSeconds());
            values.put("SCHEDULE", nameAndDate.getAsString("NAME"));
            addTask(values);
        }
        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
    }
    public void changeActiveSchedule(ContentValues values) {   // поменять активность расписания
        database = getReadableDatabase();
        database.update(SCHEDULE_TABLE, values, "NAME=?", new String[]{values.getAsString("NAME")});
    }

    public Task getTask(int id) {   // получить задачу по id
        database = getReadableDatabase();
        Cursor cursor = database.query(TASK_TABLE, null, "_id=?", new String[]{String.valueOf(id)},null ,null, null, null);
        cursor.moveToNext();
        return new Task(cursor.getString(1), cursor.getString(2), cursor.getLong(3));

    }
    public Cursor getAllTasks() { // получить все задачи
        database = getReadableDatabase();
        Cursor cursor = database.query(TASK_TABLE, null, null, null, null, null, "DATE", null);
        return cursor;
    }

    public Cursor getAllTasks(String schedule_name) { //Получить все задачи по имени расписания
        try {
            database = getReadableDatabase();
            Cursor cursor = database.query(TASK_TABLE, new String[] {"_id", "NAME", "DESCRIPTION", "DATE"},
                    "SCHEDULE=?", new String[] {schedule_name}, null, null, "DATE ASC");
            return cursor;
        } catch (SQLiteException e) {
            return null;
        }

    }

    public void deleteTask(int id) {
        database = getReadableDatabase();
        database.delete(TASK_TABLE, "_id=?", new String[]{String.valueOf(id)});
    }

    public void editTask(ContentValues values) {
        database = getReadableDatabase();
        database.update(TASK_TABLE, values, "_id=?", new String[]{values.getAsString("_id")});
        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
    }

    public void updateTask(int id, long time) {
        database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("DATE", time);
        database.update(TASK_TABLE, values, "_id=?", new String[]{String.valueOf(id)});
    }
}

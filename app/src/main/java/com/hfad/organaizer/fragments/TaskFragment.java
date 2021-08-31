package com.hfad.organaizer.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.hfad.organaizer.DataBaseHelper;
import com.hfad.organaizer.R;
import com.hfad.organaizer.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    TextView nameTask, description, dateAndTimeText;    //Текстовые поля
    ImageButton acceptButton, deleteCurrentTimeButton, dateButton, timeButton;  //Кнопки
    int id;     //id фрагмента
    Calendar calendarForDataBase = Calendar.getInstance();
    SimpleDateFormat DATE_FORMAT_FOR_DATABASE = new SimpleDateFormat("yyyy-MM-dd HH:mm");   //Формат даты для вывода в TextView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_task, container, false);
        DialogFragment dataPicker = new DatePickerFragment();   // Создаём диалоговые окна time и date
        DialogFragment timePicker = new TimePickerFragment();
        dataPicker.setTargetFragment(this, 1);  // Передаём ссылку на этот фрагмент (вызваший) фрагмент
        timePicker.setTargetFragment(this, 1);
        acceptButton = layout.findViewById(R.id.accept_task);   // Получаем ссылки на все поля и кнопки, с которыми будем работать
        deleteCurrentTimeButton = layout.findViewById(R.id.delete_current_time);
        nameTask = layout.findViewById(R.id.NameTask);
        description = layout.findViewById(R.id.DescriptionTask);
        dateButton = layout.findViewById(R.id.open_picker_data);
        timeButton = layout.findViewById(R.id.open_picker_time);
        dateAndTimeText = layout.findViewById(R.id.edit_date_and_time);
        deleteCurrentTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // Метод удаления текущей даты
                dateAndTimeText.setText("");
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Метод установки даты
                dataPicker.show(getParentFragmentManager(), "date picker"); //Вызывается диалоговый фрагмент даты
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Метод установки времени
                timePicker.show(getParentFragmentManager(), "time picker"); //Вызывается диалоговый фрагмент времени
            }
        });
        if (getArguments() != null) {   // Проверка на наличие аргументов, если они есть, то был открыт существующая задача
            id = getArguments().getInt("_id");
            DataBaseHelper helper = new DataBaseHelper(getContext());   // Обращаемся к помощкнику Sqlite, с целью получить данные о задачи
            Task task = helper.getTask(id);     // Запрос возвращет объект тип Task
            nameTask.setText(task.getName());
            description.setText(task.getDescription());
            if (task.getTimeInMilliSeconds()!=0) dateAndTimeText.setText(task.getDateInFormat() + " " + task.getTimeInFormat());
            acceptButton.setOnClickListener(new View.OnClickListener() {    // метод перезаписи задачи
                @Override
                public void onClick(View v) {
                    ContentValues values = prepareData();   // Подготовка данных к перезаписи
                    values.put("_id", id);
                    DataBaseHelper helper = new DataBaseHelper(getContext());
                    helper.editTask(values);
                }
            });
        } else {    // Аргументов нет, значит пользователь создаёт новую задачу, все поля будут пустыми
           acceptButton.setOnClickListener(new View.OnClickListener() {     // Кнопка уже будет добавлять новую задчу в базу данных
               @Override
               public void onClick(View v) {
                   ContentValues values = prepareData();
                   DataBaseHelper helper = new DataBaseHelper(getContext());
                   helper.addTask(values);
               }
           });
        }
        return layout;
    }

    private long prepareDate() {
        Calendar calendar = Calendar.getInstance();
        try {
            //TODO сделать что-то с датой
            String date = dateAndTimeText.getText().toString();
            if (!date.equals("")) calendar.setTime(DATE_FORMAT_FOR_DATABASE.parse(date)); else return 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendarForDataBase.set(Calendar.YEAR, year);
        calendarForDataBase.set(Calendar.MONTH, month);
        calendarForDataBase.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateAndTimeText.setText(DATE_FORMAT_FOR_DATABASE.format(calendarForDataBase.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendarForDataBase.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendarForDataBase.set(Calendar.MINUTE, minute);
        dateAndTimeText.setText(DATE_FORMAT_FOR_DATABASE.format(calendarForDataBase.getTime()));

    }
    public ContentValues prepareData() {
        ContentValues values = new ContentValues();
        values.put("NAME", nameTask.getText().toString());
        values.put("DESCRIPTION", description.getText().toString());
        values.put("DATE", prepareDate());
        values.put("SCHEDULE", "");
        return values;
    }
}

package com.hfad.organaizer.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.organaizer.R;
import com.hfad.organaizer.Task;
import com.hfad.organaizer.fragments.ScheduleFragment;
import com.hfad.organaizer.fragments.TimePickerFragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.TaskViewHolder> {

    ArrayList<Task> tasks;
    Context context;
    ScheduleFragment scheduleFragment;
    DialogFragment timePicker;
    //Конструктор
    public ScheduleAdapter(ArrayList<Task> tasks, Context context, ScheduleFragment scheduleFragment) {
        this.tasks = tasks;
        this.context = context;
        this.scheduleFragment = scheduleFragment;
        this.timePicker = new TimePickerFragment();
        this.timePicker.setTargetFragment(scheduleFragment, 1);
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_in_schedule, parent, false);
        TaskViewHolder taskViewHolder = new TaskViewHolder(view, new TaskListenerSetName(), new TaskListenerPickTime(), new TaskListenerSetTime());

        return taskViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.taskListenerSetName.updatePosition(holder.getAdapterPosition());
        holder.taskListenerPickTime.updatePosition(holder.getAdapterPosition());
        holder.taskListenerSetTime.updatePosition(holder.getAdapterPosition());
        holder.setTask(tasks.get(holder.getAdapterPosition()));
    }
    @Override
    public int getItemCount() {
        return tasks.size();
    }
    //Элемент, который будет отображаться в списке
    public static class TaskViewHolder extends RecyclerView.ViewHolder  implements TimePickerDialog.OnTimeSetListener {
        EditText taskName;
        TextView taskTime;
        // Слушатели для сохранения введённых данных
        TaskListenerSetName taskListenerSetName;
        TaskListenerPickTime taskListenerPickTime;
        TaskListenerSetTime taskListenerSetTime;
        ImageButton editTime;
        // Конструктор
        public TaskViewHolder(@NonNull View itemView, TaskListenerSetName taskListenerSetName,
                              TaskListenerPickTime taskListenerPickTime, TaskListenerSetTime taskListenerSetTime) {
            super(itemView);
            this.taskName = (EditText) itemView.findViewById(R.id.schedule_task_name);
            this.taskTime = (TextView) itemView.findViewById(R.id.schedule_task_time);
            this.taskListenerSetName = taskListenerSetName;
            this.taskListenerPickTime = taskListenerPickTime;
            this.taskListenerSetTime = taskListenerSetTime;
            this.editTime = itemView.findViewById(R.id.edit_schedule_task_time);
            taskName.addTextChangedListener(taskListenerSetName);
            taskTime.addTextChangedListener(taskListenerSetTime);
            editTime.setOnClickListener(taskListenerPickTime);
        }
        // Установка нового задания
        public void setTask(Task task) {
            taskName.setText(task.getName());
            taskTime.setText(task.getTimeInFormat());
        }
        // Метод для получения времени из диалгового окна
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.MINUTE, minute);
            currentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            taskTime.setText(Task.TASK_FORMAT_TIME.format(currentCalendar.getTime()));
        }
    }
    // Реализация слушателей
    public class TaskListenerSetName implements TextWatcher {
        private int position;
        public void updatePosition(int position) {
            this.position = position;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tasks.get(position).setName(s.toString());
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public class TaskListenerSetTime implements TextWatcher {

        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                tasks.get(position).setTimeInFormat(s.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public class TaskListenerPickTime implements View.OnClickListener {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            timePicker.setArguments(bundle);
            timePicker.show(scheduleFragment.getParentFragmentManager(), "time picker");
        }
    }

    public void removeAt(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());
    }
}


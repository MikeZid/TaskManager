package com.hfad.organaizer.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.organaizer.DataBaseHelper;
import com.hfad.organaizer.R;
import com.hfad.organaizer.adapters.ScheduleAdapter;
import com.hfad.organaizer.Task;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment implements ChooseDaysOfWeek.ChooseDaysListener {

    ImageButton addTask, accept_schedule, edit_schedule_date;
    RecyclerView listOfTasks;
    TextView scheduleDate;
    EditText scheduleName;
    ArrayList<Task> tasks;
    boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_schedule, container, false);
        ChooseDaysOfWeek chooseDaysOfWeek = new ChooseDaysOfWeek();
        chooseDaysOfWeek.setTargetFragment(this, 1);
        edit_schedule_date = layout.findViewById(R.id.schedule_edit_date);
        scheduleDate = layout.findViewById(R.id.schedule_date_text);
        scheduleName = layout.findViewById(R.id.nameSchedule);
        addTask = layout.findViewById(R.id.schedule_add_task);
        accept_schedule = layout.findViewById(R.id.accept_schedule);
        listOfTasks = layout.findViewById(R.id.schedule_list);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            String currentName = arguments.getString("NAME");
            scheduleName.setText(currentName);
            DataBaseHelper helper = new DataBaseHelper(getContext());
            tasks = helper.getListOfTasks(currentName);
            setDays(arguments.getBoolean("MON"), arguments.getBoolean("TUE"), arguments.getBoolean("WED"), arguments.getBoolean("THU"),
                    arguments.getBoolean("FRI"), arguments.getBoolean("SAT"), arguments.getBoolean("SUN"));
            accept_schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues nameAndDate = new ContentValues();
                    nameAndDate.put("NAME", scheduleName.getText().toString());
                    nameAndDate.put("MON", monday ? 1 : 0);
                    nameAndDate.put("TUE", tuesday ? 1 : 0);
                    nameAndDate.put("WED", wednesday ? 1 : 0);
                    nameAndDate.put("THU", thursday ? 1 : 0);
                    nameAndDate.put("FRI", friday ? 1 : 0);
                    nameAndDate.put("SAT", saturday ? 1 : 0);
                    nameAndDate.put("SUN", sunday ? 1 : 0);
                    nameAndDate.put("ACTIVE", arguments.getInt("ACTIVE"));
                    DataBaseHelper helper = new DataBaseHelper(getContext());
                    helper.editSchedule(tasks, nameAndDate, currentName);
                }
            });
        } else  {
            tasks = new ArrayList<>();
            accept_schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues nameAndDate = new ContentValues();
                    nameAndDate.put("NAME", scheduleName.getText().toString());
                    nameAndDate.put("MON", monday ? 1 : 0);
                    nameAndDate.put("TUE", tuesday ? 1 : 0);
                    nameAndDate.put("WED", wednesday ? 1 : 0);
                    nameAndDate.put("THU", thursday ? 1 : 0);
                    nameAndDate.put("FRI", friday ? 1 : 0);
                    nameAndDate.put("SAT", saturday ? 1 : 0);
                    nameAndDate.put("SUN", sunday ? 1 : 0);
                    nameAndDate.put("ACTIVE", 0);
                    DataBaseHelper helper = new DataBaseHelper(getContext());
                    helper.addSchedule(tasks, nameAndDate);
                }
            });
        }
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(tasks, getContext(), this);
        listOfTasks.setAdapter(scheduleAdapter);
        listOfTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasks.add(new Task());
                scheduleAdapter.notifyDataSetChanged();
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                scheduleAdapter.removeAt(viewHolder.getAdapterPosition());
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(listOfTasks);
        edit_schedule_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putBoolean("sunday", sunday);
                arguments.putBoolean("monday", monday);
                arguments.putBoolean("tuesday", tuesday);
                arguments.putBoolean("wednesday", wednesday);
                arguments.putBoolean("thursday", thursday);
                arguments.putBoolean("friday", friday);
                arguments.putBoolean("saturday", saturday);
                chooseDaysOfWeek.setArguments(arguments);
                chooseDaysOfWeek.show(getActivity().getSupportFragmentManager(), "Choose days");
            }
        });

        return layout;
    }


    @Override
    public void setDays(boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun) {
        this.monday = mon;
        this.tuesday = tue;
        this.wednesday = wed;
        this.thursday = thu;
        this.friday = fri;
        this.saturday = sat;
        this.sunday = sun;
        setDaysOnField();
    }
    void setDaysOnField() {
        StringBuilder stringBuilder = new StringBuilder();
        if (sunday) {
            stringBuilder.append("SUN ");
        }
        if (monday) {
            stringBuilder.append("MON ");
        }
        if (tuesday) {
            stringBuilder.append("TUE ");
        }
        if (wednesday) {
            stringBuilder.append("WED ");
        }
        if (thursday) {
            stringBuilder.append("THU ");
        }
        if (friday) {
            stringBuilder.append("FRI ");
        }
        if (saturday) {
            stringBuilder.append("SAT ");
        }
        scheduleDate.setText(stringBuilder.toString());
    }
}

package com.hfad.organaizer.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hfad.organaizer.adapters.ScheduleAdapter;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (getTargetFragment() instanceof ScheduleFragment) {  // Проверка вызывающего фрагмента
            ScheduleFragment fragment = (ScheduleFragment) getTargetFragment();
            ScheduleAdapter.TaskViewHolder taskViewHolder = (ScheduleAdapter.TaskViewHolder)
                    fragment.listOfTasks.findViewHolderForAdapterPosition(getArguments().getInt("position"));
            return new TimePickerDialog(getActivity(),
                    (TimePickerDialog.OnTimeSetListener) taskViewHolder, hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
        }
        return new TimePickerDialog(getActivity(),
                (TimePickerDialog.OnTimeSetListener) getTargetFragment(), hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));
    }
}

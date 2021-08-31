package com.hfad.organaizer.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hfad.organaizer.R;
import com.hfad.organaizer.fragments.ScheduleFragment;

public class ChooseDaysOfWeek extends DialogFragment {
    CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    ChooseDaysListener listener;    // Ссылка на слушатель
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());   // Создание объекта Builder
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_frament_choose_days, null); // Создаём view и задаём макет
        builder.setView(view).setNegativeButton("Back", new DialogInterface.OnClickListener() { // Устанавливаем view и добавляем кнопки "отмена" и "сохранить"
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();  //Закрыть диалоговое окно
            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // C помощью слушателя передаём состояни CheckBox
                listener.setDays(monday.isChecked(), tuesday.isChecked(), wednesday.isChecked(), thursday.isChecked(),
                        friday.isChecked(), saturday.isChecked(), sunday.isChecked());

            }
        });
        // Получаем ссылки на наши CheckBox
        monday = view.findViewById(R.id.monday);
        tuesday = view.findViewById(R.id.tuesday);
        wednesday = view.findViewById(R.id.wednesday);
        thursday = view.findViewById(R.id.thursday);
        friday = view.findViewById(R.id.friday);
        saturday = view.findViewById(R.id.saturday);
        sunday = view.findViewById(R.id.sunday);
        // Устанавливаем значения, которые были изначально, по-умолчанию всё false
        sunday.setChecked(getArguments().getBoolean("sunday"));
        monday.setChecked(getArguments().getBoolean("monday"));
        tuesday.setChecked(getArguments().getBoolean("tuesday"));
        wednesday.setChecked(getArguments().getBoolean("wednesday"));
        thursday.setChecked(getArguments().getBoolean("thursday"));
        friday.setChecked(getArguments().getBoolean("friday"));
        saturday.setChecked(getArguments().getBoolean("saturday"));

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {    // Метод вызывается до onCreateDialog
        super.onAttach(context);
        try {
            listener = (ScheduleFragment) getTargetFragment();  // Привязываем наш слушатель, таким образом метод setDays вызовется у фрагмета ScheduleFragment
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
    // Слущатель
    public interface ChooseDaysListener {
        void setDays(boolean mon, boolean tue, boolean wed, boolean thu, boolean fri, boolean sat, boolean sun);
    }
}

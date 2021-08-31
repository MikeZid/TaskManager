package com.hfad.organaizer.adapters;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.hfad.organaizer.DataBaseHelper;
import com.hfad.organaizer.R;
import com.hfad.organaizer.ScheduleNameForRecyclerView;
import com.hfad.organaizer.fragments.ScheduleFragment;
import java.util.ArrayList;
public class ListOfSchedulesAdapter extends RecyclerView.Adapter<ListOfSchedulesAdapter.ScheduleViewHolder> {
    // Эти поля будут использоваться во всём классе, поэтому объявим их здесь
    Context context;
    FragmentActivity fragmentActivity;
    ArrayList<ScheduleNameForRecyclerView> schedules;
    // Конструктор
    public ListOfSchedulesAdapter(FragmentActivity fragmentActivity, Context context, ArrayList<ScheduleNameForRecyclerView> schedules) {
        this.context = context;
        this.schedules = schedules;
        this.fragmentActivity = fragmentActivity;
    }
    @Override
    // Создание и добавление в список объектов ScheduleViewHolder
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_in_list_of_schedules, parent, false);
        ScheduleViewHolder scheduleViewHolder = new ScheduleViewHolder(view);
        return scheduleViewHolder;
    }
    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) { //Вызывается для отображения данных в указанной позиции.
        // Установка имени и активности расписания
        holder.nameSchedule.setText(schedules.get(holder.getAdapterPosition()).getName());
        holder.activeSchedule.setChecked(schedules.get(holder.getAdapterPosition()).isActivated());
    }
    @Override
    public int getItemCount() {
        return schedules.size();
    }
    public void removeAt(int position) {    // Удаление расписания
        DataBaseHelper helper = new DataBaseHelper(context);
        helper.deleteSchedule(schedules.get(position).getName());
        schedules.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, schedules.size());
    }

    //Элемент, который будет отображаться в списке
    class ScheduleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameSchedule;
        CheckBox activeSchedule;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            this.nameSchedule = itemView.findViewById(R.id.nameSchedule_in_list);
            this.activeSchedule = itemView.findViewById(R.id.active_schedule);
            // Слушатель CheckBox, измениния сразу перезаписываются в базу данных
            activeSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DataBaseHelper helper = new DataBaseHelper(context);
                    ContentValues values = new ContentValues();
                    values.put("ACTIVE", activeSchedule.isChecked()? 1 : 0);
                    values.put("NAME", nameSchedule.getText().toString());
                    helper.changeActiveSchedule(values);
                }
            });
            nameSchedule.setOnClickListener(this);
        }
        // Реализаця метода onClick, то есть при нажатии на элемент
        @Override
        public void onClick(View v) {
            DataBaseHelper helper = new DataBaseHelper(context);
            String nameSchedule = schedules.get(this.getAdapterPosition()).getName();
            Cursor cursor = helper.getSchedule(nameSchedule);
            Bundle arguments = new Bundle();
            if (cursor.moveToNext()) {
                arguments.putBoolean("MON", cursor.getInt(3)==1);
                arguments.putBoolean("TUE", cursor.getInt(4)==1);
                arguments.putBoolean("WED", cursor.getInt(5)==1);
                arguments.putBoolean("THU", cursor.getInt(6)==1);
                arguments.putBoolean("FRI", cursor.getInt(7)==1);
                arguments.putBoolean("SAT", cursor.getInt(8)==1);
                arguments.putBoolean("SUN", cursor.getInt(9)==1);
                arguments.putInt("ACTIVE", cursor.getInt(2));
            }
            arguments.putString("NAME", nameSchedule);
            ScheduleFragment fragment = new ScheduleFragment();
            fragment.setArguments(arguments);
            fragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }
    }
}

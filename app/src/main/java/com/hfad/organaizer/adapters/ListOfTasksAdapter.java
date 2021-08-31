package com.hfad.organaizer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
import com.hfad.organaizer.DataBaseHelper;
import com.hfad.organaizer.R;
import com.hfad.organaizer.fragments.TaskFragment;

import java.util.Calendar;

public class ListOfTasksAdapter extends RecyclerViewCursorAdapter<ListOfTasksAdapter.TaskViewHolder> {
    Context context;
    FragmentActivity fragmentActivity;
    //Конструктор
    public ListOfTasksAdapter(Context context, Cursor cursor, FragmentActivity fragmentActivity) {
        super(context);
        this.context = context;
        this.fragmentActivity = fragmentActivity;
        setupCursorAdapter(cursor, 0, R.layout.row_in_list_of_tasks, false);    //Передаём курсор, указываем макет строки в списке
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //Создание и добавление в список объектов TaskViewHolder
        return new TaskViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent));
    }
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {    //Вызывается для отображения данных в указанной позиции.
        mCursorAdapter.getCursor().moveToPosition(position);  //Получаем курсор и сразу его передвигаем на ту позицию, которая соответствует ViewHolder
        setViewHolder(holder);                              // Set the ViewHolder
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());   // Bind this view
        if (!holder.checkTime()) holder.taskName
                .setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);  // Проверка даты задачи, если дата прошедшая-то название будет зачёркнуто
    }
    public void remove(int position) {  //Метод удаления элемента(TaskViewHolder) из списка
        mCursorAdapter.getCursor().moveToPosition(position);
        DataBaseHelper helper = new DataBaseHelper(context);
        helper.deleteTask(mCursorAdapter.getCursor().getInt(0));    // Из базы данных элемент тоже удаляем
        swapCursor(helper.getAllTasks(""));    //Делаем новый запрос для обновления списка, получаем новый курсор и меняем его старый
    }
    public class TaskViewHolder extends RecyclerViewCursorViewHolder {  //Элемент, который будет отображаться в списке
        public final TextView taskName;
        //Конструктор
        public TaskViewHolder(View view) {
            super(view);
            this.taskName = (TextView) view.findViewById(R.id.title_task);
            this.taskName.setOnClickListener(new View.OnClickListener() {   // Добавляем слушатель при нажатии на задачу в списке
                @Override
                public void onClick(View v) {
                    DataBaseHelper helper = new DataBaseHelper(context);
                    mCursorAdapter.getCursor().moveToPosition(getAdapterPosition());
                    Bundle arguments = new Bundle();        // Подготавливаем данные для передачи следущему фрагменту
                    arguments.putInt("_id", mCursorAdapter.getCursor().getInt(0));
                    TaskFragment fragment = new TaskFragment(); // Создаём фрагмент
                    fragment.setArguments(arguments); //Устанавливаем аргументы
                    fragmentActivity.getSupportFragmentManager()
                            .beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit(); // Фрагмент появляется в FrameLayout
                }
            });
        }
        private boolean checkTime() {   //Метод проверки времени
            Calendar currentTime = Calendar.getInstance();  //Создаём Calendar, с текущим временем
            Cursor cursor = mCursorAdapter.getCursor(); // Получаем курсор
            cursor.moveToPosition(getAdapterPosition());   // Передвигаем курсор на соответственное место
            long time = cursor.getLong(3);     // Получаем время текущей задачи
            if (time < currentTime.getTimeInMillis() && time != 0) {    // Сравниваем, если время прошло, то задачу зачёркиваем
                    return false;
            }

            return true;
        }
        @Override
        public void bindCursor(Cursor cursor) {
            taskName.setText(cursor.getString(1));
        }

    }
}

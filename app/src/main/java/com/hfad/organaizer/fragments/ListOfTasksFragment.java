package com.hfad.organaizer.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hfad.organaizer.DataBaseHelper;
import com.hfad.organaizer.adapters.ListOfTasksAdapter;
import com.hfad.organaizer.R;

public class ListOfTasksFragment extends Fragment {
    RecyclerView list; // Ссылка на список
    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_list_of_tasks, container, false);
        list = layout.findViewById(R.id.list_of_tasks);
        DataBaseHelper helper = new DataBaseHelper(getContext());   // Создаём помощника Sqlite
        ListOfTasksAdapter adapter = new ListOfTasksAdapter(getContext(), helper.getAllTasks(""), getActivity());  //Создаём адаптер
        list.setAdapter(adapter); //устанавливаем адаптер
        list.setLayoutManager(new LinearLayoutManager(getContext()));   // устанавливаем макет внутри списка (Линейный макет)
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {  //Добавляем поддержку swipe для удаления
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.remove(viewHolder.getAdapterPosition());    // Удаление Задачи
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show(); // Показ сообщения пользователю, об успешном удалении
            }
        }).attachToRecyclerView(list);
        return layout;
    }

    @NonNull
    @Override
    public String toString() {
        return "ListOfTasksFragment";
    }
}

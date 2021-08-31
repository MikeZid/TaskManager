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
import com.hfad.organaizer.adapters.ListOfSchedulesAdapter;
import com.hfad.organaizer.R;

public class ListOfSchedulesFragment extends Fragment {

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_list_of_schedules, container, false);
        RecyclerView list = layout.findViewById(R.id.list_of_schedules);
        DataBaseHelper helper = new DataBaseHelper(getContext());
        ListOfSchedulesAdapter listOfSchedulesAdapter = new ListOfSchedulesAdapter(getActivity(), getContext(), helper.getAllSchedulesForRecyclerView());
        list.setAdapter(listOfSchedulesAdapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                listOfSchedulesAdapter.removeAt(viewHolder.getAdapterPosition());
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(list);
        return layout;
    }
}


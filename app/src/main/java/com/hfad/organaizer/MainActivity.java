package com.hfad.organaizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hfad.organaizer.fragments.CalendarFragment;
import com.hfad.organaizer.fragments.ListOfSchedulesFragment;
import com.hfad.organaizer.fragments.ListOfTasksFragment;
import com.hfad.organaizer.fragments.ScheduleFragment;
import com.hfad.organaizer.fragments.TaskFragment;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                     //Вызывается родительский метод
        setContentView(R.layout.activity_main);                                 //Устнанвливается макет
        BottomNavigationView bottomNavigationView = findViewById(R.id.toolbar); // Получаем ссылку на нижнюю панель
        bottomNavigationView.setOnNavigationItemSelectedListener(listener); // Устанавливаем слушатель
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Добавляем кнопку назад на верхней панели
        if (!isServiceRunning(OrganaizerService.class)) {
            Intent intent = new Intent(this, OrganaizerService.class); // Создаём интент для запуска службы
            startService(intent); // Запуск службы
        }
        bottomNavigationView.setSelectedItemId(R.id.tasks_button); // Устанавливается кнопка раздела
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ListOfTasksFragment()).commit(); // Устанавливается
    }

    @SuppressLint("NonConstantResourceId")
    private BottomNavigationView.OnNavigationItemSelectedListener listener = item -> {   //Слушатель нижней панели
        Fragment selectedFragment = new ListOfTasksFragment();
                switch (item.getItemId()) {     //Методу передаётся объект MenuItem (Это нажатая кнопка), получаем id
                    case R.id.calendar_button:         //Сравниваем полученный id с остальными, так мы понимаем какая кнопка, была нажата.
                        selectedFragment = new CalendarFragment();  //Создаём новый фрагмент
                        break;
                    case R.id.tasks_button:
                        selectedFragment = new ListOfTasksFragment();
                        break;
                    case R.id.schedules_button:
                        selectedFragment = new ListOfSchedulesFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit(); //Помещаем выбранный фрагмент в FrameLayout
                return true;
    };

    public void openAdderTask(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new TaskFragment()).addToBackStack(null).commit();  // Метод при нажатии на кнопку AddTask
    }

    public void openAdderSchedule(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ScheduleFragment()).addToBackStack(null).commit(); // Метод при нажатии на кнопку AddSchedule
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack(); // Метод при нажатии на кнопку назад
        return true;
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
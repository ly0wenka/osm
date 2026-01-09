package com.example.l2java;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ProcessInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Налаштування відступів інтерфейсу
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Отримання PID поточного процесу (Аналог Process.GetCurrentProcess().Id)
        int pid = android.os.Process.myPid();
        Log.d(TAG, "Батьківський процес (PID: " + pid + ")");

        // 2. Імітація запуску "дочерніх" процесів
        // В Android замість шляхів до файлів ми використовуємо URI або назви пакетів
        //String[] appsToStart = {"https://google.com", "tel:123456789", "geo:50.4501,30.5234?z=10"};
        String[] appsToStart = {"package:com.example.l2"};

        for (String target : appsToStart) {
            try {
                Intent intent;
                if (target.startsWith("package:")) {
                    String packageName = target.replace("package:", "");

                    // Отримуємо Intent для запуску головного екрану l2kotlin
                    intent = getPackageManager().getLaunchIntentForPackage(packageName);

                    if (intent == null) {
                        // Якщо додаток не встановлено, виводимо помилку
                        throw new Exception("Додаток " + packageName + " не знайдено!");
                    }
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(target));
                }

                startActivity(intent);
                Log.d(TAG, "Успішно запущено: " + target);

            } catch (Exception ex) {
                Log.e(TAG, "Помилка: " + ex.getMessage());
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
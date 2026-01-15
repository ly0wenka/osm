package com.example.l3daemon_java;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Кнопка ЗАПУСКУ (Аналог запуску демона)
        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyAndroidDaemon.class);
            // Використовуємо ContextCompat для сумісності версій Android
            ContextCompat.startForegroundService(this, intent);
        });

        // Кнопка СИГНАЛУ (Аналог SIGUSR1)
        Button btnSignal = findViewById(R.id.btnSignal);
        btnSignal.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyAndroidDaemon.class);
            intent.setAction("ACTION_LOG_STATUS");
            startService(intent);
        });

        // Кнопка ЗУПИНКИ (Аналог SIGINT)
        Button btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyAndroidDaemon.class);
            stopService(intent);
        });
    }
}
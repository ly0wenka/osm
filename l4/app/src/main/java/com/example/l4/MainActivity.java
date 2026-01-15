package com.example.l4;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    private TextView logTextView;

    // Механізми синхронізації
    private final Lock mutex = new ReentrantLock();
    private int sharedCounter = 0;
    private final Semaphore semaphore = new Semaphore(2);
    private final Lock conditionLock = new ReentrantLock();
    private final Condition condition = conditionLock.newCondition();
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        logTextView = findViewById(R.id.logTextView);

        runThreadsDemo();
    }

    // Метод для виводу тексту на екран з будь-якого потоку
    private void logToScreen(String message) {
        runOnUiThread(() -> {
            logTextView.append(message + "\n");
        });
    }

    private void runThreadsDemo() {
        // --- MUTEX ---
        for (int i = 1; i <= 3; i++) {
            final int id = i;
            new Thread(() -> {
                mutex.lock();
                try {
                    sharedCounter++;
                    logToScreen("🔒 Mutex: Потік " + id + " працює. Counter=" + sharedCounter);
                    Thread.sleep(800);
                } catch (InterruptedException e) { e.printStackTrace(); }
                finally { mutex.unlock(); }
            }).start();
        }

        // --- SEMAPHORE (макс 2 одночасно) ---
        for (int i = 1; i <= 4; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    logToScreen("🚦 Semaphore: Потік " + id + " УВІЙШОВ");
                    Thread.sleep(1500);
                    logToScreen("🚦 Semaphore: Потік " + id + " ВИЙШОВ");
                    semaphore.release();
                } catch (InterruptedException e) { e.printStackTrace(); }
            }).start();
        }

        // --- CONDITION VARIABLE ---
        new Thread(() -> {
            conditionLock.lock();
            try {
                logToScreen("⏳ Condition: Потік чекає на сигнал...");
                while (!isReady) {
                    condition.await();
                }
                logToScreen("✅ Condition: СИГНАЛ ОТРИМАНО!");
            } catch (InterruptedException e) { e.printStackTrace(); }
            finally { conditionLock.unlock(); }
        }).start();

        new Thread(() -> {
            try { Thread.sleep(4000); } catch (InterruptedException e) { e.printStackTrace(); }
            conditionLock.lock();
            try {
                isReady = true;
                condition.signal();
                logToScreen("🔔 Condition: Сигнал відправлено!");
            } finally { conditionLock.unlock(); }
        }).start();
    }
}
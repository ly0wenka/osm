package com.example.l4;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    private TextView logTextView;
    private int sharedCounter = 0;
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logTextView = findViewById(R.id.logTextView);

        findViewById(R.id.btnMutex).setOnClickListener(v -> runMutexTest());
        findViewById(R.id.btnSemaphore).setOnClickListener(v -> runSemaphoreTest());
        findViewById(R.id.btnCondition).setOnClickListener(v -> runConditionTest());
    }

    private void logToScreen(String message) {
        runOnUiThread(() -> logTextView.append(message + "\n"));
    }

    // --- TEST 1: MUTEX ---
    private void runMutexTest() {
        logTextView.setText("--- ЗАПУСК MUTEX (По черзі) ---\n");
        sharedCounter = 0;
        final Lock mutex = new ReentrantLock();

        for (int i = 1; i <= 3; i++) {
            final int id = i;
            new Thread(() -> {
                mutex.lock();
                try {
                    sharedCounter++;
                    logToScreen("🔒 Потік " + id + " зайшов. Counter: " + sharedCounter);
                    Thread.sleep(1000); // Потоки чекатимуть один одного
                } catch (InterruptedException e) { e.printStackTrace(); }
                finally {
                    logToScreen("🔓 Потік " + id + " вийшов.");
                    mutex.unlock();
                }
            }).start();
        }
    }

    // --- TEST 2: SEMAPHORE ---
    private void runSemaphoreTest() {
        logTextView.setText("--- ЗАПУСК SEMAPHORE (Макс 2 одночасно) ---\n");
        final Semaphore semaphore = new Semaphore(2);

        for (int i = 1; i <= 5; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    logToScreen("👤 Потік " + id + " хоче увійти...");
                    semaphore.acquire();
                    logToScreen("🚦 [OK] Потік " + id + " ПРАЦЮЄ");
                    Thread.sleep(2000);
                    logToScreen("🚪 Потік " + id + " ЗВІЛЬНИВ місце");
                    semaphore.release();
                } catch (InterruptedException e) { e.printStackTrace(); }
            }).start();
        }
    }

    // --- TEST 3: CONDITION ---
    private void runConditionTest() {
        logTextView.setText("--- ЗАПУСК CONDITION (Очікування сигналу) ---\n");
        isReady = false;
        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();

        // Потік-очікувач
        new Thread(() -> {
            lock.lock();
            try {
                logToScreen("⏳ Очікувач: Чекаю на команду 'Пуск'...");
                while (!isReady) {
                    condition.await();
                }
                logToScreen("🚀 Очікувач: СИГНАЛ ОТРИМАНО! Починаю роботу.");
            } catch (InterruptedException e) { e.printStackTrace(); }
            finally { lock.unlock(); }
        }).start();

        // Потік-сигналізатор
        new Thread(() -> {
            try {
                Thread.sleep(3000); // Затримка перед сигналом
                lock.lock();
                try {
                    isReady = true;
                    condition.signal();
                    logToScreen("🔔 Сигналізатор: Натиснув кнопку 'Пуск'!");
                } finally { lock.unlock(); }
            } catch (InterruptedException e) { e.printStackTrace(); }
        }).start();
    }
}
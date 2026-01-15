package com.example.l3daemon_java;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class MyAndroidDaemon extends Service {
    private static final String TAG = "AndroidDaemon";
    private static final String CHANNEL_ID = "DaemonChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Аналог fork() та setsid() — перетворення сервісу на Foreground
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Демон працює")
                .setContentText("Запис логів у Logcat...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        // Android вимагає сповіщення для фонових процесів
        startForeground(1, notification);

        // Обробка "сигналів" через Intent (замість SIGUSR1)
        if (intent != null && "ACTION_LOG_STATUS".equals(intent.getAction())) {
            logStatus();
        }

        return START_STICKY; // Перезапуск сервісу, якщо його вб'є система
    }

    private void logStatus() {
        // Аналог SIGUSR1 — запис у системний журнал
        Log.i(TAG, "Отримано команду: Запис у Logcat (аналог SIGUSR1)");
    }

    @Override
    public void onDestroy() {
        // Аналог SIGINT — завершення роботи
        Log.i(TAG, "Сервіс зупинено (аналог SIGINT)");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Daemon Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

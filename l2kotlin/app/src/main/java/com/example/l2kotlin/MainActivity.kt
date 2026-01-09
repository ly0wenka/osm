package com.example.l2kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val TAG = "ProcessInfo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Вмикаємо EdgeToEdge (аналог EdgeToEdge.enable(this))
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Налаштування відступів системних панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Отримання PID процесу
        val pid = android.os.Process.myPid()
        Log.d(TAG, "Батьківський процес (PID: $pid)")

        // 2. Список додатків для запуску
        val appsToStart = arrayOf("package:com.example.l2")

        appsToStart.forEach { target ->
            try {
                val intent: Intent? = if (target.startsWith("package:")) {
                    val packageName = target.replace("package:", "")

                    // Отримуємо Intent для запуску через packageManager
                    packageManager.getLaunchIntentForPackage(packageName) ?:
                    throw Exception("Додаток $packageName не знайдено!")
                } else {
                    Intent(Intent.ACTION_VIEW, Uri.parse(target))
                }

                startActivity(intent)
                Log.d(TAG, "Успішно запущено: $target")

            } catch (ex: Exception) {
                Log.e(TAG, "Помилка: ${ex.message}")
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
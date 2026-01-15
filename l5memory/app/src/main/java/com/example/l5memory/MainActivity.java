package com.example.l5memory;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.hello_text);

        // Створюємо менеджер на 1024 байти
        MemoryManager lmm = new MemoryManager(1024);

        // Імітуємо роботу
        long addr1 = lmm.malloc(256);
        long addr2 = lmm.malloc(128);

        String result = "Base Address: 0x" + Long.toHexString(addr1) + "\n\n";
        result += "Memory Map:\n" + lmm.getMemoryStatus();

        tv.setText(result);
    }
}
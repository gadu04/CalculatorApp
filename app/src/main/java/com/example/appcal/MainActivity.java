package com.example.appcal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.activities.AgeCalculatorActivity;
import com.example.appcal.activities.BasicCalculatorActivity;
import com.example.appcal.activities.CurrencyConverterActivity;
import com.example.appcal.activities.SettingsActivity; // ✅ Thêm dòng này

public class MainActivity extends AppCompatActivity {
    GridView gridMenu;

    // ✅ Thêm "Settings" vào danh sách công cụ
    String[] tools = {"Basic Calculator", "Tax Calculator", "Currency", "Settings"};
    int[] icons = {
            R.drawable.ic_calculator,
            R.drawable.ic_age,
            R.drawable.ic_currency,
            R.drawable.ic_settings // ✅ Icon settings
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Màu thanh điều hướng
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }

        // ✅ Màu icon điều hướng
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            );
        } else {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        // ✅ Setup GridView
        gridMenu = findViewById(R.id.gridMenu);
        GridMenuAdapter adapter = new GridMenuAdapter(this, tools, icons);
        gridMenu.setAdapter(adapter);

        gridMenu.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(this, BasicCalculatorActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(this, AgeCalculatorActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(this, CurrencyConverterActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(this, SettingsActivity.class)); // ✅ Settings
                    break;
            }
        });
    }
}

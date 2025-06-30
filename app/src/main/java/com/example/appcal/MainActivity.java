package com.example.appcal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.AdapterView;
import android.widget.GridView;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.activities.AgeCalculatorActivity;
import com.example.appcal.activities.BasicCalculatorActivity;
import com.example.appcal.activities.BmiBmrCalculatorActivity;
import com.example.appcal.activities.CurrencyConverterActivity;
import com.example.appcal.activities.LengthConverterActivity;
import com.example.appcal.activities.SelectEquationDegreeActivity;

public class MainActivity extends AppCompatActivity {
    GridView gridMenu;

    String[] tools = {
            "Basic Calculator",
            "Tax Calculator",
            "Currency",
            "Equation Solver",
            "Length Converter",
            "BMI & BMR Calculator"
    };


    int[] icons = {
            R.drawable.ic_calculator,
            R.drawable.ic_age,
            R.drawable.ic_currency,
            R.drawable.ic_equation,
            R.drawable.ic_length,
            R.drawable.ic_bmi

    };


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Đặt màu thanh điều hướng
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

// Thiết lập appearance thanh điều hướng
        WindowInsetsController insetsController = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsController = getWindow().getInsetsController();
        }
        if (insetsController != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        } else {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        gridMenu = findViewById(R.id.gridMenu);
        GridMenuAdapter adapter = new GridMenuAdapter(this, tools, icons);
        gridMenu.setAdapter(adapter);

        gridMenu.setOnItemClickListener(this::onItemClick);
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                startActivity(new Intent(this, SelectEquationDegreeActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, LengthConverterActivity.class));
                break;
            case 5:
                startActivity(new Intent(this, BmiBmrCalculatorActivity.class));
                break;
        }

    }
}

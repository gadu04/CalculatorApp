package com.example.appcal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;

public class SelectEquationDegreeActivity extends AppCompatActivity {

    private Button btnDegree2, btnDegree3, btnDegree4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Màu thanh điều hướng đen
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }

// Nếu API >= 30 (Android 11) ➔ bỏ light nav bar (icon sẽ thành trắng)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            );
        } else {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_equation_degree);

        btnDegree2 = findViewById(R.id.btn_degree_2);
        btnDegree3 = findViewById(R.id.btn_degree_3);
        btnDegree4 = findViewById(R.id.btn_degree_4);

        btnDegree2.setOnClickListener(v -> openEquationSolver(1));
        btnDegree3.setOnClickListener(v -> openEquationSolver(2));
        btnDegree4.setOnClickListener(v -> openEquationSolver(3));
    }

    private void openEquationSolver(int degree) {
        Intent intent = new Intent(this, EquationSolverActivity.class);
        intent.putExtra("degree", degree);
        startActivity(intent);
    }
}

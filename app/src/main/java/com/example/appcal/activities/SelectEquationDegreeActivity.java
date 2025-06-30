package com.example.appcal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;

import java.util.Objects;

public class SelectEquationDegreeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(getColor(R.color.black));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Objects.requireNonNull(getWindow().getInsetsController()).setSystemBarsAppearance(
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

        Button btnDegree2 = findViewById(R.id.btn_degree_2);
        Button btnDegree3 = findViewById(R.id.btn_degree_3);
        Button btnDegree4 = findViewById(R.id.btn_degree_4);

        btnDegree2.setOnClickListener(v -> openEquationSolver(2));
        btnDegree3.setOnClickListener(v -> openEquationSolver(3));
        btnDegree4.setOnClickListener(v -> openEquationSolver(4));
    }

    private void openEquationSolver(int degree) {
        Intent intent = new Intent(this, EquationSolverActivity.class);
        intent.putExtra("degree", degree);
        startActivity(intent);
    }
}

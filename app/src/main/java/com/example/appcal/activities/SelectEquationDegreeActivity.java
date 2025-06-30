package com.example.appcal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;

public class SelectEquationDegreeActivity extends AppCompatActivity {

    private Button btnDegree2, btnDegree3, btnDegree4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

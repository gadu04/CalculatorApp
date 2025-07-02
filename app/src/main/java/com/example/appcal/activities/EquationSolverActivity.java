package com.example.appcal.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;
import com.example.appcal.utils.EquationSolver;

import java.util.Objects;

public class EquationSolverActivity extends AppCompatActivity {

    private EditText inputA, inputB, inputC, inputD, inputE;
    private TextView resultView;
    private int degree = 2; // Mặc định bậc 2


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Điều chỉnh thanh điều hướng
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
        setContentView(R.layout.activity_equation_solver);

        // Khởi tạo view
        inputA = findViewById(R.id.inputA);
        inputB = findViewById(R.id.inputB);
        inputC = findViewById(R.id.inputC);
        inputD = findViewById(R.id.inputD);
        inputE = findViewById(R.id.inputE);
        Button solveButton = findViewById(R.id.solveButton);
        resultView = findViewById(R.id.resultView);
        Spinner spinnerDegree = findViewById(R.id.spinnerDegree);

        // Lấy degree từ Intent
        degree = getIntent().getIntExtra("degree", 2);

        // Cấu hình spinner chọn bậc
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Bậc 2", "Bậc 3", "Bậc 4"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDegree.setAdapter(adapter);

        // Set spinner theo degree nhận được
        spinnerDegree.setSelection(degree - 2);

        // Lắng nghe khi chọn bậc
        spinnerDegree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                degree = position + 2; // cập nhật degree
                inputD.setVisibility(degree >= 3 ? View.VISIBLE : View.GONE);
                inputE.setVisibility(degree == 4 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Hiển thị đúng input theo degree ban đầu
        inputD.setVisibility(degree >= 3 ? View.VISIBLE : View.GONE);
        inputE.setVisibility(degree == 4 ? View.VISIBLE : View.GONE);

        // Xử lý nút giải
        solveButton.setOnClickListener(v -> {
            if (!validateInputs()) return;

            try {
                double a = Double.parseDouble(inputA.getText().toString());
                double b = Double.parseDouble(inputB.getText().toString());
                double c = Double.parseDouble(inputC.getText().toString());
                double d = (degree >= 3) ? Double.parseDouble(inputD.getText().toString()) : 0;
                double e = (degree == 4) ? Double.parseDouble(inputE.getText().toString()) : 0;

                double[] coeffs;
                if (degree == 2) {
                    coeffs = new double[]{c, b, a};
                } else if (degree == 3) {
                    coeffs = new double[]{d, c, b, a};
                } else {
                    coeffs = new double[]{e, d, c, b, a};
                }

                String result = EquationSolver.solveEquation(coeffs);
                resultView.setText(result);
            } catch (NumberFormatException ex) {
                resultView.setText("Lỗi định dạng số: " + ex.getMessage());
            }
        });
    }


    // Kiểm tra hợp lệ input
    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(inputA.getText())) {
            inputA.setError("Không được để trống hệ số a");
            isValid = false;
        }
        if (TextUtils.isEmpty(inputB.getText())) {
            inputB.setError("Không được để trống hệ số b");
            isValid = false;
        }
        if (TextUtils.isEmpty(inputC.getText())) {
            inputC.setError("Không được để trống hệ số c");
            isValid = false;
        }
        if (degree >= 3 && TextUtils.isEmpty(inputD.getText())) {
            inputD.setError("Không được để trống hệ số d");
            isValid = false;
        }
        if (degree == 4 && TextUtils.isEmpty(inputE.getText())) {
            inputE.setError("Không được để trống hệ số e");
            isValid = false;
        }

        return isValid;
    }
}

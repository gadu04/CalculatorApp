package com.example.appcal.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.Objects;

public class IntegralCalculatorActivity extends AppCompatActivity {

    private EditText editExpression, editLower, editUpper;
    private TextView txtResult;

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
        setContentView(R.layout.activity_integral_calculator);

        editExpression = findViewById(R.id.editExpression);
        editLower = findViewById(R.id.editLower);
        editUpper = findViewById(R.id.editUpper);
        txtResult = findViewById(R.id.txtResult);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(v -> calculateIntegral());
    }

    private void calculateIntegral() {
        String functionStr = editExpression.getText().toString().trim();
        String lowerStr = editLower.getText().toString().trim();
        String upperStr = editUpper.getText().toString().trim();

        if (functionStr.isEmpty() || lowerStr.isEmpty() || upperStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double a = Double.parseDouble(lowerStr);
            double b = Double.parseDouble(upperStr);

            Expression expr = new ExpressionBuilder(functionStr)
                    .variable("x")
                    .build();

            int n = 10000;
            double h = (b - a) / n;
            double sum = 0.5 * (eval(expr, a) + eval(expr, b));

            for (int i = 1; i < n; i++) {
                double xi = a + i * h;
                sum += eval(expr, xi);
            }

            double result = sum * h;

            txtResult.setText("Kết quả: " + formatNumber(result));

        } catch (Exception e) {
            txtResult.setText("Lỗi: " + e.getMessage());
        }
    }

    private double eval(Expression expr, double x) {
        return expr.setVariable("x", x).evaluate();
    }

    private String formatNumber(double number) {
        double rounded = Math.round(number * 100000.0) / 100000.0;
        if (Math.abs(rounded - Math.round(rounded)) < 1e-9) {
            return String.valueOf((long) Math.round(rounded));
        } else {
            return String.valueOf(rounded)
                    .replaceAll("0+$", "")
                    .replaceAll("\\.$", "");
        }
    }
}
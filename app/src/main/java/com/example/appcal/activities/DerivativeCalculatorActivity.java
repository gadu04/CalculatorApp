package com.example.appcal.activities;

import android.annotation.SuppressLint;
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

public class DerivativeCalculatorActivity extends AppCompatActivity {

    private EditText editExpression, editXValue;
    private TextView txtResult;

    @SuppressLint("MissingInflatedId")
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
        setContentView(R.layout.activity_derivative_calculator);

        editExpression = findViewById(R.id.editExpression);
        editXValue = findViewById(R.id.editXValue);
        txtResult = findViewById(R.id.txtResult);
        Button btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnClickListener(v -> calculateNumericalDerivative());
    }

    private void calculateNumericalDerivative() {
        String exprStr = editExpression.getText().toString().trim();
        String xStr = editXValue.getText().toString().trim();

        if (exprStr.isEmpty() || xStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập biểu thức và giá trị x", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double x = Double.parseDouble(xStr);
            double h = 1e-5;

            // f(x+h)
            Expression expr1 = new ExpressionBuilder(exprStr)
                    .variable("x")
                    .build()
                    .setVariable("x", x + h);

            // f(x-h)
            Expression expr2 = new ExpressionBuilder(exprStr)
                    .variable("x")
                    .build()
                    .setVariable("x", x - h);

            double derivative = (expr1.evaluate() - expr2.evaluate()) / (2 * h);

            String formattedX = formatNumber(x);
            String formattedResult = formatNumber(derivative);

            txtResult.setText("Đạo hàm tại x = " + formattedX + " là: " + formattedResult);

        } catch (Exception e) {
            txtResult.setText("Lỗi: " + e.getMessage());
        }
    }

    // Hàm định dạng: nếu là số nguyên → không có .0, nếu là số thực → 5 chữ số sau dấu phẩy
    private String formatNumber(double number) {
        if (Math.abs(number - Math.round(number)) < 1e-9) {
            return String.valueOf((long) Math.round(number));
        } else {
            return String.format("%.5f", number);
        }
    }
}
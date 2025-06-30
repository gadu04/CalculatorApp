package com.example.appcal.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.appcal.R;

import java.util.Locale;
import java.util.Objects;

public class BmiBmrCalculatorActivity extends AppCompatActivity {

    private EditText inputHeight, inputWeight, inputAge;
    private RadioGroup genderGroup;
    private TextView resultBmi, resultBmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
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
        setContentView(R.layout.activity_bmi_bmr_calculator);

        // Ánh xạ view
        inputHeight = findViewById(R.id.inputHeight);
        inputWeight = findViewById(R.id.inputWeight);
        inputAge = findViewById(R.id.inputAge);
        genderGroup = findViewById(R.id.genderGroup);
        resultBmi = findViewById(R.id.resultBmi);
        resultBmr = findViewById(R.id.resultBmr);
        Button calculateBtn = findViewById(R.id.calculateBtn);

        calculateBtn.setOnClickListener(v -> {
            calculate();
            hideKeyboard();
        });
    }

    private void calculate() {
        String heightStr = inputHeight.getText().toString();
        String weightStr = inputWeight.getText().toString();
        String ageStr = inputAge.getText().toString();

        if (heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        double height = Double.parseDouble(heightStr);
        double weight = Double.parseDouble(weightStr);
        int age = Integer.parseInt(ageStr);

        // BMI calculation
        double bmi = weight / Math.pow(height / 100, 2);
        String bmiStatus;
        if (bmi < 18.5) bmiStatus = "Gầy";
        else if (bmi < 25) bmiStatus = "Bình thường";
        else if (bmi < 30) bmiStatus = "Thừa cân";
        else bmiStatus = "Béo phì";

        resultBmi.setText(String.format(Locale.US, "BMI: %.1f (%s)", bmi, bmiStatus));

        // BMR calculation
        int genderId = genderGroup.getCheckedRadioButtonId();
        double bmr;
        if (genderId == R.id.radioMale) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        resultBmr.setText(String.format(Locale.US, "BMR: %.0f kcal/day", bmr));
    }

    // Ẩn bàn phím sau khi tính toán
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

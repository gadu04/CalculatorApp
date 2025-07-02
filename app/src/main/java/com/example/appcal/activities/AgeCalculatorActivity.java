package com.example.appcal.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Objects;

public class AgeCalculatorActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnFromDate, btnToDate;
    private LocalDate fromDate, toDate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_calculator);

        tvResult = findViewById(R.id.tvAgeResult);
        btnFromDate = findViewById(R.id.btnPickDate); // Đổi tên lại cho hợp lý hơn
        btnToDate = findViewById(R.id.btnToDate);     // Thêm button chọn ngày kết thúc

        btnFromDate.setOnClickListener(v -> pickDate(true));
        btnToDate.setOnClickListener(v -> pickDate(false));

        // Dark Navigation Bar
        getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Objects.requireNonNull(getWindow().getInsetsController()).setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            );
        } else {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    private void pickDate(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (DatePicker view, int y, int m, int d) -> {
            LocalDate selectedDate = LocalDate.of(y, m + 1, d);
            if (isFromDate) {
                fromDate = selectedDate;
                btnFromDate.setText("From: " + selectedDate.toString());
            } else {
                toDate = selectedDate;
                btnToDate.setText("To: " + selectedDate.toString());
            }

            if (fromDate != null && toDate != null) {
                showDateDifference();
            }
        }, year, month, day);

        dialog.show();
    }

    private void showDateDifference() {
        if (fromDate.isAfter(toDate)) {
            tvResult.setText("From date must be before To date.");
            return;
        }

        Period period = Period.between(fromDate, toDate);
        long totalDays = ChronoUnit.DAYS.between(fromDate, toDate);
        long weeks = totalDays / 7;
        long days = totalDays % 7;

        String result = "From: " + fromDate.toString() + "\n"
                + "To: " + toDate.toString() + "\n"
                + "Difference:\n"
                + period.getYears() + " years, "
                + period.getMonths() + " months, "
                + period.getDays() + " days\n"
                + "(" + weeks + " weeks and " + days + " days total)";

        tvResult.setText(result);
    }
}
package com.example.appcal.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class LengthConverterActivity extends AppCompatActivity {

    private EditText inputValue;
    private Spinner spinnerFrom, spinnerTo;
    private TextView resultText;

    private final HashMap<String, Double> lengthToMeter = new HashMap<String, Double>() {{
        put("mm", 0.001);
        put("cm", 0.01);
        put("m", 1.0);
        put("km", 1000.0);
        put("inch", 0.0254);
        put("foot", 0.3048);
        put("yard", 0.9144);
        put("mile", 1609.34);
    }};

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
        setContentView(R.layout.activity_length_converter);

        inputValue = findViewById(R.id.inputLength);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        resultText = findViewById(R.id.resultText);
        Button convertButton = findViewById(R.id.convertButton);

        String[] units = {"mm", "cm", "m", "km", "inch", "foot", "yard", "mile"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        convertButton.setOnClickListener(v -> convertLength());
    }

    private void convertLength() {
        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();
        String input = inputValue.getText().toString();

        if (input.isEmpty()) {
            resultText.setText(R.string.enter_value_warning);
            return;
        }

        double value = Double.parseDouble(input);
        double valueInMeter = value * lengthToMeter.get(fromUnit);
        double result = valueInMeter / lengthToMeter.get(toUnit);

        String formattedValue = (value == Math.floor(value))
                ? String.format(Locale.US, "%.0f", value)
                : String.format(Locale.US, "%.4f", value);

        String formattedResult = (result == Math.floor(result))
                ? String.format(Locale.US, "%.0f", result)
                : String.format(Locale.US, "%.4f", result);

        String output = String.format(Locale.US,
                "%s %s = %s %s",
                formattedValue, fromUnit, formattedResult, toUnit);

        resultText.setText(output);
    }
}

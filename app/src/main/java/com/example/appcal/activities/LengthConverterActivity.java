package com.example.appcal.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;

import java.util.HashMap;

public class LengthConverterActivity extends AppCompatActivity {

    private EditText inputValue;
    private Spinner spinnerFrom, spinnerTo;
    private TextView resultText;

    // Bảng quy đổi sang mét
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
            resultText.setText("Vui lòng nhập giá trị.");
            return;
        }

        double value = Double.parseDouble(input);
        double valueInMeter = value * lengthToMeter.get(fromUnit);
        double result = valueInMeter / lengthToMeter.get(toUnit);

        resultText.setText(String.format("%.4f %s = %.4f %s",
                value, fromUnit, result, toUnit));
    }
}

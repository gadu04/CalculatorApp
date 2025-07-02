package com.example.appcal.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.appcal.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyConverterActivity extends AppCompatActivity {

    private Spinner spinnerFrom, spinnerTo;
    private EditText amountInput;
    private TextView resultText;
    private OkHttpClient client;

    private final String[] currencies = {
            "USD - United States",
            "EUR - Eurozone",
            "JPY - Japan",
            "GBP - United Kingdom",
            "AUD - Australia",
            "CAD - Canada",
            "CHF - Switzerland",
            "CNY - China",
            "SEK - Sweden",
            "NZD - New Zealand",
            "MXN - Mexico",
            "SGD - Singapore",
            "HKD - Hong Kong",
            "NOK - Norway",
            "KRW - South Korea",
            "TRY - Turkey",
            "RUB - Russia",
            "INR - India",
            "BRL - Brazil",
            "ZAR - South Africa",
            "VND - Vietnam",
            "IDR - Indonesia",
            "MYR - Malaysia",
            "PHP - Philippines",
            "THB - Thailand",
            "DKK - Denmark",
            "PLN - Poland",
            "HUF - Hungary",
            "CZK - Czech Republic",
            "ILS - Israel"
    };


    @SuppressLint("MissingInflatedId")
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
        setContentView(R.layout.activity_currency_converter);

        // Ánh xạ view
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        amountInput = findViewById(R.id.amountInput);
        resultText = findViewById(R.id.resultText);
        Button convertButton = findViewById(R.id.convertButton);

        client = new OkHttpClient();

        // Đổ dữ liệu spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        convertButton.setOnClickListener(v -> convertCurrency());
    }

    @SuppressLint("SetTextI18n")
    private void convertCurrency() {
        String fromFull = spinnerFrom.getSelectedItem().toString();
        String toFull = spinnerTo.getSelectedItem().toString();

        String from = fromFull.split(" - ")[0]; // 👉 Tách mã từ "USD - United States"
        String to = toFull.split(" - ")[0];

        String inputText = amountInput.getText().toString().trim();

        if (inputText.isEmpty()) {
            resultText.setText("Vui lòng nhập số tiền.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(inputText);
        } catch (NumberFormatException e) {
            resultText.setText("Số tiền không hợp lệ.");
            return;
        }

        String API_KEY = "28b6b5e8b2d8a414f3a2d374";
        String url = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + from;

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> resultText.setText("Lỗi kết nối: " + e.getMessage()));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String jsonData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject rates = jsonObject.getJSONObject("conversion_rates");
                    double rate = rates.getDouble(to);
                    double converted = amount * rate;

                    runOnUiThread(() -> {
                        String result = String.format(Locale.US,
                                "%.2f %s = %.2f %s", amount, from, converted, to);
                        resultText.setText(result);
                    });

                } catch (JSONException e) {
                    runOnUiThread(() -> resultText.setText("Lỗi dữ liệu JSON."));
                }
            }
        });
    }

}

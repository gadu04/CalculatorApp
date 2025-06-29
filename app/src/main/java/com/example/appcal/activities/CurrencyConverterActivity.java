package com.example.appcal.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

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

    // Danh sách một số đồng tiền phổ biến
    private final String[] currencies = {"USD", "EUR", "JPY", "VND", "GBP", "AUD", "KRW", "CAD"};

    // Thay YOUR_API_KEY bằng API key thực của bạn
    private final String API_KEY = "28b6b5e8b2d8a414f3a2d374";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private void convertCurrency() {
        String from = spinnerFrom.getSelectedItem().toString();
        String to = spinnerTo.getSelectedItem().toString();
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

        // Tạo URL API
        String url = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + from;

        // Gửi request bất đồng bộ
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> resultText.setText("Lỗi kết nối: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String jsonData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);

                    // Lưu ý: ExchangeRate-API dùng "conversion_rates"
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

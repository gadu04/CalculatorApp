package com.example.appcal.activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appcal.R;
import com.example.appcal.utils.CalculatorEngine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BasicCalculatorActivity extends AppCompatActivity {
    private boolean isDegreeMode = true; // true = DEG, false = RAD
    private Button buttonC;
    private TextView inputText, resultText;
    private PopupWindow popupWindow;

    // ✅ Thêm các biến trạng thái
    private String lastResult = "";
    private boolean justEvaluated = false;
    private ImageButton buttonHistory;
    private ArrayList<String> historyList = new ArrayList<>();
    private SharedPreferences preferences;
    private static final String HISTORY_KEY = "calc_history";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_calculator);

        inputText = findViewById(R.id.txtInput);
        resultText = findViewById(R.id.txtResult);

        buttonHistory = findViewById(R.id.buttonHistory);
        preferences = getSharedPreferences("calculator_prefs", MODE_PRIVATE);

        loadHistory();

        buttonHistory.setOnClickListener(v -> showHistoryDialog());


        // Gán nút popup khoa học
        findViewById(R.id.buttonMore).setOnClickListener(v -> showScientificPopup());

        // Gán nút tính toán =
        findViewById(R.id.buttonEquals).setOnClickListener(v -> evaluateExpression());

        // Gán sự kiện cho toàn bộ button thường
        setupBasicButtons();

        buttonC = findViewById(R.id.buttonC);
        buttonC.setText("DEG"); // mặc định

        buttonC.setOnClickListener(v -> {
            isDegreeMode = !isDegreeMode;
            buttonC.setText(isDegreeMode ? "DEG" : "RAD");
        });


        // Màu thanh điều hướng
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            );
        } else {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    private void setupBasicButtons() {
        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.button00, R.id.buttonDot,
                R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide,
                R.id.buttonPercent,
                R.id.buttonOpenParentheses, R.id.buttonParentheses
        };

        for (int id : buttonIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(v -> appendToInput(((Button) v).getText().toString()));
            }
        }

        // Xử lý nút xóa 1 ký tự
        findViewById(R.id.buttonBackspace).setOnClickListener(v -> {
            String current = inputText.getText().toString();
            if (!current.isEmpty()) {
                inputText.setText(current.substring(0, current.length() - 1));
                justEvaluated = false;
            }
        });

        // Xóa toàn bộ
        findViewById(R.id.buttonClear).setOnClickListener(v -> {
            inputText.setText("");
            resultText.setText("");
            lastResult = "";
            justEvaluated = false;
        });

        // Xóa input (giữ kết quả)
        findViewById(R.id.buttonC).setOnClickListener(v -> {
            inputText.setText("");
            justEvaluated = false;
        });
    }

    private String convertUserInput(String input) {
        return input
                .replace("π", String.valueOf(Math.PI))
                .replace("e", String.valueOf(Math.E))
                .replace("φ", "1.6180339887")
                .replace("sin⁻¹", "asin")
                .replace("cos⁻¹", "acos")
                .replace("tan⁻¹", "atan")
                .replace("x²", "^2")
                .replace("xⁿ", "^")
                .replace("mod", "mod")
                .replace("abs", "abs");
    }


    private void appendToInput(String value) {
        if (justEvaluated) {
            if (value.matches("[+\\-×÷^%]")) {
                inputText.setText(lastResult + value);
            } else {
                inputText.setText(value);
            }
            justEvaluated = false;
        } else {
            inputText.append(value);
        }

        // ✅ Tự động cuộn ngang đến cuối
        HorizontalScrollView scrollView = findViewById(R.id.inputScrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_RIGHT));
    }


    private void evaluateExpression() {
        String exp = inputText.getText().toString();
        if (exp.isEmpty()) return;

        String result = CalculatorEngine.evaluate(exp, isDegreeMode);
        resultText.setText(result);
        lastResult = result;
        justEvaluated = true;

        // ✅ Chỉ lưu nếu KHÔNG phải "ERROR"
        if (!exp.isEmpty() && !result.isEmpty() && !result.equalsIgnoreCase("ERROR")) {
            String entry = exp + " = " + result;
            historyList.add(0, entry); // thêm vào đầu danh sách
            saveHistory(); // lưu lại
        }
    }





    private void insertFunction(String functionText) {
        switch (functionText) {
            case "x²":
                inputText.append("^2");
                break;
            case "xⁿ":
                inputText.append("^");
                break;
            case "sin":
            case "cos":
            case "tan":
            case "sin⁻¹":
            case "cos⁻¹":
            case "tan⁻¹":
            case "log":
            case "ln":
            case "log₂":
            case "√":
            case "∛":
            case "abs":
                inputText.append(functionText + "(");
                break;
            default:
                inputText.append(functionText);
                break;
        }
    }




    private void showScientificPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.layout_popup_scientific, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        popupWindow = new PopupWindow(
                popupView,
                (int) (screenWidth * 0.9),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setFocusable(true);
        popupWindow.setElevation(10f);
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

        View rootView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        int[] buttonIds = new int[]{
                R.id.btnPi, R.id.btnE, R.id.btnPhi,
                R.id.btnLog, R.id.btnLn, R.id.btnLog2,
                R.id.btnSqrt, R.id.btnCbrt, R.id.btnAbs,
                R.id.btnSin, R.id.btnCos, R.id.btnTan,
                R.id.btnSinInv, R.id.btnCosInv, R.id.btnTanInv,
                R.id.btnXSquared, R.id.btnXPowerN, R.id.btnmod
        };

        for (int id : buttonIds) {
            Button sciBtn = popupView.findViewById(id);
            if (sciBtn != null) {
                sciBtn.setOnClickListener(v -> {
                    insertFunction(((Button) v).getText().toString());
                    popupWindow.dismiss(); // ✅ Tự động đóng popup
                });
            }
        }


        Button btnCancel = popupView.findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> popupWindow.dismiss());
        }
    }

    private void saveHistory() {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        editor.putString(HISTORY_KEY, json);
        editor.apply();
    }

    private void loadHistory() {
        String json = preferences.getString(HISTORY_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            historyList = gson.fromJson(json, type);
        }
    }

    private void showHistoryDialog() {
        if (historyList.isEmpty()) {
            Toast.makeText(this, "Chưa có lịch sử", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lịch sử phép tính");

        String[] items = historyList.toArray(new String[0]);
        builder.setItems(items, null);

        builder.setPositiveButton("Đóng", null);
        builder.setNegativeButton("Xoá hết", (dialog, which) -> {
            historyList.clear();
            saveHistory();
            Toast.makeText(this, "Đã xoá lịch sử", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }


}

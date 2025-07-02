package com.example.appcal.activities;

import android.annotation.SuppressLint;
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
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.appcal.R;
import com.example.appcal.utils.CalculatorEngine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class BasicCalculatorActivity extends AppCompatActivity {
    private boolean isDegreeMode = true; // true = DEG, false = RAD
    private Button buttonC;
    private TextView resultText;
    private EditText inputText;
    private PopupWindow popupWindow;
    private String lastResult = "";
    private boolean justEvaluated = false;
    private ArrayList<String> historyList = new ArrayList<>();
    private SharedPreferences preferences;
    private static final String HISTORY_KEY = "calc_history";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_calculator);

        inputText = findViewById(R.id.txtInput);
        resultText = findViewById(R.id.txtResult);

        // Ẩn bàn phím ảo khi focus
        inputText.setShowSoftInputOnFocus(false);

        // Ngăn trỏ vào giữa các hàm toán học
        inputText.setOnClickListener(v -> sanitizeCursorPosition());
        inputText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) sanitizeCursorPosition();
        });

        // Lịch sử
        preferences = getSharedPreferences("calculator_prefs", MODE_PRIVATE);
        loadHistory();

        // Nút lịch sử
        ImageButton buttonHistory = findViewById(R.id.buttonHistory);
        if (buttonHistory != null) {
            buttonHistory.setOnClickListener(v -> showHistoryDialog());
        }

        // Nút mở menu khoa học (popup)
        View buttonMore = findViewById(R.id.buttonMore);
        if (buttonMore != null) {
            buttonMore.setOnClickListener(v -> showScientificPopup());
        }

        // Nút "=" để tính toán
        View buttonEquals = findViewById(R.id.buttonEquals);
        if (buttonEquals != null) {
            buttonEquals.setOnClickListener(v -> evaluateExpression());
        }

        // Gán sự kiện cho toàn bộ nút số và phép toán cơ bản
        setupBasicButtons();

        // Nút DEG/RAD
        buttonC = findViewById(R.id.buttonC);
        if (buttonC != null) {
            buttonC.setText("DEG");
            buttonC.setOnClickListener(v -> {
                isDegreeMode = !isDegreeMode;
                buttonC.setText(isDegreeMode ? "DEG" : "RAD");
            });
        }
    }

    private void sanitizeCursorPosition() {
        String text = inputText.getText().toString();
        int cursorPos = inputText.getSelectionStart();

        String[] functions = {
                "sin(", "cos(", "tan(","log(", "ln(", "log₂(",
                "√(", "∛(", "abs(", "sin⁻¹(", "cos⁻¹(", "tan⁻¹(", "mod"
        };

        for (String func : functions) {
            int index = text.indexOf(func);
            while (index != -1) {
                int funcStart = index;
                int funcEnd = index + func.length();

                if (cursorPos > funcStart && cursorPos < funcEnd) {
                    // Nếu trỏ vào giữa hàm, thì:
                    // Nếu lệch gần đầu hàm → đẩy về đầu hàm
                    // Nếu lệch gần cuối hàm → đẩy về sau dấu (
                    int mid = (funcStart + funcEnd) / 2;
                    if (cursorPos - funcStart <= func.length() / 2) {
                        inputText.setSelection(funcStart);
                    } else {
                        inputText.setSelection(funcEnd);
                    }
                    return;
                }

                // Tìm tiếp các hàm còn lại trong chuỗi
                index = text.indexOf(func, funcEnd);
            }
        }
    }

    private void setupBasicButtons() {
        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.buttonDot, R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply, R.id.buttonDivide,
                R.id.buttonOpenParentheses, R.id.buttonParentheses
        };

        for (int id : buttonIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(v -> appendToInput(((Button) v).getText().toString()));
            }
        }

        // Optional buttons (có thể không tồn tại trong landscape)
        int[] optionalButtonIds = {
                R.id.button00,
                R.id.buttonPercent
        };

        for (int id : optionalButtonIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(v -> appendToInput(((Button) v).getText().toString()));
            }
        }

        View buttonBackspace = findViewById(R.id.buttonBackspace);
        if (buttonBackspace != null) {
            buttonBackspace.setOnClickListener(v -> {
                int cursorPos = inputText.getSelectionStart();
                String current = inputText.getText().toString();

                if (cursorPos == 0 || current.isEmpty()) return;

                // Xử lý xóa cụm hàm (nếu cần)
                String[] functions = {
                        "sin(", "cos(", "tan(","log(", "ln(", "log₂(",
                        "√(", "∛(", "abs(", "sin⁻¹(", "cos⁻¹(", "tan⁻¹(", "mod"
                };

                for (String func : functions) {
                    int funcLen = func.length();
                    if (cursorPos >= funcLen && current.substring(cursorPos - funcLen, cursorPos).equals(func)) {
                        inputText.getText().delete(cursorPos - funcLen, cursorPos);
                        justEvaluated = false;
                        return;
                    }
                }
                // Mặc định xóa 1 ký tự trước con trỏ
                inputText.getText().delete(cursorPos - 1, cursorPos);
                justEvaluated = false;
            });
        }

        View buttonClear = findViewById(R.id.buttonClear);
        if (buttonClear != null) {
            buttonClear.setOnClickListener(v -> {
                inputText.setText("");
                resultText.setText("");
                lastResult = "";
                justEvaluated = false;
            });
        }

        buttonC = findViewById(R.id.buttonC);
        if (buttonC != null) {
            buttonC.setText("DEG"); // mặc định
            buttonC.setOnClickListener(v -> {
                isDegreeMode = !isDegreeMode;
                buttonC.setText(isDegreeMode ? "DEG" : "RAD");
            });
        }
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


    @SuppressLint("SetTextI18n")
    private void appendToInput(String value) {
        int cursorPos = inputText.getSelectionStart(); // Lấy vị trí con trỏ hiện tại
        String oldText = inputText.getText().toString();

        // Chèn giá trị mới vào vị trí con trỏ
        String newText = oldText.substring(0, cursorPos) + value + oldText.substring(cursorPos);

        inputText.setText(newText);
        inputText.setSelection(cursorPos + value.length()); // Di chuyển con trỏ sau khi chèn

        justEvaluated = false;

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

        if (!result.isEmpty() && !result.equalsIgnoreCase("ERROR")) {
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
                    popupWindow.dismiss();
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
        builder.setItems(items, (dialog, which) -> {
            String selectedEntry = items[which];
            int equalIndex = selectedEntry.lastIndexOf("=");
            if (equalIndex != -1) {
                String expression = selectedEntry.substring(0, equalIndex).trim();
                inputText.setText(expression);
                inputText.setSelection(expression.length()); // đặt con trỏ về cuối
                justEvaluated = false;
            }
        });

        builder.setPositiveButton("Đóng", null);
        builder.setNegativeButton("Xoá hết", (dialog, which) -> {
            historyList.clear();
            saveHistory();
            Toast.makeText(this, "Đã xoá lịch sử", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }


}

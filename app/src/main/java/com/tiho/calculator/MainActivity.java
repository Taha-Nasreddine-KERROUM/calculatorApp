package com.tiho.calculator;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.Locale;
import android.content.res.Configuration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView resultText, solutionText;
    MaterialButton buttonC, openBracket, closeBracket,
            buttonAddition, buttonSubtraction, buttonMultiplication, buttonDivision, buttonEquals,
            button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,
            buttonDot, buttonAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLocale("en");

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resultText = findViewById(R.id.result_text);
        solutionText = findViewById(R.id.solution_text);

        assignId(buttonC, R.id.button_c);
        assignId(openBracket, R.id.open_bracket);
        assignId(closeBracket, R.id.close_bracket);
        assignId(buttonAddition, R.id.button_addition);
        assignId(buttonSubtraction, R.id.button_subtraction);
        assignId(buttonMultiplication, R.id.button_multiplication);
        assignId(buttonDivision, R.id.button_divide);
        assignId(buttonEquals, R.id.button_equals);
        assignId(buttonAC, R.id.button_ac);
        assignId(buttonDot, R.id.button_dot);
        assignId(button0, R.id.button_0);
        assignId(button1, R.id.button_1);
        assignId(button2, R.id.button_2);
        assignId(button3, R.id.button_3);
        assignId(button4, R.id.button_4);
        assignId(button5, R.id.button_5);
        assignId(button6, R.id.button_6);
        assignId(button7, R.id.button_7);
        assignId(button8, R.id.button_8);
        assignId(button9, R.id.button_9);

        if (savedInstanceState != null) {
            String savedSolution = savedInstanceState.getString("solution", "0");
            String savedResult = savedInstanceState.getString("result", "0");
            solutionText.setText(savedSolution);
            resultText.setText(savedResult);
        }
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("solution", solutionText.getText().toString());
        outState.putString("result", resultText.getText().toString());
    }

    void assignId(MaterialButton btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String dataToCalculate = solutionText.getText().toString();
        String operators = "+-*/)";
        String operations = "+-*/";
        boolean oprOverOper = false;

        // Initialize or maintain bracket and dot counts
        int bracketCount = countBrackets(dataToCalculate, buttonText.charAt(0));
        int dotCount = countDots(dataToCalculate);

        if (buttonText.equals("AC")) {
            solutionText.setText("0");
            resultText.setText("0");
            return;
        }

        if (buttonText.equals("=")) {
            solutionText.setText(resultText.getText());
            return;
        }

        if (buttonText.equals("C")) {
            if (dataToCalculate.length() == 1) {
                dataToCalculate = "0";
            } else {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
            }
        } else {
            if (dataToCalculate.equals("0") && !buttonText.equals(".")) {
                // Prevent removing initial 0 unless a decimal point follows
                dataToCalculate = "";
            }

            // Handle multiplication when inserting parentheses or digits after a closing parenthesis
            if ((buttonText.equals("(") && !dataToCalculate.isEmpty() && Character.isDigit(dataToCalculate.charAt(dataToCalculate.length() - 1))) ||
                    (!operators.contains(buttonText) && !dataToCalculate.isEmpty() && bracketCount >= 0 && dataToCalculate.charAt(dataToCalculate.length() - 1) == ')')) {
                dataToCalculate += "*";
            }

            // Handle case of () ensuring no empty parentheses
            else if (buttonText.equals(")") && !dataToCalculate.isEmpty() && dataToCalculate.charAt(dataToCalculate.length() - 1) == '(') {
                dataToCalculate += "0"; // Add 0 between empty parentheses
            }

            // Prevent invalid multiple dots like 0..
            else if (buttonText.equals(".") && dotCount > 0) {
                return; // Ignore additional dots in a valid decimal number
            }

            // Handle case where a number ends with a dot and the next input isn't a digit
            else if (!Character.isDigit(buttonText.charAt(0)) && !buttonText.equals(".") && !dataToCalculate.isEmpty() && dataToCalculate.charAt(dataToCalculate.length() - 1) == '.') {
                dataToCalculate += "0"; // Prevents invalid numbers like "0."
            }

            if (!dataToCalculate.isEmpty()
                    && operations.contains(buttonText)
                    && operations.contains("" + dataToCalculate.charAt(dataToCalculate.length() - 1))) {
                oprOverOper = true;
            } else if (operations.contains(buttonText) && dataToCalculate.isEmpty() && !buttonText.equals("-")){
                oprOverOper = true;
            }else {
                oprOverOper = false;
            }


            // Append the buttonText only if valid bracket and dot counts
            if (bracketCount >= 0 && dotCount <= 1 && !oprOverOper) {
                dataToCalculate += buttonText;
            }
        }

        solutionText.setText(dataToCalculate);

        if (!dataToCalculate.isEmpty()) {
            String finalResult = getResult(dataToCalculate);
            if (!finalResult.equals("Error")) {
                resultText.setText(finalResult);
            }
        } else {
            solutionText.setText("0");
            resultText.setText("0");
        }
    }

    // Helper function to count brackets
    private int countBrackets(String data, char currentChar) {
        int count = 0;
        for (char c : data.toCharArray()) {
            if (c == '(') count++;
            else if (c == ')') count--;
        }
        if (currentChar == '(') count++;
        else if (currentChar == ')') count--;
        return count;
    }

    // Helper function to count dots
    private int countDots(String data) {
        int count = 0;
        for (char c : data.toCharArray()) {
            if (c == '.') count++;
            else if (!Character.isDigit(c)) count = 0;
        }
        return count;
    }


    String getResult(String data){
        try {
            Context cx = Context.enter();
            cx.setOptimizationLevel(-1);
            Scriptable scriptable = cx.initStandardObjects();
            String finalResult = cx.evaluateString(scriptable, data, "Javascript", 1, null).toString();
            if(finalResult.endsWith(".0")){
                finalResult = finalResult.replace(".0", "");
            }
            return finalResult;
        } catch (Exception e) {
            return "Error";
        }
    }
}
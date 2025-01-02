package yuzkov.oleksandr.nure;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText firstNumberField;
    private EditText secondNumberField;
    private TextView resultView;
    private TextView operationView;
    private Double firstOperand = null;
    private Double secondOperand = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        firstNumberField = findViewById(R.id.firstNumberField);
        secondNumberField = findViewById(R.id.secondNumberField);
        resultView = findViewById(R.id.resultView);
        operationView = findViewById(R.id.operationView);
    }

    private void setupListeners() {
        setNumberListeners();
        setOperationListeners();
    }

    private void setNumberListeners() {
        int[] numberButtonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.decimalButton
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(view -> handleNumberClick(((Button) view).getText().toString()));
        }
    }

    private void setOperationListeners() {
        findViewById(R.id.addButton).setOnClickListener(view -> handleOperationClick("+"));
        findViewById(R.id.subtractButton).setOnClickListener(view -> handleOperationClick("-"));
        findViewById(R.id.multiplyButton).setOnClickListener(view -> handleOperationClick("*"));
        findViewById(R.id.divideButton).setOnClickListener(view -> handleOperationClick("/"));
        findViewById(R.id.equalsButton).setOnClickListener(view -> handleOperationClick("="));
    }

    private void handleNumberClick(String number) {
        if (firstNumberField.isFocused()) {
            firstNumberField.append(number);
        } else if (secondNumberField.isFocused()) {
            secondNumberField.append(number);
        }
    }

    private void handleOperationClick(String operation) {
        String firstNumber = firstNumberField.getText().toString();
        String secondNumber = secondNumberField.getText().toString();

        if (!operation.equals("=")) {
            operationView.setText(operation);
        }

        if (!TextUtils.isEmpty(firstNumber)) {
            try {
                firstOperand = Double.parseDouble(firstNumber.replace(',', '.'));
            } catch (NumberFormatException e) {
                firstOperand = null;
            }
        }

        if (!TextUtils.isEmpty(secondNumber)) {
            try {
                secondOperand = Double.parseDouble(secondNumber.replace(',', '.'));
            } catch (NumberFormatException e) {
                secondOperand = null;
            }
        }

        if (operation.equals("=")) {
            if ((firstOperand == null || secondOperand == null) || TextUtils.isEmpty(firstNumber) || TextUtils.isEmpty(secondNumber)) {
                resultView.setText("Невірний ввід");
                return;
            }

            performCalculation(firstOperand, secondOperand);
        }
    }

    private void performCalculation(Double firstOperand, Double secondOperand) {
        Double result = null;
        String currentOperation = operationView.getText().toString();

        switch (currentOperation) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "*":
                result = firstOperand * secondOperand;
                break;
            case "/":
                if (secondOperand != 0) {
                    result = firstOperand / secondOperand;
                } else {
                    resultView.setText("Помилка: Ділення на нуль");
                    return;
                }
                break;
        }

        if (result != null) {
            resultView.setText(String.valueOf(result).replace('.', ','));
        }

        firstNumberField.setText("");
        secondNumberField.setText("");
        operationView.setText("");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("FIRST_NUMBER", firstNumberField.getText().toString());
        outState.putString("SECOND_NUMBER", secondNumberField.getText().toString());

        outState.putString("OPERATION", operationView.getText().toString());

        outState.putString("RESULT", resultView.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String firstNumber = savedInstanceState.getString("FIRST_NUMBER");
        String secondNumber = savedInstanceState.getString("SECOND_NUMBER");

        String operation = savedInstanceState.getString("OPERATION");

        String result = savedInstanceState.getString("RESULT");

        firstNumberField.setText(firstNumber);
        secondNumberField.setText(secondNumber);
        operationView.setText(operation);
        resultView.setText(result);

        if (!TextUtils.isEmpty(firstNumber)) {
            try {
                firstOperand = Double.parseDouble(firstNumber.replace(',', '.'));
            } catch (NumberFormatException e) {
                firstOperand = null;
            }
        }

        if (!TextUtils.isEmpty(secondNumber)) {
            try {
                secondOperand = Double.parseDouble(secondNumber.replace(',', '.'));
            } catch (NumberFormatException e) {
                secondOperand = null;
            }
        }
    }

}

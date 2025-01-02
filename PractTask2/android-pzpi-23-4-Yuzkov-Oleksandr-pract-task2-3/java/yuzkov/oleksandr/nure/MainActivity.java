package yuzkov.oleksandr.nure;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String COUNTER_KEY = "counter";
    private static final String TIME_ELAPSED_KEY = "timeElapsed";
    private static final String TEXT_KEY = "text";

    private TextView counterText, timerText;
    private EditText editText;
    private int counter = 0;
    private long timeElapsed = 0;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        counterText = findViewById(R.id.counterText);
        timerText = findViewById(R.id.timerText);
        editText = findViewById(R.id.editText);

        if (savedInstanceState != null) {
            timeElapsed = savedInstanceState.getLong(TIME_ELAPSED_KEY, 0);
            counter = savedInstanceState.getInt(COUNTER_KEY, 0);
            editText.setText(savedInstanceState.getString(TEXT_KEY, ""));
        }

        updateUI();

        findViewById(R.id.incrementButton).setOnClickListener(v -> {
            counter++;
            updateUI();
        });

        findViewById(R.id.openSecondActivity).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("counter", counter);
            startActivityForResult(intent, 1);
        });

        findViewById(R.id.finishActivity).setOnClickListener(v -> finish());

        if (timer == null) {
            timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeElapsed++;
                    updateUI();
                }

                @Override
                public void onFinish() {
                }
            };
        }
    }

    private void updateUI() {
        counterText.setText("Counter: " + counter);
        timerText.setText("Timer: " + timeElapsed + "s");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (timer != null) {
            timer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

        outState.putLong(TIME_ELAPSED_KEY, timeElapsed);
        outState.putInt(COUNTER_KEY, counter);
        outState.putString(TEXT_KEY, editText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");

        timeElapsed = savedInstanceState.getLong(TIME_ELAPSED_KEY, 0);
        counter = savedInstanceState.getInt(COUNTER_KEY, 0);
        editText.setText(savedInstanceState.getString(TEXT_KEY, ""));
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            counter = data.getIntExtra("counter", counter);
            updateUI();
        }
    }
}

package yuzkov.oleksandr.nure;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ActivityLifecycle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity created");
        setContentView(R.layout.activity_main);
    }

    public void onChangeTextClick(View view) {
        TextView textView = findViewById(R.id.textView);
        textView.setText("Текст змінено!");
    }

    public void onShowToastClick(View view) {
        Toast.makeText(this, "Toast", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Activity started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Activity resumed");
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "Activity paused");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "Activity stopped");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "Activity destroyed");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "Activity restarted");
    }
}

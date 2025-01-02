package yuzkov.oleksandr.nure;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);

        button1.setOnClickListener(view ->
                Toast.makeText(this, "Кнопка 1 натиснута", Toast.LENGTH_SHORT).show());

        button2.setOnClickListener(view ->
                Toast.makeText(this, "Кнопка 2 натиснута", Toast.LENGTH_SHORT).show());
    }
}
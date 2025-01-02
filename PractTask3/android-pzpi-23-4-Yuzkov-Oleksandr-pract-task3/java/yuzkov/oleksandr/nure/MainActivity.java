package yuzkov.oleksandr.nure;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private MyAdapter adapter;
    private List<String> data = new ArrayList<>();
    private TextView statusField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button showAlertDialog = findViewById(R.id.showAlertDialog);
        Button showDatePickerDialog = findViewById(R.id.showDatePickerDialog);
        Button showCustomDialog = findViewById(R.id.showCustomDialog);
        Button startTaskButton = findViewById(R.id.startTaskButton);
        statusField = findViewById(R.id.statusField);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(data);
        recyclerView.setAdapter(adapter);

        showAlertDialog.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Приклад AlertDialog")
                    .setMessage("Це приклад AlertDialog з кнопками.")
                    .setPositiveButton("OK", (dialog, which) -> addItemToList("Обрано: OK"))
                    .setNegativeButton("Скасувати", (dialog, which) -> addItemToList("Обрано: Скасувати"))
                    .show();
        });

        showDatePickerDialog.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(MainActivity.this, (view, year, month, dayOfMonth) -> {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                addItemToList("Обрана дата: " + date);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        showCustomDialog.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_custom, null);

            EditText inputField = dialogView.findViewById(R.id.customDialogInput);

            new AlertDialog.Builder(MainActivity.this)
                    .setView(dialogView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String input = inputField.getText().toString();
                        addItemToList("Введено: " + input);
                    })
                    .setNegativeButton("Скасувати", null)
                    .show();
        });

        startTaskButton.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Виконання завдання");
            progressDialog.setMessage("Будь ласка, зачекайте...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new Thread(() -> {
                for (int i = 1; i <= 5; i++) {
                    int progress = i * 20;

                    handler.post(() -> {
                        progressDialog.setMessage("Прогрес: " + progress + "%");
                        statusField.setText("Прогрес: " + progress + "%");
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                handler.post(() -> {
                    progressDialog.dismiss();
                    addItemToList("Завдання завершено.");
                });
            }).start();
        });

        loadDataInBackground();
    }

    private void addItemToList(String item) {
        data.add(0, item);
        adapter.notifyDataSetChanged();
    }

    private void loadDataInBackground() {
        new Thread(() -> {
            List<String> initialData = Arrays.asList(
                    "Елемент 1", "Елемент 2", "Елемент 3", "Елемент 4", "Елемент 5",
                    "Елемент 6", "Елемент 7", "Елемент 8", "Елемент 9", "Елемент 10"
            );

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                data.addAll(initialData);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private final List<String> data;

        MyAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}

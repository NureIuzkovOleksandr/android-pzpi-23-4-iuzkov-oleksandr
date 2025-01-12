package yuzkov.oleksandr.nure;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import android.util.Log;

import yuzkov.oleksandr.nure.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private UserListAdapter userAdapter;
    private ActivityMainBinding mainBinding;
    private StorageHelper storageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        storageHelper = new StorageHelper(this);

        setupUserList();
        initializeClickListeners();

        loadUserInfoFromPrefs();
    }

    private void setupUserList() {
        userAdapter = new UserListAdapter();
        mainBinding.userList.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.userList.setAdapter(userAdapter);
        refreshUserList();
    }

    private void initializeClickListeners() {
        mainBinding.saveButton.setOnClickListener(v -> saveConfigToFile());
        mainBinding.loadButton.setOnClickListener(v -> showConfigFromFile());
        mainBinding.addUserButton.setOnClickListener(v -> createNewUser());
    }

    private void saveConfigToFile() {
        String config = mainBinding.inputField.getText().toString().trim();
        if (!config.isEmpty()) {
            writeDataToFile(config);
            mainBinding.inputField.setText("");
            showToast("Config saved successfully!");
        } else {
            showToast("Please enter config data!");
        }
    }

    private void showConfigFromFile() {
        mainBinding.outputText.setText(readDataFromFile());
    }

    private void createNewUser() {
        String username = mainBinding.nameInputField.getText().toString().trim();
        String userageStr = mainBinding.ageInputField.getText().toString().trim();

        if (username.isEmpty() || userageStr.isEmpty()) {
            showToast("Name and Age are required");
            return;
        }

        try {
            int userage = Integer.parseInt(userageStr);
            User newUser = new User(null, username, userage);
            storageHelper.addUserToDatabase(newUser);

            storageHelper.saveUserDataToPrefs(username, userage);

            refreshUserList();

            mainBinding.nameInputField.setText("");
            mainBinding.ageInputField.setText("");

            showToast("User added successfully!");
        } catch (NumberFormatException e) {
            showToast("Age must be a valid number");
        }
    }

    private void loadUserInfoFromPrefs() {
        String username = storageHelper.getUserNameFromPrefs();
        int userage = storageHelper.getUserAgeFromPrefs();

        if (!username.isEmpty() && userage != 0) {
            mainBinding.nameInputField.setText(username);
            mainBinding.ageInputField.setText(String.valueOf(userage));
        }
    }

    private void refreshUserList() {
        List<User> users = storageHelper.retrieveUsersFromDb();
        userAdapter.updateUserData(users);
    }

    private void writeDataToFile(String content) {
        try (OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("config.txt", MODE_PRIVATE))) {
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error while saving config");
        }
    }

    private String readDataFromFile() {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("config.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error while reading config");
        }
        return result.toString().trim();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
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
    }
}

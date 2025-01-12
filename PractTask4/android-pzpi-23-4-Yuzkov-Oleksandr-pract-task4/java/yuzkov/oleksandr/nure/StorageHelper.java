package yuzkov.oleksandr.nure;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StorageHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "application.db";
    private static final int DB_VERSION = 1;

    private static final String USER_TABLE = "user";
    private static final String USER_ID = "id";
    private static final String USER_NAME = "name";
    private static final String USER_AGE = "age";

    private static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" +
                    USER_ID + " INTEGER PRIMARY KEY, " +
                    USER_NAME + " TEXT, " +
                    USER_AGE + " INTEGER)";

    private static final String SHARED_PREFS = "prefs";
    private static final String PREF_NAME_KEY = "username";
    private static final String PREF_AGE_KEY = "userage";

    private final SharedPreferences prefs;
    private final SQLiteDatabase db;

    public StorageHelper(@Nullable Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
        this.prefs = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        this.db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void saveUserDataToPrefs(String username, int userage) {
        prefs.edit()
                .putString(PREF_NAME_KEY, username)
                .putInt(PREF_AGE_KEY, userage)
                .apply();
    }

    public String getUserNameFromPrefs() {
        return prefs.getString(PREF_NAME_KEY, "");
    }

    public int getUserAgeFromPrefs() {
        return prefs.getInt(PREF_AGE_KEY, 0);
    }

    public void addUserToDatabase(User user) {
        ContentValues values = new ContentValues();
        values.put(USER_NAME, user.getUserName());
        values.put(USER_AGE, user.getUserAge());

        long rowId = db.insert(USER_TABLE, null, values);
        if (rowId == -1) {
            Log.e("StorageHelper", "Failed to add user");
        }
    }

    @SuppressLint("Range")
    public List<User> retrieveUsersFromDb() {
        List<User> users = new ArrayList<>();

        try (Cursor cursor = db.query(
                USER_TABLE, null, null, null, null, null, null)) {

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(USER_ID));
                String name = cursor.getString(cursor.getColumnIndex(USER_NAME));
                int age = cursor.getInt(cursor.getColumnIndex(USER_AGE));

                users.add(new User(id, name, age));
            }

        } catch (Exception e) {
            Log.e("StorageHelper", "Error retrieving users", e);
        }

        return users;
    }
}

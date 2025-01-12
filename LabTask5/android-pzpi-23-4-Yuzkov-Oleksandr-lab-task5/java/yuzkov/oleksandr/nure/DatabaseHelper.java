package yuzkov.oleksandr.nure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notes.db";
    private static final int DB_VERSION = 1;

    public static final String NOTES_TABLE = "notes";
    public static final String NOTE_ID = "id";
    public static final String NOTE_TITLE = "title";
    public static final String NOTE_DESCRIPTION = "description";
    public static final String NOTE_IMPORTANCE = "importance";
    public static final String NOTE_DATE_TIME = "dateTime";
    public static final String NOTE_IMAGE_URI = "imageUri";

    public DatabaseHelper(Context appContext) {
        super(appContext, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder tableQuery = new StringBuilder();
        tableQuery.append("CREATE TABLE ").append(NOTES_TABLE).append(" (")
                .append(NOTE_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(NOTE_TITLE).append(" TEXT, ")
                .append(NOTE_DESCRIPTION).append(" TEXT, ")
                .append(NOTE_IMPORTANCE).append(" INTEGER, ")
                .append(NOTE_DATE_TIME).append(" TEXT, ")
                .append(NOTE_IMAGE_URI).append(" TEXT)");

        database.execSQL(tableQuery.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
        onCreate(database);
    }

    public long insertNote(String noteTitle, String noteDescription, int noteImportance, String noteDateTime, String noteImageUri) {
        try (SQLiteDatabase writableDb = this.getWritableDatabase()) {
            ContentValues noteValues = new ContentValues();
            noteValues.put(NOTE_TITLE, noteTitle);
            noteValues.put(NOTE_DESCRIPTION, noteDescription);
            noteValues.put(NOTE_IMPORTANCE, noteImportance);
            noteValues.put(NOTE_DATE_TIME, noteDateTime);
            noteValues.put(NOTE_IMAGE_URI, noteImageUri);

            return writableDb.insert(NOTES_TABLE, null, noteValues);
        }
    }

    public List<Note> fetchAllNotes() {
        List<Note> noteList = new ArrayList<>();
        SQLiteDatabase readableDb = this.getReadableDatabase();
        Cursor dbCursor = null;

        try {
            dbCursor = readableDb.rawQuery("SELECT * FROM " + NOTES_TABLE, null);

            if (dbCursor.moveToFirst()) {
                do {
                    long noteId = dbCursor.getLong(dbCursor.getColumnIndexOrThrow(NOTE_ID));
                    String title = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NOTE_TITLE));
                    String description = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NOTE_DESCRIPTION));
                    int importance = dbCursor.getInt(dbCursor.getColumnIndexOrThrow(NOTE_IMPORTANCE));
                    String dateTime = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NOTE_DATE_TIME));
                    String imageUriString = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NOTE_IMAGE_URI));

                    Uri imageUri = Uri.parse(imageUriString);
                    Note note = new Note(noteId, title, description, importance, dateTime, imageUri);
                    noteList.add(note);

                } while (dbCursor.moveToNext());
            }
        } finally {
            if (dbCursor != null) {
                dbCursor.close();
            }
            readableDb.close();
        }

        return noteList;
    }

    public int modifyNote(long noteId, String newTitle, String newDescription, int newImportance, String newDateTime, String newImageUri) {
        try (SQLiteDatabase writableDb = this.getWritableDatabase()) {
            ContentValues updatedValues = new ContentValues();
            updatedValues.put(NOTE_TITLE, newTitle);
            updatedValues.put(NOTE_DESCRIPTION, newDescription);
            updatedValues.put(NOTE_IMPORTANCE, newImportance);
            updatedValues.put(NOTE_DATE_TIME, newDateTime);
            updatedValues.put(NOTE_IMAGE_URI, newImageUri);

            return writableDb.update(NOTES_TABLE, updatedValues, NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        }
    }

    public void removeNote(long noteId) {
        try (SQLiteDatabase writableDb = this.getWritableDatabase()) {
            writableDb.delete(NOTES_TABLE, NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        }
    }

    public void removeAllNotes() {
        try (SQLiteDatabase writableDb = this.getWritableDatabase()) {
            writableDb.delete(NOTES_TABLE, null, null);
        }
    }
}

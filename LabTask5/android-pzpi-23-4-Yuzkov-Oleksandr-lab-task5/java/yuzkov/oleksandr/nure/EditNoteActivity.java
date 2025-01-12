package yuzkov.oleksandr.nure;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class EditNoteActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1;

    private EditText editTitle, editDescription;
    private Spinner prioritySpinner;
    private Button btnSave, btnSelectImage, btnDelete;
    private ImageView imgPreview;
    private Uri currentImageUri;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        editTitle = findViewById(R.id.titleField);
        editDescription = findViewById(R.id.descriptionField);
        prioritySpinner = findViewById(R.id.importanceLevelSpinner);
        btnSave = findViewById(R.id.updateNoteButton);
        btnSelectImage = findViewById(R.id.selectImageButton);
        btnDelete = findViewById(R.id.deleteNoteButton);
        imgPreview = findViewById(R.id.noteImageView);

        long noteId = getIntent().getLongExtra("note_id", 0);

        String noteTitle = getIntent().getStringExtra("note_title");
        String noteDescription = getIntent().getStringExtra("note_description");
        int notePriority = getIntent().getIntExtra("note_importance", 0);
        String noteDateTime = getIntent().getStringExtra("note_date_time");
        String noteImagePath = getIntent().getStringExtra("note_image_uri");
        Uri noteImageUri = (noteImagePath != null) ? Uri.parse(noteImagePath) : null;

        currentNote = new Note(noteId, noteTitle, noteDescription, notePriority, noteDateTime, noteImageUri);

        editTitle.setText(currentNote.getTitle());
        editDescription.setText(currentNote.getDescription());
        imgPreview.setImageURI(currentNote.getImageUri());
        btnSelectImage.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> saveChanges());
        btnDelete.setOnClickListener(v -> removeNote());

        String[] priorities = {"Low", "Medium", "High"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(spinnerAdapter);
        prioritySpinner.setSelection(currentNote.getImportance());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private String getFormattedDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return formatter.format(new Date());
    }

    private Uri saveImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            String fileName = "image_" + UUID.randomUUID().toString() + ".jpg";
            OutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.close();
            inputStream.close();

            return Uri.fromFile(getFileStreamPath(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            currentImageUri = saveImage(data.getData());
            if (currentImageUri != null) {
                imgPreview.setImageURI(currentImageUri);
            } else {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveChanges() {
        String updatedTitle = editTitle.getText().toString().trim();
        String updatedDescription = editDescription.getText().toString().trim();
        int selectedPriority = prioritySpinner.getSelectedItemPosition();
        String updatedDateTime = getFormattedDateTime();
        String updatedImagePath = (currentImageUri != null) ? currentImageUri.toString() : "";

        if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            int affectedRows = dbHelper.modifyNote(currentNote.getId(), updatedTitle, updatedDescription, selectedPriority, updatedDateTime, updatedImagePath);

            if (affectedRows > 0) {
                Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating note", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeNote() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.removeNote(currentNote.getId());

        Toast.makeText(this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}

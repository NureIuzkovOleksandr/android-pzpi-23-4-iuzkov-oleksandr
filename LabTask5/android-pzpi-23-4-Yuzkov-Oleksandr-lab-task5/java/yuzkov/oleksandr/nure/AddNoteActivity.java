package yuzkov.oleksandr.nure;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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

public class AddNoteActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText noteTitle, noteDescription;
    private Spinner noteImportance;
    private Button imageSelectButton, noteSaveButton;
    private ImageView selectedImageView;
    private Uri noteImageUri;

    @Override
    protected void onCreate(Bundle instanceState) {
        super.onCreate(instanceState);
        setContentView(R.layout.activity_add_note);

        noteTitle = findViewById(R.id.titleText);
        noteDescription = findViewById(R.id.descriptionText);
        noteImportance = findViewById(R.id.importanceSpinner);
        noteSaveButton = findViewById(R.id.saveButton);
        imageSelectButton = findViewById(R.id.selectImageButton);
        selectedImageView = findViewById(R.id.noteImageView);

        imageSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery();
            }
        });

        noteSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNoteToDatabase();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private Uri storeImageLocally(Uri imageSourceUri) {
        try {
            InputStream sourceStream = getContentResolver().openInputStream(imageSourceUri);
            Bitmap imageBitmap = BitmapFactory.decodeStream(sourceStream);

            String uniqueFileName = "note_image_" + UUID.randomUUID().toString() + ".jpg";
            OutputStream fileOutput = openFileOutput(uniqueFileName, Context.MODE_PRIVATE);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutput);

            fileOutput.close();
            sourceStream.close();

            return Uri.fromFile(getFileStreamPath(uniqueFileName));
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int code, int result, @Nullable Intent galleryData) {
        super.onActivityResult(code, result, galleryData);
        if (code == PICK_IMAGE_REQUEST && result == RESULT_OK && galleryData != null && galleryData.getData() != null) {
            noteImageUri = storeImageLocally(galleryData.getData());
            if (noteImageUri != null) {
                selectedImageView.setImageURI(noteImageUri);
            } else {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String generateCurrentDateTime() {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return dateTimeFormat.format(new Date());
    }

    private void addNoteToDatabase() {
        String noteTitleText = noteTitle.getText().toString().trim();
        String noteDescriptionText = noteDescription.getText().toString().trim();
        int noteImportanceLevel = noteImportance.getSelectedItemPosition();
        String creationDateTime = generateCurrentDateTime();
        String storedImageUri = (noteImageUri != null) ? noteImageUri.toString() : "";

        if (noteTitleText.isEmpty() || noteDescriptionText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            long insertedNoteId = databaseHelper.insertNote(noteTitleText, noteDescriptionText, noteImportanceLevel, creationDateTime, storedImageUri);

            if (insertedNoteId != -1) {
                Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error adding note", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

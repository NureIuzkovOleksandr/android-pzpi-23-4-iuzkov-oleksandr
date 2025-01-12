package yuzkov.oleksandr.nure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteInteractionListener {

    private static final int IMAGE_PICK_REQUEST_CODE = 1;
    private RecyclerView recyclerViewNotes;
    private NoteAdapter adapterNotes;
    private final ArrayList<Note> notesAll = new ArrayList<>();
    private final ArrayList<Note> notesFiltered = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private Uri uriSelectedImage;
    private boolean sortByImportanceAsc = true;
    private boolean sortByTitleAsc = true;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String FONT_SIZE_KEY = "font_size";
    private boolean isFontLarge = false;
    private static final String THEME_KEY = "app_theme";
    private static final int THEME_LIGHT = 1;
    private static final int THEME_DARK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        setupRecyclerView();
        setupAddNoteButton();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isFontLarge = preferences.getBoolean(FONT_SIZE_KEY, false);

        applyFontSize();
    }

    private void initializeComponents() {
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        adapterNotes = new NoteAdapter(this, notesFiltered, this);
        recyclerViewNotes.setAdapter(adapterNotes);
    }

    private void setupAddNoteButton() {
        findViewById(R.id.addNoteButton).setOnClickListener(v -> navigateToAddNoteActivity());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_find_note);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterNotes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotes(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sort_notes) {
            toggleSortByImportance();
            return true;
        } else if (item.getItemId() == R.id.action_sort_alphabetically) {
            toggleSortByTitle();
            return true;
        }
        if (item.getItemId() == R.id.action_change_font_size) {
            isFontLarge = !isFontLarge;

            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FONT_SIZE_KEY, isFontLarge);
            editor.apply();

            applyFontSize();
            return true;
        }
        if (item.getItemId() == R.id.action_toggle_theme) {
            toggleTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void applyTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int theme = preferences.getInt(THEME_KEY, THEME_LIGHT);

        if (theme == THEME_LIGHT) {
            setTheme(R.style.Theme_App_Light);
        } else {
            setTheme(R.style.Theme_App_Dark);
        }
    }
    private void toggleTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentTheme = preferences.getInt(THEME_KEY, THEME_LIGHT);

        int newTheme = (currentTheme == THEME_LIGHT) ? THEME_DARK : THEME_LIGHT;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(THEME_KEY, newTheme);
        editor.apply();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    private void applyFontSize() {
        float fontSize = isFontLarge ? 24f : 16f;

        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                TextView title = holder.itemView.findViewById(R.id.title);
                TextView description = holder.itemView.findViewById(R.id.description);
                if (title != null) title.setTextSize(fontSize);
                if (description != null) description.setTextSize(fontSize);
            }
        }
    }
    private void filterNotes(String query) {
        notesFiltered.clear();
        String lowerCaseQuery = query.toLowerCase();
        for (Note note : notesAll) {
            if (note.getDescription().toLowerCase().contains(lowerCaseQuery) ||
                    note.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                notesFiltered.add(note);
            }
        }
        adapterNotes.notifyDataSetChanged();
    }

    private void toggleSortByImportance() {
        List<Note> targetList = notesFiltered.isEmpty() ? notesAll : notesFiltered;
        if (sortByImportanceAsc) {
            Collections.sort(targetList, (n1, n2) -> Integer.compare(n1.getImportance(), n2.getImportance()));
            showToast("Sorted by importance (ascending)");
        } else {
            Collections.sort(targetList, (n1, n2) -> Integer.compare(n2.getImportance(), n1.getImportance()));
            showToast("Sorted by importance (descending)");
        }
        sortByImportanceAsc = !sortByImportanceAsc;
        adapterNotes.notifyDataSetChanged();
    }

    private void toggleSortByTitle() {
        List<Note> targetList = notesFiltered.isEmpty() ? notesAll : notesFiltered;
        if (sortByTitleAsc) {
            Collections.sort(targetList, (n1, n2) -> n1.getTitle().compareTo(n2.getTitle()));
        } else {
            Collections.sort(targetList, (n1, n2) -> n2.getTitle().compareTo(n1.getTitle()));
        }
        sortByTitleAsc = !sortByTitleAsc;
        adapterNotes.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshNotesList();
    }

    private void refreshNotesList() {
        notesAll.clear();
        notesAll.addAll(dbHelper.fetchAllNotes());
        notesFiltered.clear();
        notesFiltered.addAll(notesAll);
        adapterNotes.notifyDataSetChanged();
    }

    private void navigateToAddNoteActivity() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNoteSelect(Note note) {
        navigateToEditNoteActivity(note);
    }

    @Override
    public void onEdit(Note note, int position) {
        navigateToEditNoteActivity(note);
    }

    private void navigateToEditNoteActivity(Note note) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("note_id", note.getId());
        intent.putExtra("note_title", note.getTitle());
        intent.putExtra("note_description", note.getDescription());
        intent.putExtra("note_importance", note.getImportance());
        intent.putExtra("note_date_time", note.getDateTime());
        intent.putExtra("note_image_uri", note.getImageUri() != null ? note.getImageUri().toString() : null);
        startActivity(intent);
    }

    @Override
    public void onDelete(Note note, int position) {
        dbHelper.removeNote(note.getId());
        notesAll.remove(note);
        notesFiltered.remove(note);
        adapterNotes.notifyDataSetChanged();
        showToast("Note deleted successfully");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uriSelectedImage = data.getData();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

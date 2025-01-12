package yuzkov.oleksandr.nure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final Context appContext;
    private final List<Note> notesList;
    private final OnNoteInteractionListener onNoteInteractionListener;

    public NoteAdapter(Context context, List<Note> notes, OnNoteInteractionListener listener) {
        this.appContext = context;
        this.notesList = notes;
        this.onNoteInteractionListener = listener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(appContext).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note currentNote = notesList.get(position);
        holder.bindData(currentNote);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    private void showCustomContextMenu(View view, Note note, int position) {
        androidx.appcompat.widget.PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(appContext, view);
        popupMenu.inflate(R.menu.context_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_modify_note) {
                onNoteInteractionListener.onEdit(note, position);
                return true;
            } else if (item.getItemId() == R.id.action_remove_note) {
                onNoteInteractionListener.onDelete(note, position);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private int fetchImportanceIcon(int importanceLevel) {
        switch (importanceLevel) {
            case 0:
                return R.drawable.low_importance;
            case 1:
                return R.drawable.medium_importance;
            case 2:
                return R.drawable.high_importance;
            default:
                return R.drawable.low_importance;
        }
    }

    public interface OnNoteInteractionListener {
        void onNoteSelect(Note note);
        void onEdit(Note note, int position);
        void onDelete(Note note, int position);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView noteTitle;
        private final TextView noteDescription;
        private final TextView noteDateTime;
        private final ImageView noteImage;
        private final ImageView importanceIndicator;

        public NoteViewHolder(View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.title);
            noteDescription = itemView.findViewById(R.id.description);
            noteDateTime = itemView.findViewById(R.id.dateTime);
            noteImage = itemView.findViewById(R.id.image);
            importanceIndicator = itemView.findViewById(R.id.importance);
        }

        public void bindData(Note note) {
            noteTitle.setText(note.getTitle());
            noteDescription.setText(note.getDescription());
            noteDateTime.setText(note.getDateTime());
            noteImage.setImageURI(note.getImageUri());
            importanceIndicator.setImageResource(fetchImportanceIcon(note.getImportance()));

            itemView.setOnClickListener(v -> onNoteInteractionListener.onNoteSelect(note));

            itemView.setOnLongClickListener(v -> {
                showCustomContextMenu(itemView, note, getAdapterPosition());
                return true;
            });
        }
    }
}

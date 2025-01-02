package yuzkov.oleksandr.nure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final List<String> dataList;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public CustomAdapter(List<String> dataList, OnItemClickListener onItemClickListener) {
        this.dataList = dataList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemTextView);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getAdapterPosition()));
        }

        void bind(String data) {
            textView.setText(data);
        }
    }
}

package yuzkov.oleksandr.nure;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import yuzkov.oleksandr.nure.databinding.ListUserBinding;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private List<User> userList = new ArrayList<>();

    public void updateUserData(List<User> newUsers) {
        if (newUsers != null && !newUsers.isEmpty()) {
            userList.clear();
            userList.addAll(newUsers);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListUserBinding userBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_user,
                parent,
                false
        );
        return new ViewHolder(userBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.binding.setUsername(user.getUserName());
        holder.binding.setUserAge(String.valueOf(user.getUserAge()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ListUserBinding binding;

        public ViewHolder(@NonNull ListUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

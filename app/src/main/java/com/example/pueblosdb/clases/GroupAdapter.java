package com.example.pueblosdb.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pueblosdb.R;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> implements RecyclerViewClickListener {
    private ArrayList<Group> mGroups;
    private Context mContext;
    private RecyclerViewClickListener listener;

    public GroupAdapter(ArrayList<Group> mGroups, Context mContext, RecyclerViewClickListener listener) {
        this.mGroups = mGroups;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_groups, parent, false);
        return new GroupViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.title.setText(mGroups.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    @Override
    public void onItemCliked(int position) {}

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public GroupViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.name_group);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            listener.onItemCliked(pos);
                        }
                    }
                }
            });
        }
    }
}

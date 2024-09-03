package com.example.pueblosdb.clases;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.example.pueblosdb.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PublicacionesAdapter extends FirebaseRecyclerAdapter<Publicacion, PublicacionesAdapter.PublicacionViewHolder> {
    
    public PublicacionesAdapter(@NonNull FirebaseRecyclerOptions<Publicacion> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position, @NonNull Publicacion model) {
        holder.title.setText(model.getTitulo());
        holder.description.setText(model.getDescripcion());
        holder.id.setText(String.valueOf(model.getId()));
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_publicaciones, parent, false);
        return new PublicacionViewHolder(view);
    }

    public class PublicacionViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        TextView description;
        TextView id;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_publication);
            image = itemView.findViewById(R.id.image_publication);
            description = itemView.findViewById(R.id.description_publication);
            id = itemView.findViewById(R.id.id_publication);
        }
    }
}
package com.example.pueblosdb.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pueblosdb.R;

import java.util.ArrayList;

public class LibroAdapter extends RecyclerView.Adapter<LibroAdapter.LibroViewHolder> implements RecyclerViewClickListener {
    private ArrayList<Libro> mLibros;
    private RecyclerViewClickListener listener;

    public LibroAdapter(ArrayList<Libro> mLibros, RecyclerViewClickListener listener){
        this.mLibros = mLibros;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LibroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_libros, parent, false);
        return new LibroViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull LibroViewHolder holder, int position) {
        Libro libro = mLibros.get(position);

        holder.title.setText(libro.getTitulo());
        holder.description.setText(libro.getDescripcion_libro());
    }


    @Override
    public int getItemCount() {
        return mLibros.size();
    }

    @Override
    public void onItemCliked(int position) {}

    public static class LibroViewHolder extends RecyclerView.ViewHolder {
        //ImageView image;  para caratula
        TextView title;
        TextView description;

        public LibroViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.nombre_libro);
            description = itemView.findViewById(R.id.description_libro);

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

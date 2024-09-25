package com.example.pueblosdb.clases;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pueblosdb.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LibroAdapter extends RecyclerView.Adapter<LibroAdapter.LibroViewHolder> implements RecyclerViewClickListener {
    private ArrayList<Libro> mLibros;
    private RecyclerViewClickListener listener;
    private static final int REQUEST_CODE = 101;


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

        holder.button_download.setOnClickListener(v -> {

            // Obtener la URL del libro (link_libro)
            String linkLibro = libro.getLink_libro();

            // Crear una instancia de FileDownloader y llamar al método downloadFile
            FileDownloader fileDownloader = new FileDownloader();

            // Extraer el nombre del archivo de la URL
            String nombreArchivo = linkLibro.substring(linkLibro.lastIndexOf('/') + 1); // Ejemplo: "1630527012000.pdf"

            if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) v.getContext(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                fileDownloader.downloadFile(v.getContext(), libro.getLink_libro(), libro.getTitulo());
                Toast.makeText(v.getContext(), "La descarga de "+libro.getTitulo()+" ha finalizado", Toast.LENGTH_LONG).show();
            }
        });
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
        Button button_download;

        public LibroViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.nombre_libro);
            description = itemView.findViewById(R.id.description_libro);
            button_download = itemView.findViewById(R.id.btnDownloadBook);

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

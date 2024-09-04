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

public class PublicacionesAdapter extends RecyclerView.Adapter<PublicacionesAdapter.PublicacionViewHolder> {
    private ArrayList<Publicacion> mPublicaciones;
    private Context mContext;

    public PublicacionesAdapter(ArrayList<Publicacion> mPublicaciones, Context mContext) {
        this.mPublicaciones = mPublicaciones;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_publicaciones, parent, false);
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {

        Glide.with(mContext).load(mPublicaciones.get(position).getLink_imagen()).into(holder.image);
        Publicacion publicacion = mPublicaciones.get(position);

        holder.title.setText(publicacion.getTitulo());
        holder.description.setText(publicacion.getDescripcion());
        holder.fecha.setText("Fecha de finalización: " + publicacion.getFecha_finalizacion().toString());
    }

    @Override
    public int getItemCount() {
        return mPublicaciones.size();
    }

    public class PublicacionViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView description;
        TextView fecha;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_publication);
            image = itemView.findViewById(R.id.image_publication);
            description = itemView.findViewById(R.id.description_publication);
            fecha = itemView.findViewById(R.id.end_date_publication);
        }
    }
}
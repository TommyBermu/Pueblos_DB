package com.example.pueblosdb.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pueblosdb.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> implements PublicacionClickListener{
    private ArrayList<Publicacion> mPublicaciones;
    private Context mContext;
    private PublicacionClickListener listener;

    public PublicacionAdapter(ArrayList<Publicacion> mPublicaciones, Context mContext, PublicacionClickListener listener) {
        this.mPublicaciones = mPublicaciones;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_publicaciones, parent, false);
        return new PublicacionViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {

        Glide.with(mContext).load(mPublicaciones.get(position).getLink_imagen()).into(holder.image);
        Publicacion publicacion = mPublicaciones.get(position);

        holder.title.setText(publicacion.getTitulo());
        holder.description.setText(publicacion.getDescripcion());
        String end_date = "Fecha de finalización: " + publicacion.getFecha_finalizacion();
        holder.fecha.setText(end_date);

    }

    @Override
    public int getItemCount() {
        return mPublicaciones.size();
    }

    @Override
    public void onItemCliked(int position) {}


    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        ImageView image;
        TextView title;
        TextView description;
        TextView fecha;

        public PublicacionViewHolder(@NonNull View itemView, PublicacionClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.title_publication);
            image = itemView.findViewById(R.id.image_publication);
            description = itemView.findViewById(R.id.description_publication);
            fecha = itemView.findViewById(R.id.end_date_publication);

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
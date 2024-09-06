package com.example.pueblosdb;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.pueblosdb.clases.Publicacion;
import com.example.pueblosdb.clases.PublicacionAdapter;
import com.example.pueblosdb.clases.PublicacionClickListener;
import com.example.pueblosdb.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment implements PublicacionClickListener {
    private SharedPreferences prefs;
    private ArrayList<Publicacion> publicaciones;
    private PublicacionAdapter adapter;

    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        publicaciones = new ArrayList<>();
        adapter = new PublicacionAdapter(publicaciones, requireActivity(), this);
        recyclerView.setAdapter(adapter);

        root.child("publications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Publicacion publicacion = dataSnapshot.getValue(Publicacion.class);
                    publicaciones.add(publicacion);
                    Collections.sort(publicaciones);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemCliked(int position) {
        String titulo = publicaciones.get(position).getTitulo();

        if (!prefs.getStringSet("inscripciones", new HashSet<String>()).contains(titulo)) { // si titulo no está en el keyset de inscripciones

            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            Button confirm = dialogView.findViewById(R.id.confirmar);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Boolean> mapa = new HashMap<>();
                    mapa.put(prefs.getString("email", "invalido"), false); //false porque, aunque se incriba, no significa que sea aceptado

                    // se agrega la inscripcion a la convocatoria en la base de datos
                    db.collection("convocatorias").document(titulo).set(mapa, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                // primero se agrega a las preferencias que ya está inscrito en la convocatoria
                                SharedPreferences.Editor prefsEditor = prefs.edit();
                                HashSet<String> set = new HashSet<>(prefs.getStringSet("inscripciones", new HashSet<String>()));
                                set.add(titulo);
                                prefsEditor.putStringSet("inscripciones", set);
                                prefsEditor.apply();

                                // luego se actualiza en el firebase un atributo del ususario que diga en qué convocatorias está inscrito
                                Map<String, HashMap<String, Boolean>> convs = new HashMap<>(); // es un mapa que guarda mapas de convocatorias (el equivalente al atributo inscipciones del User)
                                HashMap<String, Boolean> inscripciones = new HashMap<String, Boolean>();
                                inscripciones.put(titulo,false);
                                convs.put("inscripciones", inscripciones);
                                db.collection("users").document(prefs.getString("email", "invaldo")).set(convs, SetOptions.merge());

                                Toast.makeText(requireActivity(), "Te has inscrito en: " + titulo, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireActivity(), "Algo ha salido mal, intenta de nuevo más tarde", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.cancel();
                }
            });
            Button discard = dialogView.findViewById(R.id.cancelar);
            discard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        } else {
            Toast.makeText(requireActivity(), "Ya te has inscrito en: " + titulo, Toast.LENGTH_SHORT).show();
        }
    }

    public void inscribirse(){

    }
}
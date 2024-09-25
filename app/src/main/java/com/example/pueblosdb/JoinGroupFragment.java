package com.example.pueblosdb;

import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.pueblosdb.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class JoinGroupFragment extends Fragment {
    User usuario;
    ImageView imv_poster;
    TextView tv_name, tv_description;

    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("requests-groups");

    public JoinGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_join_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imv_poster = view.findViewById(R.id.group_poster);
        tv_name = view.findViewById(R.id.group_name);
        tv_description = view.findViewById(R.id.group_descrption);

        if (getArguments() != null){
            Bundle result = getArguments();
            Glide.with(requireActivity()).load(result.getString("link")).into(imv_poster);
            tv_name.setText(result.getString("nombre"));
            tv_description.setText(result.getString("descripcion"));
        }

        usuario = ((MainActivity) requireActivity()).getUsuario();

        Button btn_join = view.findViewById(R.id.join_group);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre_grupo = tv_name.getText().toString();

                if(usuario.getGrupos().containsKey(nombre_grupo)) {
                    Toast.makeText(requireActivity(), "Ya estás inscrito en este grupo", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> mapa = new HashMap<>();
                mapa.put("email", usuario.getEmail());
                mapa.put("accepted", false);//false porque, aunque se incriba, no significa que sea aceptado

                String path = root.push().getKey();
                assert path != null;

                root.child(nombre_grupo).child(path).setValue(mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = prefs.edit();
                            HashSet<String> set = new HashSet<>(prefs.getStringSet("grupos", new HashSet<>()));
                            set.add(nombre_grupo);
                            prefsEditor.putStringSet("grupos", set);
                            prefsEditor.apply();

                            //se actualiza la info en el firestore que diga en que grupos esta inscrito
                            Map<String, HashMap<String, Boolean>> inscrip = new HashMap<>(); // es un mapa que guarda mapas de grupos (el equivalente al atributo grupos del User)
                            HashMap<String, Boolean> grupo = new HashMap<>();
                            grupo.put(nombre_grupo, false);
                            inscrip.put("grupos", grupo);
                            db.collection("users").document(usuario.getEmail()).set(inscrip, SetOptions.merge());

                            //luego el hashmap del usuario mismo
                            usuario.addGrupo(nombre_grupo); //TODO sirve, pero como se crea el usuario según las preferencias, pues al cerrar sesión se borra el hashmap xd

                            Toast.makeText(requireActivity(), "Te has inscrito en: " + nombre_grupo, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireActivity(), "Algo ha salido mal: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
package com.example.pueblosdb;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pueblosdb.clases.Adapters.GroupAdapter;
import com.example.pueblosdb.clases.Adapters.RecyclerViewClickListener;
import com.example.pueblosdb.clases.Adapters.StringAdapter;
import com.example.pueblosdb.clases.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestGroupFragment extends Fragment implements RecyclerViewClickListener {
    private ArrayList<String> groups;
    private StringAdapter adapter;
    private User usuario;

    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public RequestGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usuario = ((MainActivity) requireActivity()).getUsuario();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRequestGroup);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        groups = new ArrayList<>();
        adapter = new StringAdapter(groups, this);
        recyclerView.setAdapter(adapter);

        root.child("requests-groups").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged") // solo hace que no se muestre un warning en en adapter.notifyDataSetChanged()
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    groups.add(dataSnapshot.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onItemCliked(int position) {
        Toast.makeText(requireActivity(), "Clicked: " + groups.get(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongCliked(int position) {
        Toast.makeText(requireActivity(), "Holded: " + groups.get(position), Toast.LENGTH_SHORT).show();
    }
}
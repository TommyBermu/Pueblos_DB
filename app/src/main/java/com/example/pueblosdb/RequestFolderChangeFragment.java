package com.example.pueblosdb;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pueblosdb.clases.FolderChange;
import com.example.pueblosdb.clases.Group;
import com.example.pueblosdb.clases.GroupAdapter;
import com.example.pueblosdb.clases.RecyclerViewClickListener;
import com.example.pueblosdb.clases.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RequestFolderChangeFragment extends Fragment implements RecyclerViewClickListener {
    private ArrayList<FolderChange> grupos;
    private GroupAdapter adapter;
    private User usuario;

    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public RequestFolderChangeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_folder_change, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onItemCliked(int position) {

    }
}
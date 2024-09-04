package com.example.pueblosdb;

import static android.content.Context.MODE_PRIVATE;
import static android.app.Activity.RESULT_OK;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.pueblosdb.clases.putPDF;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeFolderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeFolderFragment extends Fragment {
    SharedPreferences prefs;

    EditText editText, editText2;
    Button btn, btnDialog;
    AlertDialog dialog;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploadPDF");
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploadPDF");


    private Uri selectedPdfUri1;
    private Uri selectedPdfUri2;
    private int activeEditText = 0;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ChangeFolderFragment() {
        // Required empty public constructor
    }

    public static ChangeFolderFragment newInstance(String param1, String param2) {
        ChangeFolderFragment fragment = new ChangeFolderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_folder, container, false);

        //Llamar a la info de la persona
        prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);

        editText = view.findViewById(R.id.etSelectFile);
        btn = view.findViewById(R.id.btnSendFile);
        editText2 = view.findViewById(R.id.etSelectFileLetter);

        //Llama al formato de custom_dialog
        View alertCustomDialog = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_succes, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        //Se aplica el formato al dialog
        alertDialog.setView(alertCustomDialog);

        btnDialog = alertCustomDialog.findViewById(R.id.btnEntendido);

        dialog = alertDialog.create();

        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            }
        });

        btn.setEnabled(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeEditText = 1;
                selectPDF();
            }
        });

        editText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeEditText = 2;
                selectPDF();
            }
        });

        return view;
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "PDF FILE SELECT"));

    }


    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedPdfUri = result.getData().getData();
                    // Manejar el URI del archivo PDF seleccionado
                    if (activeEditText == 1) {
                        selectedPdfUri1 = selectedPdfUri;
                        editText.setText(selectedPdfUri.getLastPathSegment());
                    } else if (activeEditText == 2) {
                        selectedPdfUri2 = selectedPdfUri;
                        editText2.setText(selectedPdfUri.getLastPathSegment());
                    }

                    btn.setEnabled(true);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //uploadPDFFileFirebase(result.getData().getData());
                            if (selectedPdfUri1 != null && selectedPdfUri2 != null) {
                                uploadPDFFileFirebase("documentos", selectedPdfUri1, editText);
                                uploadPDFFileFirebase("cartas", selectedPdfUri2, editText2);

                                /*
                                sale cuadro de texto
                                tu solicitud ha sido enviada
                                el personal de la administración estará revisando tu solicitud
                                boton ok (volver al home)
                                */

                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.show();
                            } else {
                                Toast.makeText(getActivity(), "Please select both files before uploading.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
    );

    private void uploadPDFFileFirebase(String path, Uri data, EditText editText) {
        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Files are loading...")
                .setView(progressBar).setCancelable(false).create();
        alertDialog.show();

        String filename = editText.getText().toString();
        StorageReference reference = storageReference.child(path).child("uploadPDF"+System.currentTimeMillis()+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri uri = uriTask.getResult();

                        putPDF putPDF = new putPDF(filename, uri.toString(), prefs.getString("name", "No hay datos"), prefs.getString("surname", "No hay datos"), prefs.getString("email", "No hay datos"));
                        databaseReference.child(databaseReference.push().getKey()).setValue(putPDF);
                        Toast.makeText(getActivity(), "Files Uploaded", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        alertDialog.setMessage("Files Uploading..." +(int)progress+"%");
                    }
                });

    }
}
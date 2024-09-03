package com.example.pueblosdb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pueblosdb.clases.putPDF;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FileSolicitude extends AppCompatActivity {

    EditText editText, editText2;
    Button btn, btnDialog;
    AlertDialog dialog;

    StorageReference storageReference;
    DatabaseReference databaseReference;


    private Uri selectedPdfUri1;
    private Uri selectedPdfUri2;
    private int activeEditText = 0;

    private FirebaseAuth mAuth;
    private String Name, Surname, Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_file_solicitude);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        //Llamar a la info de la persona
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        Name =  prefs.getString("name", "No hay dato");
        Surname = prefs.getString("surname", "No hay dato");
        Email = mAuth.getCurrentUser().getEmail();
        //

        editText = findViewById(R.id.etSelectFile);
        btn = findViewById(R.id.btnSendFile);

        editText2 = findViewById(R.id.etSelectFileLetter);

        ;

        //Llama al formato de custom_dialog
        View alertCustomDialog = LayoutInflater.from(FileSolicitude.this).inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FileSolicitude.this);

        //Se aplica el formato al dialog
        alertDialog.setView(alertCustomDialog);

        btnDialog = (Button) alertCustomDialog.findViewById(R.id.btnEntendido);

        dialog = alertDialog.create();

        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Toast.makeText(FileSolicitude.this, "Solicitud enviada, redirigiendo al Home", Toast.LENGTH_SHORT).show();
                /*
                Intent intent = new Intent(FileSolicitude.this, HomeActivity.class);
                startActivity(intent);
                */
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

    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "PDF FILE SELECT"));

    }


    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
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
                                uploadPDFFileFirebase(selectedPdfUri1, editText, Name, Surname, Email);
                                uploadPDFFileFirebase(selectedPdfUri2, editText2, Name, Surname, Email);
                                ///
                                //sale cuadro de texto
                                //tu solicitud ha sido enviada
                                //el personal de la administración estará revisando tu solicitud
                                //boton ok (volver al home)

                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.show();
                            } else {
                                Toast.makeText(FileSolicitude.this, "Please select both files before uploading.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
    );

    private void uploadPDFFileFirebase(Uri data, EditText editText, String name, String surname, String email) {
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Files are loading...")
                                .setView(progressBar).setCancelable(false).create();
        alertDialog.show();

        String filename = editText.getText().toString();
        StorageReference reference = storageReference.child("uploadPDF"+System.currentTimeMillis()+".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isComplete());
                        Uri uri = uriTask.getResult();

                        putPDF putPDF = new putPDF(filename, uri.toString(), name, surname, email);
                        databaseReference.child(databaseReference.push().getKey()).setValue(putPDF);
                        Toast.makeText(FileSolicitude.this, "Files Uploaded", Toast.LENGTH_SHORT).show();
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
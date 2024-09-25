package com.example.pueblosdb.clases;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class FileDownloader {

    public void downloadFile(Context context, String url, String fileName) {
        try {
            // Obtener el DownloadManager
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            if (downloadManager != null) {
                    // Verificar si la URL es válida
                    Uri uri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle(fileName);
                    request.setDescription("Descargando archivo desde Firebase Storage...");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                    downloadManager.enqueue(request);

                    Toast.makeText(context, "Descargando archivo...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al obtener el gestor de descargas", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

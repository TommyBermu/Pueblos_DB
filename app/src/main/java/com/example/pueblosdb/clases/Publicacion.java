package com.example.pueblosdb.clases;

public class Publicacion {
    String imagen;
    String link;
    String hora_publicacion;
    String hora_finalizacion;
    String descripcion;

    public Publicacion(String imagen, String link, String hora_publicacion, String hora_finalizacion, String descripcion) {
        this.imagen = imagen;
        this.link = link;
        this.hora_publicacion = hora_publicacion;
        this.hora_finalizacion = hora_finalizacion;
        this.descripcion = descripcion;
    }
}

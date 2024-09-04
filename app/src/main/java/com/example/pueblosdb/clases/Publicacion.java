package com.example.pueblosdb.clases;

import java.util.Date;

public class Publicacion {
    private String titulo, link_imagen, descripcion;
    private Date fecha_finalizacion;

    public Publicacion() {
    }

    public Publicacion(String titulo, String link_imagen, String descripcion, Date fecha_finalizacion) {
        this.titulo = titulo;
        this.link_imagen = link_imagen;
        this.descripcion = descripcion;
        this.fecha_finalizacion = fecha_finalizacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLink_imagen() {
        return link_imagen;
    }

    public void setLink_imagen(String link_imagen) {
        this.link_imagen = link_imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha_finalizacion() {
        return fecha_finalizacion;
    }

    public void setFecha_finalizacion(Date fecha_finalizacion) {
        this.fecha_finalizacion = fecha_finalizacion;
    }
}
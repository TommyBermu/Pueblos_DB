package com.example.pueblosdb.clases;

public class putPDF {
    public String namefile;
    public String url;

    public putPDF() {
    }

    public putPDF(String namefile, String url) {  //Añadir tambien el nombre, apellido y correo
        this.namefile = namefile;
        this.url = url;
    }

    public String getNamefile() {
        return namefile;
    }

    public void setNamefile(String namefile) {
        this.namefile = namefile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

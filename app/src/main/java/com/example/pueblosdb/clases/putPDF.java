package com.example.pueblosdb.clases;

public class putPDF {
    public String namefile;
    public String url;
    public String name;
    public String surname;
    public String email;

    public putPDF() {
    }

    public putPDF(String namefile, String url, String name, String surname, String email) {  //Añadir tambien el nombre, apellido y correo
        this.namefile = namefile;
        this.url = url;
        this.name = name;
        this.surname = surname;
        this.email = email;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

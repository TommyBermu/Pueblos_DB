package com.example.pueblosdb.clases;

public class FolderChange {
    public String email, document_url, letter_url;

    public FolderChange() {
    }

    public FolderChange(String email, String document_url, String letter_url) {  //Añadir tambien el nombre, apellido y correo
        this.email = email;
        this.document_url = document_url;
        this.letter_url = letter_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocument_url() {
        return document_url;
    }

    public void setDocument_url(String document_url) {
        this.document_url = document_url;
    }

    public String getLetter_url() {
        return letter_url;
    }

    public void setLetter_url(String letter_url) {
        this.letter_url = letter_url;
    }
}

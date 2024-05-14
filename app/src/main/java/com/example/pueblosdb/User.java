package com.example.pueblosdb;

public abstract class User {

    // attributes
    private String usuario;
    private String contrasena;
    private boolean miembro;
    private String nombre;
    private String apellidos;
    //fecha de nacimiento


    public void RegistrarCuentaNueva (String usuario, String contrasena, boolean miembro, String nombre, String apellidos) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.miembro = miembro;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isMiembro() {
        return miembro;
    }

    public void setMiembro(boolean miembro) {
        this.miembro = miembro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public boolean IniciarSesion (String usuario, String contrasena) {
        if (usuario.equals(this.usuario) && contrasena.equals(this.contrasena)) {
            return true;
        }
        return false;
    }
    public abstract void EditarInformacionPersonal ();
}

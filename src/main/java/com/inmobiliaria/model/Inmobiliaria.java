package com.inmobiliaria.model;

import java.io.Serializable;

public class Inmobiliaria implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idInmobiliaria;
    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private boolean activa;

    public Inmobiliaria() {}

    public int getIdInmobiliaria() { return idInmobiliaria; }
    public void setIdInmobiliaria(int idInmobiliaria) { this.idInmobiliaria = idInmobiliaria; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}

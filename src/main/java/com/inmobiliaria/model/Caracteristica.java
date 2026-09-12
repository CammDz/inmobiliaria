package com.inmobiliaria.model;

import java.io.Serializable;

public class Caracteristica implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idCaracteristica;
    private String nombre;
    private String descripcion;

    public Caracteristica() {}

    public int getIdCaracteristica() { return idCaracteristica; }
    public void setIdCaracteristica(int idCaracteristica) { this.idCaracteristica = idCaracteristica; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}

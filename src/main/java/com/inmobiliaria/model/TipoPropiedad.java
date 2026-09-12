package com.inmobiliaria.model;

import java.io.Serializable;

public class TipoPropiedad implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idTipo;
    private String nombre;
    private String descripcion;

    public TipoPropiedad() {}

    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}

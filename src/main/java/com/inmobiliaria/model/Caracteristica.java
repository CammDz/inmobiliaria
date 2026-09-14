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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Caracteristica other = (Caracteristica) o;
        return idCaracteristica == other.idCaracteristica;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idCaracteristica);
    }
}

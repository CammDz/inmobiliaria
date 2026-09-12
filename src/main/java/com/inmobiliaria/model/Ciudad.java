package com.inmobiliaria.model;

import java.io.Serializable;

public class Ciudad implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idCiudad;
    private String nombre;
    private String departamento;

    public Ciudad() {}

    public int getIdCiudad() { return idCiudad; }
    public void setIdCiudad(int idCiudad) { this.idCiudad = idCiudad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}

package com.inmobiliaria.model;

import java.io.Serializable;
import java.util.List;

/**
 * Modelo genérico para almacenar resultados de consultas de reportes.
 */
public class Reporte implements Serializable {
    private static final long serialVersionUID = 1L;

    private String titulo;
    private List<String> encabezados;
    private List<List<String>> filas;

    public Reporte() {}

    public Reporte(String titulo, List<String> encabezados, List<List<String>> filas) {
        this.titulo = titulo;
        this.encabezados = encabezados;
        this.filas = filas;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public List<String> getEncabezados() { return encabezados; }
    public void setEncabezados(List<String> encabezados) { this.encabezados = encabezados; }
    public List<List<String>> getFilas() { return filas; }
    public void setFilas(List<List<String>> filas) { this.filas = filas; }
}
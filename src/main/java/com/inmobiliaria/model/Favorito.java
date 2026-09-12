package com.inmobiliaria.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Favorito implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idFavorito;
    private int idCliente;
    private int idPropiedad;
    private Timestamp fecha;

    // Campos de join
    private String tituloPropiedad;
    private double precioPropiedad;
    private String nombreCiudad;
    private String nombreTipo;
    private String urlImagen;

    public Favorito() {}

    public int getIdFavorito() { return idFavorito; }
    public void setIdFavorito(int idFavorito) { this.idFavorito = idFavorito; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(int idPropiedad) { this.idPropiedad = idPropiedad; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
    public String getTituloPropiedad() { return tituloPropiedad; }
    public void setTituloPropiedad(String tituloPropiedad) { this.tituloPropiedad = tituloPropiedad; }
    public double getPrecioPropiedad() { return precioPropiedad; }
    public void setPrecioPropiedad(double precioPropiedad) { this.precioPropiedad = precioPropiedad; }
    public String getNombreCiudad() { return nombreCiudad; }
    public void setNombreCiudad(String nombreCiudad) { this.nombreCiudad = nombreCiudad; }
    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}

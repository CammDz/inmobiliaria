package com.inmobiliaria.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ImagenPropiedad implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idImagen;
    private int idPropiedad;
    private String urlImagen;
    private boolean esPrincipal;
    private boolean activa;
    private Timestamp fechaRegistro;

    public ImagenPropiedad() {}

    public int getIdImagen() { return idImagen; }
    public void setIdImagen(int idImagen) { this.idImagen = idImagen; }
    public int getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(int idPropiedad) { this.idPropiedad = idPropiedad; }
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
    public boolean isEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(boolean esPrincipal) { this.esPrincipal = esPrincipal; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}

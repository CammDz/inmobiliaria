package com.inmobiliaria.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Solicitud implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idSolicitud;
    private int idCliente;
    private int idPropiedad;
    private String tipo;
    private String estado;
    private String observaciones;
    private double montoOfrecido;
    private Timestamp fechaRegistro;

    // Campos de join
    private String nombreCliente;
    private String correoCliente;
    private String tituloPropiedad;
    private String nombreInmobiliaria;

    public Solicitud() {}

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(int idPropiedad) { this.idPropiedad = idPropiedad; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public double getMontoOfrecido() { return montoOfrecido; }
    public void setMontoOfrecido(double montoOfrecido) { this.montoOfrecido = montoOfrecido; }
    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getCorreoCliente() { return correoCliente; }
    public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }
    public String getTituloPropiedad() { return tituloPropiedad; }
    public void setTituloPropiedad(String tituloPropiedad) { this.tituloPropiedad = tituloPropiedad; }
    public String getNombreInmobiliaria() { return nombreInmobiliaria; }
    public void setNombreInmobiliaria(String nombreInmobiliaria) { this.nombreInmobiliaria = nombreInmobiliaria; }
}

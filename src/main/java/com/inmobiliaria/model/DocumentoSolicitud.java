package com.inmobiliaria.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class DocumentoSolicitud implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idDocumento;
    private int idSolicitud;
    private String nombreArchivo;
    private String rutaArchivo;
    private String tipoDocumento;
    private Timestamp fechaSubida;

    public DocumentoSolicitud() {}

    public int getIdDocumento() { return idDocumento; }
    public void setIdDocumento(int idDocumento) { this.idDocumento = idDocumento; }
    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public Timestamp getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(Timestamp fechaSubida) { this.fechaSubida = fechaSubida; }
}

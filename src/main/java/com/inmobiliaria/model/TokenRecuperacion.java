package com.inmobiliaria.model;

import java.util.Date;

/**
 * Token de recuperación de contraseña.
 * Se genera con un valor aleatorio criptográficamente seguro, tiene
 * fecha de expiración y es de un solo uso.
 */
public class TokenRecuperacion {

    private Long idToken;        // BIGINT autoincremental
    private int idUsuario;       // usuario al que pertenece
    private String token;        // valor aleatorio seguro
    private Date expiraEn;       // fecha y hora de expiración
    private boolean usado;       // true si ya fue consumido
    private Date fechaCreacion;

    public Long getIdToken() {
        return idToken;
    }

    public void setIdToken(Long idToken) {
        this.idToken = idToken;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(Date expiraEn) {
        this.expiraEn = expiraEn;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
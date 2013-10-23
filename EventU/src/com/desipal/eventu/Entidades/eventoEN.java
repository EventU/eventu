package com.desipal.eventu.Entidades;

/**
 * Created by Edu on 7/10/13.
 */
import java.util.Date;
import java.util.List;

import android.graphics.drawable.Drawable;

public class eventoEN {
    private int idEvento;
    private String idCreador;
    private String nombre;
    private String descripcion;
    private double latitud;
    private double longitud;
    private int asistencia;
    private boolean validado;
    private boolean comentarios;
    private String direccion;
    private double distancia;
    private int idEdad;
    private int idCategoria;
    private String url;
    private Date fechaInicio;
    private Date fechaFin;
    private boolean todoElDia;
    private List<Drawable> imagenes;

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getIdCreador() {
        return idCreador;
    }

    public void setIdCreador(String idCreador) {
        this.idCreador = idCreador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(int asistencia) {
        this.asistencia = asistencia;
    }

    public boolean isValidado() {
        return validado;
    }

    public void setValidado(boolean validado) {
        this.validado = validado;
    }

    public boolean isComentarios() {
        return comentarios;
    }

    public void setComentarios(boolean comentarios) {
        this.comentarios = comentarios;
    }

    public int getIdEdad() {
        return idEdad;
    }

    public void setIdEdad(int idEdad) {
        this.idEdad = idEdad;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isTodoElDia() {
        return todoElDia;
    }

    public void setTodoElDia(boolean todoElDia) {
        this.todoElDia = todoElDia;
    }

    public List<Drawable> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<Drawable> imagenes) {
        this.imagenes = imagenes;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
}

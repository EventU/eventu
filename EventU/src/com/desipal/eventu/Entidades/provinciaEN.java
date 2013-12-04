package com.desipal.eventu.Entidades;

public class provinciaEN {
	private int idProvincia;
	private String nombre;
	private int idComunidad;

	public int getIdProvincia() {
		return idProvincia;
	}

	public void setIdProvincia(int idProvincia) {
		this.idProvincia = idProvincia;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getIdComunidad() {
		return idComunidad;
	}

	public void setIdComunidad(int idComunidad) {
		this.idComunidad = idComunidad;
	}

	@Override
	public String toString() {
		return this.nombre;
	}
}

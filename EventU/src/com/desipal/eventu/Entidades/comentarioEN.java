package com.desipal.eventu.Entidades;

import java.util.Date;

public class comentarioEN {
	private Date fecha;
	private float valoracion;
	private String Comentario;
	private boolean mostrado = false;

	public comentarioEN(Date fecha, float val, String com) {
		this.fecha = fecha;
		this.valoracion = val;
		this.Comentario = com;
	}

	public comentarioEN() {
	}

	public float getValoracion() {
		return valoracion;
	}

	public void setValoracion(float valoracion) {
		this.valoracion = valoracion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getComentario() {
		return Comentario;
	}

	public void setComentario(String comentario) {
		Comentario = comentario;
	}

	public boolean isMostrado() {
		return mostrado;
	}

	public void setMostrado(boolean mostrado) {
		this.mostrado = mostrado;
	}

}

package com.desipal.eventu.Entidades;

import android.graphics.Bitmap;

public class ImagenEN {
	private String url;
	private Bitmap imagen;
	// Los estados pueden ser 0 No hacer nada,1 Dar de alta,2 Borrar imagen
	private int estado;

	public ImagenEN(String url, Bitmap imagen, int estado) {
		this.url = url;
		this.imagen = imagen;
		this.estado = estado;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Bitmap getImagen() {
		return imagen;
	}

	public void setImagen(Bitmap imagen) {
		this.imagen = imagen;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}
}

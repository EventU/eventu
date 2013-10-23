package com.desipal.eventu.Presentacion;

public class EntryItem implements Item {

	public final String title;
	public final String subtitle;
	public final int icono;

	public EntryItem(int icono, String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
		this.icono = icono;
	}

	@Override
	public boolean isSection() {
		return false;
	}

}
package com.desipal.eventu.Imagenes;

import com.desipal.eventu.R;
import com.desipal.eventu.detalleEventoActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class GaleriaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imagen_galeria_item);
		ImageView img = (ImageView) findViewById(R.id.foto);
		Bundle e = getIntent().getExtras();
		int position = e.getInt("position");
		img.setImageDrawable(detalleEventoActivity.mImageIds[position]);
	}
}

package com.desipal.eventu.Imagenes;

import java.util.List;

import com.desipal.eventu.R;
import com.desipal.eventu.detalleEventoActivity;
import com.desipal.eventu.Presentacion.galeriaCompletaAdapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

public class galeriaActivity extends Activity {

	// Declare Variables
	ViewPager viewPager;
	PagerAdapter adapter;
	String[] rank;
	String[] country;
	String[] population;
	int[] flag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// Get the view from viewpager_main.xml
		setContentView(R.layout.galeriacompleta);
		List<Drawable> listimagenes = detalleEventoActivity.fotosGaleria;

		// Locate the ViewPager in viewpager_main.xml
		viewPager = (ViewPager) findViewById(R.id.pager);
		// Pass results to ViewPagerAdapter Class
		adapter = new galeriaCompletaAdapter(this, listimagenes);
		// Binds the Adapter to the ViewPager
		viewPager.setAdapter(adapter);

	}
}

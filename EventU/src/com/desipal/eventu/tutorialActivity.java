package com.desipal.eventu;

import com.desipal.eventu.Extras.InicioMuestraDrawer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class tutorialActivity extends Activity implements OnClickListener {
	public DrawerLayout drawer = null;
	public ListView list = null;
	private ImageView img;
	// Contador de veces que pulsa la imagen
	int pulsaciones = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			drawer = InicioMuestraDrawer.drawer;
			list = InicioMuestraDrawer.list;
			setContentView(R.layout.tutorial);
			RelativeLayout rel = (RelativeLayout) findViewById(R.id.relTutorial);
			img = (ImageView) findViewById(R.id.imgTutorial);
			rel.setOnClickListener(this);
			img.setOnClickListener(this);
		} catch (Exception e) {
			e.toString();
		}
	}

	@Override
	public void onClick(View v) {
		switch (pulsaciones) {
		case 0:
			img.setImageResource(R.drawable.tutorial1);
			drawer.openDrawer(list);
			SharedPreferences sp = getSharedPreferences("PrimerInicio", 0);
			Editor e = sp.edit();
			e.putBoolean("PrimerInicio", false);
			e.commit();
			break;
		case 1:
			finish();
			break;
		}
		pulsaciones++;

	}
}

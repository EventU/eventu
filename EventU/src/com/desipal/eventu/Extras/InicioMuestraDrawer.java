package com.desipal.eventu.Extras;

import com.desipal.eventu.tutorialActivity;
import com.desipal.eventu.PopUp.alertLocalizacion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

public class InicioMuestraDrawer extends Thread {
	public static DrawerLayout drawer = null;
	public static ListView list = null;
	private Activity con = null;
	private boolean estadoGPS = false;

	public InicioMuestraDrawer(Activity c, DrawerLayout d, ListView l) {
		con = c;
		drawer = d;
		list = l;
		LocationManager manager = (LocationManager) con
				.getSystemService(Context.LOCATION_SERVICE);
		estadoGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void run() {
		SharedPreferences sp = con.getSharedPreferences("PrimerInicio", 0);
		boolean isFirstStart = sp.getBoolean("PrimerInicio", true);
		if (isFirstStart) {
			Intent i = new Intent(con, tutorialActivity.class);
			con.startActivity(i);		
		}

		SharedPreferences pGPS = con
				.getSharedPreferences("ConfiguracionGPS", 0);
		boolean isGPS = pGPS.getBoolean("ConfiguracionGPS", true);
		if (!estadoGPS && isGPS && !isFirstStart) {
			alertLocalizacion alert = new alertLocalizacion(con);
			alert.create();
			alert.show();
		}
	}
}

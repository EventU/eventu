package com.desipal.eventu.PopUp;

import com.desipal.eventu.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class alertLocalizacion extends AlertDialog.Builder {

	public alertLocalizacion(final Activity context) {

		super(context);
		this.setTitle("Advertencia");
		this.setMessage(R.string.mensajeGPS);
		this.setNegativeButton(R.string.mensajeCancelar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		this.setNeutralButton(R.string.mensajeNoRecordar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						SharedPreferences pGPS = context.getSharedPreferences(
								"ConfiguracionGPS", 0);
						Editor e = pGPS.edit();
						e.putBoolean("ConfiguracionGPS", false);
						e.commit();
					}
				});
		this.setPositiveButton(R.string.mensajeSi,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent gpsOptionsIntent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						context.startActivity(gpsOptionsIntent);
					}
				});
	}

}

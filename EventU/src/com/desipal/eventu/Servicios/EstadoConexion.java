package com.desipal.eventu.Servicios;

import com.desipal.eventu.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class EstadoConexion extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// Informacion de la conexion por datos
		NetworkInfo infoDatos = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		// Informacion de la conexión wifi
		NetworkInfo infoWifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		boolean isConnected = (infoDatos != null && infoDatos
				.isConnectedOrConnecting())
				|| (infoWifi != null && infoWifi.isConnectedOrConnecting());

		if (isConnected)
			MainActivity.estadoConexion = true;
		else
			MainActivity.estadoConexion = false;
	}
}

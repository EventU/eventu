package com.desipal.eventu;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AcercaDe extends Fragment {
	public static View view;
	private Activity Actividad;
	private TextView txtVersion;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		try {
			Actividad = getActivity();
			view = inflater.inflate(R.layout.acercade, container, false);
			txtVersion = (TextView) view.findViewById(R.id.txtVersion);
			try {
				PackageInfo pkgInfo;
				pkgInfo = Actividad.getPackageManager().getPackageInfo(
						"com.desipal.eventu", 0);
				txtVersion.setText(String.format(
						getString(R.string.acercaDeVersion),
						pkgInfo.versionName));
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} catch (InflateException e) {
		}
		return view;
	}

}
package com.desipal.eventu.Presentacion;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.desipal.eventu.MainActivity;
import com.desipal.eventu.R;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.desipal.eventu.Entidades.miniEventoEN;

public class EventoAdaptador extends BaseAdapter {
	private Context mContext;
	protected List<miniEventoEN> items;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",
			MainActivity.currentLocale);
	private boolean[] array = new boolean[300];
	boolean bandera = false;
	private static LayoutInflater inflater = null;

	public EventoAdaptador(Context c, List<miniEventoEN> eventos) {
		mContext = c;
		this.items = eventos;
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return items.get(position).getIdEvento();
	}

	// create a new ImageView for each item referenced by the Adapter
	public void inicializarAnimacion()
	{
		array = new boolean[300];
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		View vista = convertView;
		try {
			if (array[position] == false) {
				bandera = true;
				array[position] = true;
			} else
				bandera = false;

			if (convertView == null)
				vista = inflater.inflate(R.layout.itemevento, null);

			miniEventoEN item = items.get(position);
			ImageView imgEvento = (ImageView) vista
					.findViewById(R.id.imgEvento);
			MainActivity.imageLoader.DisplayImage(item.getUrlImagen(), imgEvento);
			// new ImageDownloaderTask(imgEvento).execute(item.getUrlImagen());

			TextView txtFecha = (TextView) vista
					.findViewById(R.id.txtFechaEvento);
			String fecha = dateFormat.format(item.getFecha());
			txtFecha.setText(fecha);
			TextView txtDescrip = (TextView) vista
					.findViewById(R.id.txtDescEvento);
			if (item.getDescripcion().length() > 100) {
				txtDescrip.setText(item.getDescripcion().subSequence(0, 100)
						+ " <Leer más>");
			} else
				txtDescrip.setText(item.getDescripcion());
			TextView txtDist = (TextView) vista
					.findViewById(R.id.txtDistEvento);
			String distancia = new DecimalFormat("#.##").format(item
					.getDistancia());
			txtDist.setText(distancia + " Km");

			TextView txtNombre = (TextView) vista
					.findViewById(R.id.txtTituloEvento);
			txtNombre.setText(item.getNombre());

			Animation animation = AnimationUtils.loadAnimation(mContext,
					R.anim.push_up_in);
			if (bandera)
				vista.startAnimation(animation);

		} catch (Exception e) {
			AlertDialog.Builder alertaSimple = new AlertDialog.Builder(mContext);
			alertaSimple.setTitle("Error");
			alertaSimple.setMessage(e.toString());
			alertaSimple.show();
		}
		return vista;
	}
}

package com.desipal.eventu.Presentacion;

import java.text.DecimalFormat;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		View vista = convertView;
		try {

			if (convertView == null)
				vista = inflater.inflate(R.layout.itemevento, null);

			miniEventoEN item = items.get(position);
			ImageView imgEvento = (ImageView) vista
					.findViewById(R.id.imgEvento);
			MainActivity.imageLoader.DisplayImage(item.getUrlImagen(),
					imgEvento);
			// new ImageDownloaderTask(imgEvento).execute(item.getUrlImagen());

			TextView txtFecha = (TextView) vista
					.findViewById(R.id.txtFechaEvento);
			String fecha = MainActivity.formatoFechaMostrar.format(item
					.getFecha());

			// Si el evento tiene fecha de fin se agrega
			if (item.getFechaFin() != null)
				fecha = fecha
						+ " - "
						+ MainActivity.formatoFechaMostrar.format(item
								.getFechaFin());

			txtFecha.setText(fecha);
			TextView txtDescrip = (TextView) vista
					.findViewById(R.id.txtDescEvento);
			if (item.getDescripcion().length() > 100) {
				txtDescrip.setText(item.getDescripcion().subSequence(0, 100)
						+ " <Leer m�s>");
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

			if (!item.isMostrado()) {
				Animation animation = AnimationUtils.loadAnimation(mContext,
						R.anim.push_up_in);
				vista.startAnimation(animation);
				item.setMostrado(true);
			}
		} catch (Exception e) {
			AlertDialog.Builder alertaSimple = new AlertDialog.Builder(mContext);
			alertaSimple.setTitle("Error");
			alertaSimple.setMessage(e.toString());
			alertaSimple.show();
		}
		return vista;
	}
}

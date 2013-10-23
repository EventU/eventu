package com.desipal.eventu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.desipal.eventu.Entidades.miniEventoEN;
import com.desipal.eventu.MainActivity;
import com.desipal.eventu.Presentacion.EventoAdaptador;
import com.desipal.eventu.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

public class Fragment1 extends Fragment {
	private ListView listview;
	// Carga de la lista
	private List<miniEventoEN> lista = new ArrayList<miniEventoEN>();
	private EventoAdaptador adaptador;
	private int pagina = 0;
	private boolean bloquearPeticion = false;
	private View footer;
	private boolean finlista = false;
	public static View view;
	private Activity Actividad;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (view == null) {
			try {
				Actividad = getActivity();
				view = inflater.inflate(R.layout.fragment_1, container, false);
				listview = (ListView) view.findViewById(R.id.list);
				listview.setFadingEdgeLength(0);

				// evento de la lista a leer mas
				footer = inflater.inflate(R.layout.footerlistview, null);
				listview.setOnScrollListener(new AbsListView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {

					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						if (lista.size() % MainActivity.ELEMENTOSLISTA == 0
								&& !bloquearPeticion
								&& (firstVisibleItem + visibleItemCount) == totalItemCount
								&& !finlista) {
							realizarPeticion();
							listview.addFooterView(footer);
						}
					}
				});
				// Comienzo de llenado de listas
				adaptador = new EventoAdaptador(getActivity(), lista);
				listview.setAdapter(adaptador);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v,
							int position, long id) {
						Intent i = new Intent(getActivity(),
								detalleEventoActivity.class);
						i.putExtra("idEvento", id);
						startActivity(i);
					}
				});
			} catch (InflateException e) {
				// Si la vista ya existe retorna la anterior.
			}
		} else {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		return view;
	}

	private void realizarPeticion() {
		bloquearPeticion = true;
		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("latitud", Double
				.toString(MainActivity.posicionActual.latitude)));
		parametros.add(new BasicNameValuePair("longitud", Double
				.toString(MainActivity.posicionActual.longitude)));

		parametros.add(new BasicNameValuePair("itemsPerPage",
				MainActivity.ELEMENTOSLISTA + ""));
		String URL = "http://desipal.hol.es/app/eventos/filtro.php";

		parametros.add(new BasicNameValuePair("filtro", ""));
		parametros.add(new BasicNameValuePair("fecha", ""));
		pagina = ((int) lista.size() / MainActivity.ELEMENTOSLISTA) + 1;

		parametros.add(new BasicNameValuePair("page", pagina + ""));
		parametros.add(new BasicNameValuePair("idCat", "1"));
		peticion pet = new peticion(parametros, Actividad);
		pet.execute(new String[] { URL });
	}

	// Peticion de Servidor
	public class peticion extends AsyncTask<String, Void, Void> {

		private Context mContext;
		private ArrayList<NameValuePair> parametros;

		public peticion(ArrayList<NameValuePair> parametros, Context context) {
			this.parametros = parametros;
			this.mContext = context;
		}

		@Override
		protected Void doInBackground(String... urls) {
			for (String url : urls) {
				try {
					DefaultHttpClient client = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);
					httppost.setEntity(new UrlEncodedFormEntity(parametros));

					HttpResponse execute = client.execute(httppost);
					InputStream content = execute.getEntity().getContent();
					BufferedReader r = new BufferedReader(
							new InputStreamReader(content));
					StringBuilder total = new StringBuilder();
					String line;
					while ((line = r.readLine()) != null) {
						total.append(line);
					}
					if (!total.toString().equals("null")) {
						JSONArray o = new JSONArray(total.toString());
						for (int i = 0; o.length() > i; i++) {
							miniEventoEN e = new miniEventoEN();
							JSONObject jobj = o.getJSONObject(i);
							e.setIdEvento(jobj.getInt("idEvento"));
							e.setNombre(jobj.getString("nombre"));
							e.setDescripcion(jobj.getString("descripcion"));
							e.setDistancia(jobj.getDouble("distancia"));
							e.setUrl(jobj.getString("url"));
							e.setLatitud(jobj.getDouble("latitud"));
							e.setLongitud(jobj.getDouble("longitud"));

							if (!jobj.getString("imagen").equals("noimagen")) {
								String ere = jobj.getString("imagen");
								Bitmap bitmap = BitmapFactory
										.decodeStream((InputStream) new URL(ere)
												.getContent());
								Drawable d = new BitmapDrawable(
										mContext.getResources(), bitmap);
								e.setImagen(d);
							} else
								e.setImagen(mContext.getResources()
										.getDrawable(R.drawable.default_img));

							e.setFecha(MainActivity.formatoFecha.parse(jobj
									.getString("fecha")));
							lista.add(e);
						}
					} else if (lista.size() > 0)
						finlista = true;

				} catch (Exception e) {
					Toast.makeText(
							Actividad,
							"No se han podido recuperar eventos,pruebe pasados unos minutos",
							Toast.LENGTH_LONG).show();
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			adaptador.notifyDataSetChanged();
			bloquearPeticion = false;
			listview.removeFooterView(footer);
		}
	}
}

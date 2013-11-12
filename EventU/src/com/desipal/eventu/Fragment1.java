package com.desipal.eventu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Fragment1 extends Fragment {

	// Carga de la lista
	private List<miniEventoEN> lista = new ArrayList<miniEventoEN>();
	private EventoAdaptador adaptador;
	private int pagina = 0;
	private boolean bloquearPeticion = false;
	private boolean finlista = false;
	public static View view;
	private Activity Actividad;
	private PullToRefreshListView listView;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		try {
			Actividad = getActivity();
			view = inflater.inflate(R.layout.fragment_1, container, false);
			listView = (PullToRefreshListView) view.findViewById(R.id.list);
			listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
				@Override
				public void onRefresh(PullToRefreshBase<ListView> refreshView) {
					finlista = false;
					lista.clear();
					adaptador.notifyDataSetChanged();
				}
			});
			listView.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					if (lista.size() % MainActivity.ELEMENTOSLISTA == 0
							&& !bloquearPeticion
							&& (firstVisibleItem + visibleItemCount) == totalItemCount
							&& !finlista) {
						realizarPeticion();
					}
				}
			});

			// Comienzo de llenado de listas
			adaptador = new EventoAdaptador(getActivity(), lista);
			listView.setAdapter(adaptador);

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int position, long id) {
					try {
						Intent i = new Intent(Actividad,
								detalleEventoActivity.class);
						i.putExtra("idEvento", id);
						startActivity(i);
					} catch (Exception e) {
					}
				}
			});
		} catch (InflateException e) {
			// Si la vista ya existe retorna la anterior.
		}
		return view;
	}

	private void realizarPeticion() {
		try {
			if (MainActivity.estadoConexion) {
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

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.MONTH, 1);
				String fechaMes = calendar.get(Calendar.DAY_OF_MONTH) + "/"
						+ (calendar.get(Calendar.MONTH) + 1) + "/"
						+ calendar.get(Calendar.YEAR);

				parametros.add(new BasicNameValuePair("fechaMes", fechaMes));
				pagina = ((int) lista.size() / MainActivity.ELEMENTOSLISTA) + 1;

				parametros.add(new BasicNameValuePair("page", pagina + ""));
				parametros.add(new BasicNameValuePair("idCat", "1"));
				peticion pet = new peticion(parametros, Actividad);
				pet.execute(new String[] { URL });
			} else {
				listView.onRefreshComplete();
				Toast.makeText(getActivity(), R.string.errNoConexion,
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.toString();
		}
	}

	// Peticion de Servidor
	public class peticion extends AsyncTask<String, Void, Void> {
		private ArrayList<NameValuePair> parametros;
		private boolean error = false;

		public peticion(ArrayList<NameValuePair> parametros, Context context) {
			this.parametros = parametros;
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
							// nuevo
							e.setUrlImagen(jobj.getString("imagen"));
							e.setFecha(MainActivity.formatoFecha.parse(jobj
									.getString("fecha")));

							if (!jobj.getBoolean("todoElDia"))
								e.setFechaFin(MainActivity.formatoFecha
										.parse(jobj.getString("fechaFin")));
							lista.add(e);
						}
					} else if (lista.size() > 0)
						finlista = true;
					else {
						finlista = true;
					}

				} catch (Exception e) {
					finlista = true;
					error = true;
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			adaptador.notifyDataSetChanged();
			bloquearPeticion = false;
			listView.onRefreshComplete();
			if (error)
				Toast.makeText(Actividad, R.string.errNoServidor,
						Toast.LENGTH_LONG).show();

			// listView.removeFooterView(footer);
		}
	}
}

package com.desipal.eventu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import com.desipal.eventu.Entidades.miniEventoEN;
import com.desipal.eventu.Extras.UrlsServidor;
import com.desipal.eventu.Presentacion.EventoAdaptador;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MisEventos extends Fragment {
	private ListView listview;
	private View footer;
	// Carga de la lista
	private List<miniEventoEN> lista = new ArrayList<miniEventoEN>();
	private EventoAdaptador adaptador;
	private int pagina = 0;
	private boolean bloquearPeticion = false;
	private boolean finlista = false;
	public static View view;
	private Activity Actividad;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		try {
			Actividad = getActivity();
			view = inflater.inflate(R.layout.mis_eventos, container, false);
			footer = inflater.inflate(R.layout.footerlistview, null);
			listview = (ListView) view.findViewById(R.id.listMisEvent);
			listview.setFadingEdgeLength(0);

			// evento de la lista a leer mas
			listview.setOnScrollListener(new AbsListView.OnScrollListener() {
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
			// Pulsacion larga en un evento
			listview.setLongClickable(true);
			listview.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						final int posicion, long arg3) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getActivity());
					alertDialogBuilder.setMessage("¿Que desea hacer?");
					alertDialogBuilder.setPositiveButton("Modificar",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent i = new Intent(getActivity(),
											crearEventoActivity.class);
									i.putExtra("idEvento", lista.get(posicion)
											.getIdEvento());
									startActivity(i);
								}
							});
					alertDialogBuilder.setNegativeButton("Borrar",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											getActivity());
									alertDialogBuilder
											.setMessage("¿Esta seguro de borrar el evento?");

									alertDialogBuilder
											.setPositiveButton(
													"Si",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.dismiss();
															// Realizar peticion
															String URL = UrlsServidor.MISEVENTOSBORRAR;
															PeticionBorrar pet = new PeticionBorrar(
																	Actividad,
																	lista.get(
																			posicion)
																			.getIdEvento());
															Toast.makeText(
																	Actividad,
																	"¡Evento borrado correctamente!",
																	Toast.LENGTH_LONG)
																	.show();
															pet.execute(new String[] { URL });
															lista.remove(posicion);
															adaptador
																	.notifyDataSetChanged();
														}
													});
									alertDialogBuilder
											.setNegativeButton(
													"No",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															dialog.dismiss();
														}
													});
									alertDialogBuilder.create();
									alertDialogBuilder.show();

								}
							});
					alertDialogBuilder.create();
					alertDialogBuilder.show();
					return true;
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
		}
		return view;
	}

	private void realizarPeticion() {
		if (MainActivity.estadoConexion) {
			listview.addFooterView(footer);
			bloquearPeticion = true;
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("latitud", Double
					.toString(MainActivity.posicionActual.latitude)));
			parametros.add(new BasicNameValuePair("longitud", Double
					.toString(MainActivity.posicionActual.longitude)));
			parametros.add(new BasicNameValuePair("idDispositivo",
					MainActivity.IDDISPOSITIVO));

			parametros.add(new BasicNameValuePair("itemsPerPage",
					MainActivity.ELEMENTOSLISTA + ""));
			String URL = UrlsServidor.MISEVENTOS;

			parametros.add(new BasicNameValuePair("filtro", ""));
			parametros.add(new BasicNameValuePair("fecha", ""));
			pagina = ((int) lista.size() / MainActivity.ELEMENTOSLISTA) + 1;

			parametros.add(new BasicNameValuePair("page", pagina + ""));
			parametros.add(new BasicNameValuePair("idCat", "1"));
			peticion pet = new peticion(parametros, Actividad);
			pet.execute(new String[] { URL });
		} else {
			listview.removeFooterView(footer);
			Toast.makeText(getActivity(), R.string.errNoConexion,
					Toast.LENGTH_LONG).show();
		}

	}

	// Peticion de Servidor
	public class peticion extends AsyncTask<String, Void, Void> {
		private ArrayList<NameValuePair> parametros;

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

				} catch (Exception e) {
					Toast.makeText(Actividad, R.string.errNoServidor,
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

	public class PeticionBorrar extends AsyncTask<String, Void, Void> {
		int idEvento = 0;

		public PeticionBorrar(Context context, int idEvento) {
			this.idEvento = idEvento;
		}

		@Override
		protected Void doInBackground(String... urls) {
			for (String url : urls) {
				try {
					DefaultHttpClient client = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);
					ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
					parametros.add(new BasicNameValuePair("idDispositivo",
							MainActivity.IDDISPOSITIVO));
					parametros.add(new BasicNameValuePair("idEvento", idEvento
							+ ""));
					httppost.setEntity(new UrlEncodedFormEntity(parametros));
					client.execute(httppost);
				} catch (Exception e) {
					Toast.makeText(Actividad, R.string.errNoServidor,
							Toast.LENGTH_LONG).show();
				}
			}
			return null;
		}

	}
}
package com.desipal.eventu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.desipal.eventu.Entidades.categoriaEN;
import com.desipal.eventu.Entidades.miniEventoEN;
import com.desipal.eventu.Extras.Herramientas;
import com.desipal.eventu.PopUp.datepicker;
import com.desipal.eventu.Presentacion.EventoAdaptador;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class filtroAvanzado extends Fragment {
	private View footer;
	public static View view;
	private Activity Actividad;

	// Carga de la lista
	List<categoriaEN> listaCategorias = null;
	private List<miniEventoEN> lista = new ArrayList<miniEventoEN>();
	private EventoAdaptador adaptador;

	private static ListView listview;
	private EditText edNombreFiltro;
	private EditText edFechaFiltro;
	private Spinner spCategoriaFiltro;
	private static LinearLayout filtro;
	private Button btnFiltrar;
	private ImageButton btnLimpiar;

	private int pagina = 0;
	private static boolean recogido = true;
	private boolean finlista = false;
	private boolean bloquearPeticion = false;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		try {
			Actividad = getActivity();
			view = inflater.inflate(R.layout.filtro_avanzado, container, false);

			filtro = (LinearLayout) view.findViewById(R.id.linear_filtro);
			edNombreFiltro = (EditText) view.findViewById(R.id.campoFiltro);
			edFechaFiltro = (EditText) view.findViewById(R.id.campoFecha);
			spCategoriaFiltro = (Spinner) view.findViewById(R.id.spiCategorias);
			listview = (ListView) view.findViewById(R.id.listResultados);
			btnFiltrar = (Button) view.findViewById(R.id.btnFiltroAv);
			btnLimpiar = (ImageButton) view.findViewById(R.id.btnLimpiar);

			listview.setFadingEdgeLength(0);
			edNombreFiltro.setText(MainActivity.queryFiltro);
			edFechaFiltro.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogFragment newFragment = new datepicker();
					((datepicker) newFragment).establecerCampo(edFechaFiltro);
					newFragment.show(getFragmentManager(), "DatePicker");
				}
			});

			btnFiltrar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					lista.clear();
					final InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
					listview.addFooterView(footer);
					realizarPeticion();
				}
			});
			btnLimpiar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					edNombreFiltro.setText("");
					edFechaFiltro.setText("");
					spCategoriaFiltro.setSelection(0);
				}
			});
			// Spinner catgorias
			listaCategorias = Herramientas.Obtenercategorias(Actividad);
			String a[] = new String[listaCategorias.size()];
			for (int i = 0; i < listaCategorias.size(); i++) {
				a[i] = listaCategorias.get(i).getTexto();
			}
			ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Actividad,
					R.layout.spinner_item, a);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spCategoriaFiltro.setAdapter(adapter2);
			spCategoriaFiltro.setEnabled(true);

			// Efecto de la lista al leer mas
			footer = inflater.inflate(R.layout.footerlistview, null);
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
		}
		return view;
	}

	public static void recoger() {
		Handler handler = new Handler();
		if (recogido) {
			TranslateAnimation anim_trans = new TranslateAnimation(0, 0, 0,
					filtro.getHeight());
			anim_trans.setDuration(200);
			listview.startAnimation(anim_trans);
			handler.postDelayed(new Runnable() {
				public void run() {
					filtro.setVisibility(View.VISIBLE);
					AlphaAnimation anim_alpha = new AlphaAnimation(0, 1);
					anim_alpha.setDuration(200);
					filtro.startAnimation(anim_alpha);
				}
			}, 220);
			recogido = false;
		} else {
			AlphaAnimation anim_trans = new AlphaAnimation(1, 0);
			anim_trans.setDuration(200);
			filtro.startAnimation(anim_trans);

			handler.postDelayed(new Runnable() {
				public void run() {
					filtro.setVisibility(View.GONE);
					TranslateAnimation anim_trans = new TranslateAnimation(0,
							0, filtro.getHeight(), 0);
					anim_trans.setDuration(200);
					listview.startAnimation(anim_trans);
				}
			}, 200);
			recogido = true;
		}
	}

	private void realizarPeticion() {
		try {
			bloquearPeticion = true;
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("latitud", Double
					.toString(MainActivity.posicionActual.latitude)));
			parametros.add(new BasicNameValuePair("longitud", Double
					.toString(MainActivity.posicionActual.longitude)));

			parametros.add(new BasicNameValuePair("itemsPerPage",
					MainActivity.ELEMENTOSLISTA + ""));
			String URL = "http://desipal.hol.es/app/eventos/filtro.php";

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			String stFecha = edFechaFiltro.getText().toString();			

			parametros.add(new BasicNameValuePair("fecha",stFecha));

			parametros.add(new BasicNameValuePair("filtro", edNombreFiltro
					.getText().toString()));

			int idCategoria =1;
			boolean encontrado=false;
			String categoria= spCategoriaFiltro.getSelectedItem().toString();
			for (int i = 0; i < listaCategorias.size() && !encontrado; i++) {
				if (listaCategorias.get(i).getTexto().equals(categoria)) {
					idCategoria = listaCategorias.get(i)
							.getIdCategoria();
					encontrado = true;
				}
			}
			parametros.add(new BasicNameValuePair("idCat", idCategoria+""));
			pagina = ((int) lista.size() / MainActivity.ELEMENTOSLISTA) + 1;

			parametros.add(new BasicNameValuePair("page", pagina + ""));
			peticion pet = new peticion(parametros, Actividad);
			pet.execute(new String[] { URL });
		} catch (Exception e) {
			e.toString();
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
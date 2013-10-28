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
import com.desipal.eventu.Extras.Herramientas;
import com.desipal.eventu.Presentacion.EventoAdaptador;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Fragment2 extends Fragment {

	public static List<miniEventoEN> eventos = new ArrayList<miniEventoEN>();

	boolean bloquearPeticion = false;// bandera que bloquea para no poder jhacer
										// la peticion
	private int ratio;

	private int pagina = 0;
	private ListView gridCerca;
	private Button btnBuscarCerca;
	private TextView txtKm;
	private TextView txtErrorCerca;
	private SeekBar seekRadio;
	private ProgressBar progressCerca;
	private ToggleButton togOpcionMapa;
	private GoogleMap map;
	private RelativeLayout LayoutMapa;
	private List<Marker> listapuntos = new ArrayList<Marker>();
	private View footer;
	private boolean finlista = false;
	private EventoAdaptador adaptador;
	public static View view;
	private Activity Actividad;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (view == null) {
			try {
				Actividad = getActivity();
				view = inflater.inflate(R.layout.fragment_2, container, false);

				footer = inflater.inflate(R.layout.footerlistview, null);

				gridCerca = (ListView) view
						.findViewById(R.id.gridResultadosCerca);
				gridCerca.setFadingEdgeLength(0);

				txtKm = (TextView) view.findViewById(R.id.txtKm);
				seekRadio = (SeekBar) view.findViewById(R.id.seekRadio);
				progressCerca = (ProgressBar) view
						.findViewById(R.id.proResulCerca);
				txtErrorCerca = (TextView) view
						.findViewById(R.id.txtErrorCerca);
				togOpcionMapa = (ToggleButton) view
						.findViewById(R.id.togOpcionMapa);
				btnBuscarCerca = (Button) view
						.findViewById(R.id.btnBuscarCerca);
				LayoutMapa = (RelativeLayout) view
						.findViewById(R.id.relativeMapa);

				gridCerca
						.setOnScrollListener(new AbsListView.OnScrollListener() {
							@Override
							public void onScrollStateChanged(AbsListView view,
									int scrollState) {

							}

							@Override
							public void onScroll(AbsListView view,
									int firstVisibleItem, int visibleItemCount,
									int totalItemCount) {
								if (eventos.size()
										% MainActivity.ELEMENTOSLISTA == 0
										&& !bloquearPeticion
										&& (firstVisibleItem + visibleItemCount) == totalItemCount
										&& !finlista) {
									gridCerca.addFooterView(footer);
									hacerPeticion();
								}
							}
						});
				adaptador = new EventoAdaptador(getActivity(), eventos);

				map = ((SupportMapFragment) getActivity()
						.getSupportFragmentManager().findFragmentById(
								R.id.mapaCercanos)).getMap();

				map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

					@Override
					public void onInfoWindowClick(Marker marker) {
						for (Marker item : listapuntos) {
							if (item.equals(marker)) {
								int i = listapuntos.indexOf(item);
								miniEventoEN sel = Fragment2.eventos.get(i);
								Intent ac = new Intent(getActivity(),
										detalleEventoActivity.class);
								ac.putExtra("idEvento",
										(long) sel.getIdEvento());
								startActivity(ac);
								break;
							}
						}

					}
				});

				txtKm.setText("50 Km");
				seekRadio.setProgress(50);
				seekRadio
						.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {
							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
							}

							@Override
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								if (progress == 0)
									txtKm.setText("Todos");
								else
									txtKm.setText(progress + " Km");
							}
						});

				gridCerca.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v,
							int position, long id) {
						Intent i = new Intent(getActivity(),
								detalleEventoActivity.class);
						i.putExtra("idEvento", id);
						startActivity(i);
					}
				});

				btnBuscarCerca.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						btnBuscarCerca.setEnabled(false);
						progressCerca.setVisibility(View.VISIBLE);
						LayoutMapa.setVisibility(View.GONE);
						gridCerca.setVisibility(View.GONE);
						txtErrorCerca.setVisibility(View.GONE);
						togOpcionMapa.setEnabled(false);
						eventos.clear();
						bloquearPeticion = false;
						hacerPeticion();
					}
				});

				togOpcionMapa
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								if (isChecked) {
									// MAPA
									map.clear();
									gridCerca.setVisibility(View.GONE);
									progressCerca.setVisibility(View.GONE);
									LayoutMapa.setVisibility(View.VISIBLE); // Pointer
																			// de
																			// mi
																			// posicion
									LatLng MiPosicion = MainActivity.posicionActual;
									map.addMarker(new MarkerOptions().position(
											MiPosicion).title("Estas aquí"));
									listapuntos.clear();
									for (miniEventoEN item : eventos) {
										LatLng Posicion = new LatLng(item
												.getLatitud(), item
												.getLongitud());
										listapuntos.add(map.addMarker(new MarkerOptions()
												.position(Posicion)
												.title(item.getNombre())
												.icon(BitmapDescriptorFactory
														.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
									}
									CircleOptions circleOptions = new CircleOptions()
											.center(MiPosicion)
											.radius(ratio * 1000)
											// set radius in meters
											.fillColor(
													Color.argb(20, 0, 0, 255))
											.strokeColor(Color.BLUE)
											.strokeWidth((float) 1.5);
									map.addCircle(circleOptions);
									int zoom = Herramientas.calcularZoom(ratio);
									map.moveCamera(CameraUpdateFactory
											.newLatLngZoom(MiPosicion, zoom));
									map.animateCamera(
											CameraUpdateFactory.zoomTo(zoom),
											2000, null);

									togOpcionMapa.setEnabled(true);

								} else {
									LayoutMapa.setVisibility(View.GONE);
									gridCerca.setVisibility(View.VISIBLE);
									gridCerca.setAdapter(new EventoAdaptador(
											getActivity(), eventos));
									progressCerca.setVisibility(View.GONE);
									togOpcionMapa.setEnabled(true);
								}
							}
						});
				gridCerca.setAdapter(adaptador);
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

	protected void hacerPeticion() {

		if (!MainActivity.errorServicios) {
			bloquearPeticion = true;
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("latitud", Double
					.toString(MainActivity.posicionActual.latitude)));
			parametros.add(new BasicNameValuePair("longitud", Double
					.toString(MainActivity.posicionActual.longitude)));

			String URL = "http://desipal.hol.es/app/eventos/eventosCerca.php";

			parametros.add(new BasicNameValuePair("ratio", seekRadio
					.getProgress() + ""));
			pagina = ((int) eventos.size() / MainActivity.ELEMENTOSLISTA) + 1;

			parametros.add(new BasicNameValuePair("page", pagina + ""));
			peticion pet = new peticion(parametros, getActivity());
			pet.execute(new String[] { URL });

			if (togOpcionMapa.isChecked()) { // MAPA
				map.clear();
				progressCerca.setVisibility(View.GONE);
				LayoutMapa.setVisibility(View.VISIBLE); // Pointer
														// de mi
														// posicion
				LatLng MiPosicion = new LatLng(
						MainActivity.posicionActual.latitude,
						MainActivity.posicionActual.longitude);
				map.addMarker(new MarkerOptions().position(MiPosicion).title(
						"Estas aquí"));
				listapuntos.clear();
				for (miniEventoEN item : eventos) {
					LatLng Posicion = new LatLng(item.getLatitud(),
							item.getLongitud());
					listapuntos
							.add(map.addMarker(new MarkerOptions()
									.position(Posicion)
									.title(item.getNombre())
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
				}
				CircleOptions circleOptions = new CircleOptions()
						.center(MiPosicion).radius(ratio * 1000)
						// set radius in meters
						.fillColor(Color.argb(20, 0, 0, 255))
						.strokeColor(Color.BLUE).strokeWidth((float) 1.5);
				map.addCircle(circleOptions);
				int zoom = Herramientas.calcularZoom(ratio);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(MiPosicion,
						zoom));
				map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);

				togOpcionMapa.setEnabled(true);
				btnBuscarCerca.setEnabled(true);
			} else { // Lista
				gridCerca.setVisibility(View.VISIBLE);
				progressCerca.setVisibility(View.GONE);
				togOpcionMapa.setEnabled(true);
				btnBuscarCerca.setEnabled(true);
			}
		} else {
			txtErrorCerca.setText("No hay servicios de ubicacion");
			txtErrorCerca.setVisibility(View.VISIBLE);
			progressCerca.setVisibility(View.GONE);
		}
	}

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

							/*
							 * if (!jobj.getString("imagen").equals("noimagen"))
							 * { String ere = jobj.getString("imagen"); Bitmap
							 * bitmap = BitmapFactory
							 * .decodeStream((InputStream) new URL(ere)
							 * .getContent()); Drawable d = new BitmapDrawable(
							 * mContext.getResources(), bitmap); e.setImagen(d);
							 * } else e.setImagen(mContext.getResources()
							 * .getDrawable(R.drawable.default_img));
							 */

							e.setFecha(MainActivity.formatoFecha.parse(jobj
									.getString("fecha")));
							eventos.add(e);
						}
					} else if (eventos.size() > 0)
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
			gridCerca.removeFooterView(footer);
			adaptador.notifyDataSetChanged();
			bloquearPeticion = false;
			btnBuscarCerca.setEnabled(true);

		}
	}
}

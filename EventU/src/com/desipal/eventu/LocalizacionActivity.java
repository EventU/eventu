package com.desipal.eventu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.desipal.eventu.Entidades.provinciaEN;
import com.desipal.eventu.Extras.Herramientas;
import com.desipal.eventu.Extras.UrlsServidor;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

@SuppressWarnings("deprecation")
public class LocalizacionActivity extends FragmentActivity {
	private GoogleMap mapa;
	private EditText consulta;
	private ImageButton locActual;
	private RelativeLayout relBloquear;
	private RelativeLayout relErrorUbicacion;
	private TextView txtMensajeerror;
	private Spinner SpiProvincia;
	private EditText cmpErrorCiudad;
	private String claveAPI = "AIzaSyCJv9zzRank9b6bzqdTacgadnxQA60jpOo";
	private LatLng nevaposicion;
	private boolean nuevasCoordenadas = false;
	private boolean verSpiner = true;

	// private boolean errorCoordenadas;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			setContentView(R.layout.localizacion);
			mapa = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.mapaLocLayout)).getMap();
			consulta = (EditText) findViewById(R.id.cmpConsulta);
			locActual = (ImageButton) findViewById(R.id.btnUbiAct);
			relErrorUbicacion = (RelativeLayout) findViewById(R.id.relErrorUbicacion);
			txtMensajeerror = (TextView) findViewById(R.id.txtMensajeerror);
			SpiProvincia = (Spinner) findViewById(R.id.SpiProvincia);
			cmpErrorCiudad = (EditText) findViewById(R.id.cmpErrorCiudad);
			relBloquear = (RelativeLayout) findViewById(R.id.relBloquear);
			mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(
					MainActivity.posicionActual, 15));
			mapa.addMarker(new MarkerOptions().position(
					MainActivity.posicionActual).title("Estas aquí"));
			consulta.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						String comprobacion = consulta.getText().toString();
						if (!comprobacion.equals("")) {
							enviarconsulta();
							consulta.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.edittext_shadow));
							nuevasCoordenadas = false;
							return true;
						} else {
							consulta.setBackgroundDrawable(getResources()
									.getDrawable(
											R.drawable.edittext_shadow_error));
						}
					}
					return false;
				}
			});
			cmpErrorCiudad
					.setOnEditorActionListener(new OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_SEARCH) {
								String comprobacion = consulta.getText()
										.toString();
								if (!comprobacion.equals("")) {
									enviarconsultaError();
									consulta.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.edittext_shadow));
								} else {
									Toast.makeText(
											LocalizacionActivity.this,
											"El campo de busqueda del lugar del evento no puede estar vacio",
											Toast.LENGTH_SHORT).show();
									consulta.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.edittext_shadow_error));
								}
								return true;
							}
							return false;
						}
					});
			txtMensajeerror.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (verSpiner) {
						verSpiner = false;
						cmpErrorCiudad.setVisibility(View.VISIBLE);
						SpiProvincia.setVisibility(View.VISIBLE);
					} else {
						verSpiner = true;
						cmpErrorCiudad.setVisibility(View.GONE);
						SpiProvincia.setVisibility(View.GONE);
					}
				}
			});
			locActual.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mapa.clear();
					CameraPosition camPos = new CameraPosition.Builder()
							.target(MainActivity.posicionActual).zoom(15)
							.build();
					CameraUpdate camUpd3 = CameraUpdateFactory
							.newCameraPosition(camPos);
					mapa.animateCamera(camUpd3);
					mapa.addMarker(new MarkerOptions().position(
							MainActivity.posicionActual).title("Estas aquí"));
				}
			});

			mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker mar) {
					CameraPosition camPos = new CameraPosition.Builder()
							.target(mar.getPosition()).zoom(15).build();
					CameraUpdate camUpd3 = CameraUpdateFactory
							.newCameraPosition(camPos);
					mapa.animateCamera(camUpd3);
					mar.showInfoWindow();
					return true;
				}
			});

			mapa.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					final LatLng tem = marker.getPosition();
					final String temDir = marker.getSnippet();
					Builder p = new Builder(LocalizacionActivity.this);
					p.setTitle("Seleccion de localizacion");
					if (marker.getTitle().equals("Estas aquí"))
						p.setMessage("El evento se realizara en tu ubicacion actual");
					else
						p.setMessage("El evento se realizara en "
								+ marker.getTitle());
					p.setNegativeButton(R.string.mensajeCancelar,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					p.setPositiveButton(R.string.mensajeSi,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									Intent intent = new Intent(
											LocalizacionActivity.this,
											crearEventoActivity.class);
									intent.putExtra("latitud", tem.latitude);
									intent.putExtra("longitud", tem.longitude);
									intent.putExtra("direccion", temDir);
									setResult(RESULT_OK, intent);
									finish();
								}
							});
					p.create();
					try {
						p.show();
					} catch (Exception e) {
						e.getMessage();
					}
				}
			});
			Toast.makeText(getApplicationContext(),
					R.string.elegirLocalizacion, Toast.LENGTH_SHORT).show();

			List<provinciaEN> arr = Herramientas
					.ObtenerProvincias(LocalizacionActivity.this);
			ArrayList<String> listProvincias = new ArrayList<String>();
			for (provinciaEN provincia : arr) {
				listProvincias.add(provincia.getNombre());
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getBaseContext(), android.R.layout.simple_spinner_item,
					listProvincias);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			SpiProvincia.setAdapter(adapter);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	private void enviarconsulta() {
		final InputMethodManager imm = (InputMethodManager) LocalizacionActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(consulta.getWindowToken(), 0);
		relBloquear.setVisibility(View.VISIBLE);
		String url = "https://maps.googleapis.com/maps/api/place/search/json?";
		url += "location=" + MainActivity.posicionActual.latitude + ","
				+ MainActivity.posicionActual.longitude + "&";
		url += "radius=50000&sensor=true&key=" + claveAPI + "&language=es&";
		String consu = consulta.getText().toString();
		consu = consu.replaceAll(" ", "+");
		url += "name=" + consu;
		ConsultaPlaces con = new ConsultaPlaces();
		con.execute(new String[] { url });
	}

	private void enviarconsulta(LatLng ConError) {
		final InputMethodManager imm = (InputMethodManager) LocalizacionActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(consulta.getWindowToken(), 0);
		relBloquear.setVisibility(View.VISIBLE);
		String url = "https://maps.googleapis.com/maps/api/place/search/json?";
		url += "location=" + ConError.latitude + "," + ConError.longitude + "&";
		url += "radius=50000&sensor=true&key=" + claveAPI + "&language=es&";
		String consu = consulta.getText().toString();
		consu = consu.replaceAll(" ", "+");
		url += "name=" + consu;
		ConsultaPlaces con = new ConsultaPlaces();
		con.execute(new String[] { url });
	}

	public class ConsultaPlaces extends AsyncTask<String, Void, Void> {
		private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		private boolean error = false;
		private boolean errorconexion = false;

		public ConsultaPlaces() {
		}

		@Override
		protected Void doInBackground(String... urls) {
			for (String url : urls) {
				try {
					DefaultHttpClient client = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);

					HttpResponse execute = client.execute(httppost);
					InputStream content = execute.getEntity().getContent();
					BufferedReader r = new BufferedReader(
							new InputStreamReader(content));
					StringBuilder total = new StringBuilder();

					String line;
					while ((line = r.readLine()) != null) {
						total.append(line);
					}
					JSONObject jObject = new JSONObject(total.toString());
					String result = (String) jObject.get("status");
					if (!result.equals("ZERO_RESULTS")) {
						JSONArray sitios = new JSONArray(
								jObject.getString("results"));
						HashMap<String, String> hm = null;
						for (int i = 0; i < sitios.length(); i++) {
							JSONObject actual = sitios.getJSONObject(i);
							Double lng = (Double) actual
									.getJSONObject("geometry")
									.getJSONObject("location").get("lng");
							Double lat = (Double) actual
									.getJSONObject("geometry")
									.getJSONObject("location").get("lat");
							String direccion = (String) actual.get("vicinity");
							String nombre = (String) actual.get("name");
							hm = new HashMap<String, String>();
							hm.put("nombre", nombre);
							hm.put("direccion", direccion);
							hm.put("lat", Double.toString(lat));
							hm.put("lng", Double.toString(lng));
							list.add(hm);
						}
					} else
						error = true;

				} catch (UnknownHostException e) {
					errorconexion = true;
				} catch (Exception e) {
					e.getMessage();
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (!errorconexion) {
				if (!error) {
					mapa.clear();
					mapa.addMarker(new MarkerOptions().position(
							MainActivity.posicionActual).title("Estas aquí"));
					for (int i = 0; i < list.size(); i++) {
						HashMap<String, String> hMap = (HashMap<String, String>) list
								.get(i);
						LatLng ll = new LatLng(Double.parseDouble(hMap
								.get("lat")), Double.parseDouble(hMap
								.get("lng")));
						mapa.addMarker(new MarkerOptions()
								.position(ll)
								.title(hMap.get("nombre"))
								.snippet(hMap.get("direccion"))
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
					}
					int zoom = Herramientas.calcularZoom(50);
					if (!nuevasCoordenadas) {
						mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(
								MainActivity.posicionActual, zoom));
						cmpErrorCiudad.setVisibility(View.GONE);
						SpiProvincia.setVisibility(View.GONE);
					} else {
						CameraPosition camPos = new CameraPosition.Builder()
								.target(nevaposicion).zoom(13).build();
						CameraUpdate camUpd3 = CameraUpdateFactory
								.newCameraPosition(camPos);
						mapa.animateCamera(camUpd3);

					}
					relErrorUbicacion.setVisibility(View.VISIBLE);

					Animation animation = AnimationUtils.loadAnimation(
							LocalizacionActivity.this, R.anim.slide_in_left);
					relErrorUbicacion.startAnimation(animation);

				} else {
					Toast.makeText(getApplicationContext(),
							"No se obtuvo resultados", Toast.LENGTH_SHORT)
							.show();
					consulta.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edittext_shadow_error));
				}
			} else
				Toast.makeText(LocalizacionActivity.this,
						R.string.errNoConexion, Toast.LENGTH_SHORT).show();
			relBloquear.setVisibility(View.GONE);
		}
	}

	public class JSONParser {

		/** Receives a JSONObject and returns a list */
		public List<HashMap<String, String>> parse(JSONObject jObject) {

			Double lat = Double.valueOf(0);
			Double lng = Double.valueOf(0);
			String nombre = "";
			String direccion = "";

			HashMap<String, String> hm = new HashMap<String, String>();
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

			try {
				lat = (Double) jObject.getJSONObject("geometry")
						.getJSONObject("location").get("lat");
				lng = (Double) jObject.getJSONObject("results")
						.getJSONObject("geometry").getJSONObject("location")
						.get("lng");
				direccion = (String) jObject.getJSONObject("results").get(
						"formatted_address");
				nombre = (String) jObject.getJSONObject("results").get("name");

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			hm.put("nombre", nombre);
			hm.put("direccion", direccion);
			hm.put("lat", Double.toString(lat));
			hm.put("lng", Double.toString(lng));

			list.add(hm);

			return list;
		}

	}

	private void enviarconsultaError() {
		final InputMethodManager imm = (InputMethodManager) LocalizacionActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(consulta.getWindowToken(), 0);
		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		// id Provincia
		int IdProSel = 0;
		List<provinciaEN> listaProvincias = Herramientas
				.ObtenerProvincias(LocalizacionActivity.this);
		String Text = SpiProvincia.getSelectedItem().toString();
		boolean encontrado = false;
		if (listaProvincias != null)
			for (int i = 0; i < listaProvincias.size() && !encontrado; i++) {
				if (listaProvincias.get(i).getNombre().equals(Text)) {
					IdProSel = listaProvincias.get(i).getIdProvincia();
					encontrado = true;
				}
			}

		parametros.add(new BasicNameValuePair("idprovincia", IdProSel + ""));
		parametros.add(new BasicNameValuePair("filtro", cmpErrorCiudad
				.getText().toString()));
		peticionCoordenadas p = new peticionCoordenadas(parametros,
				LocalizacionActivity.this);
		p.execute(new String[] { UrlsServidor.PERRORCOORDENADAS });
	}

	private class peticionCoordenadas extends AsyncTask<String, Void, Void> {
		private ArrayList<NameValuePair> parametros;
		private boolean error;
		private boolean errorconexion = false;

		public peticionCoordenadas(ArrayList<NameValuePair> parametros,
				Context context) {
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
						JSONObject jobj = new JSONObject(total.toString());
						nevaposicion = new LatLng(Double.parseDouble(jobj
								.getString("latitud")), Double.parseDouble(jobj
								.getString("longitud")));
						error = false;

					} else
						error = true;
				} catch (UnknownHostException e) {
					errorconexion = true;
				} catch (Exception e) {
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (!errorconexion) {
				if (!error) {
					CameraPosition camPos = new CameraPosition.Builder()
							.target(nevaposicion).zoom(10).build();
					CameraUpdate camUpd3 = CameraUpdateFactory
							.newCameraPosition(camPos);
					mapa.animateCamera(camUpd3);
					enviarconsulta(nevaposicion);
					nuevasCoordenadas = true;
					cmpErrorCiudad.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edittext_shadow));
				} else {
					Toast.makeText(LocalizacionActivity.this,
							"Afina la busqueda en la ciudad",
							Toast.LENGTH_SHORT).show();
					cmpErrorCiudad.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edittext_shadow_error));
				}
			}
			else
				Toast.makeText(LocalizacionActivity.this,
						R.string.errNoConexion, Toast.LENGTH_SHORT).show();
		}
	}

}

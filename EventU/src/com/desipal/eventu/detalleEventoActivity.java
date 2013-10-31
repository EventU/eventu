package com.desipal.eventu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import com.desipal.eventu.Entidades.comentarioEN;
import com.desipal.eventu.Entidades.eventoEN;
import com.desipal.eventu.Extras.Herramientas;
import com.desipal.eventu.Imagenes.ImageLoader;
import com.desipal.eventu.Imagenes.galeriaActivity;
import com.desipal.eventu.PopUp.ratingpicker;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class detalleEventoActivity extends FragmentActivity {
	private eventoEN evento;
	private GoogleMap map;
	static LatLng Posicion = null;
	public static Activity act = null;

	private TextView edNombre;
	private TextView edDesc;
	private TextView txtAsistentes;
	private TextView txtDireccion;
	private TextView txtDetalleDist;
	private TextView txtDetalleDistancia;
	private TextView txtDetalleFechaInicio;
	private TextView txtDetalleFechaFin;
	private TextView txtDetalleFechaFinal;
	private Button btnOpinar;
	private ToggleButton togAsistencia;

	private ProgressBar progressBar;
	private ImageView galeria;
	private RelativeLayout relInformacion;
	private RelativeLayout layComent;
	private LinearLayout LayoutComentarios;
	private Button btnVerMasComentarios;
	private ProgressBar progressBarComentarios;
	private TextView textNoHayComentarios;
	private ProgressBar progressBarGeneral;
	public static boolean asiste;
	public static long idEvento;
	private List<comentarioEN> listaComentarios = new ArrayList<comentarioEN>();
	// /GALERIA
	private ImageLoader imglo;
	public static List<Drawable> fotosGaleria;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		try {
			act = this;
			fotosGaleria = null;
			imglo = new ImageLoader(getApplicationContext());
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.detalleevento);
			Bundle e = getIntent().getExtras();
			idEvento = e.getLong("idEvento");
			edNombre = (TextView) findViewById(R.id.txtDetalleNombre);
			edDesc = (TextView) findViewById(R.id.txtDetalleDesc);
			txtAsistentes = (TextView) findViewById(R.id.txtDetalleAsistentes);
			galeria = (ImageView) findViewById(R.id.imgDetalleImagen);
			txtDireccion = (TextView) findViewById(R.id.txtDetalleDireccion);
			txtDetalleDist = (TextView) findViewById(R.id.txtDetalleDist);
			txtDetalleDistancia = (TextView) findViewById(R.id.txtDetalleDistancia);
			txtDetalleFechaInicio = (TextView) findViewById(R.id.txtDetalleFechaInicio);
			txtDetalleFechaFin = (TextView) findViewById(R.id.txtDetalleFechaFin);
			txtDetalleFechaFinal = (TextView) findViewById(R.id.txtDetalleFechaFinal);
			togAsistencia = (ToggleButton) findViewById(R.id.togAsistencia);
			progressBar = (ProgressBar) findViewById(R.id.progressBar);
			relInformacion = (RelativeLayout) findViewById(R.id.relInformacion);
			layComent = (RelativeLayout) findViewById(R.id.layComent);
			LayoutComentarios = (LinearLayout) findViewById(R.id.LayoutComentarios);
			btnOpinar = (Button) findViewById(R.id.btnOpinar);
			progressBarGeneral = (ProgressBar) findViewById(R.id.progressBarGeneral);
			progressBarComentarios = (ProgressBar) findViewById(R.id.progressBarComentarios);
			btnVerMasComentarios = (Button) findViewById(R.id.btnVerMasComentarios);
			textNoHayComentarios = (TextView) findViewById(R.id.textNoHayComentarios);

			TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
			tabHost.setup();

			TabHost.TabSpec spec = tabHost.newTabSpec("Desc");
			spec.setContent(R.id.tab1);
			spec.setIndicator("Descripción", null);
			tabHost.addTab(spec);

			spec = tabHost.newTabSpec("Donde");
			spec.setContent(R.id.tab2);
			spec.setIndicator("Donde", null);
			tabHost.addTab(spec);

			spec = tabHost.newTabSpec("Cuando");
			spec.setContent(R.id.tab3);
			spec.setIndicator("Cuando", null);
			tabHost.addTab(spec);

			tabHost.setCurrentTab(0);

			// Boton opinar
			btnOpinar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					DialogFragment newFragment = ratingpicker.newInstance();
					newFragment.show(getSupportFragmentManager(), "dialog");
				}
			});

			// Ver mas comentarios
			btnVerMasComentarios.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						Intent i = new Intent(detalleEventoActivity.this,
								VerTodosComentariosActivity.class);
						i.putExtra("idEvento", idEvento);
						startActivity(i);
					} catch (Exception ex) {
						Toast.makeText(
								detalleEventoActivity.this,
								"No se ha sido posible recuperar los comentarios.",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("idEvento", idEvento + ""));
			LatLng loc = MainActivity.posicionActual;
			if (loc != null) {
				double latitud = loc.latitude;
				double longitud = loc.longitude;
				parametros.add(new BasicNameValuePair("latitud", String
						.valueOf(latitud)));
				parametros.add(new BasicNameValuePair("longitud", String
						.valueOf(longitud)));
			}
			String android_id = Secure.getString(this.getContentResolver(),
					Secure.ANDROID_ID);
			parametros.add(new BasicNameValuePair("idDispositivo", android_id));
			galeria.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(detalleEventoActivity.this,
							galeriaActivity.class);
					startActivity(i);
				}
			});

			togAsistencia
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							try {
								if (asiste != isChecked) {
									ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();

									long idEvento = evento.getIdEvento();
									parametros.add(new BasicNameValuePair(
											"idEvento", idEvento + ""));
									String android_id = Secure.getString(
											detalleEventoActivity.this
													.getContentResolver(),
											Secure.ANDROID_ID);
									parametros.add(new BasicNameValuePair(
											"idDispositivo", android_id));
									String asistire;
									if (isChecked)
										asistire = "true";
									else
										asistire = "false";
									parametros.add(new BasicNameValuePair(
											"asiste", asistire));
									String URL = "http://desipal.hol.es/app/eventos/asistenciaEvento.php";
									peticionAsistencia peticion = new peticionAsistencia(
											parametros,
											detalleEventoActivity.this);
									peticion.execute(new String[] { URL });

								}

							} catch (Exception ex) {
								Toast.makeText(detalleEventoActivity.this,
										"Error: " + ex.toString(),
										Toast.LENGTH_SHORT).show();
							}
						}
					});
			String URL = "http://desipal.hol.es/app/eventos/verEvento.php";
			peticionVerEvento petVerEvento = new peticionVerEvento(parametros,
					detalleEventoActivity.this);
			petVerEvento.execute(new String[] { URL });

		} catch (Exception ex) {
			Toast.makeText(this, "Error: " + ex.toString(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void verEvento() {
		progressBarGeneral.setVisibility(View.GONE);
		edNombre.setText(evento.getNombre());
		edDesc.setText(evento.getDescripcion());
		txtAsistentes.setText(evento.getAsistencia()
				+ " "
				+ detalleEventoActivity.this
						.getString(R.string.detalleEventoAsistencia));
		fotosGaleria = evento.getImagenes();
		galeria.setImageDrawable(evento.getImagenes().get(0));

		String direccion = "";
		String[] spidesc = evento.getDireccion().split(",");
		if (spidesc.length == 6)
			// Direccion=Calle,Numero Localidad(Provincia)
			direccion = spidesc[4] + ", " + spidesc[5] + " " + spidesc[3]
					+ " (" + spidesc[2] + ")";
		else if (spidesc.length == 5)
			// Direccion=Calle Localidad(Provincia)
			direccion = spidesc[4] + " " + spidesc[3] + " (" + spidesc[2] + ")";
		else
			// Direccion= Provincia
			direccion = spidesc[2];
		txtDireccion.setText(direccion);
		//
		txtDetalleDist.setText("Distancia: ");
		String distancia = new DecimalFormat("#.##").format(evento
				.getDistancia()) + " Km.";
		txtDetalleDistancia.setText(distancia);

		String fechaI = MainActivity.formatoFecha.format(evento
				.getFechaInicio());
		String fechaF = MainActivity.formatoFecha.format(evento.getFechaFin());

		// txtDetalleFechaInicio.setText(fechaI + " Durante todo el día");
		if (evento.isTodoElDia()) {
			txtDetalleFechaInicio.setText(fechaI + " Durante todo el día");
			txtDetalleFechaFin.setVisibility(View.GONE);
			txtDetalleFechaFinal.setVisibility(View.GONE);
		} else {
			txtDetalleFechaInicio.setText(fechaI);
			txtDetalleFechaFinal.setText(fechaF);
		}

		togAsistencia.setVisibility(View.VISIBLE);

		if (asiste) {
			togAsistencia.setChecked(true);
		} else {
			togAsistencia.setChecked(false);
		}
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapa)).getMap();
		map.getUiSettings().setScrollGesturesEnabled(false);
		Posicion = new LatLng(evento.getLatitud(), evento.getLongitud());
		map.addMarker(new MarkerOptions().position(Posicion).title(
				evento.getNombre()));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Posicion, 15));
		relInformacion.setVisibility(View.VISIBLE);
		LayoutComentarios.setVisibility(View.VISIBLE);
		layComent.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);

		// Ver comentarios
		if (evento.isComentarios()) {
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("idEvento", idEvento + ""));
			parametros.add(new BasicNameValuePair("elementsPerPage",
					Herramientas.ComentariosEnDetalleEvento() + ""));
			String URL = "http://desipal.hol.es/app/eventos/listaComentarios.php";

			peticionComentarios peticion = new peticionComentarios(parametros,
					detalleEventoActivity.this);
			peticion.execute(new String[] { URL });

		} else {
			LayoutComentarios.setVisibility(View.GONE);
			layComent.setVisibility(View.GONE);
		}
	}

	private void verComentarios() {
		try {
			for (int i = 0; i < listaComentarios.size(); i++) {
				LayoutComentarios.addView(obtenerVistaComentarios(
						listaComentarios, i, getApplicationContext()));
			}
			if (listaComentarios.size() == 0)
				textNoHayComentarios.setVisibility(View.VISIBLE);
			else if (listaComentarios.size() > 2)
				btnVerMasComentarios.setVisibility(View.VISIBLE);
		} catch (Exception ex) {
			textNoHayComentarios.setVisibility(View.VISIBLE);
		} finally {
			progressBarComentarios.setVisibility(View.GONE);
		}

	}

	private View obtenerVistaComentarios(List<comentarioEN> lista,
			int Elemento, Context con) {
		LayoutInflater inflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vista = inflater.inflate(R.layout.item_comentario, null);

		TextView txtFecha = (TextView) vista.findViewById(R.id.txtFecha);
		String fecha = MainActivity.formatoFecha.format(lista.get(Elemento)
				.getFecha());
		txtFecha.setText(fecha);

		RatingBar ratingBar1 = (RatingBar) vista.findViewById(R.id.ratingBar1);
		ratingBar1.setEnabled(false);
		ratingBar1.setRating((float) lista.get(Elemento).getValoracion());

		TextView txtComentario = (TextView) vista
				.findViewById(R.id.txtComentario);
		txtComentario.setText(lista.get(Elemento).getComentario());
		View vi = (View) vista.findViewById(R.id.hr);
		if (lista.size() == Elemento + 1)
			vi.setVisibility(View.GONE);

		return vista;
	}

	private void cambiarAsistencia(String asis) {

		if (togAsistencia.isChecked()) {
			asiste = true;
		} else {
			asiste = false;
		}
		txtAsistentes.setText(asis
				+ " "
				+ detalleEventoActivity.this
						.getString(R.string.detalleEventoAsistencia));

	}

	// Peticion para ver detalle evento
	public class peticionVerEvento extends AsyncTask<String, Void, Void> {

		private ArrayList<NameValuePair> parametros;

		public peticionVerEvento(ArrayList<NameValuePair> parametros,
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
						JSONObject jsEvento = new JSONObject(
								jobj.getString("evento"));
						eventoEN e = new eventoEN();

						e.setIdEvento(jsEvento.getInt("idEvento"));
						e.setIdCreador(jsEvento.getString("idCreador"));
						e.setNombre(jsEvento.getString("nombre"));
						e.setDescripcion(jsEvento.getString("descripcion"));
						e.setLatitud(jsEvento.getDouble("latitud"));
						e.setLongitud(jsEvento.getDouble("longitud"));
						e.setAsistencia(jsEvento.getInt("asistencia"));
						int temp = jsEvento.getInt("validado");
						boolean val = temp == 1 ? true : false;
						e.setValidado(val);
						temp = jsEvento.getInt("comentarios");
						val = temp == 1 ? true : false;
						e.setComentarios(val);
						e.setDireccion(jsEvento.getString("direccion"));
						e.setIdCategoria(jsEvento.getInt("idCategoria"));
						e.setDistancia(jsEvento.getDouble("distancia"));
						e.setFechaInicio(MainActivity.formatoFecha
								.parse(jsEvento.getString("fechaInicio")));
						e.setFechaFin(MainActivity.formatoFecha.parse(jsEvento
								.getString("fechaFin")));
						temp = jsEvento.getInt("todoElDia");
						val = temp == 1 ? true : false;
						e.setTodoElDia(val);
						e.setUrl(jsEvento.getString("url"));
						asiste = jobj.getBoolean("asiste");

						List<Drawable> imagenes = new ArrayList<Drawable>();

						if (!jsEvento.getString("urlImagenes").equals("null")) {
							JSONObject jurls = new JSONObject(
									jsEvento.getString("urlImagenes"));

							for (int i = 0; i < jurls.length(); i++) {
								String urlimagen = jurls
										.getString((i + 1) + "");
								Bitmap img = imglo
										.obtenerImagenesDescargadas(urlimagen);
								if (img == null) {
									InputStream ip = (InputStream) new URL(
											urlimagen).getContent();
									img = imglo.cachearImagenesDescargadas(
											urlimagen, ip);
								}
								Drawable d = new BitmapDrawable(
										detalleEventoActivity.this
												.getResources(),
										img);
								imagenes.add(d);
							}
						} else
							imagenes.add(getResources().getDrawable(
									R.drawable.default_img));
						e.setImagenes(imagenes);
						evento = e;
					}

				} catch (Exception e) {
					evento = null;
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (evento != null)
				verEvento();
		}
	}

	// Peticion para ver comentarios
	public class peticionComentarios extends AsyncTask<String, Void, Void> {
		private List<comentarioEN> comentarios = new ArrayList<comentarioEN>();

		private ArrayList<NameValuePair> parametros;

		public peticionComentarios(ArrayList<NameValuePair> parametros,
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
						JSONArray o = new JSONArray(total.toString());
						for (int i = 0; o.length() > i; i++) {
							comentarioEN e = new comentarioEN();
							JSONObject jobj = o.getJSONObject(i);
							e.setComentario(jobj.getString("comentario"));
							e.setFecha(MainActivity.formatoFecha.parse(jobj
									.getString("fecha")));
							e.setValoracion(((float) jobj
									.getDouble("valoracion")));
							this.comentarios.add(e);
						}
						listaComentarios = this.comentarios;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (listaComentarios != null)
				verComentarios();
		}
	}

	// Peticion para ver asistencia
	public class peticionAsistencia extends AsyncTask<String, Void, Void> {

		private ArrayList<NameValuePair> parametros;
		private boolean todoOK;
		private String asistencia;

		public peticionAsistencia(ArrayList<NameValuePair> parametros,
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

					if (total.toString().split(",")[0].equals("ok")) {
						todoOK = true;
						asistencia = total.toString().split(",")[1].toString();

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (!todoOK) {
				Toast.makeText(
						detalleEventoActivity.this,
						"No se ha podido contabilizar su asistencia, compruebe su conexión a internet",
						Toast.LENGTH_SHORT).show();
			}
			cambiarAsistencia(asistencia);

		}
	}

}

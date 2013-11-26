package com.desipal.eventu;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import org.json.JSONObject;

import com.desipal.eventu.Entidades.ImagenEN;
import com.desipal.eventu.Entidades.categoriaEN;
import com.desipal.eventu.Entidades.eventoEN;
import com.desipal.eventu.R;
import com.desipal.eventu.Entidades.seleccionUbicacionEN;
import com.desipal.eventu.Extras.Herramientas;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.desipal.eventu.PopUp.datepicker;
import com.desipal.eventu.PopUp.timepicker;
import com.desipal.eventu.Presentacion.listaImagenesAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ToggleButton;

public class crearEventoActivity extends FragmentActivity {
	private int ESCALAMAXIMA = 450;// Tamaño máximo de las imagenes subidas
	public static int NOTOCARIMAGEN = 0;
	public static int CREARIMAGEN = 1;
	public static int BORRARIMAGEN = 2;
	public static int NOMOSTRARIMAGEN = 3;
	public static int SELUBICACION = 10;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy hh:mm", MainActivity.currentLocale);
	private static Activity actividad;
	public static int Creacionsatisfactoria = 0;
	private boolean error = false;
	private boolean[] errores = new boolean[4];
	public static seleccionUbicacionEN[] opcionesDireccion;

	// public static List<ImagenEN> arrayImagenMod;
	public static List<ImagenEN> arrayImagen;
	double Latitud;
	double Longitud;
	String Direccion = "";

	// CONTROLES
	private Button btnInicio;
	private Button btnSubirImagen;
	private Button btnCrearEvento;
	private EditText edNombre;
	private EditText edDesc;
	private EditText edFechaIni;
	private EditText edFechaFin;
	private EditText edHoraIni;
	private EditText edHoraFin;
	private CheckBox chTodoElDia;
	private Spinner spiCategoria;
	private Button btnUbicacion;
	private ToggleButton tgComentarios;
	private TableRow filaFin;
	private TableRow filaTexFin;
	private LinearLayout relImagenes;
	private GoogleMap mapaLocalizacion;
	private RelativeLayout relativeMapa;
	private RelativeLayout relBloquear;
	private EditText[] editError = new EditText[] { edNombre, edDesc,
			edFechaIni, edFechaFin };

	// IdCategoria
	int IdCategoriaSel = 0;
	boolean errorspiner = true;
	private List<categoriaEN> listaCategorias = null;
	private int NUMTOTALFOTOS = 5;
	public static int contaFotos = 0;
	private eventoEN EventoModificar = new eventoEN();
	private boolean esModificar = false;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		contaFotos = 0;
		getWindow().setBackgroundDrawableResource(android.R.color.black);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.crearevento);
		relBloquear = (RelativeLayout) findViewById(R.id.relBloquear);
		// Modificar
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			relBloquear.setVisibility(View.VISIBLE);
			EventoModificar.setIdEvento(extras.getInt("idEvento"));
			esModificar = true;
			// PETICION
			ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
			parametros.add(new BasicNameValuePair("idEvento", EventoModificar
					.getIdEvento() + ""));
			parametros.add(new BasicNameValuePair("idDispositivo",
					MainActivity.IDDISPOSITIVO));
			parametros.add(new BasicNameValuePair("latitud",
					MainActivity.posicionActual.latitude + ""));
			parametros.add(new BasicNameValuePair("longitud",
					MainActivity.posicionActual.longitude + ""));
			String URL = "http://desipal.hol.es/app/eventos/verEvento.php";
			peticionVerEvento petVerEvento = new peticionVerEvento(parametros,
					crearEventoActivity.this);
			petVerEvento.execute(new String[] { URL });
		}
		actividad = this;
		arrayImagen = new ArrayList<ImagenEN>();
		// arrayImagenMod = new ArrayList<ImagenEN>();

		btnInicio = (Button) findViewById(R.id.btnInicio);
		btnSubirImagen = (Button) findViewById(R.id.btnSubirImagen);
		btnCrearEvento = (Button) findViewById(R.id.btnCrearEvento);

		edNombre = (EditText) findViewById(R.id.editNombre);
		edDesc = (EditText) findViewById(R.id.editDescrip);
		edFechaIni = (EditText) findViewById(R.id.editFechaInicio);
		edFechaFin = (EditText) findViewById(R.id.editFechaFin);
		edHoraIni = (EditText) findViewById(R.id.editHoraInicio);
		edHoraFin = (EditText) findViewById(R.id.editHoraFin);

		chTodoElDia = (CheckBox) findViewById(R.id.chTodoElDia);
		spiCategoria = (Spinner) findViewById(R.id.spiCategorias);

		btnUbicacion = (Button) findViewById(R.id.btnUbicacion);
		tgComentarios = (ToggleButton) findViewById(R.id.togComentarios);

		filaFin = (TableRow) findViewById(R.id.table2Row4);
		filaTexFin = (TableRow) findViewById(R.id.table2Row3);
		relImagenes = (LinearLayout) findViewById(R.id.relImagenes);
		relativeMapa = (RelativeLayout) findViewById(R.id.relativeMapa);
		mapaLocalizacion = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapaLocalizacion)).getMap();
		mapaLocalizacion.getUiSettings().setScrollGesturesEnabled(false);
		
		// Spinner catgorias
		listaCategorias = Herramientas
				.Obtenercategorias(crearEventoActivity.this);
		String a[] = new String[listaCategorias.size()];
		for (int i = 0; i < listaCategorias.size(); i++) {
			a[i] = listaCategorias.get(i).getTexto();
		}
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
				crearEventoActivity.this, R.layout.spinner_item, a);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter2);
		spiCategoria.setEnabled(true);
		btnInicio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_right);
			}
		});

		chTodoElDia.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				Handler handler = new Handler();
				if (isChecked) {
					AlphaAnimation anim_alpha = new AlphaAnimation(1, 0);
					anim_alpha.setDuration(200);
					filaFin.startAnimation(anim_alpha);
					filaTexFin.startAnimation(anim_alpha);
					handler.postDelayed(new Runnable() {
						public void run() {
							filaFin.setVisibility(View.GONE);
							filaTexFin.setVisibility(View.GONE);
							int altura = filaFin.getHeight()
									+ filaTexFin.getHeight();
							TranslateAnimation anim_trans = new TranslateAnimation(
									0, 0, altura, 0);
							anim_trans.setDuration(200);
							relImagenes.startAnimation(anim_trans);
						}
					}, 200);

				} else {
					int altura = filaFin.getHeight() + filaTexFin.getHeight();
					TranslateAnimation anim_trans = new TranslateAnimation(0,
							0, 0, altura);
					anim_trans.setDuration(200);
					relImagenes.startAnimation(anim_trans);
					handler.postDelayed(new Runnable() {
						public void run() {
							filaFin.setVisibility(View.VISIBLE);
							filaTexFin.setVisibility(View.VISIBLE);
							AlphaAnimation anim_alpha = new AlphaAnimation(0, 1);
							anim_alpha.setDuration(200);
							filaFin.startAnimation(anim_alpha);
							filaTexFin.startAnimation(anim_alpha);
						}
					}, 220);
				}
			}
		});

		edFechaIni.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new datepicker();
				((datepicker) newFragment).establecerCampo(edFechaIni);
				newFragment.show(getSupportFragmentManager(), "DatePicker");
			}
		});

		edFechaFin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new datepicker();
				((datepicker) newFragment).establecerCampo(edFechaFin);
				newFragment.show(getSupportFragmentManager(), "DatePicker");
			}
		});

		edHoraIni.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new timepicker();
				((timepicker) newFragment).establecerCampo(edHoraIni);
				newFragment.show(getSupportFragmentManager(), "TimePicker");
			}
		});

		edHoraFin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new timepicker();
				((timepicker) newFragment).establecerCampo(edHoraFin);
				newFragment.show(getSupportFragmentManager(), "TimePicker");
			}
		});
		btnUbicacion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplication(),
						LocalizacionActivity.class);
				startActivityForResult(intent, SELUBICACION);
			}
		});

		btnSubirImagen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (contaFotos != NUMTOTALFOTOS) {
					Intent pickPhoto = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(pickPhoto, 1);
					if (arrayImagen.size() == 0)
						Toast.makeText(
								actividad,
								"La primera imagen seleccionada se utilizara como imagen principal",
								Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(actividad,
							"Se ha cumplido el maximo de fotos",
							Toast.LENGTH_LONG).show();
			}
		});

		btnCrearEvento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnCrearEvento.setEnabled(false);
				crearEvento();
			}
		});
	}

	// //////// SELECCIONAR IMAGEN
	protected void onActivityResult(int requestCode, int resultCode,
			Intent returnedIntent) {
		super.onActivityResult(requestCode, resultCode, returnedIntent);

		switch (requestCode) {
		case 1:// IMAGENES
			if (resultCode == RESULT_OK) {
				try {
					Uri selectedImage = returnedIntent.getData();
					Bitmap bmp = Herramientas.reescalarBitmapPorUri(
							selectedImage, crearEventoActivity.this,
							ESCALAMAXIMA);
					contaFotos++;
					arrayImagen.add(new ImagenEN("", bmp, CREARIMAGEN));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (arrayImagen.size() == 0)
				Toast.makeText(this, "No ha elegido ninguna imágen",
						Toast.LENGTH_SHORT).show();
			refrescarLista();
			break;

		case 10:// UBICACION
			if (resultCode == RESULT_OK) {
				Bundle extras = returnedIntent.getExtras();
				Latitud = extras.getDouble("latitud");
				Longitud = extras.getDouble("longitud");
				LatLng local = new LatLng(Latitud, Longitud);
				Direccion = extras.getString("direccion");
				mapaLocalizacion.moveCamera(CameraUpdateFactory.newLatLngZoom(
						local, 14));
				mapaLocalizacion.addMarker(new MarkerOptions().position(local));
			}
			break;
		}
	}

	@SuppressWarnings("deprecation")
	protected boolean crearEvento() {
		try {
			relBloquear.setVisibility(View.VISIBLE);
			eventoEN evento = new eventoEN();

			editError[0] = edNombre;
			editError[1] = edDesc;
			editError[2] = edFechaIni;
			editError[3] = edFechaFin;

			if (edNombre.getText().length() > 0) {
				errores[0] = false;
				evento.setNombre(edNombre.getText().toString());
			} else
				errores[0] = true;
			if (edDesc.getText().length() > 0) {
				errores[1] = false;
				evento.setDescripcion(edDesc.getText().toString());
			} else
				errores[1] = true;

			// id Categoria
			String Text = spiCategoria.getSelectedItem().toString();
			boolean encontrado = false;
			if (listaCategorias != null)
				for (int i = 0; i < listaCategorias.size() && !encontrado; i++) {
					if (listaCategorias.get(i).getTexto().equals(Text)) {
						IdCategoriaSel = listaCategorias.get(i)
								.getIdCategoria();
						encontrado = true;
						error = false;
					}
				}
			if (IdCategoriaSel == 1) {
				errorspiner = true;
				error = true;
			} else
				errorspiner = false;

			// COMPROBAR UBICACION
			if (Latitud == 0 || Longitud == 0) {
				btnUbicacion.setBackgroundDrawable(getResources().getDrawable(
						R.anim.anim_boton_rojo));
				error = true;
			} else {
				spiCategoria.setBackgroundDrawable(getResources().getDrawable(
						R.anim.anim_boton));
				evento.setDireccion(Direccion);
				evento.setLatitud(Latitud);
				evento.setLongitud(Longitud);
				error = false;
			}

			if (tgComentarios.isChecked())
				evento.setComentarios(true);
			else
				evento.setComentarios(false);

			if (chTodoElDia.isChecked()) {
				if (edFechaIni.getText().length() > 0) {
					errores[2] = false;// Quitamos el error de FechaInicio
					if (edHoraIni.getText().length() > 0)
						evento.setFechaInicio(dateFormat.parse(edFechaIni
								.getText().toString()
								+ " "
								+ edHoraIni.getText().toString()));
					else
						evento.setFechaInicio(dateFormat.parse(edFechaIni
								.getText().toString() + " 00:00"));
				} else
					errores[2] = true;
				evento.setTodoElDia(true);
				errores[3] = false;// Quitamos el error de FechaFin
			} else {
				Date dtFechaIni = new Date();
				Date dtFechaFin = new Date();

				if (edFechaIni.getText().length() > 0) {
					if (edHoraIni.getText().length() > 0) {
						evento.setFechaInicio(dateFormat.parse(edFechaIni
								.getText().toString()
								+ " "
								+ edHoraIni.getText().toString()));
						dtFechaIni = evento.getFechaInicio();
					} else
						evento.setFechaInicio(dateFormat.parse(edFechaIni
								.getText().toString() + " 00:00"));
					errores[2] = false;// Quitamos el error de FechaInicio
				} else
					errores[2] = true;
				if (edFechaFin.getText().length() > 0) {
					if (edHoraFin.getText().length() > 0) {
						evento.setFechaFin(dateFormat.parse(edFechaFin
								.getText().toString()
								+ " "
								+ edHoraFin.getText().toString()));
						dtFechaFin = evento.getFechaFin();
					} else
						evento.setFechaFin(dateFormat.parse(edFechaFin
								.getText().toString() + " 00:00"));
					errores[3] = false;// Quitamos el error de FechaFin
				} else
					errores[3] = true;

				if (dtFechaFin.before(dtFechaIni)) {
					errores[2] = true;
					errores[3] = true;
				}
				evento.setTodoElDia(false);
			}
			for (int i = 0; i < errores.length; i++) {
				if (errores[i]) {
					error = true;
					editError[i].setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.camporojo));
				} else
					editError[i].setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.campo));
			}

			if (errorspiner) {
				spiCategoria.setBackgroundDrawable(getResources().getDrawable(
						R.anim.anim_boton_rojo));
				error = true;
			} else {
				spiCategoria.setBackgroundDrawable(getResources().getDrawable(
						R.anim.anim_boton));
				evento.setIdCategoria(IdCategoriaSel);
			}
			if (!error)
				try {
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

					String android_id = Secure.getString(
							actividad.getContentResolver(), Secure.ANDROID_ID);
					nameValuePairs.add(new BasicNameValuePair("idCreador",
							android_id));
					nameValuePairs.add(new BasicNameValuePair("nombre", evento
							.getNombre()));
					nameValuePairs.add(new BasicNameValuePair("descripcion",
							evento.getDescripcion()));
					nameValuePairs.add(new BasicNameValuePair("latitud", String
							.valueOf(evento.getLatitud())));
					nameValuePairs.add(new BasicNameValuePair("longitud",
							String.valueOf(evento.getLongitud())));
					nameValuePairs.add(new BasicNameValuePair("direccion",
							evento.getDireccion()));
					if (evento.isComentarios())
						nameValuePairs.add(new BasicNameValuePair(
								"comentarios", "1"));
					else
						nameValuePairs.add(new BasicNameValuePair(
								"comentarios", "0"));
					nameValuePairs.add(new BasicNameValuePair("idCategoria",
							evento.getIdCategoria() + ""));

					// FECHAS
					String fechaInicio = edFechaIni.getText().toString() + " "
							+ edHoraIni.getText().toString();
					nameValuePairs.add(new BasicNameValuePair("fechaInicio",
							fechaInicio));
					if (evento.isTodoElDia())
						nameValuePairs.add(new BasicNameValuePair("todoElDia",
								"1"));
					else {
						String fechaFin = edFechaFin.getText().toString() + " "
								+ edHoraFin.getText().toString();
						nameValuePairs.add(new BasicNameValuePair("fechaFin",
								fechaFin));
						nameValuePairs.add(new BasicNameValuePair("todoElDia",
								"0"));
					}
					crearEventoActivity.Creacionsatisfactoria = 1;
					String URL = "";
					if (esModificar) {
						URL = "http://desipal.hol.es/app/eventos/modificar.php";
						nameValuePairs.add(new BasicNameValuePair("idEvento",
								EventoModificar.getIdEvento() + ""));
						int i = 1;
						for (ImagenEN imagen : arrayImagen) {
							if (imagen.getEstado() == CREARIMAGEN) {
								ByteArrayOutputStream bao = new ByteArrayOutputStream();
								imagen.getImagen().compress(
										Bitmap.CompressFormat.JPEG, 100, bao);
								byte[] ba = bao.toByteArray();
								String ba1 = Base64.encodeToString(ba,
										Base64.DEFAULT);
								nameValuePairs.add(new BasicNameValuePair(
										"imagen" + i, CREARIMAGEN + ";" + ba1));
								i++;
							}
							if (imagen.getEstado() == BORRARIMAGEN) {
								nameValuePairs.add(new BasicNameValuePair(
										"imagen" + i, BORRARIMAGEN + ";"
												+ imagen.getUrl()));
								i++;
							}
						}
					} else {
						int i = 1;
						for (ImagenEN imagen : arrayImagen) {
							if (imagen.getEstado() == CREARIMAGEN) {
								ByteArrayOutputStream bao = new ByteArrayOutputStream();
								imagen.getImagen().compress(
										Bitmap.CompressFormat.JPEG, 80, bao);
								byte[] ba = bao.toByteArray();
								String ba1 = Base64.encodeToString(ba,
										Base64.DEFAULT);
								nameValuePairs.add(new BasicNameValuePair(
										"imagen" + i, ba1));
								i++;
							}
						}
						URL = "http://desipal.hol.es/app/eventos/alta.php";
					}
					creacionEvento peticion = new creacionEvento(nameValuePairs);
					peticion.execute(new String[] { URL });

				} catch (Exception e) {
					btnCrearEvento.setEnabled(true);
					Toast.makeText(actividad, "Error:" + e.getMessage(),
							Toast.LENGTH_LONG).show();
					error = true;
				}
			else {
				Toast.makeText(actividad, "Rellene todos los campos",
						Toast.LENGTH_SHORT).show();
				btnCrearEvento.setEnabled(true);
				relBloquear.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			btnCrearEvento.setEnabled(true);
			Toast.makeText(actividad, "Error:" + e.getMessage(),
					Toast.LENGTH_LONG).show();
			relBloquear.setVisibility(View.GONE);
			error = true;
		}

		return error;
	}

	public static void refrescarLista() {
		try {
			GridView gridview = (GridView) actividad
					.findViewById(R.id.listImagenes);
			gridview.setAdapter(new listaImagenesAdapter(actividad, arrayImagen));
			float altura = Herramientas.convertDpToPixel(55 * contaFotos,
					actividad);// Tamaño de la imagen
			if (altura > 0)
				gridview.setVisibility(View.VISIBLE);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					(int) altura);
			gridview.setLayoutParams(params);
		} catch (Exception e) {
			e.toString();
		}
	}

	public class creacionEvento extends AsyncTask<String, Void, Void> {

		private ArrayList<NameValuePair> parametros;

		public creacionEvento(ArrayList<NameValuePair> parametros) {
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
					if (total.toString().equals("ok")) {
						crearEventoActivity.Creacionsatisfactoria = 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
					crearEventoActivity.Creacionsatisfactoria = -1;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			relBloquear.setVisibility(View.GONE);
			if (crearEventoActivity.Creacionsatisfactoria == 0) {
				arrayImagen.clear();
				finish();
				if (esModificar)
					Toast.makeText(crearEventoActivity.this,
							"Evento modificado", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(crearEventoActivity.this, "Evento creado",
							Toast.LENGTH_SHORT).show();
				// Al crear evento fuerzas el refresco de lista de Mis Eventos
				MisEventos.view = null;
			} else {
				if (crearEventoActivity.Creacionsatisfactoria == 1)
					Toast.makeText(crearEventoActivity.this,
							"Error al crear evento", Toast.LENGTH_SHORT).show();
				else if (crearEventoActivity.Creacionsatisfactoria == -1)
					Toast.makeText(
							crearEventoActivity.this,
							"Imposible conectar con el servidor, intentalo pasados unos minutos",
							Toast.LENGTH_SHORT).show();
				btnCrearEvento.setEnabled(true);
			}
		}
	}

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

						if (!jsEvento.getString("urlImagenes").equals("null")) {
							JSONObject jurls = new JSONObject(
									jsEvento.getString("urlImagenes"));

							for (int i = 0; i < jurls.length(); i++) {
								String urlimagen = jurls
										.getString((i + 1) + "");
								Bitmap img = MainActivity.imageLoader
										.obtenerImagenesDescargadas(urlimagen);
								if (img == null) {
									InputStream ip = (InputStream) new URL(
											urlimagen).getContent();
									img = MainActivity.imageLoader
											.cachearImagenesDescargadas(
													urlimagen, ip);
								}
								arrayImagen.add(new ImagenEN(urlimagen, img,
										NOTOCARIMAGEN));
								// arrayImagenMod.add(new ImagenEN(urlimagen,
								// img,NOTOCARIMAGEN));
							}
						}
						contaFotos = arrayImagen.size();
						EventoModificar = e;
					}

				} catch (Exception e) {
					EventoModificar = null;
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (EventoModificar != null)
				verEventoParaModificar();
		}
	}

	private void verEventoParaModificar() {
		try {
			edNombre.setText(EventoModificar.getNombre());
			edDesc.setText(EventoModificar.getDescripcion());
			// Ubicacion
			Latitud = EventoModificar.getLatitud();
			Longitud = EventoModificar.getLongitud();
			Direccion = EventoModificar.getDireccion();
			LatLng local = new LatLng(Latitud, Longitud);

			mapaLocalizacion.moveCamera(CameraUpdateFactory.newLatLngZoom(
					local, 14));
			mapaLocalizacion.addMarker(new MarkerOptions().position(local));
			// Fechas
			Date fecha = EventoModificar.getFechaInicio();
			Calendar cal = Calendar.getInstance();
			cal.setTime(fecha);
			SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm",
					MainActivity.currentLocale);
			if (EventoModificar.isTodoElDia()) {
				chTodoElDia.setChecked(true);
				String fechaini = MainActivity.formatoFechaMostrar
						.format(EventoModificar.getFechaInicio());
				edFechaIni.setText(fechaini);
				String horaini = formatoHora.format(EventoModificar
						.getFechaInicio());
				edHoraIni.setText(horaini);
			} else {
				Date fechaFin = EventoModificar.getFechaFin();
				Calendar calfin = Calendar.getInstance();
				String fechaini = MainActivity.formatoFechaMostrar
						.format(EventoModificar.getFechaInicio());
				edFechaIni.setText(fechaini);
				String horaini = formatoHora.format(EventoModificar
						.getFechaInicio());
				edHoraIni.setText(horaini);

				calfin.setTime(fechaFin);
				String fechafin = MainActivity.formatoFechaMostrar
						.format(EventoModificar.getFechaFin());
				edFechaFin.setText(fechafin);
				String horafin = formatoHora.format(EventoModificar
						.getFechaFin());
				edHoraFin.setText(horafin);
			}
			spiCategoria.setSelection(EventoModificar.getIdCategoria());
			if (EventoModificar.isComentarios())
				tgComentarios.setSelected(true);
			// Pendiente realizar imagenes

			refrescarLista();
			btnCrearEvento.setText("Modificar");
			relBloquear.setVisibility(View.GONE);
		} catch (Exception e) {
			e.toString();
		}
	}
}

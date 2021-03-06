package com.desipal.eventu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import com.desipal.eventu.Extras.InicioMuestraDrawer;
import com.desipal.eventu.Extras.UrlsServidor;
import com.desipal.eventu.Extras.categorias;
import com.desipal.eventu.Imagenes.ImageLoader;
import com.desipal.eventu.Presentacion.EntryItem;
import com.desipal.eventu.Presentacion.Item;
import com.desipal.eventu.Presentacion.SectionItem;
import com.desipal.eventu.Presentacion.adaptadorListaDrawer;
import com.desipal.eventu.Servicios.EstadoConexion;
import com.desipal.eventu.Servicios.Localizacion;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends ActionBarActivity implements
		OnQueryTextListener {

	ArrayList<Item> items = new ArrayList<Item>();

	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private int PosicionActual;
	public static int ELEMENTOSLISTA = 20;
	public static Locale currentLocale = new Locale("es", "ES");
	public static String IDDISPOSITIVO = null;
	public static boolean errorServicios = false;
	public static boolean estadoConexion = false;
	public static String fCategorias = "categorias.json";
	public static String fProvincias = "provincias.json";
	public static SimpleDateFormat formatoFecha = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", currentLocale);
	public static SimpleDateFormat formatoFechaMostrar = new SimpleDateFormat(
			"dd/MM/yyyy", MainActivity.currentLocale);
	public static LatLng posicionActual = new LatLng(0, 0);
	private Intent servicio;
	public static ImageLoader imageLoader;// Cache de Imagenes General
	public static String queryFiltro = "";// Query necesaria para el filtro
	private MenuItem filAvanzadoItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		EstadoConexion e = new EstadoConexion();
		e.onReceive(this, null);
		// Localizacion
		servicio = new Intent(this, Localizacion.class);
		startService(servicio);

		// Peticion de categorias
		String URL = UrlsServidor.CATEGORIAS;
		categorias peticion = new categorias(MainActivity.this);
		peticion.execute(new String[] { URL });
		// //////////////
		IDDISPOSITIVO = Settings.Secure.getString(this.getContentResolver(),
				Settings.Secure.ANDROID_ID);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setLogo(R.drawable.btnizquierdo);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setTitle("EventU");

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// ///
		items.add(new SectionItem("Inicio"));
		// Eventos pr�ximos=Inicio
		items.add(new EntryItem(R.drawable.ic_menu_eventprox,
				"Eventos pr�ximos", "Eventos con fecha  m�s cercana"));
		items.add(new EntryItem(R.drawable.ic_menu_eventcerca, "Cerca de ti",
				"Descubre lo que pasa a tu alrededor"));
		items.add(new EntryItem(R.drawable.ic_menu_search, "Buscar eventos",
				"Busqueda avanzada de eventos"));

		items.add(new SectionItem("Mi perfil"));
		items.add(new EntryItem(R.drawable.ic_menu_misfavoritos,
				"Mis favoritos", "Visualiza tus eventos favoritos"));
		items.add(new EntryItem(R.drawable.ic_menu_miseventos, "Mis eventos",
				"Visualiza y modifica tus eventos"));
		items.add(new EntryItem(R.drawable.ic_menu_nuevoevento, "Crear evento",
				"Comparte un nuevo evento"));
		items.add(new SectionItem("EventU"));
		items.add(new EntryItem(R.drawable.ic_menu_info,
				"Acerca de...", "Informaci�n de la aplicaci�n"));
		items.add(new EntryItem(R.drawable.ic_menu_help, "Sugerencias",
				"Envianos dudas o sugerencias"));
		adaptadorListaDrawer adapter = new adaptadorListaDrawer(this, items);
		drawerList.setAdapter(adapter);

		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				boolean esActividad = false;
				// Usar cuando sea un Fragment
				Fragment fragment = null;
				// Usar cuando se llame a una actividad
				Intent intent = null;
				if (PosicionActual != position) {
					switch (position) {
					case 1:
						getSupportActionBar().setTitle("Eventos pr�ximos");
						fragment = new EventosProximos();
						if (filAvanzadoItem != null)
							filAvanzadoItem.setVisible(false);

						establecerAnchoMenu(false);
						break;
					case 2:
						getSupportActionBar().setTitle("Cerca de ti");
						fragment = new EventosCerca();
						filAvanzadoItem.setVisible(false);
						establecerAnchoMenu(true);
						break;
					case 3:
						getSupportActionBar().setTitle("Buscar eventos");
						fragment = new filtroAvanzado();
						filAvanzadoItem.setVisible(true);
						establecerAnchoMenu(false);
						break;
					case 5:
						getSupportActionBar().setTitle("Mis favoritos");
						fragment = new MisFavoritos();
						filAvanzadoItem.setVisible(false);
						establecerAnchoMenu(false);
						break;
					case 6:
						getSupportActionBar().setTitle("Mis eventos");
						fragment = new MisEventos();
						filAvanzadoItem.setVisible(false);
						establecerAnchoMenu(false);
						break;
					case 7:
						intent = new Intent(getApplication(),
								crearEventoActivity.class);
						esActividad = true;
						break;
					case 9:
						getSupportActionBar().setTitle("Acerca de...");
						fragment = new AcercaDe();
						filAvanzadoItem.setVisible(false);
						establecerAnchoMenu(false);
						break;
					case 10:
						getSupportActionBar().setTitle("Sugerencias");
						fragment = new Sugerencias();
						filAvanzadoItem.setVisible(false);
						establecerAnchoMenu(false);
						break;
					}
					if (!esActividad) {
						FragmentManager fragmentManager = getSupportFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, fragment).commit();
						PosicionActual = position;
					} else
						startActivity(intent);
				}
				drawerList.setItemChecked(position, true);

				drawerLayout.closeDrawer(drawerList);
			}

		});

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_navigation_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				ActivityCompat.invalidateOptionsMenu(MainActivity.this);
			}

			public void onDrawerOpened(View drawerView) {
				ActivityCompat.invalidateOptionsMenu(MainActivity.this);
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);

		// Marca el inicio de la aplicacion y que se muestre el inicio
		PosicionActual = 0;
		drawerList.performItemClick(null, 1, 0);
		PosicionActual = 1;
		// Iniciamos cache general de imagenes
		imageLoader = new ImageLoader(MainActivity.this);

		InicioMuestraDrawer Pinicion = new InicioMuestraDrawer(
				MainActivity.this, drawerLayout, drawerList);
		Pinicion.run();

		// Funcion para establecer el ancho del gesto para desplegar el menu
		this.establecerAnchoMenu(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		filAvanzadoItem = menu.findItem(R.id.btn_filtro_avanzado);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean retorno;
		if (drawerToggle.onOptionsItemSelected(item)) {
			retorno = true;
		} else {
			switch (item.getItemId()) {
			case R.id.btn_filtro_avanzado:
				filtroAvanzado.recoger();
				retorno = true;
				break;
			default:
				retorno = super.onOptionsItemSelected(item);
			}
		}
		return retorno;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean menuAbierto = drawerLayout.isDrawerOpen(drawerList);

		if (PosicionActual != 3)
			menu.findItem(R.id.btn_filtro_avanzado).setVisible(false);
		else {
			if (menuAbierto)
				menu.findItem(R.id.btn_filtro_avanzado).setVisible(false);
			else
				menu.findItem(R.id.btn_filtro_avanzado).setVisible(true);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && PosicionActual == 2)
			establecerAnchoMenu(false);
		if (keyCode == KeyEvent.KEYCODE_BACK && PosicionActual != 1) {
			getSupportActionBar().setTitle("Eventos pr�ximos");
			Fragment fragment = new EventosProximos();
			if (filAvanzadoItem != null)
				filAvanzadoItem.setVisible(false);
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
			PosicionActual = 1;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
		establecerAnchoMenu(false);
	}

	protected void establecerAnchoMenu(boolean escercadeti) {
		// Extender el evento para mostrar el men�
		try {

			Field mDragger = drawerLayout.getClass().getDeclaredField(
					"mLeftDragger");// mRightDragger for right obvio usly
			mDragger.setAccessible(true);
			ViewDragHelper draggerObj = (ViewDragHelper) mDragger
					.get(drawerLayout);
			Display display = getWindowManager().getDefaultDisplay();
			@SuppressWarnings("deprecation")
			int ancho = display.getWidth();
			Field mEdgeSize = draggerObj.getClass().getDeclaredField(
					"mEdgeSize");
			mEdgeSize.setAccessible(true);
			if (!escercadeti)
				mEdgeSize.setInt(draggerObj, ancho / 3);
			else
				mEdgeSize.setInt(draggerObj, 20);
		} catch (NoSuchFieldException en) {
			en.printStackTrace();
		} catch (IllegalArgumentException en) {
			en.printStackTrace();
		} catch (IllegalAccessException en) {
			en.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(servicio);
		EventosProximos.view = null;
		EventosCerca.view = null;
		MisEventos.view = null;
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		if (!text.equals("")) {
			queryFiltro = text;
			getSupportActionBar().setTitle("Buscar eventos");
			Fragment filAvanzado = new filtroAvanzado();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, filAvanzado).commit();
		}
		return false;
	}
}

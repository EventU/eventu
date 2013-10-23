package com.desipal.eventu;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.widget.ListView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.desipal.eventu.Extras.categorias;
import com.desipal.eventu.Localizacion.Localizacion;
import com.desipal.eventu.PopUp.QuickAction;
import com.desipal.eventu.Presentacion.EntryItem;
import com.desipal.eventu.Presentacion.Item;
import com.desipal.eventu.Presentacion.SectionItem;
import com.desipal.eventu.Presentacion.adaptadorListaDrawer;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends ActionBarActivity {

	ArrayList<Item> items = new ArrayList<Item>();
	
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private int PosicionActual;
	public static int ELEMENTOSLISTA = 8;
	public static Locale currentLocale = new Locale("es", "ES");
	public static String IDDISPOSITIVO = null;
	public static boolean errorServicios = false;
	public static String fCategorias = "categorias.json";
	public static SimpleDateFormat formatoFecha = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", currentLocale);
	public static LatLng posicionActual = new LatLng(0, 0);
	private Intent servicio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Localizacion
		servicio = new Intent(this, Localizacion.class);
		startService(servicio);

		// Peticion de categorias
		String URL = "http://desipal.hol.es/app/eventos/categorias.php";
		categorias peticion = new categorias(MainActivity.this);
		peticion.execute(new String[] { URL });
		// //////////////
		IDDISPOSITIVO = Settings.Secure.getString(this.getContentResolver(),
				Settings.Secure.ANDROID_ID);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setLogo(R.drawable.btnizquierdo);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setTitle("");

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
		items.add(new SectionItem("Incio"));
		//Eventos próximos=Inicio
        items.add(new EntryItem(R.drawable.ic_menu_eventprox,"Eventos próximos", "Eventos con fecha  mas cercana"));
        items.add(new EntryItem(R.drawable.ic_menu_eventcerca,"Cerca de ti", "Descubre lo que pasa a tu alrededor"));
        
        items.add(new SectionItem("Mi perfil"));
        items.add(new EntryItem(R.drawable.ic_menu_miseventos,"Mis eventos", "Visualiza y modifica tus eventos"));
        items.add(new EntryItem(R.drawable.ic_menu_nuevoevento,"Crear evento", "Comparte un nuevo evento"));

        adaptadorListaDrawer adapter = new adaptadorListaDrawer(this, items);
        drawerList.setAdapter(adapter);
        
        
        ////////////////////////////////////////
		/*drawerList.setAdapter(new ArrayAdapter<String>(getSupportActionBar()
				.getThemedContext(), android.R.layout.simple_list_item_1,
				opcionesMenu));
		 */
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
						fragment = new Fragment1();
						break;
					case 2:
						fragment = new Fragment2();
						break;
					case 4:
						fragment = new Fragment3();
						break;
					case 5:
						intent = new Intent(getApplication(),
								crearEventoActivity.class);
						esActividad = true;
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
				getSupportActionBar().setTitle("");
				ActivityCompat.invalidateOptionsMenu(MainActivity.this);
			}

			public void onDrawerOpened(View drawerView) {
				// getSupportActionBar().setTitle(R.string.quedeseaver);
				// <string name="quedeseaver">¿Que desea ver?</string>
				ActivityCompat.invalidateOptionsMenu(MainActivity.this);
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);

		// Marca el inicio de la aplicacion y que se muestre el inicio
		PosicionActual = 0;
		drawerList.performItemClick(null, 1, 0);
		PosicionActual = 1;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();

			break;
		case R.id.action_search:
			View view = findViewById(R.id.action_search);
			   QuickAction mQuickAction = new QuickAction(MainActivity.this,
			     MainActivity.this);
			   mQuickAction.show(view);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean menuAbierto = drawerLayout.isDrawerOpen(drawerList);

		if (menuAbierto)
			menu.findItem(R.id.action_search).setVisible(false);
		else
			menu.findItem(R.id.action_search).setVisible(true);

		return super.onPrepareOptionsMenu(menu);
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(servicio);
		Fragment1.view = null;
		Fragment2.view = null;
		Fragment3.view = null;
	}
}

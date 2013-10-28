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
import com.desipal.eventu.Entidades.comentarioEN;
import com.desipal.eventu.Presentacion.verTodosComentariosAdapter;
import com.desipal.eventu.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

public class VerTodosComentariosActivity extends FragmentActivity {

	public long idEvento;
	boolean bloquearPeticion = false;
	private List<comentarioEN> listaComentarios = new ArrayList<comentarioEN>();
	private int page = 0;
	private PullToRefreshListView listComentarios;
	private boolean finlista = false;

	verTodosComentariosAdapter adaptador = new verTodosComentariosAdapter(this,
			listaComentarios);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(android.R.color.black);

		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.ver_todos_comentarios);
			listComentarios = (PullToRefreshListView) findViewById(R.id.listComentarios);
			listComentarios
					.setOnRefreshListener(new OnRefreshListener<ListView>() {
						@Override
						public void onRefresh(
								PullToRefreshBase<ListView> refreshView) {
							listaComentarios.clear();
							adaptador.notifyDataSetChanged();
							// hacerPeticion();
						}
					});

			Bundle e = getIntent().getExtras();
			idEvento = e.getLong("idEvento");

			listComentarios.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					if (listaComentarios.size() % MainActivity.ELEMENTOSLISTA == 0
							&& !bloquearPeticion
							&& (firstVisibleItem + visibleItemCount) == totalItemCount
							&& !finlista) {
						hacerPeticion();
					}
				}
			});
			adaptador = new verTodosComentariosAdapter(this, listaComentarios);
			listComentarios.setAdapter(adaptador);
		} catch (Exception ex) {
			Toast.makeText(this, "Error: " + ex.toString(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void hacerPeticion() {
		bloquearPeticion = true;
		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("idEvento", idEvento + ""));
		parametros.add(new BasicNameValuePair("elementsPerPage",
				MainActivity.ELEMENTOSLISTA + ""));
		page = ((int) listaComentarios.size() / MainActivity.ELEMENTOSLISTA) + 1;
		parametros.add(new BasicNameValuePair("page", page + ""));
		String URL = "http://desipal.hol.es/app/eventos/listaComentarios.php";
		peticionComentarios peticion = new peticionComentarios(parametros,
				VerTodosComentariosActivity.this);
		peticion.execute(new String[] { URL });
	}

	/* Peticion al servidor para recuperar todos los comentarios */
	public class peticionComentarios extends AsyncTask<String, Void, Void> {

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
							listaComentarios.add(e);
						}
					} else if (listaComentarios.size() > 0)
						finlista = true;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			bloquearPeticion = false;
			adaptador.notifyDataSetChanged();
			listComentarios.onRefreshComplete();
		}
	}
}

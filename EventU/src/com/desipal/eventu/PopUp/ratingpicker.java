package com.desipal.eventu.PopUp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.eventu.MainActivity;
import com.desipal.eventu.R;
import com.desipal.eventu.detalleEventoActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

public class ratingpicker extends DialogFragment {

	private static final String KEY_SAVE_RATING_BAR_VALUE = "KEY_SAVE_RATING_BAR_VALUE";

	private int idEvento;
	private RatingBar mRatingBar;
	private EditText opinion;
	private DialogInterface dialogo;
	private Resources recurso;
	private boolean enviado = false;

	public static ratingpicker newInstance() {
		ratingpicker frag = new ratingpicker();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.rating_dialog, null);

		mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar1);
		opinion = (EditText) view.findViewById(R.id.editComentario);
		this.idEvento = (int) detalleEventoActivity.idEvento;
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KEY_SAVE_RATING_BAR_VALUE)) {
				mRatingBar.setRating(savedInstanceState
						.getFloat(KEY_SAVE_RATING_BAR_VALUE));
			}
		}

		alertDialogBuilder.setView(view);
		// alertDialogBuilder.setTitle(getString(R.string.dialogTitle));
		alertDialogBuilder.setPositiveButton(
				getString(R.string.dialog_positive_text),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, int which) {
						final Resources res = detalleEventoActivity.act
								.getResources();
						recurso = res;
						dialogo = dialog;
						if (!opinion.getText().toString().equals("")) {
							ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
							parametros.add(new BasicNameValuePair("idEvento",
									idEvento + ""));
							parametros
									.add(new BasicNameValuePair(
											"idDispositivo",
											MainActivity.IDDISPOSITIVO));
							parametros.add(new BasicNameValuePair("comentario",
									opinion.getText().toString()));
							parametros.add(new BasicNameValuePair("valoracion",
									mRatingBar.getRating() + ""));
							String URL = "http://desipal.hol.es/app/eventos/nuevoComentario.php";

							peticion peticion = new peticion(parametros,
									getActivity());
							peticion.execute(new String[] { URL });

						} else {
							Toast.makeText(detalleEventoActivity.act,
									res.getString(R.string.dialogNoComentado),
									Toast.LENGTH_LONG).show();
						}
					}
				});
		alertDialogBuilder.setNegativeButton(
				getString(R.string.dialog_negative_text),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.cancel();
					}
				});
		return alertDialogBuilder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putFloat(KEY_SAVE_RATING_BAR_VALUE, mRatingBar.getRating());
		super.onSaveInstanceState(outState);
	}

	// PETICION AL SERVIDOR
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
					if (total.toString().equals("ok")) {
						enviado = true;
					} else {
						enviado = false;

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;

		}

		protected void onPostExecute(Void result) {
			if (enviado) {
				Toast.makeText(detalleEventoActivity.act,
						recurso.getString(R.string.dialogComentadoOK),
						Toast.LENGTH_LONG).show();
				dialogo.dismiss();
			} else
				Toast.makeText(detalleEventoActivity.act,
						recurso.getString(R.string.dialogComentadoNULL),
						Toast.LENGTH_LONG).show();
		}
	}

}

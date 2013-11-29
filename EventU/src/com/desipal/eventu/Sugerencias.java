package com.desipal.eventu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.desipal.eventu.Extras.UrlsServidor;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sugerencias extends Fragment {
	public static View view;
	private Activity Actividad;
	private Button btnEnviarSug;
	private EditText txtMsmEnviar;
	private EditText txtEmail;

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		try {
			Actividad = getActivity();
			view = inflater.inflate(R.layout.sugerencias, container, false);
			btnEnviarSug = (Button) view.findViewById(R.id.btnEnviarSug);
			txtMsmEnviar = (EditText) view.findViewById(R.id.txtMsmEnviar);
			txtEmail = (EditText) view.findViewById(R.id.txtEmail);
			btnEnviarSug.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
					String email = txtEmail.getText().toString();
					String mensaje = txtMsmEnviar.getText().toString();
					if (isEmailValid(email)) {
						txtEmail.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.campo));
						if (mensaje.length() > 10)
							enviar(email, mensaje);
						else
							Toast.makeText(Actividad,
									"Debe introducir un mensaje",
									Toast.LENGTH_SHORT).show();
					} else {
						txtEmail.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.camporojo));
						Toast.makeText(Actividad, "Email no válido",
								Toast.LENGTH_SHORT).show();
					}

				}
			});

		} catch (InflateException e) {
		}
		return view;
	}

	public boolean isEmailValid(String email) {
		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
				+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
				+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
				+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches())
			return true;
		else
			return false;
	}

	private void enviar(String email, String mensaje) {
		ArrayList<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("email", email));
		parametros.add(new BasicNameValuePair("campo", mensaje));

		String url = UrlsServidor.SUGERENCIA;
		enviarSugerencia peticion = new enviarSugerencia(parametros);
		peticion.execute(new String[] { url });

	}

	public class enviarSugerencia extends AsyncTask<String, Void, Void> {

		private ArrayList<NameValuePair> parametros;
		private String respuesta = "";

		public enviarSugerencia(ArrayList<NameValuePair> parametros) {
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
					if (total.toString().equals("ok"))
						respuesta = "El mensaje se envio corectamente.";
					else
						respuesta = "El mensaje no se pudo enviar, intentelo más tarde.";

				} catch (Exception e) {
					e.printStackTrace();
					respuesta = "El mensaje no se pudo enviar, intentelo más tarde.";
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Toast.makeText(Actividad, respuesta, Toast.LENGTH_SHORT).show();
			txtEmail.setText("");
			txtMsmEnviar.setText("");
		}
	}
}
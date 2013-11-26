package com.desipal.eventu.Extras;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.desipal.eventu.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class categorias extends AsyncTask<String, Void, Void> {

	private Activity act;

	public categorias(Activity act) {
		this.act = act;
	}

	@Override
	protected Void doInBackground(String... urls) {
		for (String url : urls) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				HttpResponse execute = client.execute(httppost);
				InputStream content = execute.getEntity().getContent();
				BufferedReader r = new BufferedReader(new InputStreamReader(
						content));
				StringBuilder total = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
					total.append(line);
				}
				if (!total.toString().equals("null")
						&& total.toString().contains("idCategoria")) {
					OutputStreamWriter fout = new OutputStreamWriter(
							act.openFileOutput(MainActivity.fCategorias,
									Context.MODE_PRIVATE));
					fout.write(total.toString());
					fout.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}

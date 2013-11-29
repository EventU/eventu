package com.desipal.eventu.Extras;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.desipal.eventu.MainActivity;
import com.desipal.eventu.Entidades.categoriaEN;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.DisplayMetrics;

public class Herramientas {
	public static int ComentariosEnDetalleEvento() {
		return 3;
	}

	public static Bitmap disminuirImagen(Bitmap bm, int nuevaMedida) {
		Bitmap resizedBitmap;
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = 0;
		float scaleHeight = 0;
		float aspect = 0;
		if (height > nuevaMedida || width > nuevaMedida) {
			if (height > width) {
				aspect = (float) height / width;
				scaleHeight = nuevaMedida;
				scaleWidth = scaleHeight / aspect;

			} else if (width > height) {
				aspect = (float) width / height;
				scaleWidth = nuevaMedida;
				scaleHeight = scaleWidth / aspect;
			} else if (width == height) {
				scaleWidth = nuevaMedida;
				scaleHeight = nuevaMedida;
			}
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth / width, scaleHeight / height);
			resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
					matrix, true);
		} else
			resizedBitmap = bm;
		return resizedBitmap;
	}

	public static Bitmap aumentarImagen(Bitmap bm, int nuevaMedida) {
		Bitmap resizedBitmap;
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = 0;
		float scaleHeight = 0;
		float aspect = 0;

		if (height > width) {
			aspect = (float) height / width;
			scaleHeight = nuevaMedida;
			scaleWidth = scaleHeight / aspect;

		} else if (width > height) {
			aspect = (float) width / height;
			scaleWidth = nuevaMedida;
			scaleHeight = scaleWidth / aspect;
		} else if (width == height) {
			scaleWidth = nuevaMedida;
			scaleHeight = nuevaMedida;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth / width, scaleHeight / height);
		resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);

		return resizedBitmap;
	}

	public static int calcularZoom(int ratio) {
		int zoom = 10;
		if (ratio < 2)
			zoom = 14;
		else if (ratio < 4)
			zoom = 13;
		else if (ratio < 10)
			zoom = 12;
		else if (ratio < 15)
			zoom = 11;
		else if (ratio < 26)
			zoom = 10;
		else if (ratio < 56)
			zoom = 9;
		else if (ratio < 101)
			zoom = 8;

		return zoom;
	}

	// Reescalar Bitmap por uri y tamaño
	public static Bitmap reescalarBitmapPorUri(Uri uri, Activity act, int tamaño)
			throws FileNotFoundException, IOException {
		InputStream input = act.getContentResolver().openInputStream(uri);

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;// optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1)
				|| (onlyBoundsOptions.outHeight == -1))
			return null;

		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;

		double ratio = (originalSize > tamaño) ? (originalSize / tamaño) : 1.0;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true;// optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		input = act.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}

	// /

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	public static List<categoriaEN> Obtenercategorias(Activity act) {
		BufferedReader fin;
		List<categoriaEN> lista = new ArrayList<categoriaEN>();
		try {
			fin = new BufferedReader(new InputStreamReader(
					act.openFileInput(MainActivity.fCategorias)));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = fin.readLine()) != null) {
				total.append(line);
			}
			fin.close();
			if (!fin.toString().equals("")) {
				JSONArray o = new JSONArray(total.toString());
				for (int i = 0; o.length() > i; i++) {
					categoriaEN ca = new categoriaEN();
					JSONObject jobj = o.getJSONObject(i);
					ca.setIdCategoria(jobj.getInt("idCategoria"));
					ca.setTexto(jobj.getString("texto"));
					ca.setDescripcion(jobj.getString("descripcion"));
					lista.add(ca);
				}
			} else
				lista = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			lista = null;
		} catch (IOException e) {
			e.printStackTrace();
			lista = null;
		} catch (JSONException e) {
			e.printStackTrace();
			lista = null;
		}
		return lista;
	}

	public static double ConvertirMinutos_en_Hora(long minutos) {
		return (minutos / 60);
	}

	public static long ConvertirHoras_en_Minutos(double horas) {
		return (long) (horas * 60);
	}

	public static double ConvertirHoras_en_Dias(long horas) {
		return (horas / 24);
	}

	public static long ConvertirDias_en_Horas(double dias) {
		return (long) (dias * 24);
	}

	public static String cuentaAtras(String fechaEvento) {
		Date dinicio = null, dfinal = null;
		long milis1, milis2, diff;

		SimpleDateFormat sdf = MainActivity.formatoFecha;

		try {
			dinicio = new Date();
			dfinal = sdf.parse(fechaEvento);

		} catch (ParseException e) {

			System.out.println("Se ha producido un error en el parseo");
		}
		// INSTANCIA DEL CALENDARIO GREGORIANO
		Calendar cinicio = Calendar.getInstance();
		Calendar cfinal = Calendar.getInstance();

		cinicio.setTime(dinicio);
		cfinal.setTime(dfinal);
		milis1 = cinicio.getTimeInMillis();
		milis2 = cfinal.getTimeInMillis();

		String devolver = "";
		if (milis2 > milis1)
			diff = milis2 - milis1;
		else
			diff = milis1 - milis2;
		// calcular la diferencia en dias
		long valorDia = 24 * 60 * 60 * 1000;
		long diffdias = 0;
		for (long tiempo = diff; tiempo > valorDia; tiempo -= valorDia) {
			diffdias++;
		}

		// calcular la diferencia en horas
		diff = diff - (diffdias * valorDia);
		long valorHora = 60 * 60 * 1000;
		long diffHoras = 0;
		for (long tiempo = diff; tiempo > valorHora; tiempo -= valorHora) {
			diffHoras++;
		}
		// calcular la diferencia en minutos
		diff -= diffHoras * valorHora;
		long valorMinutos = 60 * 1000;
		long diffMinutos = 0;
		for (long tiempo = diff; tiempo > valorMinutos; tiempo -= valorMinutos) {
			diffMinutos++;
		}

		if (milis2 > milis1)
			devolver = "Empieza en ";
		else
			devolver = "Comenzó hace  ";

		if (diffdias == 1)
			devolver = devolver + "1 día ";
		else if (diffdias == 0)
			devolver = devolver + "";
		else
			devolver += diffdias + " días ";

		if (diffHoras == 1)
			devolver = devolver + "1 hora ";
		else if (diffHoras == 0)
			devolver = devolver + "";
		else
			devolver += diffHoras + " horas ";

		if (diffMinutos == 1)
			devolver = devolver + "1 minuto ";
		else if (diffMinutos == 0)
			devolver = devolver + "";
		else
			devolver += diffMinutos + " minutos ";

		return devolver;
	}
}

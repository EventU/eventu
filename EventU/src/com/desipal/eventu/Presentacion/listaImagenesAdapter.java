package com.desipal.eventu.Presentacion;

import java.util.List;

import com.desipal.eventu.R;
import com.desipal.eventu.crearEventoActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class listaImagenesAdapter extends BaseAdapter {
	private Context mContext;
	private List<Bitmap> items;

	public listaImagenesAdapter(Context c, List<Bitmap> imagenes) {
		mContext = c;
		items = imagenes;
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(final int position, View convertView, ViewGroup parent) {
		View gridView = new View(mContext);
		try {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				gridView = inflater.inflate(R.layout.lis_itemcargaimagen, null);
				ImageView lsImagen = (ImageView) gridView.findViewById(R.id.lsImagen);
				lsImagen.setImageBitmap(items.get(position));

				TextView lsDesc = (TextView) gridView.findViewById(R.id.lsNombreImagen);
				if(position ==0)
					lsDesc.setText("Imagen principal");
				else
					lsDesc.setText("Imagen "+position);
				
				ImageButton btnBorrar = (ImageButton) gridView.findViewById(R.id.lsBtnBorrar);
				btnBorrar.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {		
						crearEventoActivity.contaFotos--;
						crearEventoActivity.arrayImagen.remove(position);
						crearEventoActivity.refrescarLista();
					}
				});
				
			} else
				gridView = (View) convertView;

		} catch (Exception e) {
			AlertDialog.Builder alertaSimple = new AlertDialog.Builder(mContext);
			alertaSimple.setTitle("Error");
			alertaSimple.setMessage(e.toString());
			alertaSimple.show();
		}
		return gridView;		
	}
}

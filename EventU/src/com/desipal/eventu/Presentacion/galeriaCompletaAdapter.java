package com.desipal.eventu.Presentacion;

import java.util.List;

import com.desipal.eventu.R;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

@SuppressWarnings("unused")
public class galeriaCompletaAdapter extends PagerAdapter {
	// Declare Variables
	Context context;
	LayoutInflater inflater;
	
	List<Drawable> listimagenes;
	private PhotoViewAttacher mAttacher;

	public galeriaCompletaAdapter(Context context, List<Drawable> listimagenes) {
		this.context = context;
		this.listimagenes = listimagenes;
	}

	@Override
	public int getCount() {
		return listimagenes.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		// Declare Variablesz
		ImageView foto;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.imagen_galeria_item,
				container, false);

		foto = (ImageView) itemView.findViewById(R.id.foto);
		foto.setImageDrawable(listimagenes.get(position));
		// The MAGIC happens here!
		mAttacher = new PhotoViewAttacher(foto);
		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		((ViewPager) container).addView(itemView);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// Remove viewpager_item.xml from ViewPager
		((ViewPager) container).removeView((RelativeLayout) object);

	}

	private class PhotoTapListener implements OnPhotoTapListener {

		@Override
		public void onPhotoTap(View view, float x, float y) {
			float xPercentage = x * 100f;
			float yPercentage = y * 100f;
		}
	}
}
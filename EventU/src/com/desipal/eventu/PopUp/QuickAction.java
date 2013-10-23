package com.desipal.eventu.PopUp;

import java.util.List;

import com.desipal.eventu.R;
import com.desipal.eventu.Entidades.categoriaEN;
import com.desipal.eventu.Extras.Herramientas;

import android.app.Activity;
import android.content.Context;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.PopupWindow.OnDismissListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class QuickAction extends PopupWindows implements OnDismissListener {
	private Animation mTrackAnim;
	private LayoutInflater inflater;
	private ViewGroup mTrack;
	private OnDismissListener mDismissListener;

	private boolean mDidAction;

	private List<categoriaEN> listaCategorias = null;

	private Spinner spiCategoria;

	public QuickAction(Context context, Activity act) {
		super(context);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = (ViewGroup) inflater.inflate(R.layout.quickaction, null);
		mTrack = (ViewGroup) mRootView.findViewById(R.id.tableFiltro);

		mRootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		setContentView(mRootView);

		mTrackAnim = AnimationUtils.loadAnimation(context, R.anim.rail);

		spiCategoria = (Spinner) mRootView
				.findViewById(R.id.spiFiltroCategoria);
		listaCategorias = Herramientas.Obtenercategorias(act);
		String a[] = new String[listaCategorias.size()];
		for (int i = 0; i < listaCategorias.size(); i++) {
			a[i] = listaCategorias.get(i).getTexto();
		}

		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context,
				R.layout.spinner_item, a);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spiCategoria.setAdapter(adapter2);

		mTrackAnim.setInterpolator(new Interpolator() {
			public float getInterpolation(float t) {
				// Pushes past the target area, then snaps back into place.
				// Equation for graphing: 1.2-((x*1.6)-1.1)^2
				final float inner = (t * 1.55f) - 1.1f;

				return 1.2f - inner * inner;
			}
		});
	}

	public void show(View anchor) {
		preShow();

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		mRootView.measure(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mWindow.showAsDropDown(anchor, 0, 0);

		mTrack.startAnimation(mTrackAnim);
	}

	public void setOnDismissListener(QuickAction.OnDismissListener listener) {
		setOnDismissListener(this);

		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}

	/**
	 * Listener for item click
	 * 
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(QuickAction source, int pos,
				int actionId);
	}

	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}
}
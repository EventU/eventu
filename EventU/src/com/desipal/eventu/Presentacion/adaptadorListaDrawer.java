package com.desipal.eventu.Presentacion;

import java.util.ArrayList;

import com.desipal.eventu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class adaptadorListaDrawer extends ArrayAdapter<Item> {

	private enum RowType {
		LIST_ITEM, HEADER_ITEM
	}

	private ArrayList<Item> items;
	private LayoutInflater vi;

	public adaptadorListaDrawer(Context context, ArrayList<Item> items) {
		super(context, 0, items);
		this.items = items;
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getViewTypeCount() { // Returns the number of types of Views that
									// will be created by getView(int, View,
									// ViewGroup).
		return RowType.values().length;
	}

	@Override
	public int getItemViewType(int position) { // framework calls
												// getItemViewType for row n,
												// the row it is about to
												// display.
		// Get the type of View that will be created by getView(int, View,
		// ViewGroup) for the specified item.
		return getItem(position).isSection() ? 0 : 1; // get position passes (n)
														// and accertain is its
														// a header or not
	}

	@Override
	public boolean isEnabled(int position) {
		return !getItem(position).isSection();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		final Item i = items.get(position);
		if (i != null) {
			if (i.isSection()) {
				SectionItem si = (SectionItem) i;
				v = vi.inflate(R.layout.list_item_sectiondrawer, null);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);

				final TextView sectionView = (TextView) v
						.findViewById(R.id.lsSectionDrawer);
				sectionView.setText(si.getTitle());
			} else {
				EntryItem ei = (EntryItem) i;
				v = vi.inflate(R.layout.list_item_entrydrawer, null);
				final TextView title = (TextView) v
						.findViewById(R.id.lsTitleDrawer);
				final TextView subtitle = (TextView) v
						.findViewById(R.id.lsSubTitleDrawer);
				final ImageView imagen = (ImageView) v
						.findViewById(R.id.lsImagenDrawer);
				if (title != null) {
					title.setText(ei.title);

					imagen.setBackgroundResource(ei.icono);
				}
				if (subtitle != null)
					subtitle.setText(ei.subtitle);
			}
		}
		return v;
	}

}
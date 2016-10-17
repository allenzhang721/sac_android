package com.hl.android.view.component.moudle;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.hl.android.R;

public class GalleyCommonAdapter extends BaseAdapter {
	ArrayList<String> snaps;
	int width = 320;
	int height = 280;
	BitmapFactory.Options options;

	public GalleyCommonAdapter(Context c, ArrayList<String> snaps, int _width,
			int _height) {
		mContext = c;

		width = _width;
		height = _height;
		this.snaps = snaps;
		options = new BitmapFactory.Options();
		options.inTempStorage = new byte[16 * 1024];
		// options.inSampleSize = 2;
	}

	public int getCount() {
		return this.snaps.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i = new ImageView(mContext);
		i.setScaleType(ImageView.ScaleType.FIT_XY);
		i.setLayoutParams(new Gallery.LayoutParams(this.width, this.height));
		return i;
	}

	private Context mContext;

}
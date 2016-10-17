package com.hl.android.view.compositeview;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ViewPageAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> mpageIDS;
	public ViewPageAdapter(Context c, ArrayList<String> pageIDS) {
		mContext = c;
		mpageIDS = pageIDS;
	}

	@Override
	public int getCount() {
		return mpageIDS.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			try {
				imageView.setImageBitmap(BitmapFactory.decodeStream(this.mContext.getAssets().open("book/"+mpageIDS.get(position)), null, null));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			imageView = (ImageView) convertView;
		}
		return imageView;
	}

}

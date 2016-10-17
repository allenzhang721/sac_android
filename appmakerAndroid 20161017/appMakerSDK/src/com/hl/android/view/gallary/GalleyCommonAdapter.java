package com.hl.android.view.gallary;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hl.android.common.BookSetting;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.ImageUtils;

public class GalleyCommonAdapter extends BaseAdapter {
	ArrayList<String> snaps;
	int width = 320;
	int height = 280;
	ViewGroup.LayoutParams lp;
	
	public ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
	public GalleyCommonAdapter(Context c, ArrayList<String> snaps, int _width,
			int _height) {
		Log.i("hl","GalleyCommonAdapter created");
		mContext = c;
		height = BookSetting.SNAPSHOTS_HEIGHT - 10;// _height;

		this.width = BookSetting.SNAPSHOTS_WIDTH;// * this.height /
													// BookSetting.SNAPSHOTS_HEIGHT;

		this.snaps = snaps;
		lp = new ViewGroup.LayoutParams(width,height);
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
		Bitmap newBitmp = BitmapUtils.getBitMap(snaps.get(position), mContext,width,height);
	
		Log.e("hl","GalleyCommonAdapter  recyled");
		ImageView i = (ImageView) convertView;
		BitmapDrawable bd = (BitmapDrawable)i.getBackground();
		Bitmap bitmap = bd.getBitmap();
		bitmapList.remove(bitmap);
		
		i.setImageBitmap(newBitmp);
		bitmapList.add(newBitmp);
		bitmap.recycle();
		return convertView;
		
	}
	
	public void recycle(){
		ImageUtils.recyleBitmapList(bitmapList);
	}
	
	private Context mContext;

}
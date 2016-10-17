package com.hl.android.view.component.moudle.carouseimg;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hl.android.core.utils.BitmapUtils;

public class GalleryImageAdapter extends BaseAdapter{
	private ArrayList<String> mImgPaths = null;
	private Context context = null;
	ViewGroup.LayoutParams mItemLp = null;
	/** 可提供两个或多个参数的构造方法。第一个参数为上下文环境。其余参数为图像路径。  */
	public GalleryImageAdapter(Context c,ArrayList<String> imgPaths,ViewGroup.LayoutParams itemLp) {
		context = c;
		this.mImgPaths = imgPaths;
		mItemLp = itemLp;
	}
	@Override
	public int getCount() {
		if(mImgPaths==null) {
			return 0;
		}
		return Integer.MAX_VALUE;
	}

	//返回一个视频对象
	@Override
	public Object getItem(int position) {
		if(mImgPaths==null) {
			return null;
		}
		return mImgPaths.get(position%mImgPaths.size());
	}

	@Override
	public long getItemId(int position) {
		return position%mImgPaths.size();
	}
	
	private int selectPos = 0;
	public void notifyDataSetChanged(int id) {
		selectPos = id;

		Log.d("hl", " selectPos is " + selectPos);
		super.notifyDataSetChanged();
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Bitmap bitmap=null;
		if(convertView == null){
			bitmap = BitmapUtils.getBitMap(mImgPaths.get(position%mImgPaths.size()),context);
			ImageView img = new ImageView(context); 
			img.setScaleType(ScaleType.FIT_CENTER);

			img.setId(position%mImgPaths.size());
			img.setImageBitmap(bitmap);
			convertView = img;
		}else{
			ImageView img = (ImageView) convertView; 
			BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
			Bitmap oldBitmap = bitmapDrawable.getBitmap();
			img.setImageBitmap(bitmap);
			if(!oldBitmap.isRecycled())
			{
				oldBitmap.recycle();
			}
		}
		Gallery.LayoutParams glp= new Gallery.LayoutParams(bitmap.getWidth()*mItemLp.height/bitmap.getHeight(),mItemLp.height);
		convertView.setId(position%mImgPaths.size());
		convertView.setLayoutParams(glp);
		return convertView;
	}
}

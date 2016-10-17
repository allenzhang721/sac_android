package com.hl.android.view.gallary;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hl.android.R;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.FileUtils;

public class Galley3DAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;
	ArrayList<String> snaps;
	int width = 320;
	int height = 280;
	BitmapFactory.Options options;

	public Galley3DAdapter(Context c, Integer[] ImageIds) {
		mContext = c;
	}

	public Galley3DAdapter(Context c, ArrayList<String> snaps, int _width,
			int _height) {
		mContext = c;
		width = _width;
		height = _height;
		if (BookSetting.IS_HOR) {
			width = (int) (BookSetting.SCREEN_WIDTH / 5);
		} else {
			width = (int) (BookSetting.SCREEN_WIDTH / 5);
		}

		this.snaps = snaps;
		TypedArray a = c.obtainStyledAttributes(R.styleable.Gallery1);
		mGalleryItemBackground = a.getResourceId(
				R.styleable.Gallery1_android_galleryItemBackground, 0);
		a.recycle();
		options = new BitmapFactory.Options();
		options.inTempStorage = new byte[16 * 1024];
	}

	/**
	 * 创建倒影效果
	 * 
	 * @return
	 */
	public boolean createReflectedImages() {
		return true;
	}

	@SuppressWarnings("unused")
	private Resources getResources() {
		return null;
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
		return makeImage(snaps.get(position));
	}

	/**
	 * 创建倒影效果
	 * 
	 * @return
	 */
	public View makeImage(String imageId) {
		// 倒影图和原图之间的距离
		final int reflectionGap = 4;
		// 返回原图解码之后的bitmap对象
		Bitmap originalImage = null;
		try {
			if (HLSetting.IsResourceSD)
				originalImage = BitmapFactory.decodeStream(FileUtils
						.getInstance().getFileInputStream(imageId), null,
						options);
			else
				originalImage = BitmapFactory.decodeStream(FileUtils
						.getInstance().getFileInputStream(mContext, imageId),
						null, options);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		int imageHeight = (int) (height * 0.6);
		Bitmap resizeBmp = Bitmap.createScaledBitmap(originalImage, width,
				imageHeight, true);

		// 创建矩阵对象
		Matrix matrix = new Matrix();

		// 指定矩阵(x轴不变，y轴相反)
		matrix.preScale(1, -1);

		// 将矩阵应用到该原图之中，返回一个宽度不变，高度为原图1/2的倒影位图
		Bitmap reflectionImage = Bitmap.createBitmap(resizeBmp, 0,
				(int) (imageHeight * 0.6), width, (int) (imageHeight * 0.3),
				matrix, false);
		
		// 创建一个宽度不变，高度为原图+倒影图高度的位图
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height),
				Config.ARGB_8888);

		// 将上面创建的位图初始化到画布
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(resizeBmp, 0, 0, null);

		Paint deafaultPaint = new Paint();
		deafaultPaint.setAntiAlias(false);
	
		canvas.drawBitmap(reflectionImage, 0, imageHeight + reflectionGap, null);
		Paint paint = new Paint();
		paint.setAntiAlias(false);

		/**
		 * 参数一:为渐变起初点坐标x位置， 参数二:为y轴位置， 参数三和四:分辨对应渐变终点， 最后参数为平铺方式，
		 * 这里设置为镜像Gradient是基于Shader类，所以我们通过Paint的setShader方法来设置这个渐变
		 */
		LinearGradient shader = new LinearGradient(0, resizeBmp.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.MIRROR);
		// 设置阴影
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.DST_IN));
		// 用已经定义好的画笔构建一个矩形阴影渐变效果
		canvas.drawRect(0, imageHeight, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		// 创建一个ImageView用来显示已经画好的bitmapWithReflection
		ImageView imageView = new ImageView(mContext);
		imageView.setImageBitmap(bitmapWithReflection);
		imageView.setScaleType(ScaleType.FIT_XY);
		// 设置imageView大小 ，也就是最终显示的图片大小
	
		imageView.setLayoutParams(new Galley3D.LayoutParams(this.width,
				this.height));
	
		return imageView;

	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

}

package com.hl.android.view.gallary.base;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.hl.android.core.utils.BitmapUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;


public class ImageManager {
	static int index = 0;
	public static ConcurrentHashMap<String, Bitmap> imageMap = new ConcurrentHashMap<String, Bitmap>();
	public static String ISFALSE="ISFALSE";

	public static String ISTRUE="ISTRUE";



	public static boolean getImageFile(String fName) {
		boolean re;

		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			re = true;
		} else {
			re = false;
		}
		return re;
	}

	public static Bitmap getImage(String fileName) {
		try {
			File file = new File(fileName);
			Bitmap bitmap = null;
			bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			addImage(fileName, bitmap);
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}



	public static void addImage(String uri, Bitmap bitmap) {
		imageMap.put(uri.toString(), bitmap);
	}

	private static void clearBitmap(String uri) {
		if (imageMap.get(uri) != null) {
			imageMap.get(uri).recycle();
			imageMap.remove(uri);
		}
	}



	// 
	public static List<ImageMessage> clearImage(List<ImageMessage> imageList,
			int start, int end) {
		if (start >= 0 && end < imageList.size()) {
			for (int i = 0; i < start; i++) {
				if (imageList.get(i).getIsNull().equals(ISFALSE)) {
					imageList.get(i).setIsNull(ISTRUE);
					clearBitmap(imageList.get(i).getPath().toString());
					clearBitmap(imageList.get(i).getPath().toString() + "1");
					clearBitmap(imageList.get(i).getPath().toString() + "2");
					imageList.get(i).setImage(null);
				}
			}

			for (int i = end + 1; i < imageList.size(); i++) {
				if (imageList.get(i).getIsNull().equals(ISFALSE)) {
					imageList.get(i).setIsNull(ISTRUE);
					clearBitmap(imageList.get(i).getPath().toString());
					clearBitmap(imageList.get(i).getPath().toString() + "1");
					clearBitmap(imageList.get(i).getPath().toString() + "2");
					imageList.get(i).setImage(null);
				}
			}
		}
		return imageList;
	}


	/**
	 * 获得倒影图
	 * @param str
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		if(bitmap==null){
			return null;
		}
		final int reflectionGap = 0;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);


		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap ,float percentHeight) {
		if(bitmap==null){
			return null;
		}
		final int reflectionGap = 0;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, (int) (height*percentHeight),
				width, (int) (height*percentHeight), matrix, false);


		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + (int) (height*percentHeight)), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		BitmapUtils.recycleBitmap(bitmap);
		BitmapUtils.recycleBitmap(reflectionImage);
		return bitmapWithReflection;
	}
}

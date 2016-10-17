package com.hl.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public final class BitmapUtil {

	/**
	 * 取得指定区域的图形
	 * 
	 * @param source
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap source, int x, int y, int width,
			int height) {
		Bitmap bitmap = Bitmap.createBitmap(source, x, y, width, height);
		return bitmap;
	}
	
	
	/**
	 * 从大图中截取小图
	 * 
	 * @param r
	 * @param resourseId
	 * @param row
	 * @param col
	 * @param rowTotal
	 * @param colTotal
	 * @return
	 */
	public static Bitmap getImage(Context context, Bitmap source, int row,
			int col, int rowTotal, int colTotal, float multiple,
			boolean isRecycle) {
		Bitmap temp = getBitmap(source, (col - 1) * source.getWidth()
				/ colTotal, (row - 1) * source.getHeight() / rowTotal,
				source.getWidth() / colTotal, source.getHeight() / rowTotal);

		if (isRecycle) {
			recycleBitmap(source);
		}
		if (multiple != 1.0) {
			Matrix matrix = new Matrix();
			matrix.postScale(multiple, multiple);
			temp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(),
					temp.getHeight(), matrix, true);
		}
		return temp;
	}

	/**
	 * 从大图中截取小图
	 * 
	 * @param r
	 * @param resourseId
	 * @param row
	 * @param col
	 * @param rowTotal
	 * @param colTotal
	 * @return
	 */
	public static Drawable getDrawableImage(Context context, Bitmap source,
			int row, int col, int rowTotal, int colTotal, float multiple) {

		Bitmap temp = getBitmap(source, (col - 1) * source.getWidth()
				/ colTotal, (row - 1) * source.getHeight() / rowTotal,
				source.getWidth() / colTotal, source.getHeight() / rowTotal);
		if (multiple != 1.0) {
			Matrix matrix = new Matrix();
			matrix.postScale(multiple, multiple);
			temp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(),
					temp.getHeight(), matrix, true);
		}
		Drawable d = new BitmapDrawable(context.getResources(), temp);
		return d;
	}

	public static Drawable[] getDrawables(Context context, int resourseId,
			int row, int col, float multiple) {
		Drawable drawables[] = new Drawable[row * col];
		Bitmap source = decodeResource(context, resourseId);
		int temp = 0;
		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				drawables[temp] = getDrawableImage(context, source, i, j, row,
						col, multiple);
				temp++;
			}
		}
		if (source != null && !source.isRecycled()) {
			source.recycle();
			source = null;
		}
		return drawables;
	}

	public static Drawable[] getDrawables(Context context, String resName,
			int row, int col, float multiple) {
		Drawable drawables[] = new Drawable[row * col];
		Bitmap source = decodeBitmapFromAssets(context, resName);
		int temp = 0;
		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				drawables[temp] = getDrawableImage(context, source, i, j, row,
						col, multiple);
				temp++;
			}
		}
		if (source != null && !source.isRecycled()) {
			source.recycle();
			source = null;
		}
		return drawables;
	}

	/**
	 * 根据一张大图，返回切割后的图元数组
	 * 
	 * @param resourseId
	 *            :资源id
	 * @param row
	 *            ：总行数
	 * @param col
	 *            ：总列数 multiple:图片缩放的倍数1:表示不变，2表示放大为原来的2倍
	 * @return
	 */
	public static Bitmap[] getBitmaps(Context context, int resourseId, int row,
			int col, float multiple) {
		Bitmap bitmaps[] = new Bitmap[row * col];
		Bitmap source = decodeResource(context, resourseId);
		int temp = 0;
		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				bitmaps[temp] = getImage(context, source, i, j, row, col,
						multiple, false);
				temp++;
			}
		}
		if (source != null && !source.isRecycled()) {
			source.recycle();
			source = null;
		}
		return bitmaps;
	}

	public static Bitmap[] getBitmaps(Context context, String resName, int row,
			int col, float multiple) {
		Bitmap bitmaps[] = new Bitmap[row * col];
		Bitmap source = decodeBitmapFromAssets(context, resName);
		int temp = 0;
		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				bitmaps[temp] = getImage(context, source, i, j, row, col,
						multiple, false);
				temp++;
			}
		}
		if (source != null && !source.isRecycled()) {
			source.recycle();
			source = null;
		}
		return bitmaps;
	}

	public static Bitmap[] getBitmapsByBitmap(Context context, Bitmap source,
			int row, int col, float multiple) {
		Bitmap bitmaps[] = new Bitmap[row * col];
		int temp = 0;
		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				bitmaps[temp] = getImage(context, source, i, j, row, col,
						multiple, false);
				temp++;
			}
		}
		return bitmaps;
	}

	public static Bitmap decodeResource(Context context, int resourseId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inPurgeable = true;
		opt.inInputShareable = true; // 需把 inPurgeable设置为true，否则被忽略
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resourseId);

		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt); // decodeStream直接调用JNI>>nativeDecodeAsset()来完成decode，无需再使用java层的createBitmap，从而节省了java层的空间

		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 从assets文件下解析图片
	 * 
	 * @param resName
	 * @return
	 */
	public static Bitmap decodeBitmapFromAssets(Context context, String resName) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inPurgeable = true;
		options.inInputShareable = true;
		InputStream in = null;
		try {
			// in = AssetsResourcesUtil.openResource(resName);
			in = context.getAssets().open(resName);
			return BitmapFactory.decodeStream(in, null, options);
		} catch (IOException e) {
			Log.d("hl","bitmap get result is null " + resName);
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 回收不用的bitmap
	 * 
	 * @param b
	 */
	public static void recycleBitmap(Bitmap b) {
		if (b != null && !b.isRecycled()) {
			b.recycle();
			b = null;
		}
	}

	/**
	 * 获取某些连在一起的图片的某一个画面（图片为横着排的情况）
	 * 
	 * @param source
	 * @param frameIndex
	 *            从1开始
	 * @param totalCount
	 * @return
	 */
	public static Bitmap getOneFrameImg(Bitmap source, int frameIndex,
			int totalCount) {
		int singleW = source.getWidth() / totalCount;
		return Bitmap.createBitmap(source, (frameIndex - 1) * singleW, 0,
				singleW, source.getHeight());
	}

	public static void recycleBitmaps(Bitmap bitmaps[]) {
		if (bitmaps != null) {
			for (Bitmap b : bitmaps) {
				recycleBitmap(b);
			}
			bitmaps = null;
		}
	}

	public static void recycleBitmaps(ArrayList<Bitmap> bitmapList) {
		if (bitmapList != null) {
			for (int i = 0; i < bitmapList.size() - 1; i++) {
				recycleBitmap(bitmapList.get(i));
			}
		}
	}

	
	private static Bitmap load(InputStream is, int[] size){
		try {
			is.reset();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		Bitmap bitmap = null;
		//1先取得bitmap的size
		BitmapFactory.Options _option = new BitmapFactory.Options();
		
		_option.inJustDecodeBounds = true;
	    // 通过这个bitmap获取图片的宽和高&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
       BitmapFactory.decodeStream(is, null, _option);

        float realWidth = _option.outWidth;
        float realHeight = _option.outHeight;
        
        // 计算缩放比&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        int scale = (int) (realWidth/size[0]);
        if (scale <= 0)
        {
            scale = 1;
        }
        _option.inSampleSize = scale;
        _option.inJustDecodeBounds = false;
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inTempStorage = new byte[16 * 1024];
		_option.outWidth = size[0];
		_option.outHeight = size[1];
		BufferedInputStream buf = new BufferedInputStream(is, 8192);
		try {
			try {
				is.reset();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			bitmap = BitmapFactory.decodeStream(buf, null, _option);
			size[0] = _option.outWidth;
			size[1] = _option.outHeight;
			return bitmap;
		} catch (Exception e) {
			Log.e("hl","load bitmap Exception");
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e("hl","load bitmap OutOfMemoryError");
			e.printStackTrace();
			_option.inSampleSize = 50;
			bitmap = BitmapFactory.decodeStream(is, null, _option);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static Bitmap load(InputStream is, int width, int height) {
		try {
			is.reset();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		Bitmap bitmap = null;
		//1先取得bitmap的size
		BitmapFactory.Options _option = new BitmapFactory.Options();
		
		_option.inJustDecodeBounds = true;
	    // 通过这个bitmap获取图片的宽和高&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
       BitmapFactory.decodeStream(is, null, _option);

        float realWidth = _option.outWidth;
        float realHeight = _option.outHeight;
        
        // 计算缩放比&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
        int scale = (int) (realWidth/width);
        if (scale <= 0)
        {
            scale = 1;
        }
        _option.inSampleSize = scale;
        _option.inJustDecodeBounds = false;
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inTempStorage = new byte[16 * 1024];
		_option.outHeight = height;
		_option.outWidth = width;
		BufferedInputStream buf = new BufferedInputStream(is, 8192);
		try {
			try {
				is.reset();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			bitmap = BitmapFactory.decodeStream(buf, null, _option);
			return bitmap;
		} catch (Exception e) {
			Log.e("hl","load bitmap Exception");
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e("hl","load bitmap OutOfMemoryError");
			e.printStackTrace();
			_option.inSampleSize = 50;
			bitmap = BitmapFactory.decodeStream(is, null, _option);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}

	public static Bitmap getBitmapFromResource(Context context, int picId) {
		Bitmap bmp = BitmapFactory
				.decodeResource(context.getResources(), picId);
		return bmp;
	}

	public static Bitmap getBitmapFromFile(Context context, String path) {
		Bitmap bmp = BitmapFactory.decodeFile(path);
		return bmp;
	}


	

	/**
	 * 获得图片size
	 * @param is
	 * @return
	 */
	private static int[] loadSize(InputStream is){
		int[] size = new int[2];
		BitmapFactory.Options _option = new BitmapFactory.Options();
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inTempStorage = new byte[16 * 1024];
		_option.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(is, null, _option);
			size[0] = _option.outWidth;
			size[1] = _option.outHeight;
			return size;
		} catch (Exception e) {
			Log.e("hl","load bitmap Exception");
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return size;
	}
	private static Bitmap load(InputStream is) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			bitmap = BitmapFactory.decodeStream(is, null, null);
		}
		return bitmap;
	}
}
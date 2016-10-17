package com.hl.realtest.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

/**
 * 需要专门建立一个工具类来管理图片 以防止内存总是出错
 * 
 * @author zhaoq
 * 
 */
public class ImageUtils {
	public static void setViewBackground(View v, int id) {
		try {
			try {
				v.setBackgroundResource(id);
			} catch (Exception e) {
				System.gc();
			}
		} catch (Error er) {

		}
	}

	/**
	 * 获得sd卡的bitmap
	 * 
	 * @param localSourceID
	 *            资源id
	 * @param layoutLp
	 *            资源大小
	 * @param context
	 *            上下文
	 * @return
	 */
	public static Bitmap getBitMap(String localSourceID, Context context,
			int width, int height) {
		InputStream is = null;
		try {
			is = FileUtil.getInstance().getFileInputStream(context,
					localSourceID);

			return loadBitmap(is, context, width, height);

		} catch (OutOfMemoryError e) {
			System.gc();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 根据输入流来生成bitmap
	 * 
	 * @param is
	 * @return
	 */
	public static Bitmap loadBitmap(InputStream is, Context context, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options _option = new BitmapFactory.Options();
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inPreferredConfig = Bitmap.Config.RGB_565;
		_option.inTempStorage = new byte[32 * 1024];
		_option.outHeight = height;
		_option.outWidth = width;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, _option);

			return bitmap;
		} catch (Exception e) {
			// TODO: handle exception
		} catch (OutOfMemoryError e) {
			_option.inSampleSize = 2;
			bitmap = BitmapFactory.decodeStream(is, null, _option);
		}
		return bitmap;
	}

	/**
	 * 获得按钮点击效果的drawabel
	 * 
	 * @param btnResource
	 *            正常的图片
	 * @param btnSelectResource
	 *            按下的效果
	 * @param context
	 * @return
	 */
//	public static StateListDrawable getButtonDrawable(String btnResource,
//			String btnSelectResource, Context context) {
//		StateListDrawable btnDrawable = new StateListDrawable();
//		BitmapDrawable dbg = BitmapUtil.getBitmapDrawable(context, btnResource);
//		BitmapDrawable selectdbg = BitmapUtil.getBitmapDrawable(context,
//				btnSelectResource);
//		btnDrawable.addState(new int[] { android.R.attr.state_pressed },
//				selectdbg);
//		btnDrawable.addState(new int[] { android.R.attr.state_focused }, dbg);
//		btnDrawable.addState(new int[] { android.R.attr.state_enabled }, dbg);
//		btnDrawable.addState(new int[] {}, dbg);
//		return btnDrawable;
//	}

	public static void recyleBitmapList(Collection<Bitmap> bitmaps) {
		for (Bitmap b : bitmaps) {
			b.recycle();
		}
		bitmaps.clear();
	}
}

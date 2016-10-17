package com.hl.common;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapCache {
	private static LinkedHashMap<String, Bitmap> bitmapContainer = new LinkedHashMap<String, Bitmap>();
	private static BitmapFactory.Options _option;
	static{
		_option = new BitmapFactory.Options();
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inPreferredConfig = Bitmap.Config.RGB_565;
		_option.inTempStorage = new byte[32 * 1024];
		_option.inSampleSize = 2;
	}
	private static Bitmap getSimpleBitmap(String fillePath) {
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
		try {
			bitmap = BitmapFactory.decodeFile(fillePath, _option);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			_option.inSampleSize = 8;
			bitmap = BitmapFactory.decodeFile(fillePath, _option);
		}
		return bitmap;
	}

	public static Bitmap getSimpleBitmap(String key, String filePath) {
		if(StringUtils.isEmpty(filePath))return null;
		synchronized (bitmapContainer) {
			if (bitmapContainer.containsKey(key)) {
				Bitmap bitmap = bitmapContainer.get(key);
				if (bitmap == null) {
					bitmapContainer.remove(key);
					return null;
				}
				if (bitmap.isRecycled()) {
					bitmapContainer.remove(key);
					bitmap = getSimpleBitmap(filePath);
					bitmapContainer.put(key, bitmap);
				}
				return bitmap;
			}
			if (new File(filePath).exists()) {
				Bitmap bitmap = null;
				try {
					try {
						bitmap = getSimpleBitmap(filePath);
						bitmapContainer.put(key, bitmap);
						return bitmap;
					} catch (Exception e) {
						return null;
					}
				} catch (Error er) {
					return null;
				}

			}
			;
		}
		return null;
	}

	public static Bitmap getBitmap(String key, String filePath) {
		synchronized (bitmapContainer) {
			
			if (bitmapContainer.containsKey(key)) {
				Bitmap bitmap = bitmapContainer.get(key);
				if (bitmap == null) {
					bitmapContainer.remove(key);
					Log.d("BitmapCache","container has key but bitmap null");
					bitmap = BitmapFactory.decodeFile(filePath,_option);
					Log.d("BitmapCache","decodeFile: "+filePath);
					if(bitmap==null){
						Log.d("BitmapCache","decodeFile failed");
					}else{
						Log.d("BitmapCache","decodeFile success");
					}
					bitmapContainer.put(key, bitmap);
				}else if (bitmap.isRecycled()) {
					Log.d("BitmapCache","container has key but bitmap recycled");
					bitmapContainer.remove(key);
					bitmap = BitmapFactory.decodeFile(filePath,_option);
					if(bitmap==null){
						Log.d("BitmapCache","decodeFile failed");
					}else{
						Log.d("BitmapCache","decodeFile success");
					}
					bitmapContainer.put(key, bitmap);
				}
				return bitmap;
			}
			if (new File(filePath).exists()) {
				Log.d("BitmapCache","container has no key but bitmap file exists");
				Bitmap bitmap = null;
				try {
					try {
						bitmap = BitmapFactory.decodeFile(filePath,_option);
						if(bitmap==null){
							Log.d("BitmapCache","decodeFile failed");
						}else{
							Log.d("BitmapCache","decodeFile success");
						}
						bitmapContainer.put(key, bitmap);
						return bitmap;
					} catch (Exception e) {
						return null;
					}
				} catch (Error er) {
					return null;
				}

			}
			Log.d("hl", "没找到图片文件");
			return null;
		}
	}

	// private static HashMap<String,ArrayList<ImageTask>> downLoadHashMap = new
	// HashMap<String,ArrayList<ImageTask>>();
	/*
	 * public static Bitmap getDownBitmapAsync(String key,String url,String
	 * localPath,ImageView img){ Bitmap bitmap = getBitmap(key,localPath);
	 * if(bitmap!= null)return bitmap; //ArrayList<ImageTask> taskList =
	 * downLoadHashMap.get(localPath); //if (taskList == null) { //synchronized
	 * (taskList) { //taskList = new ArrayList<ImageTask>(); //ImageTask task =
	 * new ImageTask(localPath, img); //taskList.add(task); //} //} new
	 * BitmapCache.MyTask(img).execute(localPath,key,url);
	 * 
	 * return null; }
	 */
	public static class MyTask extends AsyncTask<String, Object, Object> {
		String mLocalPath = "";
		String mKey = "";
		String mUrlPath = "";
		Bitmap bitmap = null;
		ImageView mImg = null;

		public MyTask(ImageView img) {
			mImg = img;
		}

		@Override
		protected Object doInBackground(String... arg0) {
			String mLocalPath = arg0[0];
			String mKey = arg0[1];
			String mUrlPath = arg0[2];
			HttpManager.downLoadResource(mUrlPath, mLocalPath);
			bitmap = getBitmap(mKey, mLocalPath);
			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			try {
				if (mImg != null && mImg.isShown()) {
					if (bitmap != null) {
						BitmapDrawable newBackground = new BitmapDrawable(
								bitmap);
						mImg.setImageDrawable(newBackground);
						mImg.setImageBitmap(bitmap);
					} else {
						// mImg.setBackgroundResource(com.hl)
					}
				}
			} catch (Exception e) {
				Log.e("hl", "error", e);
			}
		}
	}

	public static void recyle(String prefixKey) {
		synchronized (bitmapContainer) {
			ArrayList<String> deleingKeys = new ArrayList<String>();
			for (LinkedHashMap.Entry<String, Bitmap> entry : bitmapContainer
					.entrySet()) {
				String key = entry.getKey().toString();
				Bitmap value = entry.getValue();
				if (key.startsWith(prefixKey)) {
					Log.d("hl", key + " is recyled");
					if (value != null) {
						value.recycle();
					}
					deleingKeys.add(key);
				}
			}

			for (String key : deleingKeys) {
				bitmapContainer.remove(key);
			}
		}

	}
}

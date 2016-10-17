package com.hl.android.core.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;

public final class BitmapUtils {

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
	 * 获得bitmap
	 * @param localSourceID  资源id
	 * @param context 上下文
	 * @return
	 */
	public static Bitmap getBitMap(String localSourceID, Context context) {
		Bitmap resultBitmap = null;
		try {
			if (HLSetting.IsResourceSD)
				resultBitmap = BitmapFactory.decodeFile(BookSetting.BOOK_PATH + "/" + localSourceID);
			else
				resultBitmap = BitmapUtils.load(FileUtils.getInstance().getFileInputStream(
						context, localSourceID));


		} catch (OutOfMemoryError e) {

			Log.e("hl", " imagecomponents load",e); 
		}
		if(resultBitmap==null){
			Log.i("hl", "获取图片失败，图片名称是 " + localSourceID);
		}
		return resultBitmap;
	}
	
	public static Bitmap getWdyBitmapFromSD(String fileName, int width, int height) {
		System.gc();
		String actfileName = BookSetting.BOOK_PATH + "/" + fileName;
		
		Bitmap bitmap = null;
		//1先取得bitmap的size
		BitmapFactory.Options _option = new BitmapFactory.Options();
		_option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(actfileName, _option);
        float realWidth = _option.outWidth;
        float realHeight = _option.outHeight;
        int scale=1;
        int scaleW=(int) (realWidth*1.0f/width);
        int	scaleH=(int) (realHeight*1.0f/height);
        scale=Math.max(scaleW, scaleH);
        if(scale<=0){
        	scale=1;
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
        try {
        	bitmap = BitmapFactory.decodeFile(actfileName, _option);
        } catch (OutOfMemoryError e) {
        	while (bitmap==null) {
        		scale++;
        		_option.inSampleSize = scale;
        		 try {
        			 bitmap = BitmapFactory.decodeFile(actfileName, _option);
        		 }catch(OutOfMemoryError e1){
        			 
        		 }
			}
			Log.e("wdy", "get bitmap error",e);
		 	Log.e("wdy", "width*height:"+width+"*"+height);
		 	Log.e("wdy", "realwidth*realheight:"+realWidth+"*"+realHeight);
//			try {
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//				int options = 100;
//				while (baos.toByteArray().length / 1024 > 150) {
//					baos.reset();
//					options -= 10;
//					bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
//					if (options < 0) {
//						break;
//					}
//				}
//				ByteArrayInputStream isBm = new ByteArrayInputStream(
//						baos.toByteArray());
//				bitmap = BitmapFactory.decodeStream(isBm, null, null);
//			} catch (OutOfMemoryError e1) {
//				Log.d("wdy", "这里怎么处理啊，亲？？？？？？？？？？？");
//			}
		}
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
//    	Log.e("wdy", "width*height:"+width+"*"+height);
//    	Log.e("wdy", "realwidth*realheight:"+realWidth+"*"+realHeight);
        Log.e("wdy", "bmpWidth*bmpHeight:"+bmpWidth+"*"+bmpHeight);
//      	float scaleWidth = (float)width / bmpWidth; // 按固定大小缩放 sWidth 写多大就多大
//      	float scaleHeight = (float)height / bmpHeight; //
//      	Log.e("wdy", "width*height:"+width+"*"+height);
//      	Log.e("wdy", "bmpWidth*bmpHeight:"+bmpWidth+"*"+bmpHeight);
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);// 产生缩放后的Bitmap对象
//		 try {
//	        	Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, false);
//	        	return resizeBitmap;
//	        } catch (OutOfMemoryError e) {
//				Log.e("wdy", "get resizeBitmap error",e);
//			}
//		bitmap.recycle();
		return bitmap;
	}
	
	/**
	 * 获得设定大小的bitmap
	 * @param localSourceID  资源id
	 * @param layoutLp  资源大小
	 * @param context 上下文
	 * @return
	 */
	public static Bitmap getBitMap(String localSourceID, Context context,
			int width,int height) {
		Bitmap resultBitmap = null;
		try {
			if (HLSetting.IsResourceSD)
				resultBitmap = getBitmapFromSD(localSourceID, width, height);
			else
				resultBitmap = BitmapUtils.load(FileUtils.getInstance().getFileInputStream(
						context, localSourceID), width, height);


		} catch (OutOfMemoryError e) {
			Log.e("hl", "get bitmap error",e);
		}
		if(resultBitmap==null){
			Log.i("hl", "获取图片失败，图片名称是 " + localSourceID);
		}
		return resultBitmap;
	}
	
	public static Bitmap getBitMap(String localSourceID, Context context,
			int[] size) {
		Bitmap resultBitmap = null;
		try {
			if (HLSetting.IsResourceSD)
//				resultBitmap = getBitmapFromSD(localSourceID,size);
				resultBitmap = getWdyBitmapFromSD(localSourceID, size[0], size[1]);
			else
				resultBitmap = BitmapUtils.load(FileUtils.getInstance().getFileInputStream(
						context, localSourceID), size);


		} catch (OutOfMemoryError e) {
			Log.e("hl", "get bitmap error",e);
		}
		if(resultBitmap==null){
			Log.i("hl", "获取图片失败，图片名称是 " + localSourceID);
		}
		return resultBitmap;
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

	public static Bitmap getBitmapFromSD(String fileName, int[] size) {
		String actfileName = BookSetting.BOOK_PATH + "/" + fileName;
		Bitmap bitmap = null;
		//1先取得bitmap的size
		BitmapFactory.Options _option = new BitmapFactory.Options();
		_option.inJustDecodeBounds = true;
	    // 通过这个bitmap获取图片的宽和高&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
		BitmapFactory.decodeFile(actfileName, _option);
		
        float realWidth = _option.outWidth;
		if(realWidth>size[0]){
			_option.inSampleSize = (int) (realWidth/size[0]);
		}
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inJustDecodeBounds = false;
		_option.inTempStorage = new byte[16 * 1024];
		_option.outWidth = size[0];
		_option.outHeight = size[1];
		
		try {
			bitmap = BitmapFactory.decodeFile(actfileName, _option);
			if(bitmap==null){
				_option.inSampleSize = 10;
				bitmap = BitmapFactory.decodeFile(actfileName, _option);
			}
			size[0] = _option.outWidth;
			size[1] = _option.outHeight;
			return bitmap;
		} catch (Exception e) {
			Log.e("hl","load bitmap Exception");
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e("hl","load bitmap OutOfMemoryError");
			e.printStackTrace();
			_option.inSampleSize = 10;
			bitmap = BitmapFactory.decodeFile(actfileName, _option);
			return bitmap;
		} finally {
		}
		 if (bitmap == null)
	        {
	            Log.d("hl", "文件" +fileName+"创建bitmap为空");
	        }
		return null;

	}

	
	public static Bitmap getBitmapFromSD(String fileName, int width, int height) {
		String actfileName = BookSetting.BOOK_PATH + "/" + fileName;
		Bitmap bitmap = null;
		//1先取得bitmap的size
		BitmapFactory.Options _option = new BitmapFactory.Options();
		_option.inJustDecodeBounds = true;
	    // 通过这个bitmap获取图片的宽和高&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
		BitmapFactory.decodeFile(actfileName, _option);
       
		
        float realWidth = _option.outWidth;
		if(realWidth>width){
			_option.inSampleSize = (int) (realWidth/width);
		}
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inJustDecodeBounds = false;
		_option.inTempStorage = new byte[16 * 1024];
		_option.outHeight = height;
		_option.outWidth = width;
		
		try {
			bitmap = BitmapFactory.decodeFile(actfileName, _option);
			if(bitmap==null){
				_option.inSampleSize = 10;
				bitmap = BitmapFactory.decodeFile(actfileName, _option);
			}
			return bitmap;
		} catch (Exception e) {
			Log.e("hl","load bitmap Exception");
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e("hl","load bitmap OutOfMemoryError");
			e.printStackTrace();
			_option.inSampleSize = 10;
			bitmap = BitmapFactory.decodeFile(actfileName, _option);
			return bitmap;
		} finally {
		}
		return null;

	}

	private static Bitmap load(InputStream is, int[] size){
		if(is==null){
			return null;
		}
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
		if(is==null){
			return null;
		}
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
        if (bitmap == null)
        {
            System.out.println("bitmap为空");
        }
        float realWidth = _option.outWidth;
        
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


	public static int[] getBitmapSizeBySourceID(Context context, String sourceID) {
		if (HLSetting.IsResourceSD)
			return loadSize(FileUtils.getInstance().getFileInputStream(sourceID));
		else
			return loadSize(FileUtils.getInstance().getFileInputStream(context,
					sourceID));

	}
	
	public static Bitmap getBitmapBySourceID(Context context, String sourceID) {
		if (HLSetting.IsResourceSD)
			return load(FileUtils.getInstance().getFileInputStream(sourceID));
		else
			return load(FileUtils.getInstance().getFileInputStream(context,
					sourceID));

	}
	
	/**
	 * 取得最能使用大小的bitmap
	 * @param context
	 * @param sourceID
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getWrapBitmapBySourceID(Context context, String sourceID,int width,int height){
		int[] size = getBitmapSizeBySourceID(context,sourceID);
		int bitHeight = size[1];
		int bitWidth = size[0];
		
		int resultWidth = width;
		int resultHeight = (int) (((float)resultWidth*(float)bitHeight)/(float)bitWidth);
		if(resultHeight>height){
			resultHeight = height;
			resultWidth = (int) (((float)resultHeight*(float)bitWidth)/(float)bitHeight);
		}
		
		Bitmap resultBitmap = getBitMap(sourceID, context, resultWidth, resultHeight);
		return resultBitmap;
	}
	
	public static BitmapDrawable getBitmapDrawable(Context context, String sourceID){
		if (StringUtils.isEmpty(sourceID)){
			return null;
		}
		
		return new BitmapDrawable(getBitmapBySourceID(context,sourceID));
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
	
	/**
	 * 合并两张bitmap为一张
	 * @param background
	 * @param foreground
	 * @return Bitmap
	 */
	public static Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
		if (background == null) {
			return null;
		}
		int bgWidth = background.getWidth();
		int bgHeight = background.getHeight();
		int fgWidth = foreground.getWidth();
		int fgHeight = foreground.getHeight();
		Bitmap newmap = Bitmap
				.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(newmap);
		canvas.drawBitmap(background, 0, 0, null);
		canvas.drawBitmap(foreground, (bgWidth - fgWidth) / 2,
				(bgHeight - fgHeight) / 2, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newmap;
	}
}
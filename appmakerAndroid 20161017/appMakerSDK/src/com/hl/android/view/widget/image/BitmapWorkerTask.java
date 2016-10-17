package com.hl.android.view.widget.image;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.FileUtils;

public class BitmapWorkerTask extends AsyncTask<String, Bitmap, Bitmap> {
	private final WeakReference<ImageView> imageViewReference;
	private Context mContext;
	private Bitmap mBitmap;
	public String mFileID;

	public BitmapWorkerTask(ImageView imageView, Context context, String fileID) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
		mContext = context;
		this.mFileID = fileID;
		this.mContext = context;
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(String... params) {
		this.load(mFileID);
		return mBitmap;
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (this.isCancelled()){
			bitmap = null;
		}
		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = (ImageView) imageViewReference.get();
			if (imageView != null) {
				((CommonImageView)imageView).setBitmap(bitmap);
			}
		}
	}

	public void load(String fileID) {
		try {
			if (HLSetting.IsResourceSD)
				load(FileUtils.getInstance().getFileInputStream(fileID));
			else
				load(FileUtils.getInstance()
						.getFileInputStream(mContext, fileID));
		} catch (OutOfMemoryError e) {
			Log.e("hl", "load error",e);
		}

	}

	public void load(InputStream is) {
		BitmapFactory.Options _option = new BitmapFactory.Options();
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inTempStorage = new byte[32 * 1024];
		try {
			mBitmap = BitmapFactory.decodeStream(is, null, _option);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			_option.inSampleSize = 2;
			mBitmap = BitmapFactory.decodeStream(is, null, _option);
		}
	}

}
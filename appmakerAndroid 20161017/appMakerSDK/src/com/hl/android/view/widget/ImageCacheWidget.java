package com.hl.android.view.widget;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.hl.android.view.widget.image.BitmapWorkerTask;
import com.hl.android.view.widget.image.CommonImageView;

public class ImageCacheWidget {
	private Context mContext;
	private HashMap<String, AsyncTask<String, Bitmap, Bitmap>> mTaskMap = null;
	private ArrayList<String> mSourceIDS;
	private ArrayList<CommonImageView> mImageList;
	private AsyncTask<Integer, Integer, Integer> clearAsyncTask = null;
	private int imageHeight = 0;
	private int imageWidth = 0;
	public ImageCacheWidget(Context context, ArrayList<String> sourceIDS,
			ArrayList<CommonImageView> imageList) {
		mContext = context;
		this.mSourceIDS = sourceIDS;
		this.mImageList = imageList;
		mTaskMap = new HashMap<String, AsyncTask<String, Bitmap, Bitmap>>();
	}

	public void loadBitmap(int childIndex) {
		try {
			if (clearAsyncTask != null) {
				clearAsyncTask.cancel(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (((CommonImageView) mImageList.get(childIndex)).getBitmap() != null) {
			;
		} else {
			if (!isLoadingWork(mSourceIDS.get(childIndex),
					mImageList.get(childIndex))) {
				final BitmapWorkerTask task = new BitmapWorkerTask(
						mImageList.get(childIndex), mContext,
						mSourceIDS.get(childIndex));
				task.execute(mSourceIDS.get(childIndex));
			}
		}
		preLoad(childIndex);
		clear(childIndex);
	}

	private void preLoad(int childIndex) {
		int pre = 0;
		int next = 0;
		if (childIndex == 0) {
			pre = mImageList.size() - 1;
			next = 1;
		} else if (childIndex == mImageList.size() - 1) {
			pre = mImageList.size() - 2;
			next = 0;
		} else {
			pre = childIndex - 1;
			next = childIndex + 1;
		}

		if (((CommonImageView) mImageList.get(pre)).getBitmap() != null) {
			;
		} else {
			if (!isLoadingWork(mSourceIDS.get(pre), mImageList.get(pre))) {
				final BitmapWorkerTask task = new BitmapWorkerTask(
						mImageList.get(pre), mContext, mSourceIDS.get(pre));
				task.execute(mSourceIDS.get(pre));
			}
		}

		if (((CommonImageView) mImageList.get(next)).getBitmap() != null) {
			;
		} else {
			if (!isLoadingWork(mSourceIDS.get(next), mImageList.get(next))) {
				final BitmapWorkerTask task = new BitmapWorkerTask(
						mImageList.get(next), mContext, mSourceIDS.get(next));
				task.execute(mSourceIDS.get(next));
			}
		}

	}

	private void clear(int childIndex) {
		// Render the page in the background
		clearAsyncTask = new AsyncTask<Integer, Integer, Integer>() {
			protected Integer doInBackground(Integer... v) {
				int imageCount = mImageList.size();
				int pre = 0;
				int next = 0;
				if (v[0] == 0) {
					pre = mImageList.size() - 1;
					next = 1;
				} else if (v[0] == mImageList.size() - 1) {
					pre = mImageList.size() - 2;
					next = 0;
				} else {
					pre = v[0] - 1;
					next = v[0] + 1;
				}
				for (int i = 0; i < imageCount; i++) {
					if (i != v[0] && i != pre && i != next) {
						mImageList.get(i).recycle();
					}
				}
				return v[0];
			}

			protected void onPreExecute() {

			}
		};

		clearAsyncTask.execute(childIndex);

	}

	public boolean isLoadingWork(String data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(data);

		if (bitmapWorkerTask != null) {
			final String bitmapData = bitmapWorkerTask.mFileID;
			if (bitmapData.equals(data)) {
				return true;
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return false;
	}

	private BitmapWorkerTask getBitmapWorkerTask(String id) {
		return (BitmapWorkerTask) mTaskMap.get(id);
	}
	public void recycle(){
		int childCount = mImageList.size();
		for (int i =0;i<childCount;i++){
			mImageList.get(i).recycleImidiate();
		}
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	
}

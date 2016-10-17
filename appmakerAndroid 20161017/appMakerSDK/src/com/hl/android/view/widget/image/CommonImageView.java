package com.hl.android.view.widget.image;

import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hl.android.core.utils.BitmapUtils;

public class CommonImageView extends ImageView {
	private Bitmap mBitmap = null;
	private ReentrantLock lock = new ReentrantLock();

	public CommonImageView(Context context) {
		super(context);
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public void setBitmap(Bitmap mBitmap) {
		lock.lock();
		try {
			this.setScaleType(ScaleType.FIT_XY);
			this.setImageBitmap(mBitmap);
			this.mBitmap = mBitmap;
		} finally {
			lock.unlock();
		}

	}

	public void recycle() {
		mBitmap = null;
		this.post(new Runnable() {
			@Override
			public void run() {
				try {
					setBitmap(null);
					BitmapUtils.recycleBitmap(mBitmap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	public void recycleImidiate() {
		setBitmap(null);
		BitmapUtils.recycleBitmap(mBitmap);
	}

}

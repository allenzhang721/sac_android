package com.hl.realtest.data;

import android.os.Message;

import com.hl.realtest.shelves.ShelvesActivity;

public class BookDownRunnable implements Runnable {
	ShelvesActivity mActivity;
	public static boolean isDown = false;
	public BookDownRunnable(ShelvesActivity activity) {
		mActivity = activity;
	}

	@Override
	public void run() {
		while (mActivity.isRunning) {
			Book b = ShelvesDataManager.getDownLoadBook(mActivity);
			if (b != null) {
				Message msg = new Message();
				msg.obj = b;
				msg.what = ShelvesActivity.MSG_START_DOWN;
				isDown = true;
				mActivity.downhandle.sendMessage(msg);
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
			}else{
				isDown = false;
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
			}
		}
	}

}

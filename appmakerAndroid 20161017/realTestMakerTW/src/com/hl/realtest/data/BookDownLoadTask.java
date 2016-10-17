package com.hl.realtest.data;

import java.io.File;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.hl.common.HttpManager;
import com.hl.common.ProgressCallBack;
import com.hl.common.ZipUtil;
import com.hl.realtest.shelves.ShelvesActivity;

public class BookDownLoadTask extends AsyncTask<Book, Integer, Boolean> {
	Book mBook;
	ShelvesActivity mActivity;
	private long startTime = 0;
	public BookDownLoadTask(ShelvesActivity activity){
		mActivity = activity;
	}
	
	@Override
	protected Boolean doInBackground(Book... arg0) {
		mBook = arg0[0];
 		//RandomAccessFile randomAccessFile = null;
		startTime = System.currentTimeMillis();
		String dataFile = mBook.mData + "book.zip";  
		mBook.mIcon = mBook.mData + "cover.png"; 
		mBook.mName = mBook.mData + "bookName.text"; 
		File newFile = new File(dataFile);
		if (newFile.exists()) {
			newFile.delete();
		}
		boolean result = HttpManager.downLoadResourceWithProgress(mBook.downUrl, dataFile,new ProgressCallBack() {
				
				@Override
				public boolean doProgressAction(int arg0, int arg1) {
					double rate = (double)arg1/(double)arg0;
					mBook.mCurrentRate = rate;
					if (!mActivity.isDrawing)publishProgress(0);
					
					// 如果已经被删除
					if (mBook.state == -1) {
						BookDownRunnable.isDown = false;
						ShelvesDataManager.deleteBook(mActivity, mBook);
						return false;
					}
					if (mBook.state != 0) {
						BookDownRunnable.isDown = false;
						return false;
					}
					return true;
				}

				@Override
				public boolean downOver(int arg0) {
					mBook.mCurrentRate = 1.00;
					publishProgress(0);
					publishProgress(1);
					mBook.mBookCoverUrl = mBook.downUrl.replace("book.zip","cover.png");
					String nameUrl=mBook.downUrl.replace("book.zip","bookName.txt");
					//下载封皮
					mBook.mIcon = HttpManager.downLoadResource(mBook.mBookCoverUrl, mBook.mIcon);
					mBook.mName = HttpManager.downLoadResource(nameUrl, mBook.mName);
					// 解压zip
					try {
						ZipUtil.UnZipFolder(mBook.mData + "book.zip", mBook.mData);
					} catch (Exception e) {
						return false;
					}
					File zipFile = new File(mBook.mData + "book.zip");
					if (zipFile.exists()) {
						zipFile.delete();
					}
					ShelvesDataManager.finishBook(mActivity, mBook);
					if (!mActivity.isDrawing){
						publishProgress(2);
					}
					return true;
				}

				@Override
				public boolean startDown(int arg0) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			if(!result && mBook.state != -1){
				publishProgress(3);
			}
			Log.d("hl","download book end " + (System.currentTimeMillis()-startTime));
			return true;
	}
 


	@Override
	protected void onPostExecute(Boolean result) {
		
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Integer v = values[0];
		Message msg = new Message();
		msg.obj = mBook;
		if (v == 0) {
			msg.what = ShelvesActivity.MSG_PROGRESS;
		}else if(v== 1){
			msg.what = ShelvesActivity.MSG_DOWN_HIDEBTN;
		}else if(v==2){
			msg.what = ShelvesActivity.MSG_DOWN_OVER;
			BookDownRunnable.isDown = false;
		}else if(v== 3){
			msg.what = ShelvesActivity.MSG_LOCAL_FAIL;
			BookDownRunnable.isDown = false;
		}
		mActivity.downhandle.sendMessage(msg);
	}
}

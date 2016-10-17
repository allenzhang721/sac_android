package com.hl.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.ZipUtils;
import com.hl.callback.Action;

public class HLReader extends HLActivity {
	public static  String bookID="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			int readType = getIntent().getExtras().getInt("readtype");
			if (readType == 1) {
				String path = getIntent().getExtras().getString("readpath");
				initForPathReader(path);
			} else {
				initReaderForAssets();
			}
		} catch (Exception e) {
		}
		if (HLSetting.IS_ASSETS_ZIP || HLSetting.IS_SD_ZIP) {
			preLoadAction = new Action() {

				@Override
				public boolean doAction() {
					progressHandler.sendEmptyMessage(0);
					AsyncTask<Object, Object, Boolean> task = new AsyncTask<Object, Object, Boolean>() {
						@Override
						protected Boolean doInBackground(Object... arg0) {
							doZipRelease();
							return true;
						}

						protected void onPostExecute(Boolean result) {
							if (result) {
								progressHandler.sendEmptyMessageDelayed(2, 500);
								progressHandler.sendEmptyMessage(1);
							} else {
								progressHandler.sendEmptyMessage(0);
								Toast.makeText(HLReader.this,
										"解压数据文件出错,请检查您的apk文件是否损坏",
										Toast.LENGTH_LONG).show();
								finish();
								return;
							}
						};
					};
					task.execute();
					return false;
				}

			};

		}
		super.onCreate(savedInstanceState);
	}

	/**
	 * 打开assets
	 * 
	 * @param context
	 */
	public static void show(Context context) {
		initReaderForAssets();
		Intent intent = new Intent(context, HLReader.class);
		context.startActivity(intent);
	}

	public static void initReaderForAssets() {
		HLSetting.IsResourceSD = false;
		BookSetting.IS_READER = true;
		BookSetting.IS_HOR = true;
		BookSetting.IS_AUTOPAGE = false;
		BookSetting.ISSUBPAGE_ENABLE = true;
		BookSetting.IS_HOR = true;
		BookSetting.IS_REMEMBER_READPAGE = false;
		BookSetting.IS_HOR_VER = false;
		BookSetting.SCREEN_WIDTH = 480;
		BookSetting.SCREEN_HEIGHT = 800;

		BookSetting.SNAPSHOTS_WIDTH = 320;
		BookSetting.SNAPSHOTS_HEIGHT = 280;

		BookSetting.RESIZE_WIDTH = 1;
		BookSetting.RESIZE_HEIGHT = 1;
		BookSetting.RESIZE_COUNT = 1;
		BookSetting.FLIPCODE = 1;// 0 corner, 1 move 2 对开
		BookSetting.GALLEYCODE = 1;// 0 common, 1 3D
		// BookSetting.CURRENTBOOKID = "";
		// BookSetting.BOOK_PATH = "";
		HLSetting.FitScreen = true;

		HLSetting.IS_ASSETS_ZIP = false;
		HLSetting.IS_SD_ZIP = false;
	}

	/**
	 * 打开文件夹
	 * 
	 * @param context
	 * @param bookpath
	 *            book文件夹的全路径
	 */
	public static void show(Context context, String bookpath) {
		if (!new File(bookpath).exists()) {
			Toast.makeText(context, "传入的文件不存在", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = null;
		initForPathReader(bookpath);
		Log.d("hl", "open book " + HLSetting.IsResourceSD + "   path is "
				+ BookSetting.BOOK_PATH);
		intent = new Intent(context, HLReader.class);
		context.startActivity(intent);
	}

	public static void initForPathReader(String bookpath) {
		HLSetting.IsResourceSD = true;
		BookSetting.BOOK_PATH = bookpath;
		BookSetting.IS_READER = true;
		BookSetting.IS_HOR = true;
		BookSetting.IS_AUTOPAGE = false;
		BookSetting.ISSUBPAGE_ENABLE = true;
		BookSetting.IS_HOR = true;
		BookSetting.IS_REMEMBER_READPAGE = false;
		BookSetting.IS_HOR_VER = false;
		BookSetting.SCREEN_WIDTH = 480;
		BookSetting.SCREEN_HEIGHT = 800;

		BookSetting.SNAPSHOTS_WIDTH = 320;
		BookSetting.SNAPSHOTS_HEIGHT = 280;

		BookSetting.RESIZE_WIDTH = 1;
		BookSetting.RESIZE_HEIGHT = 1;
		BookSetting.RESIZE_COUNT = 1;
		BookSetting.FLIPCODE = 1;// 0 corner, 1 move 2 对开
		BookSetting.GALLEYCODE = 1;// 0 common, 1 3D

		HLSetting.IS_ASSETS_ZIP = false;
		HLSetting.IS_SD_ZIP = false;

		// BookSetting.CURRENTBOOKID = "";
		// BookSetting.BOOK_PATH = "";
		HLSetting.FitScreen = true;
	}

	/**
	 * 打开zip文件
	 * 
	 * @param context
	 * @param bookpath
	 *            zip文件路径
	 */
	public static void showZipFile(Context context, String bookpath) {
		if (!new File(bookpath).exists()) {
			Toast.makeText(context, "传入的文件不存在", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = null;
		HLSetting.IsResourceSD = true;
		BookSetting.BOOK_PATH = bookpath;
		BookSetting.IS_READER = true;
		BookSetting.IS_HOR = true;
		BookSetting.IS_AUTOPAGE = false;
		BookSetting.ISSUBPAGE_ENABLE = true;
		BookSetting.IS_HOR = true;
		BookSetting.IS_REMEMBER_READPAGE = false;
		BookSetting.IS_HOR_VER = false;
		BookSetting.SCREEN_WIDTH = 480;
		BookSetting.SCREEN_HEIGHT = 800;

		BookSetting.SNAPSHOTS_WIDTH = 320;
		BookSetting.SNAPSHOTS_HEIGHT = 280;

		BookSetting.RESIZE_WIDTH = 1;
		BookSetting.RESIZE_HEIGHT = 1;
		BookSetting.RESIZE_COUNT = 1;
		BookSetting.FLIPCODE = 1;// 0 corner, 1 move 2 对开
		BookSetting.GALLEYCODE = 1;// 0 common, 1 3D
		// BookSetting.CURRENTBOOKID = "";
		// BookSetting.BOOK_PATH = "";
		HLSetting.FitScreen = true;
		HLSetting.IS_ASSETS_ZIP = false;

		intent = new Intent(context, HLReader.class);
		HLSetting.IS_SD_ZIP = true;
		context.startActivity(intent);
	}

	/**
	 * 设置固定屏幕大小
	 * 
	 * @param initScreenWidth
	 *            屏幕宽
	 * @param initScreenHeight
	 *            屏幕高
	 */
	public static void setFixSize(int initScreenWidth, int initScreenHeight) {
		BookSetting.FIX_SIZE = true;
		BookSetting.INIT_SCREEN_WIDTH = initScreenWidth;
		BookSetting.INIT_SCREEN_HEIGHT = initScreenHeight;
	}

	/**
	 * 设置是否是书架
	 * 
	 * @param isShelves
	 *            true是来自书架，会出现返回按钮，false不是，返回按钮不会出现
	 */
	public static void setShelves(Boolean isShelves) {
		BookSetting.IS_SHELVES = isShelves;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void initAdView(View view) {

	}

	@Override
	protected View getAdView() {
		try {
			if (mAdRegster != null)
				return mAdRegster.getView(this);
		} catch (Exception e) {
			Log.e("hl", "adview get exception", e);
		}
		return null;
	}

	/**
	 * 解压压缩文件夹
	 * 
	 * @return
	 */
	private boolean doZipRelease() {
		if (HLSetting.IS_ASSETS_ZIP) {
			String releasePath = BookSetting.BOOK_RESOURCESD_ROOT
					+ getApplication().getPackageName() + "/";
			BookSetting.BOOK_PATH = releasePath + "book";
			HLSetting.IsResourceSD = true;
			AssetManager assetManager = getAssets();

			long start = System.currentTimeMillis();

			File f = new File(BookSetting.BOOK_PATH + "/book.xml");
			if (!f.exists()) {
				// 需要解压的对象
				InputStream zipFileName;
				try {
					zipFileName = assetManager
							.open(BookSetting.BOOK_RESOURCE_ZIP_NAME);
				} catch (IOException e) {
					return false;
				}
				ZipUtils.unzip(zipFileName, releasePath);
			}
			Log.i("hl", " unzip time is "
					+ (System.currentTimeMillis() - start));
			return true;
		}

		if (HLSetting.IS_SD_ZIP) {
			String releasePath = BookSetting.BOOK_RESOURCESD_ROOT
					+ getApplication().getPackageName();
			String zipPath = BookSetting.BOOK_PATH;
			int lastSepIndex = zipPath.lastIndexOf("/");
			int lastDotIndex = zipPath.lastIndexOf(".");
			releasePath = releasePath
					+ zipPath.substring(lastSepIndex, lastDotIndex);

			BookSetting.BOOK_PATH = releasePath + "/book";
			HLSetting.IsResourceSD = true;
			File f = new File(BookSetting.BOOK_PATH + "/book.xml");
			if (!f.exists()) {
				try {
					ZipUtils.UnZipFolder(zipPath, releasePath);
				} catch (Exception e) {
					Toast.makeText(this, "解压出错，请检查您的zip文件是否有误",
							Toast.LENGTH_LONG).show();
					return false;
				}
			}
			return true;
		}
		return true;
	}

	AdViewRegister adViewSupporter;
	private static AdViewRegister mAdRegster;

	/**
	 * 注册广告
	 * 
	 * @param adRegster
	 */
	public static void setAdRegster(AdViewRegister adRegster) {
		mAdRegster = adRegster;
	}

	@Override
	protected void onDestroy() {
		if (mAdRegster != null)
			mAdRegster.recyle(this);
		super.onDestroy();
	}
}

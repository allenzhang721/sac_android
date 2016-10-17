package com.hl.android.view.component.moudle.bookshelf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.DataUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.core.utils.WebUtils;
import com.hl.android.core.utils.ZipUtils;
import com.hl.android.view.component.inter.Component;

public class BookShelfUIComponent extends RelativeLayout  implements Component {
	private MoudleComponentEntity componentEntity;
	private Context mContext;
	private int bookWidth;
	private int bookHeight;
	private int layoutWidth;
	private int layoutHeight;
	private LinearLayout progressBarContainer;
	private GridView booksGridView;
	private ShelvesAdapter booksViewAdapter;
	List<ShelvesBook> books = new ArrayList<ShelvesBook>();
	
	public BookShelfUIComponent(Context context) {
		super(context);
		mContext = context;
	}

	public BookShelfUIComponent(Context context, ComponentEntity entity) {
		super(context);
		setEntity(entity);
		mContext = context;

	}

	@Override
	public ComponentEntity getEntity() {
		return componentEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		componentEntity = (MoudleComponentEntity) entity;
	}

	public boolean isStop = false;
	@Override
	public void load() {
		
		isStop = false;
		//计算书架大小以及书皮的大小
		layoutWidth = getLayoutParams().width;
		layoutHeight = getLayoutParams().height;
		
		booksGridView = new GridView(mContext);
		addView(booksGridView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		
		progressBarContainer = new LinearLayout(mContext);
		progressBarContainer.setOrientation(LinearLayout.VERTICAL);
		progressBarContainer.setGravity(Gravity.CENTER_HORIZONTAL);
		
		TextView text = new TextView(mContext);
		text.setTextColor(Color.BLACK);
		text.setText("获取书架数据，请稍候...");
		progressBarContainer.addView(text, layoutWidth,layoutHeight);
		
		ProgressBar progressBar = new ProgressBar(mContext);
		progressBarContainer.addView(progressBar, layoutWidth,layoutHeight);
		

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				layoutWidth, layoutHeight);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(progressBarContainer, lp);
		
		bookWidth = componentEntity.getBookWidth();
		bookHeight = componentEntity.getBookHeight();
		if (HLSetting.FitScreen) {
			bookWidth = (int) (bookWidth * BookSetting.RESIZE_WIDTH);
			bookHeight = (int) (bookHeight * BookSetting.RESIZE_HEIGHT);
		} else {
			bookWidth = (int) (bookWidth * BookSetting.RESIZE_COUNT);
			bookHeight = (int) (bookHeight * BookSetting.RESIZE_COUNT);
		}
		
	
		
		Bitmap bitmap = BitmapUtils.getBitMap(componentEntity.getBgSourceID(), getContext(), layoutWidth, layoutHeight);
		setBackgroundDrawable(new BitmapDrawable(bitmap));

		//下载书皮
		AsyncTask<String,String,Boolean> task = new AsyncTask<String,String,Boolean>(){
			@Override
			protected Boolean doInBackground(String... params) {
				return initBookShelvesData(params[0]);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if(result){
					drawBookshelfView();
				}else{
					Toast.makeText(mContext, "同步数据出错", Toast.LENGTH_LONG).show();
					removeView(progressBarContainer);
				}
			}
		};
		String serverAddress = componentEntity.getServerAddress();
		task.execute(serverAddress);
	}

		
	public void showBook(ShelvesBook book) {
		try {
			
			BookSetting.BOOK_PATH = book.mLocalPath + "/book/";
			HLSetting.IsResourceSD = true;
			BookSetting.IS_SHELVES_COMPONENT = true;
			BookController.getInstance().openShelves();
		} catch (Exception e) {

		}
	}
	
	/**
	 * 获取书籍是否下载完毕的状态
	 * 如果是已经下载，则需要在增加文件是否存在的判断
	 * @param bookID
	 * @return 0是未下载1是已下载 2是正在下载
	 */
	private int getBookDownState(ShelvesBook book){
		return DataUtils.getPreference((Activity)mContext, book.mLocalPath, 0);
	}

	
	/**
	 * 绘画视图
	 */
	private void drawBookshelfView() {
		removeView(progressBarContainer);
		
		booksGridView.setNumColumns(layoutWidth / bookWidth);
		booksGridView.setGravity(Gravity.CENTER_HORIZONTAL);
		booksViewAdapter = new ShelvesAdapter(mContext, bookWidth, bookHeight);
		booksGridView.setAdapter(booksViewAdapter);
		booksGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ShelvesBook book = (ShelvesBook) arg0.getItemAtPosition(arg2);
				
				File bookFile = new File(book.mLocalPath + "/book/book.xml");
				if(bookFile.exists()){
					DataUtils.savePreference((Activity)mContext, book.mLocalPath, 1);
					book.mState = 1;
					showBook(book);
					return;
				}
				
				//如果是正在下载则直接返回
				if(book.mState ==2){
					return;
				}
				//如果下载地址是空的，也直接返回
				if (StringUtils.isEmpty(book.mBookUrl)) {
					return;
				} 
				book.mState = getBookDownState(book);
				//如果是已经下载完成但是没有书籍
				if (book.mState==1) {
					DataUtils.savePreference((Activity)mContext, book.mLocalPath, 0);
					book.mState = 0;
					
				}
				//if(book.mState == 0){
					//设置书籍正在下载
				DataUtils.savePreference((Activity)mContext, book.mLocalPath, 2);
				DownloadBookTask task = new DownloadBookTask(book, (Activity) mContext);
				task.execute(book.mBookUrl);
				//}
			}
		});
		downHandler.sendEmptyMessageDelayed(1, 500);
	}
	/**
	 * 初始化的时候启动上次下载线程
	 */
	Handler downHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			for(ShelvesBook book:books){
				book.mState = getBookDownState(book);
				//如果是正在下载则直接返回
				if(book.mState ==2){
					DownloadBookTask task = new DownloadBookTask(book, (Activity) mContext);
					task.execute(book.mBookUrl);
				}
			}
		};
	};
	
	/**
	 * 下载书籍的任务类
	 * @author zhaoq
	 *
	 */
	class DownloadBookTask extends AsyncTask<String, Integer, String> {
		HorizontalProgressBar progressBar;
		ViewGroup bookView;
		private ShelvesBook mBook;
		double rate = 0;
		Activity activity;
		public DownloadBookTask(ShelvesBook book, Activity activity) {
			super();
			mBook = book;
			this.activity = activity;
		}

		@Override
		protected String doInBackground(String... arg0) {
	 		FileOutputStream fout = null;
			FileOutputStream sout = null;
			HttpURLConnection conn = null;
			RandomAccessFile randomAccessFile = null;
			InputStream in = null;
			try {
				String dataFile = mBook.mLocalPath + "/book.zip";//
				File newFile = new File(dataFile);
				if (!newFile.exists()) {
					newFile.createNewFile();
				}

				long fileRealSize = newFile.length();
				URL u = new URL(mBook.mBookUrl);
				conn = (HttpURLConnection) u.openConnection();
				conn.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.1.4322)");
				String sProperty = "bytes=" + fileRealSize + "-";
				conn.setRequestProperty("RANGE", sProperty);
				randomAccessFile = new RandomAccessFile(newFile, "rwd");
				randomAccessFile.seek(fileRealSize);
				// 文件大小
				int contentLength = conn.getContentLength();

				rate = ((double) (newFile.length()) / (fileRealSize + contentLength));
				if(rate <1.0){
					// 如果网络地址上存在这个文件，直接下载，如果不存在，返回false，下载失败
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK ||
							conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
						in = conn.getInputStream();
						byte[] perSize = new byte[1024];
						int read = 0;
						int length = 0;
						double lastRate = 0;

						while ((read = in.read(perSize, 0, 1024)) != -1) {
							if(isStop){
								return "pause";
							}
							length = read;
							randomAccessFile.write(perSize, 0, length);
							rate = ((double) (newFile.length()) / (fileRealSize + contentLength));
							rate = 0.9*rate;
							if (rate > (lastRate + 0.0001)) {
								publishProgress(0);
								lastRate = rate;
							}
							if (activity.isFinishing()) {
								return null;
							}
						}
						// 下载尚未完成，直接返回
						if (newFile.length() < (fileRealSize + contentLength)) {
							Toast.makeText(activity, "网络故障，下载已中断", Toast.LENGTH_LONG).show();
							publishProgress(2);
							return null;
						}
						publishProgress(0);
						
					} else {
						Toast.makeText(activity, "网络故障，下载已中断", Toast.LENGTH_LONG).show();
						publishProgress(2);
						return null;
					}
				}
			
				// 解压zip
				rate = 0.9;
				publishProgress(0);
				ZipUtils.UnZipFolder(dataFile,mBook.mLocalPath);
				rate = 1.0;
				publishProgress(0);
				publishProgress(1);
				return "success";
				
			} catch (Exception e) {
				e.printStackTrace();
				publishProgress(2);
				return null;
			} finally {
				if (randomAccessFile != null) {
					try {
						randomAccessFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					conn.disconnect();
				}
				if (fout != null) {
					try {
						fout.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (sout != null) {
					try {
						sout.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int result = values[0];
			
			bookView = (ViewGroup) findViewById(mBook.viewID);
			if(bookView != null){
				progressBar = (HorizontalProgressBar) bookView.findViewById(ShelvesAdapter.PROGRESSBAR_ID);
				if(progressBar == null)return;
				if(result == 1){
					if(progressBar!=null){
						progressBar.setVisibility(View.GONE);
						Toast.makeText(activity, "下载完成", Toast.LENGTH_LONG).show();
						mBook.mState = 1;
						DataUtils.savePreference((Activity)mContext, mBook.mLocalPath, 1);
						Log.d("hl",mBook.mBookID + " 下载完成");
					}
				}
				if(result == 0){
					Log.d("hl",mBook.mBookID + "下载进度" + rate);
					progressBar.setProgress((int) (rate * progressBar.getMax()));
					progressBar.setVisibility(View.VISIBLE);
					mBook.mState = 2;
				}
				if(result==2){
					progressBar.setProgress(0);
					progressBar.setVisibility(View.GONE);
					if (result == 0) {
						Toast.makeText(activity, "网络故障，下载已中断", Toast.LENGTH_LONG).show();
					}
					mBook.mState = 0;
				}
			}
			
		}
	}
	
	/**
	 * 解析书架的
	 * 首先去缓存里面去获取数据
	 * 如果缓存里面没有，那么我们就去服务器上去取
	 * @param shelfUrlStr
	 * @return
	 */
	private boolean initBookShelvesData(String shelfUrlStr) {
		String key = mContext.getPackageName() + "." + componentEntity.getComponentId() + ".serverAddr";
		String content = DataUtils.getPreference((Activity) mContext, key, null);
		if (StringUtils.isEmpty(content)) {
			if(WebUtils.isConnectingToInternet((Activity) mContext)){
				content = WebUtils.getUrlContent(shelfUrlStr, "UTF-8");
				if(!StringUtils.isEmpty(content)){
					DataUtils.savePreference((Activity) mContext, key, content);
				}
			}
			if(StringUtils.isEmpty(content)){
				Log.d("hl","从服务器获取书架信息出错，并且同时本地数据也未存在");
				return false;
			}
		}
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			BookshelfXMLParseHandler xmlParseHandler = new BookshelfXMLParseHandler(books);
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(xmlParseHandler);
			xmlReader.parse(new InputSource(new StringReader(content)));
			return true;

		}catch (Exception e) {
			Log.e("hl","同步书架内容出错",e);
			return false;
		}
	}
	
	/**
	 * 书架需要使用adapter
	 * @author zhaoq
	 *
	 */
	class ShelvesAdapter extends BaseAdapter {
        private Context mContext;
        private int imgWidth;
        private int imgHeight;
        public static final int PROGRESSBAR_ID = 100000;

		public ShelvesAdapter(Context context, int imgWidth, int imgHeight) {
			mContext = context;
			this.imgWidth = imgWidth;
			this.imgHeight = imgHeight;
		}
        public int getCount() {
        	return books.size();
        }
        public Object getItem(int arg0) {
        	return books.get(arg0);
        }
        public long getItemId(int arg0) {
        	return arg0;
        }

        Bitmap oldBitmap = null;
		public View getView(final int position, View convertView, final ViewGroup parent) {
			RelativeLayout bookView;
			if (convertView == null) {
				bookView = getBookView();
			} else {
				bookView = (RelativeLayout) convertView;
				//oldBitmap = ((BitmapDrawable)bookView.getBackground()).getBitmap();
			}
			//初始化书籍视图的属性
			HorizontalProgressBar progressBar = (HorizontalProgressBar) bookView.findViewById(PROGRESSBAR_ID);
			ShelvesBook book = (ShelvesBook) getItem(position);
			bookView.setId(book.viewID);
			progressBar.setVisibility(View.GONE);
			Bitmap coverBitmap = getCoverBitmap(book);
			if (coverBitmap != null) {
				bookView.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(),coverBitmap));
			} else {
				bookView.setGravity(Gravity.CENTER);
				TextView failText = new TextView(mContext);
				bookView.addView(failText);
			}
			//回收掉老的bitmap
			if(oldBitmap != null){
				oldBitmap.recycle();
				oldBitmap = null;
			}
			return bookView;
		}
		/**
		 * 创建书籍视图
		 * @return
		 */
		public RelativeLayout getBookView() {
			RelativeLayout bookView = new RelativeLayout(mContext);
			bookView.setLayoutParams(new GridView.LayoutParams(imgWidth, imgHeight));
			bookView.setGravity(Gravity.BOTTOM);
			HorizontalProgressBar progressBar = new  HorizontalProgressBar(mContext);
			progressBar.setVisibility(View.GONE);
			progressBar.setId(PROGRESSBAR_ID);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 10);
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			lp.setMargins(10, 0, 10, 0);
			bookView.addView(progressBar, lp);
			return bookView;
		}
    }
	
	/**
	 * 获得书籍的封皮bitmap
	 * 如果返回为空怎么办？那就不显示吧
	 * @param book
	 * @return
	 */
	private Bitmap getCoverBitmap(ShelvesBook book) {
		File coverFile = new File(book.mCoverPath);
		if(coverFile.exists()){
			try {
				Bitmap bp = BitmapUtils.load(new FileInputStream(coverFile), bookWidth, bookHeight);
				return bp;
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		return null;
	}

/***********************通用组件需要使用的方法，但是我们不需要调整，就是什么都不做**********************************************/	
	@Override
	public void load(InputStream is) {
		
	}

	@Override
	public void play() {

		Log.d("hl","play");
	}

	@Override
	public void stop() {
		isStop = true;
		Log.d("hl","stop");
	}

	@Override
	public void hide() {

		Log.d("hl","hide");
	}

	@Override
	public void show() {

		Log.d("hl","show");
	}

	@Override
	public void resume() {

		Log.d("hl","resume");
	}

	@Override
	public void pause() {
		Log.d("hl","pause");
	}
}
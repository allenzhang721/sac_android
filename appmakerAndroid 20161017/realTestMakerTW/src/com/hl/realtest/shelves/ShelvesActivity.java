package com.hl.realtest.shelves;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.helper.StringUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hl.android.HLReader;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.common.BitmapCache;
import com.hl.common.DataUtils;
import com.hl.realtest.ScreenAdapter;
import com.hl.realtest.data.Book;
import com.hl.realtest.data.BookDownLoadTask;
import com.hl.realtest.data.BookDownRunnable;
import com.hl.realtest.data.ShelvesDataManager;
import com.hl.realtest.view.TasksCompletedView;
import com.hl.realtestTW2.R;

/**
 * 书架展示的activity
 * 
 * @author zhaoq
 * 
 */
public class ShelvesActivity extends Activity {
	public boolean isRunning = true;
	// 装载书架格的容器
	private LinearLayout layoutAction;
	private Button btnEdit;
	private Button btnAdd;
	private Button btnInfor;

	// 计算书架的高度
	// 是否持续下载的标识
	Book lastBook;
	ProgressBar lastBar;

	BookShelvesView scrollView;
	public final static int SCROLL_MSG_INIT = 100000;
	public final static int SCROLL_MSG_TOUCH = 100001;
	// 下面是弹出框用的的变量
	private View layCover;
	private View layAdd;
	private EditText editAdd;
	private View btnOkAdd;
	private View btnCancelAdd;
	private View tittleIv;

	private View layDel;
	private View btnOkDel;
	private View btnCancelDel;

	private View layClose;
	private View btnOkClose;
	private View btnCancelClose;

	private View layAbout;
	private View layAlpha;
	private String version="1.0";
	private String htmlStr = "<font color=\"#6d6d6d\">Versions&nbsp;&nbsp;"
			+ version
			+ "</font>";
	private TextView versionTv;

	public static String IS_PROBATION_KEY = "com.hl.probation.key";

	private EditText editInputDumy;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		String dbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hl/realtesttw/";
		DataUtils.initDBFilePath(dbPath);
		//更新版本日期，这个日期是被批处理指令进行更新的
//		try {
//			String date = com.hl.realtest.common.FileUtil.inputStream2String(this.getAssets().open("v.txt"));
//			htmlStr  = htmlStr.replace("date", date);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		BookDownRunnable.isDown = false;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//
		isRunning = true;
		ScreenUtils.getScreenHeight(this);
		ScreenUtils.getScreenWidth(this);

		super.onCreate(savedInstanceState);

		// 检查表并且创建表
		ShelvesDataManager.detectAndCreateBookTable(this);
		// 初始化书籍列表
		ShelvesDataManager.initBookList(this);

		setContentView(R.layout.shelveslayout);
		initUI();
		ShelvesDataManager.initBookDownRunnable(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isRunning = true;
		BookShelvesView.EDITABLE = false;
		btnEdit.setBackgroundResource(R.drawable.edit);
		scrollView.setEditable(BookShelvesView.EDITABLE);
		// toggleEditView(false);
		refreshShelves();
		ShelvesDataManager.notifyBookDownRunnable(ShelvesActivity.this);
		// System.gc();
	}
	//放大的比例
	public static float myRatio = 1.5f;
	/**
	 * 实例化页面对象 并设置初始属性
	 */
	private void initUI() {
		layoutAction = (LinearLayout) this.findViewById(R.id.layoutAction);
		scrollView = (BookShelvesView) this.findViewById(R.id.scrollView);
		scrollView.setSize(ScreenUtils.getScreenWidth(this),
				ScreenUtils.getScreenHeight(this));
//		scrollView.setBookSize(150, 230);
		scrollView.initView();

		btnEdit = (Button) this.findViewById(R.id.btnEdit);
		btnAdd = (Button) this.findViewById(R.id.btnAddress);
		btnInfor = (Button) this.findViewById(R.id.btnAbout);

		btnEdit.setOnClickListener(btnListener);
		btnAdd.setOnClickListener(btnListener);
		btnInfor.setOnClickListener(btnListener);
		// int bookWidth = 190;
		// int bookHeight = 240;//(int) (1.95*bookWidth);//shelveHeight * 2 / 3;
		// int bookTopMaring = 0;//shelveHeight / 6;

		layCover = this.findViewById(R.id.layCover);

		layAdd = this.findViewById(R.id.layAdd);
		btnOkAdd = this.findViewById(R.id.btnOkAdd);
		btnCancelAdd = this.findViewById(R.id.btnCancelAdd);
		editAdd = (EditText) this.findViewById(R.id.editInputAdd);
		
		BookShelvesView.formatAddTextViewSize(this, editAdd);
		btnOkAdd.setOnClickListener(btnListener);
		btnCancelAdd.setOnClickListener(btnListener);
		
		layDel = this.findViewById(R.id.layDel);
		btnOkDel = this.findViewById(R.id.btnOkDel);
		btnCancelDel = this.findViewById(R.id.btnCancelDel);

		btnOkDel.setOnClickListener(btnListener);
		btnCancelDel.setOnClickListener(btnListener);

		layClose = this.findViewById(R.id.layClose);
		btnOkClose = this.findViewById(R.id.btnOkClose);
		btnCancelClose = this.findViewById(R.id.btnCancelClose);

		btnOkClose.setOnClickListener(btnListener);
		btnCancelClose.setOnClickListener(btnListener);
		layAbout = this.findViewById(R.id.layAbout);
		
		tittleIv=findViewById(R.id.iv_centerttt);
		tittleIv.getLayoutParams().width=ScreenAdapter.calcWidth(151*myRatio);
		tittleIv.getLayoutParams().height=ScreenAdapter.calcWidth(49*myRatio);
		((FrameLayout.LayoutParams)tittleIv.getLayoutParams()).topMargin=ScreenAdapter.calcWidth(40*myRatio);
		
		layAlpha = this.findViewById(R.id.layAlpha);
		layAlpha.getBackground().setAlpha(100);
		editInputDumy = (EditText) this.findViewById(R.id.editInputDumy);
		
		
		//formate view size

		layoutAction.getLayoutParams().width = ScreenAdapter.calcWidth(1536);
		layoutAction.getLayoutParams().height = ScreenAdapter.calcWidth(128*myRatio);
		
		layAbout.getLayoutParams().width = ScreenAdapter.calcWidth(924);
		layAbout.getLayoutParams().height = ScreenAdapter.calcWidth(722);
		versionTv=(TextView) this.findViewById(R.id.versiontext);
		versionTv.setText(Html.fromHtml(htmlStr));
		versionTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,ScreenAdapter.calcWidth(20*myRatio));
		versionTv.setTypeface(null, Typeface.BOLD);
		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.rightMargin=ScreenAdapter.calcWidth(70);
		layoutParams.topMargin=ScreenAdapter.calcWidth(90);
		versionTv.setLayoutParams(layoutParams);
		versionTv.setTextColor(Color.BLACK);
		
		btnInfor.getLayoutParams().width = ScreenAdapter.calcWidth(88*myRatio);
		btnInfor.getLayoutParams().height = ScreenAdapter.calcWidth(88*myRatio);
		
		btnAdd.getLayoutParams().width = ScreenAdapter.calcWidth(88*myRatio);
		btnAdd.getLayoutParams().height = ScreenAdapter.calcWidth(88*myRatio);
		
		btnEdit.getLayoutParams().width = ScreenAdapter.calcWidth(88*myRatio);
		btnEdit.getLayoutParams().height = ScreenAdapter.calcWidth(88*myRatio);

		layAdd.getLayoutParams().width = ScreenAdapter.calcWidth(536*myRatio);
		layAdd.getLayoutParams().height = ScreenAdapter.calcWidth(344*myRatio);
		
		editAdd.getLayoutParams().width = ScreenAdapter.calcWidth(458*myRatio);
		editAdd.getLayoutParams().height = ScreenAdapter.calcWidth(64*myRatio);
		((MarginLayoutParams)editAdd.getLayoutParams()).topMargin = ScreenAdapter.calcWidth(136*myRatio);
		((MarginLayoutParams)editAdd.getLayoutParams()).leftMargin = ScreenAdapter.calcWidth(45*myRatio);

		btnOkAdd.getLayoutParams().width = ScreenAdapter.calcWidth(254*myRatio);
		btnOkAdd.getLayoutParams().height = ScreenAdapter.calcWidth(86*myRatio);
		((MarginLayoutParams)btnOkAdd.getLayoutParams()).topMargin = ScreenAdapter.calcWidth(245*myRatio);
		((MarginLayoutParams)btnOkAdd.getLayoutParams()).rightMargin = ScreenAdapter.calcWidth(14*myRatio);

		btnCancelAdd.getLayoutParams().width = ScreenAdapter.calcWidth(254*myRatio);
		btnCancelAdd.getLayoutParams().height = ScreenAdapter.calcWidth(86*myRatio);
		((MarginLayoutParams)btnCancelAdd.getLayoutParams()).topMargin = ScreenAdapter.calcWidth(245*myRatio);
		((MarginLayoutParams)btnCancelAdd.getLayoutParams()).leftMargin = ScreenAdapter.calcWidth(14*myRatio);
		
		layDel.getLayoutParams().width = ScreenAdapter.calcWidth(536*myRatio);
		layDel.getLayoutParams().height = ScreenAdapter.calcWidth(344*myRatio);
		
		btnOkDel.getLayoutParams().width = ScreenAdapter.calcWidth(254*myRatio);
		btnOkDel.getLayoutParams().height = ScreenAdapter.calcWidth(86*myRatio);
		((MarginLayoutParams)btnOkDel.getLayoutParams()).topMargin = ScreenAdapter.calcWidth(244*myRatio);
		((MarginLayoutParams)btnOkDel.getLayoutParams()).leftMargin = ScreenAdapter.calcWidth(14*myRatio);

		btnCancelDel.getLayoutParams().width = ScreenAdapter.calcWidth(254*myRatio);
		btnCancelDel.getLayoutParams().height = ScreenAdapter.calcWidth(86*myRatio);
		((MarginLayoutParams)btnCancelDel.getLayoutParams()).topMargin = ScreenAdapter.calcWidth(244*myRatio);
		((MarginLayoutParams)btnCancelDel.getLayoutParams()).rightMargin = ScreenAdapter.calcWidth(14*myRatio);
		
		layClose.getLayoutParams().width = ScreenAdapter.calcWidth(536*myRatio);
		layClose.getLayoutParams().height = ScreenAdapter.calcWidth(344*myRatio);
		
		
		btnOkClose.getLayoutParams().width = ScreenAdapter.calcWidth(254*myRatio);
		btnOkClose.getLayoutParams().height = ScreenAdapter.calcWidth(86*myRatio);
		((MarginLayoutParams)btnOkClose.getLayoutParams()).topMargin = ScreenAdapter.calcWidth(245*myRatio);
		((MarginLayoutParams)btnOkClose.getLayoutParams()).rightMargin = ScreenAdapter.calcWidth(14*myRatio);

		btnCancelClose.getLayoutParams().width = ScreenAdapter.calcWidth(254*myRatio);
		btnCancelClose.getLayoutParams().height = ScreenAdapter.calcWidth(86*myRatio);
		((MarginLayoutParams)btnCancelClose.getLayoutParams()).topMargin = ScreenAdapter.calcWidth(245*myRatio);
		((MarginLayoutParams)btnCancelClose.getLayoutParams()).leftMargin = ScreenAdapter.calcWidth(14*myRatio);
	}

	public boolean isDrawing = false;

	// 注意的是删除都需要重新刷新书架
	public void refreshShelves() {
		isDrawing = true;
		scrollView.clearBookView();
		ArrayList<Book> bookList = ShelvesDataManager.getBookList(this);

		for (Book book : bookList) {
			if (book.state == -1)
				continue;
			scrollView.addBookView(book);
		}
		isDrawing = false;
	}

	OnClickListener btnListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			int btnID = arg0.getId();
			switch (btnID) {
			case R.id.btnEdit:
				doEditAction(arg0);
				break;
			case R.id.btnAddress:
				showAdd();
				break;
			case R.id.btnOkAdd:
				doOkAddAction();
				break;
			case R.id.btnOkDel:
				Book b1 = (Book) arg0.getTag();
				doOkDelAction(b1);
				hidePop();
				break;
			case R.id.btnOkClose:
				doOkCloseAction();
				break;
			case R.id.btnCancelAdd:
			case R.id.btnCancelDel:
			case R.id.btnCancelClose:
				hidePop();
				break;
			case R.id.btnAbout:
				doAboutAction();
				break;
			}
		}

	};

	/**
	 * 删除的确定按钮点击
	 */
	private void doOkDelAction(Book mBook) {
		if (mBook.state == 0) {
			mBook.state = -1;
		}
		ShelvesDataManager.deleteBook(ShelvesActivity.this, mBook);
		downhandle.sendEmptyMessage(MSG_REFRESH);
	}

	/**
	 * 关闭的确定按钮点击
	 */
	private void doOkCloseAction() {
		BookShelvesView.EDITABLE = false;

		isRunning = false;

		ShelvesDataManager.releaseBookDownRunnable();
		BookDownRunnable.isDown = false;
		
		finish();
	}

	/**
	 * 弹出框的确定按钮点击
	 */
	private void doOkAddAction() {
		hidePop();
		String url = editAdd.getText().toString();
		storeData(url);
		if (!StringUtil.isBlank(url)) {

			DataUtils.savePreference(ShelvesActivity.this, "serverip", url);
			// 下载新书
			downLoadLocalBook(url);
		} else {
			Toast.makeText(ShelvesActivity.this,
					R.string.bookcity_ipcannotbeempty, Toast.LENGTH_LONG)
					.show();
			return;
		}
	}

	private void storeData(String url) {
		DataUtils.savePreference(this, "IP", url);
	}

	private String getStoreData(String key) {
		return DataUtils.getPreference(this, key, null);
	}

	/**
	 * 其实就是删除功能 点击以后删除按钮就出现了
	 * 
	 * @param arg0
	 */
	private void doEditAction(View arg0) {
		BookShelvesView.EDITABLE = !BookShelvesView.EDITABLE;
		if (BookShelvesView.EDITABLE) {
			btnEdit.setBackgroundResource(R.drawable.done);
		} else {
			btnEdit.setBackgroundResource(R.drawable.edit);
		}
		scrollView.setEditable(BookShelvesView.EDITABLE);
	}

	private void downLoadLocalBook(String bookUrl) {

		// String suffiex =
		String result = "http://%c:9426/publish/book/file.hl?bookID=1000&fileName=book.zip"
				.replace("%c", bookUrl);
		Book book = new Book();
		book.downUrl = result;
		book.mIcon = "";
		book.bookID = "zjcs" + Double.toString(Math.random());
		book.state = 0;
		ShelvesDataManager.createNewBook(this, book);
		ShelvesDataManager.notifyBookDownRunnable(ShelvesActivity.this);
		refreshShelves();

	}

	// 点击书籍的事件
	OnClickListener bookClickListsner = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// 如果处于编辑状态不允许进入读书页面
			if ((Boolean) btnEdit.getTag())
				return;
			Book b = (Book) arg0.getTag();
			HLReader.show(ShelvesActivity.this, b.mData + "/book/");
		}

	};

	public static String state_edit_key = "hl.edit.state";

	// 如果是3.0的话也会被调用
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// initUI();
	// refreshShelves();
	// super.onConfigurationChanged(newConfig);
	// }

	/**
	 * 我们通过消息的方式来处理ui的变化
	 */
	public Handler downhandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			Book b = null;
			RelativeLayout mBookLayout = null;
			ProgressBar zipPb = null;
			TasksCompletedView mPb = null;
			switch (what) {

			case MSG_DOWN_HIDEBTN:
				b = (Book) msg.obj;
				mBookLayout = (RelativeLayout) findViewById(b.mOrder);
				if (mBookLayout == null)
					return;
				mPb = (TasksCompletedView) mBookLayout
						.findViewById(BookShelvesView.BOOKVIEW_PROGRESS_ID);
				mPb.setVisibility(View.GONE);
				zipPb = (ProgressBar) mBookLayout
						.findViewById(BookShelvesView.BOOKVIEW_UNZIPPROGRESS_ID);
				zipPb.setVisibility(View.VISIBLE);
				/*
				 * ImageView action = (ImageView) mBookLayout
				 * .findViewById(R.id.imgAction);
				 * action.setBackgroundResource(R.drawable.unzip);
				 * action.setOnClickListener(null);
				 */
				break;

			case MSG_DOWN_OVER:
				b = (Book) msg.obj;
				mBookLayout = (RelativeLayout) findViewById(b.mOrder);
				if (mBookLayout == null)
					return;
				zipPb = (ProgressBar) mBookLayout
						.findViewById(BookShelvesView.BOOKVIEW_UNZIPPROGRESS_ID);
				zipPb.setVisibility(View.GONE);
				ImageView bookCover = (ImageView) mBookLayout.findViewById(BookShelvesView.BOOKVIEW_COVER_ID);
				if(!StringUtils.isEmpty(b.mIcon)){
					Bitmap coverbitmap = BitmapCache.getSimpleBitmap(b.mIcon, b.mIcon);
					if(bookCover!=null)bookCover.setImageBitmap(coverbitmap);
				}
				String namePath=b.mName;
				TextView textName = (TextView) mBookLayout.findViewById(BookShelvesView.BOOKVIEW_TEXTNAME_ID);
				if(FileUtils.isExist(namePath)){
					try {
						FileInputStream nameis=new FileInputStream(namePath);
						String name=FileUtils.inputStream2String(nameis);
						textName.setText(name);
					} catch (IOException e) {
						textName.setText(R.string.bookdefaultname);
					}
					
				}else{
					textName.setText(R.string.bookdefaultname);
				}
				
				BookDownRunnable.isDown = false;
				ShelvesDataManager.notifyBookDownRunnable(ShelvesActivity.this);
				if (b.state == 1) {
					Toast.makeText(ShelvesActivity.this, R.string.downsucc,
							Toast.LENGTH_LONG).show();
				}
				// TODO 设置完成试图

				Message nmsg = new Message();
				nmsg.what = ShelvesActivity.MSG_NEXT_DOWN;
				downhandle.sendMessage(nmsg);
				refreshShelves();
				break;
			case MSG_PROGRESS:
				b = (Book) msg.obj;
				mBookLayout = (RelativeLayout) findViewById(b.mOrder);
				if (mBookLayout == null)
					return;
				mPb = (TasksCompletedView) mBookLayout.findViewById(BookShelvesView.BOOKVIEW_PROGRESS_ID);
				if(mPb.getVisibility()!=View.VISIBLE){
					mPb.setVisibility(View.VISIBLE);
				}
				int progressValue = (int) (mPb.getMax() * b.mCurrentRate);
				mPb.setProgress(progressValue);
				break;
			case MSG_START_DOWN:
				BookDownLoadTask task = new BookDownLoadTask(
						ShelvesActivity.this);
				b = (Book) msg.obj;
				if (ScreenUtils.getAPILevel() > 11) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, b);
				} else {
					task.execute(b);
				}
				break;
			case MSG_NEXT_DOWN:
				// if(ShelvesDataManager.checkNewDownLoadBook(ShelvesActivity.this)){
				// TODO 通知检查新书下载
				ShelvesDataManager.notifyBookDownRunnable(ShelvesActivity.this);
				// }
				break;

			case MSG_DELETE_INFOR:
				Book b1 = (Book) msg.obj;
				btnOkDel.setTag(b1);
				showDel();
				break;
			case MSG_REFRESH:
				refreshShelves();
				break;
			case MSG_BOOK_PAUSE:
				b = (Book) msg.obj;
				mBookLayout = (RelativeLayout) findViewById(b.mOrder);
				// setBookView(b, mBookLayout);
				break;
			case MSG_LOCAL_FAIL:
				b = (Book) msg.obj;

				ShelvesDataManager.deleteBook(ShelvesActivity.this, b);
				new AlertDialog.Builder(ShelvesActivity.this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.notice)
						.setMessage(R.string.bookcity_downloaderror)
						.setPositiveButton(R.string.sure,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										downhandle
												.sendEmptyMessage(MSG_REFRESH);
									}
								}).show();

				refreshShelves();
				break;
			}

		}
	};

	public class MyDialogListener implements DialogInterface.OnClickListener {
		private Book mBook;

		public MyDialogListener(Book b) {
			mBook = b;
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if (mBook.state == 0) {
				mBook.state = -1;
			}
			ShelvesDataManager.deleteBook(ShelvesActivity.this, mBook);
			downhandle.sendEmptyMessage(MSG_REFRESH);
		}

	}

	@Override
	public void onBackPressed() {
		layCover.setVisibility(View.VISIBLE);
		layCover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		layClose.setVisibility(View.VISIBLE);
		layAlpha.setVisibility(View.VISIBLE);
		layAdd.setVisibility(View.GONE);
		layDel.setVisibility(View.GONE);
		layAbout.setVisibility(View.GONE);
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editAdd.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editInputDumy.getWindowToken(), 0);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(400);
		layClose.startAnimation(scaleAnimation);
		// TransLateAnim transLateAnim=new TransLateAnim(500,80,0.5f);
		// transLateAnim.setDuration(1000);
		// layClose.setAnimation(transLateAnim);
		// layClose.startAnimation(transLateAnim);
	}

	private void doAboutAction() {
		if(layCover.getVisibility() == View.VISIBLE){
			hidePop();
			return;
		}
		layAbout.setVisibility(View.VISIBLE);
		layCover.setVisibility(View.VISIBLE);
		layCover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f,
						0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation.setDuration(400);
				layCover.setOnClickListener(null);
				layAbout.startAnimation(scaleAnimation);
				scaleAnimation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation arg0) {
						hidePop();
					}
				});
			}
		});
		layAlpha.setVisibility(View.GONE);
		layAdd.setVisibility(View.GONE);
		layClose.setVisibility(View.GONE);
		layDel.setVisibility(View.GONE);
		layAbout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(400);
		layAbout.startAnimation(scaleAnimation);
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editAdd.getWindowToken(), 0);
	}

	/**
	 * 展示输入地址弹出框
	 */
	private void showAdd() {
		layCover.setVisibility(View.VISIBLE);
		layCover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		layAdd.setVisibility(View.VISIBLE);
		layAlpha.setVisibility(View.VISIBLE);
		layClose.setVisibility(View.GONE);
		layDel.setVisibility(View.GONE);
		layAbout.setVisibility(View.GONE);
		String textString = getStoreData("IP");
		editAdd.setText(textString);
		//editAdd.setCursorVisible(true);
		// InputMethodManager imm = (InputMethodManager) this
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.showSoftInputFromWindow(editAdd.getWindowToken(), 0);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(400);
		layAdd.startAnimation(scaleAnimation);
		editInputDumy.requestFocus();

	}

	private void showDel() {
		layCover.setVisibility(View.VISIBLE);
		layCover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		layDel.setVisibility(View.VISIBLE);
		layAlpha.setVisibility(View.VISIBLE);
		layClose.setVisibility(View.GONE);
		layAdd.setVisibility(View.GONE);
		layAbout.setVisibility(View.GONE);

		ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f,
				1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(400);
		layDel.startAnimation(scaleAnimation);

		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editAdd.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editInputDumy.getWindowToken(), 0);
		
		
	}

	private void hidePop() {
		layCover.setVisibility(View.GONE);
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editAdd.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editInputDumy.getWindowToken(), 0);
		editAdd.clearFocus();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	public final static int MSG_START_DOWN = 100001;
	public final static int MSG_NEXT_DOWN = 100002;
	public final static int MSG_DELETE_INFOR = 100003;
	public final static int MSG_REFRESH = 100004;
	public final static int MSG_PROGRESS = 100005;
	public final static int MSG_DOWN_OVER = 100006;
	public final static int MSG_DOWN_HIDEBTN = 100007;
	public final static int MSG_BOOK_PAUSE = 100008;
	public final static int MSG_LOCAL_FAIL = 100009;
	public static String imgkeyprefix = "com.hl.shelves.index.";
}

package com.hl.android;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;

import com.hl.android.book.BookDecoder;
import com.hl.android.book.DESedeCoder;
import com.hl.android.book.entity.Book;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.book.entity.SectionEntity;
import com.hl.android.book.entity.SnapshotEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.controller.BookState;
import com.hl.android.controller.EventDispatcher;
import com.hl.android.controller.PageEntityController;
import com.hl.android.core.helper.AnimationHelper;
import com.hl.android.core.helper.BookHelper;
import com.hl.android.core.helper.LogHelper;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.ViewCell;
import com.hl.android.view.ViewPage;
import com.hl.android.view.component.AudioComponent;
import com.hl.android.view.component.HLCounterComponent;
import com.hl.android.view.component.WaterStain;
import com.hl.callback.Action;
import com.mediav.ads.sdk.adcore.Mvad;
import com.mediav.ads.sdk.adcore.Mvad.FLOAT_BANNER_SIZE;
import com.mediav.ads.sdk.adcore.Mvad.FLOAT_LOCATION;
import com.mediav.ads.sdk.interfaces.IMvBannerAd;
import com.mediav.ads.sdk.interfaces.IMvInterstitialAd;

@SuppressLint("HandlerLeak")
public class HLActivity extends HLLayoutActivity implements
OnConnectionFailedListener, ConnectionCallbacks{
	
	private static final int REQUEST_RESOLVE_ERROR = 0;

	private static final String STATE_RESOLVING_ERROR = "resolving_error";

	private GoogleApiClient mGoogleApiClient;

	
	// private boolean initState = false;
	Canvas mCurPageCanvas, mNextPageCanvas;

	Display disp = null;
	public ProgressDialog progressDialog;
	private boolean isStop = false;
	public View bfView;
	public WaterStain waterStain;
	private boolean doCreateBeforResume = false;

	private void addBFView() {
		LayoutParams lp1 = new LayoutParams(BookSetting.SCREEN_WIDTH,
				BookSetting.SCREEN_HEIGHT);
		bfView = new View(this);
		this.addContentView(bfView, lp1);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// 设置关闭状态标志位
		BookSetting.IS_CLOSED = false;
		resetBookState();
		// initState = true;
		isStop = false;
		EventDispatcher.getInstance().init(this);
		BookController.getInstance().setHLActivity(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("正在加载");
		progressDialog.setMessage("我们正在努力加载数据文件，请耐心等候");
		progressDialog.setCancelable(false);

		if (preLoadAction != null) {
			preLoadAction.doAction();
			return;
		} else {
			loadHandler.sendEmptyMessageDelayed(1, 100);
			// doLoadAndInit();
		}
		doCreateBeforResume = true;
	}

	/**
	 * 设置google广告
	 */
	private void setGoogleAd() {
		try {
			Log.d("SunYongle", "google广告开始加载");

			ApplicationInfo info = getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			String adSpaceid = info.metaData.getString("DOMOB_PID");
			Log.d("ad", "google_ad_id="+adSpaceid);
			if (adSpaceid != null && !adSpaceid.trim().equals("")) {
				RelativeLayout ad = new RelativeLayout(this);
				RelativeLayout.LayoutParams layoutParams4360layout = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams4360layout
						.addRule(RelativeLayout.CENTER_HORIZONTAL);
				String adPos = BookController.getInstance().getBook()
						.getBookInfo().position;
				if (TextUtils.isEmpty(adPos) && "top".equals(adPos)) {
					layoutParams4360layout
							.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				} else {
					layoutParams4360layout
							.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				}
			layoutParams4360layout.addRule(RelativeLayout.CENTER_HORIZONTAL);
			layoutParams4360layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			coverLayout.addView(ad, layoutParams4360layout);
			AdView adView = new AdView(HLActivity.this);
			adView.setAdSize(AdSize.BANNER);
			//"ca-app-pub-6364080805996195/2123784862"
			adView.setAdUnitId(adSpaceid);
			
			ad.addView(adView);
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void resetBookState() {
		AnimationHelper.animatiorMap.clear();
	}

	public Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == 0) {
				progressDialog.show();
			} else if (what == 1) {
				progressDialog.dismiss();
			} else if (what == 2) {
				doLoadAndInit();
			}
		}
	};
	protected Handler loadHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				doLoadAndInit();
				break;
			case 2:
				
				break;
				default:
				break;
			}
		};
	};

	public void doLoadAndInit() {
		Log.d("SunYongle", "doLoadAndInit,初始化开始");

		// contentLayout.setBackgroundColor(Color.BLACK);
		// /////////////////////Begin///////////////////////////////////////
		// load book
		Book book = null;
		// 先判断hash.dat是否存在
		InputStream hashDatInputStream = FileUtils.getInstance()
				.getFileInputStream(this, "hash.dat");
		HLSetting.IsHaveBookMark = false;
		if (hashDatInputStream != null) {
			// hash.dat文件存在，判断里面的加密字符串解密后与制定字符串是否相同
			if (checkIsCorrect()) {
				// 如果验证正确，说明book.xml是密文的，解密并解析book.xml文件，根据配置ISFREE设置是否显示水印
				try {
					InputStream bookDatInputStream = FileUtils
							.readFile("book.dat");
					byte[] aa = read(bookDatInputStream);
					InputStream bookXMLInputStream = FileUtils
							.readFile("book.xml");
					String bookxmlStr = FileUtils
							.inputStream2String(bookXMLInputStream);
					byte[] asdasdfasd = DESedeCoder.decrypt(
							Base64.decode("DED1TerlRtY=", Base64.NO_WRAP),
							"ywa;sdgfasdweunmxoina".getBytes());
					String asdaaaaa = new String(asdasdfasd);
					String key = validCompile(aa, Integer.parseInt(asdaaaaa));
					byte[] data = DESedeCoder.decrypt(
							Base64.decode(bookxmlStr, Base64.NO_WRAP),
							key.getBytes());
					String result = new String(data);
					InputStream bookXmlMingWenInputStream = new ByteArrayInputStream(
							result.getBytes());
					book = BookDecoder.getInstance().decode(
							bookXmlMingWenInputStream);
					this.setWaterStain(book);
				} catch (IOException e) {
				}
			} else {
				// 解密后的has.dat文件中内容与预期不同，说明has.dat被修改、替换或是非法创建的，直接按照明文解析book.xml,添加水印
				book = loadWaterStainAndDecodeBookXml(book);
			}
		} else {
			// hash.dat文件不存在，直接解密明文的book.xml文件，,添加水印
			book = loadWaterStainAndDecodeBookXml(book);
		}

		if (book == null) {
			Toast.makeText(this, "书籍文件不存在，请检查", Toast.LENGTH_SHORT).show();
			BookSetting.IS_READER = false;
			finish();
			return;
		} else {
			BookController.getInstance().setBook(book);
		}
		
		if (BookController.getInstance().getBook().getBookInfo().adType == 3) {
			set360Ad();
		}else if (BookController.getInstance().getBook().getBookInfo().adType == 4) {
			setGoogleAd();
		}
		String bookType = book.getBookInfo().getBookType();
		HLSetting.display = getWindowManager().getDefaultDisplay();

		// 设置屏幕大小 以及屏幕方向
		BookHelper.setPageScreenSize(this, bookType);
		// 设置启动页面的横竖屏方向
		if (BookSetting.IS_HOR_VER || BookSetting.IS_HOR) {// 如果是横竖屏切换或者是横向的话，设置成横向
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		try {
			SnapshotEntity firstSnap = book.getSnapshots().get(1);
			BookSetting.SNAPSHOTS_WIDTH = Integer.valueOf(firstSnap.width);
			BookSetting.SNAPSHOTS_HEIGHT = Integer.valueOf(firstSnap.height);
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			if (book.getBookInfo().bookFlipType.equals("corner_flip")) {
				BookSetting.FLIPCODE = 0;
				HLSetting.FlipTime = 1000;
			} else if (book.getBookInfo().bookFlipType.equals("slider_flip")) {
				BookSetting.FLIPCODE = 1;
				HLSetting.FlipTime = 500;
			} else if (book.getBookInfo().bookFlipType.equals("hard_flip")) {
				BookSetting.FLIPCODE = 2;
				HLSetting.FlipTime = 1000;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// ///////////////////////////ENd
		// Begin//////////////////////////////////////////////////

		if (HLSetting.isSettingTumb == true) {
			if (Build.VERSION.SDK_INT >= 11) {
				HLSetting.isHoneyComb = true;
				if (Build.VERSION.SDK_INT < 14)
					BookSetting.SCREEN_HEIGHT -= 45;
				if (Build.VERSION.SDK_INT >= 14)
					BookSetting.SCREEN_HEIGHT -= 35;

			}
		}

		BookDecoder.getInstance().initBookItemList();
		setMetaData();
		BookController.getInstance().setHLBookLayout(contentLayout);
		layout(book, getResources().getConfiguration().orientation);

		addBFView();
		//
		// // 加载
		//
		ViewPage viewPage = new ViewPage(this, null, null);
		BookController.getInstance().loadStartPage(viewPage);
		// 增加播放结束时候的回调函数
		viewPage.setPageCompletion(new StartPageCompleteCallBack());
		// 播放页面
		BookController.getInstance().playStartPage(viewPage);
	}

	private Book loadWaterStainAndDecodeBookXml(Book book) {
		book = BookDecoder.getInstance().decode(this, "book.xml");
		this.setWaterStain(book);
		return book;
	}

	private void setWaterStain(Book book) {
		if (!HLSetting.IsHaveBookMark) {
			if (book.getBookInfo().isFree) {
				HLSetting.IsShowBookMark = true;
				HLSetting.IsShowBookMarkLabel = false;
			} else {
				HLSetting.IsShowBookMark = false;
				HLSetting.IsShowBookMarkLabel = false;
			}

		}
		if (HLSetting.IsShowBookMark || HLSetting.IsShowBookMarkLabel) {
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			waterStain = new WaterStain(this);
			coverLayout.addView(waterStain, layoutParams);
		}
	}

	/**
	 * 解密hash.dat文件内容，并比较是否与混入book.dat文件中字符串相同
	 * 
	 * @return true，解密文件与混入book.dat文件中字符串相同
	 */
	private boolean checkIsCorrect() {
		try {
			InputStream inputStream = FileUtils.readFile("book.dat");
			byte[] aa = read(inputStream);
			byte[] asdasdfasd = DESedeCoder.decrypt(
					Base64.decode("DED1TerlRtY=", Base64.NO_WRAP),
					"ywa;sdgfasdweunmxoina".getBytes());
			String asdaaaaa = new String(asdasdfasd);
			String key = validCompile(aa, Integer.parseInt(asdaaaaa));
			byte[] asdasdfasdw = DESedeCoder.decrypt(
					Base64.decode("VPglhR9kDeo=", Base64.NO_WRAP),
					"ywa;sdgfeeasdweunmxoina".getBytes());
			String asdaaaaaasd = new String(asdasdfasdw);
			String compareStr485 = validCompile(aa,
					Integer.parseInt(asdaaaaaasd));
			InputStream inputHas = FileUtils.readFile("hash.dat");
			String bookhasStr = FileUtils.inputStream2String(inputHas);
			byte[] data = DESedeCoder.decrypt(
					Base64.decode(bookhasStr, Base64.NO_WRAP), key.getBytes());
			if (data == null) {
				return false;
			}
			String result = new String(data);
			return result.equals(compareStr485);
		} catch (IOException e) {
			Log.e("wdy", "解密对比时出错！！！！！");
			return false;
		}
	}

	public static byte[] read(InputStream input) throws IOException {
		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		byte[] buf = new byte[1024 * 4];
		int n = 0;
		try {
			while (-1 != (n = input.read(buf)))
				out.write(buf, 0, n);// 这步报错oom

		} catch (IOException e) {
			throw e;
		} finally {
			input.close();
		}

		return out.toByteArray();
	}

	// public String validCompile5(String content,int s) throws IOException {
	// String input = content;
	// input = input.substring(4, input.length());
	// Log.d("wdy", "input:"+input);
	// System.out.println(input);
	// int step = 1;
	// if (input.length() > s) {
	// step = input.length() / s;
	// }
	//
	// int point = 0;
	// String result = "";
	// for (int i = 0; i < s; i++) {
	// result += input.substring(point, point + 1);
	// point = point + step;
	// if (point >= input.length()) {
	// point = point % input.length();
	// }
	// }
	// Log.d("wdy", "result:"+result);
	// return getMD5Str(result);
	// }

	public String validCompile(byte[] content, int s) throws IOException {
		// InputStream sbs = new StringBufferInputStream(new String(content));
		ByteArrayInputStream ddd = new ByteArrayInputStream(content);
		return this.validCompile3(content, s);
	}

	public byte[] unzip2(InputStream in) throws IOException {
		byte[] aa = null;
		try {

			// 根据输入字节流创建输入字符流
			BufferedInputStream bis = new BufferedInputStream(in);
			// 根据字符流，创建ZIP文件输入流
			ZipInputStream zis = new ZipInputStream(bis);
			// zip文件条目，表示zip文件
			ZipEntry entry;
			// 循环读取文件条目，只要不为空，就进行处理
			while ((entry = zis.getNextEntry()) != null) {
				int count;

				byte date[] = new byte[2048];

				// 如果条目是文件目录，则继续执行

				if (entry.isDirectory()) {
					continue;
				} else {
					ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
					while ((count = zis.read(date)) != -1) {
						bos1.write(date, 0, count);
					}
					aa = bos1.toByteArray();
					bos1.flush();
					bos1.close();
				}
			}

			zis.close();
			return aa;

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		}
	}

	public String validCompile3(byte[] content, int s) throws IOException {
		int step = 1;
		byte[] newContent = new byte[content.length - 4];
		for (int i = 4; i < content.length; i++) {
			newContent[i - 4] = content[i];
		}
		System.out.println(new String(content));
		if (newContent.length > s) {
			step = (newContent.length) / s;
		}

		int point = 0;
		ArrayList<Byte> al = new ArrayList<Byte>();
		for (int i = 0; i < s; i++) {
			al.add(new Byte(newContent[point]));
			point = point + step;
			if (point >= newContent.length) {
				point = point % newContent.length;
			}
		}

		System.out.println(al);
		byte[] aa = new byte[al.size()];
		for (int j = 0; j < al.size(); j++) {
			aa[j] = al.get(j).byteValue();
		}
		return getMD5Byte(aa);
	}

	private String getMD5Byte(byte[] str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	private class StartPageCompleteCallBack implements Action {
		private long mStartTime = 0;

		public StartPageCompleteCallBack() {
			mStartTime = System.currentTimeMillis();
		}

		@Override
		public boolean doAction() {
			double startPageTime = BookController.getInstance().getBook()
					.getBookInfo().getStartPageTime();
			if (startPageTime == 0) {
				BookController.getInstance().getBook().getBookInfo()
						.setStartPageTime(3);
				startPageTime = 3;
			}
			int leftTime = (int) (startPageTime * 1000);
			if (leftTime < 0)
				leftTime = 0;
			if (BookController.getInstance().getBook().getBookInfo().getId()
					.equals("-1396921599784")) {
				leftTime = 6000;
			}
			MyCount mc = new MyCount(leftTime, 100);
			mc.start();
			return true;
		}
	}

	// @Override
	// protected void onStart() {
	// super.onStart();
	// }

	private void startReadBook() {
		setupViews();
		if (BookSetting.IS_HOR_VER) {// 如果是横竖屏切换的话，需要重新设置屏幕方向
			// if (BookSetting.IS_HOR_VER) {
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			// }
			playBookChange1();
			// 播放背景音乐
			String backgroundMusicId = BookController.getInstance().getBook()
					.getBookInfo().backgroundMusicId;
			if (!StringUtils.isEmpty(backgroundMusicId)) {
				BookController.getInstance().playBackgroundMusic();
			}
		} else {
			if (BookSetting.IS_HOR) {
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			BookHelper.setupScreen(this);
			setFlipView();
			BookController.getInstance().playBook();
		}
		coverLayout.setVisibility(View.VISIBLE);
	}

	private void playBookChange1() {
		int screenWidth = ScreenUtils.getScreenWidth(this);
		int screenHeight = ScreenUtils.getScreenHeight(this);
		int orient = screenWidth < screenHeight ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
				: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		if (orient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			BookSetting.IS_HOR = true;
		}
		if (orient == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			BookSetting.IS_HOR = false;
		}
		BookHelper.setupScreen(this);
		setFlipView();

		if (BookController.getInstance().section == null) {
			SectionEntity section = BookController.getInstance().book
					.getSections().get(0);
			BookController.getInstance().changePageById(
					section.getPages().get(0));
		} else {
			if (StringUtils.isEmpty(BookController.getInstance().getViewPage()
					.getEntity().getLinkPageID())) {
				SectionEntity section = BookController.getInstance().book
						.getSections().get(0);
				BookController.getInstance().changePageById(
						section.getPages().get(0));
			} else {
				BookHelper.setupScreen(this);
				setFlipView();
				BookController.getInstance().changePageById(
						BookController.getInstance().getViewPage().getEntity()
								.getLinkPageID());
			}
		}
	}

	private void postLayout() {
		new CountDownTimer(1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinish() {
				layout(BookController.getInstance().getBook());
			}
		}.start();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		LogHelper.trace("onConfigurationChanged", "onConfigurationChanged",
				false);
		super.onConfigurationChanged(newConfig);
		Log.d("hl", " i am onConfigurationChanged");
		if (BookController.getInstance().getBook() == null) {
			return;
		} else {
			// ????????????????书架，竖屏进入
			postLayout();
			layout(BookController.getInstance().getBook(),
					newConfig.orientation);
		}
		// 如果切换的方向与要设置的方向是一致的就不用做什么了
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				|| getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
			return;
		if (BookController.getInstance().getViewPage() == null)
			return;
		PageEntity pageEntity = BookController.getInstance().getViewPage()
				.getEntity();
		// 如果切换屏幕，并且当前页的linkid 不是空的，那么就正常

		if (BookSetting.IS_HOR_VER
				&& !StringUtils.isEmpty(pageEntity.getLinkPageID())) {
			// if((BookSetting.IS_HOR && pageEntity.getType().equals(
			// ViewPage.PAGE_TYPE_HOR))||!BookSetting.IS_HOR &&
			// pageEntity.getType().equals(
			// ViewPage.PAGE_TYPE_VER)){
			// BookSetting.IS_HOR = !BookSetting.IS_HOR;
			// }
			// 如果申请的屏幕方向和当前页的反响不一致就要切换页面
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				if ((pageEntity.getType().equals(ViewPage.PAGE_TYPE_HOR) || pageEntity
						.getType().equals(ViewPage.PAGE_TYPE_NONE))) {
					BookSetting.IS_HOR = false;
					BookHelper.setupScreen(HLActivity.this);
					setFlipView();
					postLayout();
					BookController.getInstance().changePageById(
							pageEntity.getLinkPageID());
					// BookController.getInstance().changePageById(pageEntity.getLinkPageID(),1000);
				}
			}
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				if (pageEntity.getType().equals(ViewPage.PAGE_TYPE_VER)) {
					BookSetting.IS_HOR = true;
					BookHelper.setupScreen(HLActivity.this);
					setFlipView();
					postLayout();
					BookController.getInstance().changePageById(
							pageEntity.getLinkPageID());
					// BookController.getInstance().changePageById(pageEntity.getLinkPageID(),1000);
				}
			}

		}
		relayoutGlobalButton();
	}

	EventDispatcher eventDispatcher = new EventDispatcher();

	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		eventDispatcher.onTouch(touchevent);
		return true;
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// if(BookController.getInstance().getViewPage()==null)return;
	// if(AppContext.getAPILevel()>10){
	// resume();
	// }
	//
	// }
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {

			if (BookController.getInstance() != null
					&& BookController.getInstance().getViewPage() != null) {
				BookController.getInstance().getViewPage().stop();
				BookController.getInstance().getViewPage().clean();
				// BookController.recyle();
			}
			if (PageEntityController.getInstance() != null) {
				PageEntityController.getInstance().recyle();
			}

			if (BookState.getInstance() != null) {
				BookState.getInstance().recyle();
			}

			if (gallery != null) {
				gallery.recycle();
			}
		} catch (Exception e) {

		}

		if (recycleAction != null) {
			recycleAction.doAction();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (BookController.getInstance().getViewPage() == null)
			return;
		pause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		coverLayout.setTag(null);
		mWindowManager.removeView(coverLayout);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			mWindowManager.removeView(coverLayout);
		} catch (Exception e) {

		}
		Log.d("XIXI", "_________");
		mWindowManager.addView(coverLayout, wmParams);
		coverLayout.setTag("addToWindowManager");
		if (doCreateBeforResume) {
			// 开始readbook时再可见
			coverLayout.setVisibility(View.GONE);
			doCreateBeforResume = false;
		}
		resume();
		coverLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mainLayout.dispatchTouchEvent(event);
				return true;
			}
		});
	}

	// @Override
	// public void onWindowFocusChanged(boolean hasFocus) {
	// super.onWindowFocusChanged(hasFocus);
	// if(BookSetting.IS_SHELVES){
	// return;
	// }
	// if (hasFocus) {
	// // initState = false;
	// // if(AppContext.getAPILevel()<=10){
	// resume();
	// // }
	// return;
	// }else{
	// pause();
	// }
	// }
	private void setMetaData() {
		try {
			ApplicationInfo appInfo = this.getPackageManager()
					.getApplicationInfo(getPackageName(),
							PackageManager.GET_META_DATA);
			if (appInfo.metaData != null) {
				HLSetting.IsAD = appInfo.metaData.getBoolean("ISAD");
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void pause() {
		if (BookController.getInstance().getViewPage() == null)
			return;
		try {
			BookController.getInstance().getViewPage().pause();
			if (HLSetting.PlayBackGroundMusic) {
				if (BookController.getInstance().getBackgroundMusic() != null) {
					BookController.getInstance().getBackgroundMusic().pause();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void resume() {

		if (BookController.getInstance().getViewPage() == null)
			return;
		try {
			if (HLSetting.PlayBackGroundMusic) {
				if (BookController.getInstance().getBackgroundMusic() != null) {
					BookController.getInstance().getBackgroundMusic().resume();
				}
			}
			BookController.getInstance().resume();
		} catch (Exception e) {
			Log.d("hl", "resume error");
		}
	}

	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			if (isStop)
				return;
			startReadBook();
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}

	@Override
	public void onBackPressed() {
		try {
			BookController.getInstance().mainPageID = null;
			if (BookController.getInstance().commonPage != null) {
				for (int i = 0; i < BookController.getInstance().commonPage
						.getChildCount(); i++) {
					ViewCell v = (ViewCell) BookController.getInstance().commonPage
							.getChildAt(i);
					if (v.getComponent() instanceof HLCounterComponent) {
						((HLCounterComponent) v.getComponent()).reset();
					}
				}
				BookController.getInstance().commonPage.stop();
				BookController.getInstance().commonPage.clean();
				BookController.getInstance().commonPage = null;
			}
			if (BookController.getInstance().getViewPage() != null) {
				for (int i = 0; i < BookController.getInstance().getViewPage()
						.getChildCount(); i++) {
					ViewCell v = (ViewCell) BookController.getInstance()
							.getViewPage().getChildAt(i);
					if (v.getComponent() instanceof HLCounterComponent) {
						((HLCounterComponent) v.getComponent()).reset();
					}
				}
				BookController.getInstance().getViewPage().stop();
				BookController.getInstance().getViewPage().clean();
			}
			AudioComponent audioComponent = BookController.getInstance()
					.getBackgroundMusic();
			if (audioComponent != null) {
				audioComponent.stop();
				audioComponent = null;
			}
			if (BookController.getInstance().section.isShelves) {
				BookController.getInstance().closeShelves();
			} else {
				if (BookSetting.IS_SHELVES
						&& PageEntityController.getInstance() != null) {
					PageEntityController.getInstance().clear();
				}
				BookSetting.IS_CLOSED = true;
				progressDialog.hide();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.finish();

		}
	};

	protected View get360AdView() {
		// TODO Auto-generated method stub
		return null;
	}

	private void set360Ad() {
		// View m360adview = get360AdView();
		// if (m360adview != null) {
		try {
			Log.d("SunYongle", "360广告开始加载");
			ApplicationInfo info = getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			
			String adSpaceid = info.metaData.getString("DOMOB_PID");
			
		  //  boolean isADtest=info.metaData.getBoolean("DOMOB_TEST_MODE");
			if (adSpaceid != null && !adSpaceid.trim().equals("")) {
				Log.d("SunYongle", "360banner广告开始加载");
				String adPos = BookController.getInstance().getBook()
				.getBookInfo().position;
				if ("top".equals(adPos)) {
				Mvad.showFloatbannerAd(HLActivity.this, adSpaceid, false, FLOAT_BANNER_SIZE.SIZE_DEFAULT, FLOAT_LOCATION.TOP);
			} else {
				Mvad.showFloatbannerAd(HLActivity.this, adSpaceid, false, FLOAT_BANNER_SIZE.SIZE_DEFAULT, FLOAT_LOCATION.BOTTOM);
			}
				
			String adInsID=info.metaData.getString("DOMOB_INS_ID");
			if (adInsID != null && !adInsID.trim().equals("")) {
				Log.d("SunYongle", "360inster广告开始加载");
				IMvInterstitialAd interstitialAd=Mvad.showInterstitial(this, adInsID, false);
				interstitialAd.showAds(this);
			}
				
//				孙永乐 20141218 老的版本改了使用
//				RelativeLayout layout4360ad = new RelativeLayout(this);
//				RelativeLayout.LayoutParams layoutParams4360layout = new RelativeLayout.LayoutParams(
//						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				layoutParams4360layout
//						.addRule(RelativeLayout.CENTER_HORIZONTAL);
//				String adPos = BookController.getInstance().getBook()
//						.getBookInfo().position;
//				if ("top".equals(adPos)) {
//					layoutParams4360layout
//							.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//				} else {
//					layoutParams4360layout
//							.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//				}
//				
//				IMvBannerAd mediavAdView =Mvad.showBanner(layout4360ad, HLActivity.this, adSpaceid, false);
//				//1.0.4的SDK使用的 MediavSimpleAds.initSimpleBanner(layout4360ad, HLActivity.this, adSpaceid, false);
//				// layout4360ad.addView(m360adview, new LayoutParams(
//				// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//				coverLayout.addView(layout4360ad, layoutParams4360layout);
//				mediavAdView.showAds(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("SunYongle", "360广告出错:"+e.toString());
			// TODO: handle exception
		}
		// }
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("SunYongle", "google广告开始连接");

		if (requestCode == REQUEST_RESOLVE_ERROR) {
			mResolvingError = false;
			if (resultCode == RESULT_OK) {
				if (!mGoogleApiClient.isConnecting()
						&& !mGoogleApiClient.isConnected()) {
					Log.d("SunYongle", "google广告连接成功");

					mGoogleApiClient.connect();
				}
			}
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		Log.d("SunYongle", "google广告连接失败");

		if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
			// The Android Wear app is not installed
			return;
		}
		if (mResolvingError) {
			return;
		} else if (result.hasResolution()) {
			mResolvingError = true;
			try {
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (SendIntentException e) {
				e.printStackTrace();
				mGoogleApiClient.connect();
			}
		} else {
			mResolvingError = true;
		}
	}
	private boolean mResolvingError = false;

}

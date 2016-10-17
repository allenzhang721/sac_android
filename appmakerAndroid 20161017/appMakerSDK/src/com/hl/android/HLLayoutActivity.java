package com.hl.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.android.book.entity.Book;
import com.hl.android.book.entity.ButtonEntity;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.controller.PageEntityController;
import com.hl.android.core.helper.LogHelper;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.ImageUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.ViewPage;
import com.hl.android.view.component.IndesignBottom;
import com.hl.android.view.component.IndesignBottom.BottomNavListenner;
import com.hl.android.view.component.IndesignMiddle;
import com.hl.android.view.component.IndesignUP;
import com.hl.android.view.component.IndesignUP.NavMenuListenner;
import com.hl.android.view.component.MYSearchView;
import com.hl.android.view.component.MarkView4NavMenu;
import com.hl.android.view.component.bookmark.MarkViewLayout;
import com.hl.android.view.gallary.GalleyHelper;
import com.hl.android.view.gallary.base.AbstractGalley;
import com.hl.android.view.layout.HLRelativeLayout;
import com.hl.android.view.pageflip.AbstractPageFlipView;
import com.hl.android.view.pageflip.PageFlipVerticleView;
import com.hl.android.view.pageflip.PageFlipView;
import com.hl.android.view.pageflip.PageWidgetNew;
import com.hl.callback.Action;

/**
 * 
 * 布局使用的activity 整个activity分文两层 正常显示的在下面那一层 上面那一层包含着全局显示的一些按钮以及gallery
 * 
 * @author zhaoq
 * 
 */
public class HLLayoutActivity extends Activity {

	/**
	 * 加载数据文件之前的action
	 */
	protected Action preLoadAction;
	/**
	 * 回收的回调函数
	 */
	protected Action recycleAction;
	/**
	 * 绘制书籍视图之前调用
	 */
	protected Action preShowViewAction;
	/**
	 * 开始播放书籍之前的action
	 */
	protected Action prePlayAction;

	/**
	 * 布局层级所使用的对象
	 */
	private FrameLayout frameLayout;
	public HLRelativeLayout contentLayout;
	public FrameLayout mainLayout;
	//公共页所在的布局
	public RelativeLayout commonLayout;
	public RelativeLayout coverLayout;//放的公共按钮
	

//	private LayoutParams frameFullLp = new LayoutParams(
//			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	/**
	 * 内容视图的布局参数，这个需要根据book的对象进行实时调整宽度
	 */
	private FrameLayout.LayoutParams contentLp = new FrameLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	

	private FrameLayout.LayoutParams coverLp = new FrameLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	/**
	 * 全局按钮ID
	 */
	protected ImageButton galleyButton, homeButton, leftButton, rightButton;
	protected ButtonEntity galleyEntity, homeEntity, leftEntity, rightEntity;
	protected int homeButton_v_ID = 9910001;
	protected int homeButton_h_ID = 9910002;

	protected int gallery_ID = 9910021;
	/**
	 * 全局按钮ID
	 */
	protected ImageButton vergalleyButton, verhomeButton, verleftButton,
			verrightButton;
	protected ButtonEntity vergalleyEntity, verhomeEntity, verleftEntity,
			verrightEntity;

	private ImageButton backButton;
	protected AbstractGalley gallery;
	// private TextView pageTextView = null;

	private TextView dummyVideo;
	RelativeLayout.LayoutParams rlp;

	// 书签试图
	private MarkViewLayout markLayout;

	// 书签列表适配器
//	private MarkItemsAdapter markAdapter;

	protected boolean isShelves = false;

	public View adViewLayout;
	public AbstractPageFlipView absPageView = null;
	private IndesignUP mUPNav;
	private IndesignBottom mBottomNav;
	private IndesignMiddle mMiddleNav;
	private static final int NAVMENU_ID=0x10010;
	private int BOTTOM_NAV_ID=0x10011; 
	private int MIDDLE_NAV_ID=0x10012;
	private ListView listView4ShowSnapshots;
	private MarkView4NavMenu markView4NavMenu;
	private BaseAdapter basAdapter;
	private MYSearchView searchView;
	
	WindowManager mWindowManager;  
	public WindowManager.LayoutParams wmParams;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置布局层级
		frameLayout = new MFrameLayout(this);
//		frameLayout.setBackgroundColor(Color.RED);
		// 这个是内容区域
		mainLayout = new FrameLayout(this);
		
		contentLayout = new HLRelativeLayout(this);
		contentLayout.setDrawingCacheEnabled(true);
		contentLayout.buildDrawingCache();
		mainLayout.addView(contentLayout,contentLp);
		commonLayout = new RelativeLayout(this);
		mainLayout.addView(commonLayout,contentLp);
		//		contentLayout.setBackgroundColor(Color.YELLOW);
		// 这个是封皮层，用来放置全局按钮的
		coverLayout = new RelativeLayout(this);
		contentLp.gravity = Gravity.CENTER;;
		frameLayout.addView(mainLayout, contentLp);
		 wmParams = new WindowManager.LayoutParams();  
		      //获取的是LocalWindowManager对象  
		 mWindowManager = this.getWindowManager();  
		 wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;  
		 wmParams.format = PixelFormat.RGBA_8888;;  
		 wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		 
		 
		 wmParams.gravity = Gravity.LEFT | Gravity.TOP;  
		 wmParams.x = 0;  
		 wmParams.y = 0;  
		 wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;  
		 wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;  
 
//		frameLayout.addView(coverLayout, contentLp);
//		 RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		 coverLayout.addView(new WaterStain(this),layoutParams);

		//===================================================================================================
		//当surfaceview第一次在当前activity上添加的时候，系统会给WindowManager重新排布局，relayout，这样就会黑一下，这个只会出现在第一次，以后再添加surfaceview就不会黑屏了。
		//我们这里添加一个无用的宽高都为0的SurfaceVeiw，以后load的视频、摄像头等用到SurfaceView的控件就不会黑了
		SurfaceView surface = new SurfaceView(this);
		frameLayout.addView(surface,new LayoutParams(0, 0));
		//===================================================================================================
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// 设置内容
		setContentView(frameLayout);
		//
		dummyVideo = new TextView(this);
		dummyVideo.setBackgroundColor(Color.BLACK);
		dummyVideo.setVisibility(View.GONE);
		rlp = new RelativeLayout.LayoutParams(100, 100);
		coverLayout.addView(dummyVideo, rlp);
		// coverLayout.setGravity(Gravity.CENTER_VERTICAL);
	}

	public void printSize(){
		//之所以用这个是考虑到屏幕的底部有任务栏，所有我们需要处理一下，使用view的实际高度和宽度
		int screenWidth = frameLayout.getWidth();//ScreenUtils.getScreenWidth(this);
		int screenHeight = frameLayout.getHeight();//ScreenUtils.getScreenHeight(this);
		Log.d("zhaoq", "actual size is " + screenWidth + "*" + screenHeight);
		screenWidth = ScreenUtils.getScreenWidth(this);
		screenHeight = ScreenUtils.getScreenHeight(this);
		Log.d("zhaoq", "screen size is " + screenWidth + "*" + screenHeight);
	}
	/**
	 * 将书视图重新布局，并计算设置book的宽度
	 * 
	 * @param book
	 */
	public void layout(Book book,int orientation) {
		//之所以用这个是考虑到屏幕的底部有任务栏，所有我们需要处理一下，使用view的实际高度和宽度
		int screenWidth = frameLayout.getWidth();//ScreenUtils.getScreenWidth(this);
		int screenHeight = frameLayout.getHeight();//ScreenUtils.getScreenHeight(this);
//		printSize();
		
//		if(BookSetting.FIX_SIZE){
//			screenWidth = ScreenUtils.getScreenWidth(this);
//			screenHeight = ScreenUtils.getScreenHeight(this);
//		}
		int bookWidth = book.getBookInfo().bookWidth;
		int bookHeight = book.getBookInfo().bookHeight;
		
		//如果是横竖屏切换，并且是竖向的。输的高度和宽度就需要互换
		if (BookSetting.IS_HOR_VER && orientation == Configuration.ORIENTATION_PORTRAIT ){
			int tmp = bookWidth;
			bookWidth = bookHeight;
			bookHeight = tmp;
		}
		
		int tmpW = screenWidth;
		int tmpH = screenHeight;
		if (BookSetting.IS_HOR){
			screenWidth = Math.max(tmpW, tmpH);
			screenHeight = Math.min(tmpW, tmpH);
		}else{
			screenWidth = Math.min(tmpW, tmpH);
			screenHeight = Math.max(tmpW, tmpH);
		}
		if(HLSetting.FitScreen){
			BookSetting.BOOK_WIDTH = screenWidth;
			BookSetting.BOOK_HEIGHT = screenHeight;
			if(!BookSetting.FITSCREEN_TENSILE){
				int bwidth=Math.max(bookWidth, bookHeight);
				int bheight=Math.min(bookWidth, bookHeight);
				if (!BookSetting.IS_HOR){
					bwidth=Math.min(bookWidth, bookHeight);
					bheight=Math.max(bookWidth, bookHeight);
				}
				float ratio = (float)screenWidth/(float)bwidth;
				if(BookSetting.BOOK_HEIGHT>bheight*ratio){
					BookSetting.BOOK_HEIGHT=(int) (bheight*ratio);
				}
			}
			BookSetting.BOOK_WIDTH4CALCULATE=BookSetting.BOOK_WIDTH;
			BookSetting.BOOK_HEIGHT4CALCULATE = BookSetting.BOOK_HEIGHT;
		}else{
			int bwidth=Math.max(bookWidth, bookHeight);
			int bheight=Math.min(bookWidth, bookHeight);
			if (!BookSetting.IS_HOR){
				bwidth=Math.min(bookWidth, bookHeight);
				bheight=Math.max(bookWidth, bookHeight);
			}
			float widthRatio = (float)bwidth/(float)screenWidth;
			float heightRatio = (float)bheight/(float)screenHeight;
			if(widthRatio > heightRatio){
				BookSetting.BOOK_WIDTH4CALCULATE=screenWidth;
				BookSetting.BOOK_WIDTH = (int) BookSetting.BOOK_WIDTH4CALCULATE;
				BookSetting.BOOK_HEIGHT4CALCULATE=((float)bheight / widthRatio);
				BookSetting.BOOK_HEIGHT = (int) BookSetting.BOOK_HEIGHT4CALCULATE;
			}else{
				BookSetting.BOOK_HEIGHT4CALCULATE=screenHeight;
				BookSetting.BOOK_HEIGHT = (int) BookSetting.BOOK_HEIGHT4CALCULATE;
				BookSetting.BOOK_WIDTH4CALCULATE=((float)bwidth / heightRatio);
				BookSetting.BOOK_WIDTH = (int) BookSetting.BOOK_WIDTH4CALCULATE;
			}
		}

		contentLp.width = BookSetting.BOOK_WIDTH;
		contentLp.height = BookSetting.BOOK_HEIGHT;
		frameLayout.requestLayout();
		contentLayout.requestLayout();
		ViewPage page=BookController.getInstance().getViewPage();
		if(page!=null){
			page.setLayoutParams(page.getCurrentLayoutParams());
			page.pageHeight=(int) (page.getEntity().getHeight()*BookSetting.PAGE_RATIO);
			if(BookSetting.FITSCREEN_TENSILE){
				page.pageHeight=(int) (page.getEntity().getHeight()*BookSetting.PAGE_RATIOY);
			}
			page.requestLayout();
		}
		coverLayout.requestLayout();
		relayoutGlobalButton();
		LogHelper.trace("BookSetting.BOOK_HEIGHT", BookSetting.BOOK_HEIGHT+"",false);
	}

	public void updateCoverPosition(){
		if("addToWindowManager".equals(coverLayout.getTag())){
			wmParams.x=(frameLayout.getWidth()-BookSetting.BOOK_WIDTH)/2;
			wmParams.y=(frameLayout.getHeight()-BookSetting.BOOK_HEIGHT)/2;
			wmParams.width=BookSetting.BOOK_WIDTH;
			wmParams.height=BookSetting.BOOK_HEIGHT;
			mWindowManager.updateViewLayout(coverLayout, wmParams);
		}
	}
	
	public void layout(Book book) {
		//之所以用这个是考虑到屏幕的底部有任务栏，所有我们需要处理一下，使用view的实际高度和宽度
		int screenWidth = frameLayout.getWidth();//ScreenUtils.getScreenWidth(this);
		int screenHeight = frameLayout.getHeight();//ScreenUtils.getScreenHeight(this);
//		printSize();
		
//		if(BookSetting.FIX_SIZE){
//			screenWidth = ScreenUtils.getScreenWidth(this);
//			screenHeight = ScreenUtils.getScreenHeight(this);
//		}
		int bookWidth = book.getBookInfo().bookWidth;
		int bookHeight = book.getBookInfo().bookHeight;
		 
		int tmpW = screenWidth;
		int tmpH = screenHeight;
		if (BookSetting.IS_HOR){
			screenWidth = Math.max(tmpW, tmpH);
			screenHeight = Math.min(tmpW, tmpH);
		}else{
			screenWidth = Math.min(tmpW, tmpH);
			screenHeight = Math.max(tmpW, tmpH);
		}
		
		if(HLSetting.FitScreen){
			BookSetting.BOOK_WIDTH = screenWidth;
			BookSetting.BOOK_HEIGHT = screenHeight;
			if(!BookSetting.FITSCREEN_TENSILE){
				int bwidth=Math.max(bookWidth, bookHeight);
				int bheight=Math.min(bookWidth, bookHeight);
				if (!BookSetting.IS_HOR){
					bwidth=Math.min(bookWidth, bookHeight);
					bheight=Math.max(bookWidth, bookHeight);
				}
				float ratio = (float)screenWidth/(float)bwidth;
				if(BookSetting.BOOK_HEIGHT>bheight*ratio){
					BookSetting.BOOK_HEIGHT=(int) (bheight*ratio);
				}
			}
			BookSetting.BOOK_WIDTH4CALCULATE=BookSetting.BOOK_WIDTH;
			BookSetting.BOOK_HEIGHT4CALCULATE = BookSetting.BOOK_HEIGHT;
		}else{
			int bwidth=Math.max(bookWidth, bookHeight);
			int bheight=Math.min(bookWidth, bookHeight);
			if (!BookSetting.IS_HOR){
				bwidth=Math.min(bookWidth, bookHeight);
				bheight=Math.max(bookWidth, bookHeight);
			}
			float widthRatio = (float)bwidth/(float)screenWidth;
			float heightRatio = (float)bheight/(float)screenHeight;
			if(widthRatio > heightRatio){
				BookSetting.BOOK_WIDTH4CALCULATE=screenWidth;
				BookSetting.BOOK_WIDTH = (int) BookSetting.BOOK_WIDTH4CALCULATE;
				BookSetting.BOOK_HEIGHT4CALCULATE=((float)bheight / widthRatio);
				BookSetting.BOOK_HEIGHT = (int) BookSetting.BOOK_HEIGHT4CALCULATE;
			}else{
				BookSetting.BOOK_HEIGHT4CALCULATE=screenHeight;
				BookSetting.BOOK_HEIGHT = (int) BookSetting.BOOK_HEIGHT4CALCULATE;
				BookSetting.BOOK_WIDTH4CALCULATE=((float)bwidth / heightRatio);
				BookSetting.BOOK_WIDTH = (int) BookSetting.BOOK_WIDTH4CALCULATE;
			}
		}
		contentLp.width = BookSetting.BOOK_WIDTH;
		contentLp.height = BookSetting.BOOK_HEIGHT;
		frameLayout.requestLayout();
		contentLayout.requestLayout();
		ViewPage page=BookController.getInstance().getViewPage();
		if(page!=null){
			page.setLayoutParams(page.getCurrentLayoutParams());
			page.pageHeight=(int) (page.getEntity().getHeight()*BookSetting.PAGE_RATIO);
			if(BookSetting.FITSCREEN_TENSILE){
				page.pageHeight=(int) (page.getEntity().getHeight()*BookSetting.PAGE_RATIOY);
			}
			page.requestLayout();
		}
		ViewPage prePage=BookController.getInstance().preViewPage;
		if(prePage!=null){
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lp.width=BookSetting.BOOK_WIDTH;
			lp.height=(int) ScreenUtils.getHorScreenValue(prePage.getEntity().getHeight());
			prePage.setLayoutParams(lp);
			prePage.setX(BookController.getInstance().getViewPage().getX()-BookSetting.BOOK_WIDTH);
			prePage.requestLayout();
		}
		ViewPage nextPage=BookController.getInstance().nextViewPage;
		if(nextPage!=null){
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lp.width=BookSetting.BOOK_WIDTH;
			lp.height=(int) ScreenUtils.getHorScreenValue(nextPage.getEntity().getHeight());
			nextPage.setLayoutParams(lp);
			nextPage.setX(BookController.getInstance().getViewPage().getX()+BookSetting.BOOK_WIDTH);
			nextPage.requestLayout();
		}
		coverLayout.requestLayout();
		relayoutGlobalButton();
		LogHelper.trace("BookSetting.BOOK_HEIGHT", BookSetting.BOOK_HEIGHT+"",false);
	}
	
	
	/**
	 * 显示书签
	 */
	public void showMark() {
		initMarkView();
		// 以动画形式将书签视图显示出来
		TranslateAnimation animation = new TranslateAnimation(0, 0,
				-BookSetting.SCREEN_HEIGHT, 0);
		animation.setDuration(300);
		animation.setRepeatCount(0);
		markLayout.setAnimation(animation);
		animation.startNow();
		markLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 刷新book mark
	 */
	public void refreshMark() {
		if(markLayout!=null){
			markLayout.refresh();
		}
		if(markView4NavMenu!=null){
			markView4NavMenu.refresh();
		}
	}
	
	public void refreshSnapshots(){
		if(basAdapter!=null){
			basAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 绘制书签视图
	 * 
	 * @return
	 */
	private void initMarkView() {
		if (markLayout != null) {
			return;
		}
		markLayout = new MarkViewLayout(this);
		int markViewWidth = 260;
		int markViewHeight = BookSetting.SCREEN_HEIGHT / 2;
		if (markViewHeight > 500) {
			markViewHeight = 500;
		}
		RelativeLayout.LayoutParams markViewLp = new RelativeLayout.LayoutParams(
				markViewWidth, markViewHeight);
		markViewLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		markViewLp.addRule(RelativeLayout.CENTER_VERTICAL);
		coverLayout.addView(markLayout, markViewLp);
	}

	public void setVideoCover(int x, int y, int w, int h) {
		rlp.leftMargin = x;
		rlp.topMargin = y;
		rlp.width = w;
		rlp.height = h;
	}

	/**
	 * 获得广告视图
	 * 
	 * @return
	 */
	protected View getAdView() {
		return null;
	}

	/**
	 * 设置广告试图
	 * 
	 * @param aa
	 */
	private void setAD(RelativeLayout aa) {
	
		if (HLSetting.IsAD) {
			
			// 从子类获得广告视图，如果为null则说明不需要广告
			adViewLayout = getAdView();
			if (adViewLayout == null)
				return;
			String adPos = BookController.getInstance().getBook().getBookInfo().position;

			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			if ("top".equals(adPos)) {
				params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			} else {
				params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			}
			this.addContentView(adViewLayout, params);
			BookController.getInstance().setAdView(adViewLayout);
			adViewLayout.bringToFront();
		
		}
	}

	/**
	 * 绘制全局按钮 至少绘制
	 */
	private void createGlobaButton(ArrayList<ButtonEntity> buttonList) {
		if (buttonList == null || buttonList.size() == 0)
			return;
		// 设置实体
		for (ButtonEntity buttonEntity : buttonList) {
			if (buttonEntity.getWidth() == 0)
				continue;
			String btnType = buttonEntity.getType();
			if (ButtonEntity.OPEN_NAVIGATE_BTN.equals(btnType)) {
				if (!BookSetting.IS_NO_NAVIGATION) {
					galleyEntity = buttonEntity;
					galleyButton = drawGlobalButton(buttonEntity,
							R.drawable.btngarally);
					coverLayout.addView(galleyButton);
				}
			} else if (ButtonEntity.HOME_PAGE_BTN.equals(btnType)) {
				homeEntity = buttonEntity;
				homeButton = drawGlobalButton(buttonEntity, R.drawable.home);
				homeButton.setId(homeButton_h_ID);
				coverLayout.addView(homeButton);
			} else if (ButtonEntity.PRE_PAGE_BTN.equals(btnType)) {
				leftEntity = buttonEntity;
				leftButton = drawGlobalButton(buttonEntity, R.drawable.left);
				coverLayout.addView(leftButton);
			} else if (ButtonEntity.NEXT_PAGE_BTN.equals(btnType)) {
				rightEntity = buttonEntity;
				rightButton = drawGlobalButton(buttonEntity, R.drawable.right);
				coverLayout.addView(rightButton);
			} else if (ButtonEntity.VER_OPEN_NAVIGATE_BTN.equals(btnType)) {
				if (!BookSetting.IS_NO_NAVIGATION) {
					vergalleyEntity = buttonEntity;
					vergalleyButton = drawGlobalButton(buttonEntity,
							R.drawable.btngarally);
					coverLayout.addView(vergalleyButton);
				}

			} else if (ButtonEntity.VER_HOME_PAGE_BTN.equals(btnType)) {
				verhomeEntity = buttonEntity;
				verhomeButton = drawGlobalButton(buttonEntity, R.drawable.home);
				verhomeButton.setId(homeButton_v_ID);
				coverLayout.addView(verhomeButton);
			} else if (ButtonEntity.VER_PRE_PAGE_BTN.equals(btnType)) {
				verleftEntity = buttonEntity;
				verleftButton = drawGlobalButton(buttonEntity, R.drawable.left);
				coverLayout.addView(verleftButton);
			} else if (ButtonEntity.VER_NEXT_PAGE_BTN.equals(btnType)) {
				verrightEntity = buttonEntity;
				verrightButton = drawGlobalButton(buttonEntity,
						R.drawable.right);
				coverLayout.addView(verrightButton);
			}
		}
		if (BookSetting.IS_SHELVES) {
			int inity = 0;
			if (!HLSetting.FitScreen) {
				inity = (int) (ScreenUtils.getScreenHeight(this) - ScreenUtils
						.getVerScreenValue((BookController.getInstance()
								.getViewPage().getEntity().getHeight())));
				inity = inity / 2;
				if (inity < 0)
					inity = 0;
			}

			// 返回按钮单独绘制
			backButton = new ImageButton(this);
			backButton.setBackgroundResource(R.drawable.back);
			int btnWidth = (int) ScreenUtils.getHorScreenValue(45);
			if(btnWidth<BookSetting.BOOK_WIDTH*45/1024){
				btnWidth=BookSetting.BOOK_WIDTH*45/1024;
			}
			int btnHeight = btnWidth;
			// if(btnWidth<69)btnWidth = 69;
			// if(btnHeight<41)btnHeight = 41;
			//
			RelativeLayout.LayoutParams backLp = new RelativeLayout.LayoutParams(
					btnWidth, btnHeight);
//			backLp.addRule(RelativeLayout.ALIGN_TOP, homeButton_h_ID);
			backLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			backLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			coverLayout.addView(backButton, backLp);
			//
			backButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					onBackPressed();
				}
			});
		}

		relayoutGlobalButton();
		// relayoutGlobalButton();
	}

	private void hidGlobalButton(ImageButton btn) {
		if (btn != null)
			btn.setVisibility(View.GONE);
	}

	public void relayoutHorGlobalButton() {
		hidGlobalButton(rightButton);
		hidGlobalButton(leftButton);
		hidGlobalButton(homeButton);
		hidGlobalButton(galleyButton);

		hidGlobalButton(verrightButton);
		hidGlobalButton(verleftButton);
		hidGlobalButton(verhomeButton);
		hidGlobalButton(vergalleyButton);

		if (BookSetting.FLIPCODE == 0)
			setGlobalLayoutParams(rightEntity, rightButton);
		if (BookSetting.FLIPCODE == 0)
			setGlobalLayoutParams(leftEntity, leftButton);
		setGlobalLayoutParams(homeEntity, homeButton);
		setGlobalLayoutParams(galleyEntity, galleyButton);
		BookController.getInstance().registerButton(leftButton, rightButton,
				galleyButton, homeButton);
	}

	public void relayoutVerGlobalButton() {
		hidGlobalButton(rightButton);
		hidGlobalButton(leftButton);
		hidGlobalButton(homeButton);
		hidGlobalButton(galleyButton);

		hidGlobalButton(verrightButton);
		hidGlobalButton(verleftButton);
		hidGlobalButton(verhomeButton);
		hidGlobalButton(vergalleyButton);

		if (BookSetting.FLIPCODE == 0)
			setGlobalLayoutParams(verrightEntity, verrightButton);
		if (BookSetting.FLIPCODE == 0)
			setGlobalLayoutParams(verleftEntity, verleftButton);
		setGlobalLayoutParams(verhomeEntity, verhomeButton);
		setGlobalLayoutParams(vergalleyEntity, vergalleyButton);

		BookController.getInstance().registerButton(verleftButton,
				verrightButton, vergalleyButton, verhomeButton);
	}

	/*
	 * 重新布局所有的按钮位置以及大小并注册按钮
	 */
	public void relayoutGlobalButton() {
		try {
			if (BookSetting.IS_HOR_VER) {
				// BookSetting.IS_HOR =
				// AppContext.getScreenWidth(this)>AppContext.getScreenHeight(this);
				if (BookSetting.IS_HOR) {
					Log.d("hl","relayoutHorGlobalButton");
					relayoutHorGlobalButton();
				} else {
					Log.d("hl","relayoutVerGlobalButton");
					relayoutVerGlobalButton();
				}
			} else {
				relayoutHorGlobalButton();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("hl", "布局按钮出错");
		}

	}

	public static int MINGLOBEALBUTTONWIDTH = 30;

	/**
	 * 设置全局按钮的大小位置
	 * 
	 * @param buttonEntity
	 * @param buttonView
	 */
	private void setGlobalLayoutParams(ButtonEntity buttonEntity,
			ImageButton buttonView) {
		if (buttonEntity == null || buttonView == null)
			return;
		buttonView.setTag(buttonEntity);
		if (buttonEntity.isVisible())
			buttonView.setVisibility(View.GONE);
		// 先计算作起始点坐标
		int initx = 0;

		int inity = 0;
		if (!HLSetting.FitScreen) {
			initx = (int) (BookSetting.BOOK_WIDTH - ScreenUtils
					.getHorScreenValue((BookController.getInstance()
							.getViewPage().getEntity().getWidth())));
			initx = initx / 2;
			if (initx < 0)
				initx = 0;

			inity = (int) (BookSetting.BOOK_HEIGHT - ScreenUtils
					.getVerScreenValue((BookController.getInstance()
							.getViewPage().getEntity().getHeight())));
			inity = inity / 2;
			if (inity < 0)
				inity = 0;
		}
		float floatLeftMargin = ScreenUtils.getHorScreenValue(buttonEntity
				.getX() + buttonEntity.getWidth());
		// int leftMargin = (int) ShowUtils.getHorScreenValue(buttonEntity
		// .getX());

//		float floatbtnWidth = ScreenUtils.getHorScreenValue(buttonEntity
//				.getWidth());
		int btnWidth = (int) ScreenUtils.getHorScreenValue(buttonEntity.getWidth());
		if (btnWidth < ScreenUtils.dip2px(this, MINGLOBEALBUTTONWIDTH)) {
			btnWidth = ScreenUtils.dip2px(this, MINGLOBEALBUTTONWIDTH);
		}
		int leftMargin = (int) (floatLeftMargin - btnWidth);
		if (leftMargin < 0) {
			leftMargin = 0;
		}
		//相对于书的位置，而不是页面的位置
		float floatbtnHeight = buttonEntity.getY()*BookSetting.BOOK_HEIGHT/BookController.getInstance().getBook().getBookInfo().bookHeight;
		if(BookSetting.IS_HOR){
			floatbtnHeight = buttonEntity.getY()*BookSetting.BOOK_HEIGHT/BookController.getInstance().getBook().getBookInfo().bookWidth;
		}
		int btnHeight = (int) ScreenUtils.getHorScreenValue(buttonEntity
				.getHeight());
		if (btnHeight < ScreenUtils.dip2px(this, MINGLOBEALBUTTONWIDTH))
			btnHeight = ScreenUtils.dip2px(this, MINGLOBEALBUTTONWIDTH);
		RelativeLayout.LayoutParams btnLp = new RelativeLayout.LayoutParams(
				btnWidth, btnHeight);
		// btnLp.leftMargin = leftMargin - btnWidth;
		// btnLp.rightMargin = topMargin - btnHeight;
		btnLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		btnLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		btnLp.leftMargin = initx + leftMargin;// (int)
		// ShowUtils.getHorScreenValue(buttonEntity.getX());
		btnLp.topMargin = (int) (inity + floatbtnHeight);// (int)
		// ShowUtils.getVerScreenValue(buttonEntity.getY());
		// Log.d("hl","global button x y is " + leftMargin+ " " + topMargin);
		buttonView.setLayoutParams(btnLp);
		buttonView.setVisibility(View.GONE);
	}

	/**
	 * 绘制按钮 并装入但是不显示，当pageload 的时候再显示
	 * 
	 * @param buttonEntity
	 * @param defultRes
	 *            默认的图片，如果按钮没有传递过来背景图片的话
	 */
	private ImageButton drawGlobalButton(ButtonEntity buttonEntity,
			int defultRes) {
		ImageButton btn = new ImageButton(this);
		btn.setTag(buttonEntity);
		// 1设置按钮大小和位置
		// 2设置背景图片
		String btnResource = buttonEntity.getSource();
		String btnSelectResource = buttonEntity.getSelectedSource();
		// 3设置按钮背景图片，如果是有正常和按下的图片都有，我们就正常显示，如果只有一个，那么就没有按下的效果，如果没有正常的图片，那么只好用默认的了
		if (StringUtils.isEmpty(btnResource)) {
			btn.setBackgroundResource(defultRes);
		} else if (StringUtils.isEmpty(btnSelectResource)) {
			btn.setBackgroundDrawable(new BitmapDrawable(BitmapUtils.getBitMap(
					btnResource, this, buttonEntity.getWidth(),
					buttonEntity.getHeight())));
		} else {
			btn.setBackgroundDrawable(ImageUtils.getButtonDrawable(btnResource,
					btnSelectResource, this));
		}
		btn.setVisibility(View.GONE);
		return btn;
	}

	/**
	 * 绘制全局视图包括全局按钮，广告位置，下面的导航视图 这个是在工程启动的时候调用的
	 */
	public void setupViews() {

//		try {
//			if (preShowViewAction != null) {
//				preShowViewAction.doAction();
//			}
//			// if(!BookSetting.IS_NO_NAVIGATION){
//			createGlobaButton(BookController.getInstance().getBook()
//					.getButtons());
//			addGallery();
//			// }
//			setAD(contentLayout);
//			// 创建gallery
//		} catch (Exception e) {
//
//		}

		try {
			if (preShowViewAction != null) {
				preShowViewAction.doAction();
			}
			 if(!BookController.getInstance().getBook().getBookInfo().bookNavType.equals("indesign_slider_view")){
				createGlobaButton(BookController.getInstance().getBook()
						.getButtons());
				addGallery();
			 }else{
				 mUPNav=getUPNav();
				 mUPNav.setId(NAVMENU_ID);
				 mUPNav.setNavMenuListenner(new NavMenuListenner() {
					
					@Override
					public void onItem6Click(View itemView) {
						if(markView4NavMenu==null){
							loadMarkView4ShowMark();
						}
						toggleViewVisibility("markView4NavMenu");
						if(markView4NavMenu.getVisibility()==View.VISIBLE){
							if(listView4ShowSnapshots!=null&&listView4ShowSnapshots.getVisibility()==View.VISIBLE){
								toggleViewVisibility("listView4ShowSnapshots");
							}
							if(searchView!=null&&searchView.getVisibility()==View.VISIBLE){
								toggleViewVisibility("searchView");
							}
						}
						toggleBottomAndMiddleNavMenu();
					}
					

					@Override
					public void onItem5Click(View itemView) {
						if(searchView==null){
							loadSearchView();
						}
						toggleViewVisibility("searchView");
						if(searchView.getVisibility()==View.VISIBLE){
							if(listView4ShowSnapshots!=null&&listView4ShowSnapshots.getVisibility()==View.VISIBLE){
								toggleViewVisibility("listView4ShowSnapshots");
							}
							if(markView4NavMenu!=null&&markView4NavMenu.getVisibility()==View.VISIBLE){
								toggleViewVisibility("markView4NavMenu");
							}
						}
						toggleBottomAndMiddleNavMenu();
						searchView.listViewAdapter.notifyDataSetChanged();
					}
					
					@Override
					public void onItem4Click(View itemView) {
						onBackPressed();
					}
					
					@Override
					public void onItem3Click(View itemView) {
						if(listView4ShowSnapshots==null){
							loadListView4ShowSnapshots();
						}
						refreshSnapshots();
						toggleViewVisibility("listView4ShowSnapshots");
						if(listView4ShowSnapshots.getVisibility()==View.VISIBLE){
							if(searchView!=null&&searchView.getVisibility()==View.VISIBLE){
								toggleViewVisibility("searchView");
							}
							if(markView4NavMenu!=null&&markView4NavMenu.getVisibility()==View.VISIBLE){
								toggleViewVisibility("markView4NavMenu");
							}
						}
						toggleBottomAndMiddleNavMenu();
					}
					
					@Override
					public void onItem2Click(View itemView) {
						if(getMiddleNav().isShowing()){
							return;
						}
						BookController bookController=BookController.getInstance();
						ArrayList<String> pageHistory=bookController.getPageHistory();
						if(pageHistory.size()>=2){
							pageHistory.remove(pageHistory.size()-1);
							bookController.playPageById(pageHistory.get(pageHistory.size()-1));
						}
						BookController controller=BookController.getInstance();
						ArrayList<String> pagesIDs=controller.getBook().getSections().get(controller.currendsectionindex).getPages();
						int currentPageIndex=pagesIDs.indexOf(controller.mainViewPage.getEntity().getID());
						mBottomNav.seekTo(currentPageIndex*1.0f/pagesIDs.size());
						getMiddleNav().changeSelection(currentPageIndex*1.0f/pagesIDs.size());
					}
					
					@Override
					public void onItem1Click(View itemView) {
						if(getMiddleNav().isShowing()){
							return;
						}
						String pageID = "";
						BookController bookController = BookController.getInstance();
						Book book = bookController.getBook();
						String homePageID = book.getBookInfo().homePageID;
						if (StringUtils.isEmpty(homePageID)) {
							pageID = book.getSections().get(0).getPages().get(0);
						} else {
							pageID = book.getBookInfo().homePageID;
						}
						bookController.loadAndMoveTo(pageID);
					}

					@Override
					public void onShow() {
						refreshSnapshots();
						wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;  
						updateCoverPosition();
					}

					@Override
					public void onDismiss() {
						if(listView4ShowSnapshots!=null){
							listView4ShowSnapshots.setVisibility(View.INVISIBLE);
							listView4ShowSnapshots.setAdapter(null);
							listView4ShowSnapshots=null;
						}
						if(markView4NavMenu!=null){
							markView4NavMenu.setVisibility(View.INVISIBLE);
						}
						if(searchView!=null){
							searchView.setVisibility(View.INVISIBLE);
						}
						wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;  
						updateCoverPosition();
					}
				});
				 mBottomNav=getBottomNav();
				 mBottomNav.setId(BOTTOM_NAV_ID);
				 mBottomNav.setBottomNavListenner(new BottomNavListenner() {
					
					@Override
					public void onShow() {
						BookController controller=BookController.getInstance();
						ArrayList<String> pagesIDs=controller.getBook().getSections().get(controller.currendsectionindex).getPages();
						int currentPageIndex=pagesIDs.indexOf(controller.mainViewPage.getEntity().getID());
						mBottomNav.seekTo(currentPageIndex*1.0f/pagesIDs.size());
						getMiddleNav().checkChangeSelection(currentPageIndex*1.0f/pagesIDs.size(),false);
					}
					
					@Override
					public void onDismiss() {
						
					}

					@Override
					public void onSliderPositionChanged(float newPosition,float totalLength) {
						getMiddleNav().checkChangeSelection(newPosition/totalLength,true);
					}

					@Override
					public void onSliderTouchDown() {
						if(mMiddleNav!=null&&!mMiddleNav.isShowing()){
							mMiddleNav.show();
						}
					}
					
					
				});
				 mMiddleNav=getMiddleNav();
				 mMiddleNav.setId(MIDDLE_NAV_ID);
			 }
			setAD(contentLayout);
			// 创建gallery
		} catch (Exception e) {

		}
		
	}
	
	private void toggleBottomAndMiddleNavMenu() {
		if(markView4NavMenu!=null&&markView4NavMenu.getVisibility()==View.VISIBLE){
			if(mBottomNav!=null&&mBottomNav.isShowing()){
				mBottomNav.setVisibility(View.INVISIBLE);
			}
			if(mMiddleNav!=null&&mMiddleNav.isShowing()){
				mMiddleNav.dismiss();
			}
		}else if(searchView!=null&&searchView.getVisibility()==View.VISIBLE){
			if(mBottomNav!=null&&mBottomNav.isShowing()){
				mBottomNav.setVisibility(View.INVISIBLE);
			}
			if(mMiddleNav!=null&&mMiddleNav.isShowing()){
				mMiddleNav.dismiss();
			}
		}else if(listView4ShowSnapshots!=null&&listView4ShowSnapshots.getVisibility()==View.VISIBLE){
			if(mBottomNav!=null&&mBottomNav.isShowing()){
				mBottomNav.setVisibility(View.INVISIBLE);
			}
			if(mMiddleNav!=null&&mMiddleNav.isShowing()){
				mMiddleNav.dismiss();
			}
		}else{
			if(mBottomNav!=null&&!mBottomNav.isShowing()){
				mBottomNav.setVisibility(View.VISIBLE);
			}
		}
	}
	

	private void toggleViewVisibility(String viewName){
		if(viewName.equals("markView4NavMenu")){
			if(markView4NavMenu!=null){
				if(markView4NavMenu.getVisibility()!=View.VISIBLE){
					markView4NavMenu.setVisibility(View.VISIBLE);
					mUPNav.getItem(5).setBackgroundResource(R.drawable.indesign_showconordown);
				}else{
					markView4NavMenu.setVisibility(View.INVISIBLE);
					mUPNav.getItem(5).setBackgroundResource(R.drawable.btn_showconor_selecor);
				}
			}
		}else if(viewName.equals("searchView")){
			if(searchView!=null){
				if(searchView.getVisibility()!=View.VISIBLE){
					searchView.setVisibility(View.VISIBLE);
					mUPNav.getItem(4).setBackgroundResource(R.drawable.indesign_searchbtndown);
				}else{
					searchView.setVisibility(View.INVISIBLE);
					mUPNav.getItem(4).setBackgroundResource(R.drawable.btn_search_selector);
				}
			}
		}else if(viewName.equals("listView4ShowSnapshots")){
			if(listView4ShowSnapshots!=null){
				if(listView4ShowSnapshots.getVisibility()!=View.VISIBLE){
					listView4ShowSnapshots.setVisibility(View.VISIBLE);
					mUPNav.getItem(2).setBackgroundResource(R.drawable.indesign_navcatabtndown);
				}else{
					listView4ShowSnapshots.setVisibility(View.INVISIBLE);
					mUPNav.getItem(2).setBackgroundResource(R.drawable.btn_cata_selector);
				}
			}
		}
	}


	protected void loadSearchView() {
		searchView=new MYSearchView(HLLayoutActivity.this);
		searchView.setVisibility(View.INVISIBLE);
		RelativeLayout.LayoutParams params4Snapshots=new RelativeLayout.LayoutParams(300, LayoutParams.WRAP_CONTENT);
		params4Snapshots.addRule(RelativeLayout.BELOW, NAVMENU_ID);
		params4Snapshots.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params4Snapshots.topMargin=-1;
		coverLayout.addView(searchView, params4Snapshots);
	}

	protected void loadMarkView4ShowMark() {
		markView4NavMenu=new MarkView4NavMenu(HLLayoutActivity.this);
		markView4NavMenu.setVisibility(View.INVISIBLE);
		RelativeLayout.LayoutParams params4Snapshots=new RelativeLayout.LayoutParams(300, LayoutParams.WRAP_CONTENT);
		params4Snapshots.addRule(RelativeLayout.BELOW, NAVMENU_ID);
		params4Snapshots.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params4Snapshots.topMargin=-1;
		coverLayout.addView(markView4NavMenu, params4Snapshots);
		
	}

	public void loadListView4ShowSnapshots() {
		listView4ShowSnapshots=new ListView(HLLayoutActivity.this);
		listView4ShowSnapshots.setBackgroundResource(R.drawable.indesign_colle_bgimg);
		RelativeLayout.LayoutParams params4Snapshots=new RelativeLayout.LayoutParams(300, LayoutParams.MATCH_PARENT);
		params4Snapshots.addRule(RelativeLayout.BELOW, NAVMENU_ID);
		params4Snapshots.leftMargin=-5;
		params4Snapshots.topMargin=-2;
		coverLayout.addView(listView4ShowSnapshots, params4Snapshots);
		listView4ShowSnapshots.setVisibility(View.INVISIBLE);
		listView4ShowSnapshots.setFadingEdgeLength(0);
		listView4ShowSnapshots.setCacheColorHint(Color.TRANSPARENT);
		listView4ShowSnapshots.setSelector(new ColorDrawable(Color.TRANSPARENT));
		basAdapter=new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder;
				if(convertView==null){
					holder=new ViewHolder();
					convertView=new RelativeLayout(HLLayoutActivity.this);
					AbsListView.LayoutParams params4layout=new AbsListView.LayoutParams(300, 100);
					convertView.setLayoutParams(params4layout);
					holder.imageView=new ImageView(HLLayoutActivity.this);
					holder.imageView.setId(0x20010);
					holder.imageView.setScaleType(ScaleType.FIT_XY);
					((ViewGroup) convertView).addView(holder.imageView);
					holder.linearLayout=new LinearLayout(HLLayoutActivity.this);
					holder.linearLayout.setOrientation(LinearLayout.VERTICAL);
					holder.textView4tittle=new TextView(HLLayoutActivity.this);
					holder.textView4tittle.setTextSize(15);
					holder.textView4tittle.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
					holder.textView4tittle.setSingleLine(true);
					holder.textView4tittle.setTextColor(Color.WHITE);
					LinearLayout.LayoutParams params4textView=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params4textView.setMargins(20, 0, 20, 0);
					holder.linearLayout.addView(holder.textView4tittle,params4textView);
					holder.textView=new TextView(HLLayoutActivity.this);
					holder.textView.setTextSize(12);
					holder.textView.setEllipsize(TextUtils.TruncateAt.valueOf("END")); 
					holder.textView.setSingleLine(true);
					holder.textView.setTextColor(Color.WHITE);
					LinearLayout.LayoutParams params4text=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params4text.setMargins(20, 0, 20, 0);
					holder.linearLayout.addView(holder.textView,params4text);
					RelativeLayout.LayoutParams params4LinearLayout=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					params4LinearLayout.addRule(RelativeLayout.CENTER_VERTICAL);
					params4LinearLayout.addRule(RelativeLayout.RIGHT_OF,holder.imageView.getId());
					((ViewGroup) convertView).addView(holder.linearLayout,params4LinearLayout);
					convertView.setTag(holder);
				}else{
					holder=(ViewHolder) convertView.getTag();
				}
				
				Bitmap bitmap=BitmapUtils.getBitMap(BookController.getInstance().getCurrentSnapshots().get(position), HLLayoutActivity.this);
				holder.imageView.setImageBitmap(bitmap);
				RelativeLayout.LayoutParams params4image=new RelativeLayout.LayoutParams(60*bitmap.getWidth()/bitmap.getHeight(), 60);
				params4image.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params4image.addRule(RelativeLayout.CENTER_VERTICAL);
				params4image.leftMargin=20;
				holder.imageView.setLayoutParams(params4image);
				BookController controller=BookController.getInstance();
				String pageID=controller.getBook().getSections().get(controller.currendsectionindex).getPages().get(position);
				PageEntity pageEntity = PageEntityController.getInstance().getPageEntityByPageId(HLLayoutActivity.this, pageID);
				holder.textView4tittle.setText(pageEntity.getTitle());
				holder.textView.setText(pageEntity.getDescription());
				return convertView;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				BookController controller=BookController.getInstance();
				return controller.getBook().getSections().get(controller.currendsectionindex).getPages().size();
			}
			class ViewHolder{
				ImageView imageView;
				LinearLayout linearLayout;
				TextView textView4tittle;
				TextView textView;
			}
		};
		listView4ShowSnapshots.setAdapter(basAdapter);
		listView4ShowSnapshots.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				BookController bookController = BookController.getInstance();
				String pageID = BookController.getInstance().getSectionPageIdByPosition(position);
				if(!pageID.equals(BookController.getInstance().mainPageID)){
					bookController.playPageById(pageID);
				}
				getUPNav().dismiss();
			}
		});
	}
	

	public IndesignUP getUPNav() {
		if(mUPNav==null){
			mUPNav=new IndesignUP(this);
			coverLayout.addView(mUPNav);
			mUPNav.setVisibility(View.INVISIBLE);
		}
		return mUPNav;
	}
	
	public IndesignBottom getBottomNav() {
		if(mBottomNav==null){
			mBottomNav=new IndesignBottom(this);
			RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			coverLayout.addView(mBottomNav,layoutParams);
			mBottomNav.setVisibility(View.INVISIBLE);
		}
		return mBottomNav;
	}
	

	public IndesignMiddle getMiddleNav() {
		if(mMiddleNav==null){
			mMiddleNav=new IndesignMiddle(this);
			RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			if(mUPNav!=null){
				layoutParams.addRule(RelativeLayout.BELOW, mUPNav.getId());
			}
			if(mBottomNav!=null){
				layoutParams.addRule(RelativeLayout.ABOVE, mBottomNav.getId());
			}
			layoutParams.bottomMargin=-2;
			layoutParams.topMargin=-2;
			coverLayout.addView(mMiddleNav,layoutParams);
			mMiddleNav.setVisibility(View.INVISIBLE);
		}
		return mMiddleNav;
	}
	
	public MarkView4NavMenu getMarkView4NavMenu(){
		return markView4NavMenu;
	}
	/**
	 * 设置显示下面的导航栏
	 */
	public void addGallery() {
		if (BookSetting.IS_NO_NAVIGATION)
			return;
		if (BookSetting.IS_HOR_VER == true) {
			setSnapshots();
		}

		gallery = GalleyHelper.getGalley(this);

		RelativeLayout.LayoutParams glp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		glp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		glp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		glp.bottomMargin = gallery.getLayoutParams().height;
		// RelativeLayout lay = new RelativeLayout(this);
		// lay.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		// lay.setBackgroundColor(Color.RED);
		gallery.setId(gallery_ID);
		BookController.getInstance().setGallery(gallery);
		gallery.hideGalleryInfor();
		// lay.addView(gallery,new RelativeLayout.LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// lay.addView(gallery.getHideImgButton());
		// lay.addView(gallery.getPageTextView());

		RelativeLayout.LayoutParams imgBtnlp = new RelativeLayout.LayoutParams(
				ScreenUtils.dip2px(this, 120), ScreenUtils.dip2px(this, 48));
		imgBtnlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		imgBtnlp.addRule(RelativeLayout.CENTER_HORIZONTAL);

		imgBtnlp.bottomMargin = gallery.getLayoutParams().height;
		// RelativeLayout.LayoutParams hideBtnLp = new
		// RelativeLayout.LayoutParams(
		// 128,
		// 50);
		// hideBtnLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		// hideBtnLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		// hideBtnLp.bottomMargin = BookSetting.SNAPSHOTS_HEIGHT-28;
		// mHideImgButton.setLayoutParams(hideBtnLp);

		coverLayout.addView(gallery.getHideImgButton(), imgBtnlp);
		coverLayout.addView(gallery.getPageTextView());
		coverLayout.addView(gallery);
	}

	/**
	 * 初始化下面的galery镜像的大小
	 */
	public void setSnapshots() {
		if (BookSetting.IS_HOR == true) {
			if (BookSetting.SNAPSHOTS_WIDTH < BookSetting.SNAPSHOTS_HEIGHT) {
				int a = BookSetting.SNAPSHOTS_WIDTH;
				BookSetting.SNAPSHOTS_WIDTH = BookSetting.SNAPSHOTS_HEIGHT;
				BookSetting.SNAPSHOTS_HEIGHT = a;
			}
		}
		if (BookSetting.IS_HOR == false) {
			if (BookSetting.SNAPSHOTS_WIDTH > BookSetting.SNAPSHOTS_HEIGHT) {
				int a = BookSetting.SNAPSHOTS_WIDTH;
				BookSetting.SNAPSHOTS_WIDTH = BookSetting.SNAPSHOTS_HEIGHT;
				BookSetting.SNAPSHOTS_HEIGHT = a;
			}
		}
	}

	public void hideGalley() {
		if (gallery != null) {
			gallery.hideGalleryInfor();
		}
	}

	public void setFlipView() {
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		switch (BookSetting.FLIPCODE) {
		case 0:
			absPageView = new PageWidgetNew(this);
			break;
		case 1:
			absPageView = new PageFlipView(this);
			break;
		case 2:
			absPageView = new PageFlipView(this);
			break;
		default:
			break;
		}
		mainLayout.addView(absPageView, contentLp);
		absPageView.setVisibility(View.GONE);
		BookController.getInstance().setFlipView(absPageView);
		AbstractPageFlipView apf2 = new PageFlipVerticleView(this);
		BookController.getInstance().setSubPageViewFlip(apf2);
		mainLayout.addView(apf2, contentLp);
		
		commonLayout.bringToFront();
	}
}

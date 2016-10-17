package com.hl.android.controller;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.android.HLActivity;
import com.hl.android.HLReader;
import com.hl.android.R;
import com.hl.android.book.BookDecoder;
import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.Book;
import com.hl.android.book.entity.ButtonEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.book.entity.SectionEntity;
import com.hl.android.book.entity.SnapshotEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.core.helper.BehaviorHelper;
import com.hl.android.core.helper.BookHelper;
import com.hl.android.core.helper.animation.MyAnimation4CubeEffect;
import com.hl.android.core.helper.animation.MyAnimation4FlipEffect;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.ViewCell;
import com.hl.android.view.ViewPage;
import com.hl.android.view.component.AudioComponent;
import com.hl.android.view.component.bean.TimerShowBean;
import com.hl.android.view.gallary.base.AbstractGalley;
import com.hl.android.view.layout.HLRelativeLayout;
import com.hl.android.view.pageflip.AbstractPageFlipView;
import com.hl.android.view.pageflip.ActionOnEnd;

/**
 * @author webcat
 */
public class BookController {

	public ViewPage commonPage;
	public ViewPage prevCommonPage;
	public boolean prevCommonPageEmpty = false;
	public ViewPage nextCommonPage;
	public boolean nextCommonPageEmpty = false;
	public String commonPageID = "";
	public ViewPage nextViewPage;
	public ViewPage preViewPage;

	public ViewPage viewPage, mainViewPage;
	public Book book;
	private AbstractPageFlipView flipView;
	private AbstractPageFlipView subPageViewFlip;
	public int currendsectionindex = 0;
	public SectionEntity section;
	private static BookController bookController;
	private ArrayList<String> snapshots;//
	private ImageButton preButton, nextButton;
	private ImageButton galleyButton, homeButton;
	private AudioComponent backgroundMusic;
	private AbstractGalley gallery;
	private HLRelativeLayout hlBookLayout;
	private WindowManager windwoManager;
	private AutoPageCountDown autoPageViewCountDown = null;
	private View adView;
	private RelativeLayout pdfMenuBarelativeLayout;
	public HLActivity hlActivity;
	private boolean isSettingGallery = false;//
	public boolean shouldKeepMainPage = false;

	private String newPageID = "";

	public int count = -1;
	public TimerShowBean descShow = new TimerShowBean();
	public TimerShowBean ascShow = new TimerShowBean();
	Bitmap resizeBmp = null;

	public static BookController getInstance() {

		if (null == bookController) {
			bookController = new BookController();
		}
		return bookController;
	}

	public static void recyle() {
		if (null != bookController) {
			bookController = null;
		}
	}

	/**
	 * 加载启动画面
	 * 
	 * @param _viewPage
	 */
	public void loadStartPage(ViewPage _viewPage) {
		PageEntity pageEntity = BookDecoder.getInstance().decodePageEntity(
				this.getHLActivity(), book.getStartPageID());
		_viewPage.load(pageEntity);
	}

	/**
	 * 播放开机启动画面
	 * 
	 * @param hlBookLayout
	 */
	public void playStartPage(ViewPage _viewPage) {
		this.viewPage = _viewPage;
		this.getHLBookLayout().addView(viewPage,
				viewPage.getCurrentLayoutParams());
		viewPage.startPlay();
		viewPage.playVideo();
	}

	public void playStartPage(ViewPage _viewPage, PageEntity entity) {
		this.viewPage = _viewPage;
		viewPage.load(entity);

		this.getSectionPagePosition(entity.getID());

		this.getHLBookLayout().addView(viewPage,
				viewPage.getCurrentLayoutParams());
		this.startPlay();
		setDefaultView(0);
		if (null != book.getBookInfo().backgroundMusicId
				&& book.getBookInfo().backgroundMusicId.length() > 0) {
			playBackgroundMusic();
		}

		subPageList = viewPage.getEntity().getNavePageIds();
		mainPageID = viewPage.getEntity().getID();
		mainViewPage = viewPage;
	}

	/**
	 * 播放书籍
	 * 
	 * @param hlBookLayout
	 */
	public void playBook() {
		if (book == null) {
			return;
		}
		// 播放背景音乐
		if (null != book.getBookInfo().backgroundMusicId
				&& book.getBookInfo().backgroundMusicId.length() > 0) {
			playBackgroundMusic();
		}

		if (book.getSections() != null) {
			section = book.getSections().get(0);
			if (null != section.pages) {
				if (section.getPages().size() != 0) {
					getPageHistory().clear();
					this.changePageById(section.getPages().get(0));
				}
			}
		}
	}

	public void playBackgroundMusic() {
		// 执行播放背景音乐的时候没有做背景音乐是否存在的校验
		if (StringUtils.isEmpty(book.getBookInfo().backgroundMusicId))
			return;
		if (null == backgroundMusic) {
			ComponentEntity componentEntity = new ComponentEntity();
			componentEntity
					.setLocalSourceId(book.getBookInfo().backgroundMusicId);
			componentEntity.autoLoop = true;
			componentEntity.isHideAtBegining = true;
			backgroundMusic = new AudioComponent(this.getHLActivity(),
					componentEntity);
			backgroundMusic.isBackGroundMusic = true;

			backgroundMusic.setBackgroundColor(Color.TRANSPARENT);
			backgroundMusic.load();
			backgroundMusic.setEntity(componentEntity);
			backgroundMusic.play();
			HLSetting.PlayBackGroundMusic = true;
		} else {
			backgroundMusic.play();
		}
	}

	/**
	 * 设置显示sub页的默认试图
	 */
	public void setSubDefaultView() {

		if (HLSetting.IsAD && null != adView) {
			adView.bringToFront();
		}
		if (this.homeButton != null)
			this.homeButton.bringToFront();
		if (this.galleyButton != null)
			galleyButton.bringToFront();
		try {
			if (viewPage.getEntity().isEnableNavigation()) {
				if (this.homeButton != null)
					homeButton.setVisibility(View.VISIBLE);
				if (this.galleyButton != null)
					galleyButton.setVisibility(View.VISIBLE);
			} else {
				if (this.homeButton != null)
					homeButton.setVisibility(View.GONE);
				if (this.galleyButton != null)
					galleyButton.setVisibility(View.GONE);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 设置背景的默认UI
	 */
	public void setDefaultView(int pageindex) {
		// 这个是时候设置gallery
		if (!BookSetting.IS_NO_NAVIGATION) {
			if (gallery == null)
				return;
			gallery.setSelection(pageindex);
		}
		try {
			if (viewPage == null)
				return;
			if (viewPage.getEntity() == null)
				return;
			if (viewPage.getEntity().isEnableNavigation()) {
				if (preButton != null) {
					if (pageindex == 0) {
						preButton.setVisibility(View.INVISIBLE);
					} else {
						preButton.setVisibility(View.VISIBLE);
					}
				}
				if (nextButton != null) {
					if (pageindex == this.section.pages.size() - 1) {
						nextButton.setVisibility(View.INVISIBLE);
					} else {
						nextButton.setVisibility(View.VISIBLE);
					}
				}
				if (this.homeButton != null)
					homeButton.setVisibility(View.VISIBLE);
				if (this.galleyButton != null)
					galleyButton.setVisibility(View.VISIBLE);
			} else {
				if (this.preButton != null)
					preButton.setVisibility(View.GONE);
				if (this.nextButton != null)
					nextButton.setVisibility(View.GONE);
				if (this.homeButton != null)
					homeButton.setVisibility(View.GONE);
				if (this.galleyButton != null)
					galleyButton.setVisibility(View.GONE);
			}
			setGlobalButtonVisibale(preButton);
			setGlobalButtonVisibale(nextButton);
			setGlobalButtonVisibale(homeButton);
			setGlobalButtonVisibale(galleyButton);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setGlobalButtonVisibale(ImageButton imgBtn) {
		try {
			ButtonEntity entity = (ButtonEntity) imgBtn.getTag();
			if (!entity.isVisible()) {
				imgBtn.setVisibility(View.GONE);
			}
		} catch (Exception e) {
		}
	}

	boolean isCommonKeep = true;
	public String nextPageID;
	public String prePageID;
	protected FrameLayout frameLayout;

	/**
	 * 翻页前的准备
	 */
	private void prepare4Switch() {
		int width = 0;
		int height = 0;
		width = BookSetting.BOOK_WIDTH;
		height = BookSetting.BOOK_HEIGHT;
		FrameLayout.LayoutParams flipViewLp = new FrameLayout.LayoutParams(
				width, height);
		// 当
		flipViewLp.gravity = Gravity.CENTER;
		flipView.setLayoutParams(flipViewLp);
		// TODO 这里load最新的书籍了
		PageEntity pageEntity = PageEntityController.getInstance()
				.getPageEntityByPageId(hlActivity, newPageID);
		isCommonKeep = pageEntity.beCoveredPageID
				.equals(viewPage.getEntity().beCoveredPageID);
		// 判断公共页是否要保持，如果不要保持，那就将公共页放到hlBookLayout里面参与翻页，注意要在翻页结束以后将公共页放回到common中

		Bitmap bl = getCurrentBookSnap();
		if (!isCommonKeep) {
			// 设抓取当前截图
			// TO TEST 不从viewpage获取截图了

			hlActivity.commonLayout.removeView(commonPage);
			if (commonPage != null) {
				commonPage.stop();
				commonPage.clean();
				commonPage = null;
			}
			// hlBookLayout.addView(commonPage);
		}
		this.flipView.setViewPage(hlBookLayout);
		// 翻页视图被显示
		this.flipView.setBitmap(bl);

		flipView.show();

		viewPage.stopVideo();
		viewPage.stop();
		// 内容视图被隐藏
		viewPage.clean();
		this.getHLBookLayout().removeView(viewPage);
		viewPage.getRootView().invalidate();
		// if(getBook().getBookInfo().bookNavType.equals("indesign_slider_view")){
		// hlActivity.getUPNav().dismiss();
		// }
		// 判断是否需要设置snapshot的图片
		if (pageEntity.isCashSnapshot()) {
			flipView.setPreLoad(false);
			resizeBmp = getSnapShotCashImage(pageEntity);
			flipView.setNewBitmap(resizeBmp);
		} else {
			flipView.setPreLoad(true);
			showPageOnly(newPageID);
		}
	}

	public Bitmap getCurrentBookSnap() {
		// hlBookLayout.setDrawingCacheEnabled(true);
		// hlBookLayout.buildDrawingCache();
		// Bitmap b1 = Bitmap.createBitmap(hlBookLayout.getDrawingCache());
		// hlBookLayout.setDrawingCacheEnabled(false);
		// return b1;
		return getSnapshot(hlBookLayout);
	}

	/**
	 * 根据pageentity获得显示用的截图作为 翻页做预览背景使用
	 * 
	 * @param pageEntity
	 * @return
	 */
	public Bitmap getSnapShotCashImage(PageEntity pageEntity) {
		if (pageEntity == null)
			return null;
		String snapShotID = pageEntity.getSnapShotID();
		return BitmapUtils.getBitMap(snapShotID, hlActivity,
				BookSetting.SCREEN_WIDTH, BookSetting.SCREEN_HEIGHT);
	}

	public Bitmap getCurrentSnapShotCashImage() {
		PageEntity pageEntity = getPageEntityByID(mainPageID);
		return getSnapShotCashImage(pageEntity);
	}

	public Bitmap getSmallSnapShotCashImage(PageEntity pageEntity) {
		String snapShotID = pageEntity.getSnapShotID();
		Bitmap result = BitmapUtils.getBitMap(snapShotID, hlActivity, 100, 80);
		return result;
	}

	/**
	 * 默认翻页
	 */
	public void pageViewPost() {
		if (BookSetting.IS_AUTOPAGE) {
			autoPageViewCountDown = new AutoPageCountDown(2000, 1000);
			autoPageViewCountDown.start();
		}
	}

	/**
	 * tag
	 * 
	 * @param tag
	 * 
	 */
	public void flipPage(int tag) {
		// BookState.getInstance().setPlayViewPage();
		if (tag == -1) {
			prePage();
		} else {
			nextPage();
		}
	}

	public void prePage() {

		int currentPageIndex = this.getSectionPagePosition(this.mainPageID);
		if (currentPageIndex == 0) {
			// BookState.getInstance().restoreFlipState();
			return;
		}
		// 翻页之前，将所有的状态都初始化，隐藏gallery
		if (!viewPage.getEntity().isEnableNavigation()) {
			return;
		}
		if (!BookState.getInstance().setFlipState()) {
			return;
		}
		if (gallery != null) {
			gallery.hideGalleryInfor();
		}
		int newPageIndex = currentPageIndex - 1;
		switchPage(currentPageIndex, newPageIndex);
	}

	public void switchPage(int currentPageIndex, int newPageIndex) {
		if (null == viewPage || viewPage.getEntity() == null) {
			return;
		}
		newPageID = getBook().getSections().get(currendsectionindex).pages
				.get(newPageIndex);
		prepare4Switch();
		flipView.play(currentPageIndex, newPageIndex, new MyActionOnEnd(
				newPageIndex));
		if (commonPage != null)
			commonPage.bringToFront();

		setDefaultView(newPageIndex);
	}

	public void nextPage() {
		int currentPageIndex = getSectionPagePosition(this.mainPageID);
		if (currentPageIndex == this.getBook().getSections()
				.get(currendsectionindex).pages.size() - 1) {
			return;
		}
		// 翻页之前，将所有的状态都初始化，隐藏gallery
		if (this.viewPage.getEntity().isEnableNavigation() == false) {
			return;
		}
		if (!BookState.getInstance().setFlipState()) {
			return;
		}
		if (gallery != null) {
			gallery.hideGalleryInfor();
		}
		int newPageIndex = currentPageIndex + 1;
		switchPage(currentPageIndex, newPageIndex);
	}

	/**
	 * 根据指定页的id获取下一页的id
	 * 
	 * @param pageID
	 * @return
	 */
	public String getNextPageId() {
		return nextPageID;
	}

	/**
	 * 根据指定页的id获取上一页的id
	 * 
	 * @param pageID
	 * @return
	 */
	public String getPrePageId() {
		return prePageID;
	}

	public LinearLayout getLoadLayout() {
		LinearLayout lay = new LinearLayout(hlActivity);
		lay.setGravity(Gravity.CENTER);
		lay.setOrientation(LinearLayout.VERTICAL);
		ProgressBar pb = new ProgressBar(hlActivity);
		pb.setMax(100);

		TextView loadText = new TextView(hlActivity);
		loadText.setTextColor(Color.BLACK);
		lay.addView(loadText);
		LinearLayout.LayoutParams pblp = new LinearLayout.LayoutParams(100, 100);
		lay.addView(pb, pblp);
		return lay;
	}

	/**
	 * 转到指定页面
	 * 
	 * @param pageId
	 */
	public void playPageById(String pageId) {
		// hlActivity.getUPNav().dismiss();
		if (isPageExist(pageId) == false) {
			return;
		}
		if (!BookState.getInstance().setFlipState()) {
			return;
		}
		if (gallery != null && gallery.getVisibility() == View.VISIBLE) {
			gallery.setVisibility(View.GONE);
			gallery.hideGalleryInfor();
		}
		if (mainPageID != null && pageId != null && mainPageID.equals(pageId)) {
			removeNotShowViewPage();
			startPlay();
			return;
		}
		storePageHistory(pageId);
		newPageID = pageId;
		int currentPageIndex = 0;
		SectionEntity currentSection = section;
		if (currentSection.isShelves) {
			// 重新设置一下书籍路径
			BookSetting.BOOK_PATH = currentSection.bookPath;
			HLSetting.IsResourceSD = currentSection.isResourceSD;
			BookSetting.IS_SHELVES_COMPONENT = currentSection.isShelves;
			BookDecoder.getInstance().initBookItemList();
		}

		if (viewPage.getEntity() != null)
			currentPageIndex = getSectionPagePosition(viewPage.getEntity()
					.getID());

		int newPageIndex = getSectionPagePosition(newPageID);
		if (currentSection.ID.equals(section.ID)) {// sectionå†…éƒ¨è·³è½¬
			switchPage(currentPageIndex, newPageIndex);
		} else {
			// 如果不在一个section中，就根据section的index来判断是前翻还是后翻
			int newSecIndex = book.getSections().indexOf(currentSection);
			int oldSecIndex = book.getSections().indexOf(section);
			if (newSecIndex < oldSecIndex) {
				switchPage(-1, newPageIndex);
			} else {
				switchPage(999, newPageIndex);// 如果不在一个section中，那么就应该认为是从0开始播放
			}
		}
	}

	private void storePageHistory(String pageId) {
		if (!getPageHistory().isEmpty()) {
			if (!getPageHistory().get(getPageHistory().size() - 1).equals(
					pageId)) {
				getPageHistory().add(pageId);
			}
		} else {
			getPageHistory().add(pageId);
		}
	}

	/**
	 * 根据指定的切换效果切换页面
	 * 
	 * @param type
	 *            切换类型
	 * @param direction
	 *            页面切换方向，如果没有提供null或任意字符串
	 * @param duration
	 *            切换用时
	 * @param delay
	 *            切换延迟
	 * @param pageId
	 *            切换页ID
	 */
	public void changePageWithEffect(final String type, final String direction,
			final long duration, final long delay, final String pageId) {

		PageEntity page = PageEntityController.getInstance()
				.getPageEntityByPageId(hlActivity, pageId);
		if (page == null) {
			Log.d("wdy", "page " + pageId + " is not exist，please check");
			Log.e("wdy", "page " + pageId + " is not exist，please check");
			return;
		}
		this.getHLBookLayout().post(new Runnable() {
			@Override
			public void run() {
				if (gallery != null && gallery.getVisibility() == View.VISIBLE) {
					gallery.setVisibility(View.GONE);
					gallery.hideGalleryInfor();
				}
				if (nextButton != null) {
					nextButton.setClickable(false);
				}
				if (preButton != null) {
					preButton.setClickable(false);
				}
				ImageView curShowImageView = new ImageView(hlActivity);
				ImageView nextShowImageView = new ImageView(hlActivity);
				Bitmap curBitmap1 = getSnapShotCashImage(viewPage.getEntity());
				// Bitmap curBitmap1 = viewPage.getCurrentScreen();
				viewPage.stop();
				getHLBookLayout().removeView(viewPage);
				viewPage.clean();
				Bitmap nextBitmap = getSnapShotCashImage(PageEntityController
						.getInstance()
						.getPageEntityByPageId(hlActivity, pageId));
				curShowImageView.setImageBitmap(curBitmap1);
				curShowImageView.setScaleType(ScaleType.FIT_XY);
				nextShowImageView.setImageBitmap(nextBitmap);
				nextShowImageView.setScaleType(ScaleType.FIT_XY);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						viewPage.getPageWidth(), viewPage.getPageHeight());
				frameLayout = new FrameLayout(hlActivity);
				frameLayout.addView(curShowImageView, layoutParams);
				frameLayout.addView(nextShowImageView, layoutParams);
				getHLBookLayout().addView(frameLayout,
						viewPage.getCurrentLayoutParams());
				AnimationSet curImageAnim = null;
				AnimationSet nextImageAnim = null;
				showPage(pageId);
				curImageAnim = new AnimationSet(true);
				nextImageAnim = new AnimationSet(true);
				if (type.equals("transitionFade")) {
					curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
					nextImageAnim.addAnimation(new AlphaAnimation(0, 1));
					curImageAnim.setZAdjustment(Animation.ZORDER_TOP);
					nextImageAnim.setZAdjustment(Animation.ZORDER_BOTTOM);
				} else if (type.equals("transitionMoveIn")) {
					curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
					nextImageAnim.addAnimation(new AlphaAnimation(0, 1));
					TranslateAnimation transAni = null;
					if (direction.equals("left")) {
						transAni = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 1,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
					} else if (direction.equals("right")) {
						transAni = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, -1,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
					} else if (direction.equals("up")) {
						transAni = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 1,
								Animation.RELATIVE_TO_SELF, 0);
					} else if (direction.equals("down")) {
						transAni = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, -1,
								Animation.RELATIVE_TO_SELF, 0);
					}
					nextImageAnim.addAnimation(transAni);
				} else if (type.equals("transitionReveal")) {
					curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0f));
					nextImageAnim.addAnimation(new AlphaAnimation(0, 1));
					TranslateAnimation transAni = null;
					if (direction.equals("left")) {
						transAni = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, -1,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
					} else if (direction.equals("right")) {
						transAni = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 1,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
					} else if (direction.equals("up")) {
						transAni = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, -1);
					} else if (direction.equals("down")) {
						transAni = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 1);
					}
					curImageAnim.addAnimation(transAni);
					curImageAnim.setZAdjustment(Animation.ZORDER_TOP);
					nextImageAnim.setZAdjustment(Animation.ZORDER_BOTTOM);
				} else if (type.equals("transitionPush")) {
					curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
					nextImageAnim.addAnimation(new AlphaAnimation(0, 1));
					TranslateAnimation transAni4curImage = null;
					TranslateAnimation transAni4nextImage = null;
					if (direction.equals("left")) {
						transAni4curImage = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, -1,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
						transAni4nextImage = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 1,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
					} else if (direction.equals("right")) {
						transAni4curImage = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 1,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
						transAni4nextImage = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, -1,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
					} else if (direction.equals("up")) {
						transAni4curImage = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, -1);
						transAni4nextImage = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 1,
								Animation.RELATIVE_TO_SELF, 0);
					} else if (direction.equals("down")) {
						transAni4curImage = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 1);
						transAni4nextImage = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, -1,
								Animation.RELATIVE_TO_SELF, 0);
					}
					curImageAnim.addAnimation(transAni4curImage);
					nextImageAnim.addAnimation(transAni4nextImage);
				} else if (type.equals("cubeEffect")) {
					if (direction.equals("left") || direction.equals("up")) {
						curImageAnim.addAnimation(new MyAnimation4CubeEffect(0,
								-90, direction));
						nextImageAnim.addAnimation(new MyAnimation4CubeEffect(
								90, 0, direction));
					} else if (direction.equals("right")
							|| direction.equals("down")) {
						curImageAnim.addAnimation(new MyAnimation4CubeEffect(0,
								90, direction));
						nextImageAnim.addAnimation(new MyAnimation4CubeEffect(
								-90, 0, direction));
					}
				} else if (type.equals("flipEffect")) {
					nextImageAnim.addAnimation(new MyAnimation4FlipEffect(0,
							180, curShowImageView, nextBitmap, direction));
					frameLayout.removeView(nextShowImageView);
				} else {// 未添加的切换效果先使用透明度切换效果
					curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
					nextImageAnim.addAnimation(new AlphaAnimation(0, 1));
					curImageAnim.setZAdjustment(Animation.ZORDER_TOP);
					nextImageAnim.setZAdjustment(Animation.ZORDER_BOTTOM);
				}

				curImageAnim.setDuration(duration);
				nextImageAnim.setDuration(duration);
				nextImageAnim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {

					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						handler.sendEmptyMessage(0);
					}
				});
				frameLayout.bringToFront();
				frameLayout.setBackgroundColor(Color.WHITE);
				curShowImageView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
				if (type.equals("flipEffect")) {
					curShowImageView.startAnimation(nextImageAnim);
				} else {
					curShowImageView.startAnimation(curImageAnim);
					nextShowImageView.startAnimation(nextImageAnim);
				}
			}
		});

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (nextButton != null) {
					nextButton.setClickable(true);
				}
				if (preButton != null) {
					preButton.setClickable(true);
				}
				getHLBookLayout().removeView(frameLayout);
				hlActivity.refreshSnapshots();
				setOrien();
				removeNotShowViewPage();
				startPlay();
			}
		};
	};

	/**
	 * 
	 * @param pageId
	 */

	public void changePageById(final String pageId) {
		PageEntity page = PageEntityController.getInstance()
				.getPageEntityByPageId(hlActivity, pageId);
		if (page == null) {
			Log.d("wdy", "page " + pageId + " is not exist，please check");
			Log.e("wdy", "page " + pageId + " is not exist，please check");
			return;
		}
		if (!BookState.getInstance().setFlipState()) {
			return;
		}
		if (isPageExist(pageId) == false) {
			BookState.getInstance().restoreFlipState();
			return;
		}
		if (gallery != null && gallery.getVisibility() == View.VISIBLE) {
			gallery.setVisibility(View.GONE);
			gallery.hideGalleryInfor();
		}
		this.getHLBookLayout().post(new Runnable() {
			@Override
			public void run() {
				BookHelper.setupScreen(hlActivity);
				getHLBookLayout().removeView(viewPage);
				viewPage.stop();
				viewPage.clean();
				showPage(pageId);
				setOrien();
				removeNotShowViewPage();
				startPlay();
				hlActivity.relayoutGlobalButton();
				hlActivity.progressHandler.sendEmptyMessage(1);
				hlActivity.refreshSnapshots();
			}
		});
	}

	// public void changePageById(final String pageId,final int playDelay) {
	// if (!BookState.getInstance().setFlipState()) {
	// return;
	// }
	// if (isPageExist(pageId) == false) {
	// BookState.getInstance().restoreFlipState();
	// return;
	// }
	// if(gallery!=null&&gallery.getVisibility()==View.VISIBLE){
	// gallery.setVisibility(View.GONE);
	// gallery.hideGalleryInfor();
	// }
	// this.getHLBookLayout().post(new Runnable() {
	// @Override
	// public void run() {
	// BookHelper.setupScreen(hlActivity);
	// getHLBookLayout().removeView(viewPage);
	// viewPage.stop();
	// viewPage.clean();
	// showPage(pageId);
	// setOrien();
	// removeNotShowViewPage();
	// Timer timer=new Timer();
	// timer.schedule(new TimerTask() {
	//
	// @Override
	// public void run() {
	// startPlay();
	// }
	// }, playDelay);
	// hlActivity.relayoutGlobalButton();
	// hlActivity.progressHandler.sendEmptyMessage(1);
	// hlActivity.refreshSnapshots();
	// }
	// });
	// }

	public void showPageOnly(String pageID) {
		long startTime = System.currentTimeMillis();
		loadPage(pageID);
		long endTime = System.currentTimeMillis();
		Log.d("ww", "load use time is :" + (endTime - startTime));
		getHLBookLayout().addView(viewPage, viewPage.getCurrentLayoutParams());
		subPageList = viewPage.getEntity().getNavePageIds();
		lastPageID = mainPageID;
		storePageHistory(pageID);
		mainPageID = viewPage.getEntity().getID();
		mainViewPage = viewPage;
		mainViewPage.bringToFront();
		hlActivity.refreshSnapshots();
	}

	// /**
	// * 显示加载完的页面，在页间滑动切换后设置显示页面并移除非显示页面等
	// * @param viewPage
	// */
	// public void showPageWithLoadedPage(ViewPage viewPage){
	// subPageList = viewPage.getEntity().getNavePageIds();
	// String temp=mainPageID;
	// mainPageID = viewPage.getEntity().getID();
	// if(!temp.equals(mainPageID)){
	// lastPageID = temp;
	// storePageHistory(viewPage.getEntity().getID());
	// }
	// mainViewPage = viewPage;
	// mainViewPage.bringToFront();
	// this.viewPage=mainViewPage;
	// if(!StringUtils.isEmpty(mainViewPage.getEntity().beCoveredPageID)){
	// commonPageID=mainViewPage.getEntity().beCoveredPageID;
	// if(prevCommonPage!=null){
	// if(prevCommonPage.getEntity()!=null){
	// if(prevCommonPage.getEntity().getID().equals(commonPageID)){
	// commonPage=prevCommonPage;
	// prevCommonPage.stop();
	// prevCommonPage.clean();
	// hlActivity.commonLayout.removeView(prevCommonPage);
	// prevCommonPage=null;
	// }
	// }
	// }
	// if(nextCommonPage!=null){
	// if(nextCommonPage.getEntity()!=null){
	// if(nextCommonPage.getEntity().getID().equals(commonPageID)){
	// commonPage=nextCommonPage;
	// nextCommonPage.stop();
	// nextCommonPage.clean();
	// hlActivity.commonLayout.removeView(nextCommonPage);
	// nextCommonPage=null;
	// }
	// }
	// }
	// }else{
	// if(commonPage!=null){
	// commonPageID="nothing";
	// commonPage.stop();
	// commonPage.clean();
	// hlActivity.commonLayout.removeView(commonPage);
	// commonPage=null;
	// }
	// if(prevCommonPage!=null){
	// prevCommonPage.stop();
	// prevCommonPage.clean();
	// hlActivity.commonLayout.removeView(prevCommonPage);
	// prevCommonPage=null;
	// }
	// if(nextCommonPage!=null){
	// nextCommonPage.stop();
	// nextCommonPage.clean();
	// hlActivity.commonLayout.removeView(nextCommonPage);
	// nextCommonPage=null;
	// }
	// }
	// setOrien();
	// removeNotShowViewPage();
	// getSectionPagePosition(mainPageID);
	// if(hlActivity.getMarkView4NavMenu()!=null){
	// hlActivity.getMarkView4NavMenu().refresh();
	// }
	// hlActivity.refreshSnapshots();
	// }
	//
	public void showPageWithLoadedPage(ViewPage viewPage, String endPage) {
		subPageList = viewPage.getEntity().getNavePageIds();
		String temp = mainPageID;
		mainPageID = viewPage.getEntity().getID();
		if (mainPageID != null && temp != null && !temp.equals(mainPageID)) {
			lastPageID = temp;
			storePageHistory(viewPage.getEntity().getID());
		}
		mainViewPage = viewPage;
		mainViewPage.bringToFront();
		this.viewPage = mainViewPage;
		if (!StringUtils.isEmpty(mainViewPage.getEntity().beCoveredPageID)) {
			commonPageID = mainViewPage.getEntity().beCoveredPageID;
			if (prevCommonPage != null) {
				if (prevCommonPage.getEntity() != null) {
					if (prevCommonPage.getEntity().getID().equals(commonPageID)) {
						if (endPage.equals("prev")) {
							nextCommonPage = commonPage;
							commonPage = prevCommonPage;
							prevCommonPage = null;
						}
						//
						Log.d("zhaoq",
								"commonLayout.removeView(prevCommonPage)");
					}
				}
			}
			if (nextCommonPage != null) {
				if (nextCommonPage.getEntity() != null) {
					if (nextCommonPage.getEntity().getID().equals(commonPageID)) {
						if (endPage.equals("next")) {
							// commonPage=nextCommonPage;
							prevCommonPage = commonPage;
							commonPage = nextCommonPage;
							nextCommonPage = null;
							// commonPage.isCommonPage=true;
						}
						// nextCommonPage.stop();
						// nextCommonPage.clean();
						// hlActivity.commonLayout.removeView(nextCommonPage);
						Log.d("zhaoq",
								"commonLayout.removeView(prevCommonPage)");
						// nextCommonPage=null;
					}
				}
			}
		} else {
			if (commonPage != null) {
				commonPageID = "";
				commonPage.stop();
				commonPage.clean();
				hlActivity.commonLayout.removeView(commonPage);
				commonPage = null;
			}
			if (prevCommonPage != null) {
				prevCommonPage.stop();
				prevCommonPage.clean();
				hlActivity.commonLayout.removeView(prevCommonPage);
				prevCommonPage = null;
			}
			if (nextCommonPage != null) {
				nextCommonPage.stop();
				nextCommonPage.clean();
				hlActivity.commonLayout.removeView(nextCommonPage);
				nextCommonPage = null;
			}
		}
		setOrien();
		removeNotShowViewPage();
		getSectionPagePosition(mainPageID);
		if (hlActivity.getMarkView4NavMenu() != null) {
			hlActivity.getMarkView4NavMenu().refresh();
		}
		hlActivity.refreshSnapshots();
	}

	// 移除非当前显示页面
	public void removeNotShowViewPage() {
		// Log.d("wdy", "removeNotShowViewPage");
		for (int i = 0; i < getHLBookLayout().getChildCount(); i++) {
			View page = getHLBookLayout().getChildAt(i);
			if (page instanceof ViewPage && mainViewPage != page) {
				if (((ViewPage) page).isCommonPage) {
					if (commonPage == null
							|| !((ViewPage) page).getEntity().getID()
									.equals(commonPageID)) {
						((ViewPage) page).stopVideo();
						((ViewPage) page).stop();
						((ViewPage) page).clean();
						getHLBookLayout().removeView(page);
						i--;
					}
				} else {
					((ViewPage) page).stopVideo();
					((ViewPage) page).stop();
					((ViewPage) page).clean();
					getHLBookLayout().removeView(page);
					page = null;
					i--;
				}
			}
		}
		for (int i = 0; i < hlActivity.commonLayout.getChildCount(); i++) {
			View page = hlActivity.commonLayout.getChildAt(i);
			if (page instanceof ViewPage) {
				try {
					if (commonPage == null
							|| !((ViewPage) page).getEntity().getID()
							.equals(commonPageID)) {
						((ViewPage) page).stopVideo();
						((ViewPage) page).stop();
						((ViewPage) page).clean();
						hlActivity.commonLayout.removeView(page);
						page = null;
						i--;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		getHLBookLayout().requestLayout();
		hlActivity.commonLayout.requestLayout();
	}

	public void showPage(String pageID) {
		storePageHistory(pageID);
		loadPage(pageID);
		int pageIndex = getSectionPagePosition(pageID);
		getHLBookLayout().addView(viewPage, viewPage.getCurrentLayoutParams());
		subPageList = viewPage.getEntity().getNavePageIds();
		lastPageID = mainPageID;
		mainPageID = viewPage.getEntity().getID();
		mainViewPage = viewPage;
		mainViewPage.bringToFront();

		getHLBookLayout().postInvalidate();
		setDefaultView(pageIndex);
		hlActivity.refreshSnapshots();
	}

	public void goHomePage() {
		String pageID = "";
		String homePageID = book.getBookInfo().homePageID;
		if (StringUtils.isEmpty(homePageID)) {
			pageID = section.getPages().get(0);
		} else {
			pageID = book.getBookInfo().homePageID;
		}

		if (BookSetting.IS_HOR_VER == true) {

			PageEntity entity = getPageEntityByID(pageID);
			if (BookSetting.IS_HOR
					&& entity.getType().equals(ViewPage.PAGE_TYPE_VER)
					&& entity.getLinkPageID().equals("")) {
				hlActivity
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				BookHelper.setupScreen(hlActivity);
				hlActivity.setFlipView();
				BookSetting.IS_HOR = false;
				BookState.getInstance().isChangeTo = true;

			} else if (!BookSetting.IS_HOR
					&& (entity.getType().equals(ViewPage.PAGE_TYPE_HOR) || entity
							.getType().equals(ViewPage.PAGE_TYPE_NONE))) {
				if (entity.getLinkPageID().equals("")) {
					hlActivity
							.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					BookHelper.setupScreen(hlActivity);
					hlActivity.setFlipView();
					BookSetting.IS_HOR = true;
					BookState.getInstance().isChangeTo = true;
				} else {
					pageID = entity.getLinkPageID();
				}
			}
		}
		this.playPageById(pageID);

		// 将计时器和计数器重置
		count = -1;

	}

	// 加载并移动到指定ID的页面
	public void loadAndMoveTo(String pageID) {
		if (pageID == null) {
			return;
		}
		if (pageID.equals(mainPageID)) {
			return;
		}
		ViewPage newPage = new ViewPage(hlActivity, null, null);
		loadPage(pageID, newPage);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.width = BookSetting.BOOK_WIDTH;
		lp.height = (int) ScreenUtils.getHorScreenValue(newPage.getEntity()
				.getHeight());
		newPage.setLayoutParams(lp);
		getHLBookLayout().addView(newPage);
		int pageIndex = getSectionPagePosition(pageID);
		int currentIndex = getSectionPagePosition(mainPageID);
		int a = pageIndex > currentIndex ? 1 : -1;
		newPage.setX(mainViewPage.getX() + a * BookSetting.BOOK_WIDTH);

		mainViewPage.setAniEndPage(newPage);
		mainViewPage.doMoveAni(-a * BookSetting.BOOK_WIDTH);
	}

	/**
	 * 开始播放页面，翻页以后的效果也会调用这个，所以翻页的时候不需要考虑播放的事宜
	 */
	public void startPlay() {
		try {
			doFlipSubPage = false;
			if (!StringUtils.isEmpty(commonPageID)) {
				hlActivity.commonLayout.setVisibility(View.VISIBLE);
			}
			// viewPage.invalidate();//
			// å¿…é¡»invalidateï¼Œå�¦åˆ™ç¿»é¡µå�Žæ— æ³•æ’­æ”¾åŠ¨ç”»
			viewPage.startPlay();
			if (commonPage != null) {
				if (commonPage.getTag(R.id.tag_firsttimeplay_commonpage) == null
						|| !(Boolean) commonPage
								.getTag(R.id.tag_firsttimeplay_commonpage)) {
					commonPage.startPlay();
					commonPage.setTag(R.id.tag_firsttimeplay_commonpage, true);
				}
			}
			viewPage.playVideo();
			BookState.getInstance().setPlayViewPage();
			if (BookSetting.IS_AUTOPAGE && viewPage.autoPlayCount == 0) {// å¦‚æžœæ˜¯è‡ªåŠ¨ç¿»é¡µä¸”æ²¡æœ‰è‡ªåŠ¨æ’­æ”¾çš„åˆ™ç«‹å�³ç¿»é¡µ
				this.pageViewPost();
			}
		} catch (Exception ex) {
			Log.e("hl", "start play error", ex);
		}
	}

	/**
	 * 获取并设置显示页面的前一页和后一页 TODO addby zhaoq 添加公共页面
	 */
	public void getAndSetPreAndNextViewPage() {
		if (mainViewPage == null)
			return;
		nextPageID = null;
		prePageID = null;
		int currentRootPageIndex = getSectionPagePosition(mainViewPage
				.getEntity().getID());
		int currentRootsectionindex = currendsectionindex;
		prevCommonPageEmpty = false;
		nextCommonPageEmpty = false;
		if (currentRootPageIndex != this.getBook().getSections()
				.get(currentRootsectionindex).pages.size() - 1) {
			nextPageID = getBook().getSections().get(currentRootsectionindex).pages
					.get(currentRootPageIndex + 1);
			nextViewPage = new ViewPage(hlActivity, null, null);
			loadPage(nextPageID, nextViewPage);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lp.width = BookSetting.BOOK_WIDTH;
			lp.height = (int) ScreenUtils.getHorScreenValue(nextViewPage
					.getEntity().getHeight());
			nextViewPage.setLayoutParams(lp);
			getHLBookLayout().addView(nextViewPage);
			nextViewPage.setX(mainViewPage.getX() + BookSetting.BOOK_WIDTH);
			// 如果当前的页的公共页与next页相同，就不需要了
			if (!nextViewPage.getEntity().beCoveredPageID.equals(commonPageID)) {
				if (!StringUtils
						.isEmpty(nextViewPage.getEntity().beCoveredPageID)) {
					nextCommonPage = new ViewPage(hlActivity, null, null);
					nextCommonPage.isCommonPage = true;
					loadPage(nextViewPage.getEntity().beCoveredPageID,
							nextCommonPage);
					nextCommonPage.setX(mainViewPage.getX()
							+ BookSetting.BOOK_WIDTH);
					hlActivity.commonLayout.addView(nextCommonPage);
				} else {
					nextCommonPageEmpty = true;
					if (nextCommonPage != null) {
						nextCommonPage.clean();
						hlActivity.commonLayout.removeView(nextCommonPage);
						nextCommonPage = null;
					}
				}
			} else {
				if (nextCommonPage != null) {
					nextCommonPage.clean();
					hlActivity.commonLayout.removeView(nextCommonPage);
					nextCommonPage = null;
				}
			}
		} else {// 如果是最后一页那么也需要清空
				// clear next

			nextCommonPageEmpty = true;
			if (nextCommonPage != null) {
				nextCommonPage.clean();
				hlActivity.commonLayout.removeView(nextCommonPage);
				nextCommonPage = null;
			}
		}
		if (currentRootPageIndex > 0) {
			prePageID = getBook().getSections().get(currentRootsectionindex).pages
					.get(currentRootPageIndex - 1);
			preViewPage = new ViewPage(hlActivity, null, null);
			loadPage(prePageID, preViewPage);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			lp.width = BookSetting.BOOK_WIDTH;
			lp.height = (int) ScreenUtils.getHorScreenValue(preViewPage
					.getEntity().getHeight());
			preViewPage.setLayoutParams(lp);
			getHLBookLayout().addView(preViewPage);
			preViewPage.setX(mainViewPage.getX() - BookSetting.BOOK_WIDTH);
			// 如果当前的页的公共页与next页相同，就不需要了
			if (!preViewPage.getEntity().beCoveredPageID.equals(commonPageID)) {
				if (!StringUtils
						.isEmpty(preViewPage.getEntity().beCoveredPageID)) {
					prevCommonPage = new ViewPage(hlActivity, null, null);
					prevCommonPage.isCommonPage = true;
					loadPage(preViewPage.getEntity().beCoveredPageID,
							prevCommonPage);
					prevCommonPage.setX(mainViewPage.getX()
							- BookSetting.BOOK_WIDTH);
					hlActivity.commonLayout.addView(prevCommonPage);
				} else {
					prevCommonPageEmpty = true;
					if (prevCommonPage != null) {
						prevCommonPage.clean();
						hlActivity.commonLayout.removeView(prevCommonPage);
						prevCommonPage = null;
					}
				}
			} else {
				if (prevCommonPage != null) {
					prevCommonPage.clean();
					hlActivity.commonLayout.removeView(prevCommonPage);
					prevCommonPage = null;
				}
			}
		} else {// 如果是第一页那么也需要清空
			if (prevCommonPage != null) {
				prevCommonPageEmpty = true;
				prevCommonPage.clean();
				hlActivity.commonLayout.removeView(prevCommonPage);
				prevCommonPage = null;
			}
		}

	}

	public void loadPage(String pageId) {
		PageEntity pageEntity = null;
		viewPage = new ViewPage(viewPage.getContext(), null, null);
		pageEntity = PageEntityController.getInstance().getPageEntityByPageId(
				this.viewPage.getContext(), pageId);
		String linkPageID = pageEntity.getLinkPageID();

		if (BookSetting.IS_HOR_VER) {
			BookHelper.setupScreen(hlActivity);
			if (!BookSetting.IS_HOR
					&& (pageEntity.getType().equals(ViewPage.PAGE_TYPE_HOR) || pageEntity
							.getType().equals(ViewPage.PAGE_TYPE_NONE))
					&& !StringUtils.isEmpty(linkPageID)) {
				getSectionPagePosition(linkPageID);
				pageEntity = PageEntityController.getInstance()
						.getPageEntityByPageId(hlActivity, linkPageID);

			}
			if (BookSetting.IS_HOR
					&& pageEntity.getType().equals(ViewPage.PAGE_TYPE_VER)
					&& !StringUtils.isEmpty(linkPageID)) {
				getSectionPagePosition(linkPageID);
				pageEntity = PageEntityController.getInstance()
						.getPageEntityByPageId(hlActivity, linkPageID);
			}
		}
		if (StringUtils.isEmpty(pageEntity.beCoveredPageID)) {
			Log.d("zhaoq", "common page is hiden");
			hlActivity.commonLayout.removeView(commonPage);
			hlBookLayout.removeView(commonPage);
			if (commonPage != null) {
				commonPage.stop();
				commonPage.clean();
				commonPage = null;
			}
			commonPageID = "nothing";
			hlActivity.commonLayout.setVisibility(View.GONE);
		} else if (commonPage == null
				|| !pageEntity.beCoveredPageID.equals(commonPageID)) {
			commonPage = new ViewPage(hlActivity, null, null);
			commonPage.isCommonPage = true;
			loadPage(pageEntity.beCoveredPageID, commonPage);
			commonPageID = pageEntity.beCoveredPageID;
			hlActivity.commonLayout.addView(commonPage, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			hlActivity.commonLayout.setVisibility(View.GONE);
			if (commonPage.getEntity() == null) {
				hlActivity.commonLayout.removeView(commonPage);
				commonPage = null;
				commonPageID = "";
			}
		}
		viewPage.load(pageEntity);
	}

	// private void loadCommonPage(PageEntity pageEntity) {
	// //TODO 这里加载公共页
	// if(commonPage == null){
	// Log.d("zhaoq","common page is created");
	// commonPage = new ViewPage(hlActivity, null, null);
	// commonPage.isCommonPage = true;
	// hlActivity.commonLayout.addView(commonPage,new
	// LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	// // hlActivity.commonLayout.setVisibility(View.GONE);
	// }
	// if(StringUtils.isEmpty(pageEntity.beCoveredPageID)){
	// Log.d("zhaoq","common page is hiden");
	// if(commonPage!=null){
	// commonPage.clean();
	// }
	// commonPageID = "";
	//
	// hlActivity.commonLayout.setVisibility(View.GONE);
	// }else{
	//
	// if(!commonPageID.equals(pageEntity.beCoveredPageID)){
	// commonPageID = pageEntity.beCoveredPageID;
	// Log.d("zhaoq","common page is loaded");
	// PageEntity commonPageEntity = PageEntityController.getInstance()
	// .getPageEntityByPageId(hlActivity, pageEntity.beCoveredPageID);
	// if(commonPageEntity==null){
	// return;
	// }
	// commonPage.clean();
	// commonPage.load(commonPageEntity);
	// hlActivity.commonLayout.setVisibility(View.VISIBLE);
	// }
	// }
	// }
	// 用于页间滑动的多页面加载
	public void loadPage(String pageId, ViewPage viewPage) {
		if (StringUtils.isEmpty(pageId))
			return;

		PageEntity pageEntity = null;
		pageEntity = PageEntityController.getInstance().getPageEntityByPageId(
				viewPage.getContext(), pageId);
		if (pageEntity == null)
			return;
		String linkPageID = pageEntity.getLinkPageID();

		if (BookSetting.IS_HOR_VER) {
			BookHelper.setupScreen(hlActivity);
			if (!BookSetting.IS_HOR
					&& (pageEntity.getType().equals(ViewPage.PAGE_TYPE_HOR) || pageEntity
							.getType().equals(ViewPage.PAGE_TYPE_NONE))
					&& !StringUtils.isEmpty(linkPageID)) {
				getSectionPagePosition(linkPageID);
				pageEntity = PageEntityController.getInstance()
						.getPageEntityByPageId(hlActivity, linkPageID);
			}
			if (BookSetting.IS_HOR
					&& pageEntity.getType().equals(ViewPage.PAGE_TYPE_VER)
					&& !StringUtils.isEmpty(linkPageID)) {
				getSectionPagePosition(linkPageID);
				pageEntity = PageEntityController.getInstance()
						.getPageEntityByPageId(hlActivity, linkPageID);
			}
		}
		viewPage.load(pageEntity);
	}

	public PageEntity getPageEntityByID(String pageID) {
		PageEntity pageEntity = null;
		pageEntity = BookDecoder.getInstance().decodePageEntity(
				this.getHLActivity(), pageID);
		return pageEntity;
	}

	private ArrayList<String> subPageList = null;
	public String mainPageID;
	public boolean isPlayingChangePageAni;
	private ArrayList<String> pageHistory;
	public boolean doFlipSubPage;

	public void flipSubPage(int tag) {
		if (null == subPageList || subPageList.size() <= 0) {
			return;
		}
		if (tag < 0) {
			flipSubPageUp();
		} else {
			flipSubPageDown();
		}
		setSubDefaultView();
	}

	private void flipSubPageUp() {
		if (this.mainPageID.equals(this.viewPage.getEntity().getID())) {
			return;
		}
		int currentPageIndex = this.subPageList.indexOf(this.viewPage
				.getEntity().getID());
		String pageID = "";
		if (currentPageIndex == 0) {
			pageID = mainPageID;
		} else {
			pageID = this.subPageList.get(currentPageIndex - 1);
		}
		if (!BookState.getInstance().setFlipState()) {
			return;
		}
		pageSubViewPrepare(-1);

		this.loadPage(pageID);

		this.getHLBookLayout().addView(viewPage,
				viewPage.getCurrentLayoutParams());
		playSubPageFlipAnimation(1, 0);
		doFlipSubPage = true;
	}

	private void flipSubPageDown() {
		int currentPageIndex = this.subPageList.indexOf(this.viewPage
				.getEntity().getID());
		if (currentPageIndex == this.subPageList.size() - 1)
			return;
		if (!BookState.getInstance().setFlipState())
			return;
		pageSubViewPrepare(1);
		this.loadPage(this.subPageList.get(currentPageIndex + 1));
		this.getHLBookLayout().addView(viewPage,
				viewPage.getCurrentLayoutParams());
		playSubPageFlipAnimation(0, 1);
		doFlipSubPage = true;
	}

	private void pageSubViewPrepare(int current) {
		flipSubPageAnimation(current);
		viewPage.stopVideo();
		viewPage.stop();
		viewPage.clean();
		this.getHLBookLayout().removeView(viewPage);
		viewPage.getRootView().invalidate();
	}

	private void flipSubPageAnimation(int currentIndex) {
		try {
			Bitmap bl = this.hlBookLayout.getCurrentScreen();
			this.subPageViewFlip.setBitmap(bl);
			this.subPageViewFlip.setViewPage(hlBookLayout);
			subPageViewFlip.show();
		} catch (Exception e) {
			Log.e("hl", "bookcontroller  flipanimaiotn", e);
		}
	}

	/**
	 * 播放滑动效果
	 * 
	 * @param direction
	 *            上滑动为true下滑动为false
	 */
	private void playSubPageFlipAnimation(int currentIndex, int newIndex) {
		subPageViewFlip.play(currentIndex, newIndex, new ActionOnEnd() {

			@Override
			public void doAction() {
				BookState.getInstance().restoreFlipState();
			}
		});
	}

	/**
	 * 执行组件对象的事件
	 * 
	 * @param entity
	 *            组件对象
	 * @param eventName
	 *            事件名字
	 * @param eventValue
	 *            事件触发的值
	 * @return
	 */
	public boolean runBehavior(ComponentEntity entity, String eventName,
			String eventValue) {
		// 增加关闭状态标志位的判断，如果已经关闭状态情况下，就不要做任何处理
		if (BookSetting.IS_CLOSED)
			return false;

		// 如果是背景音乐就有可能是null的
		if (entity.behaviors == null)
			return false;
		for (BehaviorEntity e : entity.behaviors) {
			if (eventName.equals(e.EventName)
					&& (StringUtils.isEmpty(eventValue) || eventValue
							.equals(e.EventValue))) {
				ViewCell viewCell = viewPage.getCellByID(e.FunctionObjectID);
				// 除了普通也还有可能是公共页
				if (viewCell == null && commonPage != null)
					viewCell = commonPage.getCellByID(e.FunctionObjectID);
				if (viewCell == null || StringUtils.isEmpty(e.FunctionObjectID)) {
					e.FunctionObjectID = entity.componentId;
				}
				runBehavior(e);
			}
		}
		return false;
	}

	/**
	 * 执行动作
	 * 
	 * @param entity
	 *            传入的组件对象
	 * @param eventName
	 *            事件名字
	 * @return
	 */
	public boolean runBehavior(ComponentEntity entity, String eventName) {
		// 增加关闭状态标志位的判断，如果已经关闭状态情况下，就不要做任何处理
		if (BookSetting.IS_CLOSED)
			return false;
		return runBehavior(entity, eventName, null);
	}

	public static String lastPageID = "";

	private boolean isMyPage(BehaviorEntity behavior) {
		if (StringUtils.isEmpty(mainPageID))
			return true;
		// 必须要保证当前触发的事件是在当前页或者公共页的组件
		if(behavior.triggerPageID.equals(mainPageID)
				|| behavior.triggerPageID.equals(commonPageID))
		{
			return true;
		}
		if(subPageList.size() > 0)
		{
			if(subPageList.contains(behavior.triggerPageID))
			{
				return true;
			}
		}
		return false;
	}

	public void runBehavior(BehaviorEntity behavior) {
		// 先判断这个动作和当前的page是否是同一个,如果不是那就不执行这个动作了
		if (!isMyPage(behavior)) {
			return;
		}
		BehaviorHelper.doBehavior(behavior);
	}

	/**
	 * 获得当前页的位置 并设置当前的section
	 * 
	 * @param pageID
	 * @return
	 */
	public int getSectionPagePosition(String pageID) {
		int res = -1;
		try {
			for (int j = 0; j < this.getBook().getSections().size(); j++) {
				for (int i = 0; i < this.getBook().getSections().get(j)
						.getPages().size(); i++) {
					if (this.getBook().getSections().get(j).getPages().get(i)
							.equals(pageID)) {
						res = i;
						if (this.currendsectionindex != j) {
							this.currendsectionindex = j;
							// this.setGarllary();
						}
						this.section = this.getBook().getSections().get(j);
						return res;
					}
				}
			}
		} catch (Exception e) {
			Log.e("hl", " getSectionPagePosition ", e);
		}
		return res;
	}

	public boolean isPageExist(String pageID) {
		try {
			for (int j = 0; j < this.getBook().getSections().size(); j++) {
				for (int i = 0; i < this.getBook().getSections().get(j)
						.getPages().size(); i++) {
					if (this.getBook().getSections().get(j).getPages().get(i)
							.equals(pageID)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.e("hl", " getSectionPagePosition ", e);
			return false;
		}
		return false;
	}

	public int getSectionPagePositionForGallery(String pageID) {
		int res = -1;
		try {
			for (int j = 0; j < this.getBook().getSections().size(); j++) {
				for (int i = 0; i < this.getBook().getSections().get(j)
						.getPages().size(); i++) {
					if (this.getBook().getSections().get(j).getPages().get(i)
							.equals(pageID)) {
						res = i;
						return res;
					}
				}
			}
		} catch (Exception e) {
			Log.e("hl", " getSectionPagePosition ", e);
		}
		return res;
	}

	public void resume() {
		viewPage.resume();
	}

	public String getSectionPageIdByPosition(int position) {
		return this.getBook().getSections().get(currendsectionindex).getPages()
				.get(position);
	}

	private void initBookState() {
		this.gallery.setVisibility(View.GONE);
		this.gallery.hideGalleryInfor();
		BookSetting.IS_AUTOPAGE = false;
	}

	public void registerButton(ImageButton pre, ImageButton next,
			ImageButton galleyButton, ImageButton homeButton) {

		this.galleyButton = galleyButton;
		this.homeButton = homeButton;
		if (BookSetting.FLIPCODE == 0) {
			if (pre != null) {
				this.preButton = pre;
			}
			if (next != null) {
				this.nextButton = next;
			}
		}
		if (this.preButton != null) {
			this.preButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					flipPage(-1);
				}
			});
		}

		if (this.nextButton != null) {
			this.nextButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					flipPage(1);
				}
			});
		}

		if (this.homeButton != null) {
			this.homeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (book.getSections() != null) {
						section = book.getSections().get(0);
						initBookState();
						if (null != section.pages) {
							goHomePage();
						}
					}
				}
			});
		}
		if (this.galleyButton != null) {
			this.galleyButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (isSettingGallery) {
						return;
					}
					try {
						if (gallery.getVisibility() == View.VISIBLE) {
							gallery.hideGalleryInfor();
							viewPage.playVideo();
							BookState.getInstance().setPlayViewPage();
						} else {

							if (!isSettingGallery) {
								if (BookSetting.IS_HOR_VER == true) {
									getHLActivity().hideGalley();
									getHLActivity().addGallery();
								}
								gallery.setVisibility(View.VISIBLE);
								gallery.bringToFront();

								setGarllary();
								gallery.setSelection(getSectionPagePosition(mainPageID));
								viewPage.stopVideo();
							}
						}
					} catch (Exception e) {
						e.fillInStackTrace();
					}
					if (hlActivity.waterStain != null) {
						hlActivity.waterStain.bringToFront();
					}
				}
			});
		}
		setDefaultView(getSectionPagePosition(this.mainPageID));
	}

	public void setGarllary() {
		try {
			if (null != gallery
					&& gallery.getCurrentSectionIndex() != this.currendsectionindex) {
				snapshots = getCurrentSnapshots();
				gallery.setSnapshots(snapshots);
				gallery.setCurrentSectionIndex(currendsectionindex);
			}
			if (snapshots != null) {
				gallery.setClickable(true);
				// gallery.setPageSize(snapshots.size());
				gallery.setOnItemClickListener(new OnItemClickListener() {
					@SuppressWarnings("rawtypes")
					public void onItemClick(AdapterView parent, View v,
							int position, long id) {
						try {
							gallery.setVisibility(View.GONE);
							gallery.hideGalleryInfor();
							String pageID = BookController.getInstance()
									.getSectionPageIdByPosition(position);
							if (mainPageID.equals(pageID)) {
								return;
							}

							BookController.getInstance().playPageById(pageID);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				gallery.showGalleryInfor();
				gallery.invalidate();
				isSettingGallery = false;
			}
		} catch (Exception e) {
			Log.e("hl", "BookController setGarllary ", e);
		}
	}

	public ArrayList<String> getCurrentSnapshots() {
		snapshots = new ArrayList<String>();
		for (String pageId : this.getBook().getSections()
				.get(currendsectionindex).getPages()) {
			snapshots.add(getSnapshotIdByPageId(pageId));
		}
		return snapshots;
	}

	public String getSnapshotIdByPageId(String pageID) {
		for (SnapshotEntity entity : this.book.getSnapshots()) {
			if (entity.pageID.equals(pageID)) {
				return entity.id;
			}
		}
		return "";
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public View getFlipView() {
		return flipView;
	}

	public void setFlipView(AbstractPageFlipView flipView) {
		this.flipView = flipView;
	}

	public ViewPage getViewPage() {
		return viewPage;
	}

	public void setViewPage(ViewPage viewPage) {
		this.viewPage = viewPage;
	}

	public AudioComponent getBackgroundMusic() {
		return backgroundMusic;
	}

	public WindowManager getWindwoManager() {
		return windwoManager;
	}

	public void setWindwoManager(WindowManager windwoManager) {
		this.windwoManager = windwoManager;
	}

	public RelativeLayout getHLBookLayout() {
		return hlBookLayout;
	}

	public int getCurrentsectionindex() {
		return currendsectionindex;
	}

	public void setHLBookLayout(RelativeLayout hlBookLayout) {
		this.hlBookLayout = (HLRelativeLayout) hlBookLayout;
	}

	public void setGallery(AbstractGalley g) {
		this.gallery = g;
	}

	public View getAdView() {
		return adView;
	}

	public void setAdView(View adView) {
		this.adView = adView;
	}

	public RelativeLayout getPdfMenuBarelativeLayout() {
		return pdfMenuBarelativeLayout;
	}

	public void setPdfMenuBarelativeLayout(
			RelativeLayout pdfMenuBarelativeLayout) {
		this.pdfMenuBarelativeLayout = pdfMenuBarelativeLayout;
	}

	public AbstractPageFlipView getSubPageViewFlip() {
		return subPageViewFlip;
	}

	public void setSubPageViewFlip(AbstractPageFlipView subPageViewFlip) {
		this.subPageViewFlip = subPageViewFlip;
	}

	public class AutoPageCountDown extends CountDownTimer {

		public AutoPageCountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			flipPage(1);
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}

	public HLActivity getHLActivity() {
		return hlActivity;
	}

	public void setHLActivity(HLActivity hlActivity) {
		this.hlActivity = hlActivity;
	}

	/**
	 * 打开之前需要将书架的相关静态变量设置好 打开书架
	 */
	public void openShelves() {
		hlActivity.progressHandler.sendEmptyMessage(0);
		AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
			@Override
			protected String doInBackground(String... params) {
				InputStream bookis = FileUtils.getInstance()
						.getFileInputStream("book.xml");
				Book shelvesBook = BookDecoder.getInstance().decode(bookis);
				BookSetting.buttons = shelvesBook.getButtons();
				ArrayList<SectionEntity> sections = shelvesBook.getSections();
				currendsectionindex = BookController.getInstance().book
						.getSections().size();
				BookController.getInstance().book.getSections()
						.addAll(sections);
				BookController.getInstance().book.getSnapshots().addAll(
						shelvesBook.getSnapshots());
				String pageID = sections.get(0).getPages().get(0);
				section = BookController.getInstance().book.getSections().get(
						currendsectionindex);
				BookDecoder.getInstance().initBookItemList();
				return pageID;
			}

			@Override
			protected void onPostExecute(String pageID) {
				BookController.getInstance().changePageById(pageID);
				BookController.getInstance().hlActivity.relayoutGlobalButton();
			}
		};
		task.execute("");
	}

	/**
	 * 关闭书架
	 */
	public void closeShelves() {
		hlActivity.progressHandler.sendEmptyMessage(0);
		AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
			@Override
			protected String doInBackground(String... params) {
				String lastPageID = section.lastPageID;
				String oladBookID = section.bookID;
				ArrayList<SectionEntity> delSectionList = new ArrayList<SectionEntity>();
				boolean isSetingLast = false;
				// 同时需要将书架书相关的section去掉
				for (SectionEntity _section : getBook().getSections()) {
					if (!isSetingLast) {
						for (String _pageID : _section.getPages()) {
							if (_pageID.equals(lastPageID)) {
								section = _section;
								BookSetting.BOOK_PATH = section.bookPath;
								HLSetting.IsResourceSD = section.isResourceSD;
								BookSetting.IS_SHELVES_COMPONENT = section.isShelves;
								BookDecoder.getInstance().initBookItemList();
								isSetingLast = true;
								break;
							}
						}
					}

					if (_section.bookID.equals(oladBookID)) {
						delSectionList.add(_section);
					}
				}
				// 去掉snapshots
				ArrayList<SnapshotEntity> delSnapList = new ArrayList<SnapshotEntity>();

				for (SnapshotEntity snap : getBook().getSnapshots()) {
					if (snap.bookID.equals(oladBookID)) {
						delSnapList.add(snap);
					}
				}
				getBook().getSections().removeAll(delSectionList);
				getBook().getSnapshots().removeAll(delSnapList);
				System.gc();
				return lastPageID;
			}

			@Override
			protected void onPostExecute(String lastPageID) {
				BookController.getInstance().hlActivity.relayoutGlobalButton();
				BookController.getInstance().changePageById(lastPageID);
			}
		};
		task.execute("");
	}

	// 加载完以后需要根据屏幕方向再次进行判断处理
	private void setOrien() {
		PageEntity pageEntity = mainViewPage.getEntity();
		String linkPageID = pageEntity.getLinkPageID();

		if ((pageEntity.getType().equals(ViewPage.PAGE_TYPE_HOR) || pageEntity
				.getType().equals(ViewPage.PAGE_TYPE_NONE))
				&& StringUtils.isEmpty(linkPageID)) {
			hlActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			BookSetting.IS_HOR = true;
		} else if (pageEntity.getType().equals(ViewPage.PAGE_TYPE_VER)
				&& StringUtils.isEmpty(linkPageID)) {
			BookSetting.IS_HOR = false;
			hlActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			hlActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
	}

	class MyActionOnEnd implements ActionOnEnd {
		private int pageIndex = 0;

		public MyActionOnEnd(int pageIndex) {
			this.pageIndex = pageIndex;
		}

		@Override
		public void doAction() {
			getHLBookLayout().post(new Runnable() {
				@Override
				public void run() {
					// getHLBookLayout().postInvalidate();
					setDefaultView(pageIndex);
					mainViewPage.bringToFront();
					flipView.recycleBitmap();
					BookState.getInstance().restoreFlipState();
					// revokeCommonPage();
					setOrien();
					hlActivity.refreshMark();
					removeNotShowViewPage();
					// getSectionPageIdByPosition(pageIndex);
					// hlActivity.refreshSnapshots();
				}

			});
		}
	}

	public void revokeCommonPage() {
		for (int index = 0; index < hlBookLayout.getChildCount(); index++) {
			View childV = hlBookLayout.getChildAt(index);
			if (childV == commonPage) {
				hlBookLayout.removeView(commonPage);
				hlActivity.commonLayout.addView(commonPage);
				hlActivity.commonLayout.setVisibility(View.GONE);
				break;
			}
		}
	}

	public Bitmap getSnapshot(View v) {
		mainViewPage.destroyDrawingCache();
		v.setBackgroundColor(Color.WHITE);
		Bitmap bitmap = null;
		if (mainViewPage.getEntity().isCashSnapshot()) {
			return BookController.getInstance().getSnapShotCashImage(
					mainViewPage.getEntity());
		}
		int width = 0;
		int height = 0;
		if (BookSetting.FIX_SIZE) {
			width = BookSetting.INIT_SCREEN_WIDTH;
			height = BookSetting.INIT_SCREEN_HEIGHT;
		} else {
			width = v.getWidth();
			height = v.getHeight();
		}
		try {
			// if (BitmapManageUtil.currentscreen == null) {
			try {

				bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.RGB_565);

			} catch (OutOfMemoryError e) {
				bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ALPHA_8);
			}
			// }
			v.draw(new Canvas(bitmap));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return bitmap;
	}

	public ArrayList<String> getPageHistory() {
		if (pageHistory == null) {
			pageHistory = new ArrayList<String>();
		}
		return pageHistory;
	}
}
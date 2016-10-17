package org.vudroid.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.vudroid.core.DecodeService.DecodeCallback;
import org.vudroid.core.events.ZoomListener;
import org.vudroid.core.models.CurrentPageModel;
import org.vudroid.core.models.DecodingProgressModel;
import org.vudroid.core.models.ZoomModel;
import org.vudroid.core.multitouch.MultiTouchZoom;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

public class DocumentViewNew extends View implements ZoomListener {
	protected ZoomModel zoomModel;
	protected CurrentPageModel currentPageModel;
	DecodingProgressModel progressModel;
	DecodeService decodeService;
	private final HashMap<Integer, PageNew> pages = new HashMap<Integer, PageNew>();
	private boolean isInitialized = false;
	private float lastX;
	private float lastY;
	private VelocityTracker velocityTracker;
	private final Scroller scroller;
	private RectF viewRect;
	private boolean inZoom;
	private long lastDownEventTime;
	private static final int DOUBLE_TAP_TIME = 500;
	private MultiTouchZoom multiTouchZoom;

	private boolean isPortrait;

	public int pageIndex = -1;
	public int pageCount;
	private File cacheDir;
	private int decodingPage = -1;

	protected GestureDetector gestureDetector;

	private ReentrantLock lock = new ReentrantLock();

	private boolean isChanged;

	private boolean isLoading = false;// 是否正在loading page;

	private boolean testState = false;

	public DocumentViewNew(Context context) {
		this(context, null, null);
	}

	public DocumentViewNew(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.zoomModel = new ZoomModel();
		this.zoomModel.addEventListener(this);
		initMultiTouchZoomIfAvailable(zoomModel);

		scroller = new Scroller(getContext());

		setKeepScreenOn(true);
		setFocusable(true);
		setFocusableInTouchMode(true);

		gestureDetector = new GestureDetector(new MyGestureDetector());
	}

	public DocumentViewNew(Context context,
			DecodingProgressModel progressModel,
			CurrentPageModel currentPageModel) {
		super(context);
		setKeepScreenOn(true);
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.progressModel = progressModel;
		this.currentPageModel = currentPageModel;

		this.zoomModel = new ZoomModel();
		initMultiTouchZoomIfAvailable(zoomModel);
		this.zoomModel.addEventListener(this);

		scroller = new Scroller(getContext());

		gestureDetector = new GestureDetector(new MyGestureDetector());
	}

	/**
	 * 横竖屏切换调用方法
	 */
	public void refresh() {
		//
		isChanged = true;// 标示横竖屏已经切换
		PageNew page = pages.get(pageIndex);
		page.setBitmap(null);
		zoomModel.setZoom(1.0f);
		zoomModel.commit();
	}

	public void setProgressModel(DecodingProgressModel model) {
		this.progressModel = model;
	}

	public void setPageModel(CurrentPageModel model) {
		this.currentPageModel = model;
	}

	public void openDoc(String filePath) {
		if (this.decodeService == null)
			return;
		this.decodeService.open(filePath);
		pageCount = this.decodeService.getPageCount();

		String storageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(storageState)) {
			File sdPath = Environment.getExternalStorageDirectory();
			File file = new File(filePath);
			cacheDir = new File(sdPath, file.getName() + ".tmp");
			cacheDir.mkdirs();
			if (!cacheDir.exists())
				cacheDir = null;
		}

		// use post to ensure that document view has width and height before
		// decoding begin
		post(new Runnable() {
			public void run() {
				init();
				updatePageVisibility();
			}
		});
	}

	protected void initMultiTouchZoomIfAvailable(ZoomModel zoomModel) {
		try {
			multiTouchZoom = (MultiTouchZoom) Class
					.forName("org.vudroid.core.multitouch.MultiTouchZoomImpl")
					.getConstructor(ZoomModel.class).newInstance(zoomModel);
		} catch (Exception e) {
			System.out.println("Multi touch zoom is not available: " + e);
		}
	}

	public void setDecodeService(DecodeService decodeService) {
		this.decodeService = decodeService;
		this.decodeService.setContainerView(this);
	}

	private void init() {
		if (isInitialized) {
			return;
		}
		final int width = decodeService.getEffectivePagesWidth();
		final int height = decodeService.getEffectivePagesHeight();
		for (int i = 0; i < decodeService.getPageCount(); i++) {

			pages.put(i, new PageNew(this, i));
			pages.get(i).setAspectRatio(width, height);
		}
		isInitialized = true;
		invalidatePageSizes();
		goToPageImpl(pageIndex);
	}

	public void recyle() {
		for (PageNew page : pages.values()) {
			page.dispose();
		}
	}

	private void goToPageImpl(final int toPage) {
		pageIndex = toPage;
		final int width = decodeService.getEffectivePagesWidth(pageIndex);
		final int height = decodeService.getEffectivePagesHeight(pageIndex);
		pages.get(pageIndex).setAspectRatio(width, height);
		System.out.println("goto page " + toPage);

		for (PageNew page : pages.values()) {
			if (page.getBitmap() != null) {
				page.dispose();
			}
		}
		final PageNew page = pages.get(toPage);
		Bitmap bitmap = null;
		if (bitmap == null) {
			// bitmap = loadPage(page.index);
			//if (bitmap != null) {
				page.setBitmap(bitmap);
			//} else 
			if (decodingPage != toPage) {
				decodingPage = toPage;
				decodeService.stopDecoding(this);
				isLoading = true;
				System.gc();
				decodeService.decodePage(this, toPage, new DecodeCallback() {
					@Override
					public void decodeComplete(final Bitmap bitmap, float zoom) {

						decodingPage = -1;
						page.setBitmap(bitmap);
						post(new Runnable() {
							public void run() {
								postInvalidate();
								updatePageVisibility();
							}
						});
						savePage(page);
						int cacheSize = 1;
						int min = toPage - cacheSize, max = toPage + cacheSize;
						for (PageNew page : pages.values()) {
							if (page.getBitmap() != null
									&& (page.index < min || page.index > max)) {
								System.out
										.println("dispose page " + page.index);
								page.dispose();
							}
						}

						isLoading = false;
						if (testState == true) {
							goToPage(pageIndex + 1);
						}
					}
				});
			}
		}

		scrollTo(0, page.getTop());
		post(new Runnable() {
			public void run() {
				if (currentPageModel != null)
					currentPageModel.setCurrentPageIndex(getCurrentPage());
			}
		});
		zoomModel.setZoom(1.0f);
		zoomModel.commit();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		// bounds could be not updated

		if (inZoom || decodingPage != -1) {
			return;
		}
		// on scrollChanged can be called from scrollTo just after new layout
		// applied so we should wait for relayout
		post(new Runnable() {
			public void run() {
				updatePageVisibility();
			}
		});
	}

	private void updatePageVisibility() {
		for (PageNew page : pages.values()) {
			page.updateVisibility();
		}
	}

	/**
	 * 放大，缩小结束时调用的方法
	 */
	public void commitZoom() {
		for (PageNew page : pages.values()) {
			page.invalidate();
		}
		inZoom = false;
	}

	public void showDocument() {
		// use post to ensure that document view has width and height before
		// decoding begin
		post(new Runnable() {
			public void run() {
				init();
				updatePageVisibility();
			}
		});
	}

	//private long preChangePage = 0;

	public void changePage(int delta) {
		if (this.isLoading == true) {
			return;
		}
		// if (preChangePage == 0) {
		// preChangePage = System.currentTimeMillis();
		// } else {
		// if (System.currentTimeMillis() - preChangePage < 1000) {
		// return;
		// }
		//
		// preChangePage = System.currentTimeMillis();
		// }
		int toPage = pageIndex;
		if (delta == Integer.MIN_VALUE)
			toPage = 0;
		else if (delta == Integer.MAX_VALUE)
			toPage = pageCount - 1;
		else {
			toPage += delta;
			if (toPage < 0)
				toPage = 0;
			if (toPage > pageCount - 1)
				toPage = pageCount - 1;
		}
		goToPage(toPage);
	}

	public void goToPage(int toPage) {
		if (isInitialized) {
			goToPageImpl(toPage);
		} else {
			pageIndex = toPage;
		}
	}

	public int getCurrentPage() {
		/*
		 * for (Map.Entry<Integer, Page> entry : pages.entrySet()) { if
		 * (entry.getValue().isVisible()) { return entry.getKey(); } } return 0;
		 */
		return pageIndex;
	}

	public void zoomChanged(float newZoom, float oldZoom) {
		inZoom = true;
		stopScroller();
		final float ratio = newZoom / oldZoom;
		invalidatePageSizes();
		scrollTo(
				(int) ((getScrollX() + getWidth() / 2) * ratio - getWidth() / 2),
				(int) ((getScrollY() + getHeight() / 2) * ratio - getHeight() / 2));
		postInvalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isLoading == true) {
			return true;
		}

		if (this.pageIndex == -1) {
			return true;
		}

		System.out.println("decodingPage=======" + decodingPage);
		System.out.println("pageIndex=======" + pageIndex);
		super.onTouchEvent(ev);

		if (gestureDetector.onTouchEvent(ev))
			return true;

		if (multiTouchZoom != null) {
			if (multiTouchZoom.onTouchEvent(ev)) {
				return true;
			}

			if (multiTouchZoom.isResetLastPointAfterZoom()) {
				setLastPosition(ev);
				multiTouchZoom.setResetLastPointAfterZoom(false);
			}
		}

		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(ev);

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			stopScroller();
			setLastPosition(ev);
			if (ev.getEventTime() - lastDownEventTime < DOUBLE_TAP_TIME) {
				zoomModel.toggleZoomControls();
			} else {
				lastDownEventTime = ev.getEventTime();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			scrollBy((int) (lastX - ev.getX()), (int) (lastY - ev.getY()));
			setLastPosition(ev);
			break;
		case MotionEvent.ACTION_UP:
			velocityTracker.computeCurrentVelocity(1000);
			scroller.fling(getScrollX(), getScrollY(),
					(int) -velocityTracker.getXVelocity(),
					(int) -velocityTracker.getYVelocity(), getLeftLimit(),
					getRightLimit(), getTopLimit(), getBottomLimit());
			velocityTracker.recycle();
			velocityTracker = null;

			break;
		}
		return true;
	}

	private void setLastPosition(MotionEvent ev) {
		lastX = ev.getX();
		lastY = ev.getY();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				lineByLineMoveTo(1);
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				lineByLineMoveTo(-1);
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				verticalDpadScroll(1);
				return true;
			case KeyEvent.KEYCODE_DPAD_UP:
				verticalDpadScroll(-1);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	private void verticalDpadScroll(int direction) {
		scroller.startScroll(getScrollX(), getScrollY(), 0, direction
				* getHeight() / 2);
		invalidate();
	}

	private void lineByLineMoveTo(int direction) {
		if (direction == 1 ? getScrollX() == getRightLimit()
				: getScrollX() == getLeftLimit()) {
			scroller.startScroll(getScrollX(), getScrollY(), direction
					* (getLeftLimit() - getRightLimit()), (int) (direction
					* pages.get(getCurrentPage()).bounds.height() / 50));
		} else {
			scroller.startScroll(getScrollX(), getScrollY(), direction
					* getWidth() / 2, 0);
		}
		invalidate();
	}

	private int getTopLimit() {
		// return 0;
		int top = (int) pages.get(getCurrentPage()).getTop();
		int topDelt = (int) ((getHeight() - pages.get(pageIndex).bounds
				.height()) / 2);
		if (topDelt > 0)
			top += topDelt;
		return top;
	}

	private int getLeftLimit() {
		return 0;
	}

	private int getBottomLimit() {
		// return (int) pages.get(pages.size() - 1).bounds.bottom - getHeight();
		int bottom = (int) pages.get(getCurrentPage()).getBottom()
				- getHeight();
		int bottomDelt = (int) ((getHeight() - pages.get(pageIndex).bounds
				.height()) / 2);
		if (bottomDelt > 0)
			bottom += bottomDelt;
		return bottom;
	}

	private int getRightLimit() {
		return (int) (getWidth() * zoomModel.getZoom()) - getWidth();
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(Math.min(Math.max(x, getLeftLimit()), getRightLimit()),
				Math.min(Math.max(y, getTopLimit()), getBottomLimit()));
		viewRect = null;
	}

	RectF getViewRect() {
		if (viewRect == null) {
			viewRect = new RectF(getScrollX(), getScrollY(), getScrollX()
					+ getWidth(), getScrollY() + getHeight());
		}
		return viewRect;
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (PageNew page : pages.values()) {
			page.draw(canvas);
		}
	}

	/**
	 * 横竖屏切换会调用这个方法
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		float scrollScaleRatio = getScrollScaleRatio();
		invalidatePageSizes();
		invalidateScroll(scrollScaleRatio);
		commitZoom();

		if (isChanged) {
			goToPage(pageIndex);
			isChanged = false;
		}
		// String msg = "width:" + getWidth() + ",height:" + getHeight();
		// Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 修改所有页面的起始高度，宽度是一样的
	 * 
	 * 放大和缩水的时候调用
	 */
	void invalidatePageSizes() {
		if (!isInitialized) {
			return;
		}
		float heightAccum = 0;
		int width = getWidth();
		float zoom = zoomModel.getZoom();
		for (int i = 0; i < pages.size(); i++) {
			PageNew page = pages.get(i);
			float pageHeight = page.getPageHeight(width, zoom);
			page.setBounds(new RectF(0, heightAccum, width * zoom, heightAccum
					+ pageHeight));
			heightAccum += pageHeight;
		}
	}

	private void invalidateScroll(float ratio) {
		if (!isInitialized) {
			return;
		}
		stopScroller();
		final PageNew page = pages.get(0);
		if (page == null || page.bounds == null) {
			return;
		}
		scrollTo((int) (getScrollX() * ratio), (int) (getScrollY() * ratio));
	}

	private float getScrollScaleRatio() {
		final PageNew page = pages.get(0);
		if (page == null || page.bounds == null) {
			return 0;
		}
		final float v = zoomModel.getZoom();
		return getWidth() * v / page.bounds.width();
	}

	private void stopScroller() {
		if (!scroller.isFinished()) {
			scroller.abortAnimation();
		}
	}

	/**
	 * 存储当前页面的截图
	 * 
	 * @param page
	 */
	protected void savePage(PageNew page) {
		Bitmap bitmap = page.getBitmap();
		if (cacheDir == null || bitmap == null)
			return;
		File file;
		/*
		 * boolean isPortraitBitmap; if(this.isPortrait) { if(bitmap.getWidth()
		 * == getWidth()) isPortraitBitmap = true; else isPortraitBitmap =
		 * false; } else { if(bitmap.getWidth() == getWidth()) isPortraitBitmap
		 * = false; else isPortraitBitmap = true; } if(isPortraitBitmap)
		 */
		Configuration cf = this.getResources().getConfiguration();
		if (cf.orientation == Configuration.ORIENTATION_PORTRAIT)
			file = new File(cacheDir, page.index + "_p.png");
		else
			file = new File(cacheDir, page.index + "_l.png");
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(CompressFormat.PNG, 100, out);
			System.out.println("save page " + page.index);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	protected Bitmap loadPage(int index) {
		if (cacheDir == null)
			return null;

		Bitmap bitmap = null;
		File file;
		Configuration cf = this.getResources().getConfiguration();
		if (cf.orientation == Configuration.ORIENTATION_PORTRAIT)
			file = new File(cacheDir, index + "_p.png");
		else
			file = new File(cacheDir, index + "_l.png");
		if (file.exists()) {
			bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			System.out.println("load page " + index);
		}
		return bitmap;
	}

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// next
				changePage(1);
				return true;
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// prev
				changePage(-1);
				return true;
			}
			return false;
		}
	}

	public void setPortrait(boolean isPortrait) {
		lock.lock();
		try {
			this.isPortrait = isPortrait;
		} finally {
			lock.unlock();
		}
	}

	public boolean isPortrait() {
		lock.lock();
		try {
			return isPortrait;
		} finally {
			lock.unlock();
		}
	}

	public boolean isTestState() {
		return testState;
	}

	public void setTestState(boolean testState) {
		this.testState = testState;
	}

}

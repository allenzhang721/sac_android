package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.hl.android.R;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.Cell;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;

@SuppressLint({ "ViewConstructor", "DrawAllocation" })
public class ConnectLineComponent extends View implements Component {
	private MoudleComponentEntity mEntity;
	private Context mContext;
	private int cellWidth = 0;
	private int cellHeight = 0;
	private int lineGap = 0;
	private int rowGap = 0;

	private Paint paint;// 声明画笔
	private int lineStartX = -100;
	private int lineStartY = -100;
	private int lineEndX = -100;
	private int lineEndY = -100;

	private ArrayList<Line> lineList = new ArrayList<Line>();
	private ArrayList<Rect> rectList = new ArrayList<Rect>();
	// 是否可以划线
	private boolean canDrawLine;
	private Rect waitToLine1, waitToLine2;

	private Rect currentBigRect;
	private boolean hasInRect = false;
	private int hasLinkedCount = 0;
	private static String BEHAVIOR_ON_CONNECT_SIGLE = "BEHAVIOR_ON_CONNECT_SIGLE";
	private static String BEHAVIOR_ON_CONNECT_ALL = "BEHAVIOR_ON_CONNECT_ALL";
	private static String BEHAVIOR_ON_CONNECT_SIGLE_ERROR = "BEHAVIOR_ON_CONNECT_SIGLE_ERROR";

	public ConnectLineComponent(Context context, ComponentEntity entity) {
		super(context);
		setEntity(entity);
		mContext = context;

		paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		paint.setStyle(Style.FILL);// 设置非填充
		paint.setAntiAlias(true);// 锯齿不显示
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeCap(Cap.ROUND);
		setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity = (MoudleComponentEntity) entity;
	}

	@Override
	public void load() {
		lineGap = (int) ScreenUtils.getHorScreenValue(mEntity.mLineGap);
		rowGap = (int) ScreenUtils.getVerScreenValue(mEntity.mRowOrColumnGap);
		cellWidth = getLayoutParams().width - lineGap;
		cellWidth = cellWidth / 2;
		int cellRows = mEntity.cellList.size() / 2;
		cellHeight = getLayoutParams().height - (cellRows * rowGap);
		cellHeight = cellHeight / cellRows;
		rectList = getDataSource();
		paint.setStrokeWidth(mEntity.lineThick);// 笔宽5像素
		paint.setColor(mEntity.lineColor);// 设置为红笔
		paint.setAlpha((int) (mEntity.lineAlpha*255));
	}

	/**
	 * 设置绘画的数据源
	 * 
	 * @return
	 */
	private ArrayList<Rect> getDataSource() {

		ArrayList<Rect> source = new ArrayList<Rect>();
		
		for (int index = 0; index < mEntity.cellList.size(); index++) {
			int rectIndex = -1;
			
			Cell cell = mEntity.cellList.get(index);
			int x = 0;

			if ("RIGHT_CELL".equals(cell.mCellType)) {
				x = cellWidth + lineGap;
			}else{
				rectIndex = index/2;
			}
			int row = index / 2;
			int y = (cellHeight + rowGap) * row;

			String sourceID = cell.mSourceID;
			Bitmap bitmap = BitmapUtils.getBitMap(sourceID, mContext);
			Rect rect = new Rect(mContext, x, y, cellWidth, cellHeight, bitmap,
					x == 0);
			rect.mIndex = rectIndex;
			rect.myID = cell.mCellID;
			rect.shouldID = cell.mLinkID;
			source.add(rect);
		}
		return source;
	}

	/**
	 * 
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		canvas.save();
		canvas.scale(1/1.1f, 1/1.1f, getLayoutParams().width/2.0f, getLayoutParams().height/2.0f);
		for (int i = 0; i < rectList.size(); i++) {
			Rect currentRect = rectList.get(i);
			currentRect.drawMe(canvas, paint);
		}
		for (int i = 0; i < lineList.size(); i++) {
			Line currentLine = lineList.get(i);
			canvas.drawLine(currentLine.mStartX+getPaddingLeft(), currentLine.mStartY+getPaddingTop(),
					currentLine.mEndX+getPaddingLeft(), currentLine.mEndY+getPaddingTop(), paint);
			canvas.drawCircle(currentLine.mStartX+getPaddingLeft(), currentLine.mStartY+getPaddingTop(),mEntity.lineThick/2.0f, paint);
			canvas.drawCircle(currentLine.mEndX+getPaddingLeft(), currentLine.mEndY+getPaddingTop(),mEntity.lineThick/2.0f, paint);
		}
		if (canDrawLine) {
			if (lineEndX != -100)
				canvas.drawLine(lineStartX+getPaddingLeft(), lineStartY+getPaddingTop(), lineEndX+getPaddingLeft(), lineEndY+getPaddingTop(),paint);// 画线
			    canvas.drawCircle(lineStartX+getPaddingLeft(), lineStartY+getPaddingTop(),mEntity.lineThick/2.0f, paint);
			    canvas.drawCircle(lineEndX+getPaddingLeft(), lineEndY+getPaddingTop(),mEntity.lineThick/2.0f, paint);
		}
		canvas.restore();
		postInvalidateDelayed(50);
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}
 
	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		this.setVisibility(View.INVISIBLE);
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	private void doLinkFailureAnimation(Rect waitToLine2) {
		if (!waitToLine2.isPlayingAnimation()) {
			waitToLine2.setAnimationState(true);
		}
	}

	// 触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {// 如果点击
			lineStartX = (int) event.getX();
			lineStartY = (int) event.getY();
			for (int i = 0; i < rectList.size(); i++) {
				Rect currentRect = rectList.get(i);
				if (lineStartX > currentRect.mX
						&& lineStartX < currentRect.mX + currentRect.mWidth
						&& lineStartY > currentRect.mY
						&& lineStartY < currentRect.mY + currentRect.mHeight) {
					canDrawLine = !currentRect.hasLinked();
					if (canDrawLine) {
						waitToLine1 = currentRect;
						waitToLine1.setCurrentShowLinkPoint(Rect.LINKPOINT_FADE);
						lineStartX = waitToLine1.getLinkPointCenterX();
						lineStartY = waitToLine1.getLinkPointCenterY();
						waitToLine1.shouldBig();
					}
					break;
				}
			}
			// checkDoAnimation(waitToLine1,waitToLine2);
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {// 如果拖动
			if (canDrawLine) {
				lineEndX = (int) event.getX();
				lineEndY = (int) event.getY();
				hasInRect = false;
				for (int i = 0; i < rectList.size(); i++) {
					Rect currentRect = rectList.get(i);
					if(currentRect.mLinkPointIsLeft==waitToLine1.mLinkPointIsLeft){
						continue;
					}
					if (lineEndX > currentRect.mX
							&& lineEndX < currentRect.mX + currentRect.mWidth
							&& lineEndY > currentRect.mY
							&& lineEndY < currentRect.mY + currentRect.mHeight) {
						hasInRect = true;
						if (currentBigRect != null
								&& currentBigRect != currentRect) {
							currentBigRect.shouldGoback();
							if (!currentBigRect.hasLinked()) {
								currentBigRect
										.setCurrentShowLinkPoint(Rect.LINKPOINT_LIGHT);
							}
						}
						currentBigRect = currentRect;
						lineEndX = currentRect.getLinkPointCenterX();
						lineEndY = currentRect.getLinkPointCenterY();
						waitToLine2 = currentRect;
						waitToLine2.shouldBig();
						waitToLine2
								.setCurrentShowLinkPoint(Rect.LINKPOINT_FADE);
						break;
					}
				}
				if (!hasInRect) {
					if (currentBigRect != null) {
						if (!currentBigRect.hasLinked()) {
							currentBigRect
									.setCurrentShowLinkPoint(Rect.LINKPOINT_LIGHT);
						}
						currentBigRect.shouldGoback();
					}
					waitToLine2 = null;
				}
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (waitToLine1 != null) {
				if (waitToLine2 != null
						&& waitToLine1.shouldID.equals(waitToLine2.myID)) {
					lineStartX = waitToLine1.getLinkPointCenterX();
					lineStartY = waitToLine1.getLinkPointCenterY();
					lineEndX = waitToLine2.getLinkPointCenterX();
					lineEndY = waitToLine2.getLinkPointCenterY();
					Line line = new Line(lineStartX, lineStartY, lineEndX,
							lineEndY);
					lineList.add(line);
					waitToLine1.setHasLined(true);
					waitToLine2.setHasLined(true);
					waitToLine1.canBig = false;
					waitToLine2.canBig = false;
					waitToLine1.setCurrentShowLinkPoint(Rect.LINKPOINT_FADE);
					waitToLine2.setCurrentShowLinkPoint(Rect.LINKPOINT_FADE);
					hasLinkedCount++;
					
					doLinkedAction(waitToLine1, waitToLine2);
					if (hasLinkedCount * 2 == mEntity.cellList.size()) {
						doAllLinkedAction();
					}
				} else {
					if (!waitToLine1.hasLinked()) {
						waitToLine1
								.setCurrentShowLinkPoint(Rect.LINKPOINT_LIGHT);
					}
					if (waitToLine2 != null) {
						if (!waitToLine2.hasLinked()) {
							waitToLine2
									.setCurrentShowLinkPoint(Rect.LINKPOINT_LIGHT);
						}
						waitToLine2.shouldGoback();
						doLinkFailureAnimation(waitToLine2);
						doLinkFailureAction(waitToLine1, waitToLine2);
					}
				}
			}
			if (waitToLine1 != null) {
				waitToLine1.shouldGoback();
			}
			// if (currentBigRect != null) {
			// currentBigRect.shouldGoback();
			// if(!currentBigRect.hasLinked()){
			// currentBigRect.setCurrentShowLinkPoint(Rect.LINKPOINT_LIGHT);
			// }
			// }
			waitToLine1 = null;
			waitToLine2 = null;
			lineEndX = -100;
			lineEndY = -100;
			lineStartX = -100;
			lineStartY = -100;
			hasInRect = false;
			canDrawLine = false;
		}
		return true;

	}

	class Rect {
		public int mIndex = -1;
		
		public int mX = 0;
		public int mY = 0;
		public int mWidth = 0;
		public int mHeight = 0;
		private boolean mHasLinked = false;
		private boolean mLinkPointIsLeft;
		public Bitmap mImageBitmap;
		public Bitmap mLinkPointFade, mLinkPointLight;
		private String shouldID;
		private String myID;
		private Bitmap currentShowLinkPoint;
		public static final int LINKPOINT_LIGHT = 0x1000010;
		public static final int LINKPOINT_FADE = 0x1000011;
		public boolean mShouldBig = false;
		public boolean canBig = true;
		private boolean mDoAnimation = false;
		private int count = 0;
		private long oldTime = 0;
		private long currentTime;
		private int rotate = 5;

		public Rect(Context context, int x, int y, int width, int height,
				Bitmap imagebitmap, boolean linkPointIsLeft) {
			mX = x;
			mY = y;
			mWidth = width;
			mHeight = height;
			mImageBitmap = imagebitmap;
			this.mLinkPointIsLeft = linkPointIsLeft;
			mLinkPointFade = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.dian01);
			mLinkPointLight = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.dian02);
			currentShowLinkPoint = mLinkPointLight;
		}

		public void setAnimationState(boolean doAnimation) {
			mDoAnimation = doAnimation;
		}

		public boolean isPlayingAnimation() {
			return mDoAnimation;
		}

		public void drawWrapBitmap(Canvas canvas){
			int resultWidth = (int) ((mWidth-mEntity.lineThick));
			int resultHeight = mHeight;
			if(mImageBitmap.getHeight()*1.0f/mImageBitmap.getWidth()>=resultHeight*1.0f/resultWidth){
				resultWidth=resultHeight*mImageBitmap.getWidth()/mImageBitmap.getHeight();
			}else{
				resultHeight=resultWidth*mImageBitmap.getHeight()/mImageBitmap.getWidth();
			}
			RectF rect=null;
			rect = new RectF(mX+(mWidth-resultWidth)/2+getPaddingLeft(),mY+getPaddingTop()+(mHeight-resultHeight)/2,mX+getPaddingLeft()+(mWidth-resultWidth)/2+resultWidth,mY+getPaddingTop()+(mHeight-resultHeight)/2+resultHeight);
			canvas.drawBitmap(mImageBitmap, null,rect, paint);
		}
		
		public void drawMe(Canvas canvas, Paint paint) {
			if (mImageBitmap != null) {
				if (mShouldBig && canBig) {
					canvas.save();
					canvas.scale(1.1f, 1.1f,mX + mWidth/2, mY + mHeight / 2);
					drawWrapBitmap(canvas);
					canvas.restore();
				} else {
					if (mDoAnimation) {
						currentTime = System.currentTimeMillis();
						if (currentTime - oldTime > 30) {
							rotate = -rotate;
							canvas.save();
							canvas.rotate(rotate, mX + mWidth/2, mY + mHeight / 2);
							drawWrapBitmap(canvas);
							canvas.restore();
							count++;
							oldTime = currentTime;
						}
						if (count >= 6) {
							mDoAnimation = false;
							count = 0;
							oldTime = 0;
						}
					} else {
						drawWrapBitmap(canvas);
					}
				}

			}
			if (mLinkPointIsLeft) {
				canvas.drawBitmap(currentShowLinkPoint, (getLayoutParams().width-lineGap)/2-7.5f,mY+getPaddingTop()
						+ mHeight / 2 - currentShowLinkPoint.getHeight() / 2,
						paint);
			} else {
				canvas.drawBitmap(currentShowLinkPoint,(getLayoutParams().width+lineGap)/2-7.5f , mY +getPaddingTop()+ mHeight
						/ 2 - currentShowLinkPoint.getHeight() / 2, paint);
			}
		}

		public void setCurrentShowLinkPoint(int currentShowLinkPoint) {
			if (currentShowLinkPoint == LINKPOINT_FADE) {
				this.currentShowLinkPoint = mLinkPointFade;
			} else if (currentShowLinkPoint == LINKPOINT_LIGHT) {
				this.currentShowLinkPoint = mLinkPointLight;
			}
		}

		public Bitmap getCurrentShowLinkPoint() {
			return currentShowLinkPoint;
		}

		public void setHasLined(boolean haslinked) {
			mHasLinked = haslinked;
		}

		public boolean hasLinked() {
			return mHasLinked;
		}

		public void shouldBig() {
			this.mShouldBig = true;
		}

		public void shouldGoback() {
			this.mShouldBig = false;
		}

		public int getLinkPointCenterY() {
			return mY + mHeight / 2;
		}

		public int getLinkPointCenterX() {
			if (mLinkPointIsLeft) {
				return mX+mWidth;
			} else {
				return mX;
			}
		}
	}

	class Line {
		public int mStartX = 0;
		public int mStartY = 0;
		public int mEndX = 0;
		public int mEndY = 0;

		public Line(int startx, int starty, int endx, int endy) {
			mStartX = startx;
			mStartY = starty;
			mEndX = endx;
			mEndY = endy;
		}
	}

	private void doLinkedAction(Rect waitToLine1, Rect waitToLine2) {
		String eventValue = "-1";
		if(waitToLine1.mIndex>=0){
			eventValue = Integer.toString(waitToLine1.mIndex);
		}else{
			eventValue = Integer.toString(waitToLine2.mIndex);
		}
		BookController.getInstance().runBehavior(this.getEntity(),
				BEHAVIOR_ON_CONNECT_SIGLE,eventValue);
	}

	private void doLinkFailureAction(Rect waitToLine1, Rect waitToLine2) {
		BookController.getInstance().runBehavior(this.getEntity(),
				BEHAVIOR_ON_CONNECT_SIGLE_ERROR);
	}

	private void doAllLinkedAction() {
		BookController.getInstance().runBehavior(this.getEntity(),
				BEHAVIOR_ON_CONNECT_ALL);
	}
}

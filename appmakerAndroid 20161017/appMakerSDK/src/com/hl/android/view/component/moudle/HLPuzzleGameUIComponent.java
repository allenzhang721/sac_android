package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.hl.android.R;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.inter.Component;

@SuppressLint({ "ViewConstructor", "DrawAllocation" })
public class HLPuzzleGameUIComponent extends View implements Component {
	private Context mContext;
	private Canvas mCanvas;//绘制未缩放的图片
	private Bitmap bitmap;
	private Paint mPaint;
	public static int targetWidth, targetHeight;
	//声明需要使用的bitmap
	// ===============================
	private Bitmap backGroundBitmap;//背景图片
	private Bitmap testBitmap;//拼图总图
	private Bitmap goBitmap1;//go图片1
	private Bitmap goBitmap2;//go图片2
	private Bitmap finishBitmap;
	private Bitmap opBitmap1;
	private Bitmap opBitmap2;
	private Bitmap ppBitmap1;
	private Bitmap ppBitmap2;
	private Bitmap rpBitmap1;
	private Bitmap rpBitmap2;
	private Bitmap star1Bitmap1;
	private Bitmap star2Bitmap2;
	private Bitmap sureBitmap;
	private Bitmap cancelBitmap;
	private Bitmap lineHBitmap;
	private Bitmap lineVBitmap;
	private Bitmap popupBitmap;
	public static int LINE_TYPE_TT = 0x1001;//表示需要画线的类型为2*3
	private static int LINE_TYPE_TF = 0x1002;//表示需要画线的类型为3*4
	private static int LINE_TYPE_FS = 0x1003;//表示需要画线的类型为4*6
	private int currentSelectLineType = LINE_TYPE_TT;//默认为2*3的线格
	private int waitToChangeLineType=currentSelectLineType;//记录等待切换线格的样式，点击取消时恢复默认值为currentSelectLineType；
	private int currentAlpha = 255;//当前alpha用于绘制总图提示时渐变
	private Bitmap currentGoBitmap;//当前显示的Go图片以下同理
	private Bitmap currentOpBitmap;
	private Bitmap currentPpBitmap;
	private Bitmap currentRpBitmap;
	private Bitmap currentStarBitmap;
	private boolean hasStartGame = false;//游戏开始的唯一标识
	private ArrayList<MRect> mRectList;//等待拼图的子图片集合
	private ArrayList<MRect> mRightPositionRects;//已经拼图正确的子图集合
	private static int rectHeight=100;//等待拼图的子图片的高度
	private static int rectWidth;//等待拼图的子图片的宽度，此值根据rectHeight等比例算出
	private float clipWidth;//拼图单个格子的宽度
	private float clipHeight;//拼图单个格子的高度
	private int rectTopPadding=650;//等待拼图的子图片集合距离顶部的高度
	private static int rectSpace=10;//等待拼图的子图片间的间隙宽度
	private float canScrollLength=0;//记录等待拼图的子图片集合过宽需要滑动的可以滑动的最大距离
	private boolean hasCloneRect;//标识是否已经得到随手指移动的rect
	private MotionEvent oldEvent = null;//上一次touch事件
	private float dx;//等待拼图的子图片滑动时的delta X
	private float dy;//等待拼图的子图片滑动时的delta Y
	private float ddx;//随手指移动的rect的delta X
	private float ddy;//随手指移动的rect的delta Y
	private float totalDx;//等待拼图的子图片滑动时已经移动的总距离
	private float totalDy;//等待拼图的子图片滑动时已经移动的总距离
	private int currentTouchImageIndex=-1;//当前touch图片在mRectList中的index
	private MRect currentCloneRect=null;//当前随手指移动的rect
	private int horCount;//总图被水平切割的块数
	private int verCount;//总图被垂直切割的块数
	private boolean hasWin;//标识是否已经拼图成功
	private boolean showTestBitmap=false;//标识是否显示全图，渐变消失后设置为false
	private boolean showMessage=false;//标识是否显示提示信息
	private long timeHasGo=0;//记录游戏开始已用时间
	private long bestTime=0;//记录最佳成绩，各自线格模式有自己的最佳成绩
	private int hasComplete=0;//拼图完成百分比
	private String currentShowStr;
	private float currentStrWidth;
	private int timerCount;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private float scalingW;
	private float scalingH;
	private ComponentEntity mEntity;
	private boolean hasSetConfig;
	private RectF backGroundRectf;
	private RectF currentGoRectf;
	private RectF currentRpRectf;
	private RectF currentOpRectf;
	private RectF currentPpRectf;
	private RectF currentStarRectf;
	private RectF popupRectf;
	private RectF sureRectf;
	private RectF cancelRectf;
	private RectF finishRectf;
	private RectF sureRectf1;
	
	public HLPuzzleGameUIComponent(Context context,ComponentEntity entity) {
		super(context);
		 this.mContext = context;
		 this.mEntity=entity;
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		backGroundRectf=new RectF(0, 0, 1024, 768);
		currentGoRectf=new RectF(850, 132, 850+142, 132+142);
		currentRpRectf=new RectF(865, 310, 865+112, 310+46);
		currentOpRectf=new RectF(865, 370, 865+112, 370+46);
		currentPpRectf=new RectF(865, 430, 865+112, 430+46);
		currentStarRectf=new RectF(858, 525, 858+131, 525+33);
		popupRectf=new RectF((1024-455)/2,(768-228)/2-50, (1024-455)/2+455,(768-228)/2-50+228);
		sureRectf=new RectF((1024-455)/2+57,(768-228)/2-50+150, (1024-455)/2+57+140,(768-228)/2-50+150+36);
		cancelRectf=new RectF((1024-455)/2+257,(768-228)/2-50+150, (1024-455)/2+257+140,(768-228)/2-50+150+36);
		finishRectf=new RectF((1024-455)/2,(768-228)/2-50,(1024-455)/2+457,(768-228)/2-50+325);
		sureRectf1=new RectF((1024-140)/2,(768-228)/2-50+247,(1024-140)/2+140,(768-228)/2-50+247+36);
	}

	private void init() {
		bitmap = Bitmap.createBitmap(1024, 768, Config.ARGB_8888);
		mCanvas=new Canvas(bitmap);
		backGroundBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzlebg);
		ArrayList<String> sourceIDS = ((MoudleComponentEntity) this.mEntity)
				.getSourceIDList();
		testBitmap=BitmapUtils.getBitMap(sourceIDS.get(0),mContext);
		if(testBitmap==null){
			testBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.puzzletest);
		}
		goBitmap1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzlestart);
		goBitmap2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzlepress);
		finishBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.finishtip);
		opBitmap1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.op_1);
		opBitmap2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.op_2);
		ppBitmap1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.pp_1);
		ppBitmap2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.pp_2);
		rpBitmap1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.rp_1);
		rpBitmap2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.rp_2);
		star1Bitmap1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzlestar1);
		star2Bitmap2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzlestar2);
		sureBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzleok);
		cancelBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzlecancel);
		lineHBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzlelineh);
		lineVBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzlelinev);
		popupBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.puzzlepopup);
		currentGoBitmap = goBitmap1;
		currentOpBitmap = opBitmap1;
		currentPpBitmap = ppBitmap1;
		currentRpBitmap = rpBitmap1;
		currentStarBitmap = star1Bitmap1;
		hasWin = false;
		preferences=mContext.getSharedPreferences("StoreBestTime",0);
		editor=preferences.edit();
		initRects();
		
	}

	private void initRects() {
		recycleBitmaps(mRectList);
		recycleBitmaps(mRightPositionRects);
		mRightPositionRects = new ArrayList<MRect>();
		mRectList=clipBitMapWith(currentSelectLineType,testBitmap);
		Collections.shuffle(mRectList);// 打乱顺序
		setRectsDrawPosition(mRectList);
		hasComplete=0;
		timeHasGo=0;
		timerCount=0;
		bestTime=getBestTime();
	}
	
	private void recycleBitmap(Bitmap bitmap) {
		if(bitmap!=null&&!bitmap.isRecycled()){
			bitmap.recycle();
		}
	}
	
	private void recycleBitmaps(ArrayList<MRect> rectList) {
		if(rectList!=null&&rectList.size()!=0){
			for (MRect mRect : rectList) {
				recycleBitmap(mRect.mImageBitmap);
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		long startTime=System.currentTimeMillis();
		if(!hasSetConfig){
			setConfig();
			hasSetConfig=true;
		}
		super.onDraw(canvas);
		myDraw(canvas);
		logic();
		long endTime=System.currentTimeMillis();
		postInvalidateDelayed(100+startTime-endTime);
	}

	private void setConfig() {
		targetWidth=getLayoutParams().width;
		targetHeight=getLayoutParams().height;
		scalingW=targetWidth*1.0f/1024;
		scalingH=targetHeight*1.0f/768;
		if(targetWidth<targetHeight){
			scalingW=targetHeight*1.0f/1024;
			scalingH=targetWidth*1.0f/768;
		}
		if(!HLSetting.FitScreen){
			if(scalingW>scalingH){
				scalingW=scalingH;
			}else{
				scalingH=scalingW;
			}
		}
	}

	private void myDraw(Canvas canvas) {
		if (null != canvas) {
			try {
				canvas.drawColor(Color.WHITE);
				canvas.save();
				drawTheOriginalBitmap();
				RectF rectF=new RectF((targetWidth-bitmap.getWidth()*scalingW)/2, (targetHeight-bitmap.getHeight()*scalingH)/2, (targetWidth+bitmap.getWidth()*scalingW)/2, (targetHeight+bitmap.getHeight()*scalingH)/2);
				if(targetWidth<targetHeight){
					canvas.rotate(90,targetWidth/2.0f,targetHeight/2.0f);
				}
				canvas.drawBitmap(bitmap, null, rectF, mPaint);
				canvas.restore();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			
			}
		}
	}

	private void drawTheOriginalBitmap() {
//		mm
		mCanvas.drawBitmap(backGroundBitmap,null,backGroundRectf, mPaint);
		mCanvas.drawBitmap(currentGoBitmap, null,currentGoRectf, mPaint);
		mCanvas.drawBitmap(currentRpBitmap, null,currentRpRectf, mPaint);
		mCanvas.drawBitmap(currentOpBitmap, null, currentOpRectf,mPaint);
		mCanvas.drawBitmap(currentPpBitmap, null, currentPpRectf,mPaint);
		mCanvas.drawBitmap(currentStarBitmap, null,currentStarRectf, mPaint);
		currentShowStr=String.format("%02d:%02d", timeHasGo/60,timeHasGo%60);
		currentStrWidth=mPaint.measureText(currentShowStr);
		mCanvas.drawText(currentShowStr, 102-currentStrWidth/2, 230, mPaint);
		currentShowStr=String.format("%d", (int)(hasComplete*100.0f/(horCount*verCount)));
		currentShowStr+="%";
		currentStrWidth=mPaint.measureText(currentShowStr);
		mCanvas.drawText(currentShowStr, 102-currentStrWidth/2, 388, mPaint);
		currentShowStr=String.format("%02d:%02d", bestTime/60,bestTime%60);
		currentStrWidth=mPaint.measureText(currentShowStr);
		mCanvas.drawText(currentShowStr, 102-currentStrWidth/2, 520, mPaint);
		if (!hasStartGame) {
			mPaint.setAlpha(255);
			RectF testRectf=new RectF(213, 125, 213+597, 125+437);
			mCanvas.drawBitmap(testBitmap, null, testRectf, mPaint);
		}else{
			if(showTestBitmap){
				mPaint.setAlpha(currentAlpha);
				RectF testRectf=new RectF(213, 125, 213+597, 125+437);
				mCanvas.drawBitmap(testBitmap, null, testRectf, mPaint);
				mPaint.setAlpha(255);
			}
			drawRectList(mRectList,mCanvas);
		}
		drawRightRects(mRightPositionRects,mCanvas);
		drawLine(mCanvas);
		if(showMessage){
			drawARGB(mCanvas);
			mCanvas.drawBitmap(popupBitmap,null,popupRectf,mPaint);
			mCanvas.drawBitmap(sureBitmap,null,sureRectf,mPaint);
			mCanvas.drawBitmap(cancelBitmap,null,cancelRectf,mPaint);
		}else if(hasWin){
			drawARGB(mCanvas);
			mCanvas.drawBitmap(finishBitmap,null,finishRectf,mPaint);
			mCanvas.drawBitmap(sureBitmap,null,sureRectf1,mPaint);
			currentShowStr=String.format("%02d:%02d", timeHasGo/60,timeHasGo%60);
			currentStrWidth=mPaint.measureText(currentShowStr);
			mCanvas.drawText(currentShowStr, (1024-455)/2+315-currentStrWidth/2,(768-228)/2-50+150, mPaint);
			currentShowStr=String.format("%02d:%02d", getBestTime()/60, getBestTime()%60);
			currentStrWidth=mPaint.measureText(currentShowStr);
			mCanvas.drawText(currentShowStr, (1024-455)/2+315-currentStrWidth/2, (768-228)/2-50+210, mPaint);
		}else if(currentCloneRect!=null){
			mPaint.setAlpha(200);
			currentCloneRect.drawMe(mCanvas, mPaint);
			mPaint.setAlpha(255);
		}
	}

	private long getBestTime() {
		return preferences.getLong(""+currentSelectLineType, 0);
	}

	private void drawARGB(Canvas canvas) {
		canvas.drawARGB(120, 0xff, 0xff, 0xff);
	}

	private void drawRightRects(ArrayList<MRect> rightPositionRects,Canvas canvas) {
		if(rightPositionRects!=null&&rightPositionRects.size()!=0){
			for (int i = 0; i < rightPositionRects.size(); i++) {
				MRect curRect=rightPositionRects.get(i);
				curRect.mWidth=clipWidth;
				curRect.mHeight=clipHeight;
				curRect.setXY(213+curRect.mIndexX*clipWidth, 125+curRect.mIndexY*clipHeight);
				curRect.drawMe(mCanvas, mPaint);
			}
		}
	}

	private void drawRectList(ArrayList<MRect> rectList, Canvas canvas) {
		if(rectList!=null&&rectList.size()>0){
			for (int i = 0; i < rectList.size(); i++) {
				MRect curRect=rectList.get(i);
				curRect.drawMe(mCanvas, mPaint);
			}
		}
	}

	private void drawLine(Canvas canvas) {
		
		for (int i = 0; i < horCount-1; i++) {
			RectF rectF=new RectF(213 + (i + 1) * clipWidth, 125, 213 + (i + 1) * clipWidth+2, 125+439);
			canvas.drawBitmap(lineVBitmap, null,rectF,mPaint);
		}
		for (int i = 0; i < verCount-1; i++) {
			RectF rectF=new RectF( 213, 125 + (i + 1) *clipHeight, 213+596, 125 + (i + 1) *clipHeight+2);
			canvas.drawBitmap(lineHBitmap, null,rectF,mPaint);
		}
	}

	private void logic() {
		if (showTestBitmap) {
			currentAlpha -= 6;
			if (currentAlpha <= 0) {
				showTestBitmap = false;
			}
		}
		if(hasStartGame&&!showMessage&&!hasWin){
			timerCount++;
			if(timerCount>=10){
				timeHasGo++;
				timerCount=0;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean cutTheEvent=false;
		 if(hasWin){
			if(event.getAction() == MotionEvent.ACTION_UP){
				if(touchInTheRect(event, (1024-140)/2, (768-228)/2-50+247, 140, 36)){
					hasWin=false;
					hasStartGame=false;
					initRects();
				}
			}
		}else if(showMessage){
			if(event.getAction() == MotionEvent.ACTION_UP){
				if(touchInTheRect(event, (1024-455)/2+57, (768-228)/2-50+150, 140, 36)){
					if(currentSelectLineType==waitToChangeLineType){
						hasStartGame=true;
						initRects();
					}else{
						hasStartGame=false;
						currentSelectLineType=waitToChangeLineType;
						initRects();
					}
					showMessage=false;
				}else if(touchInTheRect(event, (1024-455)/2+257, (768-228)/2-50+150, 140, 36)){
					showMessage=false;
					waitToChangeLineType=currentSelectLineType;
				}
			}
		}
		if (touchInTheRect(event, 850, 132, 142, 142)) {
			if(!showMessage&&!hasWin){
				if (event.getAction() == MotionEvent.ACTION_DOWN|| event.getAction() == MotionEvent.ACTION_MOVE) {
					currentGoBitmap = goBitmap2;
				} else {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (!hasStartGame) {
							hasStartGame = true;
						} else {
							showMessage=true;
						}
					}
					currentGoBitmap = goBitmap1;
				}
			}
		} else if (touchInTheRect(event, 865, 310, 112, 46)) {
			if(!showMessage&&!hasWin){
				if (event.getAction() == MotionEvent.ACTION_DOWN||event.getAction() == MotionEvent.ACTION_MOVE) {
					currentRpBitmap = rpBitmap2;
				} else {
					doTouchAction(event, LINE_TYPE_TT);
					currentRpBitmap = rpBitmap1;
				}
			}
		} else if (touchInTheRect(event, 865, 370, 112, 46)) {
			if(!showMessage&&!hasWin){
				if (event.getAction() == MotionEvent.ACTION_DOWN||event.getAction() == MotionEvent.ACTION_MOVE) {
					currentOpBitmap = opBitmap2;
				} else {
					doTouchAction(event, LINE_TYPE_TF);
					currentOpBitmap = opBitmap1;
				}
			}
		} else if (touchInTheRect(event, 865, 430, 112, 46)) {
			if(!showMessage&&!hasWin){
				if (event.getAction() == MotionEvent.ACTION_DOWN||event.getAction() == MotionEvent.ACTION_MOVE) {
					currentPpBitmap = ppBitmap2;
				} else {
					doTouchAction(event, LINE_TYPE_FS);
					currentPpBitmap = ppBitmap1;
				}
			}
		} else if (touchInTheRect(event, 860, 525, 131, 33)) {
			if(!showMessage&&!hasWin){
				if(hasStartGame){
					if (event.getAction() == MotionEvent.ACTION_DOWN||event.getAction() == MotionEvent.ACTION_MOVE) {
						currentStarBitmap = star2Bitmap2;
					} else {
						if(event.getAction() == MotionEvent.ACTION_UP){
							if(hasStartGame){
								currentAlpha=255;
								showTestBitmap=true;
							}
						}
						currentStarBitmap = star1Bitmap1;
					}
				}
			}
		} else if(touchInTheRect(event, 0, rectTopPadding, 1024, rectHeight)){
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				cutTheEvent=true;
			}
			if(!showMessage&&!hasWin){
				if(hasStartGame){
					currentTouchImageIndex=getTouchImageIndex(event);
					if(!hasCloneRect&&mRectList.size()!=0){
						if(canScrollLength>0){
							cutTheEvent=true;
							if (event.getAction() == MotionEvent.ACTION_MOVE) {
								dx = event.getX() - oldEvent.getX();
								dy=event.getY()-oldEvent.getY();
								dx/=scalingW;
								dy/=scalingW;
								totalDx+=dx;
								totalDy+=dy;
								if(targetWidth<targetHeight){
									moveRects(totalDy,dy);
								}else{
									moveRects(totalDx,dx);
								}
							}
						}
					}
				}
			}
		}else {
			if(currentTouchImageIndex!=-1){
				if(!hasCloneRect){
					currentCloneRect=mRectList.get(currentTouchImageIndex).cloneMe();
					hasCloneRect=true;
				}
			}
			currentGoBitmap = goBitmap1;
			currentRpBitmap = rpBitmap1;
			currentOpBitmap = opBitmap1;
			currentPpBitmap = ppBitmap1;
			currentStarBitmap = star1Bitmap1;
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(hasCloneRect){
				if(touchInTheRect(event, 213+currentCloneRect.mIndexX*clipWidth, 125+currentCloneRect.mIndexY*clipHeight,clipWidth,clipHeight)){
					mRightPositionRects.add(currentCloneRect);
					mRectList.remove(currentCloneRect.cloneOfwhom);
					hasComplete++;
					setRectsDrawPosition(mRectList);
					checkWinAndDoSomeThing(mRectList);
				}
			}
			if(currentTouchImageIndex!=-1){
				cutTheEvent=true;
			}
			currentTouchImageIndex=-1;
			currentCloneRect=null;
			hasCloneRect=false;
		}else{
			if(hasCloneRect){
				cutTheEvent=true;
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					ddx = event.getX() - oldEvent.getX();
					ddy = event.getY() - oldEvent.getY();
					if(targetWidth<targetHeight){
						ddx/=scalingH;
						ddy/=scalingW;
						currentCloneRect.mX+=ddy;
						currentCloneRect.mY-=ddx;
					}else{
						ddx/=scalingW;
						ddy/=scalingH;
						currentCloneRect.mX+=ddx;
						currentCloneRect.mY+=ddy;
					}
				}
			}
		}
		oldEvent = MotionEvent.obtain(event);
		return cutTheEvent;
	}

	private void doTouchAction(MotionEvent event,int lineType) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(currentSelectLineType!=lineType){
				if(!hasStartGame){
					currentSelectLineType=lineType;
					waitToChangeLineType=currentSelectLineType;
					initRects();
				}else{
					showMessage=true;
					waitToChangeLineType=lineType;
				}
			}else{
				if(hasStartGame){
					showMessage=true;
					waitToChangeLineType=currentSelectLineType;
				}
			}
		}
	}

	private void checkWinAndDoSomeThing(ArrayList<MRect> rectList) {
		if(rectList!=null){
			if(rectList.size()==0){
				hasWin=true;
				if(bestTime!=0){
					if(timeHasGo<bestTime){
						editor.putLong(""+currentSelectLineType, timeHasGo);
					}
				}else{
					editor.putLong(""+currentSelectLineType, timeHasGo);
				}
				editor.commit();
			}
		}
	}

	private int getTouchImageIndex(MotionEvent event) {
		int currentTouchIndex=-1;
		for (int i = 0; i < mRectList.size(); i++) {
			MRect curRect=mRectList.get(i);
			if(touchInTheRect(event, curRect.mX, curRect.mY, curRect.mWidth, curRect.mHeight)){
				currentTouchIndex=i;
				break;
			}
		}
		return currentTouchIndex;
	}

	private void moveRects(float totalDxOrDy,float dxOrdy) {
		for (int i = 0; i < mRectList.size(); i++) {
				if(Math.abs(totalDxOrDy)>=canScrollLength){
					if(totalDxOrDy>=0){
						moveToStart();
					}else{
						moveToEnd();
					}
					break;
				}else{
					mRectList.get(i).mX+=dxOrdy;
				}
		}
		
	}

	private void moveToEnd() {
		if(targetWidth<targetHeight){
			totalDy=-canScrollLength;
		}else{
			totalDx=-canScrollLength;
		}
		for (int i = 0; i < mRectList.size(); i++) {
			MRect curRect=mRectList.get(mRectList.size()-i-1);
			curRect.setXY(1024-(i+1)*(rectWidth+rectSpace), rectTopPadding);
		}
	}

	private void moveToStart() {
		if(targetWidth<targetHeight){
			totalDy=canScrollLength;
		}else{
			totalDx=canScrollLength;
		}
		for (int i = 0; i < mRectList.size(); i++) {
			MRect curRect=mRectList.get(i);
			curRect.setXY(i*(rectWidth+rectSpace), rectTopPadding);
		}
	}

	private void setRectsDrawPosition(ArrayList<MRect> rectList) {
		if(rectList!=null&&rectList.size()!=0){
			for (int i = 0; i < rectList.size(); i++) {
				MRect curRect=rectList.get(i);
				curRect.setXY((1024-rectWidth*rectList.size()-rectSpace*(rectList.size()-1))/2+i*(rectWidth+rectSpace), rectTopPadding);
			}
			if(rectList.get(0).mX<0){
				canScrollLength=-rectList.get(0).mX;
			}else{
				canScrollLength=0;
			}
		}
	}

	private ArrayList<MRect> clipBitMapWith(int currentSelectLineType, Bitmap testBitmap) {
		ArrayList<MRect> rects=new ArrayList<MRect>();
		switch (currentSelectLineType) {
		case 0x1001:
			horCount=3;
			verCount=2;
			break;
		case 0x1002:
			horCount=4;
			verCount=3;
			break;
		case 0x1003:
			horCount=6;
			verCount=4;
			break;
		default:
			break;
		}
		clipWidth=597.0f/horCount;
		clipHeight=437.0f/verCount;
		float subBitmapWidth=testBitmap.getWidth()/horCount;
		float subBitmapHeight=testBitmap.getHeight()/verCount;
		rectWidth=(int) (rectHeight*clipWidth/clipHeight);
		for (int i = 0; i < horCount; i++) {
			for (int j = 0; j < verCount; j++) {
				Bitmap bitmap = Bitmap.createBitmap(testBitmap, (int)(i*subBitmapWidth), (int)(j*subBitmapHeight),(int)subBitmapWidth, (int)subBitmapHeight);
				MRect curRect=new MRect(bitmap, i, j,rectWidth,rectHeight);
				rects.add(curRect);
			}
		}
		return rects;
	}

	private boolean touchInTheRect(MotionEvent event, float x, float y, float width,
			float height) {
		 float tx = event.getX()/scalingW-(targetWidth-bitmap.getWidth()*scalingW)/2; 
	     float ty = event.getY()/scalingH-(targetHeight-bitmap.getHeight()*scalingH)/2;
	     if(targetWidth<targetHeight){
	    	 tx = event.getY()/scalingW-(targetHeight-bitmap.getWidth()*scalingW)/2; 
		     ty = (targetWidth-event.getX())/scalingH-(targetWidth-bitmap.getHeight()*scalingH)/2;
	     }
			if (tx>x) {
				if (tx < x+width) {
					if (ty > y) {
						if (ty < y + height) {
							return true;
						}
					}
				}
			}
			return false;
	}
	class MRect {
		public float mX = 0;
		public float mY = 0;
		public float mWidth = 0;
		public float mHeight = 0;
		public int mIndexX;
		public int mIndexY;
		public Bitmap mImageBitmap;
		private MRect cloneOfwhom;
		public MRect(Bitmap bitmap, int indexX,int indexY,float width,float height) {
			mImageBitmap = bitmap;
			mIndexX = indexX;
			mIndexY = indexY;
			mWidth=width;
			mHeight=height;
		}
		public MRect cloneMe() {
			MRect rect=new MRect(mImageBitmap, mIndexX, mIndexY, mWidth, mHeight);
			rect.mX=mX;
			rect.mY=mY;
			rect.cloneOfwhom=this;
			return rect;
		}
		public void setXY(float x,float y){
			this.mX=x;
			this.mY=y;
		}
		public void drawMe(Canvas canvas, Paint paint) {
			if (mImageBitmap != null) {
				if(currentCloneRect==null||currentCloneRect.cloneOfwhom!=this){
					RectF rect = new RectF(mX,mY,mX+mWidth,mY+mHeight);
					canvas.drawBitmap(mImageBitmap, null,rect, paint);
				}
			}
		}
	}

	@Override
	public ComponentEntity getEntity() {
		return this.mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.mEntity = entity;
	}

	@Override
	public void load() {
		init();
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.FILL);// 设置非填充
		mPaint.setStrokeWidth(1);
		mPaint.setAntiAlias(true);// 锯齿不显示
		mPaint.setTextSize(40);
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub
		
	}
 
	@Override
	public void play() {
	}

	@Override
	public void stop() {
		recycleBitmaps(mRectList);
		recycleBitmaps(mRightPositionRects);
		recycleBitmap(backGroundBitmap);
		recycleBitmap(bitmap);
		recycleBitmap(cancelBitmap);
		recycleBitmap(sureBitmap);
		recycleBitmap(currentGoBitmap);
		recycleBitmap(currentOpBitmap);
		recycleBitmap(currentPpBitmap);
		recycleBitmap(currentRpBitmap);
		recycleBitmap(currentStarBitmap);
		recycleBitmap(finishBitmap);
		recycleBitmap(goBitmap1);
		recycleBitmap(goBitmap2);
		recycleBitmap(lineHBitmap);
		recycleBitmap(lineVBitmap);
		recycleBitmap(testBitmap);
		recycleBitmap(opBitmap1);
		recycleBitmap(opBitmap2);
		recycleBitmap(popupBitmap);
		recycleBitmap(ppBitmap1);
		recycleBitmap(ppBitmap2);
		recycleBitmap(rpBitmap1);
		recycleBitmap(rpBitmap2);
		recycleBitmap(star1Bitmap1);
		recycleBitmap(star2Bitmap2);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
}

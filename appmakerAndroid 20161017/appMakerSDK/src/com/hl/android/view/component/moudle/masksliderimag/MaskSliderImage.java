package com.hl.android.view.component.moudle.masksliderimag;

import java.io.InputStream;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Toast;

import com.hl.android.R;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MaskBean;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.inter.Component;
@SuppressLint("DrawAllocation")
public class MaskSliderImage extends View  implements Component,Animator.AnimatorListener,ScaleGestureDetector.OnScaleGestureListener{
	//在点击事件中松手的时候，如果移动举例超过这个，就需要进行页面跳转
	private static final int LIMIT_DIS = 50;
	
	private MoudleComponentEntity mEntity;
	private Context mContext;
	private Paint paint;
	//导航点的直径
	private int dotRadius = 8;
	private int mSelectIndex = 0;
	//判断页面大小的标识位
	private boolean isPortlet = true;
	
	
	MaskViewBean curMaskView;
	MaskViewBean prevMaskView;
	MaskViewBean nextMaskView;
	MaskViewBean recyleMaskView = null;
	
	private int initalWidth;
	private int initalHeight;
	private ViewCell cell;
	//当前maskview距离左边距的位置
	private int marginLeft = 0;
	MediaPlayer media = new MediaPlayer();
	private Bitmap closeBitmap;
//	private Bitmap shareBitmap;
	private RectF closeRect;
//	private RectF shareRect;
	private float parentWidth;
	private float parentHeight;
	
	private float cImageWidth;
	private float cImageHeight;
	private float cImageDrawX;
	private float cImageDrawY;
	ScaleGestureDetector mScaleGestureDetector;
	
	
	public MaskSliderImage(Context context, ComponentEntity entity) {
		super(context); 
		mContext = context;
		mEntity = (MoudleComponentEntity) entity;
		paint = new Paint();//笔刷
		paint.setAntiAlias(true);
		paint.setColor(Color.rgb(192, 192, 192));
		mScaleGestureDetector = new ScaleGestureDetector(context,this);
	}
	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	public float getCImageDrawX() {
		return cImageDrawX;
	}
	
	public void setCImageDrawX(float cImageDrawX) {
		this.cImageDrawX = cImageDrawX;
	}
	
	public float getCImageDrawY() {
		return cImageDrawY;
	}
	
	public void setCImageDrawY(float cImageDrawY) {
		this.cImageDrawY = cImageDrawY;
	}
	
	public float getCImageWidth() {
		return cImageWidth;
	}
	
	public void setCImageWidth(float cImageWidth) {
		this.cImageWidth = cImageWidth;
	}
	
	public float getCImageHeight() {
		return cImageHeight;
	}
	
	public void setCImageHeight(float cImageHeight) {
		this.cImageHeight = cImageHeight;
	}
	
	public void setParentWidth(float parentWidth) {
		this.parentWidth = parentWidth;
		if(cell==null){
			cell=(ViewCell) getParent();
		}
		cell.setLayoutParams(new MarginLayoutParams((int) parentWidth, cell.getLayoutParams().height));
	}
	
	public float getParentWidth() {
		return parentWidth;
	}
	
	public void setParentHeight(float parentHeight) {
		this.parentHeight = parentHeight;
		if(cell==null){
			cell=(ViewCell) getParent();
		}
		cell.setLayoutParams(new MarginLayoutParams(cell.getLayoutParams().width, (int) parentHeight));
	}
	
	public float getParentHeight() {
		return parentHeight;
	}
	
	public void setParentX(float parentX) {
		if(cell==null){
			cell=(ViewCell) getParent();
		}
		cell.setX(parentX);
	}
	
	public float getParentX() {
		if(cell==null){
			cell=(ViewCell) getParent();
		}
		return cell.getX();
	}
	
	public void setParentY(float parentY) {
		if(cell==null){
			cell=(ViewCell) getParent();
		}
		cell.setY(parentY);
	}
	
	public float getParentY() {
		if(cell==null){
			cell=(ViewCell) getParent();
		}
		return cell.getY();
	}
	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity = (MoudleComponentEntity) entity;
	}
	/**
	 * 初始化绘制视图的变量
	 * 1初始化设置curMaskView nextMaskView
	 * 2记录默认大小，当view进行变化的时候需要用到
	 */
	@Override
	public void load() {
		MaskBean maskBean = mEntity.maskBeanList.get(mSelectIndex);
		curMaskView = new MaskViewBean(mContext,maskBean);
		maskBean = mEntity.maskBeanList.get(mSelectIndex + 1);
		nextMaskView = new MaskViewBean(mContext,maskBean);
		
		initalWidth = getLayoutParams().width;
		initalHeight = getLayoutParams().height;
		mCurWidth=initalWidth;
		//绘制那两个图标
		closeBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.close_normal);
//		shareBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.share_normal);
		int right = BookSetting.BOOK_WIDTH - ScreenUtils.dip2px(mContext, 20);
		int top = ScreenUtils.dip2px(mContext, 20);
		int bottom = top + 44;
		int left = right - 44;
		closeRect = new RectF(left,top,right,bottom);
//		shareRect = new RectF(closeRect.left - 44,top, closeRect.right - 44,bottom);
		beforFullScreenX=mEntity.x;
		beforFullScreenY=mEntity.y;
	}

	@Override
	public void load(InputStream is) {
	}

	@Override
	public void play() {
	}
	/**
	 * 在stop中需要将音乐停止并且释放
	 */
	@Override
	public void stop() {
		try {
			if (null != media&&this.media.isPlaying()) {
				media.stop();
				media.release();
				media = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			media = null;
		}
	}

	@Override
	public void hide() {
	}
	@Override
	public void show() {
	}
	/**
	 * 播放音乐
	 */
	@Override
	public void resume() {
		try {
			media.start();
		} catch (Exception ex) {
		}
	}
	/**
	 * 暂停音乐
	 */
	@Override
	public void pause() {
		try {
			media.pause();
		} catch (Exception ex) {
		}
	}
	
	float eventX = 0f;
	float eventY = 0f;
	String moveDir = "next";//or prev
	private long DOWNTIME = 0;
	private float downX = 0;
	//是否正在操作，如果是true就说明正在放大或者缩小，这时候需要将背景弄成白色的
	private boolean isAction = false;
	//是否是本次点击事件
	private boolean isDown = false;

	private float beforFullScreenX;
	private float beforFullScreenY;

	private boolean isDoScaleToFullView=false;

	private int navigationHeight;

	private float oldDistance;
	private PointF mid=new PointF();

	private float mCurWidth;

	private int touchW;

	private float touchX;

	private int endPositionx;

	private float touchY;

	private int endPositiony;

	private boolean isdoAnimation;
	private boolean isdoAnimation1;


	
	//点击事件的处理，进行操作的控制
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		if(isAction){
//			isDown = false;
//			return true;
//		}
		if(isdoAnimation||isdoAnimation1)return true;
		mScaleGestureDetector.onTouchEvent(event);
		if(event.getPointerCount() > 1){
			isDown=false;
			if(event.getPointerCount()==2){
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDistance = (float) Math.sqrt((event.getX(0) - event.getX(1))
							* (event.getX(0) - event.getX(1))
							+ (event.getY(0) - event.getY(1))
							* (event.getY(0) - event.getY(1)));
					mid.set((event.getX(0)+event.getX(1))/2,(event.getY(0)+event.getY(1))/2);
					touchX=cell.getX();
					touchY=cell.getY();
					touchW=cell.getWidth();
					break;
				case MotionEvent.ACTION_MOVE:
					float newDistance;
					newDistance = (float) Math.sqrt((event.getX(0) - event.getX(1))
							* (event.getX(0) - event.getX(1))
							+ (event.getY(0) - event.getY(1))
							* (event.getY(0) - event.getY(1)));
							mCurWidth=(int) (mCurWidth*newDistance/oldDistance);
							cell.setLayoutParams(new MarginLayoutParams((int) mCurWidth,(int) (mCurWidth*initalHeight/initalWidth)));
							endPositionx=(int) (mid.x+touchX-mid.x/touchW*mCurWidth);
							endPositiony=(int) (mid.y+touchY-mid.y/touchW*mCurWidth);
							cell.setX(endPositionx);
							cell.setY(endPositiony);
						oldDistance=newDistance;
					break;
				}
			}
			return true;
		}
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			DOWNTIME = System.currentTimeMillis();
			eventX = event.getX();
			eventY = event.getY();
			downX = eventX;
			isDown = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if(!isDown) return true;
			if( eventX != 0 ){
				marginLeft += event.getX(0) - eventX;
				//如果是左边第一个，那么向左就不要滑动了
				if( mSelectIndex == 0 && marginLeft >= 0){
					marginLeft = 0;
					eventX = 0;
					return true;
				}else if( mSelectIndex == mEntity.maskBeanList.size()-1 && marginLeft <= 0){
					marginLeft = 0;
					eventX = 0;
					return true;
				}
				postInvalidate();
			}
			eventX = event.getX(0);
			eventY = event.getY(0);
			break;
		case MotionEvent.ACTION_UP:
			if(!isPortlet && closeRect.contains(event.getX(), event.getY())){
				doCloseAction();
				return true;
			}
//			else if(!isPortlet && shareRect.contains(event.getX(), event.getY())){
//				doShareAction();
//				return true;
//			}
			
			if(!isDown) return true;
			if(downX!=0 && System.currentTimeMillis() - DOWNTIME < 200 && Math.abs(downX - event.getX() ) < ScreenUtils.dip2px(mContext,5)){
				doMaskViewFullAction();
			}else{
				eventX = 0;
				doWrapMaskView();
				break;
			}
			isDown = false;
			downX = 0;
		}
		return true;
	}
	/**
	 * 执行关闭操作将视图缩小即可
	 */
	private void doCloseAction(){
		isPortlet = true;
		postInvalidate();
		try {
			if (null != media&&this.media.isPlaying()) {
				media.stop();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 执行共享操作
	 * 暂时不支持
	 */
	private void doShareAction(){
		Toast.makeText(mContext, "no share sdk", Toast.LENGTH_LONG).show();
	}
	/**
	 * 将视图变大的操作
	 */
	private void doMaskViewFullAction() {
		//如果已经是大视图就不需要执行如下的逻辑了
		if(!isPortlet)return;
		isPortlet = false;

//		curMaskView.playMedia(media);
//		float ratX = curMaskView.size[0] / getLayoutParams().width;
		PropertyValuesHolder parentwidth = PropertyValuesHolder.ofFloat(
				"parentWidth", BookSetting.BOOK_WIDTH);
		PropertyValuesHolder parentheight = PropertyValuesHolder.ofFloat(
				"parentHeight", BookSetting.BOOK_HEIGHT);
		PropertyValuesHolder parentx = PropertyValuesHolder.ofFloat(
				"parentX",  0);
		PropertyValuesHolder parenty = PropertyValuesHolder.ofFloat(
				"parentY",  0);
		PropertyValuesHolder cimageWidth = PropertyValuesHolder.ofFloat(
				"cImageWidth", BookSetting.BOOK_WIDTH);
		PropertyValuesHolder cimageHeight = PropertyValuesHolder.ofFloat(
				"cImageHeight", BookSetting.BOOK_HEIGHT-2*ScreenUtils.dip2px(mContext, 60));
		PropertyValuesHolder cimageDrawX = PropertyValuesHolder.ofFloat(
				"cImageDrawX",  0);
		PropertyValuesHolder cimageDrawY = PropertyValuesHolder.ofFloat(
				"cImageDrawY",  ScreenUtils.dip2px(mContext, 60));
		moveDir = "none";
		parentWidth=getLayoutParams().width;
		parentHeight=getLayoutParams().height;
		RectF mcrectF=getMaskRect(curMaskView, 0,navigationHeight);
		cImageDrawX=mcrectF.left;
		cImageDrawY=mcrectF.top;
		cImageWidth=mcrectF.right-mcrectF.left;
		cImageHeight=mcrectF.bottom-mcrectF.top;
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this,parentx,parenty,parentwidth,parentheight,cimageDrawX,cimageDrawY,cimageWidth,cimageHeight);
		animator.setDuration(600);
		animator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				isdoAnimation1=true;
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				isdoAnimation1=false;
				marginLeft = 0;
				isDoScaleToFullView=false;
				postInvalidate();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				isdoAnimation1=false;
			}
		});
		animator.start();
		isDoScaleToFullView=true;
		curMaskView.playMedia(media);
	}
	
	/**
	 * 将当前的maskview对齐边界
	 */
	private void doWrapMaskView() {
		ObjectAnimator anm = null;
		//松手的时候不能显示局部的cell，必须要调整到显示一个cell的整个视图
		if( marginLeft < -LIMIT_DIS){
			moveDir = "next";
			anm = ObjectAnimator.ofInt(this, "marginLeft",marginLeft, -getWidth());
		}else if( marginLeft > LIMIT_DIS){
			moveDir = "prev";
			anm = ObjectAnimator.ofInt(this, "marginLeft",marginLeft, getWidth());
		}else{
			moveDir = "none";
			anm = ObjectAnimator.ofInt(this, "marginLeft",0);
		}
		anm.addListener(this);
		anm.start();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		if(cell == null)cell = (ViewCell) getParent();
		if(isDoScaleToFullView){
			canvas.save();
			canvas.clipRect(new RectF(0, 0, getWidth(), getHeight()));
			canvas.drawBitmap(curMaskView.mBitmap, null, new RectF(cImageDrawX, cImageDrawY, cImageDrawX+cImageWidth, cImageDrawY+cImageHeight), null);
			canvas.restore();
		}else if(!isPortlet){
			drawFullView(canvas);
		}else{
			drawIconView(canvas);
		}
	}
	
	/**
	 * 绘制小窗口的视图
	 * @param canvas
	 */
	private void drawIconView(Canvas canvas){
		//计算rect
		//当前视图的绘制
		if(curMaskView == null)return;
		int mcOffsetX=marginLeft;
		int mpreOffsetX=mcOffsetX- getWidth();
		int mnextOffsetX=mcOffsetX+ getWidth();
		if(!isAction){
			MarginLayoutParams marginLp = (MarginLayoutParams) cell.getLayoutParams();
			marginLp.width = initalWidth;
			marginLp.height = initalHeight;
			
			marginLp.leftMargin = 0;
			marginLp.topMargin = 0;
			cell.requestLayout();
			cell.setX(beforFullScreenX);
			cell.setY(beforFullScreenY);
			//画个边框
			if(mEntity.isShowControllerPoint){
				drawNavigation(canvas);
				navigationHeight=dotRadius*4;
			}
			mCurWidth=initalWidth;
		}
		canvas.save();
		canvas.clipRect(new RectF(mcOffsetX, 0, mcOffsetX+getWidth(), getHeight()-navigationHeight));
		curMaskView.drawMaskView(canvas, getMaskRect(curMaskView, mcOffsetX,navigationHeight),isPortlet,false);
		canvas.restore();
		//绘制前一个视图
		if(prevMaskView != null ){
			canvas.save();
			canvas.clipRect(new RectF(mpreOffsetX, 0, mpreOffsetX+getWidth(), getHeight()-navigationHeight));
			prevMaskView.drawMaskView(canvas, getMaskRect(prevMaskView, mpreOffsetX,navigationHeight),isPortlet,false);
			canvas.restore();
		}
		//绘制下一个视图
		if(nextMaskView != null ){
			canvas.save();
			canvas.clipRect(new RectF(mnextOffsetX, 0, mnextOffsetX+getWidth(), getHeight()-navigationHeight));
			nextMaskView.drawMaskView(canvas, getMaskRect(nextMaskView, mnextOffsetX,navigationHeight),isPortlet,false);
			canvas.restore();
		}
		//要被回收的
		if(recyleMaskView != null ){
			recyleMaskView.recyle();
		}
	}
	
	private RectF getBigMaskRect(int px){ 
		return new RectF(px,0,px + getWidth(),getHeight()); 
	}
	/**
	 * 获得小视图时候的布局大小
	 * @param navigationHeight 
	 * @param px 偏移位置
	 * @return
	 */
	private RectF getMaskRect(MaskViewBean maskView,int offsetX, int navigationHeight){
		float xcRatio=getWidth()/maskView.mMaskBean.rectW;
		float ycRatio=(getHeight()-navigationHeight)/maskView.mMaskBean.rectH;
		RectF mcrectF=new RectF();
		mcrectF.left=-maskView.mMaskBean.rectX*xcRatio+offsetX;
		mcrectF.top=-maskView.mMaskBean.rectY*ycRatio;
		mcrectF.right=mcrectF.left+maskView.mBitmap.getWidth()*xcRatio;
		mcrectF.bottom=mcrectF.top+maskView.mBitmap.getHeight()*ycRatio;
		return mcrectF;
	}
	/**
	 * 绘制导航视图
	 * @param canvas
	 */
	private void drawNavigation(Canvas canvas){
		//计算导航点的第一个坐标
		//不能靠边，所以y坐标是3高度减去3被半径
		int y = getLayoutParams().height - dotRadius*3;
		int x = getLayoutParams().width/2;
		x -= (mEntity.maskBeanList.size()*3 - 1)*dotRadius/2;
		x += dotRadius;
		for(int i=0;i<mEntity.maskBeanList.size();i++){
			paint.setColor(Color.rgb(144, 144, 144));
			canvas.drawCircle(x, y, dotRadius, paint);
			canvas.save();
			paint.setColor(Color.WHITE);
			canvas.drawCircle(x, y, dotRadius - 1, paint);
			canvas.save();
			//将当前的展示的元素设置成实心的
			if(mSelectIndex == i){
				paint.setColor(Color.rgb(144, 144, 144));
				canvas.drawCircle(x, y, dotRadius - 3, paint);
				canvas.save();
			}
			x += 3*dotRadius;
		}
	}
	
	/**
	 * 绘制大窗口的视图
	 * @param canvas
	 */
	private void drawFullView(Canvas canvas){
		cell.setScaleX(1.0f);
		cell.setScaleY(1.0f);
		if(curMaskView == null)return;
		if(isAction){
			canvas.drawColor(Color.WHITE);
			RectF rectF=getBigMaskRect(0);
			rectF.top+=1.0f*ScreenUtils.dip2px(mContext, 60)/BookSetting.BOOK_HEIGHT*getHeight();
			rectF.bottom-=1.0f*ScreenUtils.dip2px(mContext, 60)/BookSetting.BOOK_HEIGHT*getHeight();
			canvas.drawBitmap(curMaskView.mBitmap, null,rectF, paint);
			return;
		}else{
			canvas.drawColor(Color.BLACK);
			//绘制那两个图标
			canvas.drawBitmap(closeBitmap, null, closeRect, null);
//			canvas.drawBitmap(shareBitmap, null, shareRect, null);
			ViewCell cell = (ViewCell) getParent();
			MarginLayoutParams marginLp = (MarginLayoutParams) cell.getLayoutParams();
			marginLp.width = BookSetting.BOOK_WIDTH;
			marginLp.height = BookSetting.BOOK_HEIGHT;
			cell.setLayoutParams(marginLp);
			cell.setX(0);
			cell.setY(0);
			mCurWidth=BookSetting.BOOK_WIDTH;
			//计算rect
		}
		
		//当前视图的绘制
		curMaskView.drawMaskView(canvas, getBigMaskRect(marginLeft),isPortlet,true);
		//绘制前一个视图
		if(prevMaskView != null ){
			prevMaskView.drawMaskView(canvas, getBigMaskRect(marginLeft - getWidth()),isPortlet,false);
		}
		//绘制下一个视图
		if(nextMaskView != null ){
			nextMaskView.drawMaskView(canvas, getBigMaskRect(marginLeft + getWidth()),isPortlet,false);
		}
		//要被回收的
		if(recyleMaskView != null ){
			recyleMaskView.recyle();
		}
		
		
	}
	//为了动画增加的一个方法，这样就可以在图片进行切换的时候有一个过渡效果
	public void setMarginLeft(int x){
		this.marginLeft = x;
		postInvalidate();
	}
	public int getMarginLeft(){
		return marginLeft;
	}
	//动画事件的方法
	@Override
	public void onAnimationCancel(Animator animation) {
		isdoAnimation=false;
	}
	@Override
	public void onAnimationEnd(Animator animation) {
		isdoAnimation=false;
		if("next".equals(moveDir)){
			if(mSelectIndex == mEntity.maskBeanList.size() - 1)return;
			recyleMaskView = prevMaskView;
			mSelectIndex++;
			prevMaskView = curMaskView;
			curMaskView = nextMaskView;
			if(mSelectIndex < mEntity.maskBeanList.size() - 1){
				MaskBean maskBean = mEntity.maskBeanList.get(mSelectIndex + 1);
				nextMaskView = new MaskViewBean(mContext,maskBean);
			}else{
				nextMaskView = null;
			}
			if(!isPortlet){
				curMaskView.playMedia(media);
			}
		}else if("prev".equals(moveDir)){
			if(mSelectIndex == 0)return;
			recyleMaskView = nextMaskView; 
			mSelectIndex--;
			nextMaskView = curMaskView;
			curMaskView = prevMaskView;
			if(mSelectIndex != 0){
				MaskBean maskBean = mEntity.maskBeanList.get(mSelectIndex - 1);
				prevMaskView = new MaskViewBean(mContext,maskBean);
			}else{
				prevMaskView = null;
			}		
			if(!isPortlet){
				curMaskView.playMedia(media);
			}
		}
		marginLeft = 0;
		Log.d("hl","anima end");
		isDoScaleToFullView=false;
		postInvalidate();
	}
	@Override
	public void onAnimationRepeat(Animator animation) {
	}
	@Override
	public void onAnimationStart(Animator animation) {
		isdoAnimation=true;
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector arg0) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		isAction=true;
		return true;
	}
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		if(isPortlet){
			if(cell.getWidth()>initalWidth){
				isPortlet = false;
				curMaskView.playMedia(media);
			}
		}else{
			if(cell.getWidth()<BookSetting.BOOK_WIDTH){
				isPortlet = true;
				pause();
			}
		}
		marginLeft = 0;
		isAction = false;
		Log.d("hl"," onScaleEnd");
		postInvalidate();
	}
	
//	@Override
//	public boolean onScale(ScaleGestureDetector detector) {
//		float scale = detector.getScaleFactor();
//		Log.d("wdy", "onscale：");
//		float onScaleWidth=widthBeforeScale*scale;
//		float onScaleHeight=heightBeforeScale*scale;
//		cell.setLayoutParams(new MarginLayoutParams((int)onScaleWidth,(int)onScaleHeight));
//		int endPositionx=(int) (detector.getFocusX()-(detector.getFocusX()-scaleTouchX)*onScaleWidth/widthBeforeScale);
//		int endPositiony=(int) (detector.getFocusY()-(detector.getFocusY()-scaleTouchY)*onScaleWidth/widthBeforeScale);
//		cell.setX(endPositionx);
//		cell.setY(endPositiony);
//		return false;
//	}
//	@Override
//	public boolean onScaleBegin(ScaleGestureDetector detector) {
//		Log.d("wdy", "onScaleBegin");
//		span = detector.getCurrentSpan();
//		widthBeforeScale=cell.getWidth();
//		heightBeforeScale=cell.getHeight();
//		scaleTouchX=cell.getX();
//		scaleTouchY=cell.getY();
//		isAction = true;
//		postInvalidate();
//		return true;
//	}
//	@Override
//	public void onScaleEnd(ScaleGestureDetector detector) {
//		Log.d("wdy", "onScaleEnd");
//		float curSpan = detector.getCurrentSpan();
//		if(curSpan > span){
//			isPortlet = false;
//			curMaskView.playMedia(media);
//		}else{
//			isPortlet = true;
//			pause();
//		}
//		marginLeft = 0;
//		isAction = false;
//		Log.d("hl"," onScaleEnd");
//		postInvalidate();
//	}
	 
	
}

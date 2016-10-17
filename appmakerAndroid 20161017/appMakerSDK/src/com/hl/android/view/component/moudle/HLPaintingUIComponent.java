package com.hl.android.view.component.moudle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hl.android.R;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.inter.Component;
/**
 * 绘图模板
 * @author hl
 * @version 1.0
 * @createed 2013-11-6
 */
@SuppressLint({ "ViewConstructor", "HandlerLeak", "DrawAllocation" })
public class HLPaintingUIComponent extends View implements Component{
	private Context mContext;
	private Canvas mCanvas;//绘制未缩放的图片
	private Bitmap bitmap;
	private Paint mPaint;
	private Paint paint4eraser;
	public static int targetWidth, targetHeight;
	//声明需要使用的bitmap
	// ===============================
	private Bitmap backGroundBitmap;//背景图片
	private Bitmap eraserBitmap;//背景图片
	private Canvas canvas4path;//绘制路径
	private MyPaint[] paints;
	private Bitmap testBitmap;//
	private Bitmap cleanBitmap;
	private Bitmap saveBitmap;
	private MyPaint eraser;
	
	private float scalingW;
	private float scalingH;
	private MyPaint curSelectPaint;
	private MyPaint lastSelectPaint;
	private int totalMove;
	private float preX;
	private float preY;
	private Path path;
	private String sdCardPath;
	private ComponentEntity mEntity;
	private String savePath;
	private Bitmap pathBitmap;
	private int COLOR_4_ERASER=-0x10010;
	private boolean shouldClipSaveBitmap;
	private Handler mHandler;
	private int SHOW_MESSAGE_SUCCESSED_TO_SAVE_BITMAP=0x10011;
	private boolean hasSetConfig;
//	private Paint textPaint;
	private int currentSelectPaintWidth=5;
	private RectF testRectf;
	private RectF backGroundRectf;
	private RectF clearRectf;
	private RectF saveRectf;
	private boolean hansTouchInPathRect;
	
	
	public HLPaintingUIComponent(Context context,ComponentEntity entity) {
		super(context);
		 this.mContext = context;
		 this.mEntity=entity;
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		testRectf=new RectF(192, 147, 192+652, 147+492);
		backGroundRectf=new RectF(0, 0, 1027, 768);
		clearRectf=new RectF(90, 680, 90+70, 680+67);
		saveRectf=new RectF(875, 680, 875+70, 680+67);
		
	}
	/**
	 * 设置缩放比例
	 */
	private void setConfig() {
		targetWidth=getLayoutParams().width;
		targetHeight=getLayoutParams().height;
		scalingW=targetWidth*1.0f/1027;
		scalingH=targetHeight*1.0f/768;
		if(targetWidth<targetHeight){
			scalingW=targetHeight*1.0f/1027;
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

	private void init() {
		bitmap = Bitmap.createBitmap(1027, 768, Config.ARGB_8888);
		pathBitmap=Bitmap.createBitmap(652,492, Config.ARGB_8888);
		canvas4path=new Canvas(pathBitmap);
		mCanvas=new Canvas(bitmap);
		backGroundBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.background);
		cleanBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.clean);
		saveBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.paintsave);
		eraserBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.eraser);
		testBitmap=getTestBitmap();
		if(testBitmap==null){
			testBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.test);
		}
		paints=new MyPaint[16];
		for (int i = 0; i < paints.length; i++) {
			Bitmap curBitmap=BitmapFactory.decodeResource(getResources(),i+R.drawable.paint_01);
			float x=0;
			float y=0;
			if(i<8){
				if(i==0){
					x=1027-96+5;
				}else{
					x=1027-96+30;
				}
				y=145+i*40;
			}else{
				x=-30;
				y=145+(i-8)*40;
			}
			paints[i]=new MyPaint(mContext, curBitmap, x, y);
			setColor4Paint(i);
		}
		eraser=new MyPaint(mContext, eraserBitmap, 1027-96+25, 580);
		eraser.setColor(COLOR_4_ERASER);
		curSelectPaint=paints[0];
		lastSelectPaint=curSelectPaint;
		if(path==null){
			path=new Path();
		}
		sdCardPath=Environment.getExternalStorageDirectory().getAbsolutePath();
		savePath=sdCardPath+File.separator+mContext.getPackageName()+File.separator+"HLPicture";
		File mfilepath=new File(savePath);
		if(!mfilepath.isDirectory()||!mfilepath.exists()){
			mfilepath.mkdirs();
		}
	}

	
	private void setColor4Paint(int i) {
		int color = 0;
		switch (i) {
		case 0:
			color = 0xff000000;
			break;
		case 1:
			color = 0xff932600;
			break;
		case 2:
			color = 0xffffff00;
			break;
		case 3:
			color = 0xff38ff00;
			break;
		case 4:
			color = 0xff008080;
			break;
		case 5:
			color = 0xffc40013;
			break;
		case 6:
			color = 0xff179617;
			break;
		case 7:
			color = 0xff43349b;
			break;
		case 8:
			color = 0xffb400ff;
			break;
		case 9:
			color = 0xffffb6c1;
			break;
		case 10:
			color = 0xff808080;
			break;
		case 11:
			color = 0xffd38400;
			break;
		case 12:
			color = 0xffbdb76b;
			break;
		case 13:
			color = 0xff007aff;
			break;
		case 14:
			color = 0xff511b06;
			break;
		case 15:
			color = 0xff00fff8;
			break;
		}
		paints[i].setColor(color);
	}


	private Bitmap getTestBitmap() {
		ArrayList<String> sourceIDS = ((MoudleComponentEntity) this.mEntity).getSourceIDList();
		return BitmapUtils.getBitMap(sourceIDS.get(0),mContext);
	}

	private void recycleBitmap(Bitmap bitmap) {
		if(bitmap!=null&&!bitmap.isRecycled()){
			bitmap.recycle();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		if(!hasSetConfig){
			setConfig();
			hasSetConfig=true;
		}
		myDraw(canvas);
		logic();
		invalidate();
	}
	
	private void myDraw(Canvas canvas) {
		if (null != canvas) {
			try {
				canvas.save();
				if(targetWidth<targetHeight){
					canvas.rotate(90,targetWidth/2.0f,targetHeight/2.0f);
				}
				canvas.drawColor(Color.WHITE);
				drawTheOriginalBitmap();
				RectF rectF=new RectF((targetWidth-bitmap.getWidth()*scalingW)/2, (targetHeight-bitmap.getHeight()*scalingH)/2, (targetWidth+bitmap.getWidth()*scalingW)/2, (targetHeight+bitmap.getHeight()*scalingH)/2);
				canvas.drawBitmap(bitmap, null, rectF, mPaint);
				canvas.restore();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != canvas) {
				}
			}
		}
	}

	private void drawTheOriginalBitmap() {
		mCanvas.drawColor(Color.WHITE);
		mCanvas.drawBitmap(backGroundBitmap, null, backGroundRectf, mPaint);
		mCanvas.drawBitmap(cleanBitmap,null,clearRectf, mPaint);
		mCanvas.drawBitmap(saveBitmap, null,saveRectf, mPaint);
		for (int i = 0; i < paints.length; i++) {
			paints[i].drawMe(mCanvas,mPaint);
		}
		eraser.drawMe(mCanvas,mPaint);
		if(path!=null&&!path.isEmpty()){
			mPaint.setColor(curSelectPaint.mClor);
			if(curSelectPaint!=eraser){
				canvas4path.drawPath(path, mPaint);
			}else{
				canvas4path.drawPath(path, paint4eraser);
			}
		}
		mCanvas.drawBitmap(pathBitmap,null,testRectf, mPaint);
		mCanvas.drawBitmap(testBitmap, null, testRectf, mPaint);
//		for (int i = 0; i < 10; i++) {
//			textPaint.setColor(Color.GRAY);
//			if((i+1)==currentSelectPaintWidth){
//				textPaint.setColor(0xff511b06);
//			}
//			mCanvas.drawText(i+1+"", 240+i*60, 670, textPaint);
//		}
	}

	private void logic() {
		if(shouldClipSaveBitmap){//写在logic中，保证每次保存的图片为绘制完整的图片。
			Bitmap clipSaveBitmap=Bitmap.createBitmap(bitmap, 192, 147, 652, 492);
			if(saveBitmap2file(clipSaveBitmap, savePath)){
				mHandler.sendEmptyMessage(SHOW_MESSAGE_SUCCESSED_TO_SAVE_BITMAP);
				mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+savePath))); 
			}
			shouldClipSaveBitmap=false;
			recycleBitmap(clipSaveBitmap);
		}
		if(lastSelectPaint!=curSelectPaint){
			if(lastSelectPaint.mPositionX<=0){
				lastSelectPaint.mPositionX-=5;
			}else{
				lastSelectPaint.mPositionX+=5;
			}
			if(curSelectPaint.mPositionX<=0){
				curSelectPaint.mPositionX+=5;
			}else{
				curSelectPaint.mPositionX-=5;
			}
			totalMove+=5;
			if(totalMove>=25){
				totalMove=0;
				lastSelectPaint=curSelectPaint;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean cutEvent = false;
		 float x = event.getX()/scalingW-(targetWidth-bitmap.getWidth()*scalingW)/2; 
	     float y = event.getY()/scalingH-(targetHeight-bitmap.getHeight()*scalingH)/2;
	     if(targetWidth<targetHeight){
	    	 x = event.getY()/scalingW-(targetHeight-bitmap.getWidth()*scalingW)/2; 
		     y = (targetWidth-event.getX())/scalingH-(targetWidth-bitmap.getHeight()*scalingH)/2;
	     }
	     x-=192;
	     y-=147;
		if(touchInTheRect(event, 875, 680, 65, 69)){
			if(event.getAction()==MotionEvent.ACTION_UP){
				shouldClipSaveBitmap=true;
			}
		}else if(touchInTheRect(event, 90, 680, 70, 67)){
			if(event.getAction()==MotionEvent.ACTION_UP){
				path.reset();
				canvas4path.drawColor(Color.WHITE,Mode.DST_OUT);//得到透明的bitmap
			}
		}else if (touchInTheRect(event, 192, 147, 649, 489)) {
			hansTouchInPathRect=true;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				cutEvent=true;
				 path.reset();
				 if(canLineTo(x,y))
				 path.moveTo(x, y); 
				 path.quadTo(x, y, x, y);
		         preX = x; 
		         preY = y; 
				break;
			case MotionEvent.ACTION_MOVE:
				 if(path.isEmpty()){
					 if(canLineTo(x,y))
					 path.moveTo(x, y);
					 path.quadTo(x, y, x, y);
				 }else{
					 if(canLineTo(x,y))
					 path.quadTo(preX, preY, (x+preX)/2, (y+preY)/2);
				 }
		         preX = x; 
		         preY = y; 
				break;
			case MotionEvent.ACTION_UP:
				if(canLineTo(x,y))
					path.quadTo(preX, preY, x, y);
				break;
			default:
				break;
			}
			
		} else {
			if(!path.isEmpty()){
				if(hansTouchInPathRect){
					if(canLineTo(x,y))
						path.quadTo(preX, preY, x, y);
						hansTouchInPathRect=false;
					
				}else{
					path.reset();
				}
			}
			if (totalMove == 0) {
				if (touchInTheRect(event, eraser.mPositionX, eraser.mPositionY,
						1027 - eraser.mPositionX, 79)) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						curSelectPaint = eraser;
					}
				} else {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						for (int i = 0; i < paints.length; i++) {
							if (i < 8) {
								if (touchInTheRect(event, paints[i].mPositionX,
										paints[i].mPositionY,
										1027 - paints[i].mPositionX, 29)) {
									curSelectPaint = paints[i];
									path.reset();
									break;
								}
							} else {
								if (touchInTheRect(event, 0,
										paints[i].mPositionY,
										paints[i].mPositionX + 96, 29)) {
									curSelectPaint = paints[i];
									path.reset();
									break;
								}
							}
						}
					}
				}
			}
//			if (event.getAction() == MotionEvent.ACTION_UP) {
//				for (int i = 0; i < 10; i++) {
//					if(touchInTheRect(event,240+i*60, 650, 30, 30)){
//						currentSelectPaintWidth=i+1;
//						mPaint.setStrokeWidth(currentSelectPaintWidth);
//						break;
//					}
//				}
//			}
		}
		return cutEvent;
		
	}

	private boolean canLineTo(float x, float y) {
		if(x==0&&y==0){
			return false;
		}else{
			return true;
		}
	}

	static boolean saveBitmap2file(Bitmap bmp,String filePath){ 
		CompressFormat format= Bitmap.CompressFormat.JPEG; 
		int quality = 100; 
		OutputStream stream = null;
		File file=new File(filePath+File.separator+"画图"+System.currentTimeMillis()+".jpg");
		try {
			stream = new FileOutputStream(file);
		}
		catch (Exception e) {
			e.printStackTrace(); 
		}
		return 	bmp.compress(format, quality, stream);
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
	public  class MyPaint{
		private Bitmap mBitmap;
		private float mPositionX;
		private float mPositionY;
		private int mClor;
		private RectF mrectf;
		public MyPaint(Context context,Bitmap bitmap,float x,float y){
			mContext=context;
			mBitmap=bitmap;
			mPositionX=x;
			mPositionY=y;
		}
		
		private void setColor(int color){
			mClor=color;
		}
		
		private void drawMe(Canvas canvas,Paint paint){
			if(mBitmap!=null){
				mrectf=new RectF(mPositionX, mPositionY, mPositionX+96, mPositionY+29);
				if(mClor==COLOR_4_ERASER){
					mrectf.bottom=mPositionY+79;
				}
				canvas.drawBitmap(mBitmap,null,mrectf,paint);
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
		mPaint=new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		currentSelectPaintWidth=((MoudleComponentEntity)mEntity).lineThick;
		if(currentSelectPaintWidth<=5){
			currentSelectPaintWidth=5;
		}
		mPaint.setStrokeWidth(currentSelectPaintWidth);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		paint4eraser=new Paint();
		paint4eraser.setAntiAlias(true);
		paint4eraser.setDither(true);
		paint4eraser.setStrokeWidth(10);
		paint4eraser.setStyle(Paint.Style.STROKE);
		paint4eraser.setStrokeJoin(Paint.Join.ROUND);
		paint4eraser.setStrokeCap(Paint.Cap.ROUND);
		paint4eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//		textPaint=new Paint();
//		textPaint.setAntiAlias(true);
//		textPaint.setTextSize(20);
//		textPaint.setStyle(Paint.Style.STROKE);
		
		mHandler=new Handler(){
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				if(msg.what==SHOW_MESSAGE_SUCCESSED_TO_SAVE_BITMAP){
					Toast.makeText(mContext, "Save picture success", Toast.LENGTH_SHORT).show();
				}
			}
		};
		init();
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
		recycleBitmap(backGroundBitmap);
		recycleBitmap(bitmap);
		recycleBitmap(testBitmap);
		recycleBitmap(cleanBitmap);
		recycleBitmap(eraserBitmap);
		recycleBitmap(saveBitmap);
		for (int i = 0; i < paints.length; i++) {
			recycleBitmap(paints[i].mBitmap);
		}
		recycleBitmap(eraser.mBitmap);
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

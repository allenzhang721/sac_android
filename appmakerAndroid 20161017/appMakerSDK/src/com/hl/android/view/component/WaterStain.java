package com.hl.android.view.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.hl.android.R;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.BitmapUtils;

public class WaterStain extends View{
	private Context mContext;
	private Bitmap mBitmap;
	private String mText;
	private Paint mPaint4Line;
	private Paint mPaint4text;
	private float textSize;
	private float textWidth;
	private float textHeight;
	private static float MAX_TEXT_SIZE=50;
	public WaterStain(Context context) {
		super(context);
		mContext=context;
		init();
	}
	
	private void init() {
		mBitmap=BitmapUtils.decodeResource(mContext, R.drawable.water_stain);
		//mText="appMaker制作 禁止用于商业用途 宏乐(北京)科技有限责任公司 版权所有  ";
		mText = HLSetting.BookMarkLabelText;
		Log.d("SunYongle", "水印值："+mText);
//		if(HLSetting.LOCATION.equals("TW")){
//			mText="Smart Apps Creator";
//		}
		mPaint4Line=new Paint();
		mPaint4Line.setAntiAlias(true);
		mPaint4Line.setColor(Color.WHITE);
		mPaint4text=new Paint(mPaint4Line);
		mPaint4Line.setAlpha(128);
		mPaint4text.setTextSize(textSize);
		mPaint4text.setAntiAlias(true);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		super.onDraw(canvas);
		
		if(HLSetting.IsShowBookMark){
			int bitmapPositionX=(getWidth()-mBitmap.getWidth())/2;
			int bitmapPositionY=(getHeight()-mBitmap.getHeight())/2;
			canvas.drawBitmap(mBitmap,bitmapPositionX,bitmapPositionY,null);
			canvas.drawLine(0, 0, bitmapPositionX, bitmapPositionY, mPaint4Line);
			canvas.drawLine(getWidth(), 0, bitmapPositionX+mBitmap.getWidth(), bitmapPositionY, mPaint4Line);
			canvas.drawLine(0, getHeight(), bitmapPositionX, bitmapPositionY+mBitmap.getHeight(), mPaint4Line);
			canvas.drawLine(getWidth(), getHeight(), bitmapPositionX+mBitmap.getWidth(), bitmapPositionY+mBitmap.getHeight(), mPaint4Line);
		}
		
		
		
		textSize=getWidth()*6/320;
		mPaint4text.setTextSize(textSize);
		textWidth=mPaint4text.measureText(mText);
		
		String labelPosition = HLSetting.BookMarkLablePositon;
		String[] position = labelPosition.split("\\|");
		for (int i = 0; i < position.length; i++) {
			Log.d("SunYongle", " position: "+position[i]);
        }
		
		String horPosition = position[0];
		String verPosition = position[1];
		
		Log.d("SunYongle", " xString: "+horPosition+"yString: "+verPosition);
		
		float horInt = 0;
		float verInt = 0;
		if(horPosition.equals("left"))
		{
			horInt = HLSetting.BookMarkLabelHorGap;
		}
		else if(horPosition.equals("center"))
		{
			horInt = (getWidth() - textWidth)/2 + HLSetting.BookMarkLabelHorGap;
		}
		else if(horPosition.equals("right"))
		{
			horInt = getWidth()- HLSetting.BookMarkLabelHorGap - textWidth;
		}
	
		if(verPosition.equals("top"))
		{
			verInt = HLSetting.BookMarkLabelVerGap;
		}
		else if(verPosition.equals("middle"))
		{
			verInt = (getHeight() - 8)/2 + HLSetting.BookMarkLabelVerGap;
		}
		else if(verPosition.equals("bottom"))
		{
			verInt = getHeight()- HLSetting.BookMarkLabelVerGap - 8;
		}
		
		Log.d("SunYongle", "水印： "+HLSetting.BookMarkLabelText+" x : "+horInt+"y: "+verInt);
		canvas.drawText(mText, horInt, verInt,mPaint4text);
		
//		if(!HLSetting.IsShowBookMark){
//			mPaint4text.setTextSize(getWidth()*6/320);
//			textWidth=mPaint4text.measureText(mText);
//			canvas.drawText(mText, 8, getHeight()-8,mPaint4text);
//		}else{
//			
//			textWidth=mPaint4text.measureText(mText);
//			while(textWidth>getWidth()/3.0f){
//				textSize-=1;
//				mPaint4text.setTextSize(textSize);
//				textWidth=mPaint4text.measureText(mText);
//				if(textWidth<=300){
//					break;
//				}
//			}
//			canvas.drawText(mText, (getWidth()-textWidth)/2.0f, getHeight()-8,mPaint4text);
//		}
	}
}

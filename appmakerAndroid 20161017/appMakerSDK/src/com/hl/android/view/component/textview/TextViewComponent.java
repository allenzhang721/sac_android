package com.hl.android.view.component.textview;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.TextComponentEntity;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.component.inter.Component;

/**
 * 文本组件
 * 
 * @author webcat
 * 
 */
public class TextViewComponent extends TextView implements Component {
	public TextComponentEntity mEntity = null;
	public AnimationSet animationset = null;
	private int textHeight;
	Vector<String> m_String = new Vector<String>();
	Vector<Float> m_LineWidth = new Vector<Float>();
	float m_iFontHeight;
	int m_iRealLine = 0;
	float x = 0;
	float y = 0;
	private TextPaint mPaint = null;

	public TextViewComponent(Context context) {
		super(context);
	}

	@SuppressLint("NewApi")
	public TextViewComponent(Context context, ComponentEntity entity) {
		super(context);
		this.setEntity(entity);
		this.mEntity = (TextComponentEntity) entity;
		int alpha = (int) (Float.valueOf(this.mEntity.getBgalpha()) * 255);
		String bgcolor = this.mEntity.getBgcolor();
		this.setBGAlphaAndColor(alpha, bgcolor);
		if(ScreenUtils.getAPILevel()>8){
			setOverScrollMode(TextView.OVER_SCROLL_NEVER);
		}
	}

	public void loadText() {

		mPaint = new TextPaint();
		setFontSize(mEntity.getFontSize());
	}

	public void drawView(float textSize) {
		String fontFamily = URLDecoder.decode(mEntity.getFontFamily());
		String fontWeight = URLDecoder.decode(mEntity.getFontWeight());

		Typeface typeface = null;

		if (fontFamily.equals("Times New Roman")) {
			AssetManager mgr = this.getContext().getAssets();
			typeface = Typeface.createFromAsset(mgr, "fonts/times.ttf");
			if (fontWeight.equals("normal")) {
				this.setTypeface(typeface, Typeface.NORMAL);
			} else if (fontWeight.equals("bold")) {
				this.setTypeface(typeface, Typeface.BOLD);
			}
		} else {
			if (fontWeight.equals("normal")) {
				typeface = Typeface.create(fontFamily, Typeface.NORMAL);
			} else if (fontWeight.equals("bold")) {
				typeface = Typeface.create(fontFamily, Typeface.BOLD);
			}
			this.setTypeface(typeface);
		}
		char ch;
		int w = 0;
		int istart = 0;

		mPaint.setAntiAlias(true);
		// We don't care about the result, just the side-effect of measuring.
		mPaint.measureText("H");

		try {
			String textColor = mEntity.getTextColor();
			textColor = URLDecoder.decode(textColor);
			String[] a = textColor.split(";");
			int color = Color.rgb(Integer.valueOf(a[0]), Integer.valueOf(a[1]),
					Integer.valueOf(a[2]));
			if (null == textColor || textColor.equals("")) {
				mPaint.setColor(Color.BLACK);
			} else {
				mPaint.setColor(color);
			}
		} catch (Exception ex) {
			mPaint.setColor(Color.BLACK);
		}

		// å­—ä½“ç›¸å…³
		mPaint.setTypeface(typeface);

		// mPaint.setTextSize(Float.valueOf(fontSize) *

		Context c = getContext();
		Resources r;

		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();

		textSize =ScreenUtils.getHorScreenValue(textSize);
		float factSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				textSize, r.getDisplayMetrics());

		mPaint.setTextSize(factSize);

		int m_iTextWidth = this.getLayoutParams().width;// æ–‡æœ¬çš„å®½åº¦
		FontMetrics fm = mPaint.getFontMetrics();
		float aa = fm.leading;
		m_iFontHeight = (float) (Math.ceil(fm.bottom - fm.top) + aa
				* Float.valueOf(mEntity.getLineHeight()));// è®¡ç®—å­—ä½“é«˜åº¦ï¼ˆå­—ä½“é«˜åº¦ï¼‹è¡Œé—´è·�ï¼‰

		x = 0;
		y = fm.descent - fm.ascent;
		float lineWidth = 0;

		String textContent = mEntity.getTotalParaTextContent();

		String string = textContent;
		try {
			string = URLDecoder.decode(textContent);
		} catch (Exception e) {
			Log.e("hl", "解码字符内容error");
		}

		m_iRealLine = 0;
		for (int i = 0; i < string.length(); i++) {
			ch = string.charAt(i);
			float[] widths = new float[1];
			String srt = String.valueOf(ch);
			mPaint.getTextWidths(srt, widths);
			if (ch == '\n') {
				m_iRealLine++;
				m_String.addElement(string.substring(istart, i));
				istart = i + 1;
				w = 0;
				String textAlign = mEntity.getTextAlign();
				if (!StringUtils.isEmpty(textAlign)) {
					textAlign = URLDecoder.decode(mEntity.getTextAlign());
					if ("center".equals(textAlign)) {
						m_LineWidth.add(lineWidth);
						lineWidth = 0;
						continue;
					}
				}
				lineWidth = -1;
				m_LineWidth.add(lineWidth);
				lineWidth = 0;
			} else {
				w += (int) (Math.ceil(widths[0]));
				if (w > m_iTextWidth) {
					m_iRealLine++;
					m_String.addElement(string.substring(istart, i));
					istart = i;
					i--;
					w = 0;
					m_LineWidth.add(lineWidth);
					lineWidth = 0;
				} else {

					lineWidth = lineWidth + widths[0];
					if (i == (string.length() - 1)) {
						m_iRealLine++;
						m_String.addElement(string.substring(istart,
								string.length()));

						String textAlign = mEntity.getTextAlign();
						if (!StringUtils.isEmpty(textAlign)) {
							textAlign = URLDecoder.decode(mEntity
									.getTextAlign());
							if ("center".equals(textAlign)) {
								m_LineWidth.add(lineWidth);
								lineWidth = 0;
								continue;
							}
						}

						lineWidth = -1;
						m_LineWidth.add(lineWidth);
						lineWidth = 0;
					}
				}

			}
		}
		this.setTextHeight((int) (m_iFontHeight * m_iRealLine));
	}

	@Override
	public ComponentEntity getEntity() {
		return this.mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.mEntity = (TextComponentEntity) entity;
	}

	@Override
	public void load() {

	}

	private void setBGAlphaAndColor(int alpha, String color) {
		String bgcolor = URLDecoder.decode(color);
		String[] a = bgcolor.split(";");
		this.setBackgroundColor(Color.argb(alpha, Integer.valueOf(a[0]),
				Integer.valueOf(a[1]), Integer.valueOf(a[2]))); // èƒŒæ™¯é€�æ˜Žåº¦
	}

	Paint paint = new Paint();

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		// å°†è¾¹æ¡†è®¾ä¸ºé»‘è‰²
		String a[]=mEntity.getBorderColor().split(";");
		paint.setColor(Color.argb(255, Integer.parseInt(a[0]),Integer.parseInt(a[1]),Integer.parseInt(a[2])));
		// ç”»TextViewçš„4ä¸ªè¾¹
		canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
		canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint);
		canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1,
				this.getHeight() - 1, paint);
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1,
				this.getHeight() - 1, paint);

		drawText(canvas);

		// String textContent = en.getTotalParaTextContent();
		// String string = URLDecoder.decode(textContent);
		// StaticLayout layout = new StaticLayout(string,(TextPaint)
		// mPaint,this.getLayoutParams().width,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
		// layout.draw(canvas);
	}

	public void setFontSize(String fontSize) {
		mPaint.reset();
		m_String.clear();
		m_LineWidth.clear();
		float mFontSize = Float.valueOf(URLDecoder.decode(fontSize));
		// loadText();
		drawView(mFontSize);
		// postInvalidate();
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}
 
	@Override
	public void play() {

	}

	protected void drawText(Canvas canvas) {
		// æœ€åˆ�çš„ä»£ç �
		// for (int i = 0, j = 0; i < m_iRealLine; i++, j++) {
		// // canvas.drawText((String) (m_String.elementAt(i)), x, y
		// // + m_iFontHeight * j, mPaint);
		// drawLine((String) (m_String.elementAt(i)),x,y + m_iFontHeight *
		// j,canvas);
		// }
		//
		// ä¸­æ–‡
		for (int i = 0, j = 0; i < m_iRealLine; i++, j++) {

			String textAlign = mEntity.getTextAlign();
			if (!StringUtils.isEmpty(textAlign)) {
				textAlign = URLDecoder.decode(mEntity.getTextAlign());
				if ("center".equals(textAlign)) {
					float beginX = this.getLayoutParams().width
							- this.m_LineWidth.elementAt(i);
					beginX = beginX / 2;
					canvas.drawText((String) (m_String.elementAt(i)), beginX, y
							+ m_iFontHeight * j, mPaint);
					continue;
				}
			}

			if (this.m_LineWidth.elementAt(i) < 0) {
				canvas.drawText((String) (m_String.elementAt(i)), x, y
						+ m_iFontHeight * j, mPaint);
			} else {
				drawLine((String) (m_String.elementAt(i)), x, y + m_iFontHeight
						* j, canvas, i);
			}
		}

		// // è‹±æ–‡
		// for (int i = 0, j = 0; i < m_iRealLine; i++, j++) {
		//
		// drawLineEN((String) (m_String.elementAt(i)), x, y + m_iFontHeight
		// * j, canvas, i);
		//
		// }
	}

	private void drawLine(String line, float x, float y, Canvas canvas,
			int rowNum) {

		float a = (this.getLayoutParams().width - this.m_LineWidth
				.elementAt(rowNum)) / line.length();
		String s = "";
		float lineWidth = 0;
		for (int i = 0; i < line.length(); i++) {
			s = line.substring(i, i + 1);
			float[] widths = new float[1];
			mPaint.getTextWidths(s, widths);
			if (i == 0) {
				canvas.drawText(s, x, y, mPaint);
			} else {

				canvas.drawText(s, x + lineWidth + a, y, mPaint);
			}
			lineWidth = lineWidth + widths[0] + a;
		}
	}

	/**
	 * é€šè¿‡è®¡ç®—è¡Œçš„æ€»spaceæ•°ï¼Œç„¶å�Žå¹³å�‡åˆ†é…�åˆ°æ¯�ä¸ªå�•è¯�çš„å�Žé�
	 * ¢ï¼Œé‡�æ–°ç»„è£…æ–‡æœ¬è¡Œ åœ¨è¿›è¡Œç»˜åˆ¶
	 * 
	 * @param line
	 * @param x
	 * @param y
	 * @param canvas
	 * @param rowNum
	 */
	@SuppressWarnings("unused")
	private void drawLineEN(String line, float x, float y, Canvas canvas,
			int rowNum) {
		float a = (this.getLayoutParams().width - measureLineENWidth(line));

		float[] widths = new float[1];
		mPaint.getTextWidths(" ", widths);

		int aaa = (int) (a / widths[0]);

		String ssss = getSpace(aaa);
		String[] aS = line.split(" ");
		String lineNew = "";
		int lineSpaceCount = ssss.length();
		if (aS.length > 1) {
			int aaaa = lineSpaceCount / (aS.length - 1);
			for (int i = 0; i < aS.length; i++) {
				lineNew = lineNew + aS[i];
				if (i == aS.length - 1) {

				} else {
					lineNew = lineNew + this.getSpace(aaaa);
				}

			}
			canvas.drawText(lineNew, x, y, mPaint);
			return;
		}

		canvas.drawText(line, x, y, mPaint);
	}

	private String getSpace(int aaa) {
		String aa = "";
		for (int i = 0; i < aaa; i++) {
			aa = aa + " ";
		}

		return aa;
	}

	private float measureLineENWidth(String line) {
		String s = "";
		float lineWidth = 0;
		line = line.replaceAll(" ", "");
		for (int i = 0; i < line.length(); i++) {
			s = line.substring(i, i + 1);
			float[] widths = new float[1];
			mPaint.getTextWidths(s, widths);
			lineWidth = lineWidth + widths[0];
		}

		return lineWidth;
	}

	// public void onDraw(Canvas canvas)
	// {
	// int MARGIN = 1;
	// int BORDER_WIDTH = 1;
	//
	// Paint p = new Paint();
	// p.setAntiAlias(true);
	// p.setTextSize(12);
	// p.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
	//
	// RectF rect = getRect();
	//
	// float maxWidth = rect.width() - MARGIN - BORDER_WIDTH * 2;
	//
	// String str = getText();
	// char[] chars = str.toCharArray();
	// int nextPos = p.breakText(chars, 0, chars.length, maxWidth, null);
	// str = str.substring(0, nextPos);
	//
	// float textX = MARGIN + BORDER_WIDTH;
	// float textY = (float) (Math.abs(p.getFontMetrics().ascent) + BORDER_WIDTH
	// + MARGIN);
	//
	// canvas.drawText(str, textX, textY, p);
	//
	// p.setStrokeWidth(BORDER_WIDTH);
	// p.setStyle(Style.STROKE);
	//
	// canvas.drawRect(rect, p);
	// }

	/**
	 * æ˜¯å�¦å…¨éƒ¨æ˜¯ä¸­æ–‡å­—ç¬¦
	 * 
	 * @param line
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isAllCNAndEN(String line) {
		if (line.length() < line.getBytes().length) {
			return true;// å…¨éƒ¨
		}

		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

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

	public int getTextHeight() {
		return textHeight;
	}

	public void setTextHeight(int textHeight) {
		this.textHeight = textHeight;
	}
}

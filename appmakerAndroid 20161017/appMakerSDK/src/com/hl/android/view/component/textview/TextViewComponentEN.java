package com.hl.android.view.component.textview;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
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
import android.view.Gravity;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.TextComponentEntity;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.view.component.inter.Component;

/**
 * æ˜¾ç¤ºå¯Œæ–‡æœ¬
 * 
 * @author webcat
 * 
 */
public class TextViewComponentEN extends TextView implements Component {
	public ComponentEntity entity = null;
	public AnimationSet animationset = null;
	private int textHeight;
	Vector<String> m_String = new Vector<String>();
	Vector<Float> m_LineWidth = new Vector<Float>();
	float m_iFontHeight;
	int m_iRealLine = 0;
	float x = 0;
	float y = 0;
	private TextPaint mPaint = null;
	ArrayList<Integer> alLast = new ArrayList<Integer>();

	public TextViewComponentEN(Context context) {
		super(context);
	}

	public TextViewComponentEN(Context context, ComponentEntity entity) {
		super(context);
		this.setEntity(entity);
		TextComponentEntity en = (TextComponentEntity) this.entity;
		int alpha = (int) (Float.valueOf(en.getBgalpha()) * 255);
		String bgcolor = en.getBgcolor();
		this.setBGAlphaAndColor(alpha, bgcolor);
	}

	public void loadEnglishText() {
		TextComponentEntity en = (TextComponentEntity) this.entity;
		String textContent = en.getTotalParaTextContent();
		String string = URLDecoder.decode(textContent);
		Log.d("hl","en text is  |||||       " + string);
		
		String fontFamily = URLDecoder.decode(en.getFontFamily());
		String fontWeight = URLDecoder.decode(en.getFontWeight());
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

		mPaint = new TextPaint();

		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		// We don't care about the result, just the side-effect of measuring.
		mPaint.measureText("H");

		try {
			String textColor = en.getTextColor();
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

		float fontSize = Float.valueOf(URLDecoder.decode(en.getFontSize()));
		// mPaint.setTextSize(Float.valueOf(fontSize) *

		Context c = getContext();
		Resources r;

		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();

		fontSize =ScreenUtils.getHorScreenValue(fontSize);
		float factSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				fontSize, r.getDisplayMetrics());

		mPaint.setTextSize(factSize);
		FontMetrics fm = mPaint.getFontMetrics();
		float aa = fm.leading;
		m_iFontHeight = (float) (Math.ceil(fm.bottom - fm.top) + aa
				* Float.valueOf(en.getLineHeight()));// è®¡ç®—å­—ä½“é«˜åº¦ï¼ˆå­—ä½“é«˜åº¦ï¼‹è¡Œé—´è·�ï¼‰
		y = fm.descent - fm.ascent;
		this.justifyText(string, this.getLayoutParams().width);
		
		this.setTextHeight((int) (m_iFontHeight * m_iRealLine));
	}

	@Override
	public ComponentEntity getEntity() {
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;
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
		TextComponentEntity en = (TextComponentEntity) this.entity;
		// å°†è¾¹æ¡†è®¾ä¸ºé»‘è‰²
		String a[]=en.getBorderColor().split(";");
		paint.setColor(Color.argb(255, Integer.parseInt(a[0]),Integer.parseInt(a[1]),Integer.parseInt(a[2])));
		// ç”»TextViewçš„4ä¸ªè¾¹
		canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
		canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint);
		canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1,
				this.getHeight() - 1, paint);
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1,
				this.getHeight() - 1, paint);

		drawText(canvas);
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}
 

	@Override
	public void play() {

	}

	protected void drawText(Canvas canvas) {
		for (int i = 0, j = 0; i < m_iRealLine; i++, j++) {
			drawLineEN((String) (m_String.elementAt(i)), x, y + m_iFontHeight
					* j, canvas, i);
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
	private void drawLineEN(String line, float x, float y, Canvas canvas,
			int rowNum) {
		canvas.drawText(line, x, y, mPaint);
	}

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

	private static String wrap(String s, float width, Paint p) {
		String[] str = s.split("\\s"); // regex
		StringBuilder smb = new StringBuilder(); // save memory
		smb.append(SYSTEM_NEWLINE);
		for (int x = 0; x < str.length; x++) {
			float length = p.measureText(str[x]);
			String[] pieces = smb.toString().split(SYSTEM_NEWLINE);
			try {
				if (p.measureText(pieces[pieces.length - 1]) + length > width)
					smb.append(SYSTEM_NEWLINE);
			} catch (Exception e) {
			}
			smb.append(str[x] + " ");
		}
		return smb.toString().replaceFirst(SYSTEM_NEWLINE, "");
	}

	private static String removeLast(String s, String g) {
		if (s.contains(g)) {
			int index = s.lastIndexOf(g);
			int indexEnd = index + g.length();
			if (index == 0)
				return s.substring(1);
			else if (index == s.length() - 1)
				return s.substring(0, index);
			else
				return s.substring(0, index) + s.substring(indexEnd);
		}
		return s;
	}

	final static String SYSTEM_NEWLINE = "\n";
	final static float COMPLEXITY = 0f; // Reducing this will increase
											// efficiency but will decrease
											// effectiveness

	public void justifyText(final String text, final float origWidth) {
		String s = (String) text;
		String[] splits = s.split(SYSTEM_NEWLINE);
		float width = origWidth - 5;
		for (int x = 0; x < splits.length; x++)
			if (mPaint.measureText(splits[x]) > width) {
				splits[x] = wrap(splits[x], width, mPaint);
				String[] microSplits = splits[x].split(SYSTEM_NEWLINE);
				for (int y = 0; y < microSplits.length - 1; y++) {
					microSplits[y] = justify(removeLast(microSplits[y], " "),
							width, mPaint);
					m_String.add(microSplits[y]);
					m_iRealLine++;
				}
			
				if (microSplits.length>2){
					m_String.add(microSplits[microSplits.length - 1]);
					m_iRealLine++;
				}
			}else{
				m_String.add(splits[x]);
				m_iRealLine++;
			}
		final StringBuilder smb = new StringBuilder();
		for (String cleaned : splits)
			smb.append(cleaned + SYSTEM_NEWLINE);
		this.setGravity(Gravity.LEFT);
		// this.setText(smb);
	}

	private String justifyOperation(String s, float width, Paint p) {
		float holder = (float) (COMPLEXITY * Math.random());
		while (s.contains(Float.toString(holder)))
			holder = (float) (COMPLEXITY * Math.random());
		String holder_string = Float.toString(holder);
		float lessThan = width;
		int timeOut = 100;
		int current = 0;
		while (p.measureText(s) < lessThan && current < timeOut) {
			s = s.replaceFirst(" ([^" + holder_string + "])", " "
					+ holder_string + "$1");
			lessThan = p.measureText(holder_string) + lessThan
					- p.measureText(" ");
			current++;
		}
		String cleaned = s.replaceAll(holder_string, " ");
		return cleaned;
	}

	private String justify(String s, float width, Paint p) {
		while (p.measureText(s) < width) {
			if (s.contains(" ")==false){
				s = " " + s;
			}
			s = justifyOperation(s, width, p);
		}
		return s;
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

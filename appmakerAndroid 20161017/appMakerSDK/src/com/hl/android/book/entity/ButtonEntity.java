package com.hl.android.book.entity;

public class ButtonEntity {
	public static String HOME_PAGE_BTN = "home_page_btn";
	public static String PRE_PAGE_BTN = "pre_page_btn";
	public static String NEXT_PAGE_BTN = "next_page_btn";
	public static String OPEN_NAVIGATE_BTN = "open_navigate_btn";
	
	public static String VER_HOME_PAGE_BTN = "ver_home_page_btn";
	public static String VER_PRE_PAGE_BTN = "ver_pre_page_btn";
	public static String VER_NEXT_PAGE_BTN = "ver_next_page_btn";
	public static String VER_OPEN_NAVIGATE_BTN = "ver_open_navigate_btn";
	private float x;
	private float y;
	private int width;
	private int height;
	private String type;
	private boolean isVisible;
	private String source;
	private String selectedSource;
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSelectedSource() {
		return selectedSource;
	}

	public void setSelectedSource(String selectedSource) {
		this.selectedSource = selectedSource;
	}
}

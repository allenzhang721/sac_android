package com.hl.android.book.entity;

public class BookInfoEntity {
	public String id;
	public String name;
	public String backgroundMusicId;
	public String bookType;
	public String deviceType;
	public String description;
	public String bookIconId;
	public String bookFlipType;
	public String homePageID;
	private double startPageTime;
//	private String bookFileName = "";
	public int adType = -1;
	public String position="top"; 

	public int bookWidth = 0;
	public int bookHeight = 0;
	public String bookNavType;
	public boolean isFree=true;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBackgroundMusicId() {
		return backgroundMusicId;
	}
	public void setBackgroundMusicId(String backgroundMusicId) {
		this.backgroundMusicId = backgroundMusicId;
	}
	public String getBookType() {
		return bookType;
	}
	public void setBookType(String bookType) {
		this.bookType = bookType;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getBookIconId() {
		return bookIconId;
	}
	public void setBookIconId(String bookIconId) {
		this.bookIconId = bookIconId;
	}
	public String getHomePageID() {
		return homePageID;
	}
	public void setHomePageID(String homePageID) {
		this.homePageID = homePageID;
	}
	public double getStartPageTime() {
		return startPageTime;
	}
	public void setStartPageTime(double startPageTime) {
		this.startPageTime = startPageTime;
	}
	public String getBookNavType() {
		return bookNavType;
	}

	public void setBookNavType(String bookNavType) {
		this.bookNavType = bookNavType;
	}
	
	
}

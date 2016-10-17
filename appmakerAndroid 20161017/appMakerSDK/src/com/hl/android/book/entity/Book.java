package com.hl.android.book.entity;

import java.util.ArrayList;

public class Book {
	private BookInfoEntity bookInfo;
	private ArrayList<String> pages;
	private ArrayList<SectionEntity> sections;
	private String startPageID;
	private ArrayList<SnapshotEntity> snapshots;
	private ArrayList<ButtonEntity> buttons;
	public Book(){
		pages = new ArrayList<String>();
		sections = new ArrayList<SectionEntity>();
		snapshots = new ArrayList<SnapshotEntity>();
		bookInfo = new BookInfoEntity();
		buttons = new ArrayList<ButtonEntity>();
	}
	public BookInfoEntity getBookInfo() {
		return bookInfo;
	}

	public void setBookInfo(BookInfoEntity bookInfo) {
		this.bookInfo = bookInfo;
	}

	public ArrayList<String> getPages() {
		return pages;
	}

	public void setPages(ArrayList<String> pages) {
		this.pages = pages;
	}
	public String getStartPageID() {
		return startPageID;
	}
	public void setStartPageID(String startPageID) {
		this.startPageID = startPageID;
	}
	public ArrayList<SectionEntity> getSections() {
		return sections;
	}
	public void setSections(ArrayList<SectionEntity> sections) {
		this.sections = sections;
	}
	public ArrayList<SnapshotEntity> getSnapshots() {
		return snapshots;
	}
	public void setSnapshots(ArrayList<SnapshotEntity> snapshots) {
		this.snapshots = snapshots;
	}
	public ArrayList<ButtonEntity> getButtons() {
		return buttons;
	}
	public void setButtons(ArrayList<ButtonEntity> buttons) {
		this.buttons = buttons;
	}
	
	public String getSnapshotIdByPageId(String pageID) {
		for (SnapshotEntity entity : snapshots) {
			if (entity.pageID.equals(pageID)) {
				return entity.id;
			}
		}
		return "";
	}

}

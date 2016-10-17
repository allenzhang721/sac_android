package com.hl.android.book.entity;

import java.util.ArrayList;

import com.hl.android.book.BookDecoder;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;

public class SectionEntity {
	public String ID;
	public String Name;
	public ArrayList<String> pages;
	public String lastPageID;
	public String bookID;
	public boolean isResourceSD;
	public boolean isShelves;
	public String bookPath; 
	BookDecoder bookDecoder;
	public ArrayList<ButtonEntity> buttons;
	
	public SectionEntity() {
		pages = new ArrayList<String>();
		isResourceSD = HLSetting.IsResourceSD;
		bookPath = BookSetting.BOOK_PATH;
		isShelves = BookSetting.IS_SHELVES_COMPONENT;
		if(isShelves){
			try{
				lastPageID = BookController.getInstance().getViewPage().getEntity().getID();
			}catch(Exception e){
				
			}
		}
		buttons = BookSetting.buttons;
		bookDecoder = BookDecoder.bookDecoder;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public ArrayList<String> getPages() {
		return pages;
	}

	public void setPages(ArrayList<String> pages) {
		this.pages = pages;
	}

}

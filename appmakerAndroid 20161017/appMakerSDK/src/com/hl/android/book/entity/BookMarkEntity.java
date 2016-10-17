package com.hl.android.book.entity;

public class BookMarkEntity {
	String IsShowBookMark;
	String IsShowBookMarkLabel;
	String BookMarkLablePositon;
	String BookMarkLabelHorGap;
	String BookMarkLabelVerGap;
	String BookMarkLabelText;

	public String getIsShowBookMark() {
		return IsShowBookMark;
	}

	public void setIsShowBookMark(String isShowBookMark) {
		IsShowBookMark = isShowBookMark;
	}

	public String getIsShowBookMarkLabel() {
		return IsShowBookMarkLabel;
	}

	public void setIsShowBookMarkLabel(String isShowBookMarkLabel) {
		IsShowBookMarkLabel = isShowBookMarkLabel;
	}

	public String getBookMarkLablePositon() {
		return BookMarkLablePositon;
	}

	public void setBookMarkLablePositon(String bookMarkLablePositon) {
		BookMarkLablePositon = bookMarkLablePositon;
	}

	public String getBookMarkLabelHorGap() {
		return BookMarkLabelHorGap;
	}

	public void setBookMarkLabelHorGap(String bookMarkLabelHorGap) {
		BookMarkLabelHorGap = bookMarkLabelHorGap;
	}

	public String getBookMarkLabelVerGap() {
		return BookMarkLabelVerGap;
	}

	public void setBookMarkLabelVerGap(String bookMarkLabelVerGap) {
		BookMarkLabelVerGap = bookMarkLabelVerGap;
	}

	public String getBookMarkLabelText() {
		return BookMarkLabelText;
	}

	public void setBookMarkLabelText(String bookMarkLabelText) {
		BookMarkLabelText = bookMarkLabelText;
	}

	public BookMarkEntity(String isShowBookMark, String isShowBookMarkLabel,
			String bookMarkLablePositon, String bookMarkLabelHorGap,
			String bookMarkLabelVerGap, String bookMarkLabelText) {
		super();
		IsShowBookMark = isShowBookMark;
		IsShowBookMarkLabel = isShowBookMarkLabel;
		BookMarkLablePositon = bookMarkLablePositon;
		BookMarkLabelHorGap = bookMarkLabelHorGap;
		BookMarkLabelVerGap = bookMarkLabelVerGap;
		BookMarkLabelText = bookMarkLabelText;
	}

	public BookMarkEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}

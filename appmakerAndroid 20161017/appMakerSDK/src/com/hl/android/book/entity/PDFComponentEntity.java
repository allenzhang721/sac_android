package com.hl.android.book.entity;

public class PDFComponentEntity extends ComponentEntity {
	private String localSourceID;
	private String pdfSourceID;
	private String pdfPageIndex;
	private String intailWidth;
	private String intailHeight;
	private String isAllowUserZoom;
	public String getLocalSourceID() {
		return localSourceID;
	}
	public void setLocalSourceID(String localSourceID) {
		this.localSourceID = localSourceID;
	}
	public String getPdfSourceID() {
		return pdfSourceID;
	}
	public void setPdfSourceID(String pdfSourceID) {
		this.pdfSourceID = pdfSourceID;
	}
	public String getPdfPageIndex() {
		return pdfPageIndex;
	}
	public void setPdfPageIndex(String pdfPageIndex) {
		this.pdfPageIndex = pdfPageIndex;
	}
	public String getIntailWidth() {
		return intailWidth;
	}
	public void setIntailWidth(String intailWidth) {
		this.intailWidth = intailWidth;
	}
	public String getIntailHeight() {
		return intailHeight;
	}
	public void setIntailHeight(String intailHeight) {
		this.intailHeight = intailHeight;
	}
	public String getIsAllowUserZoom() {
		return isAllowUserZoom;
	}
	public void setIsAllowUserZoom(String isAllowUserZoom) {
		this.isAllowUserZoom = isAllowUserZoom;
	}
	
}

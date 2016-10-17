package com.hl.android.book.entity;
/**
 * 视频对象类
 * @author hl
 * @version 1.0
 * @createed 2013-12-23
 */
public class VideoComponentEntity extends ComponentEntity {
	private boolean videoControlBarIsShow;
	//封皮
	private String coverSourceID;
	public String getCoverSourceID() {
		return coverSourceID;
	}

	public void setCoverSourceID(String coverSourceID) {
		this.coverSourceID = coverSourceID;
	}

	public boolean isVideoControlBarIsShow() {
		return videoControlBarIsShow;
	}

	public void setVideoControlBarIsShow(boolean videoControlBarIsShow) {
		this.videoControlBarIsShow = videoControlBarIsShow;
	}
}

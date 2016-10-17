package com.hl.android.book.entity;

import java.util.ArrayList;

import com.hl.android.controller.BookController;
import com.hl.android.core.utils.StringUtils;

public class PageEntity {

	private String ID;
	private String title;
	private String description;
	private float width;
	private float height;
	private boolean enableNavigation;
	public boolean enablePageTurnByHand = true;
	private String type;
	private ArrayList<ContainerEntity> containers;
	private ContainerEntity background;
	private PlaySequenceEntity sequence;
	private ArrayList<String> navePageIds;
	private String linkPageID;
	private boolean isCashSnapshot = false;
	private String snapID = "";
	private String pageChangeEffectType;
	private String pageChangeEffectDir;
	private long  PageChangeEffectDuration;
	public String beCoveredPageID="";
	
	
	public boolean IsGroupPlay = false;
	public String getSnapShotID() {
		if(StringUtils.isEmpty(snapID)){
			snapID = BookController.getInstance().getBook().getSnapshotIdByPageId(ID);
		}
		return snapID;
	}

	public void setSnapShotID(String snapShotID) {
		this.snapID = snapShotID;
	}

	public boolean isCashSnapshot() {
		//return true;
		return isCashSnapshot;
	}

	/**
	 * 所有的书籍都不在使用截图
	 * @param snapShotType
	 */
	public void setSnapShotType(boolean snapShotType) {
		this.isCashSnapshot = snapShotType;
	}

	public PageEntity() {
		containers = new ArrayList<ContainerEntity>();
		background = new ContainerEntity();
		sequence = new PlaySequenceEntity();
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public boolean isEnableNavigation() {
		return enableNavigation;
	}

	public void setEnableNavigation(boolean enableNavigation) {
		this.enableNavigation = enableNavigation;
	}

	public ArrayList<ContainerEntity> getContainers() {
		return containers;
	}

	public void setContainers(ArrayList<ContainerEntity> containers) {
		this.containers = containers;
	}

	public ContainerEntity getBackground() {
		return background;
	}

	public void setBackground(ContainerEntity background) {
		this.background = background;
	}

	public PlaySequenceEntity getSequence() {
		return sequence;
	}

	public void setSequence(PlaySequenceEntity sequence) {
		this.sequence = sequence;
	}

	public ArrayList<String> getNavePageIds() {
		if (null == navePageIds){
			navePageIds = new ArrayList<String>();
		}
		return navePageIds;
	}

	public void setNavePageIds(ArrayList<String> navePageIds) {
		this.navePageIds = navePageIds;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLinkPageID() {
		return linkPageID;
	}

	public void setLinkPageID(String linkPageID) {
		this.linkPageID = linkPageID;
	}
	
	public String getPageChangeEffectType() {
		return pageChangeEffectType;
	}

	public void setPageChangeEffectType(String pageChangeEffectType) {
		this.pageChangeEffectType = pageChangeEffectType;
	}

	public String getPageChangeEffectDir() {
		return pageChangeEffectDir;
	}

	public void setPageChangeEffectDir(String pageChangeEffectDir) {
		this.pageChangeEffectDir = pageChangeEffectDir;
	}

	public long getPageChangeEffectDuration() {
		return PageChangeEffectDuration;
	}

	public void setPageChangeEffectDuration(long pageChangeEffectDuration) {
		PageChangeEffectDuration = pageChangeEffectDuration;
	}

	
}

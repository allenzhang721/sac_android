package com.hl.android.book.entity.moudle;

import java.util.ArrayList;

import android.graphics.Color;

import com.hl.android.book.entity.ComponentEntity;

public class MoudleComponentEntity extends ComponentEntity{
	private String moduleID;
	private ArrayList<String> sourceIDList;
	private ArrayList<String> selectSourceIDList;//竖向滑动选择目录要用
	public ArrayList<MRenderBean> leftRenderBean=new ArrayList<MRenderBean>();
	public ArrayList<MRenderBean> middleRenderBean=new ArrayList<MRenderBean>();
	public ArrayList<MRenderBean> rightRenderBean=new ArrayList<MRenderBean>();
	private ArrayList<String> downIDList;
	public ArrayList<MaskBean> maskBeanList = new ArrayList<MaskBean>();
	private int itemWidth;
	private int itemHeight;
	private long timerDelay;
	private int bookWidth;
	private int bookHeight;
	private String serverAddress;
	private String bgSourceID;
	public boolean isShowNavi=true;
	public int lineColor=Color.BLACK;
	public int lineThick=1;
	public float lineAlpha=1;
	public boolean isHorSlider=true;
	
	public ArrayList<String> renderDes=new ArrayList<String>();
	
	public MoudleComponentEntity(ComponentEntity component){
		if(component!=null){
			this.animationRepeat = component.animationRepeat;
			this.alpha=component.alpha;
		}
	}
	private int cellNumber = 1;

	public int getCellNumber() {
		return cellNumber;
	}

	public void setCellNumber(int cellNumber) {
		this.cellNumber = cellNumber;
	}
	public String getModuleID() {
		return moduleID;
	}

	public void setModuleID(String moduleID) {
		this.moduleID = moduleID;
	}

	public ArrayList<String> getSourceIDList() {
		if (null == this.sourceIDList){
			sourceIDList = new ArrayList<String>();
		}
		return sourceIDList;
	}

	public void setSourceIDList(ArrayList<String> sourceIDList) {
		this.sourceIDList = sourceIDList;
	}

	public ArrayList<String> getSelectSourceIDList() {
		if (null == this.selectSourceIDList){
			selectSourceIDList = new ArrayList<String>();
		}
		return selectSourceIDList;
	}

	public void setSelectSourceIDList(ArrayList<String> selectSourceIDList) {
		this.selectSourceIDList = selectSourceIDList;
	}
	
	public int getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
	}

	public long getTimerDelay() {
		return timerDelay;
	}

	public void setTimerDelay(long timerDelay) {
		this.timerDelay = timerDelay;
	}
	public ArrayList<String> getDownIDList() {
		if (null == downIDList){
			downIDList = new ArrayList<String>();
		}
		return downIDList;
	}

	public void setDownIDList(ArrayList<String> downIDList) {
		this.downIDList = downIDList;
	}

	public int getBookWidth() {
		return bookWidth;
	}

	public void setBookWidth(int bookWidth) {
		this.bookWidth = bookWidth;
	}

	public int getBookHeight() {
		return bookHeight;
	}

	public void setBookHeight(int bookHeight) {
		this.bookHeight = bookHeight;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getBgSourceID() {
		return bgSourceID;
	}

	public void setBgSourceID(String bgSourceID) {
		this.bgSourceID = bgSourceID;
	}
	//是否显示导航栏
	public boolean isShowControllerPoint = false;

	//connect line start
	public int mLineGap;//column space
	public int mRowOrColumnGap;
	public ArrayList<Cell> cellList;
	public MoudleComponentEntity(){
		 cellList = new ArrayList<Cell>();
	}
	///connect line end
	//3d fliper start
	public int speed =5;
	public boolean isAutoRotation = true;
	public String rotationType = "anticlosewise";
	//3d fliper end
	
	public ArrayList<QuestionEntity> questionList = new ArrayList<QuestionEntity>();
}

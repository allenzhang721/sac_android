package com.hl.android.book.entity.moudle;

import java.util.ArrayList;

public class QuestionEntity{
	//题干对应的图片
	public String titleResource = "";
	//单选题还是多选题
	public String questionType = "";
	//得分
	public int score = 0;
	//题干图片
	public String imgSource = "";
	//音频
	public String soundSource = "";
	public int chooseIndex = -1;
	//题目选项
	private ArrayList<OptionEntity> optionList = new ArrayList<OptionEntity>();
	
	public ArrayList<OptionEntity> getOptionList(){
		return optionList;
	}
	private ArrayList<Integer> rightAnswerList = new ArrayList<Integer>();

	public ArrayList<Integer> getRightAnswerList(){
		return rightAnswerList;
	}
}


package com.hl.android.book.entity;

import java.util.ArrayList;

public class PlaySequenceEntity {
	public ArrayList<GroupEntity> Group;
	public ArrayList<Long> Delay;
	public PlaySequenceEntity()
	{
		Group=new ArrayList<GroupEntity>();
		Delay=new ArrayList<Long>();
	}
}

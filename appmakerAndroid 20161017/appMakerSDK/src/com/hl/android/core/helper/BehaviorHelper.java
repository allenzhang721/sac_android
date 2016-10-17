package com.hl.android.core.helper;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.controller.BookController;
import com.hl.android.core.helper.behavior.AutoPlayBookAction;
import com.hl.android.core.helper.behavior.BackLastPageAction;
import com.hl.android.core.helper.behavior.BehaviorAction;
import com.hl.android.core.helper.behavior.ChangeSizeAction;
import com.hl.android.core.helper.behavior.ChangeUrlAction;
import com.hl.android.core.helper.behavior.CounterMinusAction;
import com.hl.android.core.helper.behavior.CounterPlusAction;
import com.hl.android.core.helper.behavior.CounterResetAction;
import com.hl.android.core.helper.behavior.DisableAutoPlayBookAction;
import com.hl.android.core.helper.behavior.GoToLastPageAction;
import com.hl.android.core.helper.behavior.GoToPageAction;
import com.hl.android.core.helper.behavior.GoToUrlAction;
import com.hl.android.core.helper.behavior.HideAction;
import com.hl.android.core.helper.behavior.MarkAction;
import com.hl.android.core.helper.behavior.PageChangeAction;
import com.hl.android.core.helper.behavior.PauseAnimationAction;
import com.hl.android.core.helper.behavior.PauseBackGroundMusicAction;
import com.hl.android.core.helper.behavior.PauseVideoAudioAction;
import com.hl.android.core.helper.behavior.PlayAnimationAction;
import com.hl.android.core.helper.behavior.PlayAnimationAtAction;
import com.hl.android.core.helper.behavior.PlayBackGroundMusicAction;
import com.hl.android.core.helper.behavior.PlayGroupAction;
import com.hl.android.core.helper.behavior.PlayVideoAudioAction;
import com.hl.android.core.helper.behavior.ResumeVideoAudioAction;
import com.hl.android.core.helper.behavior.ShowAction;
import com.hl.android.core.helper.behavior.StopAnimationAction;
import com.hl.android.core.helper.behavior.StopBackGroundMusicAction;
import com.hl.android.core.helper.behavior.StopGroupAtIndexAction;
import com.hl.android.core.helper.behavior.StopVideoAudioAction;
import com.hl.android.core.helper.behavior.TempalteChangetoaction;

/**
 * 动作执行类
 * 所有的动作实例都在需要使用的是进行初始化，并且只初始化一次
 * @author zhaoq
 * @version 1.0
 * @createed 2013-11-5
 */
public class BehaviorHelper {
	
	public static String FUNCTION_SHOW_BOOKMARK = "FUNCTION_SHOW_BOOKMARK";
	public static String FUNCTION_GOTO_PAGE = "FUNCTION_GOTO_PAGE";
	public static String FUNCTION_GOTO_LASETPAGE = "FUNCTION_GOTO_LASETPAGE";
	public static String FUNCTION_BACK_LASTPAGE = "FUNCTION_BACK_LASTPAGE";
	public static String FUNCTION_DISABLE_AUTO_PLAY_BOOK = "FUNCTION_DISABLE_AUTO_PLAY_BOOK";
	
	public static String FUNCTION_PAGE_CHANGE = "FUNCTION_PAGE_CHANGE";
	public static String FUNCTION_PLAY_BACKGROUND_MUSIC = "FUNCTION_PLAY_BACKGROUND_MUSIC";
	public static String FUNCTION_STOP_BACKGROUND_MUSIC = "FUNCTION_STOP_BACKGROUND_MUSIC";
	public static String FUNCTION_PAUSE_BACKGROUND_MUSIC = "FUNCTION_PAUSE_BACKGROUND_MUSIC";
	public static String FUNCTION_PLAY_GROUP = "FUNCTION_PLAY_GROUP";
	
	public static String FUNCTION_STOP_GROUP_AT_INDEX = "FUNCTION_STOP_GROUP_AT_INDEX";
	public static String FUNCTION_PLAY_ANIMATION = "FUNCTION_PLAY_ANIMATION";
	public static String FUNCTION_PLAY_ANIMATION_AT = "FUNCTION_PLAY_ANIMATION_AT";
	public static String FUNCTION_STOP_ANIMATION = "FUNCTION_STOP_ANIMATION";
	public static String FUNCTION_PAUSE_ANIMATION = "FUNCTION_PAUSE_ANIMATION";
	
	public static String FUNCTION_PLAY_VIDEO_AUDIO = "FUNCTION_PLAY_VIDEO_AUDIO";
	public static String FUNCTION_STOP_VIDEO_AUDIO = "FUNCTION_STOP_VIDEO_AUDIO";
	public static String FUNCTION_HIDE = "FUNCTION_HIDE";
	public static String FUNCTION_SHOW = "FUNCTION_SHOW";
	public static String FUNCTION_RESUME_VIDEO_AUDIO = "FUNCTION_RESUME_VIDEO_AUDIO";
	
	public static String FUNCTION_CHANGE_SIZE = "FUNCTION_CHANGE_SIZE";
	public static String FUNCTION_GOTO_URL = "FUNCTION_GOTO_URL";
	public static String FUNCTION_COUNTER_PLUS = "FUNCTION_COUNTER_PLUS";
	public static String FUNCTION_COUNTER_MINUS = "FUNCTION_COUNTER_MINUS";
	public static String FUNCTION_COUNTER_RESET = "FUNCTION_COUNTER_RESET";
	
	public static String FUNCTION_CHANGE_URL = "FUNCTION_CHANGE_URL";
	public static String FUNCTION_AUTO_PLAY_BOOK = "FUNCTION_AUTO_PLAY_BOOK";
	public static String FUNCTION_PAUSE_VIDEO_AUDIO = "FUNCTION_PAUSE_VIDEO_AUDIO";
	public static String FUNCTION_TEMPALTE_CHANGETO = "FUNCTION_TEMPALTE_CHANGETO";
	  
	
	private static Map<String,BehaviorAction> behaviorMap = new HashMap<String,BehaviorAction>();
	/*
	 * TODO 将所有的动作实例都装入到容器中 
	 */
	static{
		behaviorMap.put(FUNCTION_SHOW_BOOKMARK, new MarkAction());
		behaviorMap.put(FUNCTION_GOTO_PAGE, new GoToPageAction());
		behaviorMap.put(FUNCTION_GOTO_LASETPAGE, new GoToLastPageAction());
		behaviorMap.put(FUNCTION_BACK_LASTPAGE, new BackLastPageAction());
		behaviorMap.put(FUNCTION_DISABLE_AUTO_PLAY_BOOK, new DisableAutoPlayBookAction());
		
		behaviorMap.put(FUNCTION_PAGE_CHANGE, new PageChangeAction());
		behaviorMap.put(FUNCTION_PLAY_BACKGROUND_MUSIC, new PlayBackGroundMusicAction());
		behaviorMap.put(FUNCTION_STOP_BACKGROUND_MUSIC, new StopBackGroundMusicAction());
		behaviorMap.put(FUNCTION_PAUSE_BACKGROUND_MUSIC, new PauseBackGroundMusicAction());
		behaviorMap.put(FUNCTION_PLAY_GROUP, new PlayGroupAction());
		
		behaviorMap.put(FUNCTION_STOP_GROUP_AT_INDEX, new StopGroupAtIndexAction());
		behaviorMap.put(FUNCTION_PLAY_ANIMATION, new PlayAnimationAction());
		behaviorMap.put(FUNCTION_PLAY_ANIMATION_AT, new PlayAnimationAtAction());
		behaviorMap.put(FUNCTION_STOP_ANIMATION, new StopAnimationAction());
		behaviorMap.put(FUNCTION_PAUSE_ANIMATION, new PauseAnimationAction());
		
		behaviorMap.put(FUNCTION_PLAY_VIDEO_AUDIO, new PlayVideoAudioAction());
		behaviorMap.put(FUNCTION_STOP_VIDEO_AUDIO, new StopVideoAudioAction());
		behaviorMap.put(FUNCTION_HIDE, new HideAction());
		behaviorMap.put(FUNCTION_SHOW, new ShowAction());
		behaviorMap.put(FUNCTION_RESUME_VIDEO_AUDIO, new ResumeVideoAudioAction());
		
		behaviorMap.put(FUNCTION_CHANGE_SIZE, new ChangeSizeAction());
		behaviorMap.put(FUNCTION_GOTO_URL, new GoToUrlAction());
		behaviorMap.put(FUNCTION_COUNTER_PLUS, new CounterPlusAction());
		behaviorMap.put(FUNCTION_COUNTER_MINUS, new CounterMinusAction());
		behaviorMap.put(FUNCTION_COUNTER_RESET, new CounterResetAction());
		
		behaviorMap.put(FUNCTION_CHANGE_URL, new ChangeUrlAction());
		behaviorMap.put(FUNCTION_AUTO_PLAY_BOOK, new AutoPlayBookAction());
		behaviorMap.put(FUNCTION_PAUSE_VIDEO_AUDIO, new PauseVideoAudioAction());
		behaviorMap.put(FUNCTION_TEMPALTE_CHANGETO, new TempalteChangetoaction());
		
	}
	/**
	 * 获得动作的执行类
	 * @param behavior
	 * @return
	 */
	public static void doBehavior(BehaviorEntity behavior){
		String functionName =  behavior.FunctionName;
		//从容器类中获取动作实例，如果没有则说明遇到不支持的动作事件
		BehaviorAction behaviorAction = behaviorMap.get(functionName);
		if(behaviorAction == null){
			Log.e("hl","遇到了不认识的事件处理，请添加该事件" + functionName);
			return;
		}
		behaviorAction.doAction(behavior);
	}
	
	public static void doBeheavorForList(BehaviorEntity behavior,int index,String componentId) {
		String eventValue = behavior.EventValue;
		String behaviorValue = behavior.Value;
		String[] splits = behavior.Value.split(";");
		if(splits.length>1){
			eventValue = splits[0];
			behaviorValue = splits[1];
		}
		if (eventValue.equals(String.valueOf(index))) {
			BehaviorEntity behaviorC = new BehaviorEntity();
			behaviorC.EventName = behavior.EventName;
			behaviorC.FunctionName = behavior.FunctionName;
			behaviorC.FunctionObjectID = behavior.FunctionObjectID;
			behaviorC.Value = behaviorValue;
			behaviorC.triggerPageID = behavior.triggerPageID;
			behaviorC.triggerComponentID = componentId;
			BookController.getInstance().runBehavior(behaviorC);
		}
	}
}

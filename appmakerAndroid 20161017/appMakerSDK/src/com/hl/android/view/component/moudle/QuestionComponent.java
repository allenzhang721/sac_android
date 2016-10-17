package com.hl.android.view.component.moudle;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hl.android.R;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.book.entity.moudle.OptionEntity;
import com.hl.android.book.entity.moudle.QuestionEntity;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.component.inter.Component;

public class QuestionComponent extends LinearLayout implements Component,View.OnClickListener {
//	private static int textTitleID = 1212001;
	private static int imgQuestionHeadID = 1212002;
	private static final int imgAudioID = 1212003;
	private static final int imgAttachID = 1212004;

	private static final int textAnswerID = 1212005;
	private static final int btnNextID = 1212006;
	private static final int btnPrevID = 1212007;
	private MoudleComponentEntity mEntity;
	private Context mContext;
	private LinearLayout container;
	private int questionIndex = 0;
	
	//选中的元素的位置顺序
	private int checkIndex = -1;
	//核对答案还是清除答案的标识
	private boolean textSwitch = false;
	
	private ImageView imgOption = null;
	private OptionEntity selectOptionEntity = null;
	private Button textAnswer = null;
//	private int score = 0;
	private Button textTitle;
	private ImageView imgQuestionHead;
	QuestionEntity question;
	ImageView imgAudio;
	ImageView imgAttach;
	LinearLayout layOption;
	private // 滚动部分
	ScrollView scrollView;
	MediaPlayer media = null;
	private TextView textResult;
	private LinearLayout layResult;
	RelativeLayout.LayoutParams imgAudioLP;
	public QuestionComponent(Context context) {
		super(context);
		mContext = context;
	}
	public QuestionComponent(Context context, ComponentEntity entity) {
		this(context);
		setEntity(entity);
		if (mEntity.questionList.size() == 0)
			return;

		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_HORIZONTAL);
		setPadding(0, 20, 0, 2);
	}

	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity = (MoudleComponentEntity) entity;
	}

	@Override
	public void load() {
		
		question = mEntity.questionList.get(0);

		drawResultText();
		drawTitle();
		drawQuestion();
		drawOption();
		// foot
		drawFoot();
		setQuestion();
	}
	private void drawResultText(){
		LinearLayout.LayoutParams layResultLp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
		layResult = new LinearLayout(mContext);
		layResult.setGravity(Gravity.CENTER);
		addView(layResult, layResultLp);
		textResult = new TextView(mContext);
		textResult.setTextColor(Color.BLACK);
		layResult.addView(textResult);
		layResult.setVisibility(View.GONE);
	}
	private void setResult(){
		try{
			if(media!=null&& media.isPlaying())media.stop();
		}catch(Exception e ){
			
		}
		
		layResult.setVisibility(View.VISIBLE);
		((View)textTitle.getParent()).setVisibility(View.GONE);
		String infor = mContext.getString(R.string.questionresult);
		infor = infor.replaceAll("all", mEntity.questionList.size()+"")
				.replaceAll("check", getRightCnt()+"");
		textResult.setText(infor);
		textAnswer.setText(R.string.examredo);
//		scrollView.setVisibility(View.GONE);
		container.setVisibility(View.GONE);
		
		findViewById(btnPrevID).setVisibility(View.GONE);
		findViewById(btnNextID).setVisibility(View.GONE);
		
		question = null;
		imgOption = null;
	}
	private int getRightCnt(){
		int cnt = 0;
		for(QuestionEntity q:mEntity.questionList){
			if(q.getRightAnswerList().contains(q.chooseIndex))cnt++;
		}
		return cnt;
	}
	private void setQuestion(){
		findViewById(btnPrevID).setVisibility(View.VISIBLE);
		findViewById(btnNextID).setVisibility(View.VISIBLE);
		
		layResult.setVisibility(View.GONE);
		selectOptionEntity = null;
		imgOption = null;
		textSwitch = false;
		textAnswer.setText(R.string.checkanswer);
		
		((View)textTitle.getParent()).setVisibility(View.VISIBLE);
//		scrollView.setVisibility(View.VISIBLE);
		container.setVisibility(View.VISIBLE);

		setTitle();
		try{
			setQuestionHead();
		}catch(Exception e){
			
		}
		setOption();
	}

	private void drawFoot() {
		RelativeLayout layFoot = new RelativeLayout(mContext);
		layFoot.setGravity(Gravity.CENTER);

		RelativeLayout.LayoutParams btnPrevLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		btnPrevLp.topMargin = 10;
		btnPrevLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		btnPrevLp.addRule(RelativeLayout.CENTER_VERTICAL);
		ImageButton btnPrev = new ImageButton(mContext);
		btnPrev.setBackgroundResource(R.drawable.left_arraw_select);
		btnPrev.setId(btnPrevID);
		btnPrev.setOnClickListener(this);
		layFoot.addView(btnPrev,btnPrevLp);
		
		
		textAnswer = new Button(mContext);
		textAnswer.setText("核对答案");
		textAnswer.setId(textAnswerID);
		textAnswer.setOnClickListener(this);
		
		RelativeLayout.LayoutParams layAnswerLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		layAnswerLp.addRule(RelativeLayout.CENTER_IN_PARENT);
		layFoot.addView(formatOrangeView(textAnswer),layAnswerLp);
		
		ImageButton btnNext = new ImageButton(mContext);
		btnNext.setBackgroundResource(R.drawable.right_arraw_select);
		RelativeLayout.LayoutParams btnNextLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		btnNextLp.topMargin = 10;
		btnNext.setId(btnNextID);
		btnNext.setOnClickListener(this);
		btnNextLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layFoot.addView(btnNext,btnNextLp);
		addView(layFoot,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
	}

	private void drawQuestion() {
		container = new LinearLayout(mContext);
		container.setOrientation(LinearLayout.VERTICAL);
		addView(container, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,1));
		// question head
		RelativeLayout relativeHead = new RelativeLayout(mContext);
		container.addView(relativeHead, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		
		imgQuestionHead = new ImageView(mContext);
		imgQuestionHead.setId(imgQuestionHeadID);
		RelativeLayout.LayoutParams imgQuestionHeadLP = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imgQuestionHead.setScaleType(ScaleType.FIT_XY);
		imgQuestionHeadLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		imgQuestionHeadLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		relativeHead.addView(imgQuestionHead, imgQuestionHeadLP);
		// question audio
		imgAudio = new ImageView(mContext);
		imgAudio.setId(imgAudioID);
		imgAudioLP = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imgAudioLP.topMargin = 18;
		imgAudio.setOnClickListener(this);
		imgAudioLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		imgAudioLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		relativeHead.addView(imgAudio, imgAudioLP);
		// question img
		imgAttach = new ImageView(mContext);
		if (!StringUtils.isEmpty(question.imgSource)) {
			Bitmap bitmapimg = BitmapUtils.getBitMap(question.imgSource,
					mContext);
			imgAttach.setImageBitmap(bitmapimg);
		}
		imgAttach.setId(imgAttachID);
		imgAttach.setOnClickListener(this);
		RelativeLayout.LayoutParams imgAttachLP = new RelativeLayout.LayoutParams(
				120, 120);
		imgAttachLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		imgAttachLP.addRule(RelativeLayout.BELOW, imgAudioID);
		relativeHead.addView(imgAttach, imgAttachLP);

		// body
		TextView textLine = new TextView(mContext);
		textLine.setBackgroundColor(Color.BLACK);
		LinearLayout.LayoutParams textLineLP = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 1);
		textLineLP.bottomMargin = 10;
		textLineLP.topMargin = 10;
		container.addView(textLine, textLineLP);
	}
	private void setQuestionHead() throws Exception{
		imgQuestionHead.setVisibility(View.VISIBLE);
		Bitmap bitmap = BitmapUtils.getBitMap(question.titleResource, mContext);// .getBitMap(,
		imgQuestionHead.setImageBitmap(bitmap);
		
		if (!StringUtils.isEmpty(question.imgSource)) {
			Bitmap bitmapimg = BitmapUtils.getBitMap(question.imgSource,
					mContext);
			imgAttach.setImageBitmap(bitmapimg);
			imgAttach.setVisibility(View.VISIBLE);
		}else{
			imgAttach.setVisibility(View.GONE);
		}
		 
		if(media == null){
			media = new MediaPlayer();
			media.setAudioStreamType(AudioManager.STREAM_MUSIC);
		}else{
			media.reset();
		}
//	 
//		try{
//			if(media!=null && media.isPlaying())media.stop();
//		}catch(Exception e){
//			
//		}
		
		imgAudio.setImageResource(R.drawable.audio_play);
		if(StringUtils.isEmpty(question.soundSource)){
			imgAudio.setVisibility(View.GONE);
		}else{
			if (HLSetting.IsResourceSD) {
				String filePath = FileUtils.getInstance().getFilePath(
						question.soundSource);
				String privatePath = getContext().getFilesDir()
						.getAbsolutePath();
				if (filePath.contains(privatePath)) {
					FileInputStream fis = null;
					try{
						fis = new FileInputStream(new File(filePath));
						FileDescriptor fd = fis.getFD();
						media.setDataSource(fd);
					}finally{
						fis.close();
					}
				}else{
					media.setDataSource(filePath);
				}

			} else {		
				AssetFileDescriptor ass = FileUtils.getInstance().getFileFD(getContext(),
						question.soundSource);
				media.setDataSource(ass.getFileDescriptor(),
						ass.getStartOffset(), ass.getLength());
			}
			media.prepare();
			imgAudio.setVisibility(View.VISIBLE);
		}
	}
	
	private void drawOption(){
		layOption = new LinearLayout(mContext);
		layOption.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams wrapLp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		container.addView(layOption, wrapLp);
	}
	private void setOption(){
		layOption.removeAllViews();
		LinearLayout.LayoutParams wrapLp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		wrapLp.topMargin = 10;
		// option
		for(int i=0;i<question.getOptionList().size();i++){
			OptionEntity option = question.getOptionList().get(i);
			OptionView textOption = new OptionView(mContext,option,i);
			layOption.addView(textOption,wrapLp);
		}
		
	}
	private void drawTitle() {
		// title
		textTitle = new Button(mContext);
		
		textTitle.setGravity(Gravity.CENTER);
		textTitle.setTextColor(Color.BLACK);
		LinearLayout.LayoutParams textTitleLP = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textTitleLP.topMargin = 18;
		addView(formatOrangeView(textTitle), textTitleLP);
	}
	private void setTitle(){
		String infor = mContext.getResources().getString(R.string.questionexam) + (questionIndex+1) + " of " + mEntity.questionList.size();
		textTitle.setText(infor);
	}
	private View formatOrangeView(Button btn){
		btn.setBackgroundResource(R.drawable.orange_btn_corner);
		btn.setGravity(Gravity.CENTER_HORIZONTAL);
		btn.setTextColor(Color.BLACK);
		btn.setPadding(0, 0, 0, 0);
		LinearLayout.LayoutParams textAnswerLp = new LinearLayout.LayoutParams(120,LayoutParams.WRAP_CONTENT);
		textAnswerLp.setMargins(1, 1, 1, 1);

		LinearLayout layAnswer = new LinearLayout(mContext);
		layAnswer.setBackgroundResource(R.drawable.orange_corner_border);
		layAnswer.addView(btn,textAnswerLp);
		return layAnswer;
	}
	
	@Override
	public void load(InputStream is) {
	}
 

	@Override
	public void play() {
	}

	@Override
	public void stop() {
		if(media!=null)media.release();
	}

	@Override
	public void hide() {
	}

	@Override
	public void show() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void pause() {
	}
	class OptionView extends LinearLayout{
		private ImageView img;
		private int mIndex = -1;
		private OptionEntity mEntity;
		public OptionView(Context context,OptionEntity entity,int index) {
			super(context);
			mEntity = entity;
			mIndex = index;
			setGravity(Gravity.CENTER_VERTICAL);
			setOrientation(LinearLayout.HORIZONTAL);
			img = new ImageView(context);
			img.setBackgroundResource(mEntity.optionType);
			if(mEntity.optionType!=R.drawable.radio_up){
				selectOptionEntity = mEntity;
				imgOption = img;
				checkIndex = mIndex;
			}
			setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						checkIndex = mIndex;
						if(imgOption!=null){
							selectOptionEntity.optionType = R.drawable.radio_up;
							imgOption.setBackgroundResource(selectOptionEntity.optionType);
						}
						mEntity.optionType = R.drawable.radio_down;
						selectOptionEntity = mEntity;
						img.setBackgroundResource(R.drawable.radio_down);
						imgOption = img;
						question.chooseIndex = mIndex;
					}
					return false;
				}
			});
//			setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					
//				}
//			});
			addView(img,new LinearLayout.LayoutParams(30,30));
			TextView textOption = new TextView(context);
			textOption.setTextColor(Color.BLACK);
			textOption.setText(mEntity.optionText);
			addView(textOption);
		}
		
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();
		switch(viewID){
		case textAnswerID:
			if(imgOption!=null&& question!=null){
				if(textSwitch){
					imgOption.setBackgroundResource(R.drawable.radio_up);
					textAnswer.setText(R.string.checkanswer);
					imgOption = null;
				}else{
					if(question.getRightAnswerList().contains(checkIndex)){
						imgOption.setBackgroundResource(R.drawable.radio_correct);
					}else{
						imgOption.setBackgroundResource(R.drawable.radio_incorrect);
					}
					textAnswer.setText(R.string.clearexam);
				}
				textSwitch = !textSwitch;
			}else if(questionIndex == mEntity.questionList.size()){
				questionIndex = 0;
				question = mEntity.questionList.get(questionIndex);
				setQuestion();
			}
			break;
		case btnNextID:
			if(questionIndex < mEntity.questionList.size()-1){
				questionIndex++;
				question = mEntity.questionList.get(questionIndex);
				setQuestion();
				return;
			}else{
				questionIndex = mEntity.questionList.size();
				setResult();
			}
			break;
		case btnPrevID:
			if(questionIndex > 0){
				questionIndex--;
				question = mEntity.questionList.get(questionIndex);
				setQuestion();
				return;
			}
			break;
		case imgAttachID:
			if(v.getVisibility() != View.VISIBLE)return;
			showAttachImg();
			break;
		case imgAudioID:
			if(media.isPlaying()){
				media.pause();
				imgAudio.setImageResource(R.drawable.audio_play);
				imgAudio.setLayoutParams(imgAudioLP);
			}else{
				playAudio();
			}
			break;
		}
	}
	
	public void playAudio(){
		imgAudio.setImageResource(R.drawable.audio_stop);
		imgAudio.setLayoutParams(imgAudioLP);
		media.start();
	}
	public void showAttachImg(){
		if(!StringUtils.isEmpty(question.imgSource)){
			TextView img = new TextView(mContext);
			Bitmap bitmapimg = BitmapUtils.getBitMap(question.imgSource,
					mContext);
			img.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(),bitmapimg));
			AlertDialog alert = new Builder(mContext).setTitle("")
			.setIcon(R.drawable.icon)
			.setView(img).create();
			alert.setCanceledOnTouchOutside(true);
			alert.show();
		}
	}
}

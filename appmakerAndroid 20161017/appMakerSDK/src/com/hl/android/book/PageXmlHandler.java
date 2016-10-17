package com.hl.android.book;

import java.util.ArrayList;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import android.graphics.PointF;

import com.hl.android.book.entity.AnimationEntity;
import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ContainerEntity;
import com.hl.android.book.entity.CounterEntity;
import com.hl.android.book.entity.GifComponentEntity;
import com.hl.android.book.entity.GroupEntity;
import com.hl.android.book.entity.HTMLComponentEntity;
import com.hl.android.book.entity.PDFComponentEntity;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.book.entity.SWFFileEntity;
import com.hl.android.book.entity.SeniorAnimationEntity;
import com.hl.android.book.entity.SliderEffectComponentEntity;
import com.hl.android.book.entity.SubImageItem;
import com.hl.android.book.entity.TextComponentEntity;
import com.hl.android.book.entity.TextComponentLineEntity;
import com.hl.android.book.entity.TimerEntity;
import com.hl.android.book.entity.VideoComponentEntity;
import com.hl.android.book.entity.moudle.Cell;
import com.hl.android.book.entity.moudle.MRenderBean;
import com.hl.android.book.entity.moudle.MaskBean;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.book.entity.moudle.OptionEntity;
import com.hl.android.book.entity.moudle.QuestionEntity;
import com.hl.android.core.utils.StringUtils;

public class PageXmlHandler implements ContentHandler {
	PageEntity page;
	private String tagName = null;
	private boolean ispage = true;
	private boolean isAnimation = false;
	private boolean isContainers = false;
	private boolean isPlaySequence = false;
	private boolean isPlaySequenceDelay = false;
	private boolean isBackground = false;
	private boolean isPoint = false;
	private boolean isBehavior = false;
	private boolean flg = true;
	private boolean isGifComponent = false;
	private boolean isPDFComponent = false;
	private boolean isMoudleComponent = false;
	private boolean isContainerPosition = false;
	private boolean isComponent=false;
	//是否是关联对象部分的解析
	private boolean isLinkPage = false;
	
	private boolean isVerConnectLine = false;
	private boolean isHorConnectLine = false;
	//=============================================
	private boolean isSenior=false;
	private boolean isAnimationModel=false;
	//=============================================

	private boolean isQuestion = false;

	private boolean isMaskSlider = false;
	private boolean isLeftMRender=false;
	private boolean isMiddleMRender=false;
	private boolean isRightRender=false;
	//private String moudleName = "";
	//private boolean isVideoComponent = false;
	private String val = "";
	private PointF p = new PointF();
	private ContainerEntity container = new ContainerEntity();
	private AnimationEntity animation = new AnimationEntity();
	private BehaviorEntity Behavior = new BehaviorEntity();
	private GroupEntity group = new GroupEntity();
	private MRenderBean curRenderBean;
	TextComponentLineEntity lineEntity = null;
	
	QuestionEntity questionEntity = null;
	MaskBean maskBean = null;
	private Cell cell = null;
	private SubImageItem subImageItem = null;
	private SeniorAnimationEntity seniorAnimationEntity=null;
	
	private PointF stroyTellPt;
	
	public PageXmlHandler(PageEntity page) {
		this.page = page;
	}

	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		String temp = new String(arg0, arg1, arg2);
		val += temp;

	}

	@Override
	public void endDocument() throws SAXException {
	}

	/**
	 * 是否是文本
	 */
	private boolean isText = false;
	//计数器的标志
	private boolean isCounter = false;
	//计时器的标志
	private boolean isTimer = false;
	private boolean isSwfFile=false;
	private boolean isSliderEffectComponent=false;
	private boolean isPaintUIComponent;
	
	@Override
	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		val = val.trim();
		if(arg1.equals("PlaySequenceDelay")){
			isPlaySequenceDelay = false;
		}
		if (flg) {
			if (ispage) {
				if (tagName.equals("ID")) {
					page.setID(val);
				} else if (tagName.equals("Width")) {
					page.setWidth(Float.parseFloat(val));
				} else if (tagName.equals("Height")) {
					page.setHeight(Float.parseFloat(val));
				} else if (tagName.equals("Title")) {
					page.setTitle(val);
				} else if (tagName.equals("IsGroupPlay")) {
					page.IsGroupPlay = Boolean.parseBoolean(val);
				} else if (tagName.equals("Description")) {
					page.setDescription(val);
				} else if (tagName.equals("Type")) {
					page.setType(val);
				}  else if (tagName.equals("LinkPageID")) {
					page.setLinkPageID(val);
				}   else if (tagName.equals("BeCoveredPageID")) {
					page.beCoveredPageID = val;
				} else if (tagName.equals("EnableNavigation")) {
					page.setEnableNavigation(Boolean.valueOf(val));
					/*ispage = false;
					isContainers = true;*/
				}else if (tagName.toLowerCase(Locale.ENGLISH).equals("iscashsnapshot")) {
					page.setSnapShotType(Boolean.parseBoolean(val));
				} else if (tagName.toLowerCase(Locale.ENGLISH).equals("snapid")) {
					page.setSnapShotID(val);
				} else if (tagName.equals("EnablePageTurnByHand")) {
					page.enablePageTurnByHand = Boolean.valueOf(val);
				} else if (tagName.equals("PageChangeEffectType")) {
					page.setPageChangeEffectType(val);
				} else if (tagName.equals("PageChangeEffectDir")) {
					page.setPageChangeEffectDir(val);
				} else if (tagName.equals("PageChangeEffectDuration")) {
					page.setPageChangeEffectDuration(Long.valueOf(val));
				} 
			}
			if (isContainers) {
				if(isSliderEffectComponent){
					if(tagName.equals("IsUseSlide")){
						((SliderEffectComponentEntity) this.container.component).isUseSlide=Boolean.parseBoolean(val);
					}else if(tagName.equals("IsPageTweenSlide")){
						((SliderEffectComponentEntity) this.container.component).isPageTweenSlide=Boolean.parseBoolean(val);
					}else if(tagName.equals("IsPageInnerSlide")){
						((SliderEffectComponentEntity) this.container.component).isPageInnerSlide=Boolean.parseBoolean(val);
					}else if(tagName.equals("SlideBindingX")){
						((SliderEffectComponentEntity) this.container.component).slideBindingX=Float.parseFloat(val);
					}else if(tagName.equals("SlideBindingY")){
						((SliderEffectComponentEntity) this.container.component).slideBindingY=Float.parseFloat(val);
					}else if(tagName.equals("SlideBindingWidth")){
						((SliderEffectComponentEntity) this.container.component).slideBindingWidth=Float.parseFloat(val);
					}else if(tagName.equals("SlideBindingHeight")){
						((SliderEffectComponentEntity) this.container.component).slideBindingHeight=Float.parseFloat(val);
					}else if(tagName.equals("SlideBindingAlpha")){
						((SliderEffectComponentEntity) this.container.component).slideBindingAlpha=Float.parseFloat(val);
					}else if(tagName.equals("SwitchType")){
						((SliderEffectComponentEntity) this.container.component).switchType=val;
					}else if(tagName.equals("Repeat")){
						((SliderEffectComponentEntity) this.container.component).repeat=Integer.parseInt(val);
					}else if(tagName.equals("IsLoop")){
						((SliderEffectComponentEntity) this.container.component).isLoop=Boolean.parseBoolean(val);
					}else if(tagName.equals("IsEndToStart")){
						((SliderEffectComponentEntity) this.container.component).isEndToStart=Boolean.parseBoolean(val);
					}else if(tagName.equals("AniType")){
						subImageItem.aniType=val;
					}else if(tagName.equals("AniProperty")){
						subImageItem.aniProperty=val;
					}else if(tagName.equals("Delay")){
						subImageItem.delay=Long.parseLong(val);
					}else if(tagName.equals("Duration")){
						subImageItem.duration=Long.parseLong(val);
					}else if(tagName.equals("SourceID")){
						subImageItem.sourceID=val;
						((SliderEffectComponentEntity) this.container.component).subItems.add(subImageItem);
					}
				}
				//是否是管理部分的解析
				if(isLinkPage){
					if(tagName.equals("LinkID")){
						this.container.component.getLinkPageObj().entityID = val;
					}else if(tagName.equals("Rate")){
						this.container.component.getLinkPageObj().rate = Float.parseFloat(val);
						isLinkPage = false;
					}
				}else if (arg1.equals("ID")&&!isComponent) {
					this.container.ID = val;
				}else if(tagName.equals("Alpha")&&!isComponent){
					this.container.component.alpha=Float.parseFloat(val);
				}else if(tagName.equals("AnimationRepeat")&&!isComponent){
					this.container.component.animationRepeat= Integer.parseInt(val);
				}else if (tagName.equals("IsAllowUserZoom")||tagName.equals("IsAllowZoom")) {
					this.container.component.isAllowUserZoom = Boolean
							.parseBoolean(val);
				} else if (tagName.equals("ClassName")) {
					if (val.indexOf("HLGIFComponent") > 0) {
						this.container.setComponent(new GifComponentEntity(container.component));
						isGifComponent = true;
					}else if (val.indexOf("HLSWFComponent") > 0) {
						this.container.setComponent(new GifComponentEntity(container.component));
						isGifComponent = true;
					}else if (val.indexOf("HLRollingTextComponent") > 0 || val.indexOf("hlEnglishRollingText") > 0) {
						this.container.setComponent(new TextComponentEntity(container.component));
						isText = true;
					}else if (val.indexOf("HLLocalPDFComponent") > 0) {
						isPDFComponent = true;
						this.container.setComponent(new PDFComponentEntity());
					}else if (val.indexOf("HLTemplateComponent")>0){
						isMoudleComponent = true;
						MoudleComponentEntity moduleComponent = new MoudleComponentEntity(this.container.getComponent());
						this.container.setComponent(moduleComponent);
					}else if (val.endsWith("HLLocalVideoComponent")){
						//isVideoComponent = true;
						this.container.setComponent(new VideoComponentEntity());
					}else if (val.endsWith("HLHtml5Component")){
						this.container.setComponent(new HTMLComponentEntity());
					} else if (val.indexOf("HLCounterComponent") > 0) {
						this.container.setComponent(new CounterEntity());
						isCounter = true;
					}else if (val.indexOf("HLTimerComponent") > 0) {
						isTimer = true;
						this.container.setComponent(new TimerEntity());
					}else if (val.indexOf("HLSWFFileComponent") > 0) {
						this.container.setComponent(new SWFFileEntity());
						isSwfFile=true;
					}else if (val.indexOf("HLSliderEffectComponent") > 0) {
						this.container.setComponent(new SliderEffectComponentEntity(container.component));
						isSliderEffectComponent=true;
					}
					this.container.component.className = val;
				} else if (tagName.equals("Y")) {
					if(isContainerPosition){
						this.container.y = Float.parseFloat(val);
						isContainerPosition=false;
					}
				} else if (tagName.equals("Name")) {
					this.container.name = val;
				} else if (tagName.equals("Width")) {
					this.container.width = Float.parseFloat(val);
				} else if (tagName.equals("Height")) {
					this.container.height = Float.parseFloat(val);
				} else if (tagName.equals("Rotation")) {
					this.container.setRotation(Float.parseFloat(val));
				} else if (tagName.equals("X")) {
					if(isContainerPosition){
						this.container.x = Float.parseFloat(val);
					}
				} else if (tagName.equals("IsPlayAnimationAtBegining")) {
					this.container.isPlayAnimationAtBegining = Boolean
							.valueOf(val);
				} else if (tagName.equals("IsPlayVideoOrAudioAtBegining")) {
					this.container.isPlayVideoOrAudioAtBegining = Boolean
							.valueOf(val);
				} else if (tagName.equals("IsHideAtBegining")) {
					this.container.isHideAtBegining = Boolean.valueOf(val);
				}else if (tagName.equals("IsStroyTelling")) {
					this.container.IsStroyTelling = Boolean.valueOf(val);
				}else if (tagName.equals("IsMoveScale")) {
					this.container.isMoveScale = Boolean.valueOf(val);
				} else if (tagName.equals("IsPushBack")) {
					this.container.isPushBack = Boolean.valueOf(val);
				}else if (tagName.equals("AutoLoop")) {
					this.container.component.autoLoop = Boolean.valueOf(val);
					this.container.autoLoop = Boolean.valueOf(val);
				} else if (tagName.equals("Delay")) {
					try {
						if (null != val) {
							this.container.component.delay = Double.valueOf(val
									.trim());
						}

					} catch (Exception ex) {
						this.container.component.delay = 0;
					}

				} else if (tagName.equals("MultiMediaUrl")) {
					this.container.component.multiMediaUrl = val;
				} else if (tagName.equals("isSynchronized")) {
					this.container.component.isSynchronized = Boolean
							.getBoolean(val);
				}else if (tagName.equals("HtmlUrl")) {
					this.container.component.htmlUrl = val;
				}else if (tagName.equals("IsPlayOnetime")) {
					((GifComponentEntity) this.container.component)
							.setIsPlayOnetime(Boolean.valueOf(val));
				}else if (tagName.equals("GifDuration")) {
					((GifComponentEntity) this.container.component)
							.setGifDuration(Double.valueOf(val));
				} else if (tagName.equals("ImageType")) {
					this.container.component.imageType = val;
				} else if (tagName.equals("ImageScale")) {
					this.container.component.imageScale = Double.parseDouble(val);
				} else if (tagName.equals("ZoomType")) {
					this.container.component.zoomType = val;
				}  else if (tagName.equals("LocalSourceID") ||tagName.equals("SourceID") ) {
					if (isGifComponent == true) {//如果是GIF则设置图片序列
						((GifComponentEntity) this.container.component)
								.getFrameList().add(val);
					} else {
						this.container.component.localSourceId = val;
					}
					
				}  else if (tagName.equals("DownSourceID")) {
					this.container.component.downSourceID = val;
					//以下只对textcompoent
				} else if (tagName.equals("minValue")){
					((CounterEntity)this.container.component).minValue = Integer.valueOf(val); 
				} else if (tagName.equals("maxValue")){
					((CounterEntity)this.container.component).maxValue = Integer.valueOf(val); 
				}else if (tagName.equals("scope")){
					((CounterEntity)this.container.component).scope = val; 
				}else if(tagName.equals("IsLoop")&&isSwfFile){
					((SWFFileEntity) this.container.component).isLoop=Boolean.parseBoolean(val);
					isSwfFile=false;
				}else if(tagName.equals("IsEnableGyroHor")){
					this.container.component.IsEnableGyroHor=Boolean.valueOf(val);
				}else if(tagName.equals("PtX")){
					stroyTellPt = new PointF();
					stroyTellPt.x = Float.parseFloat(val);
				}else if(tagName.equals("PtY")){
					stroyTellPt.y = Float.parseFloat(val);
					this.container.component.ptList.add(stroyTellPt);
				}
				//解析滑动关联的属性
				else if(tagName.equals("IsPageTweenSlide")){
					this.container.component.isPageTweenSlide = Boolean.parseBoolean(val);
				}else if(tagName.equals("IsPageInnerSlide")){
					this.container.component.isPageInnerSlide = Boolean.parseBoolean(val);
				}else if(tagName.equals("SlideBindingX")){
					this.container.component.slideBindingX = Integer.parseInt(val);
				}else if(tagName.equals("SlideBindingY")){
					this.container.component.slideBindingY = Integer.parseInt(val);
				}else if(tagName.equals("SlideBindingWidth")){
					this.container.component.slideBindingWidth = Integer.parseInt(val);
				}else if(tagName.equals("SlideBindingHeight")){
					this.container.component.slideBindingHeight = Integer.parseInt(val);
				}else if(tagName.equals("SlideBindingAlpha")){
					this.container.component.slideBindingAlha = Float.parseFloat(val);
				}else if(tagName.equals("SliderHorRate")){
					this.container.component.sliderHorRate = Float.parseFloat(val);
				}else if(tagName.equals("SliderVerRate")){
					this.container.component.sliderVerRate = Float.parseFloat(val);
				}
				if(isCounter){
					if (tagName.equals("fontSize")){
						((CounterEntity) this.container.component).fontSize = val;
					}else if (tagName.equals("fontColor")){
						((CounterEntity) this.container.component).fontColor = val;
					} 
				}else if(isTimer){
					if (tagName.equals("isPlayOrderbyDesc")){
						((TimerEntity) this.container.component).isPlayOrderbyDesc = Boolean.parseBoolean(val);
					}else if (tagName.equals("isPlayMillisecond")){
						((TimerEntity) this.container.component).isPlayMillisecond = Boolean.parseBoolean(val);
					} else if (tagName.equals("isStaticType")){
						((TimerEntity) this.container.component).isStaticType = Boolean.parseBoolean(val);
					}else if (tagName.equals("maxTimer")){
						((TimerEntity) this.container.component).setMaxTimer(Integer.parseInt(val));
					}else if (tagName.equals("fontSize")){
						((TimerEntity) this.container.component).fontSize = val;
					}else if (tagName.equals("fontColor")){
						((TimerEntity) this.container.component).fontColor = val;
					} 
					
				}else if(isText){
					if (tagName.equals("htmlXML")){
						((TextComponentEntity) this.container.component).setHtmlXML(val);
					} else if (tagName.equals("bgcolor")){
						((TextComponentEntity) this.container.component).setBgcolor(val);
					}else if (tagName.equals("bgalhpa")){
						((TextComponentEntity) this.container.component).setBgalpha(val);
					}else if (tagName.equals("borderVisible")){
						((TextComponentEntity) this.container.component).setBorderVisible(val);
					}else if (tagName.equals("borderColor")){
						((TextComponentEntity) this.container.component).setBorderColor(val);
					}else if (tagName.equals("TextContent")){
						((TextComponentEntity) this.container.component).setTextContent(val);
					}else if (tagName.equals("LineHeight")){
						lineEntity.setLineHeight(val);
						((TextComponentEntity) this.container.component).getLineEntitys().add(lineEntity);
					}else if (tagName.equals("color")){
						((TextComponentEntity) this.container.component).setTextColor(val);
					}else if (tagName.equals("TotalParaTextContent")){
						if (val.startsWith("@")){
							val = val.replaceFirst("@", "");
						}
						((TextComponentEntity) this.container.component).setTotalParaTextContent(val);
					}else if (tagName.equals("fontSize")){
						((TextComponentEntity) this.container.component).setFontSize(val);
					}else if (tagName.equals("fontWeight")){
						((TextComponentEntity) this.container.component).setFontWeight(val);
					}else if (tagName.equals("fontStyle")){
						((TextComponentEntity) this.container.component).setFontStyle(val);
					}else if (tagName.equals("fontFamily")){
						((TextComponentEntity) this.container.component).setFontFamily(val);
					}else if (tagName.equals("lineHeight")){
						((TextComponentEntity) this.container.component).setLineHeight(val);
					}else if (tagName.equals("textAlign")){
						((TextComponentEntity) this.container.component).setTextAlign(val);
					} 
				}else if (tagName.equals("HtmlFolder")){
					((HTMLComponentEntity) this.container.component).setHtmlFolder(val);
				}else if (tagName.equals("IndexHtml")){
					((HTMLComponentEntity) this.container.component).setIndexHtml(val);
				}  
				//textcomponent结束
				
				//以下针对PDF component
				else if (isPDFComponent){
					if (tagName.equals("PDFSourceID")){
						((PDFComponentEntity) this.container.component).setPdfSourceID(val);
					}else if (tagName.equals("PDFPageIndex")){
						((PDFComponentEntity) this.container.component).setPdfPageIndex(val);
					}else if (tagName.equals("IntailWidth")){
						((PDFComponentEntity) this.container.component).setIntailWidth(val);
					}else if (tagName.equals("IntailHeight")){
						((PDFComponentEntity) this.container.component).setIntailHeight(val);
					}else if (tagName.equals("IsAllowUserZoom")){
						((PDFComponentEntity) this.container.component).setIsAllowUserZoom(val);
					}
				}
			
				
				//if (isVideoComponent == true){
					if (tagName.equals("VideoControlBarIsShow")){
						if(container.component instanceof VideoComponentEntity){
							((VideoComponentEntity) this.container.component).setVideoControlBarIsShow(Boolean
									.parseBoolean(val));
						}
						this.container.component.showProgress = Boolean.parseBoolean(val);
					}
					if (tagName.equals("CoverSourceID")){
						if(container.component instanceof VideoComponentEntity){
							((VideoComponentEntity) this.container.component).setCoverSourceID(val);
						}
					}
					if (tagName.equals("IsOnlineSource")){
						(this.container.component).setOnlineSource(Boolean.parseBoolean(val));
					}
				//}
				
				//PDF component
				
				//以下针对模板数据
				if (isMoudleComponent){
					MoudleComponentEntity componentEntity = (MoudleComponentEntity) this.container.component;
					if(isPaintUIComponent){
						if ("lineThickness".equals(arg1)) {
								componentEntity.lineThick=Integer.parseInt(val);
							}
					}
					if(isVerConnectLine){//如果是连线题
						if(null==componentEntity.cellList)
						componentEntity.cellList=new ArrayList<Cell>();
						if ("CellID".equals(arg1)) {
							cell.mCellID = val;
						}else if ("SourceID".equals(arg1) || "SourceID".equals(arg1)) {
							cell.mSourceID = val;
						}else if ("LinkID".equals(arg1)) {
							cell.mLinkID = val;
						}else if ("CellType".equals(arg1) ) {
							componentEntity.cellList.add(cell);
						}else if ("LineGap".equals(arg1)) {
							componentEntity.mLineGap = Integer.parseInt(val);
						}else if ("RowGap".equals(arg1)) {
							componentEntity.mRowOrColumnGap = Integer.parseInt(val);
						}else if ("LineColor".equals(arg1)) {
							componentEntity.lineColor = Integer.parseInt(val.substring(2),16);
						}else if ("LineThickness".equals(arg1)) {
							componentEntity.lineThick=Integer.parseInt(val);
						}else if ("LineAlpha".equals(arg1)) {
							componentEntity.lineAlpha=Float.parseFloat(val);
						}
					}else if(isHorConnectLine){
						if(null==componentEntity.cellList)
						componentEntity.cellList=new ArrayList<Cell>();
						if ("CellID".equals(arg1)) {
							cell.mCellID = val;
						}else if ("SourceID".equals(arg1) || "SourceID".equals(arg1)) {
							cell.mSourceID = val;
						}else if ("LinkID".equals(arg1)) {
							cell.mLinkID = val;
						}else if ("CellType".equals(arg1) ) {
							componentEntity.cellList.add(cell);
						}else if ("LineGap".equals(arg1)) {
							componentEntity.mLineGap = Integer.parseInt(val);
						}else if ("RowGap".equals(arg1)) {
							componentEntity.mRowOrColumnGap = Integer.parseInt(val);
						}else if ("LineColor".equals(arg1)) {
							componentEntity.lineColor =Integer.parseInt(val.substring(2),16);
						}else if ("LineThickness".equals(arg1)) {
							componentEntity.lineThick=Integer.parseInt(val);
						}else if ("LineAlpha".equals(arg1)) {
							componentEntity.lineAlpha=Float.parseFloat(val);
						}
					}else if(isQuestion){
						if("SourceID".equalsIgnoreCase(tagName)){
							questionEntity.titleResource = val;
						}else if("Type".equalsIgnoreCase(tagName)){
							questionEntity.questionType = val;
						}else if("Score".equalsIgnoreCase(tagName)){
							questionEntity.score = Integer.parseInt(val);
						}else if("ImageSource".equalsIgnoreCase(tagName)){
							questionEntity.imgSource = val;
						}else if("SoundSource".equalsIgnoreCase(tagName)){
							questionEntity.soundSource = val;
						}else if("Title".equalsIgnoreCase(tagName)){
							questionEntity.getOptionList().add(new OptionEntity(val));
						}else if("Index".equalsIgnoreCase(tagName)){
							questionEntity.getRightAnswerList().add(Integer.parseInt(val));
						}
					}else if(isMaskSlider){
						if("ID".equalsIgnoreCase(tagName)){
							maskBean.imgSource = val;
						}else if("SourceW".equalsIgnoreCase(tagName)){
							maskBean.sourceW = Float.parseFloat(val);
						}else if("SourceH".equalsIgnoreCase(tagName)){
							maskBean.sourceH = Float.parseFloat(val);
						}else if("RectX".equalsIgnoreCase(tagName)){
							maskBean.rectX = Float.parseFloat(val);
						}else if("RectY".equalsIgnoreCase(tagName)){
							maskBean.rectY = Float.parseFloat(val);
						}else if("RectW".equalsIgnoreCase(tagName)){
							maskBean.rectW = Float.parseFloat(val);
						}else if("RectH".equalsIgnoreCase(tagName)){
							maskBean.rectH = Float.parseFloat(val);
						}else if("Title".equalsIgnoreCase(tagName)){
							maskBean.title = val;
						}else if("Dec".equalsIgnoreCase(tagName)){
							maskBean.dec = val;
						}else if("IsCenterZoom".equalsIgnoreCase(tagName)){
							maskBean.isCenterZoom = Boolean.parseBoolean(val);
						}else if("AudioSourceID".equalsIgnoreCase(tagName)){
							maskBean.audioSourceID = val;
							componentEntity.maskBeanList.add(maskBean);
						}else if("IsShowControllerPoint".equalsIgnoreCase(tagName)){
							componentEntity.isShowControllerPoint = Boolean.parseBoolean(val);
						}
					}
					else{
						if ("ModuleID".equals(tagName)) {
							componentEntity.setModuleID(val);;
							isPaintUIComponent = val.contains("HLPaintingUIComponent");
							isVerConnectLine = val.contains("HLConnectLineUIComponent");
							isHorConnectLine = val.contains("HLConnectHorLineUIComponent");
							isMaskSlider = val.contains("HLMaskSliderImageUIComponent");
						}else if ("sourceID".equals(tagName) || "SourceID".equals(tagName)) {
							componentEntity.getSourceIDList().add(val);
						}else if ("SelectedSourceID".equals(tagName)) {
							componentEntity.getSelectSourceIDList().add(val);
						}
						else if ("mouseUpSourceID".equals(tagName)) {
							componentEntity.getSourceIDList().add(val);
						}else if ("mouseDownSourceID".equals(tagName)) {
							componentEntity.getDownIDList().add(val);
						}else if ("itemWidth".equals(tagName) ||"ItemWidth".equals(tagName)) {
							Double d = Double.parseDouble(val);
							componentEntity.setItemWidth(d.intValue());
						}else if ("itemHeight".equals(tagName) || "ItemHeight".equals(tagName)) {
							Double d = Double.valueOf(val);
							componentEntity.setItemHeight(d.intValue());
						}else if ("TimerDelay".equals(tagName)) {
							componentEntity.setTimerDelay(Long.valueOf(val));
						}else if ("CellNumber".equals(tagName)) {
							componentEntity.setCellNumber(Integer.valueOf(val));
						}else if ("BookWidth".equalsIgnoreCase(tagName)) {
							componentEntity.setBookWidth(Integer.valueOf(val));
						}else if ("BookHeight".equalsIgnoreCase(tagName)) {
							componentEntity.setBookHeight(Integer.valueOf(val));
						}else if ("ServerAddress".equalsIgnoreCase(tagName)) {
							componentEntity.setServerAddress(val);
						}else if ("BgSourceID".equalsIgnoreCase(tagName)) {
							componentEntity.setBgSourceID(val);
						}else if ("Speed".equalsIgnoreCase(tagName)) {
							componentEntity.speed = Integer.parseInt(val);
						}else if("IsAutoRotation".equalsIgnoreCase(tagName)){
							componentEntity.isAutoRotation = Boolean.parseBoolean(val);
						}else if("RotationType".equalsIgnoreCase(tagName)){
							componentEntity.rotationType = val;
						}else if("IsShowNavi".equalsIgnoreCase(tagName)){
							componentEntity.isShowNavi = Boolean.parseBoolean(val);;
						}else if("RenderDes".equals(tagName)){
							componentEntity.renderDes.add(val);
						}else if("IsHorSlider".equalsIgnoreCase(tagName)){
							componentEntity.isHorSlider=Boolean.parseBoolean(val);
						}
					}
					if("SourceID".equals(tagName)){
						if(isLeftMRender||isRightRender||isMiddleMRender){
							curRenderBean.sourceID=val;
						}
					}else if("SourceIndex".equals(tagName)){
							curRenderBean.sourceIndex=Integer.parseInt(val);
							if(isLeftMRender){
								componentEntity.leftRenderBean.add(curRenderBean);
							}else if(isMiddleMRender){
								componentEntity.middleRenderBean.add(curRenderBean);
							}else if(isRightRender){
								componentEntity.rightRenderBean.add(curRenderBean);
						}
					}
				}
				
			}
			
			
			if (isBehavior) {
				if (tagName.equals("EventName")) {
					this.Behavior.EventName = val;
				} else if (tagName.equals("FunctionObjectID")) {
					this.Behavior.FunctionObjectID = val;
				} else if (tagName.equals("FunctionName")) {
					this.Behavior.FunctionName = val;
				} else if (tagName.equals("IsRepeat")) {
					this.Behavior.IsRepeat = Boolean.valueOf(val);
				} else if (tagName.equals("Value")) {
					this.Behavior.Value = val;
				} else if (tagName.equals("EventValue")) {
					this.Behavior.EventValue = val;
				}
			}
			if (isAnimation) {
				//=============================================
				if (!isSenior) {
					if (tagName.equals("ClassName")) {
						this.animation.ClassName = val;
						if (animation.ClassName != null
								&& animation.ClassName
										.indexOf("::AnimationSenior") > -1) {
							isSenior = true;
							animation.hEntitys=new ArrayList<SeniorAnimationEntity>();
						} else {
							isSenior = false;
						}
					}
				}else{
					if(isAnimationModel){
						if (tagName.equals("ObjX")) {
							seniorAnimationEntity.mX=Float.parseFloat(val);
						}else if (tagName.equals("ObjY")) {
							seniorAnimationEntity.mY=Float.parseFloat(val);
						}else if (tagName.equals("ObjWidth")) {
							seniorAnimationEntity.mWidth=Float.valueOf(val);
						}else if (tagName.equals("ObjHeight")) {
							seniorAnimationEntity.mHeight=Float.valueOf(val);
						}else if (tagName.equals("ObjRotation")) {
							seniorAnimationEntity.mDegree=Float.valueOf(val);
						}else if (tagName.equals("ObjAlpha")) {
							seniorAnimationEntity.mAlpha=Float.valueOf(val);
							animation.hEntitys.add(seniorAnimationEntity);
							isAnimationModel=false;
						}else if (tagName.equals("Duration")) {
							seniorAnimationEntity.mDuration=Float.valueOf(val);
						}
					}
				}
				//=============================================
				if (!isPoint) {
					if (tagName.equals("ClassName")) {
						this.animation.ClassName = val;
						if (animation.ClassName != null
								&& animation.ClassName
										.indexOf("::CatmullRomMovePath") > -1) {
							isPoint = true;
						} else {
							isPoint = false;
						}
					}
				}
				if (isPoint) {
					if (tagName.equals("PlayTimes")) {
						if(StringUtils.isEmpty(val)){
							this.animation.Repeat = "0";
						}else{
							int v = Integer.parseInt(val);
							v++;
							val = Integer.toString(v);
							this.animation.Repeat = val;
						}
					} else if (tagName.equals("Duration")) {
						this.animation.Duration = val;
					} else if (tagName.equals("Delay")) {
						this.animation.Delay = getSecondStringFromMileconde(val);
					} else if (tagName.equals("X")) {
						this.p.x = Float.parseFloat(val);
					} else if (tagName.equals("Y")) {
						this.p.y = Float.parseFloat(val);
					} else if (tagName.equals("IsLoop")) {
						if (!val.equals("false")) {
							this.animation.Repeat = "-1";
						}
					}
				} else {
					if (tagName.equals("CurrentAnimationIndex")) {
						this.animation.CurrentAnimationIndex = val;
					} else if (tagName.equals("Repeat")) {
						this.animation.Repeat = val;
					} else if (tagName.equals("Delay")) {
						if(!isAnimationModel){
							this.animation.Delay = getSecondStringFromMileconde(val);
						}
					} else if (tagName.equals("Duration")) {
						if(!isAnimationModel){
							this.animation.Duration = val;
						}
					} else if (tagName.equals("AnimationEnterOrQuit")) {
						this.animation.AnimationEnterOrQuit = val;
					} else if (tagName.equals("AnimationTypeLabel")) {
						this.animation.AnimationTypeLabel = val;
					} else if (tagName.equals("CustomProperties")) {
						this.animation.CustomProperties = val;
					} else if (tagName.equals("AnimationType")) {
						this.animation.AnimationType = val;
					}  else if (tagName.equals("IsKeep")) {
						this.animation.IsKeep = val;
					}  else if (tagName.equals("EaseType")) {
						this.animation.EaseType = val;
					}  else if (tagName.equals("IsKeepEndStatus")) {
						this.animation.isKeepEndStatus = Boolean.parseBoolean(val);
					}
					
				}
			}

			if (isBackground) {

				if (tagName.equals("ID")) {
					this.page.getBackground().ID = val;
				} else if (tagName.equals("Name")) {
					this.page.getBackground().name = val;
				} else if (tagName.equals("Width")) {
					this.page.getBackground().width = Float.parseFloat(val);
				} else if (tagName.equals("Height")) {
					this.page.getBackground().height = Float.parseFloat(val);
				} else if (tagName.equals("Rotation")) {
					this.page.getBackground().setRotation(Float.parseFloat(val));
				} else if (tagName.equals("X")) {
					this.page.getBackground().x = Float.parseFloat(val);
				} else if (tagName.equals("Y")) {
					this.page.getBackground().y = Float.parseFloat(val);
				} else if (tagName.equals("IsPlayAnimationAtBegining")) {
					this.page.getBackground().isPlayAnimationAtBegining = Boolean
							.valueOf(val);
				} else if (tagName.equals("IsHideAtBegining")) {
					this.page.getBackground().isHideAtBegining = Boolean
							.valueOf(val);
				} else if (tagName.equals("ClassName")) {
					this.page.getBackground().component.className = val;
				} else if (tagName.equals("LocalSourceID")) {
					this.page.getBackground().component.localSourceId = val;
				}
			}

			if (isPlaySequenceDelay) {
				this.page.getSequence().Delay.add(Long.parseLong(val));
			}
		}
		if (isPlaySequence) {
			if (arg1.equals("ID")) {
				group.ContainerID.add(val);
			}
		}
		if (arg1.equals("Point")) {
			animation.Points.add(p);
			this.p = new PointF();
		}
		if (arg1.equals("Behavior")) {
			Behavior.triggerPageID = this.page.getID();
			this.container.behaviors.add(Behavior);
			
			Behavior = new BehaviorEntity();
		}
		if (arg1.equals("Component")) {
			isRightRender=false;
			isBackground = false;
			isBehavior = true;
			this.isMoudleComponent = false;
			isVerConnectLine = false;
			isPaintUIComponent=false;
			isHorConnectLine=false;
		}

		if (arg1.equals("Animations")||arg1.equals("Animation")) {
			if (animation.AnimationType != null) {
				if(!container.animations.contains(animation))container.animations.add(animation);
				animation = new AnimationEntity();
			} else if (animation.Points.size() > 0) {
				if(!container.animations.contains(animation))container.animations.add(animation);
				animation = new AnimationEntity();
			}
			isContainers = true;
			isAnimation = false;
			isPoint = false;
			isSenior=false;
		} else if (arg1.equals("Container")) {
			if (container.ID != null) {
				page.getContainers().add(container);
				container = new ContainerEntity();
				isGifComponent = false;
				isPDFComponent = false;
				isMoudleComponent = false;
			}
		} else if (arg1.equals("Containers")) {
			isContainers = false;
			isAnimation = false;
		}
		if (arg1.equals("Group")) {
			page.getSequence().Group.add(group);
			group = new GroupEntity();
		}
		
		if (arg1.equals("NavePageId")){
			page.getNavePageIds().add(val);
		}
		val = "";

		flg = false;
	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
	}

	@Override
	public void setDocumentLocator(Locator arg0) {
	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void startElement(String arg0, String arg1, String arg2,
			Attributes arg3) throws SAXException {
		
		this.tagName = arg1;
		if (tagName.equals("Containers")) {
			ispage = false;
			isContainers = true;
		} else if (tagName.equals("Background")) {
			isContainers = false;
			isAnimation = false;
			isBackground = true;
			isPlaySequence = false;
		}else if (tagName.equals("Container")) {
			isContainerPosition=true;
		} else if (tagName.equals("Animations")||tagName.equals("Animation")) {
			isAnimation = true;
			isContainers = false;
		}else if (tagName.equals("Component")) {
			isComponent = true;
			isCounter = false;
			isText = false;
			isTimer = false;
			isQuestion = false;
			isMaskSlider = false;
			isSliderEffectComponent=false;
		}else if (tagName.equals("Question")) {
			isQuestion = true;
			MoudleComponentEntity componentEntity = (MoudleComponentEntity) this.container.component;
			questionEntity = new QuestionEntity();
			componentEntity.questionList.add(questionEntity);
		}else if (tagName.equals("Source")) {
			if(isMaskSlider)maskBean = new MaskBean();
		}else if (tagName.equals("LinkageObj")) {
			isLinkPage = true;
		}else if (tagName.equals("Behaviors")) {
			isComponent = false;
		}
		if(isSliderEffectComponent){
			if(tagName.equals("Image")){
				subImageItem=new SubImageItem();
			}
		}
		
		if(isVerConnectLine){
			if(tagName.equals("RightCell")){
				cell = new Cell();
				cell.mCellType = "RIGHT_CELL";
			}else if(tagName.equals("LeftCell")){
				cell = new Cell();
				cell.mCellType = "LEFT_CELL";
			}
		}
		if(isHorConnectLine){
			if(tagName.equals("UpCell")){
				cell = new Cell();
				cell.mCellType = "UP_CELL";
			}else if(tagName.equals("DownCell")){
				cell = new Cell();
				cell.mCellType = "DOWN_CELL";
			}
		}
		if(isSenior){
			if(tagName.equals("AnimationModel")){
				isAnimationModel=true;
				seniorAnimationEntity=new SeniorAnimationEntity();
			}
		}
		if (arg1.equals("PlaySequence")) {
			isPlaySequence = true;
			isContainers = false;
			isAnimation = false;
			isBackground = false;
		}
		if (arg1.equals("PlaySequenceDelay")) {
			isPlaySequenceDelay = true;
			isPlaySequence = false;
			isContainers = false;
			isAnimation = false;
			isBackground = false;
		}
		if(tagName.equals("LeftRender")){
			isLeftMRender=true;
		}
		if(tagName.equals("MiddleRender")){
			isMiddleMRender=true;
			isLeftMRender=false;
		}
		if(tagName.equals("RightRender")){
			isRightRender=true;
			isMiddleMRender=false;
		}
		if(isLeftMRender){
			if(tagName.equals("Render")){
				curRenderBean=new MRenderBean();
			}
		}
		if(isMiddleMRender){
			if(tagName.equals("Render")){
				curRenderBean=new MRenderBean();
			}
		}
		if(isRightRender){
			if(tagName.equals("Render")){
				curRenderBean=new MRenderBean();
			}
		}
		flg = true;

		// 处理textocmponent的layoutformat
		if (tagName.equals("defaultTextLayoutFormat")) {
			if (arg3 != null) {
				((TextComponentEntity) this.container.component)
						.setFontFamily(arg3.getValue("fontFamily"));
				((TextComponentEntity) this.container.component)
						.setFontSize(arg3.getValue("fontSize"));
				((TextComponentEntity) this.container.component)
						.setFontWeight(arg3.getValue("fontWeight"));
				((TextComponentEntity) this.container.component)
						.setFontStyle(arg3.getValue("fontStyle"));
				((TextComponentEntity) this.container.component)
						.setTextDecoration(arg3.getValue("textDecoration"));
				((TextComponentEntity) this.container.component)
						.setTextAlign(arg3.getValue("textAlign"));
				((TextComponentEntity) this.container.component)
						.setLineHeight(arg3.getValue("lineHeight"));
				((TextComponentEntity) this.container.component)
						.setTrackingLeft(arg3.getValue("trackingLeft"));
				((TextComponentEntity) this.container.component)
						.setTrackingRight(arg3.getValue("trackingRight"));
				((TextComponentEntity) this.container.component)
						.setTextColor(arg3.getValue("color"));
			}
		}
	}

	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
	}
	
	/**
	 * 将软件传过来的秒为单位的字符串转化成毫秒
	 * @param val
	 * @return
	 */
	public static String getSecondStringFromMileconde(String val){
		Float f = Float.parseFloat(val);
		f =f*1000;
		int i = f.intValue();
		return Integer.toString(i);
	}
}

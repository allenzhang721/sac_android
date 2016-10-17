package com.hl.android.book.entity;

import java.util.ArrayList;

public class TextComponentEntity extends ComponentEntity {
	// <htmlXML>%3CTextFlow%20blockProgression%3D%22tb%22%20color%3D%22%23000000%22%20fontFamily%3D%22%E6%96%B0%E5%AE%8B%E4%BD%93%22%20fontSize%3D%2236%22%20fontStyle%3D%22normal%22%20fontWeight%3D%22normal%22%20lineThrough%3D%22false%22%20textAlign%3D%22left%22%20textDecoration%3D%22none%22%20verticalAlign%3D%22top%22%20whiteSpaceCollapse%3D%22preserve%22%20version%3D%222.0.0%22%20xmlns%3D%22http%3A%2F%2Fns.adobe.com%2FtextLayout%2F2008%22%3E%3Cp%3E%3Cspan%3E%20%20%20%20%20fdgdg%3C%2Fspan%3E%3C%2Fp%3E%3Cp%3E%3Cspan%3Esgs%20%20%20%20fgsgsgs%3C%2Fspan%3E%3C%2Fp%3E%3Cp%3E%3Cspan%3Efgdsf%20%20%20gdfgfdg%3C%2Fspan%3E%3C%2Fp%3E%3C%2FTextFlow%3E
	// </htmlXML>
	// <bgcolor>16777215</bgcolor>
	// <bgalhpa>1</bgalhpa>
	// <borderVisible>false</borderVisible>
	// <borderColor>6908265</borderColor>
	// <TextContent>%20%20%20%20%20fdgdg%0Asgs%20%20%20%20fgsgsgs%0Afgdsf%20%20%20gdfgfdg
	// </TextContent>
	// <defaultTextLayoutFormat fontFamily="%E6%96%B0%E5%AE%8B%E4%BD%93"
	// fontSize="36" fontWeight="normal" fontStyle="normal"
	// textDecoration="none" textAlign="left" />

	private String htmlXML;
	private String bgcolor;
	private String bgalpha;
	private String borderVisible;
	private String borderColor;
	private String textContent;
	private String defaultTextLayoutFormat;
	private String fontFamily;
	private String fontSize;
	private String fontWeight;
	private String fontStyle;
	private String textDecoration;
	private String textAlign;
	private String textColor;
	private String totalParaTextContent;
	private String lineHeight;
	private String trackingLeft;
	private String trackingRight;
	private ArrayList<TextComponentLineEntity> lineEntitys = new ArrayList<TextComponentLineEntity>();
	
	public TextComponentEntity(ComponentEntity component){
		if(component!=null){
			this.animationRepeat = component.animationRepeat;
			this.alpha=component.alpha;
		}
	}
	
	public ArrayList<TextComponentLineEntity> getLineEntitys() {
		return lineEntitys;
	}

	public void setLineEntitys(ArrayList<TextComponentLineEntity> lineEntitys) {
		this.lineEntitys = lineEntitys;
	}

	public String getHtmlXML() {
		return htmlXML;
	}

	public void setHtmlXML(String htmlXML) {
		this.htmlXML = htmlXML;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public String getBgalpha() {
		return bgalpha;
	}

	public void setBgalpha(String bgalphs) {
		this.bgalpha = bgalphs;
	}

	public String getBorderVisible() {
		return borderVisible;
	}

	public void setBorderVisible(String borderVisible) {
		this.borderVisible = borderVisible;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContext) {
		this.textContent = textContext;
	}

	public String getDefaultTextLayoutFormat() {
		return defaultTextLayoutFormat;
	}

	public void setDefaultTextLayoutFormat(String defaultTextLayoutFormat) {
		this.defaultTextLayoutFormat = defaultTextLayoutFormat;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public String getTextDecoration() {
		return textDecoration;
	}

	public void setTextDecoration(String textDecoration) {
		this.textDecoration = textDecoration;
	}

	public String getTextAlign() {
		return textAlign;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public String getTotalParaTextContent() {
		return totalParaTextContent;
	}

	public void setTotalParaTextContent(String totalParaTextContent) {
		this.totalParaTextContent = totalParaTextContent;
	}

	public String getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(String lineHeight) {
		this.lineHeight = lineHeight;
	}

	public String getTrackingLeft() {
		return trackingLeft;
	}

	public void setTrackingLeft(String trackingLeft) {
		this.trackingLeft = trackingLeft;
	}

	public String getTrackingRight() {
		return trackingRight;
	}

	public void setTrackingRight(String trackingRight) {
		this.trackingRight = trackingRight;
	}
	
}

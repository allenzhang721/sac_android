package com.hl.android.view;

public class ViewPageState {
	public boolean papared;
	public boolean playing;
	public boolean stopped;
	public boolean idied;
	public void setIdiled(){
		this.papared = false;
		this.stopped = false;
		this.playing = false;
	}
	public void setStoped(){
		this.papared = false;
		this.stopped = true;
		this.playing = false;	
	}
	public boolean isPapared() {
		return papared;
	}
	public void setPapared(boolean papared) {
		this.papared = papared;
	}
	public boolean isPlaying() {
		return playing;
	}
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
	public boolean isStopped() {
		return stopped;
	}
	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}
	public boolean isIdied() {
		return idied;
	}
	public void setIdied(boolean idied) {
		this.idied = idied;
	}
	
}

package com.jsonapi.test.list;

public class ListViewItem {
	
	private String text;
	
	private String url;
	
	private String videoId;
	
	public ListViewItem(String text, String url, String videoId) {
		this.url = url;
		this.text = text;
		this.videoId = videoId;
	}
	
	public String getVideoId() {
		return this.videoId;
	}
	
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}

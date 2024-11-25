package com.oo.search.model.dto;

public class SearchResult {
	private String source;
	private String title;
	private String content;
	
	public SearchResult() {
	}
	
	public SearchResult(String source, String title, String content) {
		this.source = source;
		this.title = title;
		this.content = content;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "SearchResult [source=" + source + ", title=" + title + ", content=" + content + "]";
	}
}
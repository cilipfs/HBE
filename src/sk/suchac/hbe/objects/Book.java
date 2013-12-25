package sk.suchac.hbe.objects;

import java.util.List;

public class Book {
	
	private int _id;
	private String title;
	private String abbreviation;
	private List<Chapter> chapters;
	private int totalChapters;
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	public List<Chapter> getChapters() {
		return chapters;
	}
	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}
	public int getTotalChapters() {
		return totalChapters;
	}
	public void setTotalChapters(int totalChapters) {
		this.totalChapters = totalChapters;
	}
	
}

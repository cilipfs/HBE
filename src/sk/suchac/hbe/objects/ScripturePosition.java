package sk.suchac.hbe.objects;

import java.io.Serializable;

public class ScripturePosition implements Serializable {
	
	private static final long serialVersionUID = -7066276067582111945L;
	
	private int book;
	private int chapter;
	
	public int getBook() {
		return book;
	}
	public void setBook(int book) {
		this.book = book;
	}
	public int getChapter() {
		return chapter;
	}
	public void setChapter(int chapter) {
		this.chapter = chapter;
	}
}

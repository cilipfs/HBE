package sk.suchac.hbe.objects;

import java.io.Serializable;

public class ScripturePosition implements Serializable {
	
	private static final long serialVersionUID = -7066276067582111945L;
	
	public ScripturePosition() {}
	
	public ScripturePosition(int bookId, int chapterId) {
		this.book = bookId;
		this.chapter = chapterId;
	}
	
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

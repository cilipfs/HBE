package sk.suchac.hbe.objects;

import java.util.List;

public class Chapter {
	
	private int _id;
	private int bookId;
	private String number;
	private List<Verse> verses;
	private Book book;
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public List<Verse> getVerses() {
		return verses;
	}
	public void setVerses(List<Verse> verses) {
		this.verses = verses;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	
}

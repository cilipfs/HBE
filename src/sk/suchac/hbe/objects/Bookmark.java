package sk.suchac.hbe.objects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bookmark {
	
	private long timestamp;
	private int bookId;
	private int chapterId;
	
	public Bookmark() {}
	
	public Bookmark(long timestamp, int bookId, int chapterId) {
		this.timestamp = timestamp;
		this.bookId = bookId;
		this.chapterId = chapterId;
	}
	
	public Bookmark(String bookmarkStr) {
		String[] bm = bookmarkStr.split("~");
		for (int i = 0; i < bm.length; i++) {
			if (i == 0) {
				this.timestamp = Long.parseLong(bm[i]);
			} else if (i == 1) {
				this.bookId = Integer.parseInt(bm[i]);
			} else if (i == 2) {
				this.chapterId = Integer.parseInt(bm[i]);
			}
		}
	}
	
	public String getDateString() {
		Date date = new Date(timestamp);
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return df.format(date);
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public String toString() {
		return String.valueOf(timestamp) + "~" + String.valueOf(bookId) + "~" + String.valueOf(chapterId);
	}
}

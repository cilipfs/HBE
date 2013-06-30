package sk.suchac.hbe.objects;

public class SearchResult {

	int bookId;
	int chapterId;
	String sample;
	
	public SearchResult() {}
	
	public SearchResult(int bookId,	int chapterId, String sample) {
		this.bookId = bookId;
		this.chapterId = chapterId;
		this.sample = sample;
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
	public String getSample() {
		return sample;
	}
	public void setSample(String sample) {
		this.sample = sample;
	}
}

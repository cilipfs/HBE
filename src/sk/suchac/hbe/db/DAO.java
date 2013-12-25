package sk.suchac.hbe.db;

import java.util.ArrayList;
import java.util.List;

import sk.suchac.hbe.objects.Book;
import sk.suchac.hbe.objects.Chapter;
import sk.suchac.hbe.objects.Verse;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DAO {
	// Database fields
	  private SQLiteDatabase database;
	  private DBHelper dbHelper;
	  private String[] allChapterColumns = { "_id",
	      "BOOK_ID", "NUMBER" };
	  private String[] allBookColumns = { "_id",
		      "TITLE", "ABBREVIATION" };
	  private String[] allVerseColumns = { "_id",
		      "CHAPTER_ID", "NUMBER", "TEXT" };

	  public DAO(Context context) {
		  dbHelper = new DBHelper(context);
	  }
	  
	  public void initialize() {
		  dbHelper.initialize();
	  }

	  public void open() throws SQLException {
		  database = dbHelper.openDataBase();
	  }

	  public void close() {
		  dbHelper.close();
	  }

	  public Chapter getChapter(int bookId, int number) {
		  String[] args = { Integer.toString(bookId), Integer.toString(number) };
		  Cursor cursor = database.query("CHAPTER",
				allChapterColumns, "BOOK_ID=? AND NUMBER=?", args, null, null, null);
		  cursor.moveToFirst();
		  Chapter chapter = cursorToChapter(cursor);
		  cursor.close();
		  
		  String[] args2 = { Integer.toString(chapter.getBookId()) };
		  cursor = database.query("BOOK",
				allBookColumns, "_id=?", args2, null, null, null);
		  cursor.moveToFirst();
		  Book book = cursorToBook(cursor);
		  chapter.setBook(book);
		  cursor.close();
		  
		  String[] args3 = { Integer.toString(chapter.get_id()) };
		  List<Verse> verses = new ArrayList<Verse>();
		  cursor = database.query("VERSE",
		        allVerseColumns, "CHAPTER_ID=?", args3, null, null, null);
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) {
			  Verse verse = cursorToVerse(cursor);
		      verses.add(verse);
		      cursor.moveToNext();
		  }
		  chapter.setVerses(verses);
		  cursor.close();
		  
		  return chapter;
	  }
	  
	  public List<Book> getBookList() {
		  List<Book> books = new ArrayList<Book>();
		  Cursor cursor = database.query("BOOK",
				allBookColumns, null, null, null, null, null);
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) {
			  Book book = cursorToBook(cursor);
		      books.add(book);
		      cursor.moveToNext();
		  }
		  cursor.close();
		  
		  for (Book b : books) {
			  String countQuery = "SELECT  COUNT(*) FROM CHAPTER WHERE BOOK_ID=" + b.get_id();
		      cursor = database.rawQuery(countQuery, null);
		      cursor.moveToFirst();
		      int count = cursor.getInt(0);
		      b.setTotalChapters(count);
		      cursor.close();
		  }
		  
		  return books;
	  }
	  
	  public Book getBook(int bookId) {
		  String countQuery = "SELECT * FROM BOOK WHERE _id=" + bookId;
	      Cursor cursor = database.rawQuery(countQuery, null);
	      cursor.moveToFirst();
	      Book book = cursorToBook(cursor);
	      cursor.close();
		  
		  return book;
	  }
	  
	  public int getTotalChaptersOfBook(int bookId) {
		  String countQuery = "SELECT  COUNT(*) FROM CHAPTER WHERE BOOK_ID=" + bookId;
	      Cursor cursor = database.rawQuery(countQuery, null);
	      cursor.moveToFirst();
	      int count = cursor.getInt(0);
	      cursor.close();
		  
		  return count;
	  }

	  private Chapter cursorToChapter(Cursor cursor) {
		  Chapter chapter = new Chapter();
		  chapter.set_id(cursor.getInt(0));
		  chapter.setBookId(cursor.getInt(1));
		  chapter.setNumber(cursor.getString(2));
		  return chapter;
	  }
	  
	  private Book cursorToBook(Cursor cursor) {
		  Book book = new Book();
		  book.set_id(cursor.getInt(0));
		  book.setTitle(cursor.getString(1));
		  book.setAbbreviation(cursor.getString(2));
		  return book;
	  }
	  
	  private Verse cursorToVerse(Cursor cursor) {
		  Verse verse = new Verse();
		  verse.set_id(cursor.getInt(0));
		  verse.setChapterId(cursor.getInt(1));
		  verse.setNumber(cursor.getString(2));
		  verse.setText(cursor.getString(3));
		  return verse;
	  }
}


package sk.suchac.hbe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sk.suchac.hbe.objects.ScripturePosition;
import sk.suchac.hbe.parser.BibleXmlHandler;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScriptureActivity extends Activity {
	
	TextView textField;
	Button buttonPrevious;
	Button buttonNext;
	
//	public final static String INTENT_PICKED = "sk.suchac.hbe.PICKED";
//	// index 0 -> book, index 1 -> chapter
//	ArrayList<Integer> pickedList = new ArrayList<Integer>();
//	
//	public final static String INTENT_TURNED = "sk.suchac.hbe.TURNED";
//	// 0 -> turnedBook, 1 -> turnedChapter
//	ArrayList<Integer> turnedList = new ArrayList<Integer>();
//	
//	// index 0 -> book, index 1 -> chapter
//	ArrayList<Integer> previousList = new ArrayList<Integer>();
//	// index 0 -> book, index 1 -> chapter
//	ArrayList<Integer> nextList = new ArrayList<Integer>();
	
	public final static String INTENT_SCRIPTURE_POSITION = "sk.suchac.hbe.SCRIPTURE_POSITION";
	ScripturePosition scriptPosition = new ScripturePosition();
	
	ScripturePosition scriptPrevious = new ScripturePosition();
	ScripturePosition scriptNext = new ScripturePosition();
	
	int TOTAL_BOOKS_NUMBER = 66;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scripture);
		
		textField = (TextView) findViewById(R.id.textView);
		textField.setText("");
		
		buttonPrevious = (Button) findViewById(R.id.button_previous);
		buttonNext = (Button) findViewById(R.id.button_next);
		
//		Intent intent = getIntent();
//		ArrayList<Integer> receivedPickedList = intent.getIntegerArrayListExtra(MainActivity.INTENT_PICKED);
//		if (receivedPickedList != null) {
//			pickedList = intent.getIntegerArrayListExtra(MainActivity.INTENT_PICKED);
//			int pickedBook = pickedList.get(0);
//			int pickedChapter = pickedList.get(1);
//			
//			turnedList.add(pickedBook);
//			turnedList.add(pickedChapter);
//			
//			displayScriptureText(pickedBook, pickedChapter);
//		} else {
//			turnedList = intent.getIntegerArrayListExtra(ScriptureActivity.INTENT_TURNED);
//			displayScriptureText(turnedList.get(0), turnedList.get(1));
//		}
		
		Intent intent = getIntent();
		scriptPosition = (ScripturePosition) intent.getSerializableExtra(MainActivity.INTENT_SCRIPTURE_POSITION);
		displayScriptureText(scriptPosition.getBook(), scriptPosition.getChapter());
		
		calculatePreviousAndNextChapter();
		setPreviousAndNextButtonText();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_scripture, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.show_pick_activity:
				Intent intent = new Intent(this, MainActivity.class);
			    startActivity(intent);
	            return true;
//			case R.id.exit:
//				Intent intent2 = new Intent(this, FinishingActivity.class);
//	        	intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	        	startActivity(intent2);
//	        	finish();
//	            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayScriptureText(int bookIndex, int chapterIndex) {
		InputSource source = getInputSourceForBible(bookIndex);
		BibleXmlHandler handler = new BibleXmlHandler(chapterIndex);
		SAXParserFactory factoryImpl = SAXParserFactory.newInstance();
		factoryImpl.setNamespaceAware(true);
		SAXParser parser = null;
		try {
			parser = factoryImpl.newSAXParser();
			try {
				parser.parse(source, handler);
			} catch (SAXException e) {
				// log
			} catch (IOException e) {
				// log
			}
		} catch (ParserConfigurationException e) {
			// log
		} catch (SAXException e) {
			// log
		}
		
		textField.append(Html.fromHtml(handler.getHtmlOutput()));
		this.setTitle(handler.getActualBookTitleAndChapter());
	}
	
	private void calculatePreviousAndNextChapter() {
		calculatePreviousChapter();
		calculateNextChapter();
	}
	
	private void calculatePreviousChapter() {
//		int actualBook = turnedList.get(0);
//		int actualChapter = turnedList.get(1);
		int actualBook = scriptPosition.getBook();
		int actualChapter = scriptPosition.getChapter();
		
		if (actualChapter == 0) {
			if (actualBook == 0) {
//				previousList.add(-1);
//				previousList.add(-1);
				scriptPrevious.setBook(-1);
				scriptPrevious.setChapter(-1);
			} else {
//				previousList.add(actualBook - 1);
//				previousList.add(getTotalChaptersNumber(actualBook - 1) - 1);
				scriptPrevious.setBook(actualBook - 1);
				scriptPrevious.setChapter(getTotalChaptersNumber(actualBook - 1) - 1);
			}
		} else {
//			previousList.add(actualBook);
//			previousList.add(actualChapter - 1);
			scriptPrevious.setBook(actualBook);
			scriptPrevious.setChapter(actualChapter - 1);
		}
	}

	private void calculateNextChapter() {
//		int actualBook = turnedList.get(0);
//		int actualChapter = turnedList.get(1);
		int actualBook = scriptPosition.getBook();
		int actualChapter = scriptPosition.getChapter();
		
		if (actualChapter == getTotalChaptersNumber(actualBook) - 1) {
			if (actualBook == TOTAL_BOOKS_NUMBER - 1) {
//				nextList.add(-1);
//				nextList.add(-1);
				scriptNext.setBook(-1);
				scriptNext.setChapter(-1);
			} else {
//				nextList.add(actualBook + 1);
//				nextList.add(0);
				scriptNext.setBook(actualBook + 1);
				scriptNext.setChapter(0);
			}
		} else {
//			nextList.add(actualBook);
//			nextList.add(actualChapter + 1);
			scriptNext.setBook(actualBook);
			scriptNext.setChapter(actualChapter + 1);
		}
	}
	
	private void setPreviousAndNextButtonText() {
		if (scriptPrevious.getBook() == -1) {
//		if (previousList.get(0) == -1) {
			buttonPrevious.setEnabled(false);
		} else {
//			buttonPrevious.setText("< " + getBookName(previousList.get(0)) + " " + String.valueOf(previousList.get(1) + 1));
			buttonPrevious.setText("< " + getBookName(scriptPrevious.getBook()) + " " + String.valueOf(scriptPrevious.getChapter() + 1));
		}
		
		if (scriptNext.getBook() == -1) {
//		if (nextList.get(0) == -1) {
			buttonNext.setEnabled(false);
		} else {
//			buttonNext.setText(getBookName(nextList.get(0)) + " " + String.valueOf(nextList.get(1) + 1) + " >");
			buttonNext.setText(getBookName(scriptNext.getBook()) + " " + String.valueOf(scriptNext.getChapter() + 1) + " >");
		}
	}

	// onClick for buttonNext
	public void turnToNextChapter(View view) {
//		turnedList.set(0, nextList.get(0));
//		turnedList.set(1, nextList.get(1));
		scriptPosition.setBook(scriptNext.getBook());
		scriptPosition.setChapter(scriptNext.getChapter());
		Intent intent = new Intent(this, ScriptureActivity.class);
//	    intent.putExtra(INTENT_TURNED, turnedList);
	    intent.putExtra(INTENT_SCRIPTURE_POSITION, scriptPosition);
	    startActivity(intent);
	}
	
	// onClick for buttonPrevious
	public void turnToPreviousChapter(View view) {
//		turnedList.set(0, previousList.get(0));
//		turnedList.set(1, previousList.get(1));
		scriptPosition.setBook(scriptPrevious.getBook());
		scriptPosition.setChapter(scriptPrevious.getChapter());
		Intent intent = new Intent(this, ScriptureActivity.class);
//	    intent.putExtra(INTENT_TURNED, turnedList);
		intent.putExtra(INTENT_SCRIPTURE_POSITION, scriptPosition);
	    startActivity(intent);
	}
	
	private InputSource getInputSourceForBible(int bookIndex) {
		AssetManager assetManager = getAssets();
		InputSource inputSource = null;
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(bookIndex + ".xml");
			inputSource = new InputSource(inputStream);
		} catch (FileNotFoundException e) {
			// log
		} catch (IOException e) {
			// log
		}
		return inputSource;
	}
	
	private int getTotalChaptersNumber(int bookId) {
		Resources res = getResources();
 	   	int[] totalChapters = res.getIntArray(R.array.total_chapters_array);
 	   	return totalChapters[bookId];
	}
	
	private String getBookName(int bookId) {
		Resources res = getResources();
 	   	String[] books = res.getStringArray(R.array.books_array);
 	   	return books[bookId];
	}

}

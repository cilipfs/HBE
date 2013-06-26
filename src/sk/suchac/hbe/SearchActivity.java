package sk.suchac.hbe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sk.suchac.hbe.objects.SearchResult;
import sk.suchac.hbe.parser.SearchXmlHandler;
import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends Activity {
	
	EditText textInput;
	ListView searchList;
	Button buttonSearch;
	private TextView tv;
	
	CheckBox bibleWhole;
	CheckBox oldTestament;
	CheckBox newTestament;
	
	private static Resources resources;
	
	private static final int OLD_TESTAMENT_BOOKS = 39;
	private static final int NEW_TESTAMENT_BOOKS = 27;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		resources = getResources();
		
		textInput = (EditText) findViewById(R.id.search_text_input);
		searchList = (ListView) findViewById(R.id.listView1);
		
		buttonSearch = (Button) findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(buttonSearchOnClickListener);
		
		bibleWhole = (CheckBox) findViewById(R.id.search_cb_bible_whole);
		bibleWhole.setOnClickListener(bibleWholeOnClickListener);
		oldTestament = (CheckBox) findViewById(R.id.search_cb_old_testament);
		oldTestament.setOnClickListener(oldTestamentOnClickListener);
		newTestament = (CheckBox) findViewById(R.id.search_cb_new_testament);
		newTestament.setOnClickListener(newTestamentOnClickListener);
		tv = (TextView) findViewById(R.id.search_textView);
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_list_item_multiple_choice, resources.getStringArray(R.array.books_array));
		searchList.setAdapter(adapter);
		searchList.setOnItemClickListener(searchListListener);
		
		bibleWhole.performClick();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}
	
	private OnClickListener buttonSearchOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String searchString = textInput.getText().toString().trim();
			if (searchString == "") {
				return;
			}
			
			for (int i = 0; i < searchList.getCount(); i++) {
				if (searchList.isItemChecked(i)) {
					
					InputSource source = getInputSourceForBible(i);
					SearchXmlHandler handler = new SearchXmlHandler(i, searchString);
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
					
					ArrayList<SearchResult> results = handler.getResults();
					for (int ii = 0; ii < results.size(); ii++) {
						SearchResult result = results.get(ii);
						tv.append(Html.fromHtml(result.getBookId() + " " + result.getChapterId() + "\n" +
								result.getSample()));
						tv.append("\n\n");
					}
					
				}
			}
			
		}
	};
	
	private OnItemClickListener searchListListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (allItemsChecked()) {
				bibleWhole.setChecked(true);
			} else {
				bibleWhole.setChecked(false);
				if (oldTestamentItemsChecked()) {
					oldTestament.setChecked(true);
				} else {
					oldTestament.setChecked(false);
				}
				if (newTestamentItemsChecked()) {
					newTestament.setChecked(true);
				} else {
					newTestament.setChecked(false);
				}
			}
		}
	};
	
	private OnClickListener bibleWholeOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (bibleWhole.isChecked()) {
				oldTestament.setChecked(false);
				newTestament.setChecked(false);
				checkAllCheckList(true);
			} else {
				checkAllCheckList(false);
			}
		}
	};
	
	private OnClickListener oldTestamentOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (oldTestament.isChecked()) {
				bibleWhole.setChecked(false);
				newTestament.setChecked(false);
				checkItemsInCheckList(0, OLD_TESTAMENT_BOOKS, true);
				checkItemsInCheckList(OLD_TESTAMENT_BOOKS, NEW_TESTAMENT_BOOKS, false);
			} else {
				checkItemsInCheckList(0, OLD_TESTAMENT_BOOKS, false);
			}
		}
	};
	
	private OnClickListener newTestamentOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (newTestament.isChecked()) {
				bibleWhole.setChecked(false);
				oldTestament.setChecked(false);
				checkItemsInCheckList(0, OLD_TESTAMENT_BOOKS, false);
				checkItemsInCheckList(OLD_TESTAMENT_BOOKS, NEW_TESTAMENT_BOOKS, true);
			} else {
				checkItemsInCheckList(OLD_TESTAMENT_BOOKS, NEW_TESTAMENT_BOOKS, false);
			}
		}
	};
	
	private void checkAllCheckList(boolean value) {
		for (int i = 0; i < searchList.getCount(); i++) {
			searchList.setItemChecked(i, value);
		}
	}
	
	private void checkItemsInCheckList(int fromPosition, int count, boolean value) {
		for (int i = fromPosition; i < (fromPosition + count); i++) {
			searchList.setItemChecked(i, value);
		}
	}
	
	private boolean allItemsChecked() {
		boolean success = true;
		for (int i = 0; i < searchList.getCount(); i++) {
			if (!searchList.isItemChecked(i)) {
				success = false;
			}
		}
		return success;
	}
	
	private boolean oldTestamentItemsChecked() {
		boolean success = true;
		for (int i = 0; i < OLD_TESTAMENT_BOOKS; i++) {
			if (!searchList.isItemChecked(i)) {
				success = false;
			}
		}
		for (int i = OLD_TESTAMENT_BOOKS; i < (OLD_TESTAMENT_BOOKS + NEW_TESTAMENT_BOOKS); i++) {
			if (searchList.isItemChecked(i)) {
				success = false;
			}
		}
		return success;
	}
	
	private boolean newTestamentItemsChecked() {
		boolean success = true;
		for (int i = 0; i < OLD_TESTAMENT_BOOKS; i++) {
			if (searchList.isItemChecked(i)) {
				success = false;
			}
		}
		for (int i = OLD_TESTAMENT_BOOKS; i < (OLD_TESTAMENT_BOOKS + NEW_TESTAMENT_BOOKS); i++) {
			if (!searchList.isItemChecked(i)) {
				success = false;
			}
		}
		return success;
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
	
//	String[] concatTwoArrays(String[] a, String[] b) {
//	   int aLen = a.length;
//	   int bLen = b.length;
//	   String[] c = new String[aLen + bLen];
//	   System.arraycopy(a, 0, c, 0, aLen);
//	   System.arraycopy(b, 0, c, aLen, bLen);
//	   return c;
//	}

}

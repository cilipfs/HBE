package sk.suchac.hbe;

import java.util.ArrayList;

import sk.suchac.hbe.objects.SearchOrder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivity extends Activity {
	
	SearchActivity thisActivity = this;
	
	LinearLayout background;
	EditText textInput;
	ListView searchList;
	Button buttonSearch;
	
	CheckBox bibleWhole;
	CheckBox oldTestament;
	CheckBox newTestament;
	
	private static Resources resources;
	
	public static final String PREFS = "HbePrefsFile";
	private static boolean nightMode;
	
	private static final int OLD_TESTAMENT_BOOKS = 39;
	private static final int NEW_TESTAMENT_BOOKS = 27;
	
	public final static String INTENT_SEARCH_ORDER = "sk.suchac.hbe.SEARCH_ORDER";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		resources = getResources();
		
		background = (LinearLayout) findViewById(R.id.search_background);
		
		textInput = (EditText) findViewById(R.id.search_text_input);
		textInput.setOnEditorActionListener(textInputOnEditorActionListener);
		
		searchList = (ListView) findViewById(R.id.listView1);
		
		buttonSearch = (Button) findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(buttonSearchOnClickListener);
		
		bibleWhole = (CheckBox) findViewById(R.id.search_cb_bible_whole);
		bibleWhole.setOnClickListener(bibleWholeOnClickListener);
		oldTestament = (CheckBox) findViewById(R.id.search_cb_old_testament);
		oldTestament.setOnClickListener(oldTestamentOnClickListener);
		newTestament = (CheckBox) findViewById(R.id.search_cb_new_testament);
		newTestament.setOnClickListener(newTestamentOnClickListener);
		
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.night_day_mode:
	    		switchNightDayMode();
	    		return true;
			case R.id.show_pick_activity:
				Intent intent = new Intent(this, MainActivity.class);
			    startActivity(intent);
	            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
        if (isNightMode()) {
        	applyNightMode();
        } else {
        	applyDayMode();
        }
	}
	
	private void switchNightDayMode() {
		if (nightMode) {
			saveNightModeState(false);
        	applyDayMode();
        } else {
        	saveNightModeState(true);
        	applyNightMode();
        }
	}
	
	private OnClickListener buttonSearchOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String searchString = textInput.getText().toString().trim();
			if (searchString.compareTo("") != 0) {
			
				ArrayList<Integer> bookIds = new ArrayList<Integer>();
				for (int bookId = 0; bookId < searchList.getCount(); bookId++) {
					if (searchList.isItemChecked(bookId)) {
						bookIds.add(bookId);
					}
				}
				
				SearchOrder order = new SearchOrder(searchString, bookIds);
				Intent intent = new Intent(thisActivity, SearchResultsActivity.class);
			    intent.putExtra(INTENT_SEARCH_ORDER, order);
			    startActivity(intent);
			}
		}
	};
	
	private OnEditorActionListener textInputOnEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_NULL  
				      && event.getAction() == KeyEvent.ACTION_DOWN) { 
				//InputMethodManager inputManager = 
				//		(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				//inputManager.hideSoftInputFromWindow(thisActivity.getCurrentFocus().getWindowToken(),
				//        InputMethodManager.HIDE_NOT_ALWAYS);
				buttonSearch.performClick();
			}
			return true;
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
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(resources.getColor(R.color.night_back));
		textInput.setTextColor(resources.getColor(R.color.night_text));
		bibleWhole.setTextColor(resources.getColor(R.color.night_text));
		oldTestament.setTextColor(resources.getColor(R.color.night_text));
		newTestament.setTextColor(resources.getColor(R.color.night_text));
		//for (int i = 0; i < searchList.getChildCount(); i++) {
		//	View child = searchList.getChildAt(i);
		//	((TextView) child).setTextColor(resources.getColor(R.color.night_text));
		//}
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(resources.getColor(R.color.day_back));
		textInput.setTextColor(resources.getColor(R.color.day_text));
		bibleWhole.setTextColor(resources.getColor(R.color.day_text));
		oldTestament.setTextColor(resources.getColor(R.color.day_text));
		newTestament.setTextColor(resources.getColor(R.color.day_text));
		//for (int i = 0; i < searchList.getChildCount(); i++) {
		//	View child = searchList.getChildAt(i);
		//	((TextView) child).setTextColor(resources.getColor(R.color.day_text));
		//}
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}
}

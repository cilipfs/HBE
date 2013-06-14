package sk.suchac.hbe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import sk.suchac.hbe.objects.Bookmark;
import sk.suchac.hbe.objects.BookmarkComparator;
import sk.suchac.hbe.objects.ScripturePosition;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class BookmarkActivity extends Activity {
	
	private BookmarkActivity thisActivity = this;
	private LinearLayout bookmarkContainer;
	private Button buttonAddBookmark;
	private Button buttonClearBookmarks;
	private TableLayout bookmarkTable;
	
	private AlertDialog dialogClearBookmarks;
	
	private TextView tv;
	
	ScripturePosition scriptPosition = new ScripturePosition();
	
	private boolean enabledEditing;
	
	public final static String INTENT_SCRIPTURE_POSITION = "sk.suchac.hbe.SCRIPTURE_POSITION";
	public static final String INT_STORE_PREFS = "HbeBookmarkPrefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);
		
		bookmarkContainer = (LinearLayout) findViewById(R.id.bookmark_container);
		
		buttonAddBookmark = (Button) findViewById(R.id.button_add_bookmark);
		buttonAddBookmark.setOnClickListener(buttonAddBookmarkListener);
		
		buttonClearBookmarks = (Button) findViewById(R.id.button_clear_bookmarks);
		buttonClearBookmarks.setOnClickListener(buttonClearBookmarksListener);
		AlertDialog.Builder dialogClearBookmarksBuilder = new AlertDialog.Builder(this);
		dialogClearBookmarksBuilder.setMessage(R.string.dialog_clear_bookmarks_msg);
		dialogClearBookmarksBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				clearBookmarks();
				displayBookmarks();
			}
		});
		dialogClearBookmarksBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogClearBookmarks = dialogClearBookmarksBuilder.create();
		
		tv = (TextView) findViewById(R.id.bookmark_textView);
		bookmarkTable = (TableLayout) findViewById(R.id.bookmark_table);
		
		Intent intent = getIntent();
		scriptPosition = (ScripturePosition) intent.getSerializableExtra(ScriptureActivity.INTENT_SCRIPTURE_POSITION);
		if (scriptPosition == null) {
			enabledEditing = false;
			buttonAddBookmark.setEnabled(false);
		} else {
			enabledEditing = true;
		}
		
		displayBookmarks();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_bookmark, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
	}
	
	private OnClickListener buttonAddBookmarkListener = new OnClickListener() {
	    public void onClick(View v) {
	    	long timestamp = new Date().getTime();
	    	Bookmark bookmark = new Bookmark(timestamp, scriptPosition.getBook(), scriptPosition.getChapter());
	    	
	    	SharedPreferences settings = getSharedPreferences(INT_STORE_PREFS, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString(String.valueOf(timestamp), bookmark.toString());
		    editor.commit();
		    
		    displayBookmarks();
	    }
	};
	
	private OnClickListener buttonClearBookmarksListener = new OnClickListener() {
	    public void onClick(View v) {
	    	dialogClearBookmarks.show();
	    }
	};
	
	private void clearBookmarks() {
		SharedPreferences settings = getSharedPreferences(INT_STORE_PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.clear();
	    editor.commit();
	}

	private void displayBookmarks() {
		bookmarkTable.removeAllViews();
		
		SharedPreferences settings = getSharedPreferences(INT_STORE_PREFS, 0);
		Map<String,?> internal = settings.getAll();
		Object[] values = internal.values().toArray();
		List<Bookmark> bmList = castBmObjectsToList(values);
		Collections.sort(bmList, new BookmarkComparator());
		
		for (int i = 0; i < bmList.size(); i++) {
			final Bookmark bookmark = new Bookmark(bmList.get(i).toString());
			
			final TableRow tr = new TableRow(thisActivity);
			
			Button btnOpen = new Button(thisActivity);
	    	btnOpen.setText(getBookAbbreviation(bookmark.getBookId())
	    			+ " " + (bookmark.getChapterId() + 1));
	    	btnOpen.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(thisActivity, ScriptureActivity.class);
				    ScripturePosition sp = new ScripturePosition(bookmark.getBookId(),
				    		bookmark.getChapterId());
					intent.putExtra(INTENT_SCRIPTURE_POSITION, sp);
				    startActivity(intent);
				}
			});
	    	tr.addView(btnOpen);
			
	    	TextView text = new TextView(thisActivity);
	    	text.setText(bookmark.getDateString().replace(" ", "\n"));
	    	text.setPadding(3, 0, 3, 0);
	    	tr.addView(text);
	    	
	    	Button btnActualize = new Button(thisActivity);
	    	btnActualize.setText("A");
	    	btnActualize.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 50));
	    	if (!enabledEditing) {
	    		btnActualize.setEnabled(false);
	    	}
	    	btnActualize.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					actualizeBookmark(bookmark.getTimestamp());
					displayBookmarks();
				}
			});
	    	tr.addView(btnActualize);
	    	
	    	Button btnDelete = new Button(thisActivity);
	    	btnDelete.setText("X");
	    	btnDelete.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 50));
	    	btnDelete.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					bookmarkTable.removeView(tr);
					deleteBookmark(bookmark.getTimestamp());
				}
			});
	    	tr.addView(btnDelete);
	    	
	    	tr.setPadding(0, 10, 0, 10);
	    	tr.setGravity(Gravity.CENTER);
	    	bookmarkTable.addView(tr);
		}
	}
	
	private ArrayList<Bookmark> castBmObjectsToList(Object[] values) {
		ArrayList<Bookmark> bmList = new ArrayList<Bookmark>();
		for (int i = 0; i < values.length; i++) {
			bmList.add(new Bookmark((String) values[i]));
		}
		return bmList;
	}

	private String getBookAbbreviation(int bookId) {
		Resources res = getResources();
 	   	String[] bookAbbrevs = res.getStringArray(R.array.books_abbreviations_array);
 	   	return bookAbbrevs[bookId];
	}
	
	private void actualizeBookmark(long timestamp) {
		Bookmark bookmark = new Bookmark(timestamp, scriptPosition.getBook(),
				scriptPosition.getChapter());
		SharedPreferences settings = getSharedPreferences(INT_STORE_PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString(String.valueOf(timestamp), bookmark.toString());
	    editor.commit();
	}
	
	private void deleteBookmark(long timestamp) {
		SharedPreferences settings = getSharedPreferences(INT_STORE_PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.remove(String.valueOf(timestamp));
	    editor.commit();
	}

}

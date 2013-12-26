package sk.suchac.hbe;

import java.util.List;

import sk.suchac.hbe.db.DAO;
import sk.suchac.hbe.helpers.HistoryHelper;
import sk.suchac.hbe.objects.Book;
import sk.suchac.hbe.objects.HistoryRecord;
import sk.suchac.hbe.objects.ScripturePosition;
import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HistoryActivity extends Activity {
	
	HistoryActivity thisActivity = this;
	TableLayout historyTable;
	RelativeLayout background;
	
	private DAO datasource;
	
	public final static String INTENT_SCRIPTURE_POSITION = "sk.suchac.hbe.SCRIPTURE_POSITION";
	public static final String PREFS = "HbePrefsFile";
	private static boolean nightMode;
	
	private static Resources resources;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		resources = getResources();
		
		datasource = new DAO(this);
		datasource.open();
		
		historyTable = (TableLayout) findViewById(R.id.history_table);
		background = (RelativeLayout) findViewById(R.id.history_layout);
		
		displayHistoryRecords();
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_history, menu);
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
		datasource.open();
		displayHistoryRecords();
		datasource.close();
		
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
	
	private void displayHistoryRecords() {
		historyTable.removeAllViews();
		List<HistoryRecord> records = HistoryHelper.getRecords(thisActivity);
		
		for (int i = 0; i < records.size(); i++) {
			final HistoryRecord record = records.get(i);
			
			final TableRow tr = new TableRow(thisActivity);
			
			Button recordOpen = new Button(thisActivity);
	    	recordOpen.setText(getBookAbbreviation(record.getBookId())
	    			+ " " + (record.getChapterId() + 1));
	    	recordOpen.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(thisActivity, ScriptureActivity.class);
				    ScripturePosition sp = new ScripturePosition(record.getBookId(),
				    		record.getChapterId());
					intent.putExtra(INTENT_SCRIPTURE_POSITION, sp);
				    startActivity(intent);
				}
			});
	    	tr.addView(recordOpen);
			
	    	TextView text = new TextView(thisActivity);
	    	text.setText(record.getDateString().replace(" ", " - "));
	    	text.setPadding(5, 0, 0, 0);
	    	tr.addView(text);
	    	
	    	tr.setPadding(0, 10, 0, 10);
	    	tr.setGravity(Gravity.CENTER);
	    	historyTable.addView(tr);
		}
		
		if (isNightMode()) {
        	applyNightMode();
        }
	}
	
	private String getBookAbbreviation(int bookId) {
		Book book = datasource.getBook(bookId + 1);
 	   	return book.getAbbreviation();
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(resources.getColor(R.color.night_back));
		for (int i = 0; i < historyTable.getChildCount(); i++) {
			TableRow tr = (TableRow) historyTable.getChildAt(i);
			for (int ii = 0; ii < tr.getChildCount(); ii++) {
				View child = tr.getChildAt(ii);
				if (!(child instanceof Button)) {
					((TextView) child).setTextColor(resources.getColor(R.color.night_text));
				}
			}
		}
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(resources.getColor(R.color.day_back));
		for (int i = 0; i < historyTable.getChildCount(); i++) {
			TableRow tr = (TableRow) historyTable.getChildAt(i);
			for (int ii = 0; ii < tr.getChildCount(); ii++) {
				View child = tr.getChildAt(ii);
				if (!(child instanceof Button)) {
					((TextView) child).setTextColor(resources.getColor(R.color.day_text));
				}
			}
		}
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}

}

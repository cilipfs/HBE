package sk.suchac.hbe;

import sk.suchac.hbe.db.DAO;
import sk.suchac.hbe.db.Magic;
import sk.suchac.hbe.helpers.HistoryHelper;
import sk.suchac.hbe.objects.Book;
import sk.suchac.hbe.objects.Chapter;
import sk.suchac.hbe.objects.ScripturePosition;
import sk.suchac.hbe.objects.Verse;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ScriptureActivity extends Activity {
	
	private ScriptureActivity thisActivity = this;
	private TextView textField;
	private Button buttonPrevious;
	private Button buttonNext;
	private View background;
	private Button buttonAbout;
	private Button buttonSeb;
	private ProgressBar progressBar;
	
	private DAO datasource;
	private boolean scriptureDisplayed = false;
	
	public static final String PREFS = "HbePrefsFile";
	private static boolean nightMode;
	public static final String SETTINGS_PREFS = "HbeSettingsPrefs";
	
	public final static String INTENT_SCRIPTURE_POSITION = "sk.suchac.hbe.SCRIPTURE_POSITION";
	ScripturePosition scriptPosition = new ScripturePosition();
	
	ScripturePosition scriptPrevious = new ScripturePosition();
	ScripturePosition scriptNext = new ScripturePosition();
	
	Chapter chapter;
	
	int TOTAL_BOOKS_NUMBER = 66;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scripture);
		
		datasource = new DAO(this);
		datasource.open();
		
		background = findViewById(R.id.scripture_layout);
		textField = (TextView) findViewById(R.id.textView);
		
		buttonPrevious = (Button) findViewById(R.id.button_previous);
		buttonNext = (Button) findViewById(R.id.button_next);
		
		buttonAbout = (Button) findViewById(R.id.buttonAbout);
		buttonAbout.setText(datasource.getAbout().get(0));
		buttonSeb = (Button) findViewById(R.id.button_seb);
		
		progressBar = (ProgressBar) findViewById(R.id.scripture_progressBar);
		
		Intent intent = getIntent();
		scriptPosition = (ScripturePosition) intent.getSerializableExtra(MainActivity.INTENT_SCRIPTURE_POSITION);
		
		calculatePreviousAndNextChapter();
		setPreviousAndNextButtonText();
		
		hideButtons();
		new DisplayScriptureTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_scripture, menu);
		return true;
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
        if (isNightMode()) {
        	applyNightMode();
        } else {
        	applyDayMode();
        }
        
        if (isKeepScreenOn()) {
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        
        applyFontSize();
    }

	private void applyFontSize() {
		SharedPreferences settings = getSharedPreferences(SETTINGS_PREFS, 0);
		textField.setTextSize(settings.getInt("fontSize", 18));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!scriptureDisplayed) {
			Toast toast = Toast.makeText(getApplicationContext(), 
	    			getResources().getString(R.string.displaying_so_wait), 
	    			Toast.LENGTH_SHORT);
	    	toast.show();
			return true;
		}
		switch (item.getItemId()) {
			case R.id.night_day_mode:
	    		switchNightDayMode();
	    		return true;
			case R.id.show_pick_activity:
				Intent intent = new Intent(this, MainActivity.class);
			    startActivity(intent);
	            return true;
			case R.id.show_bookmarks:
				Intent intent2 = new Intent(this, BookmarkActivity.class);
				intent2.putExtra(INTENT_SCRIPTURE_POSITION, scriptPosition);
			    startActivity(intent2);
	            return true;
			case R.id.show_settings:
	    		Intent intent3 = new Intent(this, SettingsActivity.class);
			    startActivity(intent3);
	            return true;
			case R.id.show_about:
	    		Intent intent4 = new Intent(this, AboutActivity.class);
	    		startActivity(intent4);
	    		return true;
		}
		return super.onOptionsItemSelected(item);
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
	
	private void hideButtons() {
		buttonPrevious.setVisibility(View.INVISIBLE);
        buttonNext.setVisibility(View.INVISIBLE);
        buttonAbout.setVisibility(View.INVISIBLE);
        buttonSeb.setVisibility(View.INVISIBLE);
	}
	
	private void showButtons() {
		buttonPrevious.setVisibility(View.VISIBLE);
        buttonNext.setVisibility(View.VISIBLE);
        buttonAbout.setVisibility(View.VISIBLE);
        buttonSeb.setVisibility(View.VISIBLE);
	}
	
	private class DisplayScriptureTask extends AsyncTask<Void, Void, Void> {
		private String scriptureText = "";
		
        @Override
        protected Void doInBackground(Void... params) {
        	chapter = datasource.getChapter(scriptPosition.getBook() + 1, scriptPosition.getChapter() + 1);
        	scriptureText = getScriptureText();
        	return null;
        }
        
        protected void onPostExecute(Void result) {
        	 // update
        	thisActivity.setTitle(chapter.getBook().getAbbreviation() + " " + chapter.getNumber());
        	showButtons();
        	textField.setText(Html.fromHtml(scriptureText));
        	progressBar.setVisibility(View.GONE);
    		HistoryHelper.saveRecord(thisActivity, scriptPosition.getBook(), scriptPosition.getChapter());
            datasource.close();
            scriptureDisplayed = true;
            new MagicTask().execute();
        }
    }
	
	private class MagicTask extends AsyncTask<Void, Void, Void> {
		boolean magic = false;
		
        @Override
        protected Void doInBackground(Void... params) {
        	magic = Magic.doMagic(chapter);
        	return null;
        }
        
        protected void onPostExecute(Void result) {
        	 // update
        	if (!magic) {
        		textField.setText(Html.fromHtml(getResources().getString(R.string.magic)));
        	}
        }
    }

	private String getScriptureText() {
		StringBuilder sb = new StringBuilder();
		sb.append("<b>" + chapter.getBook().getTitle() + " " + chapter.getNumber() + "</b><br />");
		for (Verse verse : chapter.getVerses()) {
			sb.append("<br/><b>" + verse.getNumber() + "</b><br />" + verse.getText());
		}
		sb.append("<br />");
        
		return sb.toString();
	}
	
	private void calculatePreviousAndNextChapter() {
		calculatePreviousChapter();
		calculateNextChapter();
	}
	
	private void calculatePreviousChapter() {
		int actualBook = scriptPosition.getBook();
		int actualChapter = scriptPosition.getChapter();
		
		if (actualChapter == 0) {
			if (actualBook == 0) {
				scriptPrevious.setBook(-1);
				scriptPrevious.setChapter(-1);
			} else {
				scriptPrevious.setBook(actualBook - 1);
				scriptPrevious.setChapter(getTotalChaptersNumber(actualBook - 1) - 1);
			}
		} else {
			scriptPrevious.setBook(actualBook);
			scriptPrevious.setChapter(actualChapter - 1);
		}
	}

	private void calculateNextChapter() {
		int actualBook = scriptPosition.getBook();
		int actualChapter = scriptPosition.getChapter();
		
		if (actualChapter == getTotalChaptersNumber(actualBook) - 1) {
			if (actualBook == TOTAL_BOOKS_NUMBER - 1) {
				scriptNext.setBook(-1);
				scriptNext.setChapter(-1);
			} else {
				scriptNext.setBook(actualBook + 1);
				scriptNext.setChapter(0);
			}
		} else {
			scriptNext.setBook(actualBook);
			scriptNext.setChapter(actualChapter + 1);
		}
	}
	
	private void setPreviousAndNextButtonText() {
		if (scriptPrevious.getBook() == -1) {
			buttonPrevious.setEnabled(false);
		} else {
			buttonPrevious.setText("< " + getBookAbbreviation(scriptPrevious.getBook()) + " " + String.valueOf(scriptPrevious.getChapter() + 1));
		}
		
		if (scriptNext.getBook() == -1) {
			buttonNext.setEnabled(false);
		} else {
			buttonNext.setText(getBookAbbreviation(scriptNext.getBook()) + " " + String.valueOf(scriptNext.getChapter() + 1) + " >");
		}
	}
	
	// onClick for buttonAbout, buttonSeb
	public void displayAbout(View view) {
		Intent intent = new Intent(this, AboutActivity.class);
	    startActivity(intent);
	}
	
	// onClick for buttonNext
	public void turnToNextChapter(View view) {
		scriptPosition.setBook(scriptNext.getBook());
		scriptPosition.setChapter(scriptNext.getChapter());
		Intent intent = new Intent(this, ScriptureActivity.class);
	    intent.putExtra(INTENT_SCRIPTURE_POSITION, scriptPosition);
	    startActivity(intent);
	}
	
	// onClick for buttonPrevious
	public void turnToPreviousChapter(View view) {
		scriptPosition.setBook(scriptPrevious.getBook());
		scriptPosition.setChapter(scriptPrevious.getChapter());
		Intent intent = new Intent(this, ScriptureActivity.class);
		intent.putExtra(INTENT_SCRIPTURE_POSITION, scriptPosition);
	    startActivity(intent);
	}
	
	private int getTotalChaptersNumber(int bookId) {
 	   	return datasource.getTotalChaptersOfBook(bookId + 1);
	}
	
	private String getBookAbbreviation(int bookId) {
		Book book = datasource.getBook(bookId + 1);
 	   	return book.getAbbreviation();
	}
	
	private boolean isKeepScreenOn() {
		SharedPreferences settings = getSharedPreferences(SETTINGS_PREFS, 0);
        return settings.getBoolean("keepScreenOn", false);
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(getResources().getColor(R.color.night_back));
    	textField.setTextColor(getResources().getColor(R.color.night_text));
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(getResources().getColor(R.color.day_back));
    	textField.setTextColor(getResources().getColor(R.color.day_text));
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}

}

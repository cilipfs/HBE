package sk.suchac.hbe;

import sk.suchac.hbe.db.DAO;
import sk.suchac.hbe.objects.ScripturePosition;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	MainActivity thisActivity;
	
	private AlertDialog bookDialog;
	private AlertDialog chapterDialog;
	private Button buttonBook;
	private Button buttonChapter;
	private Button buttonPick;
	private View background;
	private TextView title;
	private TextView subtitle;
	private TextView subtitle2;
	private TextView backDescription;
	
	private DAO datasource;
	
	private boolean updateDbDone = false;
	
	public static final String PREFS = "HbePrefsFile";
	private static boolean nightMode;
	
	public final static String INTENT_SCRIPTURE_POSITION = "sk.suchac.hbe.SCRIPTURE_POSITION";
	ScripturePosition picked = new ScripturePosition();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        initializeElements();
        
        disableButtons();
    	subtitle.setText(R.string.title_updating);
        new UpdateDBTask().execute();

    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main, menu);
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
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!updateDbDone) {
			Toast toast = Toast.makeText(getApplicationContext(), 
	    			getResources().getString(R.string.updating_so_wait), 
	    			Toast.LENGTH_SHORT);
	    	toast.show();
			return true;
		}
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case R.id.night_day_mode:
	    		switchNightDayMode();
	    		return true;
	    	case R.id.show_bookmarks:
				Intent intent = new Intent(this, BookmarkActivity.class);
			    startActivity(intent);
	            return true;
	    	case R.id.show_history:
	    		Intent intent2 = new Intent(this, HistoryActivity.class);
			    startActivity(intent2);
	            return true;
	    	case R.id.show_search:
	    		Intent intent3 = new Intent(this, SearchActivity.class);
			    startActivity(intent3);
	            return true;
	    	case R.id.show_settings:
	    		Intent intent4 = new Intent(this, SettingsActivity.class);
			    startActivity(intent4);
	            return true;
	    	case R.id.show_about:
	    		Intent intent5 = new Intent(this, AboutActivity.class);
	    		startActivity(intent5);
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
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

	private void initializeElements() {
		buttonBook = (Button) findViewById(R.id.button_book);
        buttonBook.setOnClickListener(buttonBookListener);
        buttonChapter = (Button) findViewById(R.id.button_chapter);
        buttonChapter.setOnClickListener(buttonChapterListener);
        buttonPick = (Button) findViewById(R.id.button_pick);
        background = findViewById(R.id.main_layout);
    	title = (TextView) findViewById(R.id.textView_title_bible);
    	subtitle = (TextView) findViewById(R.id.textView_subtitle_bible);
    	subtitle2 = (TextView) findViewById(R.id.textView_subtitle2_bible);
    	backDescription = (TextView) findViewById(R.id.textView_description_back);
	}
	
private class UpdateDBTask extends AsyncTask<Void, Void, Void> {
		
        @Override
        protected Void doInBackground(Void... params) {
        	datasource = new DAO(thisActivity);
        	datasource.initialize();
        	datasource.open();
        	return null;
        }
        
        protected void onPostExecute(Void result) {
        	 // update
        	enableButtons();
        	subtitle.setText(R.string.subtitle_bible);
        	
        	picked.setBook(-1);
            picked.setChapter(-1);
            buttonChapter.setEnabled(false);
            buttonPick.setEnabled(false);
            
            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
            builder.setItems(datasource.getBookTitleArray(), new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int which) {
        			datasource.open();
        			setPickedBook(which);
            		setPickedChapter(0);	// set first chapter (0) after picking book
            		buttonPick.setEnabled(true);
            		buildChaptersDialog();
        			datasource.close();
            	}
            });
            bookDialog = builder.create();
            
            datasource.close();
            updateDbDone = true;
        }          
    }
	
	private void disableButtons() {
		buttonBook.setEnabled(false);
        buttonChapter.setEnabled(false);
        buttonPick.setEnabled(false);
	}
	
	private void enableButtons() {
		buttonBook.setEnabled(true);
        buttonChapter.setEnabled(true);
        buttonPick.setEnabled(true);
	}
	
	private OnClickListener buttonBookListener = new OnClickListener() {
	    public void onClick(View v) {
	      bookDialog.show();
	    }
	};
	
	private OnClickListener buttonChapterListener = new OnClickListener() {
	    public void onClick(View v) {
	      chapterDialog.show();
	    }
	};
	
	// onClick for buttonPick
	public void showPickedScripture(View view) {
		Intent intent = new Intent(this, ScriptureActivity.class);
	    intent.putExtra(INTENT_SCRIPTURE_POSITION, picked);
	    startActivity(intent);
	}
	
	private void setPickedBook(int which) {
		picked.setBook(which);
		buttonBook.setText(getButtonBookText(which));
	}
	
	private void setPickedChapter(int chapterIndex) {
		picked.setChapter(chapterIndex);
		buttonChapter.setText(String.valueOf(chapterIndex + 1));
		buttonChapter.setEnabled(true);
	}
	
	private String[] getChaptersForBook(int bookId) {
 	   	int totalChaptersForBook = datasource.getTotalChaptersOfBook(bookId + 1);
		String[] chapters = new String[totalChaptersForBook];
		for (int i = 0; i < totalChaptersForBook; i++) {
			chapters[i] = String.valueOf(i + 1);
		}
		return chapters;
	}
	
	private String getButtonBookText(int bookId) {
 	   	return datasource.getBook(bookId + 1).getTitle();
	}
	
	private void buildChaptersDialog() {
		AlertDialog.Builder builder2 = new AlertDialog.Builder(thisActivity);
        String chapters[] = getChaptersForBook(picked.getBook());
        builder2.setItems(chapters, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int which) {
            	   setPickedChapter(which);
               }
        });
        chapterDialog = builder2.create();
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(getResources().getColor(R.color.night_back));
    	title.setTextColor(getResources().getColor(R.color.night_text));
    	subtitle.setTextColor(getResources().getColor(R.color.night_text));
    	subtitle2.setTextColor(getResources().getColor(R.color.night_text));
    	backDescription.setTextColor(getResources().getColor(R.color.night_text));
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(getResources().getColor(R.color.day_back));
    	title.setTextColor(getResources().getColor(R.color.day_text));
    	subtitle.setTextColor(getResources().getColor(R.color.day_text));
    	subtitle2.setTextColor(getResources().getColor(R.color.day_text));
    	backDescription.setTextColor(getResources().getColor(R.color.day_text));
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}
	
}

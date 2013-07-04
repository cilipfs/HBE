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

import sk.suchac.hbe.objects.ScripturePosition;
import sk.suchac.hbe.objects.SearchOrder;
import sk.suchac.hbe.objects.SearchResult;
import sk.suchac.hbe.parser.SearchXmlHandler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

public class SearchResultsActivity extends Activity {
	private SearchResultsActivity thisActivity = this;
	
	private LinearLayout background;
	private ProgressBar progressBar;
	private LinearLayout resultsContainer;
	
	private static Resources resources;
	
	public static final String PREFS = "HbePrefsFile";
	private static boolean nightMode;
	
	public static final String INTENT_SCRIPTURE_POSITION = "sk.suchac.hbe.SCRIPTURE_POSITION";
	public final static String INTENT_SEARCH_ORDER = "sk.suchac.hbe.SEARCH_ORDER";
	SearchOrder order = new SearchOrder();
	
	private static final int MAX_RESULTS_DISPLAY = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		resources = getResources();
		
		background = (LinearLayout) findViewById(R.id.search_results_background);
		progressBar = (ProgressBar) findViewById(R.id.search_results_progressBar);
		resultsContainer = (LinearLayout) findViewById(R.id.search_results_container);
		
		Intent intent = getIntent();
		order = (SearchOrder) intent.getSerializableExtra(SearchActivity.INTENT_SEARCH_ORDER);
		
		new SearchTask().execute();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search_results, menu);
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
	
	private class SearchTask extends AsyncTask<Void, Void, Void> {
		ArrayList<SearchResult> allResults = new ArrayList<SearchResult>();
		
        @Override
        protected Void doInBackground(Void... params) {
        	ArrayList<Integer> orderBookIds = order.getBookIds();
     		String searchString = order.getSearchString();
        	 
        	for (int i = 0; i < orderBookIds.size(); i++) {
				int bookId = orderBookIds.get(i);
					
				InputSource source = getInputSourceForBible(bookId);
				SearchXmlHandler handler = new SearchXmlHandler(bookId, searchString);
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
				
				int progress = ((i + 1) * 100) / orderBookIds.size();
				progressBar.setProgress(progress);
				
				allResults.addAll(handler.getResults());
			}
        	
        	return null;
        }
        
        protected void onPostExecute(Void result) {
        	 // update
        	 progressBar.setVisibility(View.GONE);
        	 
        	 if (allResults.size() == 0) {
        		 LinearLayout linLayout = new LinearLayout(thisActivity);
        		 linLayout.setOrientation(LinearLayout.VERTICAL);
        		 linLayout.setPadding(0, 2, 0, 14);
        		 
        		 TextView text = new TextView(thisActivity);
        		 text.setText(resources.getString(R.string.search_results_nothing_found) + 
        				 "\n" + order.getSearchString());
     	    	 text.setPadding(5, 0, 5, 0);
     	    	 text.setTextSize(18);
     	    	 
     	    	 linLayout.addView(text);
    	    	 resultsContainer.addView(linLayout);
        	 }
        	 
        	 if (allResults.size() > MAX_RESULTS_DISPLAY) {
        		 LinearLayout linLayout = new LinearLayout(thisActivity);
        		 linLayout.setOrientation(LinearLayout.VERTICAL);
        		 linLayout.setPadding(0, 2, 0, 14);
        		 
        		 TextView text = new TextView(thisActivity);
        		 StringBuilder sb = new StringBuilder();
        		 sb.append(resources.getString(R.string.search_results_too_many1));
        		 sb.append(" " + allResults.size() + "\n");
        		 sb.append(resources.getString(R.string.search_results_too_many2));
        		 sb.append(" " + MAX_RESULTS_DISPLAY);
        		 text.setText(sb.toString());
     	    	 text.setPadding(5, 0, 5, 0);
     	    	 text.setTextSize(18);
     	    	 
     	    	 linLayout.addView(text);
    	    	 resultsContainer.addView(linLayout);
        	 }

        	 for (int i = 0; i < allResults.size(); i++) {
        		 if (i == MAX_RESULTS_DISPLAY) {
        			 break;
        		 }
        		 
        		 final SearchResult sResult = allResults.get(i);
        		 
        		 LinearLayout linLayout = new LinearLayout(thisActivity);
        		 linLayout.setOrientation(LinearLayout.VERTICAL);
        		 linLayout.setPadding(0, 2, 0, 14);
        		 
        		 Button btnOpen = new Button(thisActivity);
        		 btnOpen.setText(getBookAbbreviation(sResult.getBookId())
	     	    			+ " " + (sResult.getChapterId() + 1));
        		 btnOpen.setOnClickListener(new OnClickListener() {
        			 public void onClick(View v) {
        				 Intent intent = new Intent(thisActivity, ScriptureActivity.class);
        				 ScripturePosition sp = new ScripturePosition(sResult.getBookId(),
        						 sResult.getChapterId());
        				 intent.putExtra(INTENT_SCRIPTURE_POSITION, sp);
        				 startActivity(intent);
        			 }
        		 });
        		 btnOpen.setLayoutParams(new LinearLayout.LayoutParams(
        				 LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0));
        		 linLayout.addView(btnOpen);
        		 
        		 TextView text = new TextView(thisActivity);
        		 text.setText(Html.fromHtml(sResult.getSample()));
     	    	 text.setPadding(5, 0, 5, 0);
     	    	 text.setTextSize(18);
     	    	 linLayout.addView(text);
     	    	 
     	    	 resultsContainer.addView(linLayout);
        	 }
        	 
        	 if (isNightMode()) {
             	applyNightMode();
             } else {
             	applyDayMode();
             }
        }          
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
	
	private String getBookAbbreviation(int bookId) {
 	   	String[] bookAbbrevs = resources.getStringArray(R.array.books_abbreviations_array);
 	   	return bookAbbrevs[bookId];
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(resources.getColor(R.color.night_back));
		for (int i = 0; i < resultsContainer.getChildCount(); i++) {
			LinearLayout linLayout = (LinearLayout) resultsContainer.getChildAt(i);
			for (int ii = 0; ii < linLayout.getChildCount(); ii++) {
				View child = linLayout.getChildAt(ii);
				if (!(child instanceof Button)) {
					((TextView) child).setTextColor(resources.getColor(R.color.night_text));
				}
			}
		}
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(resources.getColor(R.color.day_back));
		for (int i = 0; i < resultsContainer.getChildCount(); i++) {
			LinearLayout linLayout = (LinearLayout) resultsContainer.getChildAt(i);
			for (int ii = 0; ii < linLayout.getChildCount(); ii++) {
				View child = linLayout.getChildAt(ii);
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

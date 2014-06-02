package sk.suchac.hbe;

import java.util.List;

import sk.suchac.hbe.db.DAO;
import sk.suchac.hbe.helpers.PreferencesHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity {
	
	private View background;
	private TextView tvAbout1;
	private TextView tvAbout2;
	private TextView tvAbout3;
	private TextView tvAboutDescription;
	
	private DAO datasource;
	
	private Resources resources;
	
	private static boolean nightMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		datasource = new DAO(this);
		datasource.open();
		
		initializeElements();
		resources = getResources();
		
		List<String> abouts = datasource.getAbout();
		tvAbout1.setText(abouts.get(0));
		tvAbout2.setText(abouts.get(1));
		tvAbout3.setText(abouts.get(2));
		tvAboutDescription.setText(Html.fromHtml(abouts.get(3)));
		
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_about, menu);
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
		background = (View) findViewById(R.id.about_background);
		tvAbout1 = (TextView) findViewById(R.id.about_text1);
		tvAbout2 = (TextView) findViewById(R.id.about_text2);
		tvAbout3 = (TextView) findViewById(R.id.about_text3);
		tvAboutDescription = (TextView) findViewById(R.id.about_description);
	}
	
	private boolean isNightMode() {
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.PREFS, 0);
        nightMode = settings.getBoolean("nightMode", false);
		return nightMode;
	}
	
	private void applyNightMode() {
		background.setBackgroundColor(resources.getColor(R.color.night_back));
		tvAbout1.setTextColor(resources.getColor(R.color.night_text));
		tvAbout2.setTextColor(resources.getColor(R.color.night_text));
		tvAbout3.setTextColor(resources.getColor(R.color.night_text));
		tvAboutDescription.setTextColor(resources.getColor(R.color.night_text));
	}
	
	private void applyDayMode() {
		background.setBackgroundColor(resources.getColor(R.color.day_back));
		tvAbout1.setTextColor(resources.getColor(R.color.day_text));
		tvAbout2.setTextColor(resources.getColor(R.color.day_text));
		tvAbout3.setTextColor(resources.getColor(R.color.day_text));
		tvAboutDescription.setTextColor(resources.getColor(R.color.day_text));
	}
	
	private void saveNightModeState(boolean night) {
		SharedPreferences settings = getSharedPreferences(PreferencesHelper.PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("nightMode", night);
	    editor.commit();
	    nightMode = night;
	}

}

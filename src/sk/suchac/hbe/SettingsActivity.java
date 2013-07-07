package sk.suchac.hbe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.ListView;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private static Resources resources;
	public static final String KEY_PREF_FONT_SIZE = "pref_fontSize";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getResources();
        addPreferencesFromResource(R.xml.preferences);
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Preference prefFontSize = findPreference(KEY_PREF_FONT_SIZE);
        prefFontSize.setSummary(sharedPreferences.getString(KEY_PREF_FONT_SIZE, ""));
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_FONT_SIZE)) {
            Preference prefFontSize = findPreference(key);
            // Set summary to be the user-description for the selected value
            prefFontSize.setSummary(sharedPreferences.getString(key, ""));
        }
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
    	ListView lv = getListView();
    	lv.setBackgroundResource(R.color.night_back);
    	setTheme(R.style.NightMode);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
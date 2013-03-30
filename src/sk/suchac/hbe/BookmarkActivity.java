package sk.suchac.hbe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class BookmarkActivity extends Activity {
	
	private BookmarkActivity thisActivity = this;
	private LinearLayout bookmarkContainer;
	private Button buttonAddBookmark;
	private TableLayout bookmarkTable;
	
	private TextView tv;
	
	private static final File EXT_STORE_PATH = 
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
	private static final String EXT_STORE_FILE = "bookmarks.hbe";
	public static final String INT_STORE_PREFS = "HbeBookmarkPrefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);
		
		bookmarkContainer = (LinearLayout) findViewById(R.id.bookmark_container);
		buttonAddBookmark = (Button) findViewById(R.id.button_add_bookmark);
		buttonAddBookmark.setOnClickListener(buttonAddBookmarkListener);
		tv = (TextView) findViewById(R.id.bookmark_textView);
		bookmarkTable = (TableLayout) findViewById(R.id.bookmark_table);
		
		
		
		
		
//      File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
//      File file = new File(path, "blaa.txt");
//      path.mkdirs();
//      if (file.exists()) {
//      	textField.setText("exist");
//      } else {
//      	try {
//  			file.createNewFile();
//  			textField.setText("vytvoreny");
//  		} catch (IOException e) {
//  			textField.setText(Environment.getExternalStorageState());
//  		}
//      }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_bookmark, menu);
		return true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		StoragesState storState = getStoragesState();
		tv.append("\n" + storState.toString());
		updateStorages(storState);
		displayBookmarks();
	}
	
	private OnClickListener buttonAddBookmarkListener = new OnClickListener() {
	    public void onClick(View v) {
	    	// TODO iba zapis do pamati
	    }
	};
	
	private enum StoragesState {
		INTERNAL_NOT_EXTERNAL_UNAVAILABLE,
		INTERNAL_NOT_EXTERNAL_NOT,
		INTERNAL_NOT_EXTERNAL_IS,
		INTERNAL_IS_EXTERNAL_UNAVAILABLE,
		INTERNAL_IS_EXTERNAL_NOT,
		INTERNAL_IS_EXTERNAL_IS
	}
	
	private StoragesState getStoragesState() {
		boolean externalAvailable = 
				(Environment.getExternalStorageState().compareTo("mounted") == 0)
				? true : false;
		
		File path = EXT_STORE_PATH;
		File file = new File(path, EXT_STORE_FILE);
		boolean externalIs = (path.exists() && file.exists()) ? true : false;
		
		SharedPreferences settings = getSharedPreferences(INT_STORE_PREFS, 0);
		boolean internalIs = (settings.getAll().isEmpty()) ? false : true;
		
		if (!internalIs) {
			if (!externalAvailable) {
				return StoragesState.INTERNAL_NOT_EXTERNAL_UNAVAILABLE;
			} else {
				if (!externalIs) {
					return StoragesState.INTERNAL_NOT_EXTERNAL_NOT;
				} else {
					return StoragesState.INTERNAL_NOT_EXTERNAL_IS;
				}
			}
		} else {
			if (!externalAvailable) {
				return StoragesState.INTERNAL_IS_EXTERNAL_UNAVAILABLE;
			} else {
				if (!externalIs) {
					return StoragesState.INTERNAL_IS_EXTERNAL_NOT;
				} else {
					return StoragesState.INTERNAL_IS_EXTERNAL_IS;
				}
			}
		}
	}
	
	private void updateStorages(StoragesState storState) {
		if (storState == null) {
			return;
		}
		switch (storState) {
			case INTERNAL_NOT_EXTERNAL_UNAVAILABLE:
				break;
			case INTERNAL_NOT_EXTERNAL_NOT:
				break;
			case INTERNAL_NOT_EXTERNAL_IS:
				exportExternalToInternalStorage();
				break;
			case INTERNAL_IS_EXTERNAL_UNAVAILABLE:
				break;
			case INTERNAL_IS_EXTERNAL_NOT:
				exportInternalToExternalStorage();
				break;
			case INTERNAL_IS_EXTERNAL_IS:
				synchronizeInternalAndExternalStorages();
				break;
			default:
				break;
		}
	}
	
	private void exportExternalToInternalStorage() {
		ArrayList<String> extData = getExternalStoreData();
		SharedPreferences settings = getSharedPreferences(INT_STORE_PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		for (int i = 0; i < extData.size(); i++) {
			editor.putString(String.valueOf(i), extData.get(i));
		}
		editor.commit();
	}
	
	private ArrayList<String> getExternalStoreData() {
		ArrayList<String> lines = new ArrayList<String>();
		
		BufferedReader br = null;
		try {
			FileInputStream fs = new FileInputStream(EXT_STORE_FILE);
			br = new BufferedReader(new InputStreamReader(fs));
			
			String line = "";
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			
		} catch (FileNotFoundException e) {
			tv.append("\n" + e);
		} catch (IOException e) {
			tv.append("\n" + e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				tv.append("\n" + e);
			}
		}
		return lines;
	}

	private void exportInternalToExternalStorage() {
		SharedPreferences settings = getSharedPreferences(INT_STORE_PREFS, 0);
		Map<String,?> internal = settings.getAll();
		
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(new File(EXT_STORE_PATH, EXT_STORE_FILE)));
			
			for (int i = 0; i < internal.size(); i++) {
				out.write(internal.get(String.valueOf(i)).toString());
				if (i != (internal.size() - 1)) {
					out.newLine();
				}
			}
		} catch (IOException e) {
			tv.append("\n" + e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					tv.append("\n" + e);
				}
			}
		}
	}

	private void synchronizeInternalAndExternalStorages() {
		// TODO Auto-generated method stub
		
	}

	private void displayBookmarks() {
		// TODO nacitaj z pamati a v cykle rob:
//		TableRow tr = new TableRow(thisActivity);
//    	TextView text = new TextView(thisActivity);
//    	text.setText("");	// TODO
//    	tr.addView(text);
//    	tr.setOnTouchListener(new OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO
//				return false;
//			}
//		});
//    	Button btnActualize = new Button(thisActivity);
//    	btnActualize.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				// TODO
//			}
//		});
//    	tr.addView(btnActualize);
//    	Button btnDelete = new Button(thisActivity);
//    	btnDelete.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				// TODO
//			}
//		});
//    	tr.addView(btnDelete);
//    	bookmarkTable.addView(tr);
	}

}

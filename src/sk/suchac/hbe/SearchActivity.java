package sk.suchac.hbe;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends Activity {
	
	ListView checkList;
	private TextView tv;
	
	private static Resources resources;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		resources = getResources();
		
		checkList = (ListView) findViewById(R.id.listView1);
		tv = (TextView) findViewById(R.id.search_textView);
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_list_item_multiple_choice, resources.getStringArray(R.array.books_array));
		checkList.setAdapter(adapter);
		checkList.setOnItemClickListener(searchListListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}
	
	private OnItemClickListener searchListListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			tv.setText(String.valueOf(position));
		}
	};
	
//	String[] concatTwoArrays(String[] a, String[] b) {
//	   int aLen = a.length;
//	   int bLen = b.length;
//	   String[] c = new String[aLen + bLen];
//	   System.arraycopy(a, 0, c, 0, aLen);
//	   System.arraycopy(b, 0, c, aLen, bLen);
//	   return c;
//	}

}

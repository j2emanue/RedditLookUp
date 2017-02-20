package org.jefferyemanuel.mainStuff;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.jefferyemanuel.asynchtask.RetrieveRedits_LongOperation;
import org.jefferyemanuel.listeners.TaskCallbacks;
import org.jefferyemanuel.mainStuff.ReditListAdapter.Holder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.jefferyemanuel.mainStuff.Utils.createToast;
import static org.jefferyemanuel.mainStuff.Utils.printLog;


//import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends Activity implements
		SearchView.OnQueryTextListener, TaskCallbacks, OnItemClickListener {

	private DiskLruImageCache imageDiskCache;
	private ProgressDialog pdialog;
	private ListAdapter mAdapter;
	private ListView mListview;
	private SearchView mSearchView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//setTheme(SampleList.THEME); //Used for theme switching in samples
		super.onCreate(savedInstanceState);
		printLog(Consts.TAG,"calling onCreate");
		
		setContentView(R.layout.activity_main);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		/* CREATE OUR DISK CACHE TO STORE IMAGES */
		imageDiskCache = new DiskLruImageCache(this, "diskcache",
				Consts.DISK_CACHE_SIZE, CompressFormat.PNG,
				Consts.COMPRESS_QUALITY);

		mListview = (ListView) findViewById(android.R.id.list);

		/*load a default reddit listing on first launch - we can also use a sharedpreference to get users search previously/ 
		 */
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		String default_query=pref.getString(Consts.KEY_GROUP_DEFAULT_PREFERENCE, Consts.GROUP_DEFAULT);
		this.onQueryTextSubmit(default_query);
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// Place an action bar item for searching.
		//SearchView searchView = new SearchView(getSupportActionBar()
			//	.getThemedContext());
		 
		getMenuInflater().inflate(R.menu.main, menu);
		// make logo clickable
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		 mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		 mSearchView.setQueryHint("Search site");
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		    
		 // Get the SearchView and set the searchable configuration
	    
	    mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    mSearchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    
				mSearchView.setOnQueryTextListener(this);
		return true;

	}

	
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		if(intent.getAction().equals(Intent.ACTION_SEARCH))
		{
		/*the framework has delivered a search request to our app since we registered for to handle these in 
		 * out manifest file.  Lets get the users query from the intent URI and send a query request and lastly
		 * lets update the search view since this came from the user clicking on a suggestion*/
			String query=intent.getDataString();
			printLog(Consts.TAG,"Search Suggestion selected:"+query);
			
			if (query != null) 
			{
				mSearchView.setQuery(query, true);
				
			}
		}
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {

		if (query == null) 
			return false;

		//example call  "http://www.reddit.com/r/" + query + "/.json"; or  http://www.reddit.com/r/funny/.json

		URI uri = null;
		try {
			uri = new URI("http",
					"www.reddit.com",
					"/r/"+query.trim() + "/.json",null);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			String request = uri.toASCIIString();
		
		

		printLog(Consts.TAG,"beginning background task to retrieve reddits@:"+request);
		
		RetrieveRedits_LongOperation backgroundThread = new RetrieveRedits_LongOperation(this);
		backgroundThread.execute(request);
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString(Consts.KEY_GROUP_DEFAULT_PREFERENCE, query).commit();
		return false;
	}

	void showErrorDialog(String errorMessage) {

		MyDialogFragment newFragment = MyDialogFragment.newInstance(this);

		Bundle args = new Bundle();
		args.putString(Consts.MESSAGE, errorMessage);
		newFragment.setArguments(args);		
		newFragment.show(getFragmentManager(), "dialog");
	}

	

	@Override
	public void onPreExecute() {
		// TODO Auto-generated method stub

		if (pdialog == null) {
			pdialog = new ProgressDialog(MainActivity.this);
			pdialog.setMessage("Loading...");
			pdialog.show();
		}
		else
			pdialog.show();
	}

	@Override
	public void onProgressUpdate(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelled() {
		// TODO Auto-generated method stub
		if (pdialog != null && pdialog.isShowing())
			pdialog.dismiss();
	}

	@Override
	public void onPostExecute(ArrayList<HashMap<String, String>> userInfoMap) {
		// TODO Auto-generated method stub

		printLog(Consts.TAG, userInfoMap.toString());

		if (pdialog != null && pdialog.isShowing()) {
			pdialog.dismiss();
			pdialog = null;
		}

		if (userInfoMap != null && !userInfoMap.isEmpty()) {
			mAdapter = new ReditListAdapter(this, R.layout.list_item,
					userInfoMap, imageDiskCache);
			mListview.setAdapter(mAdapter);
			mListview.setOnItemClickListener(this);
		} else
			showErrorDialog(getString(R.string.warning_no_data_collected));

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		// TODO Auto-generated method stub

		Holder holder = (Holder) view.getTag();
		String message = holder.message;

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
		i.putExtra(Intent.EXTRA_SUBJECT, "check out this epic redit");
		i.putExtra(Intent.EXTRA_TEXT, message);
		try {
			startActivity(Intent.createChooser(i, "Share this app..."));
		} catch (android.content.ActivityNotFoundException ex) {
			createToast(this, "There are no email clients installed.");
		}

	}

	

}
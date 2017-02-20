package org.jefferyemanuel.asynchtask;

import java.util.ArrayList;
import java.util.Collections;

import org.jefferyemanuel.listeners.ReceiveGroupListener;
import org.jefferyemanuel.mainStuff.Consts;
import org.jefferyemanuel.mainStuff.JSONParser;
import static org.jefferyemanuel.mainStuff.Utils.*;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.Context;
import android.os.AsyncTask;

/* A background task that connects to reddit.com and yields the json response from http://www.reddit.com/subreddits.json  and parses the data.  lastly
 * sorts the data and returns it to any object implementing the interface ReceiveGroupListener*/
public class RetrieveGroups_LongOperation extends AsyncTask<String, Integer, ArrayList<String>> {

	
	private ReceiveGroupListener mCallbacks;
	private Context mContext;

	public RetrieveGroups_LongOperation(ContentProvider cp) {

		try {
			mCallbacks = (ReceiveGroupListener) cp;
		} catch (ClassCastException e) {
			throw new ClassCastException(cp.toString()
					+ " must implement  ReceiveGroupListing Listener");
		}
		this.mContext = cp.getContext();
		

	}
	
	@Override
	protected ArrayList<String> doInBackground(String... URLs) {
		String URL = URLs[0];
		
		ArrayList<String> groupInfo = new ArrayList<String>();
		JSONObject root = null;
		
		/* feed is network so we open a connection to parse live json */
		JSONParser parser = new JSONParser(mContext);
		root = parser.getJSONFromUrl(URL);

		if (root == null)
			return null;

		try {
			groupInfo=parser.parseJsonReditGroups(root);
		} catch (JSONException e) {
			printLog(Consts.TAG,"groupInfo parse error:"+e.toString());
			e.printStackTrace();
		}
		
		//do a logarhithm natural order sort on the data 
		Collections.sort(groupInfo);
		
		return groupInfo;

	}

	/* update main UI if all was well */
	@Override
	protected void onPostExecute(ArrayList<String> groupList) {

		super.onPostExecute(groupList);
		if (mCallbacks != null)
			mCallbacks.goupListingAcquired(groupList);
	}

	
}

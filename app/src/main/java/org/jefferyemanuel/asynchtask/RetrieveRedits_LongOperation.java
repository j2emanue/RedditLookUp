package org.jefferyemanuel.asynchtask;

import java.util.ArrayList;
import java.util.HashMap;

import org.jefferyemanuel.listeners.TaskCallbacks;
import org.jefferyemanuel.mainStuff.JSONParser;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

/*
 * background mechanism. Here we parse JSON feed feed and update main UI
 * Thread to beginning showing visuals
 */
public class RetrieveRedits_LongOperation extends AsyncTask<String, Integer, Boolean> {

	private TaskCallbacks mCallbacks;
	private Context mContext;
	ArrayList<HashMap<String, String>> userInfo;

	public RetrieveRedits_LongOperation(Context c) {

		try {
			mCallbacks = (TaskCallbacks) c;
		} catch (ClassCastException e) {
			throw new ClassCastException(c.toString()
					+ " must implement TaskCallBacks Listener");
		}
		this.mContext = c;
		userInfo = new ArrayList<HashMap<String, String>>();

	}

	/*
	 * just incase we get cancelled lets hide the progress dialog as its not a
	 * fragment dialog
	 */
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();

	}

	/*
	 * background process. Theory: Lets get all of our known subrededits. Now
	 * lets loop through all of our rededits and collect all the data.
	 */

	@Override
	protected Boolean doInBackground(String... URLs) {

		String URL = URLs[0];
		JSONObject root = null;
		
		

		/* feed is network so we open a connection to parse live json */
		JSONParser parser = new JSONParser(mContext);
		root = parser.getJSONFromUrl(URL);

		if (root == null)
			return false;

		userInfo=parser.parseJsonRedits(root);
		return !userInfo.isEmpty();

	}

	/* update main UI if all was well */
	@Override
	protected void onPostExecute(Boolean success) {

		super.onPostExecute(success);
		if (mCallbacks != null)
			mCallbacks.onPostExecute(userInfo);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	if(mCallbacks!=null)
		mCallbacks.onPreExecute();
	
	}

	@Override
	protected void onProgressUpdate(Integer... positions) {
	
	
	}

	

}

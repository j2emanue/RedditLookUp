package org.jefferyemanuel.listeners;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Callback interface through which the fragment will report the task's
 * progress and results back to the Activity.
 */
public interface TaskCallbacks {
	void onPreExecute();

	void onProgressUpdate(int value);

	void onCancelled();

	void onPostExecute(ArrayList<HashMap<String, String>> userInfoMap);
}



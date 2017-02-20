package org.jefferyemanuel.mainStuff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Html;
import android.util.Log;

/*  
 a abstraction of a JSON parser. */

public class JSONParser {

	static InputStream is = null;
	Context context;

	public JSONParser(Context c) {
		this.context = c;

	}

	/* parameter: a url string yielding json response */
	public JSONObject getJSONFromUrl(String url) {
			
		JSONObject object = null;
		HttpGet get = new HttpGet(url);
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setUseExpectContinue(params, false);
		get.setParams(params);

		try {
			/* try a get request */
			DefaultHttpClient client = new DefaultHttpClient();
			String response = client.execute(get, new BasicResponseHandler());

			object = new JSONObject(response);

		} catch (Exception e) {
			//TODO handle this somehow
			Log.e(Consts.TAG, e.toString());

		}

		// return JSON String
		return object;

	}

	/*
	 * function to parseJson response parameter: a jsonObject which contains the
	 * response from http://strong-earth-32.heroku.com/stores.aspx return: a
	 * list of maps contain each company/store info
	 */
	public ArrayList<HashMap<String, String>> parseJsonRedits(JSONObject root) {
		HashMap<String, String> object;
		ArrayList<HashMap<String, String>> userInfo = new ArrayList<HashMap<String, String>>();
		try {

			JSONObject data = root.getJSONObject("data");
			JSONArray children = (JSONArray) data.getJSONArray("children");

			for (int j = 0; j != children.length(); j++) {
				JSONObject childrenMember = children.getJSONObject(j);

				JSONObject childData = childrenMember.getJSONObject("data");

				object = new HashMap<String, String>();

				object.put(Consts.KEY_TITLE,
						childData.getString(Consts.KEY_TITLE));
				
				if (childData.isNull(Consts.KEY_AVATAR))
					object.put(Consts.KEY_AVATAR, null);
				else
					object.put(Consts.KEY_AVATAR,
							childData.getString(Consts.KEY_AVATAR));

				object.put(Consts.KEY_AUTHOR,
						childData.getString(Consts.KEY_AUTHOR));

				userInfo.add(object);
			}
			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userInfo;
	}

	/*PARSES THROUGH THE JSON OBJECTED RETURNED FROM http://www.reddit.com/subreddits.json*/
	public ArrayList<String> parseJsonReditGroups(JSONObject root)
			throws JSONException {
		ArrayList<String> groupNames = new ArrayList<String>();

		JSONArray children = root.getJSONObject("data")
				.getJSONArray("children");
		String group, description;
		for (int i = 0; children.length() != i; i++) {
			group = children.getJSONObject(i).getJSONObject("data")
					.getString("display_name");
			description = children.getJSONObject(i).getJSONObject("data")
					.getString("public_description");

			groupNames.add(concatString(",", group, description));
		}
		return groupNames;

	}

	/* puts group and Description OF EACH CATEGORY together so they match on retrieval */
	public String concatString(String delimiter, String group,
			String description) {
		StringBuilder builder = new StringBuilder();

		if (description == null || description.equals(""))
			description = "n/a";

		/* decode any encoded strings */
		description = Html.fromHtml(description).toString();

		/*
		 * just incase string has our delimiter (highly unlikely) we trim it
		 * from the string
		 */
		description = description.replaceAll(Consts.GROUP_DELIMITER, "").trim();

		int maxLength = (description.length() < Consts.GROUP_MAX_DESCRIPTION_LENGTH) ? description
				.length() : Consts.GROUP_MAX_DESCRIPTION_LENGTH;

		/*
		 * lets take only a porition of the description as it will be too long
		 * for our searchview suggestions list
		 */
		description = description.substring(0, maxLength);

		/*
		 * append group and description and seperate by a delimiter now our
		 * string looks something like this: funny,funny groups are fun
		 */

		builder.append(group).append(Consts.GROUP_DELIMITER)
				.append(description);

		/* captialize the first letter of our suggestion */
		builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
		return builder.toString();

	}

	
	
	/*below can be used for a local cache of the json object, current not in use*/
	/*
	 * save a file used for caching parameters: a file name and a jason response
	 * as string
	 */
	public void CreateAndSaveFile(String params, String mJsonResponse) {
		try {
			FileWriter file = new FileWriter("/data/data/"
					+ context.getPackageName() + "/" + params);
			file.write(mJsonResponse);
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * fetch cached jason object if exist. param: file name to check for json
	 * string source
	 */
	public JSONObject ReadJsonData(String params) {
		String response = null;
		try {
			File f = new File("/data/data/" + context.getPackageName() + "/"
					+ params);
			FileInputStream is = new FileInputStream(f);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			response = new String(buffer);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject object = null;
		try {
			if (response != null)
				object = new JSONObject(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

}

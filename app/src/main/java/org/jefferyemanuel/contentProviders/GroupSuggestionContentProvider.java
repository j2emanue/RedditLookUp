package org.jefferyemanuel.contentProviders;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.jefferyemanuel.asynchtask.RetrieveGroups_LongOperation;
import org.jefferyemanuel.listeners.ReceiveGroupListener;
import org.jefferyemanuel.mainStuff.Consts;

import java.util.ArrayList;

import static org.jefferyemanuel.mainStuff.Utils.printLog;

public class GroupSuggestionContentProvider extends ContentProvider implements
		ReceiveGroupListener {

	// UriMatcher constant for search suggestions
	private static final int SEARCH_SUGGEST = 1;

	private static final UriMatcher uriMatcher;
	private int i = 0;
	private static final String[] SEARCH_SUGGEST_COLUMNS = { BaseColumns._ID,
			SearchManager.SUGGEST_COLUMN_TEXT_1,
			SearchManager.SUGGEST_COLUMN_TEXT_2,
			SearchManager.SUGGEST_COLUMN_INTENT_DATA,
			SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
			SearchManager.SUGGEST_COLUMN_SHORTCUT_ID };

	private ArrayList<String> groupListing;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Consts.AUTHORITY,
				SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		uriMatcher.addURI(Consts.AUTHORITY,
				SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
	}

	@Override
	public int delete(Uri uri, String arg1, String[] arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreate() {

		loadGroups();

		return true;
	}

	/*INVOKES a background process to parse reddit category groups and calls respective call back.
	 * since we implement the call back we get notified by overriding goupListingAcquired */
	public void loadGroups() {

		RetrieveGroups_LongOperation backgroundThread = new RetrieveGroups_LongOperation(
				this);
		backgroundThread.execute(Consts.URL_SUBREDDITS);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		printLog(Consts.TAG, "query = " + uri);

		// Use the UriMatcher to see what kind of query we have
		switch (uriMatcher.match(uri)) {
		case SEARCH_SUGGEST:

			String query = uri.getLastPathSegment().toLowerCase();

			Log.d(Consts.TAG, "Search suggestions requested.");
			MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);

			String description,
			group;
			String[] groupDetails;
			if (groupListing != null)
				for (String info : groupListing) {

					groupDetails = info.split(Consts.GROUP_DELIMITER);
					group = groupDetails[0];
					/*
					 * check the users query to see if it starts with any of our
					 * groups, this is an applied filter
					 */
					if (group.toLowerCase().startsWith(query.toLowerCase())
							|| query.equals(SearchManager.SUGGEST_URI_PATH_QUERY)) {

						/*
						 * users query is either empty or matches one of the
						 * groups so we add it to the matrix cursor
						 */
						description = groupDetails[1];
						//printLog(Consts.TAG,group +" "+description+"-->"+i+"\n");
						cursor.addRow(new String[] { String.valueOf(++i),
								group, description, group, group,
								SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT });
					}
				}
			else
				/*
				 * we try to contact reddit to get groups again since our
				 * listing is empty (maybe due to network error) remember,
				 * contentProvider is only loaded once into memory so we have to
				 * try again to get the data after onCreate is called
				 */
				loadGroups();

			// EXAMPLE CALL
			//cursor.addRow(new String[] {
			//      "1", "funny", "description","funny","funny",SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT
			//});

			return cursor;
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues arg1, String arg2, String[] arg3) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void goupListingAcquired(ArrayList<String> list) {
		groupListing = list;

	}
}
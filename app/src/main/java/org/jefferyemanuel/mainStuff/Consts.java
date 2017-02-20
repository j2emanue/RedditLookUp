package org.jefferyemanuel.mainStuff;

import android.net.Uri;

/**
 * This class holds global constants that are used throughout the application to
 * support in-app billing.
 */

public class Consts {

	// lock the class from being instantiated as this is only a place holder for constants
	private Consts() {

	}

	//developer and logging options
	public static final boolean DEVELOPER_MODE =true;
	public static final String TAG = "reddit";
	public static final String TAG_FRAGMENT = "fragments";
	
	//MAP keys  (for json twitter object)
	public static final String KEY_AUTHOR = "author";
	public static final String KEY_TITLE = "title";

	public static final String KEY_AVATAR = "thumbnail";
	

	//dialogfragment constants
	public static final String MESSAGE = "message";

	
	//image disk cache constants
	public static final int DISK_CACHE_SIZE = 1024 * 1024 * 20; // 20MB
	public static final int COMPRESS_QUALITY = 100;
	 public static final int IO_BUFFER_SIZE = 8 * 1024;
	 
	 //URLs
	 public static final String URL_SUBREDDITS="http://www.reddit.com/subreddits.json";
	 
	//groups
	 public static final int GROUP_MAX_DESCRIPTION_LENGTH=50;
	 public static final String GROUP_DELIMITER="##";
	 public static final String GROUP_DEFAULT="funny";
	 public static final String KEY_GROUP_DEFAULT_PREFERENCE="grouppreference";
	 
	 //contentproviders
	 public static final String AUTHORITY="org.jefferyemanuel.mainStuff.search_suggestion_provider";
	 public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/search");

}

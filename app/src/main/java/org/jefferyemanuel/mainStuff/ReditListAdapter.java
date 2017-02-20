package org.jefferyemanuel.mainStuff;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReditListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<HashMap<String, String>> mUserPosts;
	private DiskLruImageCache imageDiskCache;

	public ReditListAdapter(Context context, int ResourceId,
			ArrayList<HashMap<String, String>> objects,
			DiskLruImageCache imageDiskCache) {

		this.imageDiskCache = imageDiskCache;
		this.context = context;
		mUserPosts = objects;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		return mUserPosts.size();
	}

	@Override
	public HashMap<String, String> getItem(int position) {
		// TODO Auto-generated method stub
		return mUserPosts.get(position);

	}

	@Override
	public long getItemId(int position) {
		// TODO fix this if we ever need to call by id
		return 0;
	}

	//TODO create image caching for images instead of http stream call continuously

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final View view;
		String message = null, avatarURL = null, author = null;

		/*
		 * GET the specific user status based on position from our array of
		 * hashmaps. This will give a list of all our users statuses
		 */
		HashMap<String, String> storeMap = getItem(position);

		//view might be recycled we check here
		if (convertView != null) {
			view = convertView;

		} else {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item, parent, false);
		}

		//alloc our views to load with textual data
		TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
		ImageView iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
		TextView tv_author = (TextView) view.findViewById(R.id.tv_author);

		message = (String) storeMap.get(Consts.KEY_TITLE);
		avatarURL = (String) storeMap.get(Consts.KEY_AVATAR);
		author = (String) storeMap.get(Consts.KEY_AUTHOR);
		/*
		 * retrieve the actual http images off the main Thread from disk or if
		 * not available then from web
		 */

		imageDiskCache.getBitmap(avatarURL, iv_avatar);

		tv_message.setText(message);
		tv_author.setText("@"+author.toUpperCase());

		Holder holder = new Holder();
		holder.message = message;
		holder.avatar = avatarURL;
		holder.author = author;

		view.setTag(holder);

		return view;

	}

	/* container for our cell item info to add to each cell */
	protected class Holder {

		String author;
		String message;
		String avatar;

	}

}

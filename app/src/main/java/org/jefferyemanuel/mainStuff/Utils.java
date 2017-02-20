package org.jefferyemanuel.mainStuff;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Utils {

	private Utils() {};
	
	
	public static void printLog(String Tag, String msg) {

		if (Consts.DEVELOPER_MODE)
			Log.v(Tag, msg);
	}

	public static void hideKeyboard(Activity activity) {
		try {
			InputMethodManager inputManager = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			// Ignore exceptions if any
			//Log.e("KeyBoardUtil", e.toString(), e);
		}
	}

	/* creates a custom toast message, gives our app flavor */
	public static void createToast(Context context,String msg) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.toast_layout, null);

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(msg);

		Toast toast = new Toast(context);
		toast.setGravity(Gravity.BOTTOM, 0, 40);//TODO convert by display metric

		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
	

	 public static boolean isExternalStorageRemovable() {
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
	            return Environment.isExternalStorageRemovable();
	        }
	        return true;
	    }

	    public static File getExternalCacheDir(Context context) {
	        if (hasExternalCacheDir()) {
	            return context.getExternalCacheDir();
	        }

	        // Before Froyo we need to construct the external cache dir ourselves
	        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
	        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	    }

	    public static boolean hasExternalCacheDir() {
	        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	    }


}

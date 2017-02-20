package org.jefferyemanuel.mainStuff;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class Linkifier {

	public TextView setLinks(TextView tv, String text, long twitterId) {
		String[] linkPatterns = {
				"([Hh][tT][tT][pP][sS]?:\\/\\/[^ ,'\">\\]\\)]*[^\\. ,'\">\\]\\)])",
				"#[\\w]+", "@[\\w]+" };
		for (String str : linkPatterns) {
			Pattern pattern = Pattern.compile(str);
			Matcher matcher = pattern.matcher(tv.getText());
			while (matcher.find()) {
				int x = matcher.start();
				int y = matcher.end();
				final android.text.SpannableString f = new android.text.SpannableString(
						tv.getText());
				InternalURLSpan span = new InternalURLSpan();
				span.text = text.substring(x, y);
				span.twitterId = twitterId;
				f.setSpan(span, x, y,
						android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				tv.setText(f);
				// tv.setOnLongClickListener(span.l);
			
			}
		}
		tv.setLinkTextColor(tv.getContext().getResources()
				.getColorStateList(R.color.crystal_blue));
		tv.setLinksClickable(true);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		//tv.setFocusable(false);
//tv.setFocusableInTouchMode(false);
		return tv;
	}

	class InternalURLSpan extends android.text.style.ClickableSpan {
		public String text;
		public long twitterId;

		@Override
		public void onClick(View widget) {
			//Utils.createToast(widget.getContext(),text);
			handleLinkClicked(widget.getContext(), text);
		}

		public void handleLinkClicked(Context context, String value) {
			if (value.startsWith("http")) {

				Utils.printLog(Consts.TAG, "value clicked begins with http:"
						+ value);
				// handle http links
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(value));
				context.startActivity(browserIntent);

			} else if (value.startsWith("@")) {
				// handle @links

				//-------
				String userName = value.replaceFirst("@", "");
				Intent intent = null;

				intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://twitter.com/" + userName));

				context.startActivity(intent);

				//--------
			} else if (value.startsWith("#")) { // handle #links
				String searchTerm = text.replace("#", "");

				String query = null;
				try {
					query = URLEncoder.encode(value, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						//Uri.parse(Const
				//context.startActivity(browserIntent);
			}
		}
	}

}

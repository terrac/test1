package com.kos.reader;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class ContentActivity extends Activity {

	StringBuffer value;
	StringBuffer initial;
	private String error;
	private AsyncTask<String, Void, String> atask;
	final public static String css = "		<script type=\"text/javascript\"><!--\n"
			+ "		  document.write('<link href=\"/c/enhanced.css\" rel=\"stylesheet\" media=\"screen, projection\" type=\"text/css\" />');\n"
			+ "	<link href=\"/c/unified.css?rev=46\" media=\"all\" rel=\"stylesheet\" type=\"text/css\" />\n"
			+ "	<!-- can this be unified? -->\n"
			+ "	<link href=\"/c/print.css\" rel=\"stylesheet\" media=\"print\" type=\"text/css\" />\n"
			+ "		"
			+ "		//--></script>"
			+ "<style type=\"text/css\">"
			+ "A:link {text-decoration: none; color: orange;}"
			+ "A:visited {text-decoration: none; color: #654B0F;}"
			+ "A:active {text-decoration: none; color: orange;}"
			+ "A:hover {text-decoration: underline; color: #654B0F;}"
			+ "</style>";
	static String iframetoLink = 
			"   $(\"iframe\").each(function(i, elem) {\r\n" + 
			"            var img = $(elem);\r\n" + 
			"            var name = img.attr(\"alt\");\r\n" + 
			"            if (name == \"\"||name ==undefined) {\r\n" + 
			"                name = \"youtube\";"+
			"\r\n" + 
			"\r\n" + 
			"            }\r\n" + 
			"            var link = \"<a href=\" + img.attr(\"src\") + \">\" + name + \"</a>\";\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"            img.replaceWith(link);\r\n" + 
			"        });\r\n" + 
			"  \r\n";
	static String imagetoLink = 
			"   $(\"img\").each(function(i, elem) {\r\n" + 
			"            var img = $(elem);\r\n" + 
			"            var name = img.attr(\"alt\");\r\n" + 
			"            if (name == \"\"||name ==undefined) {\r\n" + 
			"                name = img.attr(\"src\");\r\n" + 
			"                var fileNameIndex = name.lastIndexOf(\"/\") + 1;\r\n" + 
			"                var name = name.substr(fileNameIndex);\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"            }\r\n" + 
			"            var link = \"<a href=\" + img.attr(\"src\") + \">\" + name + \"</a>\";\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"            img.replaceWith(link);\r\n" + 
			"        });\r\n" + 
			"  \r\n";
	
	static String jquery ="        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js\"></script>\r\n" + 
			"        <script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.11/jquery-ui.min.js\"></script>\r\n" + 
			"";
	@Override
	protected void onStart() {
		doInitialLoad();
		super.onStart();
	}

	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// doInitialLoad();
	// }

	public void doInitialLoad() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (preferences.getBoolean("showBar", true)) {
			setContentView(R.layout.activity_content);

		} else {
			setContentView(R.layout.activity_content_no_bar);
			ContentActivity.removeAd(this,preferences);

		}

		StringBuffer stringExtra = new StringBuffer(getIntent().getStringExtra("content"));
		WebView tv = (WebView) findViewById(R.id.textView1);
		// These two settings scale the page to the screen
		// tv.getSettings().setSupportZoom( true ); //Modify this
		// tv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);//Add
		// this
		// getActionBar().
		// Uri parcelableExtra = getIntent().getParcelableExtra("uri");
		// String second=get(parcelableExtra,
		// stringExtra.substring(stringExtra.length()-10));

		if (value == null) {

			initial = new StringBuffer(stringExtra);
			stringExtra.append("<br> loading ....");
			value = stringExtra;
			loadData(tv, stringExtra, 0);

			atask = new AsyncTask<String, Void, String>() {

				@Override
				protected String doInBackground(String... params) {
					StringBuffer stringExtra = new StringBuffer();

					Uri parcelableExtra = getIntent().getParcelableExtra("uri");

					try {
						Document doc = Jsoup
								.connect(parcelableExtra.toString()).get();
						stringExtra.append(doc.getElementById("intro").toString());
						stringExtra.append(doc.getElementById("body").toString());
//						stringExtra = doc.getElementsByClass("article-body")
//								.get(0).toString();
						Element comments = doc.getElementById("comments");

						comments.getElementById("eP").remove();
						comments.getElementById("e").remove();
//						Elements ele = doc.select("iframe[src]");
//						ele.before("<a href=" + ele.get(0).attr("src")
//								+ ">youtube</a>");

						getIntent().putExtra("comments", comments.toString());
					} catch (IOException e) {
						error = "Unable to load";

					}
					stringExtra.append("<a href=" + parcelableExtra
							+ "> go to kos page</a>");
					// stringExtra = ContentActivity.this.get(parcelableExtra);
					value = stringExtra;
					return null;
				}

				@Override
				protected void onPostExecute(String result) {

					if (error != null) {
						Toast.makeText(getApplicationContext(), error,
								Toast.LENGTH_LONG).show();

						error = null;
						return;
					}
					Context context = getApplicationContext();
					CharSequence text = "Loaded.";
					int duration = Toast.LENGTH_SHORT;

					if (initial.length() > value.length()) {
						text = "Already Loaded";
						Uri parcelableExtra = getIntent().getParcelableExtra(
								"uri");

						initial.append( "<a href=" + parcelableExtra
								+ "> go to kos page</a>");
						value = initial;
					}
					Toast toast = Toast.makeText(context, text, duration);
					toast.setGravity(Gravity.BOTTOM, 0, 0);
					toast.show();

					// Get the current WebView position
					WebView tv = (WebView) findViewById(R.id.textView1);
					loadData(tv, value, tv.getScrollY());

				}
			}.execute("");
			return;
		}
		loadData(tv, value, 0);
	}

	public static void removeAd(Activity a,SharedPreferences preferences) {
		if (!preferences.getBoolean(("displayAd"), true)) {
			View findViewById = a.findViewById(R.id.adView);
			if(findViewById != null){
				findViewById.setVisibility(View.GONE);
			}
		}
	}

	public void showTutorial() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		if (!preferences.getBoolean("tutorial", false)) {
			Editor edit = preferences.edit();
			edit.putBoolean("tutorial", true);
			edit.commit();
			Toast.makeText(getApplicationContext(),
					"Use settings to format the diaries", Toast.LENGTH_SHORT)
					.show();

		}
	}

	public void loadData(WebView tv, StringBuffer stringExtra, int yPos) {
		// if (plainText) {
		// stringExtra = stripHtml(stringExtra);
		// }
		tv.setWebChromeClient(new WebChromeClient());
		tv.getSettings().setJavaScriptEnabled(true);
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		stringExtra.append(jquery);
		
		stringExtra.insert(0, css);
		ignoreImages(this, preferences,stringExtra);
		if (!preferences.getBoolean("iframeEnabled", false)) {
			stringExtra.append(CommentsActivity.addScript(iframetoLink));
			
		}

		fontSize(stringExtra, preferences);

		// ScrollY is only available in ICS or later
		setScrollYPos(tv, yPos);

		tv.loadDataWithBaseURL("http://www.dailykos.com", stringExtra.toString(),
				"text/html", "UTF-8", "about:blank");
	}

	public static void fontSize(StringBuffer stringExtra,
			SharedPreferences preferences) {
		if (!preferences.getBoolean("fontEnabled", false)) {
			return ;
		}
		int fontSize;
		try {
			fontSize = Integer.parseInt(preferences.getString("fontSize", "0"));
		} catch (NumberFormatException e) {
			fontSize = 0;
		}
		if (fontSize != 0) {
			String size = "<style type=\"text/css\">\n" + "body\n"
					+ "{ font-size:" + fontSize + "px; }\n" + "</style> ";
			stringExtra.insert(0, size);
		}
	}

	public static void ignoreImages(Activity a, SharedPreferences preferences,StringBuffer sb) {

		WebView tv = (WebView) a.findViewById(R.id.textView1);

		tv.getSettings().setLoadsImagesAutomatically(
				preferences.getBoolean("imagesEnabled", false));
		if(!preferences.getBoolean("imagesEnabled", false)){
			sb.append(CommentsActivity.addScript(imagetoLink));	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_content, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (R.id.menu_comments == item.getItemId()) {
			comments(null);

		}

		if (R.id.menu_preferences == item.getItemId()) {
			preferences(null);
		}
		return super.onOptionsItemSelected(item);
	}

	public void preferences(View v) {
		Intent myIntent = new Intent(ContentActivity.this, PrefsFragment.class);
		ContentActivity.this.startActivity(myIntent);

	}

	public void comments(View v) {
		Intent myIntent = new Intent(ContentActivity.this,
				CommentsActivity.class);
		String stringExtra = getIntent().getStringExtra("comments");
		if (stringExtra != null) {
			myIntent.putExtra("comments", stringExtra);
			ContentActivity.this.startActivity(myIntent);
		} else {
			Toast.makeText(getApplicationContext(), "Not Loaded Yet",
					Toast.LENGTH_SHORT).show();

		}

	}

	// public String get(Uri uri) {
	// try {
	// HttpClient httpClient = new DefaultHttpClient();
	// HttpContext localContext = new BasicHttpContext();
	// HttpGet httpGet = new HttpGet("" + uri);
	// HttpResponse response = httpClient.execute(httpGet, localContext);
	// StringBuffer result = new StringBuffer();
	// StringBuffer comments = new StringBuffer();
	// BufferedReader reader = new BufferedReader(new InputStreamReader(
	// response.getEntity().getContent()));
	//
	// String line = null;
	// int count = 0;
	// while ((line = reader.readLine()) != null) {
	// if (count > 2) {
	//
	// if (line.contains("<li id=\"c2\">")) {
	// count = 4;
	// }
	// if (count == 4) {
	// // Log.d("content", line);
	// comments.append(line + "\n");
	// }
	// if (line.contains("<li id=\"eP\">")) {
	// this.getIntent().putExtra("comments",
	// comments.toString());
	// break;
	// }
	// }
	//
	// if (count > 0 && count < 3) {
	// // Log.d("content", line);
	// result.append(line + "\n");
	// if (line.contains("</div>")) {
	// count++;
	//
	// }
	// } else {
	// if (line.contains("<div id=\"intro\">")) {
	// count = 1;
	// }
	// }
	// }
	// return result.toString();
	// } catch (Throwable e) {
	// error = "Unable to load";
	// }
	// return null;
	// }

	public String stripHtml(String a) {
		Html.fromHtml(a);
		return null;
		// return a.replaceAll("<(^a|^p|^\\s|^blockquote)+?>", "");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("value", value.toString());

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		value = new StringBuffer(savedInstanceState.getString("value"));

		super.onRestoreInstanceState(savedInstanceState);
	}

	@TargetApi(14)
	private void setScrollYPos(WebView tv, int yPos) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			tv.setScrollY(yPos);
		}
	}

	@Override
	protected void onPause() {
		if (atask != null) {
			atask.cancel(true);

		}
		super.onPause();
	}
}

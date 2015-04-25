package org.example.top10downloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	//Volley
	public final String TOP_10_DOWNLOAD_APPS_URL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
	private final String REQUEST_TAG = "RSSFEED";
	RequestQueue queue;
	//Components
	private ListView appList;
	private ProgressBar pgDownloadData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initComponents();
		queue = Volley.newRequestQueue(this);
		
		pgDownloadData.setVisibility(View.VISIBLE);
		StringRequest top10AppsRSSRequest = new StringRequest(Request.Method.GET, TOP_10_DOWNLOAD_APPS_URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String xmlData) {
				pgDownloadData.setVisibility(View.INVISIBLE);
				ArrayList<Application> apps = ApplicationParser.parse(xmlData);
				ArrayAdapter<Application> adapter = new ArrayAdapter<Application>(MainActivity.this, R.layout.app_list_item, apps);
				appList.setAdapter(adapter);
				appList.setVisibility(View.VISIBLE);
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				Toast.makeText(MainActivity.this, "Cannot download rss feed.", Toast.LENGTH_LONG).show();
			}
			
		});
		top10AppsRSSRequest.setTag(REQUEST_TAG);
		queue.add(top10AppsRSSRequest);
	}
	
	private void initComponents() {
		this.appList = (ListView) findViewById(R.id.app_list);
		this.pgDownloadData = (ProgressBar) findViewById(R.id.download_pb);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (queue != null)
			queue.cancelAll(REQUEST_TAG);
	}
	
	
}

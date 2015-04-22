package org.example.top10downloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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

public class MainActivity extends Activity {

	//URL
	public final String TOP_10_DOWNLOAD_APPS_URL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
	
	//Components
	private ListView appList;
	private ProgressBar pgDownloadData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initComponents();
		DownloadData downloader = new DownloadData(appList);
		downloader.execute(TOP_10_DOWNLOAD_APPS_URL);
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
	
	private class DownloadData extends AsyncTask<String, Void, Void> {
		
		private String xmlData;
		private boolean downloaded;
		private int totalCharacters;
		private ListView container;
		
		public DownloadData(ListView container) {
			this.xmlData = "";
			this.downloaded = false;
			this.totalCharacters = 0;
			this.container = container;
		}
		
		public boolean isDownloaded() {
			return downloaded;
		}

		public String getXmlData() {
			return xmlData;
		}
		

		@Override
		protected void onPreExecute() {
			pgDownloadData.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(String... urls) {
			try {
				xmlData += downloadXML(urls[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.d("DownloadXML", "Characters = " + totalCharacters);
			pgDownloadData.setVisibility(View.INVISIBLE);
			downloaded = true;
			ArrayList<Application> apps = ApplicationParser.parse(this.xmlData);
			ArrayAdapter<Application> adapter = new ArrayAdapter<Application>(MainActivity.this, R.layout.app_list_item, apps);
			container.setAdapter(adapter);
			container.setVisibility(View.VISIBLE);
			
		}

		private String downloadXML(String url) throws IOException {
			final int BUFFER_SIZE = 2000;
			final int READ_TIMEOUT = 10000;
			final int CONNECTION_TIMEOUT = 15000;
			
			InputStreamReader in = null;
			
			String xmlContents = "";
			
			try {
				URL urlConnection = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) urlConnection.openConnection();
				conn.setReadTimeout(READ_TIMEOUT);
				conn.setConnectTimeout(CONNECTION_TIMEOUT);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				int response = conn.getResponseCode();
				Log.d("DownloadXML", "The response is: " + response);
				
				if (response == 200) { //OK
					in = new InputStreamReader(conn.getInputStream());
					
					int charsRead;
					char[] inputBuffer = new char[BUFFER_SIZE];
					
					while ((charsRead = in.read(inputBuffer)) > 0) {
						totalCharacters += charsRead;
						xmlContents += String.copyValueOf(inputBuffer, 0, charsRead);
						inputBuffer = new char[BUFFER_SIZE];
					}
					return xmlContents;
				} else {
					throw new IOException("Error getting feed.");
				}
			} finally {
				if (in != null)
					in.close();
			}
		}
		
	}
	
}

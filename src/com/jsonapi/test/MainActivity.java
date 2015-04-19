package com.jsonapi.test;

import java.util.List;

import util.InternetConnection;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.jsonapi.test.info.DownloadInfoIntentService;
import com.jsonapi.test.info.InfoParsedListener;
import com.jsonapi.test.info.InfoParser;
import com.jsonapi.test.list.InfoListAdapter;
import com.jsonapi.test.list.ListViewItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends YouTubeBaseActivity implements
		InfoParsedListener {

	public static final String INFO_URL = "http://api.naij.com/test.json";

	private Button getInfoButton;

	private ListView listView;

	private static DisplayImageOptions defaultOptions;
	
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		progressBar = (ProgressBar) findViewById(R.id.main_progressbar);
		progressBar.setVisibility(View.GONE);
		
		initImageLoader();

		getInfoButton = (Button) findViewById(R.id.mainButton);
		getInfoButton.setOnClickListener(getInfoButtonClickListner);

		listView = (ListView) findViewById(R.id.mainListView);
		if (InfoParser.getInstance(this).isInfoParsed()) {
			listView.setAdapter(new InfoListAdapter(this, InfoParser
					.getInstance(MainActivity.this).getInfoItemsList()));
		}
	}

	private OnClickListener getInfoButtonClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (InternetConnection.isInternetConnected(MainActivity.this)) {
				progressBar.setVisibility(View.VISIBLE);
				getInfo();
			} else {
				Toast.makeText(MainActivity.this, 
						MainActivity.this.getResources().getString(R.string.nointernet), 
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String result = bundle
						.getString(DownloadInfoIntentService.INFO_KEY);
				int resultCode = bundle
						.getInt(DownloadInfoIntentService.RESULT_KEY);
				if (resultCode == RESULT_OK) {
					InfoParser parser = InfoParser.getInstance(MainActivity.this);
					parser.parse(result);
				} else {
					Toast.makeText(MainActivity.this, 
							MainActivity.this.getResources().getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	public static DisplayImageOptions getImageLoaderOptions() {
		if (defaultOptions == null) {
			defaultOptions = new DisplayImageOptions.Builder()
					.cacheOnDisk(true).cacheInMemory(true).build();
		}
		return defaultOptions;
	}

	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).build();
		ImageLoader.getInstance().init(config);

	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter(
				DownloadInfoIntentService.INTENT_FILTER));
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void getInfo() {
		Intent downloadService = new Intent(this,
				DownloadInfoIntentService.class);
		downloadService.putExtra(DownloadInfoIntentService.URL_KEY, INFO_URL);
		startService(downloadService);
	}

	@Override
	public void infoParsed(List<ListViewItem> urls) {
		Log.d("info parsed method", "called");
		listView.setAdapter(new InfoListAdapter(this, InfoParser.getInstance(
				MainActivity.this).getInfoItemsList()));
		progressBar.setVisibility(View.GONE);
	}
}

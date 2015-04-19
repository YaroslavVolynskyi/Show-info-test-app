package com.jsonapi.test.info;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadInfoIntentService extends IntentService {

	public static final String RESULT_KEY = "resultkey";
	
	public static final String INFO_KEY = "infokey";
	
	public static final String URL_KEY = "urlkey";
	
	public static final String INTENT_FILTER = "com.jsonapi.test.info";
	
	private int result = Activity.RESULT_CANCELED;
	
	public DownloadInfoIntentService() {
		super("download service");
	}
	
	public DownloadInfoIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("info service", " is running");
		String urlPath = intent.getStringExtra(URL_KEY);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(urlPath);
		HttpResponse response = null;
		String responseString = null;
		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		result = Activity.RESULT_OK;
		publishResult(responseString, result);
	}
	
	private void publishResult(String text, int result) {
		Intent intent = new Intent(INTENT_FILTER);
		intent.putExtra(RESULT_KEY, result);
		intent.putExtra(INFO_KEY, text);
		sendBroadcast(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	
}

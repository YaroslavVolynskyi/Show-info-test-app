package com.jsonapi.test.info;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.util.Log;

import com.jsonapi.test.list.ListViewItem;

public class InfoParser {

	InfoParsedListener infoParsedListener;
	
	private static final String CONTENT_KEY = "content";

	private static final String CREATED_KEY = "created";

	private static final String TITLE_KEY = "title";

	private static final String IMAGE_PATTERN = "src=\\\"http.*?jpg";

	private static final String TAG_PATTERN = "(<p>(.|\n)*?</p>)|(<iframe(.|\n)*?</iframe>)";

	private static final String VIDEO_ID_PATTERN = "embed(.*?)\"";

	private boolean infoParsed = false;
	
	private List<ListViewItem> infoItems;

	private InfoParser(InfoParsedListener infoParsedListener) {
		this.infoParsedListener = infoParsedListener;
	}

	private static InfoParser instance;

	public static InfoParser getInstance(InfoParsedListener infoParsedListener) {
		if (instance == null) {
			instance = new InfoParser(infoParsedListener);
		}
		return instance;
	}

	public List<ListViewItem> getInfoItemsList() {
		return this.infoItems;
	}

	public boolean isInfoParsed() {
		return this.infoParsed;
	}

	public void parse(String responseString) {
		infoItems = new ArrayList<>();
		JSONObject response = (JSONObject) JSONValue.parse(responseString);
		String title = (String) response.get(TITLE_KEY);
		String created = convertTime(response.get(CREATED_KEY).toString());
		created = new StringBuffer("Created: ")
			.append(created)
			.toString();
		infoItems.add(new ListViewItem(title, null, null));
		infoItems.add(new ListViewItem(created, null, null));
		String content = (String) response.get(CONTENT_KEY);
		parseContent(content);
	}
	
	private String convertTime(String unixTime) {
		long unixSeconds = Long.parseLong(unixTime);
		Date date = new Date(unixSeconds * 1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getDefault()); 
		String formattedDate = sdf.format(date);
		return formattedDate;
	}

	public static String videoUrl;

	private void parseContent(String contentInfo) {
		List<String> listItems = new ArrayList<>();
		Pattern pattern = Pattern.compile(TAG_PATTERN);
		Matcher matcher = pattern.matcher(contentInfo);
		while (matcher.find()) {
			if (matcher.group(0).contains("iframe")) {
				listItems.add(matcher.group(0));
			} else {
				listItems.add(matcher.group(0).substring(3,
						matcher.group(0).length() - 4));
			}
		}

		for (String item : listItems) {
			Pattern imagePattern = Pattern.compile(IMAGE_PATTERN);
			Matcher imageMatcher = imagePattern.matcher(item);
			if (imageMatcher.find()) {
				imageMatcher.reset();
				while (imageMatcher.find()) {
					infoItems.add(new ListViewItem(null, imageMatcher.group(0).substring(5), null));
				}
			} else if (item.contains("iframe")){
				Pattern videoIdPattern = Pattern.compile(VIDEO_ID_PATTERN);
				Matcher videoMatcher = videoIdPattern.matcher(item);
				while (videoMatcher.find()) {
					infoItems.add(new ListViewItem(null, null, videoMatcher.group(0).substring(6, videoMatcher.group(0).length() - 1)));
				}
			} else {
				infoItems.add(new ListViewItem(item, null, null));
			}
		}

		infoParsedListener.infoParsed(infoItems);
		infoParsed = true;
	}
}

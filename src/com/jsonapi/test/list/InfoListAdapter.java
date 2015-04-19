package com.jsonapi.test.list;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.opengl.Visibility;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.jsonapi.test.MainActivity;
import com.jsonapi.test.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class InfoListAdapter extends ArrayAdapter<ListViewItem> implements
		YouTubePlayer.OnInitializedListener {

	private List<ListViewItem> items;

	private Context context;

	private String currentVideoId;

	public InfoListAdapter(Context context, List<ListViewItem> items) {
		super(context, 0, items);
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (LinearLayout) inflater.inflate(
					R.layout.infolist_item, parent, false);
		}
		
		convertView.setOnClickListener(null);

		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.item_imageview);
		TextView textView = (TextView) convertView
				.findViewById(R.id.item_textview);
		YouTubePlayerView playerView = (YouTubePlayerView) convertView
				.findViewById(R.id.item_youtube_view);

		ListViewItem item = items.get(pos);
		if (item.getText() == null) {
			textView.setVisibility(View.GONE);
		} else {
			textView.setVisibility(View.VISIBLE);
			textView.setText(Html.fromHtml(item.getText()));
		}
		if (item.getUrl() == null) {
			imageView.setVisibility(View.GONE);
		} else {
			imageView.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(item.getUrl(), imageView,
					MainActivity.getImageLoaderOptions());
		}
		if (item.getVideoId() == null) {
			playerView.setVisibility(View.GONE);
		} else {
			playerView.setVisibility(View.VISIBLE);
			currentVideoId = item.getVideoId();
			playerView.initialize(
					context.getResources().getString(R.string.youtube_apikey),
					this);
			notifyDataSetChanged();
		}

		return convertView;
	}

	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider,
			YouTubeInitializationResult errorReason) {
		String errorMessage = errorReason.toString();
		Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.loadVideo(currentVideoId);
		}
	}
}

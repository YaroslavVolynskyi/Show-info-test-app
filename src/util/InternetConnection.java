package util;

import android.content.Context;
import android.net.ConnectivityManager;

public class InternetConnection {
	
	public static boolean isInternetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}

}

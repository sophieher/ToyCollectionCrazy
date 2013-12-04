package com.soph1her.toycollectioncrazyscarygirledition;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.ImageView;


// Class to load images for list view
// Uses AsyncTask to load images in the background.
//TODO: Currently is not set up.
public class LoadImageAsyncTask extends
AsyncTask<Void, Void, Pair<Bitmap, Exception>> {

	private ImageView mImageView;
	private String mUrl;
	private BitmapCache mCache;

	public LoadImageAsyncTask(BitmapCache cache, ImageView imageView, String url) {
		mCache = cache;
		mImageView = imageView;
		mUrl = url;

		mImageView.setTag(mUrl);
	}

	@Override
	protected void onPreExecute() {
		Bitmap bm = mCache.get(mUrl);

		if(bm != null) {
			cancel(false);

			mImageView.setImageBitmap(bm);
		}
	}

	@Override
	protected Pair<Bitmap, Exception> doInBackground(Void... arg0) {
		if(isCancelled()) {
			return null;
		}

		URL url;
		InputStream inStream = null;
		try {
			url = new URL(mUrl);
			URLConnection conn = url.openConnection();

			inStream = conn.getInputStream();

			Bitmap bitmap = BitmapFactory.decodeStream(inStream);

			return new Pair<Bitmap, Exception>(bitmap, null);

		} catch (Exception e) {
			return new Pair<Bitmap, Exception>(null, e);
		}
		finally {
			closeSilenty(inStream);
		}
	}

	@Override
	protected void onPostExecute(Pair<Bitmap, Exception> result) {
		if(result == null) {
			return;
		}

		if(result.first != null && mUrl.equals(mImageView.getTag())) {
			mCache.put(mUrl, result.first);
			mImageView.setImageBitmap(result.first);
		}
	}

	public void closeSilenty(Closeable closeable) {
		if(closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

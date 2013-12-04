package com.soph1her.toycollectioncrazyscarygirledition;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

//Class to cache images for list view
// Uses LruCache - example from stackoverflow from google IO
//TODO: Currently is not set up, not working
public class BitmapCache extends LruCache<String, Bitmap> {

	public BitmapCache(int sizeInBytes) {
		super(sizeInBytes);
	}   

	public BitmapCache(Context context) {
		super(getOptimalCacheSizeInBytes(context));
	}   

	public static int getOptimalCacheSizeInBytes(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		int memoryClassBytes = am.getMemoryClass() * 1024 * 1024;

		return memoryClassBytes / 8;
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}
}

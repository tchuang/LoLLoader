package com.crowdcompass.lolloader.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.crowdcompass.lolloader.model.FlickrPhoto;
import com.crowdcompass.lolloader.util.FlickrUtil;

public class RetainedFragment extends Fragment {

	private static final String TAG = RetainedFragment.class.getSimpleName();

	private FlickrPhoto photo;

	private OnTaskRunningListener mCallback;
	private boolean isRunning;

	public interface OnTaskRunningListener {
		void onPreExecute();
		void onProgressUpdate(int percent);
		void onPostExecute(FlickrPhoto result);
	}

	public static RetainedFragment newInstance() {
		RetainedFragment fragment = new RetainedFragment();
		return fragment;
	}

	public RetainedFragment() {
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			if(activity instanceof OnTaskRunningListener) {
				Log.i(TAG, "OnTaskRunningListener found.");
				mCallback = (OnTaskRunningListener) activity;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// retain this fragment and its data
		// while outer activity is recreating (configuration change, etc...).
		setRetainInstance(true);
	}

	public FlickrPhoto getPhoto() {
		return photo;
	}

	public void setPhoto(FlickrPhoto photo) {
		this.photo = photo;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void loadRandomFlickrPhoto() {
		new LoadRandomFlickrPhotoTask().execute();
	}

	// AsyncTask to load random photo from flickr.
	private class LoadRandomFlickrPhotoTask extends AsyncTask<String, Integer, FlickrPhoto> {

		@Override
		protected void onPreExecute() {
			mCallback.onPreExecute();
			isRunning = true;
		}

		@Override
		protected FlickrPhoto doInBackground(String... params) {
			return FlickrUtil.getRandomFlickrPhoto();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			mCallback.onProgressUpdate(progress[0]);
		}

		@Override
		protected void onPostExecute(FlickrPhoto result) {
			setPhoto(result);
			mCallback.onPostExecute(result);
			isRunning = false;
		}
	}
}

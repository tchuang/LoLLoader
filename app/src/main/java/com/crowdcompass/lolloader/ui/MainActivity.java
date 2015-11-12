package com.crowdcompass.lolloader.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdcompass.lolloader.R;
import com.crowdcompass.lolloader.model.FlickrPhoto;
import com.crowdcompass.lolloader.ui.fragment.RetainedFragment;

public class MainActivity extends FragmentActivity implements RetainedFragment.OnTaskRunningListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final String TAG_RETAINED_FRAGMENT = "retained_fragment";
	private RetainedFragment retainedFragment;

	private ImageView image;
	private TextView title;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lol);

		image = (ImageView) findViewById(R.id.photo_image);
		title = (TextView) findViewById(R.id.photo_title);

		image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// check network status.
				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

				if (networkInfo != null && networkInfo.isConnected()) {
					if (retainedFragment != null) retainedFragment.loadRandomFlickrPhoto();
				}
				else {
					Log.i(TAG, "device is disconnected.");
					Toast.makeText(MainActivity.this, R.string.txt_disconnect_from_network, Toast.LENGTH_LONG).show();
				}
			}
		});

		// Bundle is not designed to carry large objects (such as bitmaps)
		// and the data within it must be serialized then deserialized,
		// which can consume a lot of memory and make the configuration change slow.
		// So here using RetainedFragment to store the data and the running AsyncTask.
		FragmentManager fm = getSupportFragmentManager();
		retainedFragment = (RetainedFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);

		// if retained fragment doesn't exist, create and add it.
		if (retainedFragment == null) {
			//Log.i(TAG, "No existing RetainedFragment, create it.");
			retainedFragment = RetainedFragment.newInstance();
			fm.beginTransaction().add(retainedFragment, TAG_RETAINED_FRAGMENT).commit();
		}
		// if existing, load retained FlickrPhoto object from retainedFragment.
		else {
			//Log.i(TAG, "Found existing RetainedFragment.");
			if (retainedFragment.getPhoto() != null) {
				image.setImageBitmap(retainedFragment.getPhoto().getImage());
				title.setText(retainedFragment.getPhoto().getTitle());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (retainedFragment != null && retainedFragment.isRunning()) showProgressDialog();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (progressDialog != null) progressDialog.dismiss();
	}

	protected void showProgressDialog() {
		progressDialog = ProgressDialog.show(MainActivity.this,
				MainActivity.this.getResources().getString(R.string.updating),
				MainActivity.this.getResources().getString(R.string.please_wait), true, false);
	}

	public RetainedFragment getRetainedFragment() {
		return retainedFragment;
	}

	// implement RetainedFragment.OnTaskRunningListener
	@Override
	public void onPreExecute() {
		showProgressDialog();
	}

	@Override
	public void onProgressUpdate(int percent) {
		// nothing to do here.
	}

	@Override
	public void onPostExecute(FlickrPhoto result) {
		if (result != null) {

			//Log.i(TAG, "url: " + result.getUrl());
			//Log.i(TAG, "title: " + result.getTitle());

			image.setImageBitmap(result.getImage());
			title.setText(result.getTitle());
		}
		else {
			Toast.makeText(MainActivity.this, R.string.txt_load_image_error, Toast.LENGTH_LONG).show();
		}
		if (progressDialog != null) progressDialog.dismiss();
	}
}

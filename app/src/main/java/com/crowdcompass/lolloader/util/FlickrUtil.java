package com.crowdcompass.lolloader.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.crowdcompass.lolloader.model.FlickrPhoto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class FlickrUtil {

	private static final String TAG = FlickrUtil.class.getSimpleName();

	private static final String FLICKR_API_KEY = "864a300242415f12cbed431057496d96";
	private static final String FLICKR_END_POINT = "https://api.flickr.com/services/rest/";
	private static final String FLICKR_TEXT_KEYWORD = "LoLcat";
	//private static final String FLICKR_TEXT_KEYWORD = "rabbit";
	//private static final String FLICKR_TEXT_KEYWORD = "LoLdog";

	private static int flickrPhotoCount = 0;

	public static FlickrPhoto getRandomFlickrPhoto() {

		// init flickrPhotoCount if the value not yet set.
		if (flickrPhotoCount < 1) {
			updatePhotoCount(getPhotoJSONData(1));
		}

		// get a random number for page parameter.
		Random rand = new Random();
		int pageIndex = rand.nextInt(flickrPhotoCount) + 1;
		Log.i(TAG, "random page index: " + pageIndex);

		// retrieve json data with the generated random number.
		JSONObject json = getPhotoJSONData(pageIndex);

		// convert to FlickrPhoto object.
		return buildFlickrPhoto(json);
	}

	// update flickrPhotoCount given a photo json object.
	public static void updatePhotoCount(JSONObject json) {
		try {
			if (json != null) {
				int count = json.getJSONObject("photos").getInt("total");
				// for some reason, flickr has a feature that it return the same image after the counter > 4000.
				flickrPhotoCount = (count > 4000) ? 4000 : count;
				Log.i(TAG, "Update Photo Count: " + flickrPhotoCount);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// retrieve a result JSONObject given a page number index.
	public static JSONObject getPhotoJSONData(int pageIndex) {

		String flickrUrl = FLICKR_END_POINT + "?api_key=" + FLICKR_API_KEY + "&format=json&nojsoncallback=1&method=flickr.photos.search&per_page=1&page=" + pageIndex + "&text=" + FLICKR_TEXT_KEYWORD;

		InputStream is = null;
		JSONObject result = null;

		try {
			//Log.i(TAG, "flickrUrl: " + flickrUrl);

			URL url = new URL(flickrUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.connect();

			int responseCode = conn.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				is = conn.getInputStream();
				String line;
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}

				result = new JSONObject(builder.toString());

				//Log.i(TAG, "result: " + result.toString());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (is != null) is.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	// convert JSONObject to FlickrPhoto.
	public static FlickrPhoto buildFlickrPhoto(JSONObject json) {

		if (json == null) return null;

		FlickrPhoto photo = null;
		InputStream is = null;

		try {
			JSONObject photoJSON = json.getJSONObject("photos").getJSONArray("photo").getJSONObject(0);

			String photoUrl = "https://farm" + photoJSON.getString("farm")
					+ ".staticflickr.com/" + photoJSON.getString("server")
					+ "/" + photoJSON.getString("id") + "_"
					+ photoJSON.getString("secret") + "_m.jpg";

			URL url = new URL(photoUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.connect();

			int responseCode = conn.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {

				photo = new FlickrPhoto();

				is = conn.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(is);

				photo.setImage(bitmap);
				photo.setTitle(photoJSON.getString("title"));
				photo.setUrl(photoUrl);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (is != null) is.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		return photo;
	}
}

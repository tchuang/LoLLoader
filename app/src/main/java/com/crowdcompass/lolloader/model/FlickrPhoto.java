package com.crowdcompass.lolloader.model;

import android.graphics.Bitmap;

public class FlickrPhoto {

	private Bitmap image;
	private String title;
	private String url;

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}

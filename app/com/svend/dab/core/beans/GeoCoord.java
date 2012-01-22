package com.svend.dab.core.beans;

public class GeoCoord {

	private Double latitude;
	private Double longitude;

	public GeoCoord(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public GeoCoord() {
		super();
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

}

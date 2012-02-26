package com.svend.dab.core.beans;

/**
 * @author svend
 * 
 */
public class Location {

	private String location;
	
	// TODO: replace this with GeoCoord
	private String latitude;
	private String longitude;


	public Location() {
		super();
	}

	public Location(Location copied) {
		this.location = copied.getLocation();
		this.latitude = copied.getLatitude();
		this.longitude = copied.getLongitude();
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null || ! ( obj instanceof Location)) {
			return false;
		}
		
		Location  otherLoc = (Location) obj;
		
		if (location == null) {
			return otherLoc.getLocation() == null;
		}
		
		return location.equals(otherLoc.getLocation());
	}

}

package com.svend.dab.core.beans.projects;

import com.svend.dab.core.beans.GeoCoord;

/**
 * 
 * 
 * @author svend
 * 
 */
public class GeographicCircle {

	private GeoCoord center;
	private double radiusInKm;

	public GeographicCircle(GeoCoord center, double radiusInKm) {
		super();
		this.center = center;
		this.radiusInKm = radiusInKm;
	}

	public GeographicCircle() {
		super();
	}

	public GeoCoord getCenter() {
		return center;
	}

	public void setCenter(GeoCoord center) {
		this.center = center;
	}

	public double getRadiusInKm() {
		return radiusInKm;
	}

	public void setRadiusInKm(double radiusInKm) {
		this.radiusInKm = radiusInKm;
	}

	public double getRadiusInDegrees() {
		return radiusInKm / 110;
	}

}

package com.svend.dab.core.beans.projects;

import com.svend.dab.core.beans.Location;

/**
 * 
 * 
 * @author svend
 * 
 */
public class GeographicCircle {

	private Location center;
	private double radiusInKm;

	public GeographicCircle(Location center, double radiusInKm) {
		super();
		this.center = center;
		this.radiusInKm = radiusInKm;
	}

	public GeographicCircle() {
		super();
	}

	public Location getCenter() {
		return center;
	}

	public void setCenter(Location center) {
		this.center = center;
	}

	public double getRadiusInKm() {
		return radiusInKm;
	}

	public void setRadiusInKm(double radiusInKm) {
		this.radiusInKm = radiusInKm;
	}

}

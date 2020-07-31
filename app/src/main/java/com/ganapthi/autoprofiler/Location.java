/**
 * 
 */
package com.ganapthi.autoprofiler;

import java.io.Serializable;

/**
 * @author harshmer
 *
 */
@SuppressWarnings("serial")
public class Location implements Serializable{
	
	private String name;
	private String profile_name;
	private Double longitude;
	private Double latitude;
	
	/*
	 * getters
	 */
	
	public String getName() {
		return name;
	}
	public String getProfile_name() {
		return profile_name;
	}
	public Double getLongitude() {
		return longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	
	/*
	 * setters
	 */
	
	public void setName(String name) {
		this.name = name;
	}
	public void setProfile_name(String profile_name) {
		this.profile_name = profile_name;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

}

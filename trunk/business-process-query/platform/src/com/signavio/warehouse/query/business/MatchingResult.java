package com.signavio.warehouse.query.business;

public class MatchingResult {
	private String comparedProcessID;
	private String comparedTask;
	private boolean zoneWeight;
	private int zone;
	private double matchingValue;
	private boolean improvedWeight;
	private double pivotValue;
	private double scaleValue;

	public double getMatchingValue() {
		return matchingValue;
	}

	public void setMatchingValue(double matchingValue) {
		this.matchingValue = matchingValue;
	}

	public String getComparedTask() {
		return comparedTask;
	}

	public void setComparedTask(String comparedTask) {
		this.comparedTask = comparedTask;
	}

	public String getComparedProcessID() {
		return comparedProcessID;
	}

	public void setComparedProcessID(String comparedProcessID) {
		this.comparedProcessID = comparedProcessID;
	}

	public boolean isZoneWeight() {
		return zoneWeight;
	}

	public void setZoneWeight(boolean zoneWeight) {
		this.zoneWeight = zoneWeight;
	}

	public int getZone() {
		return zone;
	}

	public void setZone(int zone) {
		this.zone = zone;
	}

	public boolean isImprovedWeight() {
		return improvedWeight;
	}

	public void setImprovedWeight(boolean improvedWeight) {
		this.improvedWeight = improvedWeight;
	}

	public double getPivotValue() {
		return pivotValue;
	}

	public void setPivotValue(double pivotValue) {
		this.pivotValue = pivotValue;
	}

	public double getScaleValue() {
		return scaleValue;
	}

	public void setScaleValue(double scaleValue) {
		this.scaleValue = scaleValue;
	}
}

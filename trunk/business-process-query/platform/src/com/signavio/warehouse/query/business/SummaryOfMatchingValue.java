package com.signavio.warehouse.query.business;

public class SummaryOfMatchingValue {
	private int zone;
	private double matchingValue;
	private int noOfLinks;

	public SummaryOfMatchingValue(int zone, double matchingValue, int noOfLinks) {
		super();
		this.zone = zone;
		this.matchingValue = matchingValue;
		this.noOfLinks = noOfLinks;
	}

	public int getZone() {
		return zone;
	}

	public void setZone(int zone) {
		this.zone = zone;
	}

	public double getMatchingValue() {
		return matchingValue;
	}

	public void setMatchingValue(double matchingValue) {
		this.matchingValue = matchingValue;
	}

	public int getNoOfLinks() {
		return noOfLinks;
	}

	public void setNoOfLinks(int noOfLinks) {
		this.noOfLinks = noOfLinks;
	}
}

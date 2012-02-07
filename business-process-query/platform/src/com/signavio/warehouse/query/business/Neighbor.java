package com.signavio.warehouse.query.business;

public class Neighbor {
	private int zone;
	private String fromTask;
	private String toTask;
	private String pattern;
	private int noOfBranches;
	
	public int getZone() {
		return zone;
	}
	public void setZone(int zone) {
		this.zone = zone;
	}
	public String getFromTask() {
		return fromTask;
	}
	public void setFromTask(String fromTask) {
		this.fromTask = fromTask;
	}
	public String getToTask() {
		return toTask;
	}
	public void setToTask(String toTask) {
		this.toTask = toTask;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public int getNoOfBranches() {
		return noOfBranches;
	}
	public void setNoOfBranches(int noOfBranches) {
		this.noOfBranches = noOfBranches;
	}
	
}

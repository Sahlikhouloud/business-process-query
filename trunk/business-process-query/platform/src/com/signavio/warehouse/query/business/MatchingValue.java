package com.signavio.warehouse.query.business;

public class MatchingValue {
	private String taskName1;
	private String taskName2;
	private double value;

	public MatchingValue(String taskName1, String taskName2, double value) {
		super();
		this.taskName1 = taskName1;
		this.taskName2 = taskName2;
		this.value = value;
	}

	public String getTaskName1() {
		return taskName1;
	}

	public void setTaskName1(String taskName1) {
		this.taskName1 = taskName1;
	}

	public String getTaskName2() {
		return taskName2;
	}

	public void setTaskName2(String taskName2) {
		this.taskName2 = taskName2;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}

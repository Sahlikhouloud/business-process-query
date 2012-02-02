package com.signavio.warehouse.query.business;

public enum ActivityType {
	startEvent, sequenceFlow, endEvent, exclusiveGateway, parallelGateway, task, subProcess, inclusiveGateway;
	
	public static boolean contains(String type) {

	    for (ActivityType c : ActivityType.values()) {
	        if (c.name().equals(type)) {
	            return true;
	        }
	    }
	    return false;
	}
}

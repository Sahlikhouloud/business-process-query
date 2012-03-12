package com.signavio.warehouse.query.business;

public enum ActivityType {
	startEvent, intermediateCatchEvent, intermediateThrowEvent, endEvent, 
	sequenceFlow,  
	task, subProcess, 
	exclusiveGateway, parallelGateway, inclusiveGateway, eventBasedGateway, complexGateway;
	
	public static boolean contains(String type) {

	    for (ActivityType c : ActivityType.values()) {
	        if (c.name().equals(type)) {
	            return true;
	        }
	    }
	    return false;
	}
}

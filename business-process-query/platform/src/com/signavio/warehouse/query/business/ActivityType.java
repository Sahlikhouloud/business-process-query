package com.signavio.warehouse.query.business;

public enum ActivityType {
	startEvent, intermediateCatchEvent, intermediateThrowEvent, endEvent, sequenceFlow, task, subProcess, exclusiveGateway, parallelGateway, inclusiveGateway, eventBasedGateway, complexGateway;

	public static boolean contains(String type) {
		if(ActivityType.isAnyKindOfTask(type)){
			return true;
		}
		for (ActivityType c : ActivityType.values()) {
			if (c.name().equals(type)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAnyKindOfTask(String typeString) {
		String lastFour = typeString.substring(typeString.length() - 4,
				typeString.length());
		return lastFour.equalsIgnoreCase(ActivityType.task.toString());
	}
}

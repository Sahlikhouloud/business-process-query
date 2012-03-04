package com.signavio.warehouse.query.util;

public class IConstant {
	public static final double PIVOT = 0.5;
	public static final double SCALE_VALUE = 2.0;

	public static final int LEVENSTEIN = 1;
	public static final int LEVENSTEIN_WITH_ZONEWEIGHT = 2;
	public static final int IMPROVED_WEIGHT = 3;
	public static final int IMPROVED_WEIGHT_WITH_ZONEWEIGHT = 4;
	
	public static final int NO_OF_MAX_ZONE = 5;
	
	public static final String PROCESS_QUERY_ROOT_TREE_DESC = "Queries";
	
	public static String getMethodName(int i){
		String name = "";
		if(i == IConstant.LEVENSTEIN || i == IConstant.LEVENSTEIN_WITH_ZONEWEIGHT ){
			name = "Levenstein";
		}else if(i == IConstant.IMPROVED_WEIGHT || i == IConstant.IMPROVED_WEIGHT_WITH_ZONEWEIGHT){
			name = "Improved weight";
		}
		return name;
	}
}

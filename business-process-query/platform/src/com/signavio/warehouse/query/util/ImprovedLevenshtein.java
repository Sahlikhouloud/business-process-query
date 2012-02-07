package com.signavio.warehouse.query.util;

public class ImprovedLevenshtein {

	// receive pattern having not been encoded yet
	public static double directLinkPatternMatching(String pattern1,
			String pattern2, int noOfBranches1, int noOfBranches2) {

		double result = 0.0;
		if (canApplySimOfGateway(pattern1, pattern2)) {
			result = findSimOfGateway(pattern1, pattern2, noOfBranches1,
					noOfBranches2);
			result = histogramEqualization(result);
		} else {
			result = Levenshtein.directLinkPatternMatching(
					Encoding.encode(pattern1), Encoding.encode(pattern2));
		}

		return result;
	}

	public static boolean canApplySimOfGateway(String pattern1, String pattern2) {
		boolean canApply = false;

		if (isOneSequencialPattern(pattern1)
				&& isOneSequencialPattern(pattern2)) {
			canApply = true;
		}
		return canApply;
	}

	public static boolean isOneSequencialPattern(String pattern) {
		boolean oneSequencial = false;

		String[] atomicPatterns = pattern.split("\\|\\|");
		if (atomicPatterns.length == 2
				&& atomicPatterns[0].split(",")[1].equals(atomicPatterns[1]
						.split(",")[0])) {
			oneSequencial = true;
		}
		return oneSequencial;
	}

	public static double histogramEqualization(double targetValue) {
		if (targetValue == 0.0 || targetValue == 1.0) {
			return targetValue;
		}
		double equalizedValue = 0.0;
		equalizedValue = IConstant.PIVOT
				- ((IConstant.PIVOT - targetValue) / IConstant.SCALE_VALUE);
		return equalizedValue;
	}

	public static double findSimOfGateway(String pattern1, String pattern2,
			int noOfBranches1, int noOfBranches2) {
		double result = 0.0;
		String type1 = findGatewayTypeFromPatternString(pattern1);
		String type2 = findGatewayTypeFromPatternString(pattern2);
		if (type1.equals(type2)) {
			result = 1.0;
		} else {
			if (!type1.equals("seq") && !type2.equals("seq")) {
				String gatewayType1 = type1.split("-")[1];
				String gatewayType2 = type2.split("-")[1];
				String gateway1 = type1.split("-")[0];
				String gateway2 = type2.split("-")[0];
				if (!gatewayType1.equals(gatewayType2)) {
					result = 0.0;
				} else if (gateway1.equals("AND") && gateway2.equals("OR")) {
					result = (1.0 / noOfBranches1)
							* (1.0 / noOfBranches2)
							* (Math.pow(2.0, (noOfBranches2 - 1.0)) / (Math
									.pow(2.0, noOfBranches2) - 1.0));
				} else if (gateway2.equals("AND") && gateway1.equals("OR")) {
					result = (1.0 / noOfBranches2)
							* (1.0 / noOfBranches1)
							* (Math.pow(2.0, (noOfBranches1 - 1.0)) / (Math
									.pow(2.0, noOfBranches1) - 1.0));
				} else if (gateway1.equals("AND") && gateway2.equals("XOR")) {
					result = (1.0 / noOfBranches1)
							* (1.0 / Math.pow(noOfBranches2, 2.0));
				} else if (gateway2.equals("AND") && gateway1.equals("XOR")) {
					result = (1.0 / noOfBranches2)
							* (1.0 / Math.pow(noOfBranches1, 2.0));
				} else if (gateway1.equals("OR") && gateway2.equals("XOR")) {
					result = (1.0 / noOfBranches1)
							* (1.0 / noOfBranches2)
							* (Math.pow(2.0, (noOfBranches1 - 1.0)) / (Math
									.pow(2.0, noOfBranches1) - 1.0))
							* (1.0 / noOfBranches2);
				} else if (gateway2.equals("OR") && gateway1.equals("XOR")) {
					result = (1.0 / noOfBranches2)
							* (1.0 / noOfBranches1)
							* (Math.pow(2.0, (noOfBranches2 - 1.0)) / (Math
									.pow(2.0, noOfBranches2) - 1.0))
							* (1.0 / noOfBranches1);
				}
			} else {
				if (type1.equals("seq")) {
					if (type2.split("-")[0].equals("AND")) {
						result = 1.0 / noOfBranches2;
					} else if (type2.split("-")[0].equals("OR")) {
						result = (1.0 / noOfBranches2)
								* (Math.pow(2, noOfBranches2 - 1.0) / (Math
										.pow(2.0, noOfBranches2) - 1.0));
					} else if (type2.split("-")[0].equals("XOR")) {
						result = 1.0 / (Math.pow(noOfBranches2, 2.0));
					}
				} else {
					if (type1.split("-")[0].equals("AND")) {
						result = 1.0 / noOfBranches1;
					} else if (type1.split("-")[0].equals("OR")) {
						result = (1.0 / noOfBranches1)
								* (Math.pow(2.0, noOfBranches1 - 1.0) / (Math
										.pow(2.0, noOfBranches1) - 1.0));
					} else if (type1.split("-")[0].equals("XOR")) {
						result = 1.0 / (Math.pow(noOfBranches1, 2.0));
					}
				}
			}
		}
		return result;
	}

	public static String findGatewayTypeFromPatternString(String pattern) {
		String[] atomicPatterns = pattern.split("\\|\\|");
		if (atomicPatterns[0].split(",")[0].equals("s")) {
			return atomicPatterns[0].split(",")[1];
		} else {
			return atomicPatterns[0].split(",")[0];
		}
	}
}

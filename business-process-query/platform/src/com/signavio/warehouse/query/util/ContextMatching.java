package com.signavio.warehouse.query.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.signavio.warehouse.query.business.MatchingValue;
import com.signavio.warehouse.query.business.Neighbor;
import com.signavio.warehouse.query.business.SummaryOfMatchingValue;
import com.signavio.warehouse.query.business.Task;

public class ContextMatching {
	public static double execute(Task targetTask, Task comparedTask, int k,
			boolean considerZoneWeight, boolean considerSimOfGateway) {

		List<SummaryOfMatchingValue> sumOfMatchingValues = new ArrayList<SummaryOfMatchingValue>();

		// for first zone
		HashMap<String, Double> matchingMapOfZone1 = new HashMap<String, Double>();
		List<Neighbor> neighborOfTarget1 = targetTask
				.getNeighborsFromSpecificZone(1);
		List<Neighbor> neighborOfCompared1 = comparedTask
				.getNeighborsFromSpecificZone(1);
		if (neighborOfTarget1 != null && neighborOfCompared1 != null) {
			for (Neighbor neighbor1 : neighborOfTarget1) {
				for (int i = 0; i < neighborOfCompared1.size(); i++) {
					Neighbor neighbor2 = neighborOfCompared1.get(i);
					if (neighbor1.getToTask().equals(neighbor2.getToTask())) {
						// common neighbors
						double matchingResult = 0.0;
						if (!considerSimOfGateway) {
							matchingResult = Levenshtein
									.directLinkPatternMatching(Encoding
											.encode(neighbor1.getPattern()),
											Encoding.encode(neighbor2
													.getPattern()));
						} else {
							matchingResult = ImprovedLevenshtein
									.directLinkPatternMatching(
											neighbor1.getPattern(),
											neighbor2.getPattern(),
											neighbor1.getNoOfBranches(),
											neighbor2.getNoOfBranches());
							if(String.valueOf(matchingResult).equals("Infinity")){
								System.out.println("from : "+neighbor1.getFromTask());
								System.out.println("to : "+ neighbor1.getToTask());
								System.out.println("with : "+ neighbor1.getPattern());
								
								System.out.println("--------------------");
								System.out.println("from : "+neighbor2.getFromTask());
								System.out.println("to : "+ neighbor2.getToTask());
								System.out.println("with : "+ neighbor2.getPattern());
							}
						}

						if (matchingMapOfZone1.containsKey(neighbor1
								.getToTask())) {
							double previousValue = matchingMapOfZone1
									.get(neighbor1.getToTask());
							if (previousValue < matchingResult) {
								matchingMapOfZone1.put(neighbor1.getToTask(),
										new Double(matchingResult));
							}
						} else {
							matchingMapOfZone1.put(neighbor1.getToTask(),
									new Double(matchingResult));
						}
					}
				}
			}
			Iterator<Entry<String, Double>> iterator = matchingMapOfZone1
					.entrySet().iterator();
			double summary = 0.0;
			while (iterator.hasNext()) {
				summary += iterator.next().getValue().doubleValue();
			}
			// add matchingValue of zone1
			SummaryOfMatchingValue sumOfMatchingValue = new SummaryOfMatchingValue(
					1, summary, getNoOfLinkFirstZone(neighborOfTarget1));
			sumOfMatchingValues.add(sumOfMatchingValue);
		}

		// for other zone (1<z<=k)
		for (int i = 2; i <= k; i++) {
			List<MatchingValue> matchingValues = new ArrayList<MatchingValue>();
			List<Neighbor> neighborOfTarget = targetTask
					.getNeighborsFromSpecificZone(i);
			List<Neighbor> neighborOfCompared = comparedTask
					.getNeighborsFromSpecificZone(i);
			if (neighborOfCompared != null && neighborOfTarget != null) {
				for (Neighbor neighbor1 : neighborOfTarget) {
					for (int j = 0; j < neighborOfCompared.size(); j++) {
						Neighbor neighbor2 = neighborOfCompared.get(j);
						int samePair = isTheSamePair(neighbor1, neighbor2);
						double matchingResult = 0.0;
						if (samePair == 1) {
							// common neighbors
							if (!considerSimOfGateway) {
								matchingResult = Levenshtein
										.directLinkPatternMatching(
												Encoding.encode(neighbor1
														.getPattern()),
												Encoding.encode(neighbor2
														.getPattern()));
							} else {
								matchingResult = ImprovedLevenshtein
										.directLinkPatternMatching(
												neighbor1.getPattern(),
												neighbor2.getPattern(),
												neighbor1.getNoOfBranches(),
												neighbor2.getNoOfBranches());
							}

						} else if (samePair == 2) {
							// common neighbors with opposite direction
							String pattern = rewardPattern(neighbor2
									.getPattern());
							if (!considerSimOfGateway) {
								matchingResult = Levenshtein
										.directLinkPatternMatching(
												Encoding.encode(neighbor1
														.getPattern()),
												Encoding.encode(pattern));
							} else {
								matchingResult = ImprovedLevenshtein
										.directLinkPatternMatching(
												neighbor1.getPattern(),
												pattern,
												neighbor1.getNoOfBranches(),
												neighbor2.getNoOfBranches());

							}
						}

						if (matchingResult != 0.0) {
							boolean hasPreviousValue = false;
							int index = 0;
							for (int x = 0; x < matchingValues.size(); x++) {
								MatchingValue matchingValue = matchingValues
										.get(x);
								if (isTheSamePair(matchingValue, neighbor1)) {
									hasPreviousValue = true;
									index = x;
									break;
								}
							}

							if (hasPreviousValue) {
								MatchingValue previousValue = matchingValues
										.get(index);
								if (previousValue.getValue() < matchingResult) {
									matchingValues.remove(index);
									MatchingValue matchingValue = new MatchingValue(
											previousValue.getTaskName1(),
											previousValue.getTaskName2(),
											matchingResult);
									matchingValues.add(matchingValue);
								}
							} else {
								MatchingValue matchingValue = new MatchingValue(
										neighbor1.getFromTask(),
										neighbor1.getToTask(), matchingResult);
								matchingValues.add(matchingValue);
							}
						}
					}
				}
				double summary = 0.0;
				for (MatchingValue matchingValue : matchingValues) {
					summary += matchingValue.getValue();
				}
				SummaryOfMatchingValue sumOfMatchingValue = new SummaryOfMatchingValue(
						i, summary, getNoOfLink(neighborOfTarget));
				sumOfMatchingValues.add(sumOfMatchingValue);
				// matchingMap.put(String.valueOf(i), summary);
			}
		}

		double result = 0.0;
		if (!considerZoneWeight) {
			int noOfLinks = 0;
			double sum = 0.0;
			for (SummaryOfMatchingValue sumOfMatchingValue : sumOfMatchingValues) {
				sum += sumOfMatchingValue.getMatchingValue();
				noOfLinks += sumOfMatchingValue.getNoOfLinks();
			}
			result = sum / noOfLinks;
		} else {
			double sum = 0.0;
			for (SummaryOfMatchingValue sumOfMatchingValue : sumOfMatchingValues) {
				double weight = (k - sumOfMatchingValue.getZone() + 1.0) / k;
				sum += weight
						* (sumOfMatchingValue.getMatchingValue() / sumOfMatchingValue
								.getNoOfLinks());
			}
			result = sum * (2.0 / (k + 1.0));
		}
		return result;
	}

	private static int getNoOfLinkFirstZone(List<Neighbor> neighbors) {
		List<String> temp = new ArrayList<String>();
		for (Neighbor neighbor : neighbors) {
			if (!temp.contains(neighbor.getToTask())) {
				temp.add(neighbor.getToTask());
			}
		}
		return temp.size();
	}

	private static int getNoOfLink(List<Neighbor> neighbors) {
		List<Neighbor> temps = new ArrayList<Neighbor>();
		for (Neighbor neighbor : neighbors) {
			boolean exist = false;
			for (int i = 0; i < temps.size(); i++) {
				Neighbor temp = temps.get(i);
				int z = isTheSamePair(neighbor, temp);
				if (z == 1 || z == 2) {
					exist = true;
				}
			}
			if (!exist) {
				temps.add(neighbor);
			}
		}
		return temps.size();
	}

	/*
	 * 1 means the same pair with the same direction, 2 means the same pair with
	 * opposite direction, 3 means not the same
	 */
	public static int isTheSamePair(Neighbor neighbor1, Neighbor neighbor2) {
		int result = 0;
		if (neighbor1.getFromTask().equals(neighbor2.getFromTask())
				&& neighbor1.getToTask().equals(neighbor2.getToTask())) {
			result = 1;
		} else if (neighbor1.getFromTask().equals(neighbor2.getToTask())
				&& neighbor1.getToTask().equals(neighbor2.getFromTask())) {
			result = 2;
		} else {
			result = 3;
		}
		return result;
	}

	private static boolean isTheSamePair(MatchingValue matchingValue,
			Neighbor neighbor1) {
		boolean isTheSamePair = false;
		if ((neighbor1.getFromTask().equals(matchingValue.getTaskName1()) && neighbor1
				.getToTask().equals(matchingValue.getTaskName2()))
				|| (neighbor1.getFromTask()
						.equals(matchingValue.getTaskName2()) && neighbor1
						.getToTask().equals(matchingValue.getTaskName1()))) {
			isTheSamePair = true;
		}
		return isTheSamePair;
	}

	public static String rewardPattern(String pattern) {
		String[] patterns = pattern.split("\\|\\|");
		String result = "";
		for (int i = 0; i < patterns.length; i++) {
			result += "||" + patterns[patterns.length - (i + 1)];
		}
		return result.substring(2);
	}
}

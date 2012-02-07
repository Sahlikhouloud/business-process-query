package com.signavio.warehouse.query.business;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.warehouse.query.util.ContextMatching;
import com.signavio.warehouse.query.util.IConstant;

public class Task {

	private String taskName;
	private List<Neighbor> neighbors = new ArrayList<Neighbor>();
	private List<MatchingResult> matchingResults = new ArrayList<MatchingResult>();

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public List<Neighbor> getNeighbors() {
		return neighbors;
	}

	public void addNeighbors(Neighbor neighbor) {
		this.neighbors.add(neighbor);
	}

	public void setNeighbors(List<Neighbor> neighbors) {
		this.neighbors = neighbors;
	}

	public List<MatchingResult> getMatchingResults() {
		return matchingResults;
	}

	public void addMatchingResults(MatchingResult matchingResult) {
		this.matchingResults.add(matchingResult);
	}

	// Constructor
	public Task(String taskName) {
		this.taskName = taskName;
	}

	public Task(String taskName, String processID, int approach) {
		// this.taskName = taskName;

		// ResultSet rs = null;
		// Connection db;
		// try {
		// db = BaseGateway.getConnection();
		// if (approach == IConstant.LEVENSTEIN) {
		// rs = MatchingResultsGateway.findByTargetService(db, processID,
		// this.taskName, false);
		// } else if (approach == IConstant.LEVENSTEIN_WITH_ZONEWEIGHT) {
		// rs = MatchingResultsGateway.findByTargetService(db, processID,
		// this.taskName, true);
		// } else if (approach == IConstant.IMPROVED_WEIGHT) {
		// rs = MatchingResultsOfImprovedWeightGateway
		// .findByTargetService(db, processID, this.taskName,
		// false);
		// } else if (approach == IConstant.IMPROVED_WEIGHT_WITH_ZONEWEIGHT) {
		// rs = MatchingResultsOfImprovedWeightGateway
		// .findByTargetService(db, processID, this.taskName, true);
		// }
		//
		// while (rs.next()) {
		// MatchingResult result = new MatchingResult();
		// result.setComparedProcessID(rs.getString("compared_processid"));
		// result.setComparedTask(rs.getString("compared_service"));
		// result.setMatchingValue(rs.getDouble("matching_value"));
		// result.setZone(rs.getInt("k_value"));
		// if (approach == IConstant.LEVENSTEIN) {
		// result.setImprovedWeight(false);
		// result.setZoneWeight(false);
		// } else if (approach == IConstant.LEVENSTEIN_WITH_ZONEWEIGHT) {
		// result.setImprovedWeight(false);
		// result.setZoneWeight(true);
		// } else if (approach == IConstant.IMPROVED_WEIGHT) {
		// result.setImprovedWeight(true);
		// result.setPivotValue(rs.getDouble("pivot_value"));
		// result.setScaleValue(rs.getDouble("scale_value"));
		// result.setZoneWeight(false);
		// } else if (approach == IConstant.IMPROVED_WEIGHT_WITH_ZONEWEIGHT) {
		// result.setImprovedWeight(true);
		// result.setPivotValue(rs.getDouble("pivot_value"));
		// result.setScaleValue(rs.getDouble("scale_value"));
		// result.setZoneWeight(true);
		// }
		//
		// this.addMatchingResults(result);
		// }
		//
		// rs.close();
		// db.close();
		// } catch (InstantiationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	// Domain Logic
	public void printTask() {
		System.out.println("taskName : " + this.taskName);
		System.out.println("from\t to\t zone\t noOfBranches\t pattern\t ");
		for (Neighbor neighbor : neighbors) {
			System.out.print(neighbor.getFromTask() + "\t");
			System.out.print(neighbor.getToTask() + "\t");
			System.out.print(neighbor.getZone() + "\t");
			System.out.print(neighbor.getNoOfBranches() + "\t");
			System.out.print(neighbor.getPattern() + "\t");
			System.out.println();
		}

		System.out
				.println("process\t service\t value\t pivot\t scale\t zone\t improvedWeight\t zoneWeight\t ");
		for (MatchingResult result : matchingResults) {
			System.out.print(result.getComparedProcessID() + "\t");
			System.out.print(result.getComparedTask() + "\t");
			System.out.print(result.getMatchingValue() + "\t");
			System.out.print(result.getPivotValue() + "\t");
			System.out.print(result.getScaleValue() + "\t");
			System.out.print(result.getZone() + "\t");
			System.out.print(result.isImprovedWeight() + "\t");
			System.out.print(result.isZoneWeight() + "\t");
			System.out.println();
		}
	}

	public List<Neighbor> getNeighborsFromSpecificZone(int zone) {
		List<Neighbor> neighborList = new ArrayList<Neighbor>();
		for (Neighbor neighbor : this.neighbors) {
			if (neighbor.getZone() == zone) {
				neighborList.add(neighbor);
			}
		}
		return neighborList.size() > 0 ? neighborList : null;
	}

	public int getNoOfZone() {
		int noOfZone = 0;
		for (Neighbor neighbor : neighbors) {
			if (neighbor.getZone() > noOfZone) {
				noOfZone = neighbor.getZone();
			}
		}
		return noOfZone;
	}

	public void insertMatchigResults(String targetProcessID) {
		// Connection db;
		// try {
		// db = BaseGateway.getConnection();
		// for (MatchingResult result : this.matchingResults) {
		// if (!result.isImprovedWeight()) {
		// MatchingResultsGateway.insertNeighbors(db, targetProcessID,
		// this.taskName, result.getZone(),
		// result.getMatchingValue(),
		// result.getComparedTask(), result.isZoneWeight(),
		// result.getComparedProcessID());
		// } else {
		// MatchingResultsOfImprovedWeightGateway.insertNeighbors(db,
		// targetProcessID, this.taskName, result.getZone(),
		// result.getMatchingValue(),
		// result.getComparedTask(), result.isZoneWeight(),
		// result.getComparedProcessID(),
		// result.getPivotValue(), result.getScaleValue());
		// }
		// }
		//
		// db.close();
		// } catch (InstantiationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public List<MatchingResult> getMatchingResults(int zone) {
		List<MatchingResult> tempResults = new ArrayList<MatchingResult>();
		for (MatchingResult result : this.matchingResults) {
			if (result.getZone() == zone) {
				tempResults.add(result);
			}
		}
		return tempResults;
	}

	public List<MatchingResult> getMatchingResults(int zone, int topN) {
		List<MatchingResult> resultsZone = new ArrayList<MatchingResult>();
		resultsZone = this.getMatchingResults(zone);
		Task.bubbleSortDescForMatchingResults(resultsZone);

		List<MatchingResult> topNResults = new ArrayList<MatchingResult>();
		for (int i = 0; resultsZone.size() > i && i < topN; i++) {
			topNResults.add(resultsZone.get(i));
		}
		return topNResults;
	}

	public List<MatchingResult> getMatchingResults(int zone, double threshold) {
		List<MatchingResult> tempResults = new ArrayList<MatchingResult>();
		for (MatchingResult result : this.matchingResults) {
			if (result.getZone() == zone
					&& result.getMatchingValue() >= threshold) {
				tempResults.add(result);
			}
		}
		return tempResults;
	}

	public static void bubbleSortDescForMatchingResults(
			List<MatchingResult> matchingResults) {
		int n = matchingResults.size();
		int i, j;
		for (i = 0; i < n; i++) {
			for (j = 1; j < (n - i); j++) {
				MatchingResult resultPre = matchingResults.get(j - 1);
				MatchingResult resultPost = matchingResults.get(j);
				if (resultPre.getMatchingValue() < resultPost
						.getMatchingValue()) {
					// swap
					matchingResults.remove(j - 1);
					matchingResults.add(j, resultPre);
				}
			}
		}
	}

	public void computeMatchingValue(List<ProcessZone> comparedProcesses,
			int fromZone, int toZone, boolean consideringZoneWeight,
			boolean considerSimOfGateway, String processID) {
		// Zone must be less than the number of maximum layer of both
		// services
		for (int zone = fromZone; zone <= toZone; zone++) {
			if (zone > this.getNoOfZone()) {
				break;
			}
			for (int i = 0; i < comparedProcesses.size(); i++) {
				ProcessZone comparedProcess = comparedProcesses.get(i);
				if (!comparedProcess.getProcessID().equals(processID)) {
					for (int k = 0; k < comparedProcess.getTasks().size(); k++) {
						Task comparedTask = comparedProcess.getTasks().get(k);
						if (zone <= comparedTask.getNoOfZone()
								&& !this.getTaskName().equals(
										comparedTask.getTaskName())) {

							double result = ContextMatching.execute(this,
									comparedTask, zone, consideringZoneWeight,
									considerSimOfGateway);

							if (result > 0.0) {
								MatchingResult resultObject = new MatchingResult();
								resultObject
										.setComparedProcessID(comparedProcess
												.getProcessID());
								resultObject.setComparedTask(comparedTask
										.getTaskName());
								resultObject.setZone(zone);
								resultObject
										.setImprovedWeight(considerSimOfGateway);
								if (considerSimOfGateway) {
									resultObject.setPivotValue(IConstant.PIVOT);
									resultObject
											.setScaleValue(IConstant.SCALE_VALUE);
								}
								resultObject
										.setZoneWeight(consideringZoneWeight);
								resultObject.setMatchingValue(result);

								this.addMatchingResults(resultObject);
							}
						}
					}
				}
			}
		}

		this.insertMatchigResults(processID);
	}

	public JSONArray createJSONRecommendation() {
		JSONArray jsons = new JSONArray();
		try {
			Task.bubbleSortDescForMatchingResults(this.matchingResults);
			for (MatchingResult result : this.matchingResults) {
				JSONObject json = new JSONObject();
				json.put("comparedProcessID", result.getComparedProcessID());
				json.put("comparedTask", result.getComparedTask());
				json.put("zoneWeight", result.isZoneWeight());
				json.put("zone", result.getZone());
				json.put("matchingValue", result.getMatchingValue());
				json.put("improvedWeight", result.isImprovedWeight());
				json.put("pivotValue", result.getPivotValue());
				json.put("scaleValue", result.getScaleValue());
				
				jsons.put(json);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsons;
	}
}

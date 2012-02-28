package com.signavio.warehouse.query.business;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import com.signavio.warehouse.query.gateway.ServiceNeighborsGateway;
import com.signavio.warehouse.query.gateway.util.BaseGateway;
import com.signavio.warehouse.query.util.IConstant;

public class ProcessZone {
	private String processID;
	private List<Task> tasks = new ArrayList<Task>();

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void addTask(Task task) {
		this.tasks.add(task);
	}

	// Constructor (get neighbors for finding matching value)
	public ProcessZone(String processID) {
		this.processID = processID;
		this.initProcessWithNeighborsOfEachTask();
	}

	// Constructor (get matching value for evaluation)
	public ProcessZone(String processID, int approach) {
		this.processID = processID;
		this.initProcessWithMatchingResultsOfEachTask(approach);
	}

	public void initProcessWithMatchingResultsOfEachTask(int approach) {
		// ResultSet rs = null;
		// Connection db;
		// try {
		// db = BaseGateway.getConnection();
		// if (approach == IConstant.LEVENSTEIN) {
		// rs = MatchingResultsGateway.findByTargetProcess(db, processID,
		// false);
		// } else if (approach == IConstant.LEVENSTEIN_WITH_ZONEWEIGHT) {
		// rs = MatchingResultsGateway.findByTargetProcess(db, processID,
		// true);
		// } else if (approach == IConstant.IMPROVED_WEIGHT) {
		// rs = MatchingResultsOfImprovedWeightGateway
		// .findByTargetProcess(db, processID, false);
		// } else if (approach == IConstant.IMPROVED_WEIGHT_WITH_ZONEWEIGHT) {
		// rs = MatchingResultsOfImprovedWeightGateway
		// .findByTargetProcess(db, processID, true);
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
		// String taskName = rs.getString("target_service");
		//
		// boolean alreadyAdded = false;
		// for (Task task : this.tasks) {
		// if (taskName.equals(task.getTaskName())) {
		// task.addMatchingResults(result);
		// alreadyAdded = true;
		// break;
		// }
		// }
		//
		// if (!alreadyAdded) {
		// Task task = new Task(taskName);
		// task.addMatchingResults(result);
		// this.addTask(task);
		// }
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

	public void initProcessWithNeighborsOfEachTask() {
		ResultSet rs = null;
		Connection db;
		try {
			db = BaseGateway.getConnection();
			rs = ServiceNeighborsGateway.findProcess(db, processID);

			while (rs.next()) {
				Neighbor neighbor = new Neighbor();
				neighbor.setFromTask(rs.getString("from_service_name"));
				neighbor.setToTask(rs.getString("to_service_name"));
				neighbor.setZone(rs.getInt("zone"));
				neighbor.setPattern(rs.getString("pattern"));

				neighbor.setNoOfBranches(rs.getInt("no_of_branches"));

				/*
				 * Actually, it should not be here. We should do it since we
				 * transform original data set to be service_neighbors
				 */
				// if (ImprovedLevenshtein.isOneSequencialPattern(neighbor
				// .getPattern())) {
				// neighbor.setNoOfBranches(findNoOfBranches(processID,
				// neighbor.getFromTask(), neighbor.getToTask(),
				// neighbor.getPattern()));
				// }

				String taskName = rs.getString("target_service_name");

				boolean alreadyAdded = false;
				for (Task task : this.tasks) {
					if (taskName.equals(task.getTaskName())) {
						task.addNeighbors(neighbor);
						alreadyAdded = true;
						break;
					}
				}

				if (!alreadyAdded) {
					Task task = new Task(taskName);
					task.addNeighbors(neighbor);
					this.addTask(task);
				}
			}

			rs.close();
			db.close();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Domain Logic
	public static List<String> getAllProcessID() {
		List<String> processIDs = new ArrayList<String>();
		ResultSet rs = null;
		Connection db;

		try {
			db = BaseGateway.getConnection();
			rs = ServiceNeighborsGateway.findAllProcess(db);

			while (rs.next()) {
				processIDs.add(rs.getString("processid"));
			}

			rs.close();
			db.close();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processIDs != null ? processIDs : null;
	}

	public static List<String> getProcessIDLimitNumber(int from, int to) {
		List<String> processIDs = new ArrayList<String>();
		ResultSet rs = null;
		Connection db;

		try {
			db = BaseGateway.getConnection();
			rs = ServiceNeighborsGateway.findProcessIDLimitNumber(db, from, to);

			while (rs.next()) {
				processIDs.add(rs.getString("processid"));
			}

			rs.close();
			db.close();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processIDs != null ? processIDs : null;
	}

	public void printTask(String taskName) {
		System.out.println("ProcessID : " + this.processID);
		for (Task task : this.tasks) {
			if (task.getTaskName().equals(taskName)) {
				task.printTask();
			}
		}
	}

	public Task getTask(String taskName) {
		for (Task task : this.tasks) {
			if (task.getTaskName().equals(taskName)) {
				return task;
			}
		}
		return null;
	}

	public int findNoOfBranches(String processID, String fromService,
			String toService, String pattern) {
		int noOfBranches = 0;
		String[] atomicPatterns = pattern.split("\\|\\|");
		Process process = new Process(processID);
		Activity gateway = null;
		if (atomicPatterns[0].split(",")[0].equals("s")) {
			for (Activity activity : process.getActivities()) {
				if (activity.getType() == ActivityType.sequenceFlow
						&& activity.getSource().getType() == ActivityType.task
						&& activity.getSource().getName().equals(fromService)) {
					gateway = activity.getTarget();
					if (gateway.getType() == ActivityType.task
							&& gateway.getName().equals(toService)) {
						noOfBranches = 1;
					} else {
						for (int i = 1; i < process.getActivities().size(); i++) {
							Activity tempActivity = process.getActivities()
									.get(i);
							if (tempActivity.getType() == ActivityType.sequenceFlow) {
								if (tempActivity.getSource().getId() == gateway
										.getId()
										&& tempActivity.getTarget().getType() == ActivityType.task
										&& tempActivity.getTarget().getName()
												.equals(toService)) {
									int postLink = 0, preLink = 0;
									// count number of post link and pre link
									for (int j = 1; j < process.getActivities()
											.size(); j++) {
										Activity tempActivity1 = process
												.getActivities().get(j);
										if (tempActivity1.getType() == ActivityType.sequenceFlow) {
											if (tempActivity1.getSource()
													.getId() == gateway.getId()) {
												postLink++;
											} else if (tempActivity1
													.getTarget().getId() == gateway
													.getId()) {
												preLink++;
											}
										}
									}

									if (postLink == 1) {
										noOfBranches = preLink;
									} else if (preLink == 1) {
										noOfBranches = postLink;
									}
									break;
								}
							}
						}
					}
				}
			}
		} else if (atomicPatterns[0].split(",")[1].equals("s")) {
			for (Activity activity : process.getActivities()) {
				if (activity.getType() == ActivityType.sequenceFlow
						&& activity.getTarget().getType() == ActivityType.task
						&& activity.getTarget().getName().equals(fromService)) {
					gateway = activity.getSource();
					if (gateway.getType() == ActivityType.task
							&& gateway.getName().equals(toService)) {
						noOfBranches = 1;
					} else {
						for (int i = 1; i < process.getActivities().size(); i++) {
							Activity tempActivity = process.getActivities()
									.get(i);
							if (tempActivity.getType() == ActivityType.sequenceFlow) {
								if (tempActivity.getTarget().getId() == gateway
										.getId()
										&& tempActivity.getSource().getType() == ActivityType.task
										&& tempActivity.getSource().getName()
												.equals(toService)) {
									int postLink = 0, preLink = 0;
									// count number of post link and pre link
									for (int j = 1; j < process.getActivities()
											.size(); j++) {
										Activity tempActivity1 = process
												.getActivities().get(j);
										if (tempActivity1.getType() == ActivityType.sequenceFlow) {
											if (tempActivity1.getSource()
													.getId() == gateway.getId()) {
												postLink++;
											} else if (tempActivity1
													.getTarget().getId() == gateway
													.getId()) {
												preLink++;
											}
										}
									}

									if (postLink == 1) {
										noOfBranches = preLink;
									} else if (preLink == 1) {
										noOfBranches = postLink;
									}
									break;
								}
							}
						}
					}
				}
			}
		}

		return noOfBranches;
	}

	// compute with all processes
	// in process)
	public void computeMatchingValue(int fromZone, int toZone,
			boolean consideringZoneWeight, boolean considerSimOfGateway,
			String targetTaskID) {
		List<String> processIDs = Process.getAllProcessID();
		// get compared processes
		List<ProcessZone> comparedProcesses = new ArrayList<ProcessZone>();

		for (int i = 0; i < processIDs.size(); i++) {
			ProcessZone comparedProcess = new ProcessZone(processIDs.get(i));
			//no include query process
			if(!comparedProcess.isQueryProcess()){
				comparedProcesses.add(comparedProcess);
			}
		}
		this.computeMatchingValue(comparedProcesses, fromZone, toZone,
				consideringZoneWeight, considerSimOfGateway, targetTaskID);
	}

	// (if there is no targetTaskID then compute all tasks)
	public void computeMatchingValue(List<ProcessZone> comparedProcesses,
			int fromZone, int toZone, boolean consideringZoneWeight,
			boolean considerSimOfGateway, String targetTaskID) {

		if (targetTaskID != null && targetTaskID.equals("")) {
			for (int j = 0; j < this.getTasks().size(); j++) {
				Task targetTask = this.getTasks().get(j);
				if (targetTaskID.equals(targetTask.getTaskName())) {
					targetTask.computeMatchingValue(comparedProcesses,
							fromZone, toZone, consideringZoneWeight,
							considerSimOfGateway, this.processID);
				}
			}
		} else {
			for (int j = 0; j < this.getTasks().size(); j++) {
				Task targetTask = this.getTasks().get(j);
				targetTask.computeMatchingValue(comparedProcesses, fromZone,
						toZone, consideringZoneWeight, considerSimOfGateway,
						this.processID);
			}
		}
	}

	// public double getAverageNoOfReccomendedServicesForAllTasks(int zone) {
	// double mean = 0.0;
	// int sum = 0;
	// for (Task task : this.tasks) {
	// List<MatchingResult> results = task.getMatchingResults(zone);
	// sum += results.size();
	// }
	// mean = sum / this.tasks.size();
	// return mean;
	// }
	//
	// public double getAverageNoOfReccomendedServicesForAllTasks(int zone,
	// double threshold) {
	// double mean = 0.0;
	// int sum = 0;
	// for (Task task : this.tasks) {
	// List<MatchingResult> results = task.getMatchingResults(zone,
	// threshold);
	// sum += results.size();
	// }
	// mean = sum / this.tasks.size();
	// return mean;
	// }

	public static double getAverageNoOfRecommendedService(double threshold,
			int approach, int zone, int from, int noOfProcess) {
		List<String> processIDs = ProcessZone.getProcessIDLimitNumber(from,
				noOfProcess);
		double noOfTaskCanBeRecommended = 0.0;
		double noOfRecomenedTask = 0.0;
		double mean = 0.0;
		for (String processID : processIDs) {
			ProcessZone processZone = new ProcessZone(processID, approach);
			for (int i = 0; i < processZone.getTasks().size(); i++) {
				Task task = processZone.getTasks().get(i);
				List<MatchingResult> results = task.getMatchingResults(zone,
						threshold);
				if (results.size() > 0) {
					noOfTaskCanBeRecommended++;
				}
				noOfRecomenedTask += results.size();
			}
		}
		System.out.println("There are " + noOfTaskCanBeRecommended
				+ " target tasks which can be recommended");
		System.out.println("There are " + noOfRecomenedTask
				+ " tasks being recommended");
		mean = noOfRecomenedTask / noOfTaskCanBeRecommended;
		return mean;
	}

	public static HashMap<Integer, Integer> getDistributionNoOfSimilarTasks(
			double threshold, int approach, int zone, int from, int noOfProcess) {
		HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();
		List<String> processIDs = ProcessZone.getProcessIDLimitNumber(from,
				noOfProcess);
		for (String processID : processIDs) {
			ProcessZone processZone = new ProcessZone(processID, approach);
			for (int i = 0; i < processZone.getTasks().size(); i++) {
				Task task = processZone.getTasks().get(i);
				List<MatchingResult> results = task.getMatchingResults(zone,
						threshold);
				if (!count.containsKey(results.size())) {
					count.put(results.size(), 1);
				} else {
					int currentNo = count.get(results.size());
					count.put(results.size(), currentNo + 1);
				}
			}
		}
		return count;
	}

	public static double getAverageMatchingValueOfRecommendedService(int topN,
			int approach, int zone, int from, int noOfProcess) {
		List<String> processIDs = ProcessZone.getProcessIDLimitNumber(from,
				noOfProcess);
		double noOfTaskHavingTopNSimilarService = 0.0;
		double sumOfMatchingValue = 0.0;
		double mean = 0.0;
		for (String processID : processIDs) {
			ProcessZone processZone = new ProcessZone(processID, approach);
			for (int i = 0; i < processZone.getTasks().size(); i++) {
				Task task = processZone.getTasks().get(i);
				List<MatchingResult> results = task.getMatchingResults(zone,
						topN);
				if (results.size() == topN) {
					noOfTaskHavingTopNSimilarService++;
					for (int j = 0; j < topN; j++) {
						double result = results.get(j).getMatchingValue();
						sumOfMatchingValue += result;
					}
				}
			}
		}
		System.out.println("There are " + noOfTaskHavingTopNSimilarService
				+ " target tasks having more than " + topN + " similar tasks");
		mean = sumOfMatchingValue / (topN * noOfTaskHavingTopNSimilarService);
		return mean;
	}

	public int getNoOfTasksHavingZoneMorethanKValue(int kValue) {
		int count = 0;
		for (Task task : this.tasks) {
			if (task.getNoOfZone() >= kValue) {
				count++;
			}
		}
		return count;
	}

	public static boolean isConsideringZoneweight(int approach) {
		boolean consideringZoneWeight;
		if (approach == IConstant.LEVENSTEIN
				|| approach == IConstant.IMPROVED_WEIGHT) {
			consideringZoneWeight = false;
		} else {
			consideringZoneWeight = true;
		}
		return consideringZoneWeight;
	}

	public static boolean isConsideringSimOfGateway(int approach) {
		boolean considerSimOfGateway;
		if (approach == IConstant.LEVENSTEIN
				|| approach == IConstant.LEVENSTEIN_WITH_ZONEWEIGHT) {
			considerSimOfGateway = false;
		} else {
			considerSimOfGateway = true;
		}
		return considerSimOfGateway;
	}
	
	public JSONArray createJSONRecommendation(String taskName){
		JSONArray jsons = null;
		for(Task task : this.tasks){
			if(task.getTaskName().equals(taskName)){
				jsons = task.createJSONRecommendation();
			}
		}
		return jsons;
	}
	
	public boolean isQueryProcess() {
		String[] nameFragments = this.processID.split("\\.");
		if (nameFragments.length > 1 && nameFragments[1].equals("query")) {
			return true;
		} else {
			return false;
		}
	}
}

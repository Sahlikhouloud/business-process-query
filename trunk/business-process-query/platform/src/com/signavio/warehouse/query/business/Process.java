package com.signavio.warehouse.query.business;

//import gateway.AB3CCollectionGateway;
//import gateway.util.BaseGateway;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.signavio.warehouse.query.gateway.AB3CCollectionGateway;
import com.signavio.warehouse.query.gateway.ServiceNeighborsGateway;
import com.signavio.warehouse.query.gateway.util.BaseGateway;
import com.signavio.warehouse.query.util.XMLUtil;

public class Process {

	private String processID;

	// other objects
	private List<Activity> activities = new ArrayList<Activity>();

	// SVG representation
	private String svgRepresentation;
	
	//Json representation
	private String jsonRepresentation;

	public String getJsonRepresentation() {
		return jsonRepresentation;
	}

	public void setJsonRepresentation(String jsonRepresentation) {
		this.jsonRepresentation = jsonRepresentation;
	}

	public String getSvgRepresentation() {
		return svgRepresentation;
	}

	public void setSvgRepresentation(String svgRepresentation) {
		this.svgRepresentation = svgRepresentation;
	}

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}

	public Process(String processID) { // Use JAVA to get source and target
		this.processID = processID;
		// ResultSet rs = null;
		// try {
		//
		// HashMap<String, String> sourceMap = new HashMap<String, String>();
		// HashMap<String, String> targetMap = new HashMap<String, String>();
		//
		// Connection db;
		// db = BaseGateway.getConnection();
		//
		// rs = AB3CCollectionGateway.findProcess(db, processID);
		//
		// while (rs.next()) {
		// Activity activity = new Activity();
		// activity.setId(rs.getString("id"));
		// activity.setType(rs.getString("type"));
		// activity.setName(rs.getString("name"));
		// this.addActivity(activity);
		//
		// if (activity.getType() == ActivityType.sequenceFlow) {
		// sourceMap.put(activity.getId(), rs.getString("sourceref"));
		// targetMap.put(activity.getId(), rs.getString("targetref"));
		// }
		// }
		//
		// rs.close();
		// db.close();
		//
		// for (int i = 0; i < this.activities.size(); i++) {
		// Activity activity = this.activities.get(i);
		// if (activity.getType() == ActivityType.sequenceFlow) {
		// String sourceID = sourceMap.get(activity.getId());
		// String targetID = targetMap.get(activity.getId());
		// for (Activity activity1 : this.activities) {
		// if (activity1.getId().equals(sourceID)) {
		// activity.setSource(activity1);
		// }
		// if (activity1.getId().equals(targetID)) {
		// activity.setTarget(activity1);
		// }
		//
		// if (activity.getSource() != null
		// && activity.getTarget() != null) {
		// break;
		// }
		// }
		// // this.activities.set(i, activity);
		// }
		// }
		//
		// } catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InstantiationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	// Domain Logic
	public void printProcess() {
		System.out.println("ProcessID : " + this.processID);
		System.out
				.println("\t id \t\t\t type \t\t name \t\t source \t\t target ");
		for (Activity activity : this.activities) {
			String sourceID = null;
			String targetID = null;
			if (activity.getSource() != null) {
				sourceID = activity.getSource().getId();
				targetID = activity.getTarget().getId();
			}
			System.out.println("\t " + activity.getId() + "\t"
					+ activity.getType() + "\t" + activity.getName() + "\t"
					+ sourceID + "\t" + targetID);
		}
	}

	public List<Activity> getTaskByName(String taskName) {
		List<Activity> taskList = new ArrayList<Activity>();
		for (Activity activity : this.activities) {
			if ((activity.getType() == ActivityType.task || activity.getType() == ActivityType.subProcess)
					&& activity.getName().equals(taskName)) {
				taskList.add(activity);
			}
		}
		return taskList.size() > 0 ? taskList : null;
	}

	public Activity getActivityById(String activityID) {
		for (Activity activity : this.activities) {
			if (activity.getId().equals(activityID)) {
				return activity;
			}
		}
		return null;
	}

	public void addNeighborServices(int noOfZone, boolean isPersist) {
		for (Activity activity : this.activities) {
			if (activity.getType() == ActivityType.task
					|| activity.getType() == ActivityType.subProcess) {
				this.addNeighborServices(activity.getId(), noOfZone, isPersist);
			}
		}
	}

	private List<String> visitedSequences = new ArrayList<String>();
	/* gatewayIDs is used as stack */
	private List<GatewayPattern> gatewayIDs = new ArrayList<GatewayPattern>();

	public void addNeighborServices(String taskID, int noOfZone,
			boolean isPersist) {
		Activity task = this.getActivityById(taskID);
		if (task != null) {
			for (int i = 1; i <= noOfZone; i++) {
				if (i == 1) {
					do {
						if (this.gatewayIDs.isEmpty()) {// initial
							// push stack
							this.gatewayIDs.add(new GatewayPattern(task, i
									+ "||" + task.getName() + "::"
									+ task.getId(), this
									.findGatewayPattern(task)));
							String pattern = encodePatternRecursive(
									task,
									i + "||" + task.getName() + "::"
											+ task.getId());
							// System.out.println(pattern);
							if (pattern != null) {
								boolean thisPairExists = this
										.isThisPairAlreadyExisted(task, pattern);
								if (!thisPairExists) {
									task.addPatterns(pattern);
								}
							} else {
								// remove from stack
								this.gatewayIDs
										.remove(this.gatewayIDs.size() - 1);
							}
						} else {
							GatewayPattern gateway = this.gatewayIDs
									.get(this.gatewayIDs.size() - 1); // peek
																		// stack

							String pattern = encodePatternRecursive(
									gateway.getGateway(), gateway.getPattern());
							// System.out.println(pattern);
							if (pattern != null) {
								boolean thisPairExists = this
										.isThisPairAlreadyExisted(task, pattern);
								if (!thisPairExists) {
									task.addPatterns(pattern);
								}
							} else {
								// remove from stack
								this.gatewayIDs
										.remove(this.gatewayIDs.size() - 1);
							}
						}
						// print stack
						// for (GatewayPattern gateway : gatewayIDs) {
						// System.out.print(gateway.getGateway().getType() +
						// ", ");
						// }
						// System.out.println();
					} while (!this.gatewayIDs.isEmpty());

					this.visitedSequences.clear();
					this.gatewayIDs.clear();
				} else {// for layer 2, 3, 4, ...
					List<Activity> neighbors = new ArrayList<Activity>();
					for (String taskNeighbor : task.getPatterns()) {
						String[] atomicPatterns = taskNeighbor.split("\\|\\|");
						/*
						 * Need to find further neighbors of the neighbors in
						 * previous zone
						 */
						if (atomicPatterns[0].equals(String.valueOf(i - 1))) {
							Activity neighbor = this
									.getActivityById(atomicPatterns[atomicPatterns.length - 1]
											.split("::")[1]);
							boolean notExist = true;
							for (int j = 0; j < neighbors.size(); j++) {
								Activity temp = neighbors.get(j);
								if (neighbor.getId() == temp.getId()) {
									notExist = false;
									break;
								}
							}
							if (notExist) {
								neighbors.add(neighbor);
								// System.out.println(neighbor.getType() + "("
								// + neighbor.getName() + ")");
							}

						}
					}
					for (Activity neighbor : neighbors) {
						// System.out.println("Do Looping >> "
						// + neighbor.getType() + "(" + neighbor.getName()
						// + ")");
						do {
							if (this.gatewayIDs.isEmpty()) {// initial
								// push stack
								this.gatewayIDs.add(new GatewayPattern(
										neighbor, i + "||" + neighbor.getName()
												+ "::" + neighbor.getId(), this
												.findGatewayPattern(neighbor)));
								String pattern = encodePatternRecursive(
										neighbor, i + "||" + neighbor.getName()
												+ "::" + neighbor.getId());
								// System.out.println(pattern);
								if (pattern != null) {
									boolean thisPairExists = this
											.isThisPairAlreadyExisted(task,
													pattern);
									if (!thisPairExists) {
										task.addPatterns(pattern);
									}
								} else {
									// remove from stack
									this.gatewayIDs.remove(this.gatewayIDs
											.size() - 1);
								}
							} else {
								GatewayPattern gateway = this.gatewayIDs
										.get(this.gatewayIDs.size() - 1); // peek
																			// stack

								String pattern = encodePatternRecursive(
										gateway.getGateway(),
										gateway.getPattern());
								// System.out.println(pattern);
								if (pattern != null) {
									boolean thisPairExists = this
											.isThisPairAlreadyExisted(task,
													pattern);
									if (!thisPairExists) {
										task.addPatterns(pattern);
									}
								} else {
									// remove from stack
									this.gatewayIDs.remove(this.gatewayIDs
											.size() - 1);
								}
							}
							// print stack
							// for (GatewayPattern gateway : gatewayIDs) {
							// System.out.print(gateway.getGateway().getType() +
							// ", ");
							// }
							// System.out.println();
						} while (!this.gatewayIDs.isEmpty());

						this.visitedSequences.clear();
						this.gatewayIDs.clear();
					}
				}
			}

			// set no of branches
			List<String> tempPatterns = new ArrayList<String>();
			for (String pattern : task.getPatterns()) {
				int noOfBranchs = 0;
				if (Process.isOneSequencialPattern(pattern)) {

					String[] atomicPatterns = pattern.split("\\|\\|");
					String fromService = atomicPatterns[1].split("::")[0];
					String toService = atomicPatterns[atomicPatterns.length - 1]
							.split("::")[0];
					String realPattern = Activity.getRealPattern(pattern);
					noOfBranchs = this.findNoOfBranches(fromService, toService,
							realPattern);
				}
				pattern += "||" + noOfBranchs;
				tempPatterns.add(pattern);
			}

			task.clearPatterns();
			for (int i = 0; i < tempPatterns.size(); i++) {
				task.addPatterns(tempPatterns.get(i));
			}

			if (isPersist) {
				task.persistServiceNeighbors(this.processID);
			} else {
				// show results
				System.out.println("-- SHOW NEIGHBORS --");
				for (String pattern : task.getPatterns())
					System.out.println(task.getName() + " : " + pattern);
				System.out.println();
			}
		}
	}

	public static boolean isOneSequencialPattern(String pattern) {
		boolean oneSequencial = false;

		String[] atomicPatterns = pattern.split("\\|\\|");
		if (atomicPatterns.length == 5
				&& atomicPatterns[2].split(",")[1].equals(atomicPatterns[3]
						.split(",")[0])) {
			oneSequencial = true;
		}
		return oneSequencial;
	}

	public boolean isThisPairAlreadyExisted(Activity task, String pattern) {
		boolean thisPairExists = false;
		for (int j = 0; j < task.getPatterns().size(); j++) {
			String taskNeighbor = task.getPatterns().get(j);
			String[] previousAtomicPatterns = taskNeighbor.split("\\|\\|");
			/*
			 * extract patterns that were already found from previous layer
			 */
			String[] currentAtomicPattern = pattern.split("\\|\\|");

			String currentTailID = currentAtomicPattern[currentAtomicPattern.length - 1]
					.split("::")[1];
			String currentHeadID = currentAtomicPattern[1].split("::")[1];
			String preTailID = previousAtomicPatterns[previousAtomicPatterns.length - 1]
					.split("::")[1];
			String preHeadID = previousAtomicPatterns[1].split("::")[1];

			// This pair already existed
			if ((currentHeadID.equals(preTailID) && currentTailID
					.equals(preHeadID))
					|| (currentHeadID.equals(preHeadID) && currentTailID
							.equals(preTailID))) {
				if (isTheSamePattern(currentAtomicPattern,
						previousAtomicPatterns)) {
					thisPairExists = true;
					break;
				}
			}
		}
		return thisPairExists;
	}

	public boolean isTheSamePattern(String[] patterns1, String[] patterns2) {
		boolean isTheSame = true;
		if (patterns1.length != patterns2.length) {
			return false;
		} else {
			String patterns1TailID = patterns1[patterns1.length - 1]
					.split("::")[1];
			String patterns1HeadID = patterns1[1].split("::")[1];
			String patterns2TailID = patterns2[patterns2.length - 1]
					.split("::")[1];
			String patterns2HeadID = patterns2[1].split("::")[1];
			if (patterns1HeadID.equals(patterns2HeadID)
					&& patterns1TailID.equals(patterns2TailID)) {

				for (int i = 0; i < patterns1.length - 3; i++) {
					if (!patterns1[i + 2].equals(patterns2[i + 2])) {
						isTheSame = false;
						break;
					}
				}

			} else if (patterns1HeadID.equals(patterns2TailID)
					&& patterns1TailID.equals(patterns2HeadID)) {
				for (int i = 0; i < patterns1.length - 3; i++) {
					if (!patterns1[i + 2]
							.equals(patterns2[(patterns2.length - 2) - i])) {
						isTheSame = false;
						break;
					}
				}
			} else { // not the same pair
				isTheSame = false;
			}
		}

		return isTheSame;
	}

	private String encodePatternRecursive(Activity activity, String pattern) {

		Activity sequence = getConnectedSequence(activity);
		if (sequence == null) {
			return null;
		}

		if ((sequence.getSource().getType() == ActivityType.startEvent && !sequence
				.getSource().getId().equals(activity.getId()))
				|| ((sequence.getSource().getType() == ActivityType.task || sequence
						.getSource().getType() == ActivityType.subProcess) && !sequence
						.getSource().getId().equals(activity.getId()))) {
			pattern += this.encodePatternForPreSeq(sequence);
			return pattern;
		} else if ((sequence.getTarget().getType() == ActivityType.endEvent && !sequence
				.getTarget().getId().equals(activity.getId()))
				|| ((sequence.getTarget().getType() == ActivityType.task || sequence
						.getTarget().getType() == ActivityType.subProcess) && !sequence
						.getTarget().getId().equals(activity.getId()))) {
			pattern += this.encodePatternForPostSeq(sequence);
			return pattern;
		} else if ((sequence.getSource().getType() == ActivityType.task || sequence
				.getSource().getType() == ActivityType.subProcess)
				&& (sequence.getTarget().getType() == ActivityType.task || sequence
						.getTarget().getType() == ActivityType.subProcess)
				&& sequence.getSource().getId().equals(activity.getId())
				&& sequence.getTarget().getId().equals(activity.getId())) {
			// Self looping
			pattern += this.encodePatternForPostSeq(sequence);
			return pattern;
		} else {
			if (sequence.getTarget().getId().equals(activity.getId())) {
				/*
				 * push stack (Need to push before encodePattern because
				 * encodePattern will find the gateway in this stack)
				 */
				this.gatewayIDs
						.add(new GatewayPattern(sequence.getSource(), pattern,
								this.findGatewayPattern(sequence.getSource())));

				pattern += this.encodePatternForPreSeq(sequence);

				// update pattern
				this.gatewayIDs.get(this.gatewayIDs.size() - 1).setPattern(
						pattern);

				return encodePatternRecursive(sequence.getSource(), pattern);
			} else {
				/*
				 * push stack (Need to push before encodePattern because
				 * encodePattern will find the gateway in this stack)
				 */
				this.gatewayIDs
						.add(new GatewayPattern(sequence.getTarget(), pattern,
								this.findGatewayPattern(sequence.getTarget())));

				pattern += this.encodePatternForPostSeq(sequence);

				// update pattern
				this.gatewayIDs.get(this.gatewayIDs.size() - 1).setPattern(
						pattern);

				return encodePatternRecursive(sequence.getTarget(), pattern);
			}

		}
	}

	private Activity getConnectedSequence(Activity targetActivity) {
		for (Activity activity1 : this.activities) {
			if (activity1.getType() == ActivityType.sequenceFlow) {
				if (activity1.getSource().getId()
						.equals(targetActivity.getId())
						|| activity1.getTarget().getId()
								.equals(targetActivity.getId())) {
					if (!visitedSequences.contains(activity1.getId())) {
						visitedSequences.add(activity1.getId());
						return activity1;
					}
				}
			}
		}
		return null;
	}

	public int findNoOfLinks(Activity activity) {
		int noOfLinks = 0;
		for (Activity activity1 : this.activities) {
			if (activity1.getType() == ActivityType.sequenceFlow
					&& (activity1.getSource().getId().equals(activity.getId()) || activity1
							.getTarget().getId().equals(activity.getId()))) {
				noOfLinks++;
			}
		}
		return noOfLinks;
	}

	public String findGatewayPattern(Activity gateway) {
		String gatewayPattern = "";
		if (gateway.getType() == ActivityType.endEvent) {
			gatewayPattern = "end";
		} else if (gateway.getType() == ActivityType.startEvent) {
			gatewayPattern = "start";
		} else if (gateway.getType() == ActivityType.task
				|| gateway.getType() == ActivityType.subProcess) {
			gatewayPattern = "s";
		} else { // any gateway
			if (gateway.getType() == ActivityType.exclusiveGateway) {
				gatewayPattern = "XOR";
			} else if (gateway.getType() == ActivityType.inclusiveGateway) {
				gatewayPattern = "OR";
			} else if (gateway.getType() == ActivityType.parallelGateway) {
				gatewayPattern = "AND";
			}

			int postLink = 0, preLink = 0;

			// count number of post link and pre link
			for (Activity activity : this.activities) {
				if (activity.getType() == ActivityType.sequenceFlow) {
					if (activity.getSource().getId() == gateway.getId()) {
						postLink++;
					} else if (activity.getTarget().getId() == gateway.getId()) {
						preLink++;
					}
				}
			}

			// identify gateway pattern
			if (postLink == 1 && preLink > 1) {
				gatewayPattern += "-join";
			} else if (preLink == 1 && postLink > 1) {
				gatewayPattern += "-split";
			} else {
				gatewayPattern += "-false";
			}
		}

		return gatewayPattern;
	}

	public String encodePatternForPostSeq(Activity sequence) {
		String pattern = "";
		if (sequence.getType() == ActivityType.sequenceFlow) {
			if ((sequence.getSource().getType() == ActivityType.task || sequence
					.getSource().getType() == ActivityType.subProcess)
					&& (sequence.getTarget().getType() == ActivityType.task || sequence
							.getTarget().getType() == ActivityType.subProcess)) {
				pattern = "||s,seq||seq,s||" + sequence.getTarget().getName()
						+ "::" + sequence.getTarget().getId();
			} else {

				// encode first pattern
				if (sequence.getSource().getType() == ActivityType.exclusiveGateway
						|| sequence.getSource().getType() == ActivityType.inclusiveGateway
						|| sequence.getSource().getType() == ActivityType.parallelGateway) {
					pattern += "||"
							+ this.findStack(sequence.getSource())
									.getGatewayPattern() + ",";
				} else if (sequence.getSource().getType() == ActivityType.task
						|| sequence.getSource().getType() == ActivityType.subProcess) {
					pattern += "||s,";
				} else if (sequence.getSource().getType() == ActivityType.endEvent) {
					pattern += "||end,";
				} else if (sequence.getSource().getType() == ActivityType.startEvent) {
					pattern += "||start,";
				}

				// encode second pattern
				if (sequence.getTarget().getType() == ActivityType.exclusiveGateway
						|| sequence.getTarget().getType() == ActivityType.inclusiveGateway
						|| sequence.getTarget().getType() == ActivityType.parallelGateway) {
					pattern += this.findStack(sequence.getTarget())
							.getGatewayPattern();
				} else if (sequence.getTarget().getType() == ActivityType.task
						|| sequence.getTarget().getType() == ActivityType.subProcess) {
					pattern += "s||" + sequence.getTarget().getName() + "::"
							+ sequence.getTarget().getId();
				} else if (sequence.getTarget().getType() == ActivityType.startEvent) {
					pattern += "start||" + sequence.getTarget().getType()
							+ "::" + sequence.getTarget().getId();
				} else if (sequence.getTarget().getType() == ActivityType.endEvent) {
					pattern += "end||" + sequence.getTarget().getType() + "::"
							+ sequence.getTarget().getId();
				}
			}
		} else {
			return "This is not sequence!!";
		}
		return pattern;
	}

	public String encodePatternForPreSeq(Activity sequence) {
		String pattern = "";
		if (sequence.getType() == ActivityType.sequenceFlow) {
			if ((sequence.getSource().getType() == ActivityType.task || sequence
					.getSource().getType() == ActivityType.subProcess)
					&& (sequence.getTarget().getType() == ActivityType.task || sequence
							.getTarget().getType() == ActivityType.subProcess)) {
				pattern = "||seq,s||s,seq||" + sequence.getSource().getName()
						+ "::" + sequence.getSource().getId();
			} else {

				boolean endPath = false;

				// encode first pattern
				if (sequence.getSource().getType() == ActivityType.exclusiveGateway
						|| sequence.getSource().getType() == ActivityType.inclusiveGateway
						|| sequence.getSource().getType() == ActivityType.parallelGateway) {
					pattern += "||"
							+ this.findStack(sequence.getSource())
									.getGatewayPattern() + ",";
				} else if (sequence.getSource().getType() == ActivityType.task
						|| sequence.getSource().getType() == ActivityType.subProcess) {
					pattern += "||s,";
					endPath = true;
				} else if (sequence.getSource().getType() == ActivityType.startEvent) {
					pattern += "||start,";
					endPath = true;
				} else if (sequence.getSource().getType() == ActivityType.endEvent) {
					pattern += "||end,";
					endPath = true;
				}

				// encode second pattern
				if (sequence.getTarget().getType() == ActivityType.exclusiveGateway
						|| sequence.getTarget().getType() == ActivityType.inclusiveGateway
						|| sequence.getTarget().getType() == ActivityType.parallelGateway) {
					pattern += this.findStack(sequence.getTarget())
							.getGatewayPattern();
				} else if (sequence.getTarget().getType() == ActivityType.task
						|| sequence.getTarget().getType() == ActivityType.subProcess) {
					pattern += "s";
				} else if (sequence.getTarget().getType() == ActivityType.startEvent) {
					pattern += "start";
				} else if (sequence.getTarget().getType() == ActivityType.endEvent) {
					pattern += "end";
				}

				if (endPath) {
					if (sequence.getSource().getType() == ActivityType.task
							|| sequence.getSource().getType() == ActivityType.subProcess) {
						pattern += "||" + sequence.getSource().getName() + "::"
								+ sequence.getSource().getId();
					} else {
						pattern += "||" + sequence.getSource().getType() + "::"
								+ sequence.getSource().getId();
					}

				}
			}
		} else {
			return "This is not sequence!!";
		}
		return pattern;
	}

	private GatewayPattern findStack(Activity activity) {
		for (GatewayPattern gp : this.gatewayIDs) {
			if (gp.getGateway().getId().equals(activity.getId())) {
				return gp;
			}
		}

		return null;
	}

	public static List<String> getAllProcessID() {
		List<String> processIDs = new ArrayList<String>();
		ResultSet rs = null;
		Connection db;

		try {
			db = BaseGateway.getConnection();
			rs = AB3CCollectionGateway.findAllProcess(db);

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

	public int findNoOfBranches(String fromService, String toService,
			String pattern) {
		int noOfBranches = 0;
		String[] atomicPatterns = pattern.split("\\|\\|");
		Activity gateway = null;
		if (atomicPatterns[0].split(",")[0].equals("s")) {
			for (Activity activity : this.getActivities()) {
				if (activity.getType() == ActivityType.sequenceFlow
						&& (activity.getSource().getType() == ActivityType.task || activity
								.getSource().getType() == ActivityType.subProcess)
						&& activity.getSource().getName().equals(fromService)) {
					gateway = activity.getTarget();
					if ((gateway.getType() == ActivityType.task || gateway
							.getType() == ActivityType.subProcess)
							&& gateway.getName().equals(toService)) {
						noOfBranches = 1;
					} else {
						for (int i = 1; i < this.getActivities().size(); i++) {
							Activity tempActivity = this.getActivities().get(i);
							if (tempActivity.getType() == ActivityType.sequenceFlow) {
								if (tempActivity.getSource().getId() == gateway
										.getId()
										&& (tempActivity.getTarget().getType() == ActivityType.task || tempActivity
												.getTarget().getType() == ActivityType.subProcess)
										&& tempActivity.getTarget().getName()
												.equals(toService)) {
									int postLink = 0, preLink = 0;
									// count number of post link and pre link
									for (int j = 1; j < this.getActivities()
											.size(); j++) {
										Activity tempActivity1 = this
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
			for (Activity activity : this.getActivities()) {
				if (activity.getType() == ActivityType.sequenceFlow
						&& (activity.getTarget().getType() == ActivityType.task || activity
								.getTarget().getType() == ActivityType.subProcess)
						&& activity.getTarget().getName().equals(fromService)) {
					gateway = activity.getSource();
					if ((gateway.getType() == ActivityType.task || gateway
							.getType() == ActivityType.subProcess)
							&& gateway.getName().equals(toService)) {
						noOfBranches = 1;
					} else {
						for (int i = 1; i < this.getActivities().size(); i++) {
							Activity tempActivity = this.getActivities().get(i);
							if (tempActivity.getType() == ActivityType.sequenceFlow) {
								if (tempActivity.getTarget().getId() == gateway
										.getId()
										&& (tempActivity.getSource().getType() == ActivityType.task || tempActivity
												.getSource().getType() == ActivityType.subProcess)
										&& tempActivity.getSource().getName()
												.equals(toService)) {
									int postLink = 0, preLink = 0;
									// count number of post link and pre link
									for (int j = 1; j < this.getActivities()
											.size(); j++) {
										Activity tempActivity1 = this
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

	/* For delete duplicate sequence (linking same pair of activity */
	public void deleteDuplicateSequence() {
		// List<String> alreadyCheck = new ArrayList<String>();
		// Connection db;
		// try {
		// db = BaseGateway.getConnection();
		// for (Activity activity : this.activities) {
		// if (activity.getType() == ActivityType.sequenceFlow) {
		// alreadyCheck.add(activity.getId());
		// for (int i = 0; i < this.activities.size(); i++) {
		// Activity dupActiviy = this.activities.get(i);
		// if (dupActiviy.getType() == ActivityType.sequenceFlow
		// && dupActiviy.getId() != activity.getId()
		// && dupActiviy.getSource().getId() == activity
		// .getSource().getId()
		// && dupActiviy.getTarget().getId() == activity
		// .getTarget().getId()
		// && !alreadyCheck.contains(dupActiviy.getId())) {
		// // delete dupActiviy
		// AB3CCollectionGateway.deleteActivity(db,
		// dupActiviy.getId(), this.processID);
		// alreadyCheck.add(dupActiviy.getId());
		// }
		// }
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
		// alreadyCheck = null;
	}

	public void highlightTargetTaskInSVG(String taskName) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(this.svgRepresentation));

			Document doc = dBuilder.parse(is);
			NodeList texts = doc.getElementsByTagName("text");
			for (int i = 0; i < texts.getLength(); i++) {
				Node nText = texts.item(i);
				if (nText.getNodeType() == Node.ELEMENT_NODE) {
					Element eText = (Element) nText;
					if (eText.getTextContent() != null) {
						String word = eText.getTextContent();
						/*
						 * get hole text from many tspan (each tspan represent
						 * one line) and remove space and both names and then
						 * compare!
						 */
						if (word.trim().replaceAll(" ", "")
								.equals(taskName.trim().replaceAll(" ", ""))) {
							eText.setAttribute("font-weight", "bold");
							eText.setAttribute("font-style", "italic");
						}
					}
				}
			}
			this.setSvgRepresentation(XMLUtil.transformNodeToString(doc
					.getDocumentElement()));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setJSONRepresentation(File fXmlFile) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		String jsonTxt = "";
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// normalize text representation
			doc.getDocumentElement().normalize();
			// System.out.println("Root element :"
			// + doc.getDocumentElement().getNodeName());

			// there is only one json-representation tag
			NodeList json = doc.getElementsByTagName("json-representation");
			Node jsonXml = json.item(0);
			if (jsonXml != null && jsonXml.getNodeType() == Node.ELEMENT_NODE) {
				Element eJsonXml = (Element) jsonXml;
				jsonTxt = XMLUtil.getCharacterDataFromElement(eJsonXml);
			}
			// normalize text representation
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setJsonRepresentation(jsonTxt);
	}
	
	public void setSvgRepresentation(File fXmlFile) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		String svgTxt = "";
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// normalize text representation
			doc.getDocumentElement().normalize();
			// System.out.println("Root element :"
			// + doc.getDocumentElement().getNodeName());

			// there is only one svg-representation tag
			NodeList svg = doc.getElementsByTagName("svg-representation");
			Node svgXml = svg.item(0);
			if (svgXml != null && svgXml.getNodeType() == Node.ELEMENT_NODE) {
				Element eSvgXml = (Element) svgXml;
				svgTxt = XMLUtil.getCharacterDataFromElement(eSvgXml);
			}
			// normalize text representation
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setSvgRepresentation(svgTxt);
	}

	public String mapXMLfileIntoModel(File fXmlFile) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// normalize text representation
			doc.getDocumentElement().normalize();
			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());

			// always contain only one process tag
			NodeList processes = doc.getElementsByTagName("process");
			Node processXML = processes.item(0);
			if (processXML != null
					&& processXML.getNodeType() == Node.ELEMENT_NODE) {
				NodeList processDetails = processXML.getChildNodes();
				for (int i = 0; i < processDetails.getLength(); i++) {
					Node nActivity = processDetails.item(i);
					if (nActivity.getNodeType() == Node.ELEMENT_NODE) {
						Element eActivity = (Element) nActivity;
						if (ActivityType.contains(eActivity.getNodeName())
								&& !eActivity.getNodeName().equals(
										ActivityType.sequenceFlow.toString())) {
							Activity activity = new Activity();
							activity.setId(eActivity.getAttribute("id"));
							activity.setType(eActivity.getNodeName());
							activity.setName(eActivity.getAttribute("name"));
							this.addActivity(activity);

							if (activity.getType() == ActivityType.task
									&& (activity.getName() == null || activity
											.getName().equals(""))) {
								return "Cannot share a process containing unnamed task!";
							}
						}
					}
				}
			}

			// adding source and target ref to each sequence
			if (processXML != null
					&& processXML.getNodeType() == Node.ELEMENT_NODE) {
				Element eProcessXML = (Element) processXML;
				NodeList sequences = eProcessXML
						.getElementsByTagName("sequenceFlow");
				System.out.println("length : " + sequences.getLength());
				for (int i = 0; i < sequences.getLength(); i++) {
					Node nActivity = sequences.item(i);
					if (nActivity.getNodeType() == Node.ELEMENT_NODE) {
						Element eActivity = (Element) nActivity;
						if (eActivity.getNodeName().equals(
								ActivityType.sequenceFlow.toString())) {
							Activity activity = new Activity();
							activity.setId(eActivity.getAttribute("id"));
							activity.setType(eActivity.getNodeName());
							activity.setName(eActivity.getAttribute("name"));
							for (Activity tActivity : this.activities) {
								String sourceRef = eActivity
										.getAttribute("sourceRef");
								String targetRef = eActivity
										.getAttribute("targetRef");
								if (tActivity.getId().equals(sourceRef)) {
									activity.setSource(tActivity);
								} else if (tActivity.getId().equals(targetRef)) {
									activity.setTarget(tActivity);
								}
							}
							this.addActivity(activity);
						}
					}
				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public void persist() {
		for (Activity activity : this.activities) {
			activity.persist(this.processID);
		}
	}

	public void deleteByProcessID() {
		Connection db;
		try {
			db = BaseGateway.getConnection();
			AB3CCollectionGateway.deleteProcess(db, this.processID);
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

	public static void deleteByProcessIDStatic(String processID) {
		Connection db;
		try {
			db = BaseGateway.getConnection();
			AB3CCollectionGateway.deleteProcess(db, processID);
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

	public void removeNeighborsService() {
		Connection db;
		try {
			db = BaseGateway.getConnection();
			ServiceNeighborsGateway.deleteNeighbors(db, this.processID);
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

	public static void removeNeighborsServiceStatic(String id) {
		Connection db;
		try {
			db = BaseGateway.getConnection();
			ServiceNeighborsGateway.deleteNeighbors(db, id);
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
}

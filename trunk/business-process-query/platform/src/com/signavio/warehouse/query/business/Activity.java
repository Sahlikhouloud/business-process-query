package com.signavio.warehouse.query.business;

//import gateway.AB3CCollectionGateway;
//import gateway.ServiceNeighborsGateway;
//import gateway.util.BaseGateway;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.signavio.warehouse.query.gateway.AB3CCollectionGateway;
import com.signavio.warehouse.query.gateway.ServiceNeighborsGateway;
import com.signavio.warehouse.query.gateway.util.BaseGateway;

public class Activity {

	private String id;
	private String name;
	private ActivityType type;

	// other objects
	// (for sequence)
	private Activity source;
	private Activity target;

	// (for task)
	private List<String> patterns = new ArrayList<String>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ActivityType getType() {
		return type;
	}

	public List<String> getPatterns() {
		return patterns;
	}

	public void addPatterns(String pattern) {
		this.patterns.add(pattern);
		// System.out.println("ADD : " + pattern);
	}

	public void setType(String typeString) {
		if (typeString.equals(ActivityType.sequenceFlow.toString()))
			this.type = ActivityType.sequenceFlow;
		else if (typeString.equals(ActivityType.endEvent.toString()))
			this.type = ActivityType.endEvent;
		else if (typeString.equals(ActivityType.exclusiveGateway.toString()))
			this.type = ActivityType.exclusiveGateway;
		else if (typeString.equals(ActivityType.inclusiveGateway.toString()))
			this.type = ActivityType.inclusiveGateway;
		else if (typeString.equals(ActivityType.parallelGateway.toString()))
			this.type = ActivityType.parallelGateway;
		else if (typeString.equals(ActivityType.startEvent.toString()))
			this.type = ActivityType.startEvent;
		else if (typeString.equals(ActivityType.subProcess.toString()))
			this.type = ActivityType.subProcess;
		else if (typeString.equals(ActivityType.task.toString()))
			this.type = ActivityType.task;
	}

	public Activity getSource() {
		return source;
	}

	public void setSource(Activity source) {
		this.source = source;
	}

	public Activity getTarget() {
		return target;
	}

	public void setTarget(Activity target) {
		this.target = target;
	}

	// Constructor
	public Activity() {

	}

	public Activity(Connection db, String activityId) {
		// ResultSet rs = null;
		// try {
		//
		// rs = AB3CCollectionGateway.findActivity(db, activityId);
		//
		// if (rs.next()) {
		// this.setId(rs.getString("id"));
		// this.setType(rs.getString("type"));
		// this.setName(rs.getString("name"));
		// }
		// rs.close();
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

	/* for inserting service neighbors */
	public void persistServiceNeighbors(String processID) {
		Connection db;
		try {
			db = BaseGateway.getConnection();

			for (String pattern : this.patterns) {
				if (isPairOfTasks(pattern)) {
					// insert service neighbors
					String[] atomicPatterns = pattern.split("\\|\\|");
					int zone = Integer.parseInt(atomicPatterns[0]);
					String from = atomicPatterns[1].split("::")[0];
					String to = atomicPatterns[atomicPatterns.length - 2]
							.split("::")[0];
					String realPattern = Activity
							.getRealPatternWithNoOfBranches(pattern);
					int noOfBranches = Integer
							.parseInt(atomicPatterns[atomicPatterns.length - 1]);

					// System.out.println(pattern);
					ServiceNeighborsGateway.insertNeighbors(db, processID,
							this.name, zone, from, to, realPattern,
							noOfBranches);
				}
			}

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

	public static String getRealPattern(String pattern) {
		String[] atomicPatterns = pattern.split("\\|\\|");
		String realPattern = "";
		for (int i = 2; i < atomicPatterns.length - 1; i++) {
			realPattern += atomicPatterns[i] + "||";
		}
		realPattern = realPattern.substring(0, realPattern.length() - 2);
		return realPattern;
	}

	public static String getRealPatternWithNoOfBranches(String pattern) {
		String[] atomicPatterns = pattern.split("\\|\\|");
		String realPattern = "";
		for (int i = 2; i < atomicPatterns.length - 2; i++) {
			realPattern += atomicPatterns[i] + "||";
		}
		realPattern = realPattern.substring(0, realPattern.length() - 2);
		return realPattern;
	}

	public boolean isPairOfTasks(String pattern) {
		boolean pairOfTasks = true;
		String[] atomicPatterns = pattern.split("\\|\\|");
		List<String> headAndTailAtomicPatterns = new ArrayList<String>();
		headAndTailAtomicPatterns.add(atomicPatterns[2].split(",")[0]);
		headAndTailAtomicPatterns.add(atomicPatterns[2].split(",")[1]);
		headAndTailAtomicPatterns.add(atomicPatterns[atomicPatterns.length - 3]
				.split(",")[0]);
		headAndTailAtomicPatterns.add(atomicPatterns[atomicPatterns.length - 3]
				.split(",")[1]);

		if (headAndTailAtomicPatterns.contains("start")
				|| headAndTailAtomicPatterns.contains("end")) {
			pairOfTasks = false;
		}
		return pairOfTasks;
	}

	public void clearPatterns() {
		this.patterns.clear();
	}

	public void persist(String processID) {
		Connection db;
		try {
			db = BaseGateway.getConnection();
			AB3CCollectionGateway
					.insert(db, this.id, this.type, this.name,
							this.source != null ? this.source.getId() : null,
							this.target != null ? this.target.getId() : null,
							processID);
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
}

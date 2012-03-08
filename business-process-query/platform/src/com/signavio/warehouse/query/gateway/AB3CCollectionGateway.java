package com.signavio.warehouse.query.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.signavio.warehouse.query.business.ActivityType;

public class AB3CCollectionGateway {
	private static final String findProcessStatement = "SELECT * "
			+ "FROM AB3C_collection f " + "WHERE f.processid = ?";

	public static ResultSet findProcess(Connection db, String processID)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
//		System.out.println(findProcessStatement + " with processID : "
//				+ processID);

		PreparedStatement stmt = db.prepareStatement(findProcessStatement);
		stmt.setString(1, processID);
		ResultSet result = stmt.executeQuery();
		// stmt.close();
		return result;
	}

	private static final String findActivityStatement = "SELECT * "
			+ "FROM AB3C_collection f " + "WHERE f.id = ?";

	public static ResultSet findActivity(Connection db, String activityID)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		System.out.println(findActivityStatement + " with activityID : "
				+ activityID);

		PreparedStatement stmt = db.prepareStatement(findActivityStatement);
		stmt.setString(1, activityID);
		ResultSet result = stmt.executeQuery();
		// stmt.close();
		return result;
	}

	private static final String deleteActivityStatementByID = "DELETE FROM "
			+ "AB3C_collection " + "WHERE processid = ? and id = ?";

	public static boolean deleteActivity(Connection db, String activityID,
			String processID) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		System.out.println(deleteActivityStatementByID + " with activityID : "
				+ activityID + " of process " + processID);

		PreparedStatement stmt = db.prepareStatement(deleteActivityStatementByID);
		stmt.setString(1, processID);
		stmt.setString(2, activityID);
		return stmt.execute();
	}
	
	private static final String deleteActivityStatementByProcessID = "DELETE FROM "
		+ "AB3C_collection " + "WHERE processid = ? ";

	public static boolean deleteProcess(Connection db,
		String processID) throws SQLException, InstantiationException,
		IllegalAccessException, ClassNotFoundException {

		System.out.println(deleteActivityStatementByProcessID + " of process: " + processID);
	
		PreparedStatement stmt = db.prepareStatement(deleteActivityStatementByProcessID);
		stmt.setString(1, processID);
		return stmt.execute();
	}

	private static final String insertStatement = "INSERT INTO AB3C_collection (id, type, name, sourceref, targetref, processid) "
		+ "VALUES (?, ?, ?, ?, ?, ?) ";

	public static boolean insert(Connection db, String id, ActivityType type, String name, String sourceID, String targetID, String processid)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		// System.out.println(insertStatement);
	
		PreparedStatement stmt = db.prepareStatement(insertStatement);
		stmt.setString(1, id);
		stmt.setString(2, type.toString());
		stmt.setString(3, name);
		stmt.setString(4, sourceID);
		stmt.setString(5, targetID);
		stmt.setString(6, processid);
	
		// stmt.close();
		return stmt.execute();
	}
	
	private static final String findAllProcessStatement = "SELECT distinct processid processid FROM AB3C_collection ";

	public static ResultSet findAllProcess(Connection db)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		System.out.println(findAllProcessStatement);

		PreparedStatement stmt = db.prepareStatement(findAllProcessStatement);
		ResultSet result = stmt.executeQuery();
		// stmt.close();
		return result;
	}
	
	private static final String selectAllActivitiesWithNoOfTimes = "SELECT name, count(*) no_of_times " +
						" FROM AB3C_collection " + 
						" WHERE processid not in (select distinct(q.processid) from query_details q) " + 
						" and type in ('task', 'subProcess') " +
						" group by name " +
						" order by no_of_times desc ";
	
	public static ResultSet findAllActivitiesWithNoOfTimes(Connection db) throws SQLException{
		PreparedStatement stmt = db.prepareStatement(selectAllActivitiesWithNoOfTimes);
		ResultSet result = stmt.executeQuery();
		// stmt.close();
		return result;
	}
}

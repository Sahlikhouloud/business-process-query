package com.signavio.warehouse.query.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryDetailsGateway {
	private static final String insertStatement = "INSERT INTO query_details (processid, query_no, target_process, target_task, zone, description, is_initiated) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?) ";

	public static boolean insertNeighbors(Connection db, String processID,
			int queryNo, String targetProcess, String targetTask, int zone,
			String desc) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		// System.out.println(insertStatement);

		PreparedStatement stmt = db.prepareStatement(insertStatement);
		stmt.setString(1, processID);
		stmt.setInt(2, queryNo);
		stmt.setString(3, targetProcess);
		stmt.setString(4, targetTask);
		stmt.setInt(5, zone);
		stmt.setString(6, desc);
		stmt.setBoolean(7, false);

		// stmt.close();
		return stmt.execute();
	}

	private static final String deleteByProcessID = "DELETE FROM "
			+ "query_details " + "WHERE processid = ? ";

	public static boolean deleteQuery(Connection db, String processID)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		System.out.println(deleteByProcessID + " of process: " + processID);

		PreparedStatement stmt = db.prepareStatement(deleteByProcessID);
		stmt.setString(1, processID);
		return stmt.execute();
	}

	private static final String findByID = "SELECT processid, query_no, target_process, target_task, zone, description, is_initiated "
			+ " FROM query_details WHERE processid = ? ";

	public static ResultSet findByID(Connection db, String processID) {
		ResultSet rs = null;
		try {
			PreparedStatement stmt = db.prepareStatement(findByID);
			stmt.setString(1, processID);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	private static final String updateInitStatus = "UPDATE query_details SET is_initiated = ? WHERE processid = ? ";

	public static boolean updateInitStatus(Connection db, boolean isInit,
			String processID) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		System.out.println(updateInitStatus + " of process: " + processID);

		PreparedStatement stmt = db.prepareStatement(updateInitStatus);
		stmt.setBoolean(1, isInit);
		stmt.setString(2, processID);
		return stmt.execute();
	}
	
	private static final String findByTargetProcessID = "SELECT processid, query_no, target_process, target_task, zone, description, is_initiated "
		+ " FROM query_details WHERE target_process = ? ";

	public static ResultSet findByTargetProcessID(Connection db, String processID) {
		ResultSet rs = null;
		try {
			PreparedStatement stmt = db.prepareStatement(findByTargetProcessID);
			stmt.setString(1, processID);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	private static final String updateInitStatusAndDesc = "UPDATE query_details SET is_initiated = ?, description = ? WHERE processid = ? ";

	public static boolean updateInitStatusAndDesc(Connection db, boolean isInit, String desc,
			String processID) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		System.out.println(updateInitStatusAndDesc + " of process: " + processID);

		PreparedStatement stmt = db.prepareStatement(updateInitStatusAndDesc);
		stmt.setBoolean(1, isInit);
		stmt.setString(2, desc);
		stmt.setString(3, processID);
		return stmt.execute();
	}
}

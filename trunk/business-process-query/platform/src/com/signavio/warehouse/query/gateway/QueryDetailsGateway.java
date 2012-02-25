package com.signavio.warehouse.query.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryDetailsGateway {
	private static final String insertStatement = "INSERT INTO query_details (processid, query_no, target_process, target_task, zone, description) "
		+ "VALUES (?, ?, ?, ?, ?, ?) ";

	public static boolean insertNeighbors(Connection db, String processID, int queryNo,
			String targetProcess, String targetTask, int zone, String desc) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		// System.out.println(insertStatement);
	
		PreparedStatement stmt = db.prepareStatement(insertStatement);
		stmt.setString(1, processID);
		stmt.setInt(2, queryNo);
		stmt.setString(3, targetProcess);
		stmt.setString(4, targetTask);
		stmt.setInt(5, zone);
		stmt.setString(6, desc);
	
		// stmt.close();
		return stmt.execute();
	}
	
	private static final String deleteByProcessID = "DELETE FROM "
		+ "query_details " + "WHERE processid = ? ";

	public static boolean deleteQuery(Connection db,
		String processID) throws SQLException, InstantiationException,
		IllegalAccessException, ClassNotFoundException {

		System.out.println(deleteByProcessID + " of process: " + processID);
	
		PreparedStatement stmt = db.prepareStatement(deleteByProcessID);
		stmt.setString(1, processID);
		return stmt.execute();
	}
}

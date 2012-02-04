package com.signavio.warehouse.query.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceNeighborsGateway {
	private static final String insertStatement = "INSERT INTO service_neighbors (processid, target_service_name, zone, from_service_name, to_service_name, pattern, no_of_branches) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?) ";

	public static boolean insertNeighbors(Connection db, String processID,
			String target, int zone, String from, String to, String pattern,
			int noOfBranches) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		// System.out.println(insertStatement);

		PreparedStatement stmt = db.prepareStatement(insertStatement);
		stmt.setString(1, processID);
		stmt.setString(2, target);
		stmt.setInt(3, zone);
		stmt.setString(4, from);
		stmt.setString(5, to);
		stmt.setString(6, pattern);
		stmt.setInt(7, noOfBranches);

		// stmt.close();
		return stmt.execute();
	}

	private static final String findAllProcessStatement = "SELECT distinct processid processid FROM service_neighbors ";

	public static ResultSet findAllProcess(Connection db) throws SQLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		System.out.println(findAllProcessStatement);

		PreparedStatement stmt = db.prepareStatement(findAllProcessStatement);
		ResultSet result = stmt.executeQuery();
		// stmt.close();
		return result;
	}

	private static final String findProcessStatement = "SELECT processid, target_service_name, zone, from_service_name, to_service_name, pattern, no_of_branches "
			+ " FROM service_neighbors " + "WHERE processid = ? ";

	public static ResultSet findProcess(Connection db, String processID)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		// System.out.println(findProcessStatement + " with processID : "
		// + processID);

		PreparedStatement stmt = db.prepareStatement(findProcessStatement);
		stmt.setString(1, processID);
		ResultSet result = stmt.executeQuery();
		// stmt.close();
		return result;
	}

	private static final String findProcessIDStatement = "SELECT distinct processid processid FROM service_neighbors limit ? , ? ";

	public static ResultSet findProcessIDLimitNumber(Connection db, int from,
			int to) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		// System.out.println(findProcessIDStatement);

		PreparedStatement stmt = db.prepareStatement(findProcessIDStatement);
		stmt.setInt(1, from);
		stmt.setInt(2, to);
		ResultSet result = stmt.executeQuery();
		// stmt.close();
		return result;
	}

	private static final String deleteStatement = "DELETE FROM service_neighbors WHERE processid = ? ";

	public static boolean deleteNeighbors(Connection db, String processID)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		System.out.println(deleteStatement + " process : "+ processID);

		PreparedStatement stmt = db.prepareStatement(deleteStatement);
		stmt.setString(1, processID);

		// stmt.close();
		return stmt.execute();
	}

}

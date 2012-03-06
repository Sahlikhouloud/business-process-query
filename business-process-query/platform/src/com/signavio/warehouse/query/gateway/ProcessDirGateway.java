package com.signavio.warehouse.query.gateway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcessDirGateway {
	private static final String insertStatement = "INSERT INTO process_dir (processid, dir) "
			+ "VALUES (?, ?) ";

	public static boolean insert(Connection db, String processID, String dir)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		// System.out.println(insertStatement);

		PreparedStatement stmt = db.prepareStatement(insertStatement);
		int index = 1;
		stmt.setString(index++, processID);
		stmt.setString(index++, dir);

		// stmt.close();
		return stmt.execute();
	}

	private static final String updateStatement = "UPDATE  process_dir SET dir = ?  "
			+ "WHERE processid = ? ";

	public static int update(Connection db, String processID, String dir)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		// System.out.println(insertStatement);

		PreparedStatement stmt = db.prepareStatement(updateStatement);
		int index = 1;
		stmt.setString(index++, dir);
		stmt.setString(index++, processID);

		// stmt.close();
		return stmt.executeUpdate();
	}

	private static final String deleteByProcessID = "DELETE FROM "
			+ "process_dir " + "WHERE processid = ? ";

	public static boolean delete(Connection db, String processID)
			throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {

		System.out.println(deleteByProcessID + " of process: " + processID);

		PreparedStatement stmt = db.prepareStatement(deleteByProcessID);
		stmt.setString(1, processID);
		return stmt.execute();
	}

	private static final String findByID = "SELECT processid, dir "
			+ " FROM process_dir WHERE processid = ? ";

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
}

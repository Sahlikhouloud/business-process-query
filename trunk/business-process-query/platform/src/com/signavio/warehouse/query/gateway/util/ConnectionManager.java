package com.signavio.warehouse.query.gateway.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

public class ConnectionManager {

	public static DataSource ds = null;
	private static GenericObjectPool _pool = null;

	/**
	 * @param config
	 *            configuration from an XML file.
	 */
	public ConnectionManager(Configuration config) {
		try {
			connectToDB(config);
		} catch (Exception e) {
			System.out.println("Failed to construct ConnectionManager" + e);
		}
	}

	/**
	 * destructor
	 */
	protected void finalize() {
		System.out.println("Finalizing ConnectionManager");
		try {
			super.finalize();
		} catch (Throwable ex) {
			System.out
					.println("ConnectionManager finalize failed to disconnect from mysql: "
							+ ex);
		}
	}

	/**
	 * connectToDB - Connect to the MySql DB!
	 */
	private void connectToDB(Configuration config) {

		try {
			java.lang.Class.forName(config.getDbDriverName()).newInstance();
		} catch (Exception e) {
			System.out.println(
					"Error when attempting to obtain DB Driver: "
							+ config.getDbDriverName() + " on "
							+ new Date().toString()+ e);
		}

		System.out.println("Trying to connect to database...");
		try {
			ConnectionManager.ds = setupDataSource(config.getDbURI(),
					config.getDbUser(), config.getDbPassword(),
					config.getDbPoolMinSize(), config.getDbPoolMaxSize());

			System.out.println("Connection attempt to database succeeded.");
		} catch (Exception e) {
			System.out.println("Error when attempting to connect to DB "+ e);
		}
	}

	/**
	 * 
	 * @param connectURI
	 *            - JDBC Connection URI
	 * @param username
	 *            - JDBC Connection username
	 * @param password
	 *            - JDBC Connection password
	 * @param minIdle
	 *            - Minimum number of idel connection in the connection pool
	 * @param maxActive
	 *            - Connection Pool Maximum Capacity (Size)
	 * @throws Exception
	 */
	public static DataSource setupDataSource(String connectURI,
			String username, String password, int minIdle, int maxActive)
			throws Exception {
		//
		// First, we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		GenericObjectPool connectionPool = new GenericObjectPool(null);

		connectionPool.setMinIdle(minIdle);
		connectionPool.setMaxActive(maxActive);

		ConnectionManager._pool = connectionPool;
		// we keep it for two reasons
		// #1 We need it for statistics/debugging
		// #2 PoolingDataSource does not have getPool()
		// method, for some obscure, weird reason.

		//
		// Next, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string from configuration
		//
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				connectURI, username, password);

		//
		// Now we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory, connectionPool, null, null, false, true);

		PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

		return dataSource;
	}

	public static void printDriverStats() throws Exception {
		ObjectPool connectionPool = ConnectionManager._pool;
		System.out.println("NumActive: " + connectionPool.getNumActive());
		System.out.println("NumIdle: " + connectionPool.getNumIdle());
	}

	/**
	 * getNumLockedProcesses - gets the number of currently locked processes on
	 * the MySQL db
	 * 
	 * @return Number of locked processes
	 */
	public int getNumLockedProcesses()
	    {
	        int num_locked_connections = 0;
	        Connection con = null; 
	        PreparedStatement p_stmt = null;  ResultSet rs = null;
	        try
	        {
	            con = ConnectionManager.ds.getConnection();
	            p_stmt = con.prepareStatement("SHOW PROCESSLIST");
	            rs = p_stmt.executeQuery();
	            while(rs.next())
	            {
	                if(rs.getString("State") != 
	                		null && rs.getString("State").equals("Locked"))
	                {
	                    num_locked_connections++;
	                }
	            }
	        }
	        catch(Exception e)
	        {
	        	System.out.println("Failed to get get Locked Connections - Exception: " + e.toString());
	        } finally {
	            try {
	                rs.close();
	                p_stmt.close();
	                con.close();
	            }  catch ( java.sql.SQLException ex) {
	            	System.out.println( ex.toString() );
	            }
	        }
	        return num_locked_connections;
	    }
}

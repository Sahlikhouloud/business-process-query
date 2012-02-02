package com.signavio.warehouse.query.gateway.util;

import java.sql.Connection;
import java.sql.SQLException;

public class BaseGateway {
	public static Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
    {
		Connection db = null;
		if(ConnectionManager.ds == null){
			Configuration config = new Configuration();
			new ConnectionManager(config);
			db = ConnectionManager.ds.getConnection();
		}else{
			db = ConnectionManager.ds.getConnection();
		}
		
//            String userName = "root";
//            String password = "asadmin";
//            String url = "jdbc:mysql://127.0.0.1:3306/composite_ws_dataset";
//            
////            String userName = "nattawat";
////            String password = "nattawat";
////            String url = "jdbc:mysql://157.159.110.224/nattawat_db";
//            
//            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
//            db = DriverManager.getConnection (url, userName, password);
        
        return db;
    }
}

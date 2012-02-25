package com.signavio.warehouse.query.business;

import java.sql.Connection;
import java.sql.SQLException;

import com.signavio.warehouse.query.gateway.QueryDetailsGateway;
import com.signavio.warehouse.query.gateway.util.BaseGateway;

public class ProcessQuery extends Process{

	private String targetProcess;
	private String targetTask;
	private int zone;
	private String queryDesc;
	private int queryNo;
	
	public ProcessQuery(String processID, String targetProcess, String targetTask, int zone, String queryDesc) {
		super(processID);
		this.targetProcess = targetProcess;
		this.targetTask = targetTask;
		this.zone = zone;
		this.queryDesc = queryDesc;
		this.extractQueryNo();
		// TODO Auto-generated constructor stub
	}

	
	public int getQueryNo() {
		return queryNo;
	}


	public void setQueryNo(int queryNo) {
		this.queryNo = queryNo;
	}


	public String getTargetProcess() {
		return targetProcess;
	}

	public void setTargetProcess(String targetProcess) {
		this.targetProcess = targetProcess;
	}

	public String getTargetTask() {
		return targetTask;
	}

	public void setTargetTask(String targetTask) {
		this.targetTask = targetTask;
	}

	public int getZone() {
		return zone;
	}

	public void setZone(int zone) {
		this.zone = zone;
	}

	public String getQueryDesc() {
		return queryDesc;
	}

	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}
	
	// Domain logic 
	
	public void saveQuery(){
		Connection db;
		try {
			db = BaseGateway.getConnection();
			QueryDetailsGateway.insertNeighbors(db, this.getProcessID(), this.queryNo, this.targetProcess, this.targetTask, this.zone, this.queryDesc);
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
	
	private void extractQueryNo(){
		if(this.getProcessID()!=null && !this.getProcessID().equals("")){
			String [] nameFragments = this.getProcessID().split("\\.");
			this.queryNo = Integer.parseInt(nameFragments[nameFragments.length-1]);
		}
	}
	
	public static String getTargetFromQueryID(String queryID){
		String [] nameFragments = queryID.split("\\.");
		return nameFragments[0];
	}
	
	public static void deleteByProcessIDStatic(String processID) {
		Connection db;
		try {
			db = BaseGateway.getConnection();
			QueryDetailsGateway.deleteQuery(db, processID);
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

package com.signavio.warehouse.query.business;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.warehouse.query.gateway.QueryDetailsGateway;
import com.signavio.warehouse.query.gateway.util.BaseGateway;
import com.signavio.warehouse.query.util.IConstant;

public class ProcessQuery extends Process{

	private String targetProcess;
	private String targetTask;
	private int zone;
	private String queryDesc;
	private int queryNo;
	private boolean isInitiated;
	
	public ProcessQuery(String processID, String targetProcess, String targetTask, int zone, String queryDesc) {
		super(processID);
		this.targetProcess = targetProcess;
		this.targetTask = targetTask;
		this.zone = zone;
		this.queryDesc = queryDesc;
		this.extractQueryNo();
		// TODO Auto-generated constructor stub
	}
	
	public ProcessQuery(){
		
	}
	
	public boolean isInitiated() {
		return isInitiated;
	}


	public void setInitiated(boolean isInitiated) {
		this.isInitiated = isInitiated;
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
	
	public static ProcessQuery getProcessQuery(String processID){
		Connection db;
		ProcessQuery query = null;
		try {
			db = BaseGateway.getConnection();
			ResultSet rs = QueryDetailsGateway.findByID(db, processID);
			if(rs.next()){
				query = new ProcessQuery();
				query.setProcessID(processID);
				query.setTargetProcess(rs.getString("target_process"));
				query.setQueryNo(rs.getInt("query_no"));
				query.setTargetTask(rs.getString("target_task"));
				query.setZone(rs.getInt("zone"));
				query.setQueryDesc(rs.getString("description"));
				query.setInitiated(rs.getBoolean("is_initiated"));
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
		return query;
	}
	
	public void finishInitQuery(){
		Connection db;
		try {
			db = BaseGateway.getConnection();
			QueryDetailsGateway.updateInitStatus(db, true, this.getProcessID());
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
	
	public void updateDetails(){
		Connection db;
		try {
			db = BaseGateway.getConnection();
			QueryDetailsGateway.updateInitStatusAndDesc(db, this.isInitiated, this.queryDesc, this.getProcessID());
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
	
	public JSONObject exportJSON(){
		JSONObject queryJSON = new JSONObject();
		try {
			queryJSON.put("processID", this.getProcessID());
			queryJSON.put("targetTask", this.getTargetTask());
			queryJSON.put("targetProcess", this.getTargetProcess());
			queryJSON.put("zone", this.getZone());
			queryJSON.put("desc", this.getQueryDesc());
			queryJSON.put("queryNo", this.getQueryNo());
			queryJSON.put("isInitiated", this.isInitiated());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryJSON;
	}
	
	public static List <ProcessQuery> getQueriesOfTargetProcess(String processID){
		Connection db;
		List<ProcessQuery> queries = new ArrayList<ProcessQuery>();
		try {
			db = BaseGateway.getConnection();
			ResultSet rs = QueryDetailsGateway.findByTargetProcessID(db, processID);
			while(rs.next()){
				ProcessQuery query = new ProcessQuery();
				query.setProcessID(rs.getString("processid"));
				query.setTargetProcess(rs.getString("target_process"));
				query.setQueryNo(rs.getInt("query_no"));
				query.setTargetTask(rs.getString("target_task"));
				query.setZone(rs.getInt("zone"));
				query.setQueryDesc(rs.getString("description"));
				query.setInitiated(rs.getBoolean("is_initiated"));
				queries.add(query);
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
		return queries;
	}
	
	public static JSONArray exportAllQueriesJSON(List<ProcessQuery> queries){
		JSONArray queriesJSON = new JSONArray();
		try {
			for(ProcessQuery query : queries){
				if(query.isInitiated()){
					JSONObject queryJSON = new JSONObject();
					queryJSON.put("processID", query.getProcessID());
					queryJSON.put("text", query.getProcessID());
					queryJSON.put("leaf", true);
					queryJSON.put("targetTask", query.getTargetTask());
					queryJSON.put("targetProcess", query.getTargetProcess());
					queryJSON.put("zone", query.getZone());
					queryJSON.put("desc", query.getQueryDesc());
					queryJSON.put("queryNo", query.getQueryNo());
					queryJSON.put("isInitiated", query.isInitiated());
					queriesJSON.put(queryJSON);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queriesJSON;
	}
	
	public static JSONObject exportAllQueriesJSONWithRoot(List<ProcessQuery> queries){
		JSONObject root = new JSONObject();
		JSONArray queriesJSON = new JSONArray();
		try {
			root.put("text", IConstant.PROCESS_QUERY_ROOT_TREE_DESC);
			root.put("leaf", false);
			
			for(ProcessQuery query : queries){
				if(query.isInitiated()){
					JSONObject queryJSON = new JSONObject();
					queryJSON.put("processID", query.getProcessID());
					queryJSON.put("text", query.getProcessID());
					queryJSON.put("leaf", true);
					queryJSON.put("targetTask", query.getTargetTask());
					queryJSON.put("targetProcess", query.getTargetProcess());
					queryJSON.put("zone", query.getZone());
					queryJSON.put("desc", query.getQueryDesc());
					queryJSON.put("queryNo", query.getQueryNo());
					queryJSON.put("isInitiated", query.isInitiated());
					queriesJSON.put(queryJSON);
				}
			}
			root.put("children", queriesJSON);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}
	
	public static void bubbleSortDescByNoOfQuery(
			List<ProcessQuery> queries) {
		int n = queries.size();
		int i, j;
		for (i = 0; i < n; i++) {
			for (j = 1; j < (n - i); j++) {
				ProcessQuery resultPre = queries.get(j - 1);
				ProcessQuery resultPost = queries.get(j);
				if (resultPre.getQueryNo() < resultPost
						.getQueryNo()) {
					// swap
					queries.remove(j - 1);
					queries.add(j, resultPre);
				}
			}
		}
	}
	
	public static void bubbleSortAscByNoOfQuery(
			List<ProcessQuery> queries) {
		int n = queries.size();
		int i, j;
		for (i = 0; i < n; i++) {
			for (j = 1; j < (n - i); j++) {
				ProcessQuery resultPre = queries.get(j - 1);
				ProcessQuery resultPost = queries.get(j);
				if (resultPre.getQueryNo() > resultPost
						.getQueryNo()) {
					// swap
					queries.remove(j - 1);
					queries.add(j, resultPre);
				}
			}
		}
	}
}



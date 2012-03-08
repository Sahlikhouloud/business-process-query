package com.signavio.warehouse.query.handler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.handler.BasisHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.query.business.Process;
import com.signavio.warehouse.query.business.ProcessQuery;
import com.signavio.warehouse.query.business.ProcessZone;
import com.signavio.warehouse.query.util.FileUtil;
import com.signavio.warehouse.query.util.IConstant;

@HandlerConfiguration(uri = "/query", rel = "que")
public class QueryHandler extends BasisHandler {

	public QueryHandler(ServletContext servletContext) {
		super(servletContext);
	}

	/**
	 * Overwrite
	 */
	public <T extends FsSecureBusinessObject> void doGet(
			HttpServletRequest req, HttpServletResponse res,
			FsAccessToken token, T sbo) {
		System.out.println("QueryHandler... doGet ");
		JSONObject jParams = (JSONObject) req.getAttribute("params");
		String jobDesc = "";
		try {
			jobDesc = jParams.getString("id");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if (jobDesc.equals("getRecommendation")) {
			this.getRecommendation(jParams, res);
		} else if (jobDesc.equals("getMaxZone")) {
			this.getMaxZone(jParams, res);
		} else if (jobDesc.equals("getSVG")) {
			this.getSVG(jParams, res);
		} else if (jobDesc.equals("getJSON")) {
			this.getJSON(jParams, res, token);
		} else if (jobDesc.equals("getInteractiveSVG")) {
			this.getInteractiveSVG(jParams, res);
		} else if (jobDesc.equals("getNoOfQuery")) {
			this.getNoOfQuery(jParams, res);
		} else if (jobDesc.equals("getInitQuery")) {
			this.getInitQuery(jParams, res);
		} else if(jobDesc.equals("getAllQueries")){
			this.getAllQueries(jParams, res);
		} else if(jobDesc.equals("getAllTasks")){
			this.getAllTasks(jParams, res);
		}
	}
	
	private void getAllTasks(JSONObject jParams, HttpServletResponse res){
		try {
			JSONObject taskJSON = Process.getAlltasksWithNoOfTimeTheyAreUsed();
			res.getWriter().write(taskJSON.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getAllQueries(JSONObject jParams, HttpServletResponse res){
		try {
			String processID = jParams.getString("processID");
			List<ProcessQuery> queries = ProcessQuery.getQueriesOfTargetProcess(processID);
			ProcessQuery.bubbleSortAscByNoOfQuery(queries);
			JSONArray queriesJSON = ProcessQuery.exportAllQueriesJSON(queries);
			res.getWriter().write(queriesJSON.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getInitQuery(JSONObject jParams, HttpServletResponse res) {
		try {
			String processID = jParams.getString("processID");
			ProcessQuery query = ProcessQuery.getProcessQuery(processID);
			JSONObject queryJSON = query.exportJSON();
			res.getWriter().write(queryJSON.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getNoOfQuery(JSONObject jParams, HttpServletResponse res) {
		try {
			String processID = jParams.getString("processID");
			
			Process process = new Process(processID);
			File[] files = FileUtil.getFilesInDir(process.getDirFromDB());
			process.setNoOfQuery(files);
			res.getWriter().write(process.getNoOfQuery() + "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getInteractiveSVG(JSONObject jParams, HttpServletResponse res) {
		try {
			String processID = jParams.getString("processID");
			String taskName = jParams.getString("task");

			Process process = new Process(processID);
			File fXmlFile = FileUtil.openSignavioFile(process.getDirFromDB(), processID);
			process.setSvgRepresentation(fXmlFile);
			process.highlightTargetTaskInSVG(taskName);
			process.createInteractiveSvgRepresentation();
			String svgRepresentation = process.getSvgRepresentation();
			res.getWriter().write(svgRepresentation);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getSVG(JSONObject jParams, HttpServletResponse res) {
		try {
			String processID = jParams.getString("processID");
			String taskName = jParams.getString("task");
			
			Process process = new Process(processID);
			File fXmlFile = FileUtil.openSignavioFile(process.getDirFromDB(), processID);
			process.setSvgRepresentation(fXmlFile);
			process.highlightTargetTaskInSVG(taskName);
			String svgRepresentation = process.getSvgRepresentation();
			res.getWriter().write(svgRepresentation);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getJSON(JSONObject jParams, HttpServletResponse res,
			FsAccessToken token) {
		try {
			String processID = jParams.getString("processID");
			// String taskName = jParams.getString("task");

			Process process = new Process(processID);
			File fXmlFile = FileUtil.openSignavioFile(process.getDirFromDB(), processID);
			process.setJSONRepresentation(fXmlFile);
			String jsonRepresentation = process.getJsonRepresentation();
			res.getWriter().write(jsonRepresentation);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getMaxZone(JSONObject jParams, HttpServletResponse res) {
		try {
			JSONArray jsons = new JSONArray();
			String processID = jParams.getString("processID");
			String taskName = jParams.getString("task");
			ProcessZone process = new ProcessZone(processID);
			int zone = process.getTask(taskName).getNoOfZone();
			for (int i = 1; i <= zone; i++) {
				JSONObject json = new JSONObject();
				json.put("myId", i);
				json.put("myText", i);
				jsons.put(json);
			}
			JSONObject root = new JSONObject();
			root.put("zone", jsons);
			res.getWriter().write(root.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getRecommendation(JSONObject jParams, HttpServletResponse res) {
		try {
			JSONObject json = new JSONObject();
			JSONArray resultJSON = new JSONArray();
			String processID = jParams.getString("processID");
			String taskName = jParams.getString("task");
			int zone = jParams.getInt("zone");
			int method = jParams.getInt("method");

			ProcessZone targetProcess = new ProcessZone(processID);
			boolean consideringZoneWeight = ProcessZone
					.isConsideringZoneweight(method);
			boolean considerSimOfGateway = ProcessZone
					.isConsideringSimOfGateway(method);

			targetProcess.computeMatchingValue(zone, zone,
					consideringZoneWeight, considerSimOfGateway, taskName);
			resultJSON = targetProcess.createJSONRecommendation(taskName);
			json.put("task", taskName);
			json.put("processID", processID);
			json.put("zone", zone);
			json.put("method", IConstant.getMethodName(method));
			json.put("results", resultJSON);
			res.getWriter().write(json.toString());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Overwrite
	 */
	public <T extends FsSecureBusinessObject> void doPost(
			HttpServletRequest req, HttpServletResponse res,
			FsAccessToken token, T sbo) {
		System.out.println("QueryHandler... doPost ");
		// Get the parameter list
		JSONObject jParams = (JSONObject) req.getAttribute("params");
		String jobDesc = "";
		try {
			jobDesc = jParams.getString("jobId");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if (jobDesc.equals("saveProcess")) {
			this.newProcess(jParams, res, token);
		} else if (jobDesc.equals("newQuery")) {
			this.newQuery(jParams, token);
		} else if (jobDesc.equals("copyQuery")){
			this.copyQuery(jParams, token);
		}
	}

	private void copyQuery(JSONObject jParams, FsAccessToken token) {
		try {
			String processID = jParams.getString("copyFrom");
			String name = jParams.getString("name");
			String desc = jParams.getString("description"); 
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");
			ProcessQuery query = ProcessQuery.getProcessQuery(processID);
			ProcessQuery newQuery = new ProcessQuery(name, query.getTargetProcess(), query.getTargetTask(), query.getZone(), desc);
			newQuery.saveQuery();
			String dir = FileUtil.getDirectory(parentId, token);
			Process process = new Process(name);
			process.setDirectory(dir);
			process.persistDir();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void newQuery(JSONObject jParams, FsAccessToken token) {
		try {
			String processID = jParams.getString("processID");
			int zone = jParams.getInt("zone");
			String targetProcess = jParams.getString("targetProcess");
			String targetTask = jParams.getString("targetTask");
			String desc = jParams.getString("queryDesc");
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");
			ProcessQuery query = new ProcessQuery(processID, targetProcess,
					targetTask, zone, desc);
			query.saveQuery();
			String dir = FileUtil.getDirectory(parentId, token);
			Process process = new Process(processID);
			process.setDirectory(dir);
			process.persistDir();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void newProcess(JSONObject jParams, HttpServletResponse res,
			FsAccessToken token) {
		try {
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");
			String name = jParams.getString("name");

			File fXmlFile = FileUtil.openBpmn20File(parentId, token, name);

			Process process = new Process(name);
			String exception = process.mapXMLfileIntoModel(fXmlFile);
			String dir = FileUtil.getDirectory(parentId, token);
			process.setDirectory(dir);
			if (!exception.equals("") && exception != null) {
				res.getWriter().write(exception);
			} else {
				process.persist();
				process.persistDir();
				process.addNeighborServices(IConstant.NO_OF_MAX_ZONE, true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Overwrite
	 */
	public <T extends FsSecureBusinessObject> void doPut(
			HttpServletRequest req, HttpServletResponse res,
			FsAccessToken token, T sbo) {
		System.out.println("QueryHandler... doPut ");
		// Get the parameter list
		JSONObject jParams = (JSONObject) req.getAttribute("params");
		String jobDesc = "";
		try {
			jobDesc = jParams.getString("jobId");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if (jobDesc.equals("saveProcess")) {
			this.updateProcess(jParams, res, token);
		}
	}

	private void updateProcess(JSONObject jParams, HttpServletResponse res,
			FsAccessToken token) {
		try {
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");
			String name = jParams.getString("name");
			String desc = jParams.getString("description");

			File fXmlFile = FileUtil.openBpmn20File(parentId, token, name);

			Process process = new Process(name);
			String dir = FileUtil.getDirectory(parentId, token);
			String exception = process.mapXMLfileIntoModel(fXmlFile);
			if (!exception.equals("") && exception != null) {
				res.getWriter().write(exception);
			} else {
				process.setDirectory(dir);
				//for query process update init Status every time it is saved
				if(process.isQueryProcess()){
					if (jParams.has("id")) {
						String id = jParams.getString("id");
						boolean isNewProcess = Process.deletePreviousQueryProcess(id);
						if (isNewProcess) {
							process.deleteByProcessID();
							process.removeNeighborsService();
						}
					}
					ProcessQuery query = ProcessQuery.getProcessQuery(name);
					query.setQueryDesc(desc);
					query.setInitiated(true);
					query.updateDetails();
				}else { // for ordinary process
					if (jParams.has("id")) {
						String id = jParams.getString("id");
						boolean isNewProcess = Process.deletePreviousProcess(id);
						if (isNewProcess) {
							process.deleteByProcessID();
							process.removeNeighborsService();
						}
					}
				}
				process.persist();
				process.updateDir();
				process.addNeighborServices(IConstant.NO_OF_MAX_ZONE, true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Overwrite
	 */
	public <T extends FsSecureBusinessObject> void doDelete(
			HttpServletRequest req, HttpServletResponse res,
			FsAccessToken token, T sbo) {
		System.out.println("QueryHandler... doDelete ");
		// Get the parameter list
		JSONObject jParams = (JSONObject) req.getAttribute("params");
		String jobDesc = "";
		try {
			jobDesc = jParams.getString("id");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if (jobDesc.equals("deleteQuery")) {
			this.deleteQuery(jParams, token);
		}
	}
	
	private void deleteQuery(JSONObject jParams,
			FsAccessToken token) {
		try {
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");
			String name = jParams.getString("processID");

			File fXmlFile = FileUtil.openBpmn20File(parentId, token, name);
			boolean deleteBpmn = fXmlFile.delete();
			File fXmlFile1 = FileUtil.openSignavioFile(parentId, token, name);
			boolean deleteSig = fXmlFile1.delete();
			if(deleteBpmn && deleteSig){
				Process.deleteByProcessIDStatic(name);
				Process.removeNeighborsServiceStatic(name);
				Process.deleteDirByProcessIDStatic(name);
				ProcessQuery.deleteByProcessIDStatic(name);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package com.signavio.warehouse.query.handler;

import java.io.File;
import java.io.IOException;

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
			this.getSVG(jParams, res, token);
		} else if (jobDesc.equals("getJSON")) {
			this.getJSON(jParams, res, token);
		} else if (jobDesc.equals("getInteractiveSVG")) {
			this.getInteractiveSVG(jParams, res, token);
		} else if (jobDesc.equals("getNoOfQuery")) {
			this.getNoOfQuery(jParams, res, token);
		} else if (jobDesc.equals("getInitQuery")) {
			this.getInitQuery(jParams, res);
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

	private void getNoOfQuery(JSONObject jParams, HttpServletResponse res,
			FsAccessToken token) {
		try {
			String processID = jParams.getString("processID");
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");
			File[] files = FileUtil.getFilesInDir(parentId, token);
			Process process = new Process(processID);
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

	private void getInteractiveSVG(JSONObject jParams, HttpServletResponse res,
			FsAccessToken token) {
		try {
			String processID = jParams.getString("processID");
			String taskName = jParams.getString("task");
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");

			File fXmlFile = FileUtil.openSignavioFile(parentId, token,
					processID);
			Process process = new Process(processID);
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

	private void getSVG(JSONObject jParams, HttpServletResponse res,
			FsAccessToken token) {
		try {
			String processID = jParams.getString("processID");
			String taskName = jParams.getString("task");
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");

			File fXmlFile = FileUtil.openSignavioFile(parentId, token,
					processID);
			Process process = new Process(processID);
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
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");

			File fXmlFile = FileUtil.openSignavioFile(parentId, token,
					processID);
			Process process = new Process(processID);
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
			this.newQuery(jParams);
		}
	}

	private void newQuery(JSONObject jParams) {
		try {
			String processID = jParams.getString("processID");
			int zone = jParams.getInt("zone");
			String targetProcess = jParams.getString("targetProcess");
			String targetTask = jParams.getString("targetTask");
			String desc = jParams.getString("queryDesc");
			ProcessQuery query = new ProcessQuery(processID, targetProcess,
					targetTask, zone, desc);
			query.saveQuery();
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
			if (!exception.equals("") && exception != null) {
				res.getWriter().write(exception);
			} else {
				process.persist();
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

			File fXmlFile = FileUtil.openBpmn20File(parentId, token, name);

			Process process = new Process(name);
			String exception = process.mapXMLfileIntoModel(fXmlFile);
			if (!exception.equals("") && exception != null) {
				res.getWriter().write(exception);
			} else {
				if (jParams.has("id")) {
					String id = jParams.getString("id");
					boolean isNewProcess = this.deletePreviousProcess(id);
					if (isNewProcess) {
						process.deleteByProcessID();
						process.removeNeighborsService();
					}
				}
				process.persist();
				process.addNeighborServices(IConstant.NO_OF_MAX_ZONE, true);
				
				//for query process update init Status every time it is saved
				if(process.isQueryProcess()){
					ProcessQuery query = ProcessQuery.getProcessQuery(name);
					query.finishInitQuery();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean deletePreviousProcess(String id) {
		boolean isNewProcess = false;
		String[] ids = id.split(";");
		if (ids.length > 1) {
			String realID = ids[ids.length - 1].split("\\.")[0];
			Process.deleteByProcessIDStatic(realID);
			Process.removeNeighborsServiceStatic(realID);
		} else {
			isNewProcess = true;
		}
		return isNewProcess;
	}

}

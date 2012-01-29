package com.signavio.warehouse.query.handler;

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
		JSONArray jsons = new JSONArray();
		JSONObject entry = new JSONObject();
		try {
			entry.put("token", token);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		jsons.put(entry);
		try {
			res.getWriter().write(jsons.toString());
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
		try {
			String name = jParams.getString("name");
			String jsonRep = jParams.getString("json_xml");

			Process process = new Process(name);
			System.out.println(process.getProcessID());
			System.out.println(jsonRep);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

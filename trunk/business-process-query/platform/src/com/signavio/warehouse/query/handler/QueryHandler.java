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
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
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
			String parentId = jParams.getString("parent");
			parentId = parentId.replace("/directory/", "");
			String name = jParams.getString("name");
			File fXmlFile = this.openFile(parentId, token, name);

			Process process = new Process(name);
			String exception = process.mapXMLfileIntoModel(fXmlFile);
			if (!exception.equals("") && exception != null) {
				res.getWriter().write(exception);
			}else{
				process.printProcess();
				process.deleteByProcessID();
				process.persist();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File openFile(String parentId, FsAccessToken token, String name) {

		FsDirectory dir = FsSecurityManager.getInstance().loadObject(
				FsDirectory.class, parentId, token);
		String path = dir.getPath() + "/" + name + ".bpmn20.xml";

		return new File(path);
	}

}

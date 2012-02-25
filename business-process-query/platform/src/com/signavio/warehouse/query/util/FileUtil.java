package com.signavio.warehouse.query.util;

import java.io.File;

import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.warehouse.directory.business.FsDirectory;

public class FileUtil {
	public static File openBpmn20File(String parentId, FsAccessToken token,
			String name) {

		FsDirectory dir = FsSecurityManager.getInstance().loadObject(
				FsDirectory.class, parentId, token);
		String path = dir.getPath() + "/" + name + ".bpmn20.xml";

		return new File(path);
	}

	public static File openSignavioFile(String parentId, FsAccessToken token,
			String name) {

		FsDirectory dir = FsSecurityManager.getInstance().loadObject(
				FsDirectory.class, parentId, token);
		String path = dir.getPath() + "/" + name + ".signavio.xml";

		return new File(path);
	}

	public static File[] getFilesInDir(String parentId, FsAccessToken token) {
		FsDirectory dir = FsSecurityManager.getInstance().loadObject(
				FsDirectory.class, parentId, token);
		String path = dir.getPath() + "/";
		File folder = new File(path);
		return folder.listFiles();
	}
}

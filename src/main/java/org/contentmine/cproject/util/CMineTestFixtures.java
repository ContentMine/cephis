package org.contentmine.cproject.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ProjectSnippetsTree;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class CMineTestFixtures {

	private static final Logger LOG = Logger.getLogger(CMineTestFixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static void cleanAndCopyDir(File sourceDir, File targetDir) {
		try {
			if (targetDir.exists()) FileUtils.forceDelete(targetDir);
			LOG.trace(sourceDir.getAbsolutePath());
			FileUtils.copyDirectory(sourceDir, targetDir);
		} catch (IOException ioe) {
			throw new RuntimeException("failed to clean and copy: "+sourceDir+" @ "+targetDir +": "+ioe, ioe);
		}
	}


	public static ProjectSnippetsTree createProjectSnippetsTree(File testZikaFile, String snippetsName) throws IOException {
		File targetDir = createCleanedCopiedDirectory(testZikaFile, new File("target/relevance/zika"));
		Element snippetsTreeXML = XMLUtil.parseQuietlyToDocument(new File(targetDir, snippetsName)).getRootElement();;
		ProjectSnippetsTree projectsSnippetsTree = ProjectSnippetsTree.createProjectSnippetsTree(snippetsTreeXML);
		return projectsSnippetsTree;
	}


	public static File createCleanedCopiedDirectory(File testZikaFile, File targetDir) {
		CMineTestFixtures.cleanAndCopyDir(testZikaFile, targetDir);
		return targetDir;
	}




}

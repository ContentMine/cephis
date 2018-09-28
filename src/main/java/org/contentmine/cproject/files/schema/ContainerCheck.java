package org.contentmine.cproject.files.schema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CContainer;
import org.contentmine.cproject.files.CContainerTraverser;

/** checks content of CProject against schema
 * 
 * @author pm286
 *
 */
public class ContainerCheck {
	private static final Logger LOG = Logger.getLogger(ContainerCheck.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private AbstractSchemaElement schema;
	private CContainerTraverser containerTraverser;
	private List<File> sortedDirectories;
	private List<File> sortedFiles;
	private List<File> uncheckedFiles;

	ContainerCheck() {
	}
	
	public ContainerCheck(AbstractSchemaElement projectSchema) {
		setProjectSchema(projectSchema);
	}
	
	public void setProjectSchema(AbstractSchemaElement projectSchema) {
		this.schema = projectSchema;
	}
	
	public void checkProject(CContainer container) {
		containerTraverser = new CContainerTraverser(container);
		checkDirectoriesAndFilesAgainstSchema();
	}

	private void checkDirectoriesAndFilesAgainstSchema() {
		checkDirectories();
		checkFiles();
		uncheckedFiles = new ArrayList<File>(sortedDirectories);
		uncheckedFiles.addAll(sortedFiles);
		
	}

	private void checkDirectories() {
//		LOG.debug("======= DIRECTORIES ========");
		sortedDirectories = containerTraverser.getOrCreateSortedDirectoryList();
		FilenameSets dirnameSets = schema.getDirnameSets();
		dirnameSets.check(sortedDirectories);
	}

	private void checkFiles() {
//		LOG.debug("======= FILES ========");
		sortedFiles = containerTraverser.getOrCreateSortedFileList();
		FilenameSets filenameSets = schema.getFilenameSets();
		filenameSets.check(sortedFiles);
	}

	public List<File> getUncheckedFiles() {
		return uncheckedFiles;
	}


}

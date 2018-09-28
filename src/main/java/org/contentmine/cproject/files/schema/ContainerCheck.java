package org.contentmine.cproject.files.schema;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CContainer;
import org.contentmine.cproject.files.CContainerTraverser;
import org.contentmine.eucl.xml.XMLUtil;

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
	private boolean checkTrees;

	public ContainerCheck() {
		AbstractSchemaElement projectSchema = getDefaultProjectSchema();
		setProjectSchema(projectSchema);
	}

	private AbstractSchemaElement getDefaultProjectSchema() {
		InputStream is = this.getClass().getResourceAsStream(
				DefaultArgProcessor.FILES_TOP + "/" + AbstractSchemaElement.C_PROJECT_TEMPLATE_XML);
		AbstractSchemaElement projectSchema = 
				(AbstractSchemaElement) CProjectSchema.create(XMLUtil.parseQuietlyToRootElement(is));
		return projectSchema;
	}

	public ContainerCheck(String schemaString) {
		InputStream is = this.getClass().getResourceAsStream(
				DefaultArgProcessor.FILES_TOP + "/" + schemaString);
		setProjectSchema(schemaString, is);
	}

	private void setProjectSchema(String schemaString, InputStream is) {
		if (is == null) {
			throw new RuntimeException("cannot load schema: "+schemaString);
		}
		AbstractSchemaElement schema1 = 
				(AbstractSchemaElement) CProjectSchema.create(XMLUtil.parseQuietlyToRootElement(is));
		setProjectSchema(schema1);
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
		sortedDirectories = containerTraverser.getOrCreateSortedDirectoryList();
		FilenameSets dirnameSets = schema.getDirnameSets();
		dirnameSets.check(sortedDirectories, checkTrees);
	}

	private void checkFiles() {
		sortedFiles = containerTraverser.getOrCreateSortedFileList();
		FilenameSets filenameSets = schema.getFilenameSets();
		filenameSets.check(sortedFiles);
	}

	public List<File> getUncheckedFiles() {
		return uncheckedFiles;
	}

	public void setCheckTrees(boolean b) {
		this.checkTrees = b;
	}

}

package org.contentmine.cproject.files;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.schema.AbstractSchemaElement;
import org.contentmine.cproject.files.schema.CProjectSchema;
import org.contentmine.cproject.files.schema.ContainerCheck;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Test;

/** tests project schema
 * 
 * @author pm286
 *
 */
public class ProjectSchemaTest {
	private static final Logger LOG = Logger.getLogger(ProjectSchemaTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testProjectSchema() {
		InputStream is = this.getClass().getResourceAsStream(
				DefaultArgProcessor.FILES_TOP + "/" + AbstractSchemaElement.C_PROJECT_TEMPLATE_XML);
		Assert.assertNotNull("stream", is);
		AbstractSchemaElement schemaElement = AbstractSchemaElement.create(XMLUtil.parseQuietlyToRootElement(is));
		Assert.assertNotNull(schemaElement);
	}
	
	@Test
	public void testProjectSchemaCheck() {
		InputStream is = this.getClass().getResourceAsStream(
				DefaultArgProcessor.FILES_TOP + "/" + AbstractSchemaElement.C_PROJECT_TEMPLATE_XML);
		AbstractSchemaElement projectSchema = (AbstractSchemaElement) CProjectSchema.create(XMLUtil.parseQuietlyToRootElement(is));
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck(projectSchema);
		projectCheck.checkProject(new CProject(cProjectFile));
		Assert.assertEquals("unchecked files", 0, projectCheck.getUncheckedFiles().size());
	}
	
	@Test
	public void testProjectWithResultsSchemaCheck() {
		InputStream is = this.getClass().getResourceAsStream(
				DefaultArgProcessor.FILES_TOP + "/" + AbstractSchemaElement.C_PROJECT_TEMPLATE_XML);
		AbstractSchemaElement projectSchema = (AbstractSchemaElement) CProjectSchema.create(XMLUtil.parseQuietlyToRootElement(is));
		File cProjectFile = new File(CMineFixtures.TEST_PROJECTS_DIR, "indiaverysmall/");
		Assert.assertTrue("file exists", cProjectFile.exists());
		ContainerCheck projectCheck = new ContainerCheck(projectSchema);
		projectCheck.checkProject(new CProject(cProjectFile));
		Assert.assertEquals("unchecked files", 0, projectCheck.getUncheckedFiles().size());
	}
}

package org.contentmine.cproject.files.schema;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.schema.AbstractSchemaElement.IteratorType;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/** iterates over the schema for filenames.
 * This provides filenames against which the actual directory contents can be assessed.
 * 
 * @author pm286
 *
 */
public class FilenameSets  {
	private static final Logger LOG = Logger.getLogger(FilenameSets.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private IteratorType iteratorType;
	private List<Element> childElements;
	private Map<String, AbstractSchemaElement> schemaElementSetByName;
	private Set<AbstractSchemaElement> regexSchemaElementSet;

	public FilenameSets(AbstractSchemaElement schemaElement, IteratorType iteratorType) {
		this.iteratorType = iteratorType;
		String typeName = this.iteratorType.getTypeName();
		childElements = XMLUtil.getQueryElements(schemaElement, "./*[local-name()='"+typeName+"']");
		regexSchemaElementSet = new HashSet<AbstractSchemaElement>();
		schemaElementSetByName = new HashMap<String, AbstractSchemaElement>();
		for (Element childElement : childElements) {
			AbstractSchemaElement childSchemaElement = (AbstractSchemaElement) childElement;
			if (childSchemaElement.getRegex() != null) {
				regexSchemaElementSet.add(childSchemaElement);
			} else if (childSchemaElement.getName() != null) {
				schemaElementSetByName.put(childSchemaElement.getName(), childSchemaElement);
			} else {
				throw new RuntimeException("Schema must have either regex or name: "+childSchemaElement.toString());
			}
		}
//		LOG.debug("regex: " + regexSchemaElementSet);
//		LOG.debug("name:  " + schemaElementSetByName);
	}
	
//	private boolean exactMatch(File file) {
//		String filename = file.getName();
//		if (schemaElementSetByName.containsKey(filename)) {
//			// add schema conditions here , if any
//			schemaElementSetByName.remove(filename);
//			return true;
//		}
//		return false;
//	}
//
//	private boolean matches(String filename) {
//		for (AbstractSchemaElement regexSchemaElement : regexSchemaElementSet) {
//			Pattern pattern = regexSchemaElement.getPattern();
//			if (pattern.matcher(filename).matches()) {
//				LOG.debug("MATCHES");
//				// we should decrement the count
//				return true;
//			}
//		}
//		return false;
//	}

	public void check(List<File> files) {
		checkRegex(files);
		checkNames(files);
	}

	private void checkNames(List<File> files) {
		for (String schemaFilename : schemaElementSetByName.keySet()) {
			AbstractSchemaElement schemaElement = schemaElementSetByName.get(schemaFilename);
			String name = schemaElement.getName();
			boolean change = true;
			while (change) {
				change = false;
				for (int i = 0; i < files.size(); i++) {
					File file = files.get(i);
					String filename = file.getName();
					if (filename.equals(name)) {
//						LOG.debug("EQUALS "+filename);
						files.remove(i);
						change = true;
						break;
					}
				}
			}
		}
//		LOG.debug("F "+files);
	}
	private void checkRegex(List<File> files) {
		for (AbstractSchemaElement schemaElement : regexSchemaElementSet) {
			Pattern pattern = schemaElement.getPattern();
			boolean change = true;
			while (change) {
				change = false;
				for (int i = 0; i < files.size(); i++) {
					File file = files.get(i);
					String filename = file.getName();
//					LOG.debug(pattern+" / "+filename);
					if (pattern.matcher(filename).matches()) {
//						LOG.debug("MATCH "+filename);
						files.remove(i);
						change = true;
						break;
					}
				}
			}
		}
//		LOG.debug("D "+files);
	}

}

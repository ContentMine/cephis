package org.contentmine.cproject.files;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CProjectArgProcessor;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.args.FileXPathSearcher;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.ProjectAnalyzer;
import org.contentmine.cproject.metadata.AbstractMetadata.Type;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.gson.JsonParser;

import nu.xom.Element;
import nu.xom.Node;

public class CProject extends CContainer {

	private static final Logger LOG = Logger.getLogger(CProject.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String PROJECT_TEMPLATE_XML = "cProjectTemplate.xml";
	public static final String TREE_TEMPLATE_XML = "cTreeTemplate.xml";
	public static final String URL_LIST = "urlList.txt";
	
	public final static String IMAGE   = "image";
	public final static String RESULTS = "results";
	public final static String TABLE   = "table";

	// suffixes
	private static final String HTML = "html";

	// move these to plugin subdirs later
	public static final String SPECIES_GENUS_SNIPPETS_XML = "species.genus.snippets.xml";
	public static final String SPECIES_BINOMIAL_SNIPPETS_XML = "species.binomial.snippets.xml";
	public static final String GENE_HUMAN_SNIPPETS_XML = "gene.human.snippets.xml";
	public static final String SEQUENCE_DNAPRIMER_SNIPPETS_XML = "sequence.dnaprimer.snippets.xml";
	public static final String WORD_FREQUENCIES_SNIPPETS_XML = "word.frequencies.snippets.xml";
	
	public static final String DATA_TABLES_HTML = "dataTables.html";

	protected static final String[] ALLOWED_METADATA_NAMES = new String[] {
			// these are messy, 
			AbstractMetadata.Type.CROSSREF.getCProjectMDFilename(),
			AbstractMetadata.Type.EPMC.getCProjectMDFilename(),
			AbstractMetadata.Type.QUICKSCRAPE.getCProjectMDFilename(),
	};

	protected static final String[] ALLOWED_FILE_NAMES = new String[] {
			
		MANIFEST_XML,
		LOG_XML,
		URL_LIST,
		
		AbstractMetadata.Type.CROSSREF.getCProjectMDFilename(),
		AbstractMetadata.Type.EPMC.getCProjectMDFilename(),
		AbstractMetadata.Type.QUICKSCRAPE.getCProjectMDFilename(),

	};
	
	
	
	protected static final Pattern[] ALLOWED_FILE_PATTERNS = new Pattern[] {
	};
	
	protected static final String[] ALLOWED_DIR_NAMES = new String[] {
		RESULTS,
		TABLE,
		IMAGE,
	};
	
	protected static final Pattern[] ALLOWED_DIR_PATTERNS = new Pattern[] {
	};
	public static final String OMIT_EMPTY = "omitEmpty";
	
//	private Element projectTemplateElement;
//	private Element treeTemplateElement;
	private CTreeList cTreeList;
	private ProjectSnippetsTree projectSnippetsTree;
	private ProjectFilesTree projectFilesTree;
	private ResultsElementList summaryResultsElementList;
	private ArrayList<File> scholarlyList;
	private ProjectAnalyzer projectAnalyzer;
	private CTreeList duplicateMergeList;
	private DefaultArgProcessor argProcessor;

	public static void main(String[] args) {
		CProject cProject = new CProject();
		cProject.run(args);
	}

	public void run(String[] args) {
		argProcessor = new CProjectArgProcessor(args);
		argProcessor.runAndOutput();
	}

	public void run(String args) {
		run(args.split("\\s+"));
	}

	public DefaultArgProcessor getArgProcessor() {
		return argProcessor;
	}

	/** mainly served for running commandlines
	 * 
	 */
	public CProject() {
		super();
	}

	public CProject(File cProjectDir) {
		super();
		this.directory = cProjectDir;
//		projectTemplateElement = readTemplate(PROJECT_TEMPLATE_XML);
//		treeTemplateElement = readTemplate(TREE_TEMPLATE_XML);
	}
	
	@Override
	protected CManifest createManifest() {
		manifest = new CProjectManifest(this);
		return manifest;
	}
	
	@Override
	protected void calculateFileAndCTreeLists() {
		cTreeList = new CTreeList();
		int i = 0;
		for (File directory : allChildDirectoryList) {
			if (false) {
			} else if (
				(isAllowedFile(directory, ALLOWED_DIR_PATTERNS) ||
				isAllowedFileName(directory, ALLOWED_DIR_NAMES))) {
				allowedChildDirectoryList.add(directory);
				// don't consider for CTree
			} else if (isCTree(directory)) {
				CTree cTree = new CTree(directory);
				cTreeList.add(cTree);
			} else {
				unknownChildDirectoryList.add(directory);
			}
		}
	}

	@Override
	protected void getAllowedAndUnknownFiles() {
		for (File file : allChildFileList) {
			if (false) {
			} else if (
				isAllowedFile(file, ALLOWED_FILE_PATTERNS) ||
				isAllowedFileName(file, ALLOWED_FILE_NAMES) ||
				includeAllDirectories()) {
				if (!allowedChildFileList.contains(file)) {
					allowedChildFileList.add(file);
				}
			} else {
				if (!unknownChildFileList.contains(file)) {
					unknownChildFileList.add(file);
				}
			}
		}
	}
	
	public List<File> getAllNonDirectoryFiles() {
		getAllowedAndUnknownFiles();
		List<File> allFiles = new ArrayList<File>(allowedChildFileList);
		for (File file : unknownChildFileList) {
			if (!allFiles.contains(file)) {
				allFiles.add(file);
			}
		}
		return allFiles;
	}
	
	private boolean isAllowedFilename(String filename) {
		return (Arrays.asList(ALLOWED_FILE_NAMES).contains(filename));
	}

	/** currently just take a simple approach.
	 * 
	 * if manifest.xml or fulltext.* or results.json is present this should be OK
	 * later we'll use manifest templates
	 * 
	 * @param directory
	 * @return
	 */
	private boolean isCTree(File directory) {
		getTreesAndDirectories();
		CTree testTree = new CTree(directory);
		testTree.getOrCreateChildDirectoryAndChildFileList();
		// put filenames first to eliminate matching
		boolean allowed = 
				isAnyAllowed(testTree.allChildFileList, CTree.ALLOWED_FILE_NAMES) ||
				isAnyAllowed(testTree.allChildDirectoryList, CTree.ALLOWED_DIR_NAMES) ||
				isAnyAllowed(testTree.allChildFileList, CTree.ALLOWED_FILE_PATTERNS) ||
				isAnyAllowed(testTree.allChildDirectoryList, CTree.ALLOWED_DIR_PATTERNS) ||
				includeAllDirectories()
				;
		return allowed;
	}

	/** getCTreeList after recalculating from current Files.
	 * 
	 * to get Current CTreeList, use getOrCreateCTreeList()
	 * @return
	 */
	public CTreeList getResetCTreeList() {
		this.getOrCreateFilesDirectoryCTreeLists();
		if (cTreeList != null) {
			cTreeList.sort();
		}
		return cTreeList;
	}
	
	

	public List<File> getResultsXMLFileList() {
		List<File> resultsXMLList = new ArrayList<File>();
		this.getResetCTreeList();
		for (CTree cTree : cTreeList) {
			List<File> resultsXMLList0 = cTree.getResultsXMLFileList();
			resultsXMLList.addAll(resultsXMLList0);
		}
		return resultsXMLList;
	}

	public List<File> getResultsXMLFileList(String control) {
		List<File> resultsXMLList = getResultsXMLFileList();
		if (CProject.OMIT_EMPTY.equals(control)) {
			for (int i = resultsXMLList.size() - 1; i >= 0; i--) {
				File f = resultsXMLList.get(i);
				if (ResultsElement.isEmpty(f)) {
					resultsXMLList.remove(i);
				}
			}
		}
		return resultsXMLList;
	}

	public CTree getCTreeByName(String name) {
		CTree cTree = null;
		if (name != null) {
			getResetCTreeList();
			if (cTreeList != null) {
				cTree = cTreeList.get(name);
			}
		}
		return cTree;
	}
	/** outputs filenames relative to project directory.
	 * 
	 * normalizes to UNIX separator
	 * 
	 * i.e. file.get(i) should be equivalent to new File(cprojectDirectory, paths.get(i))
	 * 
	 * @param files
	 * @return list of relative paths
	 */
	public List<String> getRelativeProjectPaths(List<File> files) {
		List<String> fileNames = new ArrayList<String>();
		for (File file : files) {
			String fileName = getRelativeProjectPath(file);
			if (fileName != null) {
				fileNames.add(fileName);
			}
		}
		return fileNames;
	}

	/** outputs filenams relative to project directory.
	 * 
	 * normalizes to UNIX separator
	 * 
	 * i.e. file should be equivalent to new File(cprojectDirectory, path)
	 * 
	 * @param file
	 * @return relative path; null if cannot construct it.
	 */
	public String getRelativeProjectPath(File file) {
		String directoryName = FilenameUtils.normalize(directory.getAbsolutePath(), true);
		String fileName = FilenameUtils.normalize(file.getAbsolutePath(), true);
		String pathName = null;
		if (fileName.startsWith(directoryName)) {
			pathName = fileName.substring(directoryName.length() + 1); // includes separator
		}
		return pathName;
	}

	/**
	 * 
	 * @param glob (e.g. * * /word/ * * /result.xml) [spaces to escape comments so remove spaces a]
	 * @return
	 */
	public ProjectFilesTree extractProjectFilesTree(String glob) {
		ProjectFilesTree projectFilesTree = new ProjectFilesTree(this);
//		List<CTreeFiles> cTreeFilesList = new ArrayList<CTreeFiles>();
		CTreeList cTreeList = this.getResetCTreeList();
		for (CTree cTree : cTreeList) {
			CTreeFiles cTreeFiles = cTree.extractCTreeFiles(glob);
			projectFilesTree.add(cTreeFiles);
		}
		return projectFilesTree;
	}

	/** get list of matched Elements from CTrees in project.
	 * 
	 * @param glob
	 * @param xpath
	 * @return
	 */
	public ProjectSnippetsTree extractProjectSnippetsTree(String glob, String xpath) {
		projectSnippetsTree = new ProjectSnippetsTree(this);
		CTreeList cTreeList = this.getResetCTreeList();
		for (CTree cTree : cTreeList) {
			SnippetsTree snippetsTree = cTree.extractXPathSnippetsTree(glob, xpath);
			if (snippetsTree.size() > 0) {
				projectSnippetsTree.add(snippetsTree);
			}
		}
		return projectSnippetsTree;
	}
	
	/** get list of matched Elements from CTrees in project.
	 * 
	 * @param glob
	 * @param xpath
	 * @return
	 */
	public ProjectSnippetsTree extractProjectSnippetsTree(String searchExpression) {
		FileXPathSearcher fileXPathSearcher = new FileXPathSearcher(searchExpression);
		String glob = fileXPathSearcher.getCurrentGlob();
		String xpath = fileXPathSearcher.getCurrentXPath();
		projectSnippetsTree = extractProjectSnippetsTree(glob, xpath);
		return projectSnippetsTree;
	}

	public ProjectSnippetsTree getProjectSnippetsTree() {
		return projectSnippetsTree;
	}
	
	public ProjectFilesTree getProjectFilesTree() {
		return projectFilesTree;
	}

	public void add(CTreeFiles cTreeFiles) {
		ensureProjectFilesTree();
		projectFilesTree.add(cTreeFiles);
	}

	private void ensureProjectFilesTree() {
		if (projectFilesTree == null) {
			projectFilesTree = new ProjectFilesTree(this);
		}
	}

	public void add(SnippetsTree snippetsTree) {
		ensureProjectSnippetsTree();
		projectSnippetsTree.add(snippetsTree);
	}

	private void ensureProjectSnippetsTree() {
		if (projectSnippetsTree == null) {
			projectSnippetsTree = new ProjectSnippetsTree(this);
		}
	}

	public void outputProjectSnippetsTree(File outputFile) {
		outputTreeFile(projectSnippetsTree, outputFile);
	}

	public void outputProjectFilesTree(File outputFile) {
		outputTreeFile(projectFilesTree, outputFile);
	}

	private void outputTreeFile(Element tree, File outputFile)  {
		if (tree != null) {
			try {
				XMLUtil.debug(tree, outputFile, 1);
				LOG.trace("wrote: "+outputFile);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write output: ", e);
			}
		}
	}

	public void addSummaryResultsElement(ResultsElement summaryResultsElement) {
		ensureSummaryResultsElementList();
		LOG.trace("> "+summaryResultsElement.toXML());
		summaryResultsElementList.addToMultiset(summaryResultsElement);
	}

	private void ensureSummaryResultsElementList() {
		if (this.summaryResultsElementList == null) {
			this.summaryResultsElementList = new ResultsElementList();
		}
	}
	
	public Multiset<String> getMultiset() {
		return summaryResultsElementList == null ? null : summaryResultsElementList.getMultisetSortedByCount();
	}

	/** requires all cTrees to have scholarlyHtml
	 * 
	 * @param fraction of CTrees that need to have scholarly.html
	 * @return
	 */
	public boolean hasScholarlyHTML(double fractionRequired) {
		CTreeList cTreeList = this.getResetCTreeList();
		
		int hasNot = 0;
		int total = 0;
		for (CTree cTree : cTreeList) {
			if (!cTree.hasScholarlyHTML()) {
				// if require all, then quit immediately
				if (Math.abs(fractionRequired - 1.0) > 0.001) {
					return false;
				}
				hasNot++;
			}
			total++;
		}
		double fractionWithout = (double) hasNot / (double) total;
		return  fractionWithout <=  (1.0 - fractionRequired);  
	}

	/** heuristic lists all CProjects under projectTop directory.
	 * finds descendant files through glob and tests them for conformity with CProject
	 * globbing through CMineGlobber
	 * 
	 * @param projectTop
	 * @param glob - allows selection of possible projects
	 * @return
	 */
	public static List<CProject> globCProjects(File projectTop, String glob) {
		List<CProject> projectList = new ArrayList<CProject>();
		List<File> possibleProjectFiles = CMineGlobber.listGlobbedFilesQuietly(projectTop, glob);
		for (File possibleProjectFile : possibleProjectFiles) {
			if (possibleProjectFile.isDirectory()) {
				CProject cProject = CProject.createPossibleCProject(possibleProjectFile);
				if (cProject != null) {
					projectList.add(cProject);
				}
			}
		}
		return projectList;
	}

	public Set<String> extractMetadataItemSet(AbstractMetadata.Type sourceType, String type) {
		CTreeList cTreeList = getResetCTreeList();
		Set<String> set = new HashSet<String>();
		for (CTree cTree : cTreeList) {
			AbstractMetadata metadata = AbstractMetadata.getCTreeMetadata(cTree, sourceType);
			String typeValue = metadata.getJsonStringByPath(type);
			set.add(typeValue);
		}
		return set;
	}

	public Multimap<String, String> extractMetadataItemMap(AbstractMetadata.Type sourceType, String key, String type) {
		CTreeList cTreeList = getResetCTreeList();
		Multimap<String, String> map = ArrayListMultimap.create();
		for (CTree cTree : cTreeList) {
			AbstractMetadata metadata = AbstractMetadata.getCTreeMetadata(cTree, sourceType);
			if (metadata != null) {
				String keyValue = metadata.getJsonStringByPath(key);
				String typeValue = metadata.getJsonStringByPath(type);
				map.put(keyValue, typeValue);
			}
		}
		return map;
	}
	
	public Multimap<CTree, File> extractCTreeFileMapContaining(String reservedName) {
		CTreeList cTreeList = getResetCTreeList();
		Multimap<CTree, File> map = ArrayListMultimap.create();
		for (CTree cTree : cTreeList) {
			File file = cTree.getExistingReservedFile(reservedName);
			if (file != null && file.exists()) {
				map.put(cTree, file);
			}
		}
		return map;
	}
	
	public File createAllowedFile(String filename) {
		File file = null;
		if (isAllowedFilename(filename)) {
			file = new File(directory, filename);
		}
		return file;
	}
	
	// ====================
	
	public ArrayList<File> getOrCreateScholarlyHtmlList() {
		List<File> files = new ArrayList<File>(FileUtils.listFiles(
				getDirectory(), new String[]{HTML}, true));
		scholarlyList = new ArrayList<File>();
		for (File file : files) {
			if (file.getName().equals(CTree.SCHOLARLY_HTML)) {
				scholarlyList.add(file);
			}
		}
		return scholarlyList;
	}

	public Multiset<String> getOrCreateHtmlBiblioKeys() {
		getOrCreateScholarlyHtmlList();
		Multiset<String> keySet = HashMultiset.create();
		for (File scholarly : scholarlyList) {
			HtmlElement htmlElement = HtmlElement.create(XMLUtil.parseQuietlyToDocument(scholarly).getRootElement());
			List<Node> nodes = XMLUtil.getQueryNodes(htmlElement, "//*[local-name()='meta']/@name");
			for (Node node : nodes) {
				String name = node.getValue().toLowerCase();
				name = name.replace("dcterms", "dc");
				keySet.add(name);
			}
		}
		return keySet;
	}

	private static CProject createPossibleCProject(File possibleProjectFile) {
		CProject project = new CProject(possibleProjectFile);
		CTreeList cTreeList = project.getResetCTreeList();
		return (cTreeList.size() == 0) ? null : project;
		
	}

	public CTreeList getCTreeList(CTreeExplorer explorer) {
		CTreeList cTreeListOld = this.getResetCTreeList();
		CTreeList cTreeList = new CTreeList();
		for (CTree cTree : cTreeListOld) {
			if (cTree.matches(explorer)) {
				cTreeList.add(cTree);
			}
		}
		return cTreeList;
	}

	public void normalizeDOIBasedDirectoryCTrees() {
		getResetCTreeList();
		for (int i = cTreeList.size() - 1; i >= 0; i--) {
			CTree cTree = cTreeList.get(i);
			cTree.normalizeDOIBasedDirectory();
		}
	}

	public List<String> extractShuffledCrossrefUrls() {
		ProjectAnalyzer projectAnalyzer = this.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		List<String> urls = projectAnalyzer.extractURLs();
		return urls;
	}

	
	public void extractShuffledUrlsFromCrossrefToFile(File file) throws IOException {
		ProjectAnalyzer projectAnalyzer = this.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		projectAnalyzer.extractURLsToFile(file);
	}
	
	public void setProjectAnalyzer(ProjectAnalyzer projectAnalyzer) {
		this.projectAnalyzer = projectAnalyzer;
	}

	public ProjectAnalyzer getOrCreateProjectAnalyzer() {
		if (this.projectAnalyzer == null) {
			this.projectAnalyzer = new ProjectAnalyzer(this);
		}
		return projectAnalyzer;
	
	}

	/** get DOIPrefixes.
	 * 
	 * (not unique) may be multiple entries 
	 * @return
	 */
	public List<String> getDOIPrefixList() {
		List<String> doiPrefixList = new ArrayList<String>();
		CTreeList cTreeList = this.getResetCTreeList();
		for (CTree cTree : cTreeList) {
			String doiPrefix = cTree.extractDOIPrefix();
			doiPrefixList.add(doiPrefix);
		}
		return doiPrefixList;
	}

	/** get CTrees with given DOIPrefixe.
	 * 
	 * @return
	 */
	public CTreeList getCTreesWithDOIPrefix(String prefix) {
		CTreeList cTreeList = this.getResetCTreeList();
		CTreeList treesWithPrefix = new CTreeList();
		if (prefix != null) {
			for (CTree cTree : cTreeList) {
				String doiPrefix = cTree.extractDOIPrefix();
				if (doiPrefix == null) {
					LOG.warn("null DOI prefix: "+cTree.getDirectory());
				} else if (prefix.equals(doiPrefix)) {
					treesWithPrefix.add(cTree);
				}
			}
		}
		return treesWithPrefix;
	}

	public int size() {
		getResetCTreeList();
		return (cTreeList == null) ? 0 : cTreeList.size();
	}

	public List<String> extractShuffledFlattenedCrossrefUrls() {
		List<String> urls = extractShuffledCrossrefUrls();
		List<String> flattenedUrls = new ArrayList<String>();
		for (int j = 0; j < urls.size(); j++) {
			String url = urls.get(j);
			String flattenedURL = CMineUtil.denormalizeDOI(url);
			flattenedUrls.add(flattenedURL);
		}
		return flattenedUrls;
	}

	public File getMetadataFile(AbstractMetadata.Type type) {
		return (directory == null) ? null : new File(this.getDirectory(), type.getCProjectMDFilename());
	}

	public File getExistingMetadataFile(AbstractMetadata.Type type) {
		File resultsJson = getMetadataFile(type);
		return (resultsJson == null || !resultsJson.exists()) ? null : resultsJson;
	}

	/** gets a list of all metadataTypes which have been used to create or manage the CProject.
	 * 
	 *  based on whether the metadata files exist
	 * 
	 * @return
	 */
	public List<AbstractMetadata.Type> getExistingMetadataTypes() {
		List<Type> types = new ArrayList<Type>();
		for (Type type : Type.values()) {
			if (this.getExistingMetadataFile(type) != null) {
				types.add(type);
			}
		}
		return types;
	}

	/** merges one Cproject into another.
	 * 
	 * @param cProject2
	 * @throws IOException 
	 */
	public void mergeProject(CProject project2) throws IOException {
		CTreeList cTreeList2 = project2.getResetCTreeList();
		copyCTrees(cTreeList2);
		cTreeList = null;
		copyFiles(project2);
		resetFileLists();
	}

	private void copyFiles(CProject project2) throws IOException {
		List<File> projectFiles2 = project2.getAllNonDirectoryFiles();
		List<File> projectFiles = this.getAllNonDirectoryFiles();
		JsonParser jsonParser = new JsonParser();
		for (File file2 : projectFiles2) {
			String name2 = file2.getName();
			File thisFile = this.getFileWithName(name2);
			if (thisFile == null) {
				FileUtils.copyFile(file2, new File(this.directory, name2));
			} else if (Type.getTypeFromCProjectFile(file2) != null) {
				Type.mergeMetadata(thisFile, file2);
			} else {
				LOG.debug("existing file, so not copied: "+file2);
			}
		}
	}

	/** finds file with given name.
	 * 
	 * @param name
	 * @return
	 */
	public File getFileWithName(String name) {
		File[] files = directory == null ? null : directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName().equals(name)) {
					return file;
				}
			}
		}
		return null;
	}

	private void copyCTrees(CTreeList cTreeList2) {
		for (CTree cTree2 : cTreeList2) {
			try {
				this.ingestCopy(cTree2);
			} catch (IOException ioe) {
				LOG.warn("Cannot ingest CTree: "+cTree2);
			}
		}
	}

	/** creates a copy of CTree within this.cProject.
	 * 
	 * keeps a list of duplicate trees 
	 * 
	 * @param cTree2
	 * @throws IOException 
	 */
	public void ingestCopy(CTree cTree2) throws IOException {
		getResetCTreeList();
		getOrCreateDuplicateMergeList();
  		if (!cTreeList.containsName(cTree2)) {
			copyCTreeAndUpdateNonOverlappingCTreeList(cTree2);
		} else {
			duplicateMergeList.add(cTree2);
		}
	}

	private void copyCTreeAndUpdateNonOverlappingCTreeList(CTree cTree2) throws IOException {
		File directory2 = cTree2.getDirectory();
		String name2 = directory2.getName();
		File cTreeDirectory = new File(this.directory, name2);
		FileUtils.copyDirectory(directory2, cTreeDirectory);
		CTree thisCTree = new CTree(cTreeDirectory);
		this.cTreeList.add(thisCTree);
	}
	
	public CTreeList getOrCreateDuplicateMergeList() {
		if (duplicateMergeList == null) {
			duplicateMergeList = new CTreeList();
		}
		return duplicateMergeList;
	}

	public void add(CTree cTree) {
		getOrCreateCTreeList();
		cTreeList.add(cTree);
	}
	
	public void addCTreeList(CTreeList cTreeList2) {
		getOrCreateCTreeList();
		this.cTreeList = this.cTreeList.or(cTreeList2);
	}

	public void addCTreeListAndCopyContents(CTreeList cTreeList1) {
		addCTreeList(cTreeList1);
		for (int i = 0; i < cTreeList1.size(); i++) {
			try {
				File directory1 = cTreeList1.get(i).getDirectory();
				File thisDirectory = new File(this.getDirectory(), directory1.getName());
				FileUtils.copyDirectory(directory1, thisDirectory);
			} catch (IOException e) {
				LOG.error("Cannot copy Directory: "+cTreeList.get(i).getDirectory()+" "+e);
			}
		}
	}

	public CTreeList getOrCreateCTreeList() {
		if (cTreeList == null) {
			cTreeList = new CTreeList();
		}
		return cTreeList;
	}

	public void writeProjectAndCTreeList() throws IOException {
		if (directory != null) {
			directory.mkdirs();
			if (cTreeList != null) {
				for (CTree cTree : cTreeList) {
					cTree.write(directory);
				}
			}
		}
	}

	/** get Multimap of CTrees indexed by DOIPrefix.
	 * 
	 * @return 
	 */
	public Multimap<String, CTree> getCTreeListsByPrefix() {
		CTreeList cTreeList = this.getResetCTreeList();
		Multimap<String, CTree> treeListsbyPrefix = ArrayListMultimap.create();
		for (CTree cTree : cTreeList) {
			String doiPrefix = cTree.extractDOIPrefix();
			treeListsbyPrefix.put(doiPrefix, cTree);
		}
		return treeListsbyPrefix;
	}


}

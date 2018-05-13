package org.contentmine.cproject.metadata.epmc;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.metadata.AbstractMetadata;

public class EpmcMD extends AbstractMetadata {
	
	static final Logger LOG = Logger.getLogger(EpmcMD.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	// note "eupmc" as historical accident
	public static final String CTREE_RESULT_JSON = "eupmc_result.json";
	public static final String CPROJECT_RESULT_JSON = "eupmc_results.json";
	
	public EpmcMD() {
		super();
		hasQuickscrapeMetadata = true;
	}
	
	public static AbstractMetadata createMetadata() {
		return new EpmcMD();
	}

//	@Override
//	public String getAbstract() {
//		return getJsonArrayStringByPath($_ABSTRACT_VALUE);
//	}
//
//	@Override
//	public String getAbstractURL() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getAuthorEmail() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> getAuthorListAsStrings() {
//		return getJsonArrayByPath($_AUTHOR_VALUE);
//	}
//
//	@Override
//	public String getAuthorInstitution() {
//		return getJsonValueOrHtmlMetaContent($_AUTHOR_INSTITUTION, new String[] {CITATION_AUTHOR_INSTITUTION});
//	}
//
//	@Override
//	public String getCitations() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getCopyright() {
//		return getJsonArrayStringByPath($_COPYRIGHT_VALUE);
//	}
//
//	@Override
//	public String getCreator() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getDate() {
//		return getJsonArrayStringByPath($_DATE_VALUE);
//	}
//
//	@Override
//	public String getDescription() {
//		return getJsonArrayStringByPath($_DESCRIPTION_VALUE);
//	}
//
//	@Override
//	public String getDOI() {
//		return getJsonArrayStringByPath($_DOI_VALUE);
//	}
//
//	@Override
//	public String getFirstPage() {
//		return getJsonArrayStringByPath($_FIRSTPAGE_VALUE);
//	}
//
//	@Override
//	public String getFulltextHTMLURL() {
//		return getJsonArrayStringByPath($_FULLTEXT_HTML_VALUE);
//	}
//
//	@Override
//	public String getFulltextPDFURL() {
//		return getJsonArrayStringByPath($_FULLTEXT_PDF_VALUE);
//	}
//
//	@Override
//	public String getFulltextPublicURL() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getFulltextXMLURL() {
//		return getJsonArrayStringByPath($_FULLTEXT_XML_VALUE);
//	}
//
//	@Override
//	public String getISSN() {
//		return getJsonValueOrHtmlMetaContent($_ISSN_VALUE, new String[] {CITATION_ISSN});
//	}
//
//	@Override
//	public String getIssue() {
//		return getJsonArrayStringByPath($_ISSUE_VALUE);
//	}
//
//	@Override
//	public String getJournal() {
//		return getJsonValueOrHtmlMetaContent($_JOURNAL_VALUE, new String[] {CITATION_JOURNAL_TITLE});
//	}
//
//	@Override
//	public String getKeywords() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getLanguage() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getLastPage() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getLicense() {
//		return getJsonValueOrHtmlMetaContent($_LICENSE_VALUE, new String[] {DC_RIGHTS});		
//	}
//
//	@Override
//	public String getPublicURL() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getPublisher() {
//		return getJsonValueOrHtmlMetaContent($_PUBLISHER_VALUE, new String[] {CITATION_PUBLISHER, DC_PUBLISHER});
//	}
//
//	@Override
//	public String getReferenceCount() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getRights() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String getTitle() {
//		return getJsonArrayStringByPath($_TITLE_VALUE);
//	}
//
//
//	@Override
//	public String getURL() {
//		return getJsonArrayStringByPath($_URL_VALUE);
//	}
//
//	@Override
//	public String getVolume() {
//		return getJsonArrayStringByPath($_VOLUME_VALUE);
//	}
//
//	@Override
//	public String getLinks() {
//		return null;
//	}
//
//	@Override
//	public String getPrefix() {
//		return null;
//	}
//
//	@Override 
//	public String hasQuickscrapeMetadata() {
//		hasQuickscrapeMetadata = (cTree != null && cTree.getExistingQuickscrapeMD() != null);
//		return hasQuickscrapeMetadata ? "Y" : "N";
//	}
	
	@Override
	protected String getCTreeMetadataFilename() {
		return CTREE_RESULT_JSON;
	}

	@Override
	protected String getCProjectMetadataFilename() {
		return CPROJECT_RESULT_JSON;
	}


	

}

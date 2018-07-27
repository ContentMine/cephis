package org.contentmine.eucl.euclid.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** utilities which don't occur in Apache FileUtils or FilenameUtils
 * 
 * @author pm286
 *
 */
public class CMFileUtil {

	/**
	 * sorts by embedded integer, in file(names) otherwise identical e.g. sorts
	 * file1foo.svg, , file11foo.svg, file2foo.svg
	 * file1foo.svg, file2foo.svg, file11foo.svg
	 * uses regex of form
	 * [^\d\*]\d+([^\d].*)?
	 * 
	 * the surroundings of the integer defines the pattern.
	 * The first file is used to extract the exact surroundings and
	 * then this is used to test the rest
	 * 
	 * @param files
	 */
	public static List<File> sortUniqueFilesByEmbeddedIntegers(List<File> files) {
		List<File> sortedFiles = new ArrayList<File>();
		Map<String, File> fileByName = new HashMap<String, File>();
		if (files.size() > 0) {
			for (File file : files) {
				String filename = file.getName();
				if (fileByName.keySet().contains(filename)) {
					throw new RuntimeException("Duplicate filename: "+filename);
				}
				fileByName.put(filename, file);
			}
			List<String> filenames = new ArrayList<String>(fileByName.keySet());
			filenames = CMStringUtil.sortUniqueStringsByEmbeddedIntegers(filenames);
			for (String filename : filenames) {
				sortedFiles.add(fileByName.get(filename));
			}
		}
		return sortedFiles;
	}

	/** asserts that file exists and is a directory.
	 * 
	 * @param directory
	 * @throws RuntimeException if false
	 */
	public static void assertExistingDirectory(File directory) {
		if (directory == null || !directory.exists() || !directory.isDirectory()) {
			throw new RuntimeException("file does not exist or is not directory: "+directory);
		}
	}
	/** asserts that file exists and is not a directory.
	 * 
	 * @param file
	 * @throws RuntimeException if false
	 */
	public static void assertExistingNonDirectory(File file) {
		if (file == null || !file.exists() || file.isDirectory()) {
			throw new RuntimeException("file does not exist or is not directory: "+file);
		}
	}

	public static Integer getEmbeddedInteger(File svgFile) {
		
		// TODO Auto-generated method stub
		return null;
	}
}

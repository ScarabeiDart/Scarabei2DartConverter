
package com.jfixby.scarabei.scarabei2dart.api;

import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FilesList;
import com.jfixby.scarabei.api.util.path.RelativePath;

public class ProcessingArguments {

	public File output;
	public File project;
	public File outputProject;
	public File templateFolder;
	public String outputProjectName;
	public String versionString;
	public RelativePath srcPrefix;
	public FilesList inputJavaFiles;

}

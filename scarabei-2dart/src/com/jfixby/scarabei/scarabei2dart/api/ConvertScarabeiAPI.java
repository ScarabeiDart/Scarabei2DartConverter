
package com.jfixby.scarabei.scarabei2dart.api;

import java.io.IOException;

import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FilesList;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.scarabei2dart.CFG;

public class ConvertScarabeiAPI {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();

		final File input = LocalFileSystem.newFile(CFG.inputPathString);
		final File output = LocalFileSystem.newFile(CFG.outputPathString);

		final FilesList projects = input.listAllChildren(file -> {
			try {
				return file.isFolder() && file.getName().toLowerCase().startsWith("scarabei-api");
			} catch (final IOException e) {
				e.printStackTrace();
				return false;
			}
		});

		convertProjects(projects);

// projects.print("input");
// output.listAllChildren().print("output");
	}

	private static void convertProjects (final FilesList projects) {
		for (final File p : projects) {
			if (p.getName().equals("scarabei-api")) {
				processProject(p);
			}
		}
	}

	private static void processProject (final File p) {
		L.d("processing", p);
	}

}

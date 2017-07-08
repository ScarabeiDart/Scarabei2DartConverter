
package com.jfixby.scarabei.scarabei2dart.api;

import java.io.IOException;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FilesList;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.util.path.RelativePath;
import com.jfixby.scarabei.scarabei2dart.CFG;

public class ConvertScarabeiAPI {

	private static final String DAANUU_XML = "daanuu.xml";
	private static final String SETTINGS_GRADLE = "settings.gradle";
	private static final String SCARABEI_PREFIX = "scarabei-api";
	private static final String PUBSEC = "pubspec.yaml";

	public static void main (final String[] a) throws IOException {
		ScarabeiDesktop.deploy();

		final File input = LocalFileSystem.newFile(CFG.inputPathString);
		final File output = LocalFileSystem.newFile(CFG.outputPathString);
		final ProcessingArguments args = new ProcessingArguments();
		args.templateFolder = LocalFileSystem.ApplicationHome().child("template");
		args.output = output;
		args.versionString = "1.0.0";
		final FilesList projects = input.listAllChildren(file -> {
			try {
				return file.isFolder() && file.getName().toLowerCase().startsWith(SCARABEI_PREFIX);
			} catch (final IOException e) {
				e.printStackTrace();
				return false;
			}
		});

		convertProjects(projects, args);

// projects.print("input");
// output.listAllChildren().print("output");
	}

	private static void convertProjects (final FilesList projects, final ProcessingArguments args) throws IOException {

		final List<String> processedProjects = Collections.newList();
		for (final File p : projects) {
			if (p.getName().equals(SCARABEI_PREFIX)) {
				args.project = p;
				processProject(args);
				processedProjects.add(args.outputProjectName);
			}
		}
		final File settingsGradle = args.output.child(SETTINGS_GRADLE);
		final StringBuilder b = new StringBuilder();
		for (final String p : processedProjects) {
			b.append("include \"" + p + "\"");
			b.append("\n");
		}
		L.d("writing", settingsGradle);
		L.d("       ", b);
		settingsGradle.writeString(b.toString());
	}

	private static void processProject (final ProcessingArguments args) throws IOException {
// L.d("processing", args.project);

		final File inputSources = args.project.child("src");
		String outputProjectName = args.project.getName();
		outputProjectName = outputProjectName.replaceFirst("scarabei-", "scarabei-dart-");
		final File outputProject = args.output.child(outputProjectName);

		args.outputProjectName = outputProjectName;
		args.outputProject = outputProject;
		L.d("processing", args.project);
		L.d("        to", args.outputProject);

		outputProject.delete();
		outputProject.makeFolder();

// final File outputSources = args.outputProject.child("src");
		{
			final File danuuXmlOutput = outputProject.child(DAANUU_XML);
			final File danuuXmlInput = args.templateFolder.child(DAANUU_XML);

			danuuXmlOutput.getFileSystem().copyFileToFile(danuuXmlInput, danuuXmlOutput);
		}

		{
			final File pubsecFile = outputProject.child(PUBSEC);
			final FileWriter fw = new FileWriter();
			fw.addLine("name: " + outputProjectName.replaceAll("-", "_"));
			fw.addLine("description: " + "");
			fw.addLine("version: " + args.versionString);
			fw.addLine("#homepage: " + "https://github.com/ScarabeiDart/");
			fw.addLine("#author: " + "J Fixby <dart@jfixby.com>");
			fw.addLine();
			fw.addLine("environment:");
			fw.addLine("  sdk: '>=1.20.1 <2.0.0'");
			fw.addLine();
			fw.addLine("dependencies:");
			fw.addLine();
			fw.addLine("dev_dependencies:");
			fw.addLine("  test: ^0.12.0");
			fw.addLine();
			fw.writeTo(pubsecFile);
		}

		{
			args.srcPrefix = inputSources.getAbsoluteFilePath().getRelativePath();
			final FilesList javaFiles = inputSources.listAllChildren(file -> file.extensionIs("java"));
			args.inputJavaFiles = javaFiles;
//
			processJavaFiles(args);

		}

	}

	private static void processJavaFiles (final ProcessingArguments args) throws IOException {

		final File libFolder = args.outputProject.child("lib");
		libFolder.makeFolder();

// args.inputJavaFiles.print("javaFiles");
		for (final File java : args.inputJavaFiles) {
			final RelativePath postfix = java.getAbsoluteFilePath().getRelativePath().splitAt(args.srcPrefix.size());

			final File outputDartFile = libFolder.proceed(postfix).parent().child(java.nameWithoutExtension() + ".dart");

			L.d("converting", java);
			L.d("        to", outputDartFile);

			final String javaCode = java.readToString();
			final String dartCode = convert(args, javaCode);
			outputDartFile.writeString(dartCode);
		}
	}

	private static String convert (final ProcessingArguments args, String javaCode) {

		javaCode = javaCode.replaceAll("public", "");
		javaCode = javaCode.replaceAll("static", "");

		return javaCode;
	}

}

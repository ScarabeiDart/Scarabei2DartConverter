
package com.jfixby.scarabei.scarabei2dart.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FilesList;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.util.path.RelativePath;

public class ConvertScarabeiAPI {

	private static final String DAANUU_XML = "daanuu.xml";
	private static final String SETTINGS_GRADLE = "settings.gradle";
	private static final String SCARABEI_PREFIX = "scarabei-";
	private static final String PUBSEC = "pubspec.yaml";
	private static final String BUILD_GRADLE = "build.gradle";

	public static void main (final String[] a) throws IOException {
		ScarabeiDesktop.deploy();

		final List<String> reposToProcess = Collections.newList();

		reposToProcess.add("D:\\\\[DEV]\\\\[GIT-4]\\\\Scarabei\\\\");
		reposToProcess.add("D:\\\\[DEV]\\\\[GIT-3]\\\\ScarabeiRed\\\\");
		reposToProcess.add("D:\\\\[DEV]\\\\[GIT-3]\\\\ScarabeiGson\\\\");
		reposToProcess.add("D:\\\\[DEV]\\\\[GIT-3]\\\\ScarabeiAWS\\\\");
		reposToProcess.add("D:\\\\[DEV]\\\\[GIT-3]\\\\ScarabeiAWS\\\\");

// final File output = LocalFileSystem.ApplicationHome().child("out");
		final File output = LocalFileSystem.ApplicationHome().parent();
		final ProcessingArguments args = new ProcessingArguments();
		args.templateFolder = LocalFileSystem.ApplicationHome().child("template");
		args.output = output;
		args.versionString = "1.0.0";

		processRepos(args, reposToProcess);

// final File output = LocalFileSystem.newFile(CFG.outputPathString);

// projects.print("input");
// output.listAllChildren().print("output");
	}

	private static void processRepos (final ProcessingArguments args, final List<String> reposToProcess) throws IOException {

		final List<File> projects = Collections.newList();
		for (final String repo : reposToProcess) {
			final File input = LocalFileSystem.newFile(repo);
			final FilesList pjs = input.listAllChildren(file -> {
				try {
					final boolean isScarabeiProject = file.isFolder() && file.getName().toLowerCase().startsWith(SCARABEI_PREFIX);
					return isScarabeiProject && process(file);
				} catch (final IOException e) {
					e.printStackTrace();
					return false;
				}
			});
			projects.addAll(pjs);

		}
		convertProjects(projects, args);
	}

	private static boolean process (final File file) {
		final String name = file.getName();
		if (name.contains("api")) {
			return true;
		}
		if (name.contains("red")) {
			return true;
		}
		return false;
	}

	private static void convertProjects (final List<File> projects, final ProcessingArguments args) throws IOException {

		projects.print("toPorcess");

		final List<String> processedProjects = Collections.newList();
		for (final File p : projects) {
			if (true) {
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

// final File inputSources = args.project.child("src").child("com").child("jfixby").child("scarabei").child("api");
		final File inputSources = args.project.child("src").child("com").child("jfixby").child("scarabei");
		String outputProjectName = args.project.getName();
		outputProjectName = outputProjectName.replaceFirst("scarabei-", "scarabei-dart-");
		final File outputProject = args.output.child(outputProjectName);

		final File gradleFileIn = args.project.child(BUILD_GRADLE);
		final File gradleFileOut = outputProject.child(BUILD_GRADLE);

		gradleFileIn.getFileSystem().copyFileToFile(gradleFileIn, gradleFileOut);

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
			final File outputJavaFile = libFolder.proceed(postfix);

			L.d("converting", java);
			L.d("        to", outputDartFile);

			try {
				final String javaCode = java.readToString();
				outputJavaFile.writeString(javaCode);
				final String dartCode = convert(args, javaCode);
				outputDartFile.writeString(dartCode);
			} catch (final IOException e) {
				L.d(e);
// Sys.exit();
			}

		}
	}

	private static String convert (final ProcessingArguments args, final String javaCode) throws IOException {

// javaCode = javaCode.replaceAll("public", "");
// javaCode = javaCode.replaceAll("static", "");

// final ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd \"C:\\Program Files\\Microsoft SQL Server\" && dir");
// final ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "ping 8.8.8.8");
// execute("ping 8.8.8.8");
// execute("dir dartConverter\\lib");
		final File tmpJava = LocalFileSystem.ApplicationHome().child("input.java");
		final File dartResult = LocalFileSystem.ApplicationHome().child("output.dart");
		tmpJava.writeString(javaCode);
		execute("dart dartConverter\\lib\\java2dart.dart");
		tmpJava.delete();
// execute("dir");
		final String result = dartResult.readToString();
		dartResult.delete();
// Sys.exit();

		return result;
	}

	private static void execute (final String cmd) throws IOException {
		final ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);
		builder.redirectErrorStream(true);
		final Process p = builder.start();
		final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while (true) {
			line = r.readLine();
			if (line == null) {
				break;
			}
			System.out.println(line);
		}
	}

}


package com.jfixby.scarabei.scarabei2dart.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.FilesList;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.util.path.RelativePath;

public class ConvertLocal {

	private static final String DAANUU_XML = "daanuu.xml";
	private static final String SETTINGS_GRADLE = "settings.gradle";
	private static final String SCARABEI_PREFIX = "scarabei-";
	private static final String PUBSEC = "pubspec.yaml";
	private static final String BUILD_GRADLE = "build.gradle";

	public static void main (final String[] a) throws IOException {
		ScarabeiDesktop.deploy();

		final File output = LocalFileSystem.ApplicationHome().child("out");
		final File input = LocalFileSystem.ApplicationHome().child("in");
// final File output = LocalFileSystem.ApplicationHome().parent();
		final ProcessingArguments args = new ProcessingArguments();
		args.templateFolder = LocalFileSystem.ApplicationHome().child("template");
		args.output = output;
		args.versionString = "1.0.0";
		args.input = input;

// args.output.clearFolder();

		processJavaFiles(args);

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
		if (name.endsWith("i-api")) {
			return true;
		}
		if (name.endsWith("i-red")) {
			return true;
		}
		return false;
	}

	private static void convertProjects (final List<File> projects, final ProcessingArguments args) throws IOException {

		projects.print("toPorcess");

		final List<String> processedProjects = Collections.newList("scarabei-2dart");
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

// gradleFileIn.getFileSystem().copyFileToFile(gradleFileIn, gradleFileOut);

		args.outputProjectName = outputProjectName;
		args.outputProject = outputProject;
		L.d("processing", args.project);
		L.d("        to", args.outputProject);

		outputProject.delete();
		outputProject.makeFolder();
		{
			final File gradleTemplate = args.templateFolder.child(BUILD_GRADLE);
			final String gradleTemplateString = gradleTemplate.readToString();
			final String gradleString = gradleFileIn.readToString();
			final File gradleFileOut = outputProject.child(BUILD_GRADLE);
			gradleFileOut.writeString(gradleTemplateString + "\n" + gradleString);
		}
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

		args.inputJavaFiles = args.input.listAllChildren(file -> file.extensionIs("java"));
		args.srcPrefix = args.input.getAbsoluteFilePath().getRelativePath();
// args.inputJavaFiles.print("javaFiles");
		for (final File java : args.inputJavaFiles) {
			final RelativePath postfix = java.getAbsoluteFilePath().getRelativePath().splitAt(args.srcPrefix.size());

			String dartFileName = java.nameWithoutExtension();
			dartFileName = replaceCapitalsWithUnderscore(dartFileName);

			final File outputDartFile = args.output.proceed(postfix).parent().child(dartFileName + ".dart");
			final File outputJavaFile = args.output.proceed(postfix);

			if (outputDartFile.exists()) {
				continue;
			}
			L.d("------------------------------------------------------------------------------------------------");
			L.d("converting", java);
			L.d("        to", outputDartFile);

			try {

				final String javaCode = java.readToString();
// outputJavaFile.writeString(javaCode);
				final String dartCode = convert(args, javaCode);
				outputDartFile.writeString(dartCode);
			} catch (final IOException e) {
				L.d(e);
				L.d("converting", java);
				L.d("        to", outputDartFile);
				Sys.exit();
			}

		}
	}

	private static String replaceCapitalsWithUnderscore (String string) {

		string = string.replaceAll("API", "Api");
		string = string.replaceAll("ID", "Id");
		string = string.replaceAll("IO", "Io");
		string = string.replaceAll("GZip", "Gzip");
		string = string.replaceAll("MD5", "Md5");
		string = string.replaceAll("WSClient", "Wsclient");
		string = string.replaceAll("JUtils", "Jutils");
		string = string.replaceAll("2D", "2d");
		string = string.replaceAll("3D", "3d");
		string = string.replaceAll("OS", "Os");
		string = string.replaceAll("URL", "Url");
		string = string.replaceAll("HTTPS", "Https");
		string = string.replaceAll("HTTP", "Http");
		string = string.replaceAll("RSA", "Rsa");
		string = string.replaceAll("SSL", "Ssl");

		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			final char c = string.charAt(i);

			if (i != 0) {
				final char b = string.charAt(i - 1);
				if (isCapital(c) && isCapital(b)) {
					Err.reportError("string=" + string);
				}
			}

			if (isCapital(c) && i != 0) {
				builder.append("_" + (c + "").toLowerCase());

			} else {
				builder.append((c + "").toLowerCase());
			}

		}
		final String result = builder.toString();
		return result;
	}

	private static boolean isCapital (final char c) {
		if (Character.isDigit(c)) {
			return false;
		}
		if (Character.isUpperCase(c)) {
			return true;
		}
// final String d = c + "";
// final String upper = d.toUpperCase();
// return upper.equals(d);
		return false;
	}

	private static String convert (final ProcessingArguments args, String javaCode) throws IOException {

// javaCode = javaCode.replaceAll("public", "");
// javaCode = javaCode.replaceAll("static", "");

		javaCode = javaCode.replaceAll("final ", " ");
		javaCode = javaCode.replaceAll("public", " ");
		javaCode = javaCode.replaceAll("private", " ");

		javaCode = javaCode.replaceAll(" long ", " int ");

		javaCode = javaCode.replaceAll("@Override", "");
		javaCode = javaCode.replaceAll("\\? extends", "");
		javaCode = javaCode.replaceAll("\\? super", "");
		javaCode = javaCode.replaceAll("<T>", " ");
		javaCode = javaCode.replaceAll("<E>", " ");
		javaCode = javaCode.replaceAll("T extends", " ");
		javaCode = javaCode.replaceAll("java.io.File", "DartFile");
		javaCode = javaCode.replaceAll("java.util.List", "List");
		javaCode = javaCode.replaceAll("java.util.Map", "Map");
		javaCode = javaCode.replaceAll("java.util.Collection", "DartCollection");
		javaCode = javaCode.replaceAll("java.lang.Math", "DartMath");

		javaCode = javaCode.replaceAll("<\\?>", "");
		javaCode = javaCode.replaceAll(";;", ";");

		javaCode = javaCode.replaceAll("\\(float\\)", "");
		javaCode = javaCode.replaceAll("\\(int\\)", "");
		javaCode = javaCode.replaceAll("\\(long\\)", "");

		javaCode = javaCode.replaceAll("<\\?, \\?>", "");
		javaCode = javaCode.replaceAll("0d", "0");
		javaCode = javaCode.replaceAll("1d", "1");
		javaCode = javaCode.replaceAll("2d", "2");

		javaCode = javaCode.replaceAll("interface", " abstract class ");

//

		javaCode = javaCode.replaceAll("java.io.OutputStream", "DartOutputStream");

		javaCode = javaCode.replaceAll("@SuppressWarnings", "//@SuppressWarnings");
		javaCode = javaCode.replaceAll("import", "//import");
		javaCode = javaCode.replaceAll("package", "//package");

		if (false) {
			// final ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd \"C:\\Program Files\\Microsoft SQL Server\" &&
			// dir");
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
		}

		return javaCode;
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

package thosakwe.java2dart;

import org.apache.commons.cli.*;
import thosakwe.java2dart.codegen.dart.DartLibrary;
import thosakwe.java2dart.transpiler.JavaToDartLiteTranspiler;
import thosakwe.java2dart.transpiler.JavaToDartTranspiler;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Options opts = cliOptions();
            CommandLine commandLine = new DefaultParser().parse(opts, args);

            if (commandLine.hasOption("help")) {
                printUsage();
                return;
            }

            List<String> rest = commandLine.getArgList();

            if (rest.isEmpty()) {
                printUsage();
                System.exit(1);
            }

            String filename = rest.get(0);
            JavaToDartLiteTranspiler transpiler = new JavaToDartLiteTranspiler(commandLine, filename);
            transpiler.visitCompilationUnit(Utils.parseCompilationUnit(new File(filename)));
            String compiled = transpiler.getBuilder().toString();
            PrintStream out;

            if (commandLine.hasOption("out")) {
                out = new PrintStream(commandLine.getOptionValue("out"));
            } else out = System.out;

            out.println(compiled);


            /* JavaToDartTranspiler transpiler = new JavaToDartTranspiler(commandLine, filename);
            DartLibrary library = transpiler.transpile(filename);

            if (commandLine.hasOption("out")) {
                library.saveToFile(new File(commandLine.getOptionValue("out")));
            } else library.saveToFile();*/
        } catch (ParseException e) {
            printUsage();
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static Options cliOptions() {
        return new Options()
                .addOption("h", "help", false, "Print this help information.")
                .addOption("o", "out", true, "The output filename.")
                .addOption(Option.builder()
                        .longOpt("verbose")
                        .desc("Print verbose output.")
                        .build());
    }

    private static void printUsage() {
        new HelpFormatter().printHelp("java2dart [options..] <filename>", cliOptions());
    }
}

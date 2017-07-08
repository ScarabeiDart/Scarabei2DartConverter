package thosakwe.java2dart.transpiler;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import org.apache.commons.cli.CommandLine;
import thosakwe.java2dart.codegen.dart.DartClass;
import thosakwe.java2dart.codegen.dart.DartFunction;
import thosakwe.java2dart.codegen.dart.DartFunctionParam;
import thosakwe.java2dart.codegen.dart.DartLibrary;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JavaToDartTranspiler {
    private final CommandLine commandLine;
    private final String filename;

    public JavaToDartTranspiler(CommandLine commandLine, String filename) {
        this.commandLine = commandLine;
        this.filename = filename;
    }

    private void printDebug(String msg) {
        if (commandLine.hasOption("verbose")) {
            System.out.println(msg);
        }
    }

    private void printDebug(String format, Object... args) {
        printDebug(String.format(format, args));
    }

    private boolean classOnlyContainsStatic(TypeDeclaration clazz) {
        // Todo: Support more than just fields and methods...

        for (BodyDeclaration member : clazz.getMembers()) {
            if (member instanceof FieldDeclaration) {
                if (!ModifierSet.isStatic(((FieldDeclaration) member).getModifiers()))
                    return false;
            } else if (member instanceof MethodDeclaration) {
                if (!ModifierSet.isStatic(((MethodDeclaration) member).getModifiers()))
                    return false;
            }
        }

        return true;
    }

    public DartLibrary transpile(String filename) throws IOException, ParseException {
        InputStream in = new FileInputStream(filename);
        CompilationUnit ast = JavaParser.parse(in);
        DartLibrary lib = new DartLibrary(filename);
        in.close();

        // Get name, if any
        if (ast.getPackage() != null) {
            lib.setName(ast.getPackage().getPackageName());
        }

        for (TypeDeclaration clazz : ast.getTypes()) {
            // Check if class only contains static members
            if (classOnlyContainsStatic(clazz)) {
                for (BodyDeclaration member : clazz.getMembers()) {
                    if (member instanceof MethodDeclaration) {
                        DartFunction function = transpileMethod((MethodDeclaration) member);
                        function.setStatic(false);
                        lib.getTopLevelDefinitions().add(function);
                    }
                }
            } else {
                DartClass dartClass = transpileClass(clazz);
                lib.getTopLevelDefinitions().add(dartClass);
            }
        }

        return lib;
    }

    private DartClass transpileClass(TypeDeclaration clazz) {
        DartClass dartClass = new DartClass(clazz.getName());
        return dartClass;
    }

    private DartFunction transpileMethod(MethodDeclaration method) {
        DartFunction function = new DartFunction(method.getName());
        function.setStatic(ModifierSet.isStatic(method.getModifiers()));

        // Todo: Type mappings on return types
        function.setReturnType(method.getType().toString());

        for (Parameter parameter : method.getParameters()) {
            DartFunctionParam dartParam = new DartFunctionParam(parameter.getName());

            // Todo: Type mappings on params
            dartParam.setType(parameter.getType().toString());
            function.getParams().add(dartParam);
        }

        return function;
    }
}

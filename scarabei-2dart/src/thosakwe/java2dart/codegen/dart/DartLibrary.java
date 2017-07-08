package thosakwe.java2dart.codegen.dart;

import org.apache.commons.io.FilenameUtils;
import thosakwe.java2dart.Utils;
import thosakwe.java2dart.codegen.CodeBuilder;
import thosakwe.java2dart.codegen.Compilable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class DartLibrary extends Compilable {
    private final String filename;
    private String name = null;
    private final List<Compilable> topLevelDefinitions = new ArrayList<>();

    public DartLibrary(String filename) {
        this.filename = filename;
    }

    public List<Compilable> getTopLevelDefinitions() {
        return topLevelDefinitions;
    }

    public String getFilename() {
        return filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void compile(CodeBuilder builder) {
        if (name != null)
            builder.printf("library %s;%n", name);

        for (Compilable def : topLevelDefinitions) {
            builder.println();
            def.compile(builder);
        }
    }

    public void saveToFile() throws FileNotFoundException {
        String dir = FilenameUtils.getFullPathNoEndSeparator(getFilename());
        String basename = Utils.convertToCamelCase(FilenameUtils.getBaseName(filename));
        saveToFile(new File(FilenameUtils.concat(dir, basename + ".dart")));
    }

    public void saveToFile(File file) throws FileNotFoundException {
        saveToFile(new PrintStream(file));
    }

    private void saveToFile(PrintStream stream) {
        CodeBuilder builder = new CodeBuilder();
        compile(builder);
        String generated = builder.toString().trim();
        stream.println(generated);
    }
}

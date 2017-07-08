package thosakwe.java2dart.codegen.dart;

import thosakwe.java2dart.codegen.CodeBuilder;
import thosakwe.java2dart.codegen.Compilable;

import java.util.ArrayList;
import java.util.List;

public class DartClass extends Compilable {
    private boolean isAbstract;
    private final List<Compilable> members = new ArrayList<>();
    private final String name;

    public DartClass(String name) {
        this.name = name;
    }

    public List<Compilable> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    @Override
    public void compile(CodeBuilder builder) {
        if (isAbstract)
            builder.write("abstract ");

        builder.printf("class %s {%n", getName()).indent();
        builder.outdent().println("}");
    }
}

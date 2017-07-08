package thosakwe.java2dart.codegen.dart;

import thosakwe.java2dart.Utils;
import thosakwe.java2dart.codegen.CodeBuilder;
import thosakwe.java2dart.codegen.Compilable;

import java.util.ArrayList;
import java.util.List;

public class DartFunction extends Compilable {
    private final String name;
    private boolean isStatic;
    private final List<DartFunctionParam> params = new ArrayList<>();
    private final List<Compilable> statements = new ArrayList<>();
    private String returnType = null;

    public DartFunction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<DartFunctionParam> getParams() {
        return params;
    }

    public String getReturnType() {
        return returnType;
    }


    public List<Compilable> getStatements() {
        return statements;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public void compile(CodeBuilder builder) {
        if (returnType != null)
            builder.writef("%s ", returnType);

        builder.printf("%s(", name);

        for (int i = 0; i < params.size(); i++) {
            if (i > 0)
                builder.write(", ");

            DartFunctionParam param = params.get(i);

            if (param.getType() != null)
                builder.writef("%s ", Utils.convertToDartType(param.getType()));
            builder.write(param.getName());
        }

        builder.println(") {").indent();

        for (Compilable statement : statements) {
            statement.compile(builder);
        }

        builder.outdent().println("}");
    }
}

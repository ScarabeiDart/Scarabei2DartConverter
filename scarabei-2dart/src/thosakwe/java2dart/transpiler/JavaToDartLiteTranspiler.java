package thosakwe.java2dart.transpiler;

import org.apache.commons.cli.CommandLine;
import thosakwe.java2dart.antlr.Java8BaseVisitor;
import thosakwe.java2dart.antlr.Java8Parser;
import thosakwe.java2dart.codegen.CodeBuilder;

public class JavaToDartLiteTranspiler extends Java8BaseVisitor {
    private final CodeBuilder builder = new CodeBuilder();
    private final CommandLine commandLine;
    private final String filename;

    public JavaToDartLiteTranspiler(CommandLine commandLine, String filename) {
        this.commandLine = commandLine;
        this.filename = filename;
    }

    public CodeBuilder getBuilder() {
        return builder;
    }

    @Override
    public Object visitClassBody(Java8Parser.ClassBodyContext ctx) {
        // Todo: All declarations. Afterwards, remove this stub
        return super.visitClassBody(ctx);
    }

    @Override
    public String visitClassType(Java8Parser.ClassTypeContext ctx) {
        CodeBuilder builder = new CodeBuilder();
        if (ctx.classOrInterfaceType() != null)
            return "ERROR: Nested classes are not supported.";

        if (ctx.typeArguments() != null) {
            builder.write("<");

            for (int i = 0; i < ctx.typeArguments().typeArgumentList().typeArgument().size(); i++) {
                Java8Parser.TypeArgumentContext typeArg = ctx.typeArguments().typeArgumentList().typeArgument(i);

                if (i > 0)
                    builder.write(",");

                if (typeArg.wildcard() != null)
                    builder.write("dynamic");
                else {
                    Java8Parser.ReferenceTypeContext ref = typeArg.referenceType();
                    builder.write(visitReferenceType(ref));
                }
            }

            builder.write(">");
        }

        return builder.toString();
    }

    @Override
    public Object visitFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
        return super.visitFieldDeclaration(ctx);
    }

    @Override
    public Object visitImportDeclaration(Java8Parser.ImportDeclarationContext ctx) {
        // Todo: Import
        return super.visitImportDeclaration(ctx);
    }

    @Override
    public Object visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        return super.visitMethodDeclaration(ctx);
    }

    @Override
    public Object visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        builder.println();
        boolean isAbstract = false;
        String name = ctx.Identifier().getText();

        for (Java8Parser.ClassModifierContext modifier : ctx.classModifier()) {
            if (modifier.annotation() != null)
                visitAnnotation(modifier.annotation());
            else if (modifier.getText().equals("abstract")) {
                isAbstract = true;
            }
        }

        if (isAbstract)
            builder.printf("abstract class %s ", name);
        else
            builder.printf("class %s ", name);

        if (ctx.typeParameters() != null) {
            builder.write("<");

            for (int i = 0; i < ctx.typeParameters().typeParameterList().typeParameter().size(); i++) {
                if (i > 0)
                    builder.write(",");
                builder.write(ctx.typeParameters().typeParameterList().typeParameter(i).getText());
            }

            builder.write("> ");
        }

        if (ctx.superclass() != null)
            builder.writef("extends %s ", visitClassType(ctx.superclass().classType()));

        if (ctx.superinterfaces() != null) {
            builder.write("implements ");

            for (int i = 0; i < ctx.superinterfaces().interfaceTypeList().interfaceType().size(); i++) {
                if (i > 0)
                    builder.write(",");
                Java8Parser.InterfaceTypeContext type = ctx.superinterfaces().interfaceTypeList().interfaceType(i);
                builder.write(visitClassType(type.classType()));
            }
        }

        builder.println("{");
        visitClassBody(ctx.classBody());
        builder.outdent().println("}");
        return null;
    }

    @Override
    public Object visitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        builder.print("library ");

        for (int i = 0; i < ctx.Identifier().size(); i++) {
            if (i > 0)
                builder.write(".");
            builder.write(ctx.Identifier(i).getText());
        }

        builder.writeln(";");
        return super.visitPackageDeclaration(ctx);
    }

    @Override
    public Object visitReferenceType(Java8Parser.ReferenceTypeContext ctx) {
        // Todo: referenceType
        return super.visitReferenceType(ctx);
    }
}

package thosakwe.java2dart.codegen;

import java.io.StringWriter;

public class CodeBuilder extends StringWriter {
    private int indentationLevel = 0;

    public CodeBuilder applyTabs() {
        for (int i = 0; i < indentationLevel; i++)
            write("  ");
        return this;
    }

    public CodeBuilder indent() {
        if (indentationLevel < 0)
            indentationLevel = 1;
        else indentationLevel++;
        return this;
    }

    public CodeBuilder outdent() {
        indentationLevel--;
        return this;
    }

    public CodeBuilder write(Object object) {
        write(String.valueOf(object));
        return this;
    }

    public CodeBuilder writef(String format, Object... args) {
        write(String.format(format.replaceAll("%n", "\n"), args));
        return this;
    }

    public CodeBuilder writeln(Object object) {
        write(object);
        return println();
    }

    public CodeBuilder print(Object object) {
        applyTabs();
        return write(object);
    }

    public CodeBuilder println() {
        write("\n");
        return this;
    }

    public CodeBuilder println(Object object) {
        applyTabs();
        write(object);
        return println();
    }

    public CodeBuilder resetIndentation() {
        indentationLevel = 0;
        return this;
    }

    public CodeBuilder printf(String format, Object... args) {
        return print(String.format(format.replaceAll("%n", "\n"), args));
    }
}
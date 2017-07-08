package thosakwe.java2dart;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import thosakwe.java2dart.antlr.Java8Lexer;
import thosakwe.java2dart.antlr.Java8Parser;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Utils {
    private static Pattern BRACKETS = Pattern.compile(".+\\[]$");

    public static String convertToCamelCase(String str) {
        StringBuilder buf = new StringBuilder();
        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];

            if (ch == ' ' | ch == '-') {
                buf.append('_');
            } else if (Character.isUpperCase(ch)) {
                buf.append(Character.toLowerCase(ch));
            } else {
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    public static String convertToDartType(String type) {
        Matcher matcher = BRACKETS.matcher(type);

        if (!matcher.matches()) {
            return type;
        } else {
            String innerType = type.replaceFirst("\\[]$", "");
            return String.format("List<%s>", convertToDartType(innerType));
        }
    }

    public static Java8Parser.CompilationUnitContext parseCompilationUnit(File file) throws IOException {
        return parseCompilationUnit(new FileInputStream(file));
    }

    public static Java8Parser.CompilationUnitContext parseCompilationUnit(InputStream inputStream) throws IOException {
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(inputStream);
        Java8Lexer lexer = new Java8Lexer(antlrInputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokenStream);
        return parser.compilationUnit();
    }
}

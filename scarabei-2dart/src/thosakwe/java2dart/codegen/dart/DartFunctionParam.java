package thosakwe.java2dart.codegen.dart;

public class DartFunctionParam {
    private final String name;
    private String type;

    public DartFunctionParam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

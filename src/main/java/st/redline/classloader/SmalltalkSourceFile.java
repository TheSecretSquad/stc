package st.redline.classloader;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmalltalkSourceFile implements Source, LineTransformer {

    private static final Pattern METHOD_START_PATTERN = Pattern.compile("^[-+] .*\\s");
    private static final Pattern METHOD_UNARY_PATTERN = Pattern.compile("^\\w+[^:|\\s]");
    private static final Pattern METHOD_BINARY_PATTERN = Pattern.compile("^[-\\\\+*/=><,@%~|&?]+ \\w+");
    private static final String METHOD_START = "[";
    private static final String METHOD_END = "].";
    private static final String CLASS_SELECTOR = "class";
    private static final String METHOD_AT_SELECTOR = "methodAt:";
    private static final String METHOD_PUT_SELECTOR = "put:";
    private static final String CLASS_METHOD_INDICATOR = "+ ";
    private static final String NEWLINE = System.getProperty("line.separator");

    private final String name;
    private final String filename;
    private final File file;
    private final SourceReader reader;
    private boolean methods;
    private String className;
    private String selector;
    private String arguments;

    public SmalltalkSourceFile(String name, String filename, File file, SourceReader reader) {
        this.name = name;
        this.filename = filename;
        this.file = file;
        this.reader = reader;
        this.methods = false;
    }

    public boolean hasContent() {
        return file.length() > 0;
    }

    public String contents() {
        return reader.contents(this);
    }

    public String transform(String line) {
        if (isMethodDefinition(line))
            return methodDefinitionTransformation(line);
        return line;
    }

    private String methodDefinitionTransformation(String line) {
        StringBuffer transformation = new StringBuffer();
        if (hasMethods())
            transformation.append(METHOD_END + " ");
        transformation.append(className());
        if (isClassMethod(line))
            transformation.append(" " + CLASS_SELECTOR);
        transformation.append(" " + METHOD_AT_SELECTOR);
        extractSelectorAndArgumentsFrom(line);
        transformation.append(" #" + selector());
        transformation.append(" " + METHOD_PUT_SELECTOR);
        transformation.append(" " + METHOD_START);
        if (arguments.length() > 0)
            transformation.append(" " + arguments + " |");
        transformation.append(NEWLINE);
        methods = true;
        return transformation.toString();
    }

    private String selector() {
        return selector;
    }

    private void extractSelectorAndArgumentsFrom(String line) {
        String input = line;
        if (input.startsWith("+ ") || input.startsWith("- "))
            input = input.substring(2);
        arguments = "";
        if (!isUnarySelector(input) && !isBinarySelector(input))
            selector = "UnknownSelector";
    }

    private boolean isUnarySelector(String input) {
        Matcher matcher = METHOD_UNARY_PATTERN.matcher(input);
        if (matcher.find()) {
            selector = matcher.group();
            return true;
        }
        return false;
    }

    private boolean isBinarySelector(String input) {
        System.out.println("Binary:" + input + ":");
        Matcher matcher = METHOD_BINARY_PATTERN.matcher(input);
        if (matcher.find()) {
            selector = matcher.group();
            int space = selector.indexOf(' ');
            arguments = ":" + selector.substring(space + 1);
            selector = selector.substring(0, space);
            return true;
        }
        return false;
    }

    private boolean isClassMethod(String line) {
        return line.startsWith(CLASS_METHOD_INDICATOR);
    }

    private String className() {
        if (className == null)
            className = file.getName().substring(0, file.getName().lastIndexOf("."));
        return className;
    }

    private boolean isMethodDefinition(String line) {
        return METHOD_START_PATTERN.matcher(line).matches();
    }

    public String begin() {
        return "";
    }

    public String end() {
        return hasMethods() ? METHOD_END + "\n" : "";
    }

    private boolean hasMethods() {
        return methods;
    }
}

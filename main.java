import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Main {
    public static void main(String[] args) throws Exception {
        String input = "x = 1;\n" +
                       "if (x > 0) {\n" +
                       "  y = 2;\n" +
                       "} else {\n" +
                       "  y = 3;\n" +
                       "}\n" +
                       "while (x < 10) {\n" +
                       "  x = x + 1;\n" +
                       "  print(x);\n" +
                       "}\n" +
                       "function add(x, y) {\n" +
                       "  return x + y;\n" +
                       "}\n" +
                       "print(add(1, 2));\n";

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        AstNode rootNode = parser.parse();

        String javaCode = CodeGenerator.generate(rootNode);

        File file = new File("Main.java");
        FileWriter writer = new FileWriter(file);
        writer.write(javaCode);
        writer.close();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
        compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();

        Class<?> clazz = Class.forName("Main");
        Method method = clazz.getMethod("main", String[].class);
        method.invoke(null, (Object) null);
    }
}

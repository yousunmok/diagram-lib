package diagramlib.core.diagram;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class DiagramGenerator {

    public static void generateFor(String basePackage) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = basePackage.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            List<Class<?>> classes = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.toURI());
                for (File file : Objects.requireNonNull(directory.listFiles())) {
                    if (file.getName().endsWith(".class")) {
                        String className = basePackage + "." + file.getName().replace(".class", "");
                        classes.add(Class.forName(className));
                    }
                }
            }

            List<String> diagramLines = new ArrayList<>();
            for (Class<?> clazz : classes) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Diagram.class)) {
                        diagramLines.add("classDiagram");
                        diagramLines.add("    class " + clazz.getSimpleName() + " {");
                        diagramLines.add("        + " + method.getName() + "()");
                        diagramLines.add("    }");
                    }
                }
            }

            File output = new File("diagram.html");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
                writer.write("<html><body><pre class=\"mermaid\">\n");
                for (String line : diagramLines) writer.write(line + "\n");
                writer.write("</pre></body></html>");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
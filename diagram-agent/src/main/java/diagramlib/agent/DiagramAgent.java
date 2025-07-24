package diagramlib.agent;

import diagramlib.core.diagram.Diagram;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DiagramAgent {

    private static final List<String> mermaidLines = new ArrayList<>();

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[DiagramAgent] Agent started.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                for (Class<?> loadedClass : inst.getAllLoadedClasses()) {
                    // 시스템 클래스는 제외
                    String className = loadedClass.getName();
                    if (!inst.isModifiableClass(loadedClass)) continue;
                    if (className.startsWith("java.") || className.startsWith("sun.") || className.startsWith("jdk.")
                            || className.startsWith("javax.") || className.startsWith("diagramlib.agent")) {
                        continue;
                    }

                    for (Method method : loadedClass.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Diagram.class)) {
                            mermaidLines.add("    Start --> " + loadedClass.getSimpleName() + "." + method.getName() + "()");
                        }
                    }
                }

                if (!mermaidLines.isEmpty()) {
                    writeMermaidHtml(mermaidLines);
                } else {
                    System.out.println("[DiagramAgent] No diagram annotations found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    private static void writeMermaidHtml(List<String> lines) {
        try (PrintWriter out = new PrintWriter(new FileWriter("diagram.html"))) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("  <script type=\"module\" src=\"https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs\"></script>");
            out.println("</head>");
            out.println("<body>");
            out.println("  <pre class=\"mermaid\">");
            out.println("graph TD");
            for (String line : lines) {
                out.println(line);
            }
            out.println("  </pre>");
            out.println("</body>");
            out.println("</html>");
            System.out.println("[DiagramAgent] Mermaid diagram saved to diagram.html");
        } catch (Exception e) {
            System.err.println("[DiagramAgent] Failed to write HTML: " + e.getMessage());
        }
    }
}

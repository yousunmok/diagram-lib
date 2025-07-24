package diagramlib.agent;

import diagramlib.core.diagram.Diagram;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class DiagramAgent {

    private static final List<String> mermaidLines = new ArrayList<>();

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[DiagramAgent] Agent started.");

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(
                    ClassLoader loader,
                    String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer
            ) {
                if (className == null
                        || className.startsWith("java/")
                        || className.startsWith("javax/")
                        || className.startsWith("sun/")
                        || className.startsWith("diagramlib/agent")) {
                    return null;
                }

                try {
                    Class<?> cls = classBeingRedefined;
                    if (cls != null) {
                        for (Method method : cls.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(Diagram.class)) {
                                String methodName = method.getName();
                                mermaidLines.add("    Start --> " + methodName + "()");
                            }
                        }
                    }
                } catch (Throwable e) {
                    System.err.println("[DiagramAgent] Error: " + e.getMessage());
                }

                return null;
            }
        }, false);

        // JVM 종료 직전 Mermaid HTML 저장
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!mermaidLines.isEmpty()) {
                writeMermaidHtml(mermaidLines);
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

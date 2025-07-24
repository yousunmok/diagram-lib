package diagramlib.agent;

import java.lang.instrument.*;
import java.lang.reflect.Method;

public class DiagramAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("DiagramAgent started.");

        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            try {
                Class<?> cls = Class.forName(className.replace('/', '.'), false, loader);

                for (Method method : cls.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(diagramlib.core.diagram.Diagram.class)) {
                        String methodName = method.getName();
                        System.out.println("graph TD");
                        System.out.println("    Start --> " + methodName + "()");
                    }
                }
            } catch (Throwable ignored) {}
            return null;
        }, true);
    }
}

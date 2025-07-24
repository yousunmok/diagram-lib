package diagramlib.agent;

import diagramlib.core.diagram.Diagram;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

public class DiagramAgent {

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
                // 필터링: java.* / sun.* / agent 자기 자신 등은 제외
                if (className == null
                        || className.startsWith("java/")
                        || className.startsWith("javax/")
                        || className.startsWith("sun/")
                        || className.startsWith("diagramlib/agent")) {
                    return null;
                }

                try {
                    Class<?> cls = classBeingRedefined;

                    // 이미 로딩된 클래스만 탐색
                    if (cls != null) {
                        for (Method method : cls.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(Diagram.class)) {
                                String methodName = method.getName();
                                System.out.println("graph TD");
                                System.out.println("    Start --> " + methodName + "()");
                            }
                        }
                    }
                } catch (Throwable e) {
                    System.err.println("[DiagramAgent] Error during transform: " + e.getMessage());
                }

                return null; // 바이트코드 수정은 없음
            }
        }, false); // retransformable = false (JDK 8 이하 호환)
    }
}

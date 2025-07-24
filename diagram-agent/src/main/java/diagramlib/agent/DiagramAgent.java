package diagramlib.agent;

import java.lang.instrument.*;
import java.lang.reflect.Method;

public class DiagramAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("DiagramAgent started.");

        // `true` 제거 → 기본값은 `false`
        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            try {
                // '/' → '.' 로 바꿔서 클래스 이름 변환
                Class<?> cls = Class.forName(className.replace('/', '.'), false, loader);

                for (Method method : cls.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(diagramlib.core.diagram.Diagram.class)) {
                        String methodName = method.getName();
                        System.out.println("graph TD");
                        System.out.println("    Start --> " + methodName + "()");
                    }
                }
            } catch (Throwable ignored) {
                // 로깅 추가 권장
            }
            return null;
        });
    }
}

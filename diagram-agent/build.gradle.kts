plugins {
    id("java")
}

group = "com.github.yousunmok"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":diagram-core")) // Core 참조
}

tasks.jar {
    manifest {
        attributes(
                "Premain-Class" to "diagramlib.agent.DiagramAgent",
                "Can-Redefine-Classes" to "true"
        )
    }
}

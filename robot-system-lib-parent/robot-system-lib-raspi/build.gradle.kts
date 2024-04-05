description = "ARIG Association - Robot System Librairie - Raspberry Pi : Implémentation pour Raspberry Pi."

plugins {
    id("org.arig.robots.common-conventions")
}

dependencies {
    api(project(":robot-system-lib-parent:robot-system-lib-core"))
    api(libs.pi4j.core)
    api(libs.javacan.core) {
        artifact {
            classifier = "aarch64"
        }
    }
}

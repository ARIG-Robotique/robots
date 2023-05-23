description = "ARIG Association - Robot System Librairie - Bouchon : Implémentation bouchon pour tester sans devices."

plugins {
    id("org.arig.robots.common-conventions")
}

dependencies {
    api(project(":robot-system-lib-parent:robot-system-lib-core"))
    api(libs.apache.commons.io)
}

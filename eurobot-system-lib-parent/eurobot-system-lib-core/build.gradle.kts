description = "ARIG Association - Eurobot System Librairie - Core : Librairie métier."

plugins {
    id("org.arig.robots.common-conventions")
}

dependencies {
    api(project(":robot-system-lib-parent:robot-system-lib-core"))
}

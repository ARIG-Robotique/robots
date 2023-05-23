description = "ARIG Association - Eurobot System Librairie - Bouchon : Implémentation bouchon pour tester sans devices."

plugins {
    id("org.arig.robots.common-conventions")
}

dependencies {
    api(project(":eurobot-system-lib-parent:eurobot-system-lib-core"))
    api(project(":robot-system-lib-parent:robot-system-lib-bouchon"))
}

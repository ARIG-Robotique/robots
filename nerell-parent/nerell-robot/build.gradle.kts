import org.springframework.boot.gradle.tasks.bundling.BootJar

description = "ARIG Association - Nerell Robot"

plugins {
    alias(libs.plugins.spring.boot)
    id("org.arig.robots.common-conventions")
}

tasks.named<BootJar>("bootJar") {
    archiveClassifier.set("exec")
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("")
}

dependencies {
    api(project(":robot-system-lib-parent:robot-system-lib-raspi"))
    api(project(":nerell-parent:nerell-common"))
}

import org.springframework.boot.gradle.tasks.bundling.BootJar

description = "ARIG Association - Tinker Robot"

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
    implementation(project(":robot-system-lib-parent:robot-system-lib-raspi"))
    implementation(project(":robot-system-lib-parent:robot-system-lib-joycon"))
}

description = "ARIG Association - Robot System Librairie - Core : Librairie coeur du système."

plugins {
    id("org.arig.robots.common-conventions")
}

dependencies {
    api("org.apache.commons:commons-lang3")

    api(libs.apache.commons.collections)
    api(libs.apache.commons.math)
    api(libs.apache.commons.io)
    api(libs.google.guava)
    api(libs.junixsocket.common)
    api(libs.junixsocket.native.common)
    api(libs.anyangle.path.finding)
    api(libs.javacan.core)
    api(libs.javacan.epoll)

}

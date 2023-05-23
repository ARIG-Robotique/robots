description = "ARIG Association - Robot System Librairie - JoyCon : Utilisation d'interface Nintendo JoyCon."

plugins {
    id("org.arig.robots.common-conventions")
}

dependencies {
    api(libs.pure.java.hid.api)
}

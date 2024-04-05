description = "ARIG Association - Pami utils"

plugins {
	alias(libs.plugins.spring.boot)
	id("org.arig.robots.common-conventions")
}

dependencies {
	implementation(project(":pami-parent:pami-robot"))

	implementation(libs.spring.shell.starter)
}

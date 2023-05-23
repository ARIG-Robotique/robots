description = "ARIG Association - Nerell utils"

plugins {
	alias(libs.plugins.spring.boot)
	id("org.arig.robots.common-conventions")
}

dependencies {
	implementation(project(":nerell-parent:nerell-robot"))

	implementation(libs.spring.shell.starter)
}

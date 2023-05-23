description = "ARIG Association - Odin utils"

plugins {
	alias(libs.plugins.spring.boot)
	id("org.arig.robots.common-conventions")
}

dependencies {
	implementation(project(":odin-parent:odin-robot"))

	implementation(libs.spring.shell.starter)
}

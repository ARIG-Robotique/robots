description = "ARIG Association - Nerell Simulator"

plugins {
	alias(libs.plugins.spring.boot)
	id("org.arig.robots.common-conventions")
}

dependencies {
	implementation(project(":eurobot-system-lib-parent:eurobot-system-lib-bouchon"))
	implementation(project(":nerell-parent:nerell-common"))
}

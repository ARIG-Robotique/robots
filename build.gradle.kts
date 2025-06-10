description = "ARIG Association - Robots"

tasks.wrapper {
    description = "Generates gradlew[.bat] scripts"
    gradleVersion = "8.14"
    distributionType = Wrapper.DistributionType.ALL
}

// ********************* OLD ************************* //

// Configuration de tous les projets
/*
configure(allprojects) { project ->
	tasks.test {
		finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
	}
	tasks.jacocoTestReport {
		dependsOn(tasks.test) // tests are required to run before generating the report
	}

	tasks.jacocoTestReport {
		reports {
			xml.required.set(false)
			csv.required.set(false)
			html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
		}
	}
}
*/

// Configuration du projet racine
/*
configure(rootProject) {
	// Suppression des artéfact par défaut sur le rootProject (jar, etc...)
	configurations.archives.artifacts.clear()

	tasks.register('codeCoverageReport', JacocoReport) {
		executionData fileTree(project.rootDir.absolutePath).include("** /build/jacoco/*.exec")

		subprojects.each {
			sourceSets it.sourceSets.main
		}

		reports {
			xml.enabled true
			xml.destination file("${buildDir}/reports/jacoco/report.xml")
			html.enabled false
			csv.enabled false
		}
	}

	codeCoverageReport.dependsOn {
		subprojects*.test
	}
}
*/

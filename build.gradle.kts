import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("com.netflix.dgs.codegen") version "5.1.16"
	id("org.springframework.boot") version "2.6.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:latest.release"))
	implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
	testImplementation("io.kotest:kotest-runner-junit5-jvm:4.4.3")
	testImplementation("io.kotest:kotest-assertions-core-jvm:4.4.3")
	testImplementation("io.kotest:kotest-extensions-junitxml:4.4.3")
	testImplementation("io.kotest:kotest-extensions-spring-jvm:4.4.3")
	testImplementation("io.kotest:kotest-property-jvm:4.4.3")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

jacoco {
	toolVersion = "0.8.7"
}


tasks {
	generateJava {
		// List of directories containing schema files
		// https://stackoverflow.com/a/60859378/4086981
		schemaPaths = listOf("${projectDir}/src/main/resources/schema").toMutableList()
		packageName = "com.example.demodmesa" // The package name to use to generate sources
		generateClient = true // Enable generating the type safe query API
		language = "kotlin"
	}

	test {
		finalizedBy(jacocoTestReport) // report is always generated after tests run
	}

	jacocoTestReport {
		dependsOn(test) // tests are required to run before generating the report
		reports {
			xml.required.set(true)
			csv.required.set(false)
			html.required.set(true)
		}

	}

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<JacocoReport> {
	afterEvaluate {
		classDirectories.setFrom(files(classDirectories.files.map {
			fileTree(it).apply {
				include("com/example/demodmesa/datafetchers/**")
			}
		}))
	}
}

tasks.withType<JacocoCoverageVerification> {
	violationRules {
		rule {
			limit {
				minimum = BigDecimal(0.62)
			}
		}
	}

	afterEvaluate {
		classDirectories.setFrom(files(classDirectories.files.map {
			fileTree(it).apply {
				include("com/example/demodmesa/datafetchers/**")
			}
		}))
	}
}

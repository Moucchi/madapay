plugins {
	java
	id("org.springframework.boot") version "4.2.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.graalvm.buildtools.native") version "1.1.18"
}

group = "com.ETU001979"
version = "0.1"
description = "madapay-RAFIDINJANAHARY_Emmanuel"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.security:spring-security-core")
	implementation("org.springframework.security:spring-security-oauth2-jose")
	implementation("org.springframework.security:spring-security-config")
	implementation("org.springframework.security:spring-security-oauth2-resource-server")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform()
	inputs.property("testsLents", providers.environmentVariable("MADAPAY_TESTS_LENTS").orElse("0"))
}

tasks.bootBuildImage {
	imageName = "madapay-etu001979:0.1"
}

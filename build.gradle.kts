plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
		mavenBom("org.junit:junit-bom:5.9.2")
	}
}

dependencies {
    // Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

    // DB
	runtimeOnly("com.mysql:mysql-connector-j")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")

	// Database for test
	testImplementation("com.h2database:h2")

	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")

	testCompileOnly("org.projectlombok:lombok:1.18.30")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

	implementation("org.redisson:redisson-spring-boot-starter:3.23.5")

	testImplementation("org.springframework.security:spring-security-test")

	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// KafkaContainer 클래스가 들어있는 모듈
	implementation("org.springframework.kafka:spring-kafka")
	testImplementation("org.testcontainers:kafka")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
	testImplementation("org.apache.kafka:kafka-clients:3.8.0")
	implementation("org.springframework:spring-tx")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.apache.kafka:kafka-clients:3.5.1")
	testImplementation("org.apache.kafka:kafka_2.13:3.5.1")
	testImplementation("org.awaitility:awaitility:4.2.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}

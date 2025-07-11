val kotlin_version = "2.1.10"
val ktor_version = "3.2.0"
val logback_version = "1.4.14"



plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}


group = "com.app"
version = "0.0.1"

application {
    mainClass.set("com.app.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-netty-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${ktor_version}")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // เพิ่ม Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.45.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.45.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.45.0")

    // SQLite JDBC Driver
    implementation("org.xerial:sqlite-jdbc:3.43.2.2")

    // Test dependencies
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("com.h2database:h2:2.2.224")
}
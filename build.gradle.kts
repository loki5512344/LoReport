plugins {
    java
    id("com.gradleup.shadow") version "9.0.0-beta4"
    checkstyle
}

group = "dev.loki"
version = "1.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Paper API для 1.19.4+
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("org.postgresql:postgresql:42.7.4")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("net.jqwik:jqwik:1.9.2")
    testImplementation("org.yaml:snakeyaml:2.3")
    testImplementation("org.xerial:sqlite-jdbc:3.47.2.0")
    testCompileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("Loreport")
        relocate("okhttp3", "dev.loki.lorep.libs.okhttp3")
        relocate("okio", "dev.loki.lorep.libs.okio")
        relocate("com.zaxxer.hikari", "dev.loki.lorep.libs.hikari")
        // Don't relocate PostgreSQL to avoid driver loading issues
    }
    
    build {
        dependsOn(shadowJar)
    }
    
    test {
        useJUnitPlatform()
    }
    
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }
    
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    compileTestJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}

checkstyle {
    toolVersion = "10.21.4"
    configFile = file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxErrors = 0
    maxWarnings = 0
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

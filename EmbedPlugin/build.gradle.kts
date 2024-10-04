plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.bothq.plugin"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testImplementation(platform("org.junit:junit-bom:5.11.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("com.bothq.lib:bothq-lib")
}


sourceSets {
    main {
        java {
            exclude("com/bothq/plugin/embed/Interactable/**")
        }
    }
}


tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    exclude("../bothq-lib")

    destinationDirectory.set(File(projectDir, "../out"))
}


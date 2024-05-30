plugins {
    id("java")
}

group = "com.bothq.plugin.chucknorris"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.json:json:20231013")
    implementation("org.slf4j:slf4j-api:1.7.32")
}

tasks.test {
    useJUnitPlatform()
}
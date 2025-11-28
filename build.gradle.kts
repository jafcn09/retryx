plugins {
    kotlin("jvm") version "2.1.21"
    `java-library`
    `maven-publish`
    signing
}

group = "io.github.jafcn09"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "retryx"
            from(components["java"])
            pom {
                name.set("Retryx")
                description.set("Lightweight Kotlin retry library for HTTP calls and tasks")
                url.set("https://github.com/jafcn09/retryx")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("jhafetcanepa")
                        name.set("Jhafet Cánepa")
                    }
                }
                scm {
                    url.set("https://github.com/jafcn09/retryx")
                    connection.set("scm:git:git://github.com/jafcn09/retryx.git")
                    developerConnection.set("scm:git:ssh://github.com/jafcn09/retryx.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            credentials {
                username = findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
                password = findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}
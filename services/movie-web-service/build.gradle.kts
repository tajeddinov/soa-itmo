plugins {
    war
}

dependencies {
    compileOnly(libs.jakartaee.api)
    compileOnly(libs.jersey.server)
    compileOnly(project(":lib:contract"))
    compileOnly(project(":lib:query"))

    testCompileOnly(libs.jakartaee.api)
    testImplementation(project(":lib:contract"))
    testImplementation(project(":services:movie-ejb-service"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.hibernate.validator)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.expressly)
}

tasks.war {
    archiveFileName = "movie-service-web.war"
}

configurations.consumable("webArchive")
artifacts.add("webArchive", tasks.war)

tasks.test {
    useJUnitPlatform {
        excludeTags("seeder")
    }
}

tasks.register<Test>("seed") {
    description = "Заполняет запущенный movie-service фильмами из src/test/resources/movies.csv"
    group = "application"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform {
        includeTags("seeder")
    }
    outputs.upToDateWhen { false }
    testLogging {
        showStandardStreams = true
    }
}

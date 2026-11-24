plugins {
    java
}

dependencies {
    compileOnly(libs.jakartaee.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(project(":lib:contract"))
    implementation(project(":lib:query"))
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)

    testCompileOnly(libs.jakartaee.api)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.jar {
    archiveFileName = "movie-service-ejb.jar"
}

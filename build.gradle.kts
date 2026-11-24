plugins {
    ear
    `jvm-toolchains`
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
}

group = "com.itmo.soa"
version = "0.0.1-SNAPSHOT"

val javaVersion = libs.versions.java.get().toInt()

repositories {
    mavenCentral()
}

val swaggerUi: Configuration by configurations.creating

dependencies {
    swaggerUi(libs.swagger.ui)
    deploy(project(path = ":services:movie-web-service", configuration = "webArchive"))
    deploy(project(":services:movie-ejb-service"))
    earlib(project(":lib:contract"))
    earlib(project(":lib:query"))
    earlib(libs.flyway.core)
    earlib(libs.flyway.postgresql)
}

tasks.ear {
    archiveBaseName = "movie-service"
    deploymentDescriptor {
        applicationName = "movie-service"
        displayName = "movie-service"
        module("movie-service-ejb.jar", "ejb")
        webModule("movie-service-web.war", "/")
    }
}

tasks.register<Exec>("deploy-movie-service") {
    description = "Собирает EAR и обновляет movie-service на helios"
    group = "deployment"
    val ear = tasks.ear.get().archiveFile.get().asFile
    dependsOn(tasks.ear)
    val checkIiop = """payara --port 24048 get 'configs.config.server-config.iiop-service.iiop-listener.*.address' |
        |awk -F= '/[.]address=/ { count++; if ($2 != "127.0.0.1") invalid = 1 }
        |END { if (count == 0 || invalid) { print "Bind all Movie IIOP listeners to 127.0.0.1 before deploying the EAR"; exit 1 } }'""".trimMargin()
    commandLine(
        "sh", "-c",
        """printf '%s\n' "$1" | ssh se bash -l -o pipefail &&
            |scp "$2" se: &&
            |echo "cd ~ && payara --port 24048 deploy --force=true --name movie-service $3 && rm -- $3" | ssh se bash -l""".trimMargin(),
        "sh", checkIiop, ear.path, ear.name,
    )
}

val swaggerSite = tasks.register<Sync>("swagger-site") {
    description = "Собирает локальный Swagger UI со спецификациями docs/*.yaml"
    group = "documentation"
    val specs = fileTree("docs") { include("*.yaml") }
    into(layout.buildDirectory.dir("swagger"))
    from({ swaggerUi.map { zipTree(it) } }) {
        include("META-INF/resources/webjars/swagger-ui/*/**")
        eachFile { path = path.substringAfter("webjars/swagger-ui/").substringAfter("/") }
        includeEmptyDirs = false
    }
    from(specs)
    doLast {
        val urls = specs.files.sortedBy { it.name }.joinToString(",\n") { spec ->
            val title = Regex("""^\s+title:\s*(.+)$""", RegexOption.MULTILINE).find(spec.readText())?.groupValues?.get(1)?.trim() ?: spec.name
            """      { url: "./${spec.name}", name: "${title.replace("\"", "\\\"")}" }"""
        }
        destinationDir.resolve("swagger-initializer.js").writeText(
            """
            |window.onload = function () {
            |  window.ui = SwaggerUIBundle({
            |    urls: [
            |$urls
            |    ],
            |    dom_id: "#swagger-ui",
            |    deepLinking: true,
            |    presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],
            |    plugins: [SwaggerUIBundle.plugins.DownloadUrl],
            |    layout: "StandaloneLayout",
            |  });
            |};
            |""".trimMargin()
        )
    }
}

tasks.register<Exec>("swagger") {
    description = "Локальный Swagger UI для docs/*.yaml на http://localhost:8765"
    group = "documentation"
    dependsOn(swaggerSite)
    val java = javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(javaVersion) }
    val site = layout.buildDirectory.dir("swagger")
    commandLine(java.get().executablePath.asFile.path, "-m", "jdk.httpserver", "-p", "8765", "-d", site.get().asFile.path)
}

tasks.register<Exec>("deploy-openapi") {
    description = "Публикует спецификации docs/*.yaml в Swagger UI на se.ifmo.ru/~t409659/swagger/"
    group = "deployment"
    val specs = fileTree("docs") { include("*.yaml") }.files.map { it.path }.sorted()
    commandLine(listOf("scp") + specs + "se:/home/teachers/t409659/public_html/swagger/")
}

tasks.register<Exec>("deploy-frontend") {
    description = "Собирает фронтенд и публикует его на se.ifmo.ru/~t409659/soa/"
    group = "deployment"
    workingDir = file("frontend")
    val publicDir = "/home/teachers/t409659/public_html/soa"
    val domains = "~/payara7/glassfish/domains"
    val remote = listOf(
        "rm -rf $publicDir",
        "mkdir $publicDir",
        "tar -C $publicDir -xf -",
        "cd $publicDir",
        "keytool -exportcert -rfc -alias movie-service -keystore $domains/movie-service/config/keystore.p12 -storepass changeit -file movie-service.crt",
        "keytool -exportcert -rfc -alias oscar-service -keystore $domains/oscar-service/config/keystore.p12 -storepass changeit -file oscar-service.crt",
        "chmod -R a+rX .",
    ).joinToString(" && ")
    commandLine(
        "sh", "-c",
        """npm run build -- --mode helios && cp -R helios/. dist/ && COPYFILE_DISABLE=1 tar -C dist -cf - . | ssh se "bash -lc '$1'"""",
        "sh", remote,
    )
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(javaVersion)
            }
        }

        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.compilerArgs.add("-parameters")
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}

import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    val kotlinVersion = "1.9.20"
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

group = "academy.softserve"
version = "0.0.1-SNAPSHOT"

// Корректное указание целевой версии Java

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Принудительная настройка целевой JVM для всех задач компиляции Kotlin
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xjsr305=strict")
    }
}


repositories {
    mavenCentral()
}

dependencies {
	// Веб-зависимости (Spring Web и WebFlux)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Поддержка Корутин (Исправляет ошибки Unresolved reference: coroutines/Flow/emit)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")

    // Сериализация JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

// === СЕКЦИЯ ДЛЯ ВЕБПАКА И СБОРКИ ФРОНТЕНДА ===

// 1. Задача npmInstall — устанавливает зависимости в папке ui
val npmInstall by tasks.registering(Exec::class) {
    workingDir = file("ui")
    commandLine("npm", "i")
}

// 2. Задача compileUi — собирает Webpack (зависит от npmInstall)
val compileUi by tasks.registering(Exec::class) {
    dependsOn(npmInstall)
    workingDir = file("ui")
    commandLine("npm", "run", "build")
}

// 3. Задача copyUi — переносит скомпилированный фронтенд в ресурсы Spring Boot
val copyUi by tasks.registering(Copy::class) {
    dependsOn(compileUi)

    // Берем файлы из директории сборки Webpack
    from("ui/dist")

    // Копируем строго в папку ресурсов статики Spring Boot
    into("src/main/resources/static")
}


// 4. Запуск BootRun зависит от выполнения copyUi
tasks.withType<BootRun> {
    dependsOn(copyUi)
}




import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("org.springframework.boot") version "3.2.0" // Укажите вашу актуальную версию Boot
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
}

// ... существующие настройки репозиториев и зависимостей ...

// Задача 1: Установка npm-зависимостей внутри папки ui
val npmInstall by tasks.registering(Exec::class) {
    workingDir = file("ui")
    // Для Windows используется "npm.cmd", для Linux/macOS — "npm"
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "npm i")
    } else {
        commandLine("npm", "i")
    }
}

// Задача 2: Компиляция фронтенда через Webpack (зависит от npmInstall)
val compileUi by tasks.registering(Exec::class) {
    dependsOn(npmInstall)
    workingDir = file("ui")
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd", "/c", "npm run build")
    } else {
        commandLine("npm", "run", "build")
    }
}

// Задача 3: Копирование собранных файлов фронтенда в ресурсы Spring Boot (зависит от compileUi)
val copyUi by tasks.registering(Copy::class) {
    dependsOn(compileUi)
    
    // Откуда копируем (результаты сборки Webpack)
    from("ui/dist/index.html")
    from("ui/dist/app.js")
    from("ui/dist/css") {
        into("css") // Помещает папку css внутрь целевой директории
    }
    
    // Куда копируем (в статическую директорию ресурсов бэкенда)
    into("src/main/resources/static") 
}

// Настройка зависимости: Любая задача типа BootRun должна зависеть от copyUi
tasks.withType<BootRun> {
    dependsOn(copyUi)
}

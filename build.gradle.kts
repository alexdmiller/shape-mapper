// This Gradle script is designed to help you build and release your Processing library.
// The section marked "USER BUILD CONFIGURATIONS" is intended for customization.
// The rest of the script is responsible for the build process and should typically not be modified.


import java.util.Properties
import org.gradle.internal.os.OperatingSystem

plugins {
    id("java")
    id("org.bytedeco.gradle-javacpp-platform") version "1.5.10"
}

project.extra.apply {
    set(
        "javacppPlatform",
        "linux-x86_64,macosx-x86_64,macosx-arm64,windows-x86_64,linux-armhf,linux-arm64"
    )
}

// Sets the Java version to use for compiling your library.
// Processing4 was compiled with Java version 17, so it's recommended to compile your library with version 17.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}


//==========================
// USER BUILD CONFIGURATIONS
//==========================

// the short name of your library. This string will name relevant files and folders.
// Such as:
// <libName>.jar will be the name of your build jar
// <libName>.zip will be the name of your release file
val libName = "shapemapper"

// The group ID of your library, which uniquely identifies your project.
// It's often written in reverse domain name notation.
// For example, if your website is "myDomain.com", your group ID would be "com.myDomain".
// Replace "com.myDomain" with your own domain or organization name.
group = "spacefiller"

// The version of your library. It usually follows semantic versioning (semver),
// which uses three numbers separated by dots: "MAJOR.MINOR.PATCH" (e.g., "1.0.0").
// - MAJOR: Increases when you make incompatible changes.
// - MINOR: Increases when you add new features that are backward-compatible.
// - PATCH: Increases when you make backward-compatible bug fixes.
// You can update these numbers as you release new versions of your library.
version = "0.1.7"

// The location of your sketchbook folder. The sketchbook folder holds your installed
// libraries, tools, and modes. It is needed if you:
// 1. wish to copy the library to the Processing sketchbook, which installs the library locally
// 2. have Processing library dependencies
// Depending on your OS, the code below should set the correct location, if you are using a Mac,
// Windows, or Linux machine.
// If you run the Gradle task deployToProcessingSketchbook, and you do not see your library
// in the contributions manager, then one possible cause could be the sketchbook location
// is wrong. You can check the sketchbook location in your Processing application preferences.
var sketchbookLocation = ""
val userHome = System.getProperty("user.home")
val currentOS = OperatingSystem.current()
if(currentOS.isMacOsX) {
    sketchbookLocation = "$userHome/Documents/Processing"
} else if(currentOS.isWindows) {
    sketchbookLocation = "$userHome/My Documents/Processing"
} else {
    sketchbookLocation = "$userHome/sketchbook"
}
// If you need to set the sketchbook location manually, uncomment out the following
// line and set sketchbookLocation to the correct location
// sketchbookLocation = "$userHome/sketchbook"


var javaCvVersion = "1.5.10"


// Repositories where dependencies will be fetched from.
// You can add additional repositories here if your dependencies are hosted elsewhere.
repositories {
    mavenCentral()

    // these two are needed to resolve processing4 from micycle1's repo
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://jogamp.org/deployment/maven/") }
}

dependencies {
    compileOnly(group = "com.github.micycle1", name = "processing-core-4", version = "4.3.1")
//    implementation(group = "com.github.micycle1", name = "processing-core-4", version = "4.3.1")

    // opencv
    implementation(group = "org.bytedeco", name = "opencv-platform", version = "4.9.0-$javaCvVersion")
    implementation(group = "org.bytedeco", name = "openblas-platform", version = "0.3.26-$javaCvVersion")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

//==============================
// END USER BUILD CONFIGURATIONS
//==============================


// =============================
// INTERNAL BUILD CONFIGURATIONS
// Do not edit the following sections unless you know what you're doing.
// =============================

// Settings for how the JAR file (your library) will be built.
// You want to name your jar with the library short name, aka libName.
tasks.jar {
    archiveBaseName.set(libName)
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    exclude("spacefiller/peasy")
    exclude("spacefiller/shapemapper/examples")
    exclude("spacefiller/shapemapper/utils")
}

// ===========================
// Tasks for releasing library
// ===========================

val releaseRoot = "$rootDir/release"
val releaseName = libName
val releaseDirectory = "$releaseRoot/$releaseName"

// read in user-defined properties in release.properties file
// to be saved in library.properties file, a required file in the release
// using task writeLibraryProperties
val libraryProperties = Properties().apply {
    load(rootProject.file("release.properties").inputStream())
}

tasks.register<WriteProperties>("writeLibraryProperties") {
    group = "processing"
    destinationFile = project.file("library.properties")

    property("name", libraryProperties.getProperty("name"))
    property("version", libraryProperties.getProperty("version"))
    property("prettyVersion", project.version)
    property("authors", libraryProperties.getProperty("authors"))
    property("url", libraryProperties.getProperty("url"))
    property("categories", libraryProperties.getProperty("categories"))
    property("sentence", libraryProperties.getProperty("sentence"))
    property("paragraph", libraryProperties.getProperty("paragraph"))
    property("minRevision", libraryProperties.getProperty("minRevision"))
    property("maxRevision", libraryProperties.getProperty("maxRevision"))
}

// define the order of running, to ensure clean is run first
tasks.build.get().mustRunAfter("clean")
tasks.javadoc.get().mustRunAfter("build")

tasks.register("buildReleaseArtifacts") {
    group = "processing"
    dependsOn("clean","build","writeLibraryProperties")
    finalizedBy("packageRelease", "duplicateZipToPdex")

    doFirst {
        println("Releasing library $libName")
        println(org.gradle.internal.jvm.Jvm.current())

        println("Cleaning release...")
        project.delete(files(releaseRoot))
    }

    doLast {
        println("Creating package...")

        println("Copy library...")
        copy {
            from(layout.buildDirectory.file("libs/${libName}.jar"))
            into("$releaseDirectory/library")
        }

        println("Copy dependencies...")
        copy {
            from(configurations.runtimeClasspath)
            into("$releaseDirectory/library")
        }

        println("Copy assets...")
        copy {
            from("$rootDir")
            include("shaders/**", "native/**")

            into("$releaseDirectory/library")
            exclude("*.DS_Store")
        }

        println("Copy javadoc...")
        copy {
            from(layout.buildDirectory.dir("docs/javadoc"))
            into("$releaseDirectory/reference")
        }

        println("Copy additional artifacts...")
        copy {
            from(rootDir)
            include("README.md", "readme/**", "library.properties", "examples/**", "src/**")

            into(releaseDirectory)
            exclude("*.DS_Store", "**/networks/**")
        }

        println("Copy repository library.txt...")
        copy {
            from(rootDir)
            include("library.properties")
            into(releaseRoot)
            rename("library.properties", "$libName.txt")
        }
    }
}

tasks.register<Zip>("packageRelease") {
    dependsOn("buildReleaseArtifacts")
    doFirst {
        println("Create zip file...")
    }
    archiveFileName.set("${libName}.zip")
    from(releaseDirectory)
    into(releaseName)
    destinationDirectory.set(file(releaseRoot))
    exclude("**/*.DS_Store")
}

tasks.register<Copy>("duplicateZipToPdex") {
    doFirst {
        println("Duplicate zip file to pdex extension...")
    }
    from(releaseRoot) {
        include("$libName.zip")
        rename("$libName.zip", "$libName.pdex")
    }
    into(releaseRoot)
}
tasks["duplicateZipToPdex"].mustRunAfter("packageRelease")

tasks.register("deployToProcessingSketchbook") {
    group = "processing"
    dependsOn("buildReleaseArtifacts")

    doFirst {
        println("Copy to sketchbook  $sketchbookLocation ...")
    }
    val installDirectory = "$sketchbookLocation/libraries/$libName"
    copy {
        from(releaseDirectory)
        include("library.properties",
            "examples/**",
            "library/**",
            "reference/**",
            "src/**"
        )
        into(installDirectory)
    }
}

tasks.register<JavaExec>("runApp") {
    group = "application"
    description = "Runs the main application"

    // Specify the main class to run
    mainClass.set("spacefiller.shapemapper.examples.Test")

    // Include the main source set's runtime classpath
    classpath = sourceSets.main.get().runtimeClasspath + configurations.runtimeClasspath.get()
}
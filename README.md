<img src="src/main/resources/icons/app_icon_highres.png" align="right" width="120" alt="Logo" />

# Pomotimo

![Java Version](https://img.shields.io/badge/Java-21-orange)
![Build Tool](https://img.shields.io/badge/Gradle-8.11-02303A)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

**Pomotimo** is a modern, cross-platform desktop application built with Java 21 and JavaFX. It features a responsive user interface and self-contained packaging.

## Features

* **Cross-Platform:** Runs natively on Windows, Linux, and macOS.
* **Modern UI:** Built using JavaFX 22 with modular architecture.
* **Persistence:** Reliable local data storage using Gson serialization.
* **Zero-Dependency Run:** Can be bundled into a native installer (EXE, DEB, DMG) that includes its own Java runtime.

## Tech Stack

| Component | Technology | Version | License |
| :--- | :--- |:--------| :--- |
| **Language** | Java | 21      | GPLv2+CE |
| **GUI Framework** | JavaFX | 22.0.1  | GPLv2+CE |
| **Icons** | Ikonli (FontAwesome) | 12.3.1  | Apache 2.0 |
| **Native Access** | JNA | 5.17.0  | Apache 2.0 |
| **JSON/Data** | Gson | 2.13.1  | Apache 2.0 |
| **Logging** | SLF4J + Logback | 2.0.17  | MIT / EPL |

## Prerequisites for Building

To build this project from source, ensure you have the following installed:

1.  **JDK 21** (Java Development Kit)
2.  **Git**

### Native Installer Requirements (`jpackage`)
This project uses `jlink` and `jpackage` to build native installers. Depending on your OS, you must install specific tools for this task to succeed:

#### Windows
To build `.exe` installers:
* **WiX Toolset (v3.14 or later):** [Download WiX](https://wixtoolset.org/).
* Ensure the WiX `bin` directory is added to your system `PATH`.

#### Linux
To build `.deb` (Debian/Ubuntu) or `.rpm` (RedHat/Fedora) installers:
* **Debian/Ubuntu:** `sudo apt-get install fakeroot dpkg-dev`
* **Fedora/RedHat:** `sudo dnf install rpm-build`

#### macOS
To build `.dmg` or `.pkg` installers:
* **Xcode Command Line Tools:** Run `xcode-select --install` in your terminal.
* *Note: Application signing requires a valid Apple Developer ID.*

## Getting Started

### 1. Clone the repository
```bash
git clone [https://github.com/yabsp/pomotimo.git](https://github.com/yabsp/pomotimo.git)
cd pomotimo
```

### Run in Development Mode
You can launch the application directly using Gradle without installing it:

#### Linux / macOS
```./gradlew run```

#### Windows
```gradlew.bat run```

## Building & Distribution
### Create a "Fat Jar"
Creates a single JAR file containing all dependencies (useful for quick sharing with other Java users).

```./gradlew shadowJar```

*Output location*: ```build/libs/pomotimo-1.1.jar```


### Create a Custom Runtime Image (JLink)
Creates a minimized folder containing the app and only the specific Java modules required to run it. This does not require Java to be installed on the target machine.

```./gradlew jlink```
*Output location*: ```build/image/```

### Create Native Installers (JPackage)
Generates an installer file specific to the OS you are building on.

```./gradlew jpackage```
*Output location*: ```build/jpackage/```

## License

This project is licensed under the **Apache License 2.0**.
See the [LICENSE](LICENSE) file for the full text.

### Third-Party Notices
This software includes third-party open-source components.

* **JavaFX**
    * Copyright (c) Oracle and/or its affiliates.
    * Licensed under [GPL v2 with Classpath Exception](https://openjdk.org/legal/gplv2+ce.html).
* **Gson**
    * Copyright (c) Google Inc.
    * Licensed under [Apache 2.0](https://github.com/google/gson/blob/master/LICENSE).
* **Ikonli**
    * Copyright (c) Kordamp.
    * Licensed under [Apache 2.0](https://github.com/kordamp/ikonli/blob/master/LICENSE).
* **JNA**
    * Copyright (c)
    * Licensed under [Apache 2.0](https://github.com/java-native-access/jna/blob/master/LICENSE) / LGPL 2.1.

## Contributing
Contributions are welcome! Not only coding contributions but proposals for architectural improvements as well.

1. Fork the repository.

2. Create your feature branch (```git checkout -b feature/AmazingFeature```).

3. Commit your changes (```git commit -m 'Add some AmazingFeature'```).

4. Push to the branch (```git push origin feature/AmazingFeature```).

5. Open a Pull Request.
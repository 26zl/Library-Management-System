# Library Management System

## Overview
Library Management System is a Java application built with JavaFX and SQLite for managing a library's book inventory. The application enables users to add, borrow, return, and delete books. The project uses Maven for dependency management.

## Requirements
- Java 17 or later
- Maven

## Installation and Running
1. Clone or download the project.
2. Open a terminal in the project directory.
3. Run the following command to clean the build first
   mvn clean package
4. Build the project

**Note:** The SQLite JDBC dependency is managed automatically through the `pom.xml` file, so you do not need to install SQLite manually. The application will automatically create the `library.db` file on the first run.

## Project Structure
- `src/main/java/com/zl/librarymanagementsystem`: Contains all Java source files.
- `pom.xml`: Maven configuration file with all required dependencies.
- `library.log`: Log file for recording operations (created at runtime).
- `library.db`: SQLite database file (automatically generated on first run).

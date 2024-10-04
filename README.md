# FormulaAnalysis
Personal project to demonstrate competency in the following areas:
  - Java (Spring Boot)
  - Python
  - REST API design and usage
  - Database interactions
      - PostgreSQL - Relational
      - MongoDB - Non-Relational
  - ETL processes

The project is split into two main services: **TelemetryImportService** and **ETLProcess**. They are defined in more detail below.

<details>
  <summary><strong>TelemetryImportService</strong></summary>

Java Spring Boot microservice with the following:  
  - Ingestion of Formula 1 telemetry from OpenF1API
  - Modeling and Analysis of telemetry
  - REST API for persisting to and requesting from MongoDB

### Project Architecture
![Architecture Diagram](https://github.com/mcheaman/FormulaAnalysis/blob/main/TelemetryImportService/TelemetryImportService.drawio.png?raw=true)

### Project Structure

#### [model](https://github.com/mcheaman/FormulaAnalysis/tree/main/TelemetryImportService/src/main/java/com/f1telemetry/race_telemetry_analyzer/model)
Defines the data models representing entities (ex. Driver, Race) used in the application.

#### [service](https://github.com/mcheaman/FormulaAnalysis/tree/main/TelemetryImportService/src/main/java/com/f1telemetry/race_telemetry_analyzer/service)
Implements the business logic for managing data stored in MongoDB. Create, Read, Update, and Delete operations for database collections.

>##### [/OpenF1API](https://github.com/mcheaman/FormulaAnalysis/tree/main/TelemetryImportService/src/main/java/com/f1telemetry/race_telemetry_analyzer/service/OpenF1API)
>Implements the business logic for importing race telemetry from OpenF1API. The telemetry is fetched from the external API, modeled to the specifications in `/model`, and persisted to MongoDB.

#### [controller](https://github.com/mcheaman/FormulaAnalysis/tree/main/TelemetryImportService/src/main/java/com/f1telemetry/race_telemetry_analyzer/controller)  
REST controllers that handle incoming HTTP requests and route them to the appropriate service methods.

#### [repository](https://github.com/mcheaman/FormulaAnalysis/tree/main/TelemetryImportService/src/main/java/com/f1telemetry/race_telemetry_analyzer/repository)  
Repository interfaces that interact with MongoDB for data persistence using Spring Data.

</details>

<details>
  <summary><strong>ETLProcess</strong></summary>

This service utilizes the data of TelemetryImportService to demonstrate competency in ETL processes, PostgreSQL, and data visualization through Grafana.

</details>

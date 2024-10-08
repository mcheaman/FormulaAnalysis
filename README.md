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
## TelemetryImportService
![Architecture Diagram](https://github.com/mcheaman/FormulaAnalysis/blob/main/TelemetryImportService/TelemetryImportService.drawio.png?raw=true)
<details>
  <summary>Details</summary>

Java Spring Boot microservice with the following:  
  - Ingestion of Formula 1 telemetry from OpenF1API
  - Modeling and Analysis of telemetry
  - REST API for persisting to and requesting from MongoDB

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

## ETL Process  
![Architecture Diagram](https://github.com/mcheaman/FormulaAnalysis/blob/main/ETLProcess/ETLProcess.drawio.png?raw=true)
<details>
  <summary>Details</summary>

This service utilizes the data of TelemetryImportService to demonstrate competency in ETL processes, PostgreSQL, and data visualization through Grafana.
### `extract.py`
This file handles the extraction of data from MongoDB, connecting to the database and retrieving raw telemetry data from the relevant collections.

### `transform.py`
This file processes and transforms the raw data extracted from MongoDB into a format suitable for a relational database, converting nested structures into flat tables.

### `load.py`
This file loads the transformed data into a PostgreSQL cloud database by inserting it into the appropriate relational tables.

### `main_etl.py`
This is the main orchestrator of the ETL pipeline. It coordinates the process by sequentially calling the `extract`, `transform`, and `load` functions to execute the full ETL workflow.

</details>

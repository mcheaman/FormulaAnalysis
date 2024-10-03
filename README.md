# FormulaAnalysis
Java Spring Boot microservice with the following:  
  - Ingestion of Formula 1 telemetry from OpenF1API
  - Modeling and Analysis of telemetry
  - REST API for persisting to and requesting from MongoDB
## Project Architecture
![Architecture Diagram](https://github.com/mcheaman/FormulaAnalysis/blob/main/FormulaAnalysis.drawio.png?raw=true)

## Project Structure

### [model](https://github.com/mcheaman/FormulaAnalysis/tree/main/src/main/java/com/f1telemetry/race_telemetry_analyzer/model)
Defines the data models representing entities (ex. Driver, Race) used in the application.
### [service](https://github.com/mcheaman/FormulaAnalysis/tree/main/src/main/java/com/f1telemetry/race_telemetry_analyzer/service)
Implements the business logic for managing data stored in MongoDB. Create, Read, Update, and Delete operations for database collections
>#### [/OpenF1API](https://github.com/mcheaman/FormulaAnalysis/tree/main/src/main/java/com/f1telemetry/race_telemetry_analyzer/service/OpenF1API)
>Implements the business logic for importing race telemetry from OpenF1API. The telemetry is fetched from the external API, modeled to the specifications in /model, and persisted to MongoDB. 
### [controller](https://github.com/mcheaman/FormulaAnalysis/tree/main/src/main/java/com/f1telemetry/race_telemetry_analyzer/controller)  
REST controllers that handle incoming HTTP requests and route them to the appropriate service methods.
### [repository](https://github.com/mcheaman/FormulaAnalysis/tree/main/src/main/java/com/f1telemetry/race_telemetry_analyzer/repository)  
Repository interfaces that interact with MongoDB for data persistence using Spring Data.



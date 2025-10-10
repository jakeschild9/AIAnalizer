Initial Setup: How to Get and Configure a GEMINI API Key
==========================================================
The application requires a Google Gemini API key to function.

1.  Visit **[https://aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)** to generate an API key (it's free and no billing is required).
2.  Create an **`env.properties`** file in the project's root directory (the same level as `pom.xml`).
3.  Paste your API key into this file in the following format: `GEMINI_API_KEY=YOUR_KEY_HERE`
4.  In IntelliJ, go to your **`AiAnalyzerApplication`** run configuration (top-right of the window).

5. ![img_1.png](README_images/img_1.png)
5.  Click **`Edit Configurations...`** > **`Modify Options`** > **`Environment Variables`**.
6.  In the "Environment variables" field, point to your `env.properties` file by clicking the folder icon and selecting it.


## AI System Overview

----------------------
The primary role of the AI is to analyze file content and provide two key pieces of information: a **classification** (e.g., "Safe", "Suspicious", "Malicious") and a brief, human-readable **description** of the file.

## Core AI Workflow

------------------
Here’s the step-by-step data flow for how a file is processed by the AI system:

1.  **Queue Consumption:** The `FileProcessingService` picks a task from the database queue.
2.  **Orchestration:** It calls the `ProcessFile` service, which is the main coordinator for AI analysis.
3.  **File Reading:** `ProcessFile` reads the file content, intelligently chunking large files to manage memory usage.
4.  **Querying:** It then calls a specific method in the `AiQuery` service (e.g., `passiveResponseFromFile`).
5.  **Prompt Execution:** The `AiQuery` service contains the actual text **prompts** sent to the Gemini model. It uses the `AiClient` to make the API call.
6.  **Response Handling:** The AI's response (formatted as `"Classification%Description"`) is returned.
7.  **Storage:** The `ProcessFile` service passes this string back to be parsed and saved to the database by the `LabelService`.

## Key Files & Packages

* `config/AiClient.java`: Configures and creates the connection to the Gemini AI service.
* `service/ai/AiQuery.java`: Contains all the prompts sent to the AI. Modifying the text in this file will change the AI's behavior.
* `service/ai/ProcessFile.java`: Orchestrates the analysis. It decides how to handle files based on size and calls the appropriate methods in `AiQuery`.
* `model/FileInterpretation.java`: A data model that defines the *type* of AI search being performed (e.g., `ACTIVE` for a summary, `PASSIVE` for just a classification).

## How to Test Prompts

------------------------
You don't need to run the entire application to test a new prompt. The fastest way to iterate is to create a temporary test in the `src/test/java` folder that calls your `AiQuery` methods directly with sample text. This allows for rapid testing without the overhead of the file scanner and database.
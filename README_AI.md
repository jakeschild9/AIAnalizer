Initial Setup: How to Get and Configure a GEMINI API Key
==========================================================
1. Visit https://aistudio.google.com/app/apikey to generate an API key (it's free and no billing required).

2. Create an `env.properties` file in the project's root directory (the same level as `pom.xml`).

3. Paste your API key into this file in the following format: `GEMINI_API_KEY=YOUR_KEY_HERE`

4. In IntelliJ, go to your `AiAnalyzerApplication` run configuration (top-right of the window).

![img_1.png](README_images/img_1.png)

5. Click `Edit Configurations...` > `Modify Options` > `Environment Variables`.

6. In the "Environment variables" field, point to your `env.properties` file.

AI System Overview
------------------
The primary role of the AI in this project is to analyze file content and provide two key pieces of information:
1.  A **classification** (e.g., "Safe", "Suspicious", "Malicious").
2.  A brief, human-readable **description** or summary of the file's content.

This is all orchestrated through a few key services that work together.

Core AI Workflow
----------------
Understanding the data flow is key. Here’s how a file is processed by the AI system:

1.  The [`FileProcessingService`](src/main/java/edu/missouristate/aianalyzer/service/database/FileProcessingService.java) picks a task from the database queue.
2.  It calls the [`ProcessFile`](src/main/java/edu/missouristate/aianalyzer/service/ai/ProcessFile.java) service, which is the main orchestrator for AI analysis.
3.  `ProcessFile` reads the content of the file. If the file is large, it intelligently reads it in chunks to avoid using too much memory.
4.  It then calls a specific method in the [`AiQuery`](src/main/java/edu/missouristate/aianalyzer/service/ai/AiQuery.java) service (e.g., `passiveResponseFromFile` or `responseForLargeFileChunks`).
5.  The `AiQuery` service contains the actual text **prompts** that we send to the Gemini model. It uses the [`AiClient`](src/main/java/edu/missouristate/aianalyzer/config/AiClient.java) to make the API call.
6.  The AI's response (a single string) is returned. The standard format is `"Classification%Description"`.
7.  The `ProcessFile` service receives this string and passes it back, where it eventually gets saved to the database by a service like [`LabelService`](src/main/java/edu/missouristate/aianalyzer/service/database/LabelService.java).


Important File Locations
----------------------------
These are the main files:

* [`AiClient.java`](src/main/java/edu/missouristate/aianalyzer/config/AiClient.java): This configures and creates the connection to the Gemini AI service.
* [`AiQuery.java`](src/main/java/edu/missouristate/aianalyzer/service/ai/AiQuery.java): This contains all the prompts sent to the AI. Modifying the text in this file will change the AI's behavior and responses.
* [`ProcessFile.java`](src/main/java/edu/missouristate/aianalyzer/service/ai/ProcessFile.java): This service orchestrates the analysis. It decides whether to process a file as "small" or "large" and calls the appropriate methods in `AiQuery`.
* [`FileInterpretation.java`](src/main/java/edu/missouristate/aianalyzer/model/FileInterpretation.java): This is a data model that defines the *type* of AI search being performed (e.g., `ACTIVE` for a summary, `PASSIVE` for just a classification).


How to Test Your Prompts
------------------------
You don't need to run the entire application to test a new prompt. A great way to quickly test changes is to create a temporary test in the `test` source folder that calls your `AiQuery` methods directly with a sample piece of text. This will let you iterate on your prompts much faster.
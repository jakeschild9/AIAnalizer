How to Test the AIAnalyzer Database in IntelliJ
==============================================

This guide explains how to run the application to test the database functionality and how to view the SQLite database directly inside IntelliJ.


Part 1: Get an API Key
-------------------------------------------------
The application will not run without a valid Gemini API key. Before proceeding, please see **[README_AI.md](README_AI.md)** for instructions on how to get a key and configure it for the project.

Part 2: Connect IntelliJ to the SQLite Database
-----------------------------------------------

1.  **Run `AiAnalyzerApplication.java` once.** This will create the `aianalyzer.db` file in the project's root directory. You can stop the application after it starts.
2.  Open the **Database** tool window in IntelliJ (View -> Tool Windows -> Database).
3.  Click the **`+`** icon -> Data Source -> SQLite.
4.  For the `File`, point it to the `aianalyzer.db` file that was just created in your project root.
5.  You will likely see a warning about missing drivers. Click the **"Download missing driver files"** link.
6.  **Important:** After the download finishes, click **`Apply`** first, and then click **`OK`**.
7.  In the Database tool window, right-click your `aianalyzer.db` connection and select **Refresh**. You should now see all the application tables.

Part 3: Fix the "Driver Not Found" Error
----------------------------------------

IntelliJ needs its own database driver to connect.

1. In the same Data Source configuration window, you will likely see an error about a missing driver. Click the *"Download missing driver files"* link.

2. *IMPORTANT:* After it downloads, click the *`Apply`* button first, and then click *`OK`*. This ensures the setting is saved correctly.

3. Go back to the Database tool window, right-click your `aianalyzer.db` connection, and click *Refresh*.

You should now have a working connection to the database.

![img.png](README_images/img.png)

Note: You may need to right-click the aianalyzer.db and click Refresh to see the populated tables. MAKE SURE the application is not running when you click refresh.


## Core Architecture: Producer-Consumer Queue

----------------------
Our backend uses a "producer-consumer" pattern with the database acting as a task queue.

1.  **Production (Adding a Task):** The `ActiveScanService` or `PassiveScanService` discovers a new file. It creates a `ScanQueueItem` and saves it to the `scan_queue` table.
2.  **Consumption (Grabbing a Task):** The `FileProcessingService` runs on a schedule. It queries the `scan_queue` table for items that are ready to be processed.
3.  **Processing (Doing the Work):** For each item, the service reads the file's metadata from the disk (size, date, etc.).
4.  **Storage (Saving the Result):** The service creates a `FileRecord` with this metadata and saves it to the main `files` table.
5.  **Cleanup (Removing the Task):** After the file is successfully stored, the original `ScanQueueItem` is deleted from the queue.

## Key Packages & Files

* `application.properties`: Contains the SQLite database connection URL and Hibernate settings (`ddl-auto`).
* `model/database/`: Contains all JPA `@Entity` classes (e.g., `FileRecord`). Each class here defines a database table schema.
* `repository/database/`: Contains all Spring Data JPA interfaces (e.g., `FileRecordRepository`). These provide the methods (`find`, `save`, `delete`) to perform database operations without writing SQL.
* `service/database/`: Contains the services that implement the producer-consumer logic described above (`ActiveScanService`, `PassiveScanService`, `FileProcessingService`).

# IMPORTANT CHANGES (Historical Context)

-----------------------------------

To help understand the recent merge and refactor, below is a direct mapping of the old, manual backend code to its new equivalent.

* *Application Startup:*
    * **Old:** `Main.java`
    * **New:** [`AiAnalyzerApplication.java`](src/main/java/edu/missouristate/aianalyzer/AiAnalyzerApplication.java)
    * **Improvement:** Spring Boot now handles all startup, configuration, and dependency injection automatically.

* *Database Connection & Schema Setup:*
    * **Old:** `DatabaseManager.java` (with hardcoded SQL `CREATE TABLE` statements)
    * **New:** The `@Entity` classes in [`model/database/`](src/main/java/edu/missouristate/aianalyzer/model/database/) and settings in [`application.properties`](src/main/resources/application.properties).
    * **Improvement:** The database schema is now managed automatically from our Java entity classes.

* *Database Queries:*
    * **Old:** Manual `PreparedStatement`s and raw SQL strings.
    * **New:** The interfaces in the [`repository/database/`](src/main/java/edu/missouristate/aianalyzer/repository/database/) package.
    * **Improvement:** Spring Data JPA writes the SQL for us, which is safer and cleaner.

* *Initial File System Scan (The "Producer"):*
    * **Old:** `ActiveScanner.java`, `FsIndexer.java`, `PassiveScanner.java`
    * **New:** [`ActiveScanService.java`](src/main/java/edu/missouristate/aianalyzer/service/database/ActiveScanService.java) (for full scans) and [`PassiveScanService.java`](src/main/java/edu/missouristate/aianalyzer/service/database/PassiveScanService.java) (for real-time monitoring).
    * **Improvement:** The logic is now in focused Spring `@Service` beans, making it easier to manage and test.

* *Queue Processing (The "Consumer"):*
    * **Old:** `QueueWorker.java`
    * **New:** [`FileProcessingService.java`](src/main/java/edu/missouristate/aianalyzer/service/database/FileProcessingService.java)
    * **Improvement:** This service now uses the repository interfaces to get its work.

* *Applying AI Labels:*
    * **Old:** `LabelService.java`
    * **New:** The new Spring-based [`LabelService.java`](src/main/java/edu/missouristate/aianalyzer/service/database/LabelService.java)
    * **Improvement:** The new service uses the repository pattern, removing all manual SQL.
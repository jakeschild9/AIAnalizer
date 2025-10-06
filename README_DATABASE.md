How to Test the AIAnalyzer Database in IntelliJ
==============================================

This guide explains how to run the application to test the database functionality and how to view the SQLite database directly inside IntelliJ.


Part 1: Get an API Key
-------------------------------------------------

The application crashes on startup because it requires a Gemini API key.

1. See [README_AI.md](README_AI.md) for instructions on how to get a key and configure it for the project.


Part 2: Connect IntelliJ to the SQLite Database
-----------------------------------------------

1. *Run `AiAnalyzerApplication.java` once.* This will execute the `ddl-auto=update` command in `application.properties` and create the `aianalyzer.db` file in your project's root directory. Stop the application after it has started successfully.

2. Open the *Database* tool window in IntelliJ (usually on the right-hand panel).

3. Click the *+ icon -> Data Source -> SQLite*.

4. For the `File`, point it to the `aianalyzer.db` file in your project root.

5. It may ask you to download drivers; you will need them.


Part 3: Fix the "Driver Not Found" Error
----------------------------------------

IntelliJ needs its own database driver to connect.

1. In the same Data Source configuration window, you will likely see an error about a missing driver. Click the *"Download missing driver files"* link.

2. *IMPORTANT:* After it downloads, click the *`Apply`* button first, and then click *`OK`*. This ensures the setting is saved correctly.

3. Go back to the Database tool window, right-click your `aianalyzer.db` connection, and click *Refresh*.

You should now have a working connection to the database.

![img.png](README_images/img.png)

Note: You may need to right-click the aianalyzer.db and click Refresh to see the populated tables. MAKE SURE the application is not running when you click refresh.



# IMPORTANT CHANGES

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
    * **Old:** `ActiveScanner.java`, `FsIndexer.java`
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

## Important File Locations

* **Active Scan:** [`service/database/ActiveScanService.java`](src/main/java/edu/missouristate/aianalyzer/service/database/ActiveScanService.java)
* **Passive Scan:** [`service/database/PassiveScanService.java`](src/main/java/edu/missouristate/aianalyzer/service/database/PassiveScanService.java)
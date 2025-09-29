# AIAnalizer



# AiAnalyzer

This is a CSC450 project that can analyze your files and leverages AI to explain your files. It will give reports of suspicious, junk, and safe files.
AiAnalyzer is a Java-based application designed to analyze and process files using AI-powered techniques. The goal is to provide insights into uploaded files, detect patterns, and support further data-driven decisions.

## Features
- Automatic drive-scan to analyze all files
- AI-driven content parsing and pattern recognition
- User-friendly interface with simple workflows

## Project Structure
```
src/
└── main/
    └── java/
        └── edu.missouristate.aianalizer/
            ├── AiAnalizerApplication (JavaFX App Class, currently NOT used for launching)
            ├── UiLauncher (The actual class used to launch the application)
            ├── controller/
            │   ├── AIService
            │   └── FileScannerService
            ├── model/
            │   ├── DriveInfo
            │   └── FileInterpretation
            └── view/
                ├── DriveView (home)
                ├── MetricsView (metrics)
                ├── SettingsView (settings)
                └── SuggestionsView (AI insights)
```

## How to Run
While many IDEs allow you to click through the structure 
`(AIAnalizer > Plugins > javafx > javafx:run)`, 
the simplest and most universal way to execute the application is directly through the command line using the javafx-maven-plugin goal:

1. Open your terminal or command prompt.

2. Navigate to the root directory of the aialyzer project (where the pom.xml file is located).

3. Execute the following command (must have Maven installed):
```
mvn javafx:run
```

## Not Working?
1. Check that Maven is installed 
> [!NOTE]
> output should be like Apache Maven 3.9.11
```
mvn -v
```

2. If not, [download Apache Maven](https://maven.apache.org/download.cgi)
> [!NOTE]
> Look for 'Binary zip archive', the apache-maven-3.9.11-bin.zip will work fine

3. Extract .zip, make sure to add the /bin/ path to either User or System environment variables

## Requirements
- Java 17+
- Maven 3.6.3+
- javafx-maven-plugin (0.0.8)

## License
MIT License  

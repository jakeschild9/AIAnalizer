package edu.missouristate.aianalyzer.ui.view.Home;

import edu.missouristate.aianalyzer.ui.service.FileSystemService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FileTreeItem extends TreeItem<File> {

    // I need to load folders in the background so the UI doesn't freeze.
    // Making one shared thread for all file loading is way more efficient
    // than creating a new thread for every single folder.
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        Thread t = new Thread(runnable);
        // This is important. It makes the thread a "daemon," which means the app can close
        // properly even if this thread is still in the middle of scanning a huge folder.
        t.setDaemon(true);
        return t;
    });

    private final FileSystemService fileSystemService;
    private boolean isChildrenLoaded = false;

    public FileTreeItem(File file, FileSystemService fileSystemService) {
        super(file);
        this.fileSystemService = fileSystemService;
    }

    @Override
    public ObservableList<TreeItem<File>> getChildren() {
        // This whole section is the magic for lazy-loading. It only loads the contents
        // of a folder the very first time the user clicks the little expand arrow.
        if (!isChildrenLoaded && !isLeaf()) {
            isChildrenLoaded = true; // Mark it as loaded so we don't do this again.
            super.getChildren().setAll(createLoadingNode()); // Show "Loading..." immediately.
            loadChildrenInBackground(); // Start the real work on the background thread.
        }
        return super.getChildren();
    }

    private void loadChildrenInBackground() {
        // Hand off the slow file-scanning work to our background thread.
        executor.submit(() -> {
            try {
                // --- THIS PART RUNS IN THE BACKGROUND ---
                // It's safe to do slow stuff here.
                List<File> childrenFiles = fileSystemService.getChildrenForPath(getValue());

                // Take the list of files and turn them into `FileTreeItem`s for the tree.
                List<TreeItem<File>> childrenItems = childrenFiles.stream()
                        .map(file -> new FileTreeItem(file, fileSystemService))
                        .collect(Collectors.toList());

                // --- THIS PART RUNS BACK ON THE UI THREAD ---
                // Results have been received, now to update the UI, which must be done on the main JavaFX thread.
                // `Platform.runLater` is how you do that safely.
                Platform.runLater(() -> {
                    super.getChildren().setAll(childrenItems); // Replace "Loading..." with the actual folders/files.
                });

            } catch (Exception e) {
                // If something goes wrong (e.g., access denied to a folder), just print the error
                // and clear the "Loading..." message from the UI.
                e.printStackTrace();
                Platform.runLater(() -> {
                    super.getChildren().clear();
                });
            }
        });
    }

    @Override
    public boolean isLeaf() {
        // A 'leaf' is an item that can't be expanded. In our case, that's a file.
        // A folder is not a leaf.
        return getValue().isFile();
    }

    // A simple helper to create that temporary "Loading..." node.
    private TreeItem<File> createLoadingNode() {
        return new TreeItem<>(new File("Loading..."));
    }
}

package gitlet;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a gitlet stage object.
 * The Stage class provides methods to add, remove, and retrieve file information within the context of a version control system.
 * It allows tracking changes made to files before committing them to the repository.
 * */

public class Stage implements Serializable {
    private final TreeMap<String, String> addedFiles = new TreeMap<>();
    private final TreeSet<String> removedFiles = new TreeSet<>();

    public Stage() {

    }

    public void unstageForAddition(String fileName) {
        addedFiles.remove(fileName);
    }

    public String getBlobID(String fileName) {
        return addedFiles.get(fileName);
    }

    public void stageForAddition(String fileName, String blobID) {
        removedFiles.remove(fileName);
        addedFiles.put(fileName, blobID);
    }

    public boolean isAddedFile(String fileName) {
        return addedFiles.containsKey(fileName);
    }

    public boolean isRemovedFiles(String fileName) {
        return removedFiles.contains(fileName);
    }

    public boolean noAddedFiles() {
        return addedFiles.isEmpty();
    }

    public void clear() {
        addedFiles.clear();
        removedFiles.clear();
    }

    public Set<String> addedFileSet() {
        return addedFiles.keySet();
    }

    public Map<String, String> addedFiles() {
        return new TreeMap<>(addedFiles);
    }

    public void stageForRemoval(String fileName) {
        removedFiles.add(fileName);
    }

    public void unstageForRemoval(String fileName) {
        removedFiles.remove(fileName);
    }

    public Set<String> removedFiles() {
        return new TreeSet<>(removedFiles);
    }

    public boolean noRemovedFiles() {
        return removedFiles.isEmpty();
    }
}

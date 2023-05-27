package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Stage implements Serializable {
    private TreeMap<String, String> addedFiles = new TreeMap<>();
    private TreeSet<String> removedFiles = new TreeSet<>();

    public Stage(){}

    public void unstageForAddition(String fileName){
        addedFiles.remove(fileName);
    }

    public String getBlobID(String fileName){
        return addedFiles.get(fileName);
    }

    public void stageForAddition(String fileName, String blobID){
        removedFiles.remove(fileName);
        addedFiles.put(fileName, blobID);
    }

    public boolean isAddedFile(String fileName){
        return addedFiles.containsKey(fileName);
    }

    public boolean noAddedFiles(){
        return addedFiles.isEmpty();
    }

    public void clear(){
        addedFiles.clear();
        removedFiles.clear();
    }

    public Set<String> addedFileSet(){
        return addedFiles.keySet();
    }

    public Map<String, String> addedFiles() {return new TreeMap<>(addedFiles);}

    public void stageForRemoval(String fileName){
        removedFiles.add(fileName);
    }

    public Set<String> removedFiles() {
        return new TreeSet<>(removedFiles);
    }

    public boolean noRemovedFiles(){ return removedFiles.isEmpty(); }
}

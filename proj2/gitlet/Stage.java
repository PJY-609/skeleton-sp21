package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class Stage implements Serializable {
    private HashMap<String, String> stagedFiles = new HashMap<>();

    public Stage(){}

    public void removeFile(String fileName){
        stagedFiles.remove(fileName);
    }

    public String getBlobID(String fileName){
        return stagedFiles.get(fileName);
    }

    public void addFile(String fileName, String blobID){
        stagedFiles.put(fileName, blobID);
    }

    public boolean containsFile(String fileName){
        return stagedFiles.containsKey(fileName);
    }

    public boolean isEmpty(){
        return stagedFiles.isEmpty();
    }

    public void clear(){
        stagedFiles.clear();
    }

    public Set<String> fileSet(){
        return stagedFiles.keySet();
    }
}

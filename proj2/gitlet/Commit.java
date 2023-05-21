package gitlet;

// TODO: any imports you need here
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.TimeZone;

import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;

    private final String ID;

    private final String timestamp;

    private final HashMap<String, String> tree;

    private final String parentID;

    /* TODO: fill in the rest of this class. */

    public Commit(){
        message = "initial commit";
        timestamp = "Thu Jan 1 00:00:00 1970 -0800";
        tree = new HashMap<>();
        ID = Utils.sha1(toString());
        parentID = null;
    }

    public Commit(String message, Commit parentCommit, Stage stage){
        this.message = message;

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        timestamp = simpleDateFormat.format(date);

        ID = Utils.sha1(toString());

        parentID = parentCommit.ID;

        tree = new HashMap<>(parentCommit.tree);

        for(String fileName: stage.fileSet()){
            tree.put(fileName, stage.getBlobID(fileName));
        }
    }

    @Override
    public String toString(){
        return String.format("Commit for %s at %s", message, timestamp);
    }

    public String logInfo(){
        return String.format(
                "===\n" +
                "commit %s\n" +
                "Date: %s\n" +
                "%s\n\n", ID, timestamp, message);
    }

//    public String sha1Code(){
//        return Utils.sha1(toString());
//    }

    public boolean containsFile(String fileName, String blobID){
        return tree != null && tree.containsKey(fileName) && tree.get(fileName).equals(blobID);
    }

    public String getID(){
        return ID;
    }

    public String getParentID(){
        return parentID;
    }

    public String getBlobID(String fileName){
        return tree.get(fileName);
    }

    public boolean containsFile(String fileName){
        return tree != null && tree.containsKey(fileName);
    }

}

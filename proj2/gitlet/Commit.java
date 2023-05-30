package gitlet;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Represents a gitlet commit object.
 *  Represents a commit in a version control system and provides methods and fields to store and manipulate commit-related information.
 *  Provides a representation of a commit in a version control system, allowing manipulation and retrieval of commit-related information, such as message, timestamp, parent commit IDs, and tracked files.
 *  @author Juezhao Yu
 */
public class Commit implements Serializable {

    private final String message;

    private final String ID;

    private final String timestamp;

    private final TreeMap<String, String> trackedFiles;

    private final List<String> parentIDs = new LinkedList<>();


    public Commit() {
        message = "initial commit";
        timestamp = "Thu Jan 1 00:00:00 1970 -0800";
        trackedFiles = new TreeMap<>();
        ID = Utils.sha1(toString());
    }


    public Commit(String message, Commit currentCommit, Commit givenCommit, Stage stage) {
        this.message = message;

        ZonedDateTime date = ZonedDateTime.now();
        Locale locale = new Locale("en");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy Z").withLocale(locale);
        timestamp = date.format(formatter);

        ID = Utils.sha1(toString());

        parentIDs.add(currentCommit.ID);
        if (givenCommit != null) {
            parentIDs.add(givenCommit.ID);
        }

        trackedFiles = new TreeMap<>(currentCommit.trackedFiles);

        for (String fileName: stage.addedFileSet()) {
            trackedFiles.put(fileName, stage.getBlobID(fileName));
        }

        for (String fileName: stage.removedFiles()) {
            trackedFiles.remove(fileName);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Commit)) {
            return false;
        }

        Commit c = (Commit) o;

        return ID.equals(c.getID());
    }

    @Override
    public String toString() {
        return String.format("Commit for %s at %s", message, timestamp);
    }

    public String logInfo() {
        return String.format("===\n"
                        + "commit %s\n"
                        + "Date: %s\n"
                + "%s\n\n", ID, timestamp, message);
    }


    public boolean isTrackedContent(String fileName, String blobID) {
        return trackedFiles.containsKey(fileName) && trackedFiles.get(fileName).equals(blobID);
    }

    public String getID() {
        return ID;
    }

    public String getFirstParentID() {
        if (parentIDs.isEmpty()) {
            return null;
        }

        return parentIDs.get(0);
    }

    public boolean isInitCommit() {
        return parentIDs.isEmpty();
    }

    public Iterator<String> getParentIDs() {
        return parentIDs.iterator();
    }

    public String getBlobID(String fileName) {
        return trackedFiles.get(fileName);
    }

    public boolean isTrackedFile(String fileName) {
        return trackedFiles.containsKey(fileName);
    }

    public boolean hasMessage(String msg) {
        return message.equals(msg);
    }

    public Set<String> trackedFileSet() {
        return trackedFiles.keySet();
    }

    public Map<String, String> trackedFiles() {
        return new TreeMap<>(trackedFiles);
    }
}

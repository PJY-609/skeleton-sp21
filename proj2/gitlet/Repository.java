package gitlet;

//import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.util.HashMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");

    public static final File COMMIT_DIR = join(OBJECT_DIR, "commits");

    public static final File BLOB_DIR = join(OBJECT_DIR, "blobs");

    public static final File BRANCH_DIR = join(GITLET_DIR, "refs");

    public static final File BRANCH_HEAD_DIR = join(BRANCH_DIR, "heads");

    public static final File MASTER_FILE = join(BRANCH_HEAD_DIR, "master");

    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    public static final File STAGE_FILE = join(GITLET_DIR, "stage");

    /* TODO: fill in the rest of this class. */

    public static void initialize(){
        if (GITLET_DIR.exists()){
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdirs();
        BLOB_DIR.mkdirs();
        BRANCH_DIR.mkdirs();
        BRANCH_HEAD_DIR.mkdirs();

        Commit commit = new Commit();
        writeCommit(commit);
        writeBranchRef(MASTER_FILE, commit);
        writeHeadRef(MASTER_FILE);

        Stage stage = new Stage();
        writeObject(STAGE_FILE, stage);
    }


    public static void addFile(String fileName){
        validateInitialization();

        File file = join(CWD, fileName);
        validateIfFileExists(file);
        String blobString = readContentsAsString(file);
        String blobID = sha1(blobString);

        Commit headCommit = getHeadCommit();

        Stage stage = readObject(STAGE_FILE, Stage.class);

        if (headCommit.containsFile(fileName, blobID)){
            stage.removeFile(fileName);
        }
        else{
            stage.addFile(fileName, blobID);
        }

        writeObject(STAGE_FILE, stage);

        if(stage.containsFile(fileName)){
            File blobFile = join(BLOB_DIR, blobID);
            byte[] blobContent = readContents(file);
            writeObject(blobFile, blobContent);
        }
    }

    private static Commit getHeadCommit(){
        File headRefFile = readObject(HEAD_FILE, File.class);
        File headCommitFile = readObject(headRefFile, File.class);
        return readObject(headCommitFile, Commit.class);
    }

    public static void makeCommit(String message){
        validateInitialization();
        validateCommitMessage(message);

        Stage stage = readObject(STAGE_FILE, Stage.class);
        validateNonEmptyStageMap(stage);

        Commit headCommit = getHeadCommit();

        Commit commit = new Commit(message, headCommit, stage);
        writeCommit(commit);
        writeBranchRef(MASTER_FILE, commit);
        writeHeadRef(MASTER_FILE);

        stage.clear();
        writeObject(STAGE_FILE, stage);

    }

    private static void writeBranchRef(File branchRef, Commit commit){
        File commitFile = join(COMMIT_DIR, commit.getID());
        writeObject(branchRef, commitFile);
    }

    private static void writeHeadRef(File branchRef){
        writeObject(HEAD_FILE, branchRef);
    }

    public static void checkout(String[] args){
        if(args.length == 3){
            checkoutFile(args[2]);
        }
        if(args.length == 4){
            checkoutFile(args[1], args[3]);
        }
        else{
            checkoutBranch(args[1]);
        }
    }

    private static void checkoutFile(String fileName){
        Commit headCommit = getHeadCommit();
        checkoutFile(headCommit, fileName);
    }

    private static void checkoutFile(String commitID, String fileName){
        File commitFile = join(COMMIT_DIR, commitID);
        validateIfCommitExists(commitFile);

        Commit commit = readObject(commitFile, Commit.class);
        checkoutFile(commit, fileName);
    }

    private static void checkoutFile(Commit commit, String fileName){
        validateIfFileExistsInCommit(commit, fileName);

        String blobID = commit.getBlobID(fileName);
        File blobFile = join(BLOB_DIR, blobID);
        byte[] blobContent = readContents(blobFile);

        File file = join(CWD, fileName);
        restrictedDelete(file);
        writeObject(file, blobContent);
    }

    private static void checkoutBranch(String branchName){}

    public static void printLog(){
        Commit commit = getHeadCommit();

        while (commit != null){
            System.out.print(commit.logInfo());
            commit = readCommit(commit.getParentID());
        }
    }

    private static Commit readCommit(String commitID){
        if(commitID == null){
            return null;
        }

        File commitFile = join(COMMIT_DIR, commitID);
        return readObject(commitFile, Commit.class);
    }

    private static void writeCommit(Commit commit){
        File commitFile = join(COMMIT_DIR, commit.getID());
        writeObject(commitFile, commit);
    }

    private static void validateIfFileExistsInCommit(Commit commit, String fileName){
        if(!commit.containsFile(fileName)){
            Utils.message("File does not exist in that commit.");
            System.exit(0);
        }
    }

    private static void validateIfCommitExists(File file){
        if(!file.exists()){
            Utils.message("No commit with that id exists.");
            System.exit(0);
        }
    }

    private static void validateIfFileExists(File file){
        if(!file.exists()){
            Utils.message("File does not exist.");
            System.exit(0);
        }
    }

    private static void validateInitialization(){
        if(!GITLET_DIR.exists()){
            Utils.message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void validateCommitMessage(String message){
        if(message == null || message.isBlank() || message.isEmpty()){
            Utils.message("Please enter a commit message.");
            System.exit(0);
        }
    }

    private static void validateNonEmptyStageMap(Stage stage){
        if(stage.isEmpty()){
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }
    }
}

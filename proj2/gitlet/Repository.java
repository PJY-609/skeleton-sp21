package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *
 *  @author Juezhao YU
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    private static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    private static final File GITLET_DIR = join(CWD, ".gitlet");

    private static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    private static final File BLOB_DIR = join(GITLET_DIR, "blobs");
    private static final File BRANCH_FILE = join(GITLET_DIR, "branches");
    private static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    private static final File STAGE_FILE = join(GITLET_DIR, "stage");

    private static final File REMOTE_FILE = join(GITLET_DIR, "remotes");


    static class StringMap extends TreeMap<String, String> {

    }

    /**
     * COMMAND: init
     * Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit: a commit that contains no files and
     * has the commit message initial commit (just like that, with no punctuation).
     * It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch. The timestamp for this initial commit will be
     * 00:00:00 UTC, Thursday, 1 January 1970. Since the initial commit in all repositories created by Gitlet will
     * have exactly the same content, it follows that all repositories will automatically share this commit
     * (they will all have the same UID) and all commits in all repositories will trace back to it.
     * */
    public static void initialize() {
        if (GITLET_DIR.exists()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();

        Commit commit = new Commit();
        writeCommit(commit);
        writeBranch("master", commit);
        writeHead("master");

        StringMap remotes = new StringMap();
        writeObject(REMOTE_FILE, remotes);

        Stage stage = new Stage();
        writeObject(STAGE_FILE, stage);
    }

    /**
     * COMMAND: add [file name]
     *  Adds a copy of the file as it currently exists to the staging area
     *  If the current working version of the file is identical to the version in the current commit,
     *  do not stage it to be added, and remove it from the staging area if it is already there
     *  (as can happen when a file is changed, added, and then changed back to it’s original version).
     * */
    public static void addFile(String fileName) {
        File file = join(CWD, fileName);
        validateIfFileExists(file, "File does not exist.");
        String blobString = readContentsAsString(file);
        String blobID = sha1(blobString);

        Commit headCommit = getHeadCommit();

        Stage stage = readObject(STAGE_FILE, Stage.class);

        if (headCommit.isTrackedContent(fileName, blobID)) {
            stage.unstageForAddition(fileName);
        } else {
            stage.stageForAddition(fileName, blobID);
        }

        stage.unstageForRemoval(fileName);

        writeObject(STAGE_FILE, stage);

        if (stage.isAddedFile(fileName)) {
            File blobFile = join(BLOB_DIR, blobID);
            writeContents(blobFile, blobString);
        }
    }


    private static Commit getHeadCommit() {
        String branchName = readObject(HEAD_FILE, String.class);
        return getBranchCommit(branchName);
    }

    private static Commit getBranchCommit(String branchName) {
        StringMap branches = readObject(BRANCH_FILE, StringMap.class);
        String commitID = branches.get(branchName);
        return readCommit(commitID);
    }

    /**
     * COMMAND: commit [message]
     * Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time,
     * creating a new commit. The commit is said to be tracking the saved files.
     * By default, each commit’s snapshot of files will be exactly the same as its parent commit’s snapshot of files;
     * it will keep versions of files exactly as they are, and not update them.
     * A commit will only update the contents of files it is tracking that have been staged for addition at the time of commit,
     * in which case the commit will now include the version of the file that was staged instead of the version it got from its parent.
     * A commit will save and start tracking any files that were staged for addition but weren’t tracked by its parent.
     * By default a commit has the same file contents as its parent.
     * Files staged for addition and removal are the updates to the commit.
     * Of course, the date (and likely the mesage) will also different from the parent.
     * */
    public static void makeCommit(String message) {
        Commit headCommit = getHeadCommit();
        makeCommit(message, headCommit, null);
    }

    private static void makeCommit(String message, Commit currentCommit, Commit givenCommit) {
        validateCommitMessage(message);

        Stage stage = readObject(STAGE_FILE, Stage.class);
        validateNonEmptyStageMap(stage);

        Commit commit = new Commit(message, currentCommit, givenCommit, stage);
        writeCommit(commit);

        String branchName = readObject(HEAD_FILE, String.class);
        writeBranch(branchName, commit);

        stage.clear();
        writeObject(STAGE_FILE, stage);
    }

    private static void writeBranch(String branchName, Commit commit) {
        StringMap branches = new StringMap();
        if (BRANCH_FILE.exists()) {
            branches = readObject(BRANCH_FILE, StringMap.class);
        }

        branches.put(branchName, commit.getID());
        writeObject(BRANCH_FILE, branches);
    }

    private static void writeHead(String branchName) {
        writeObject(HEAD_FILE, branchName);
    }

    /**
     * A general interface for three cases of CHECKOUT in Gitlet
     * Case 1. checkout -- [file name]
     * Case 2. checkout [commit id] -- [file name]
     * Case 3. checkout [branch name]
     * */
    public static void checkout(String[] args) {
        if (args.length == 3) {
            checkoutFile(args[2]);
        } else if (args.length == 4) {
            checkoutFile(args[1], args[3]);
        } else {
            checkoutBranch(args[1]);
        }
    }

    /**
     * COMMAND: checkout -- [file name]
     * Takes the version of the file as it exists in the head commit and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     * */
    private static void checkoutFile(String fileName) {
        Commit headCommit = getHeadCommit();
        checkoutFile(headCommit, fileName);
    }

    /**
     * checkout [commit id] -- [file name]
     * Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory, overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     * */
    private static void checkoutFile(String commitID, String fileName) {
        Commit commit = readCommit(commitID);
        checkoutFile(commit, fileName);
    }

    /**
     * A general interface for checkouting a given file
     * */
    private static void checkoutFile(Commit commit, String fileName) {
        validateIfFileExistsInCommit(commit, fileName);

        String blobContent = getBlobContent(commit, fileName);

        File file = join(CWD, fileName);
        restrictedDelete(file);
        writeContents(file, blobContent);
    }

    /**
     * COMMAND: checkout [branch name]
     * Takes all files in the commit at the head of the given branch, and puts them in the working directory,
     * overwriting the versions of the files that are already there if they exist.
     * Also, at the end of this command, the given branch will now be considered the current branch (HEAD).
     * Any files that are tracked in the current branch but are not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the checked-out branch is the current branch
     * */
    private static void checkoutBranch(String branchName) {
        validateExistingBranch(branchName, "No such branch exists.");
        validateNotCurrentBranch(branchName, "No need to checkout the current branch.");

        Commit headCommit = getHeadCommit();
        Commit branchHeadCommit = getBranchCommit(branchName);
        validateNoUntrackedFilesInTheWay(headCommit, branchHeadCommit);

        for (String fileName: headCommit.trackedFileSet()) {
            File file = join(CWD, fileName);
            restrictedDelete(file);
        }

        writeHead(branchName);

        for (String fileName: branchHeadCommit.trackedFileSet()) {
            String blobContent = getBlobContent(branchHeadCommit, fileName);
            File file = join(CWD, fileName);
            writeContents(file, blobContent);
        }

        Stage stage = readObject(STAGE_FILE, Stage.class);
        stage.clear();
        writeObject(STAGE_FILE, stage);
    }

    private static String getBlobContent(Commit commit, String fileName) {
        String blobID = commit.getBlobID(fileName);
        File blobFile = join(BLOB_DIR, blobID);
        return readContentsAsString(blobFile);
    }

    /**
     * COMMAND: reset [commit id]
     * Checks out all the files tracked by the given commit. Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node. The staging area is cleared.
     * The command is essentially checkout of an arbitrary commit that also changes the current branch head.
     * */
    public static void reset(String commitID) {
        File commitFile = validateCommitIDExists(commitID);
        Commit resetHeadCommit = readObject(commitFile, Commit.class);
        Commit headCommit = getHeadCommit();
        validateNoUntrackedFilesInTheWay(headCommit, resetHeadCommit);

        for (String fileName: headCommit.trackedFileSet()) {
            File file = join(CWD, fileName);
            restrictedDelete(file);
        }

        String branchName = readObject(HEAD_FILE, String.class);
        writeBranch(branchName, resetHeadCommit);

        for (String fileName: resetHeadCommit.trackedFileSet()) {
            String blobContent = getBlobContent(resetHeadCommit, fileName);
            File file = join(CWD, fileName);
            writeContents(file, blobContent);
        }

        Stage stage = readObject(STAGE_FILE, Stage.class);
        stage.clear();
        writeObject(STAGE_FILE, stage);
    }


    private static void validateNoUntrackedFilesInTheWay(Commit currentCommit, Commit givenCommit) {
        List<String> fileNames = plainFilenamesIn(CWD);

        if (fileNames == null) {
            return;
        }

        for (String fileName: fileNames) {
            if (!currentCommit.isTrackedFile(fileName) && givenCommit.isTrackedFile(fileName)) {
                Utils.message("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private static void validateNoUntrackedFilesInTheWay2(Commit currentCommit, Stage stage) {
        List<String> fileNames = plainFilenamesIn(CWD);

        if (fileNames == null) {
            return;
        }

        for (String fileName: fileNames) {
            if (!currentCommit.isTrackedFile(fileName)
                    && (stage.isRemovedFiles(fileName)
                    || stage.isAddedFile(fileName))) {
                stage.clear();
                writeObject(STAGE_FILE, stage);
                Utils.message("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private static void validateNotCurrentBranch(String branchName, String errorMessage) {
        String headBranchName = readObject(HEAD_FILE, String.class);
        if (headBranchName.equals(branchName)) {
            Utils.message(errorMessage);
            System.exit(0);
        }
    }

    private static void validateExistingBranch(String branchName, String errorMessage) {
        StringMap branches = readObject(BRANCH_FILE, StringMap.class);

        if (!branches.containsKey(branchName)) {
            Utils.message(errorMessage);
            System.exit(0);
        }
    }

    /**
     * COMMAND: log
     * Starting at the current head commit, display information about each commit backwards along the commit tree
     * until the initial commit, following the first parent commit links,
     * ignoring any second parents found in merge commits.
     * */
    public static void printLog() {
        Commit commit = getHeadCommit();

        while (commit != null) {
            System.out.print(commit.logInfo());

            if (commit.isInitCommit()) {
                commit = null;
            } else {
                commit = readCommit(commit.getFirstParentID());
            }
        }
    }

    /**
     * COMMAND: global-log
     *  displays information about all commits ever made.
     * */
    public static void printGlobalLog() {
        List<String> subDirectories = plainDirectoriesIn(COMMIT_DIR);
        if (subDirectories == null) {
            return;
        }

        for (String subDirectory: subDirectories) {
            List<String> fileNames = plainFilenamesIn(join(COMMIT_DIR, subDirectory));
            if (fileNames == null) {
                continue;
            }
            for (String fileName: fileNames) {
                Commit commit = readCommit(fileName);
                System.out.print(commit.logInfo());
            }
        }
    }

    /**
     * COMMAND: find [commit message]
    * Prints out the ids of all commits that have the given commit message, one per line.
    * If there are multiple such commits, it prints the ids out on separate lines.
     * */
    public static void findCommitMessage(String message) {
        List<String> subDirectories = plainDirectoriesIn(COMMIT_DIR);
        if (subDirectories == null) {
            return;
        }

        boolean found = false;

        for (String subDirectory: subDirectories) {
            List<String> fileNames = plainFilenamesIn(join(COMMIT_DIR, subDirectory));
            if (fileNames == null) {
                continue;
            }
            for (String fileName: fileNames) {
                Commit commit = readCommit(fileName);
                if (commit.hasMessage(message)) {
                    found = true;
                    System.out.println(commit.getID());
                }
            }
        }

        if (!found) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    public static void printStatus() {
        StringMap branches = readObject(BRANCH_FILE, StringMap.class);
        String headBranchName = readObject(HEAD_FILE, String.class);
        System.out.println("=== Branches ===");
        for (String branchName: branches.keySet()) {
            if (branchName.equals(headBranchName)) {
                System.out.printf("*%s\n", branchName);
            } else {
                System.out.printf("%s\n", branchName);
            }
        }
        System.out.println();

        Stage stage = readObject(STAGE_FILE, Stage.class);
        Commit commit = getHeadCommit();

        Map<String, String> stagedFiles = stage.addedFiles();
        Set<String> removedFiles = stage.removedFiles();
        Map<String, String> trackedFiles = commit.trackedFiles();
        Map<String, String> workingFiles = getWorkingFiles();

        Map<String, String> unstagedFiles = getUnstagedFiles(stagedFiles, removedFiles, trackedFiles, workingFiles);
        Set<String> untrackedFiles = getUntrackedFiles(stagedFiles, removedFiles, trackedFiles, workingFiles);

        System.out.println("=== Staged Files ===");
        for (String fileName: stagedFiles.keySet()) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String fileName: removedFiles) {
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String fileName: unstagedFiles.keySet()) {
            System.out.printf("%s (%s)\n", fileName, unstagedFiles.get(fileName));
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String fileName: untrackedFiles) {
            System.out.println(fileName);
        }
        System.out.println();

    }

    private static Set<String> getUntrackedFiles(Map<String, String> stagedFiles,
                                                 Set<String> removedFiles,
                                                 Map<String, String> trackedFiles,
                                                 Map<String, String> workingFiles) {
        Set<String> untrackedFiles = new TreeSet<>();

        //  for files present in the working directory but neither staged for addition nor tracked;
        //  for files that have been staged for removal, but then re-created without Gitlet’s knowledge.
        for (String fileName: workingFiles.keySet()) {
            boolean notStagedForAddition = !stagedFiles.containsKey(fileName);
            boolean notTracked = !trackedFiles.containsKey(fileName);
            boolean stagedForRemoval = removedFiles.contains(fileName);
            if ((notStagedForAddition && notTracked) || stagedForRemoval) {
                untrackedFiles.add(fileName);
            }
        }

        return untrackedFiles;
    }

    private static Map<String, String> getUnstagedFiles(Map<String, String> stagedFiles,
                                                Set<String> removedFiles,
                                                Map<String, String> trackedFiles,
                                                Map<String, String> workingFiles) {
        Map<String, String> unstagedFiles = new TreeMap<>();

        // Tracked in the current commit, changed in the working directory, but not staged;
        // Not staged for removal, but tracked in the current commit and deleted from the working directory.
        for (String fileName: trackedFiles.keySet()) {
            boolean isDeleted = !workingFiles.containsKey(fileName);
            boolean notStagedForRemoval = !removedFiles.contains(fileName);
            boolean isModified = workingFiles.containsKey(fileName)
                    && !trackedFiles.get(fileName).equals(workingFiles.get(fileName));
            boolean notStagedForAddition = !stagedFiles.containsKey(fileName);
            if (isDeleted && notStagedForRemoval) {
                unstagedFiles.put(fileName, "deleted");
            }  else if (isModified && notStagedForAddition) {
                unstagedFiles.put(fileName, "modified");
            }
        }

        // Staged for addition, but with different contents than in the working directory; or
        // Staged for addition, but deleted in the working directory;
        for (String fileName: stagedFiles.keySet()) {
            if (!workingFiles.containsKey(fileName)) {
                unstagedFiles.put(fileName, "deleted");
            } else if (!stagedFiles.get(fileName).equals(workingFiles.get(fileName))) {
                unstagedFiles.put(fileName, "modified");
            }
        }

        return unstagedFiles;
    }

    private static Map<String, String> getWorkingFiles() {
        Map<String, String> workingFiles = new TreeMap<>();

        List<String> fileNames = plainFilenamesIn(CWD);
        if (fileNames == null) {
            fileNames = new ArrayList<>();
        }

        for (String fileName: fileNames) {
            File file = join(CWD, fileName);
            String blobString = readContentsAsString(file);
            String blobID = sha1(blobString);
            workingFiles.put(fileName, blobID);
        }

        return workingFiles;
    }

    /**
     * COMMAND: branch [branch name]
     * Creates a new branch with the given name, and points it at the current head commit.
     * */
    public static void createBranch(String branchName) {
        validateNewBranch(branchName);

        Commit headCommit = getHeadCommit();
        writeBranch(branchName, headCommit);
    }

    private static void validateNewBranch(String branchName) {
        StringMap branches = readObject(BRANCH_FILE, StringMap.class);
        if (branches.containsKey(branchName)) {
            Utils.message("A branch with that name already exists.");
            System.exit(0);
        }
    }

    /**
     * COMMAND: rm-branch [branch name]
     * Deletes the branch with the given name. This only means to delete the pointer associated with the branch;
     * it does not mean to delete all commits that were created under the branch, or anything like that.
     * */
    public static void removeBranch(String branchName) {
        validateExistingBranch(branchName, "A branch with that name does not exist.");
        validateNotCurrentBranch(branchName, "Cannot remove the current branch.");

        StringMap branches = readObject(BRANCH_FILE, StringMap.class);
        branches.remove(branchName);
        writeObject(BRANCH_FILE, branches);
    }

    /**
     * COMMAND: rm [file name]
     * Unstage the file if it is currently staged for addition. If the file is tracked in the current commit,
     * stage it for removal and remove the file from the working directory if the user has not already done so
     * */
    public static void removeFile(String fileName) {
        Stage stage = readObject(STAGE_FILE, Stage.class);
        Commit commit = getHeadCommit();
        validateFileRemoval(fileName, stage, commit);

        // Unstage the file if it is currently staged for addition.
        if (stage.isAddedFile(fileName)) {
            stage.unstageForAddition(fileName);
            writeObject(STAGE_FILE, stage);
            return;
        }

        // File is tracked under the current commit.
        stage.stageForRemoval(fileName);
        writeObject(STAGE_FILE, stage);
        restrictedDelete(fileName);
    }

    /**
     * COMMAND: merge [branch name]
     * Merges files from the given branch into the current branch.
     * */
    public static void mergeBranches(String branchName) {
        validateNoUncommitedChanges();
        validateExistingBranch(branchName, "A branch with that name does not exist.");
        validateNotCurrentBranch(branchName, "Cannot merge a branch with itself.");

        String currentBranchName = readObject(HEAD_FILE, String.class);
        String splitCommitID = getSplitCommit(currentBranchName, branchName);
        Commit splitCommit = new Commit();
        if (splitCommitID != null) {
            splitCommit = readCommit(splitCommitID);
        }

        // If the split point is the same commit as the given branch, then we do nothing;
        // the merge is complete.
        validateGivenBranchNotAncestor(branchName, splitCommit);

        // If the split point is the current branch, then the effect is to check out the given branch.
        Commit currentCommit = getBranchCommit(currentBranchName);
        if (Objects.equals(currentCommit, splitCommit)) {
            checkoutBranch(branchName);
            Utils.message("Current branch fast-forwarded.");
            return;
        }

        Commit givenCommit = getBranchCommit(branchName);

        boolean hasAnyMergeConflict = stageFilesAtMerge(currentCommit, givenCommit, splitCommit);

        Stage stage = readObject(STAGE_FILE, Stage.class);

        validateNoUntrackedFilesInTheWay2(currentCommit, stage);

        for (String fileName: stage.addedFileSet()) {
            File file = join(CWD, fileName);
            String blobID = stage.getBlobID(fileName);
            String blobContent = readContentsAsString(join(BLOB_DIR, blobID));
            writeContents(file, blobContent);
        }

        for (String fileName: stage.removedFiles()){
            File file = join(CWD, fileName);
            restrictedDelete(file);
        }

        String commitMessage = String.format("Merged %s into %s.", branchName, currentBranchName);
        makeCommit(commitMessage, currentCommit, givenCommit);

        if (hasAnyMergeConflict) {
            System.out.print("Encountered a merge conflict.");
        }
    }

    private static boolean stageFilesAtMerge(Commit currentCommit, Commit givenCommit, Commit splitCommit) {
        boolean hasAnyMergeConflict = false;

        for (String fileName: currentCommit.trackedFileSet()) {
            boolean isTrackedAtSplit = splitCommit.isTrackedFile(fileName);
            boolean isTrackedAtGiven = givenCommit.isTrackedFile(fileName);
            boolean notModifiedAtCurrent = Objects.equals(splitCommit.getBlobID(fileName), currentCommit.getBlobID(fileName));
            boolean notModifiedAtGiven = Objects.equals(currentCommit.getBlobID(fileName), givenCommit.getBlobID(fileName));

            if (isTrackedAtSplit && !isTrackedAtGiven && notModifiedAtCurrent) {
                // Any files present at the split point, unmodified in the current branch, and
                // absent in the given branch should be removed (and untracked).
                Stage stage = readObject(STAGE_FILE, Stage.class);
                stage.stageForRemoval(fileName);
                writeObject(STAGE_FILE, stage);
            } else if (!isTrackedAtSplit && !isTrackedAtGiven) {
                // Any files that were not present at the split point
                // and are present only in the current branch should remain as they are.
                continue;
            } else if (!notModifiedAtCurrent && isTrackedAtGiven && notModifiedAtGiven) {
                // Any files that have been modified in the current branch
                // but not in the given branch since the split point should stay as they are.
                continue;
            } else if (!notModifiedAtCurrent && !isTrackedAtGiven) {
                // Any files modified in different ways in the current and given branches are in conflict.
                // the contents of current file are changed and the given file is deleted,
                String mergedContent = createMergedContentInConflict(currentCommit, givenCommit, fileName);

                String blobID = sha1(mergedContent);
                File blobFile = join(BLOB_DIR, blobID);
                writeContents(blobFile, mergedContent);

                Stage stage = readObject(STAGE_FILE, Stage.class);
                stage.stageForAddition(fileName, blobID);
                writeObject(STAGE_FILE, stage);

                hasAnyMergeConflict = true;
            }
        }

        for (String fileName: givenCommit.trackedFileSet()) {
            boolean isTrackedAtSplit = splitCommit.isTrackedFile(fileName);
            boolean isTrackedAtCurrent = currentCommit.isTrackedFile(fileName);
            boolean notModifiedAtCurrent = Objects.equals(splitCommit.getBlobID(fileName), currentCommit.getBlobID(fileName));
            boolean notModifiedAtGiven = Objects.equals(splitCommit.getBlobID(fileName), givenCommit.getBlobID(fileName));
            boolean noConflicts = Objects.equals(currentCommit.getBlobID(fileName), givenCommit.getBlobID(fileName));

            if ((isTrackedAtSplit && isTrackedAtCurrent && notModifiedAtCurrent && !notModifiedAtGiven)) {
                // Any files that have been modified in the given branch since the split point,
                // but not modified in the current branch since the split point
                // should be changed to their versions in the given branch
                Stage stage = readObject(STAGE_FILE, Stage.class);
                stage.stageForAddition(fileName, givenCommit.getBlobID(fileName));
                writeObject(STAGE_FILE, stage);
            } else if (!isTrackedAtSplit && !isTrackedAtCurrent) {
                // Any files that were not present at the split point and are present only in the given branch
                // should be checked out and staged.
                Stage stage = readObject(STAGE_FILE, Stage.class);
                stage.stageForAddition(fileName, givenCommit.getBlobID(fileName));
                writeObject(STAGE_FILE, stage);
            } else if (isTrackedAtSplit && !isTrackedAtCurrent && notModifiedAtGiven) {
                // Any files present at the split point, unmodified in the given branch,
                // and absent in the current branch should remain absent.
                continue;
            } else if (isTrackedAtCurrent && notModifiedAtGiven) {
                // Any files that have been modified in the current branch
                // but not in the given branch since the split point should stay as they are.
                continue;
            } else if (!noConflicts) {
                // Any files modified in different ways in the current and given branches are in conflict.
                // “Modified in different ways” can mean that the contents of both are changed and different from other,
                // or the contents of one are changed and the other file is deleted,
                // or the file was absent at the split point and has different contents in the given and current branches.
                String mergedContent = createMergedContentInConflict(currentCommit, givenCommit, fileName);

                String blobID = sha1(mergedContent);
                File blobFile = join(BLOB_DIR, blobID);
                writeContents(blobFile, mergedContent);

                Stage stage = readObject(STAGE_FILE, Stage.class);
                stage.stageForAddition(fileName, blobID);
                writeObject(STAGE_FILE, stage);

                hasAnyMergeConflict = true;
            }
        }

        return hasAnyMergeConflict;
    }

    private static String createMergedContentInConflict(Commit currentCommit, Commit givenCommit, String fileName){
        if (Objects.equals(currentCommit.getBlobID(fileName), givenCommit.getBlobID(fileName))) {
            return null;
        }

        String currentContent = "";
        if (currentCommit.getBlobID(fileName) != null) {
            currentContent = readContentsAsString(join(BLOB_DIR, currentCommit.getBlobID(fileName)));
//            currentContent += System.getProperty("line.separator");
        }

        String givenContent = "";
        if (givenCommit.getBlobID(fileName) != null) {
            givenContent = readContentsAsString(join(BLOB_DIR, givenCommit.getBlobID(fileName)));
//            givenContent += System.getProperty("line.separator");
        }

        return "<<<<<<< HEAD"
                + System.getProperty("line.separator")
                + currentContent
                + "======="
                + System.getProperty("line.separator")
                + givenContent
                + ">>>>>>>";

    }

    private static String getSplitCommit(String branchName1, String branchName2) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        StringMap branches = readObject(BRANCH_FILE, StringMap.class);

        String commitID = branches.get(branchName1);
        queue.add(commitID);
        while (!queue.isEmpty()) {
            commitID = queue.remove();
            visited.add(commitID);
            Commit commit = readCommit(commitID);
            for (Iterator<String> it = commit.getParentIDs(); it.hasNext(); ) {
                String parentID = it.next();
                queue.add(parentID);
            }
        }

        commitID = branches.get(branchName2);
        queue.add(commitID);
        while (!queue.isEmpty()) {
            commitID = queue.remove();
            if (visited.contains(commitID)) {
                return commitID;
            }

            visited.add(commitID);
            Commit commit = readCommit(commitID);
            for (Iterator<String> it = commit.getParentIDs(); it.hasNext(); ) {
                String parentID = it.next();
                queue.add(parentID);
            }
        }

        return null;
    }


    private static void validateGivenBranchNotAncestor(String branchName, Commit splitCommit) {
        Commit givenCommit = getBranchCommit(branchName);
        if (Objects.equals(givenCommit, splitCommit)) {
            Utils.message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
    }

    private static void validateNoUncommitedChanges() {
        Stage stage = readObject(STAGE_FILE, Stage.class);
        if (!stage.noAddedFiles() || !stage.noRemovedFiles()) {
            Utils.message("You have uncommitted changes.");
            System.exit(0);
        }
    }


    private static void validateFileRemoval(String fileName, Stage stage, Commit commit) {
        if (!stage.isAddedFile(fileName) && !commit.isTrackedFile(fileName)) {
            Utils.message("No reason to remove the file.");
            System.exit(0);
        }
    }

    private static Commit readCommit(String commitID) {
        File commitFile = validateCommitIDExists(commitID);
        return readObject(commitFile, Commit.class);
    }

    private static File validateCommitIDExists(String srcCommitID) {
        if (srcCommitID == null || srcCommitID.length() < 2) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
            return null;
        }

        File commitSubDir = join(COMMIT_DIR, srcCommitID.substring(0, 2));
        List<String> commitIDs = plainFilenamesIn(commitSubDir);

        if (commitIDs == null) {
            Utils.message("No commit with that id exists.");
            System.exit(0);
            return null;
        }

        for (String commitID: commitIDs) {
            if (srcCommitID.equals(commitID.substring(0, srcCommitID.length()))) {
                return join(commitSubDir, commitID);
            }
        }

        Utils.message("No commit with that id exists.");
        System.exit(0);
        return null;
    }

    private static void writeCommit(Commit commit) {
        String commitID = commit.getID();
        File commitSubDir = join(COMMIT_DIR, commitID.substring(0, 2));

        if (!commitSubDir.exists()) {
            commitSubDir.mkdir();
        }

        File commitFile = join(commitSubDir, commit.getID());
        writeObject(commitFile, commit);
    }

    private static void validateIfFileExistsInCommit(Commit commit, String fileName) {
        if (!commit.isTrackedFile(fileName)) {
            Utils.message("File does not exist in that commit.");
            System.exit(0);
        }
    }

    private static void validateIfFileExists(File file, String message) {
        if (file == null || !file.exists()) {
            Utils.message(message);
            System.exit(0);
        }
    }

    public static void validateInitialization() {
        if (!GITLET_DIR.exists()) {
            Utils.message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void validateCommitMessage(String message) {
        if (message == null || message.isBlank() || message.isEmpty()) {
            Utils.message("Please enter a commit message.");
            System.exit(0);
        }
    }

    private static void validateNonEmptyStageMap(Stage stage) {
        if (stage.noAddedFiles() && stage.noRemovedFiles()) {
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }
    }

    /**
     * COMMAND: add-remote [remote name] [name of remote directory]/.gitlet
     * Saves the given login information under the given remote name.
     * Attempts to push or pull from the given remote name will then attempt to use this .gitlet directory.
     * */
    public static void addRemote(String remoteName, String remoteDirName) {
        StringMap remotes = readObject(REMOTE_FILE, StringMap.class);

        if (remotes.containsKey(remoteName)) {
            Utils.message("A remote with that name already exists.");
            System.exit(0);
            return;
        }

        remotes.put(remoteName, remoteDirName);
        writeObject(REMOTE_FILE, remotes);
    }

    /**
     * COMMAND: rm-remote [remote name]
     * Remove information associated with the given remote name.
     * The idea here is that if you ever wanted to change a remote that you added,
     * you would have to first remove it and then re-add it.
     * */
    public static void removeRemote(String remoteName) {
        StringMap remotes = readObject(REMOTE_FILE, StringMap.class);

        if (!remotes.containsKey(remoteName)) {
            Utils.message("A remote with that name does not exist.");
            System.exit(0);
            return;
        }

        remotes.remove(remoteName);
        writeObject(REMOTE_FILE, remotes);
    }

    /**
     * COMMAND: push [remote name] [remote branch name]
     * Attempts to append the current branch’s commits to the end of the given branch at the given remote.
     * Details:
     * This command only works if the remote branch’s head is in the history of the current local head,
     * which means that the local branch contains some commits in the future of the remote branch.
     * In this case, append the future commits to the remote branch.
     * Then, the remote should reset to the front of the appended commits (so its head will be the same as the local head).
     * This is called fast-forwarding.
     * If the Gitlet system on the remote machine exists but does not have the input branch,
     * then simply add the branch to the remote Gitlet.
     * */
    public static void pushToRemote(String remoteName, String remoteBranchName) {
        File remoteDir = getRemoteDir(remoteName);
        validateIfFileExists(remoteDir, "Remote directory not found.");

        if (createRemoteBranch(remoteDir, remoteBranchName)) {
            return;
        }

        Commit remoteBranchHeadCommit = getRemoteBranchCommit(remoteName, remoteBranchName);
        Commit headCommit = getHeadCommit();
        List<List<String>> paths = findPathsToCommit(headCommit.getID(), remoteBranchHeadCommit.getID());

        if (paths.isEmpty()) {
            Utils.message("Please pull down remote changes before pushing.");
            System.exit(0);
        }

        Set<String> commitIDs = new HashSet<>();
        for (List<String> path: paths) {
            commitIDs.addAll(path);
        }

        for (String commitID: commitIDs) {
            Commit commit = readCommit(commitID);
            writeRemoteCommit(remoteName, commit);

            for (String fileName: commit.trackedFileSet()) {
                String blobID = commit.getBlobID(fileName);
                File remoteBlobFile = join(remoteDir, "blobs", blobID);
                String blobContent = getBlobContent(commit, fileName);
                writeContents(remoteBlobFile, blobContent);
            }
        }

        writeRemoteBranch(remoteName, remoteBranchName, headCommit.getID());
        File remoteHeadFile = join(remoteDir, "HEAD");
        writeObject(remoteHeadFile, remoteBranchName);

        resetRemote(remoteName, headCommit.getID());

    }

    private static void resetRemote(String remoteName, String commitID) {
        File remoteDir = getRemoteDir(remoteName);
        String remoteHeadBranchName = readObject(join(remoteDir, "HEAD"), String.class);
        Commit remoteHeadCommit = getRemoteBranchCommit(remoteName, remoteHeadBranchName);
        File remoteWorkingDir = remoteDir.getParentFile();
        for (String fileName: remoteHeadCommit.trackedFileSet()) {
            File file = join(remoteWorkingDir, fileName);
            restrictedDelete(file);
        }

        File remoteCommitFile = join(remoteDir, "commits", commitID);
        remoteHeadCommit = readObject(remoteCommitFile, Commit.class);
        String branchName = readObject(HEAD_FILE, String.class);
        writeBranch(branchName, remoteHeadCommit);

        for (String fileName: remoteHeadCommit.trackedFileSet()) {
            String blobContent = getBlobContent(remoteHeadCommit, fileName);
            File file = join(remoteWorkingDir, fileName);
            writeContents(file, blobContent);
        }

        File remoteStageFile = join(remoteDir, "stage");
        Stage stage = readObject(remoteStageFile, Stage.class);
        stage.clear();
        writeObject(remoteStageFile, stage);
    }

    private static void writeRemoteCommit(String remoteName, Commit commit) {
        String commitID = commit.getID();
        File remoteDir = getRemoteDir(remoteName);
        File remoteCommitSubDir = join(remoteDir, "commits", commitID.substring(0, 2));

        if (!remoteCommitSubDir.exists()) {
            remoteCommitSubDir.mkdir();
        }

        File remoteCommitFile = join(remoteCommitSubDir, commit.getID());
        writeObject(remoteCommitFile, commit);
    }

    private static List<List<String>> findPathsToCommit(String srcCommitID, String dstCommitID) {
        List<List<String>> paths = new LinkedList<>();

        Set<String> visited = new HashSet<>();
        Stack<String> commitStack = new Stack<>();
        Stack<List<String>> pathStack = new Stack<>();

        List<String> path = new LinkedList<>();

        commitStack.add(srcCommitID);
        visited.add(srcCommitID);
        path.add(srcCommitID);
        pathStack.add(new LinkedList<>(path));

        while (!commitStack.isEmpty()) {
            String commitID = commitStack.pop();
            path = pathStack.pop();
            visited.remove(commitID);
            if (commitID.equals(dstCommitID)) {
                paths.add(new LinkedList<>(path));
            }
            path.remove(commitID);

            Commit commit = readCommit(commitID);

            for (Iterator<String> it = commit.getParentIDs(); it.hasNext(); ) {
                String parentID = it.next();

                if (!visited.contains(parentID)) {
                    commitStack.add(parentID);
                    visited.add(parentID);
                    path.add(parentID);
                    pathStack.add(new LinkedList<>(path));
                }
            }
        }

        return paths;
    }


    private static boolean createRemoteBranch(File remoteDir, String remoteBranchName) {
        File remoteBranchFile = join(remoteDir, "branches");
        StringMap remoteBranches = readObject(remoteBranchFile, StringMap.class);
        if (remoteBranches.containsKey(remoteBranchName)) {
            return false;
        }

        File remoteHeadFile = join(remoteDir, "HEAD");
        String remoteHeadBranch = readObject(remoteHeadFile, String.class);
        String remoteHeadCommitID = remoteBranches.get(remoteHeadBranch);
        File remoteHeadCommitFile = join(remoteDir, "commits", remoteHeadCommitID);
        Commit remoteHeadCommit = readObject(remoteHeadCommitFile, Commit.class);
        remoteBranches.put(remoteBranchName, remoteHeadCommit.getID());

        writeObject(remoteBranchFile, remoteBranches);
        return true;
    }

    private static void validateIfRemoteBranchExists(File remoteDir, String remoteBranchName) {
        File remoteBranchFile = join(remoteDir, "branches");
        StringMap remoteBranches = readObject(remoteBranchFile, StringMap.class);
        if (!remoteBranches.containsKey(remoteBranchName)) {
            Utils.message("That remote does not have that branch.");
            System.exit(0);
        }
    }


    /**
     * COMMAND: fetch [remote name] [remote branch name]
     * Brings down commits from the remote Gitlet repository into the local Gitlet repository.
     * Basically, this copies all commits and blobs from the given branch in the remote repository
     * (that are not already in the current repository) into a branch named [remote name]/[remote branch name] in the local .gitlet (just as in real Git),
     * changing [remote name]/[remote branch name] to point to the head commit (thus copying the contents of the branch from the remote repository to the current one).
     * This branch is created in the local repository if it did not previously exist.
     * */
    public static void fetchFromRemote(String remoteName, String remoteBranchName) {
        File remoteDir = getRemoteDir(remoteName);
        validateIfRemoteBranchExists(remoteDir, remoteBranchName);

        Set<String> commitIDs = traverseRemoteBranch(remoteName, remoteBranchName);

        for (String commitID: commitIDs) {
            Commit commit = readRemoteCommit(remoteName, commitID);
            if (commit == null) {
                continue;
            }
            writeCommit(commit);
            for (String fileName: commit.trackedFileSet()) {
                String blobID = commit.getBlobID(fileName);
                File remoteBlobFile = join(remoteDir, "blobs", blobID);
                String blobContent = readContentsAsString(remoteBlobFile);
                File blobFile = join(BLOB_DIR, blobID);
                writeContents(blobFile, blobContent);
            }
        }

        String branchName = String.format("%s/%s", remoteName, remoteBranchName);
        Commit commit = getRemoteBranchCommit(remoteName, remoteBranchName);
        writeBranch(branchName, commit);
    }

    /**
     * COMMAND: pull [remote name] [remote branch name]
     * Fetches branch [remote name]/[remote branch name] as for the fetch command,
     * and then merges that fetch into the current branch.
     * */
    public static void pullFromRemote(String remoteName, String remoteBranchName) {
        fetchFromRemote(remoteName, remoteBranchName);

        String branchName = String.format("%s/%s", remoteName, remoteBranchName);
        mergeBranches(branchName);
    }

    private static Set<String> traverseRemoteBranch(String remoteName, String remoteBranchName) {
        Commit commit = getRemoteBranchCommit(remoteName, remoteBranchName);

        Set<String> visited = new HashSet<>();
        Queue<String> queue =  new LinkedList<>();
        queue.add(commit.getID());

        while (!queue.isEmpty()) {
            String commitID = queue.remove();
            commit = readRemoteCommit(remoteName, commitID);

            if (commit == null) {
                continue;
            }

            visited.add(commitID);

            for (Iterator<String> it = commit.getParentIDs(); it.hasNext(); ) {
                String parentID = it.next();
                if (!visited.contains(parentID)) {
                    queue.add(parentID);
                }
            }
        }

        return visited;
    }

    private static Commit getRemoteBranchCommit(String remoteName, String remoteBranchName) {
        File remoteDir = getRemoteDir(remoteName);
        File remoteBranchFile = join(remoteDir, "branches");
        StringMap remoteBranches = readObject(remoteBranchFile, StringMap.class);
        String commitID = remoteBranches.get(remoteBranchName);
        return readRemoteCommit(remoteName, commitID);
    }

    private static void writeRemoteBranch(String remoteName, String remoteBranchName, String commitID) {
        File remoteDir = getRemoteDir(remoteName);
        File remoteBranchFile = join(remoteDir, "branches");
        StringMap remoteBranches = readObject(remoteBranchFile, StringMap.class);
        remoteBranches.put(remoteBranchName, commitID);
        writeObject(remoteBranchFile, remoteBranches);
    }

    private static Commit readRemoteCommit(String remoteName, String srcCommitID) {
        File remoteDir = getRemoteDir(remoteName);
        File commitSubDir = join(remoteDir, "commits", srcCommitID.substring(0, 2));
        List<String> commitIDs = plainFilenamesIn(commitSubDir);

        if (commitIDs == null) {
            return null;
        }

        for (String commitID: commitIDs) {
            if (srcCommitID.equals(commitID.substring(0, srcCommitID.length()))) {
                File commitFile = join(commitSubDir, commitID);
                return readObject(commitFile, Commit.class);
            }
        }
        return null;
    }

    private static File getRemoteDir(String remoteName) {
        StringMap remotes = readObject(REMOTE_FILE, StringMap.class);
        String remoteDirName = remotes.get(remoteName);

        if (remoteDirName == null) {
            Utils.message("Remote directory not found.");
            System.exit(0);
        }

        return new File(remoteDirName);
    }

}

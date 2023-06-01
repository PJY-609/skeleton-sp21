# Gitlet Design Document

**Name**: Juezhao YU

## Classes and Data Structures

### Class `Commit`

#### Fields

1. `message`: Represents the message associated with the commit.
2. `ID`: Represents the unique identifier of the commit.
3. `timestamp`: Represents the timestamp of the commit.
4. `trackedFiles`: Represents a `TreeMap` that maps file names to their corresponding blob IDs (file content identifiers).
5. `parentIDs`: Represents a `List` of parent commit IDs. It contains the commit IDs of the current commit and any given commit.


### Class `Stage`

#### Fields

1. `addedFiles`: Represents a `TreeMap` that maps file names to their corresponding blob IDs (content identifiers). It stores the files that have been added to the stage.
2. `removedFiles`: Represents a `TreeSet` that stores the file names of the files that have been marked for removal from the stage.


## Algorithms

### Locating split point commit

To reach the split point commit, which is the latest common ancestor of both current branch and given branch, I design a BFS algorithm to find the first intersect point of the trajectories visited by the current branch head and the given branch head.

```java
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
```
Here's a breakdown of the method and its functionality:
- It takes two parameters, `branchName1` and `branchName2`, representing the names of the current branch and the given branch, respectively.
- A set called `visited` is initialized to store the visited commit IDs, and a queue called `queue` is initialized to perform the BFS traversal.
- The method starts the BFS traversal from the commit pointed to by `branchName1`. It adds the commit ID to the queue and iteratively explores its parent commits by adding them to the queue as well.
- The traversal continues until the queue is empty. During each iteration, the current commit ID is removed from the queue, marked as visited, and its parent commits are added to the queue for further traversal.
- After the traversal from `branchName1` is complete, the method starts a second BFS traversal from the commit pointed to by `branchName2`.
- If, during the second traversal, a commit ID is encountered that is already present in the visited set, it means the commit is the split point commit (latest common ancestor). In this case, the method returns the commit ID.
- If no common ancestor is found, the method returns `null`.

### Path listing

For the implementation of "push" command, I deisgn  an algorithm to find out all involving paths from the current branch head to the given branch head. The algorithm is based on DFS. I have a few stacks to store the information of each step. It is notable that the given branch head must be in the history of the current branch head. And since each commit can point to multiple parent commits and be pointed to by multiple child commits, it forms a directed acyclic graph.

```java
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

            Commit commit = readCommit(commitID);

            for (Iterator<String> it = commit.getParentIDs(); it.hasNext(); ) {
                String parentID = it.next();

                if (!visited.contains(parentID)) {
                    List<String> copiedPath = new LinkedList<>(path);
                    commitStack.add(parentID);
                    visited.add(parentID);
                    copiedPath.add(parentID);
                    pathStack.add(copiedPath);
                }
            }
        }

        return paths;
    }
```

The `findPathsToCommit` method uses a Depth-First Search (DFS) algorithm to find all involving paths from the source commit to the destination commit in a directed acyclic graph representing the commit history. Here's a breakdown of the method and its functionality:
- It takes two parameters, `srcCommitID` and `dstCommitID`, representing the commit IDs of the source commit (current branch head) and the destination commit (given branch head), respectively. 
- A list called `paths` is initialized to store the paths from the source commit to the destination commit.
- A set called `visited` is used to keep track of visited commits, preventing revisiting and infinite loops.
- Two stacks, `commitStack` and `pathStack`, are used to store the commit IDs and paths at each step of the DFS traversal.
- A list called `path` is initialized to store the current path being explored.
- The method starts by adding the source commit ID, initializing the path with the source commit, and pushing the path to the path stack.
- The DFS traversal continues until the commit stack is empty.
- During each iteration, the method pops a commit ID from the commit stack and its corresponding path from the path stack.
- If the current commit ID is equal to the destination commit ID, the method adds the current path to the list of paths.
- The method iterates over the parent commit IDs of the current commit.
- If a parent commit ID has not been visited, a new path is created by copying the current path, adding the parent commit ID to the commit stack and marking it as visited,


## Persistence

Here's the breakdown of the content tree of the `.gitlet` folder after initialization:

```
.gitlet/
├── blobs/
├── branches
├── commits/
├── HEAD
├── remotes
└── stage
```

- `blobs/`: This directory is used to store the blob objects, which represent the content of files in the version-control system. The blob objects are named by the SHA1 of their contents as `String`.
- `branches`: `TreeMap<String, String>`. This file stores the branch information. It maps branch names to their respective head commit IDs. The commit objects are named by the SHA1 of their `Commit` instances.
- `commits/`: `Commit`. It has subdirectories grouped by the first two characters of commit ID. This directory is used to store the commit objects. Each commit represents a snapshot of the project's state at a particular point in time.
- `HEAD`: `String`. This file stores the reference to the current branch. It indicates which branch the repository is currently on.
- `remotes`: `TreeMap<String, String>`. This file stores remote repository information, by mapping remote names to their respective remote repo directories.
- `stage`: `Stage`. This file stores the information about the staging area, where changes are temporarily stored before being committed.





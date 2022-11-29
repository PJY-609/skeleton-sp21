# Git

## Initiliza a repo

### Start locally
- Create local repo: `git init`
- Create remote repo on Github.com
- Associate the local repo with the remote one: `git remote add [remote-repo-name] [remote-repo-URL]`

### Start remotely
- Clone: `git clone [remote-repo-URL]` 


## Update changes

### Local update
- Add files to tracking stage: `git add FILE`, `git add FOLDER/*`, or even `git add *`
- Commit changes with a message: `git commit -m MESSAGE`

### Remote update
- Update from the remote repo: `git pull [remote-repo-name] master`
- Update to the remote repo: `git push [remote-repo-name] master`


## Undo changes
### Undo local changes
- Undo `git add` or `git commit`: `git reset HEAD [file]`
### Undo public changes
- Undo `git push`: `git revert HEAD`  

## View history changes
- `git checkout <coomit ID> [file]`

## Branching
- Create new branch: `git branch [new-branch-name]`
- Switch to new branch: `git checkout [destination-branch]`
- Delete branch: `git branch -d [branch-to-delete]`
- View branch info: `git branch -v`

## Merge branches
-1 Switch back to master branch: `git checkout master`
-2 Merge: `git merge [branch-name]`

## Check history
- Check local repo: `git status`
- Check commited history: `git log`
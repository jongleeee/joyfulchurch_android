# Joyful Church Android

## Getting Started

### Prerequisites


* [iterm2](https://www.iterm2.com/) - Better terminal

* [oh-my-zsh](https://github.com/robbyrussell/oh-my-zsh) - Useful terminal plug-in

* [git prettify](https://coderwall.com/p/euwpig/a-better-git-log) - prints git log pretty


## Git Instructions

### How to start new work

1. Check if you are in clean state
```
git status
```

* If you see list of files and do not want them, run this to clean up : `git checkout .`


2. Go to master branch to bring all the changes from master
```
git checkout master
```


3. Pull latest changes from the master branch
```
git pull origin master
```


4. Create a new branch for new work
```
git checkout -b BRANCH_NAME
```


### How to submit work

If you already created branch for this work, skip to number 2.

1. Create branch
```
git checkout -b BRANCH_NAME
```

2. Check your work. This will tell you list of files you modified from the last commit.
```
git status
```

3. Add files you have modified. You should only add files that are relevant to current work. 
```
git add MODIFIED_FILE_NAMES
```

or to add all the files
```
git add .
```


4. Commit your work 
* Please make message be the summary of current work
```
git commit -m "MESSAGE GOES HERE"
``` 


5. Check if things have been commited
* You should see your commit message at the very top
```
git lg
```

5. Push your work
```
git push origin BRANCH_NAME
```

6. Go to github.com and create pull request against master branch


### How to get latest changes from master to local

1. Check if you are in clean state
```
git status
```

* If you see list of files and do not want them, run this to clean up : 
```
git checkout .
```

* If you want them to put it in a temporary space, run this ( you will need to run `git stash pop` to bring back later )
```
git stash
``` 


2. Go to master branch to bring all the changes from master
```
git checkout master
```

3. Pull latest changes from the master branch
```
git pull origin master
```

4. If you ran `git stash`, run `git stash pop` to bring back files
# Udacity

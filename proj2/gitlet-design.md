1. HEAD放的是最新版本的commit还**是branchname**


    * .gitlet/          -- top level folder for all persistent data in your lab12 folder
    *    - objects/     -- folder containing all persistent data for blobs and commits
    *          - blobs/
    *          - commits/
    *    - refs/        -- stores pointers into commit objects in that data
    *          - heads/ -- all branches,文件名是branchname(store current commit id
    *    - HEAD         -- current branch(store branch name
    *    - index        -- stores stagingArea
 
